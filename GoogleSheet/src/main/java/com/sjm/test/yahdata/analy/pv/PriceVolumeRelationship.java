package com.sjm.test.yahdata.analy.pv;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Set;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.PvrStockBean;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.pv.cntdoublevol.DoubleVolMarker;
import com.sjm.test.yahdata.analy.pv.cntdoublevol.DoubleVolumeMarkerHelper;
import com.sjm.test.yahdata.analy.pv.cntdoublevol.ShrinkageVolMarker;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.helper.VolumePriceStructureHelper;
import com.sjm.test.yahdata.analy.ta.rule.event.pattern.GapDownAndStandUpRule;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class PriceVolumeRelationship {

	private GapDownAndStandUpRule gapupStandRule = null;
	private DoubleVolumeMarkerHelper doubleVolHelper = null;
	
	public PriceVolumeRelationship() {
		gapupStandRule = new GapDownAndStandUpRule();
		doubleVolHelper = new DoubleVolumeMarkerHelper();
	}
	
	public PvrStockBean findPriceVolRelationship(List<StockBean> orgStockList, int daysLen) {
		if (orgStockList == null || orgStockList.size() <= daysLen) {
			return null;
		}
		
		List<StockBean> stockList = orgStockList.subList(orgStockList.size()- daysLen, orgStockList.size());
		StockBean last = orgStockList.get(orgStockList.size()-1);
				
		StockBean maxBodyPiceChgPctStock = stockList.stream().max(Comparator.comparingDouble(StockBean::getBodyDailyChgPct)).get();
		StockBean minVolStock = stockList.stream().min(Comparator.comparingDouble(StockBean::getVolume)).get();
		StockBean maxVolStock = stockList.stream().max(Comparator.comparingDouble(StockBean::getVolume)).get();
		
		double avgVol = stockList.stream().mapToDouble(StockBean::getVolume).average().orElse(Double.NaN);
		
		int maxVolIdx = StreamTransformHelper.findIndex(stockList,maxVolStock.getTxnDate());
		if(maxVolIdx>daysLen) {
			List<StockBean> subMaxVolList = orgStockList.subList(maxVolIdx-daysLen, maxVolIdx+1);
			avgVol = subMaxVolList.stream().mapToDouble(StockBean::getVolume).average().orElse(Double.NaN);
		}
		
		
		double maxVolRatioCompareAvg = (double)maxVolStock.getVolume()/avgVol;
		double minVolRatioCompareAvg = (double)minVolStock.getVolume()/avgVol;
		
		
		PvrStockBean rtn = null;
		boolean isMonoPeak = false;	
		
		
		//check is valid max-volum
		if(maxVolRatioCompareAvg>=2) {
			isMonoPeak = this.findMonoPeakStock( stockList, maxVolStock);
			
			String maxVolDesc = VolumePriceStructureHelper.getVolumeDescription(maxVolRatioCompareAvg);

			rtn = new PvrStockBean(last, maxVolStock, maxVolDesc, minVolStock, maxBodyPiceChgPctStock, maxVolRatioCompareAvg, minVolRatioCompareAvg, isMonoPeak);
			String volPriceInRange = this.findVolAndPriceInRange(stockList, maxVolStock);
			rtn.setMaxVolStockInRange(volPriceInRange);
			/////
//			List<StockBean> subList = StreamTransformHelper.extractData(stockList, maxVolStock.getTxnDate(), last.getTxnDate());
			int maxVolStockIdx = StreamTransformHelper.findIndex(stockList, maxVolStock.getTxnDate());
			
			Set<String> crossedMaxVolDesc = new LinkedHashSet<String>();
			
			if(maxVolStockIdx == stockList.size()-1) {
				//means the last stock bean should be the max vol stock
				crossedMaxVolDesc.add("今天是高量日");
			}else {
				List<StockBean> subList = stockList.subList(maxVolStockIdx+1, stockList.size());
//				StockBean maxHBean = subList.stream().max(Comparator.comparing(StockBean::getH)).orElseThrow(NoSuchElementException::new);		
//				StockBean minLBean = subList.stream().min(Comparator.comparing(StockBean::getL)).orElseThrow(NoSuchElementException::new);
				
//				if(minLBean.getL() < maxVolStock.getL())
//					crossedMaxVolDesc.add("曾低於最低");
//				if(maxHBean.getH() > maxVolStock.getH())
//					crossedMaxVolDesc.add("曾高於最高");
				//// second apprach
				
				long countAbvH = subList.stream().filter(sk -> sk.getC() >= maxVolStock.getH()).count();
				long countBlwL = subList.stream().filter(sk -> sk.getC() <= maxVolStock.getL()).count();
				
				double ratioCountAbvH = (double) countAbvH / (double)subList.size();
				double ratioCountBlwL = (double) countBlwL / (double)subList.size();
				
				crossedMaxVolDesc.add("其後"+GeneralHelper.toPct(ratioCountAbvH)+"日數>最高");
				crossedMaxVolDesc.add("其後"+GeneralHelper.toPct(ratioCountBlwL)+"日數<最低");
			}
			
			
			rtn.setCrossedMaxVolHighLow(crossedMaxVolDesc.toString());
		}else {
			
			rtn = new PvrStockBean(last, null, Const.NA, minVolStock, maxBodyPiceChgPctStock, 0, minVolRatioCompareAvg, isMonoPeak);
			rtn.setMaxVolStockInRange(Const.NA);
		}
				
		
		//////////////////////////////find how many double volumne + is UP candle
		List<DoubleVolMarker> dvMkrListNonFinish = doubleVolHelper.findDoubleVolDateList(stockList);
		
		List<DoubleVolMarker> dvMkrList = this.filterInvalidHighVol(dvMkrListNonFinish, stockList);
		
		Set<String> maTagSet = new LinkedHashSet<String>();
		StringBuffer doubleVolMsg = new StringBuffer();
		for (DoubleVolMarker elem : dvMkrList) {
			
			maTagSet.clear();
			if(elem.getNow().getPriceSma()==null) {
				continue;
			}
			
			doubleVolMsg.append(elem.getNow().getTxnDate());
			
			if(elem.getNow().getC() >= elem.getNow().getPriceSma().getMa20())
				maTagSet.add("20");
			if(elem.getNow().getC() >= elem.getNow().getPriceSma().getMa50())
				maTagSet.add("50");
			if(elem.getNow().getC() >= elem.getNow().getPriceSma().getMa100())
				maTagSet.add("100");
			if(elem.getNow().getC() >= elem.getNow().getPriceSma().getMa200() || elem.getNow().getC() >= elem.getNow().getPriceSma().getMa250()  )
				maTagSet.add("200");
			
			if(maTagSet.isEmpty()==false) {
				
				doubleVolMsg.append(" ["+maTagSet.size()+"]");
			}
			doubleVolMsg.append(", ");
		}
		rtn.setNumOfDoubleVolumeDate(dvMkrList.size()+"");
		rtn.setDoubleVolumeDateMsg(doubleVolMsg.toString());
		
		// find how much shrinkage volume 縮量
		List<ShrinkageVolMarker> dvShrinkageMkrList = doubleVolHelper.findShrinkageVolDateList(stockList);
		StringBuffer shrinkageVolMsg = new StringBuffer();
		for (ShrinkageVolMarker elem : dvShrinkageMkrList) {
			shrinkageVolMsg.append(elem.getNow().getTxnDate());
			shrinkageVolMsg.append(",");
		}
		rtn.setNumOfShrinkageVolumeDate(dvShrinkageMkrList.size()+"");
		rtn.setShrinkageVolumeDateMsg(shrinkageVolMsg.toString());

		
		return rtn;
		
	}
	
	private List<DoubleVolMarker> filterInvalidHighVol(List<DoubleVolMarker> dvMkrList, List<StockBean> stockList) {
		
		int size = stockList.size();
		StockBean last = stockList.get(size-1);
		StockBean prev1 = stockList.get(size-2);
		StockBean prev2 = stockList.get(size-3);
		
		
		List<DoubleVolMarker> resultList = new ArrayList<DoubleVolMarker>();
		for (DoubleVolMarker dvmBean : dvMkrList) {
			boolean b1Last = true;
			boolean b2Prev1 = true;
			boolean b3Prev2 = true;
			if(last.getC()<dvmBean.getNow().getBodyBottom() || (last.getBodyTop() < dvmBean.getNow().getBodyBottom() && last.getH() < dvmBean.getNow().getH()))
				b1Last= false;
			
			if(prev1.getC()<dvmBean.getNow().getBodyBottom() || (prev1.getBodyTop() < dvmBean.getNow().getBodyBottom() && prev1.getH() < dvmBean.getNow().getH()))
				b2Prev1= false;
			
			if(prev2.getC()<dvmBean.getNow().getBodyBottom() || (prev2.getBodyTop() < dvmBean.getNow().getBodyBottom() && prev2.getH() < dvmBean.getNow().getH()))
				b3Prev2 = false;
			
			if(b1Last && b2Prev1 && b3Prev2)
				resultList.add(dvmBean);
				
		}
		
		return resultList;
		
	}
	
	private String findVolAndPriceInRange(List<StockBean> stockList, StockBean maxVolStockBean) {
		StockBean minLoStock = stockList.stream().min(Comparator.comparingDouble(StockBean::getL)).get();
		StockBean maxHiStock = stockList.stream().max(Comparator.comparingDouble(StockBean::getH)).get();
		
		double range = maxHiStock.getH() - minLoStock.getL();
		double threshold = range / 3.0;
		String txt = "";
		
		if (maxVolStockBean.getC() < (minLoStock.getL() + threshold)) {
//            System.out.println("MaxVolStock is in the bottom 1/3 of the range");
			if(maxVolStockBean.getDayChgPct()>=0)
				txt = "低位大量-陽";
			else
				txt = "低位大量-陰";
        } else if (maxVolStockBean.getC() > (maxHiStock.getH() - threshold)) {
//            System.out.println("Second Max is in the top 1/3 of the range");
			if(maxVolStockBean.getDayChgPct()>=0)
				txt = "高位大量-陽";
			else
				txt = "高位大量-陰";
        } else {
//        	return Const.NA;
        }
		
		double absDayChgPct = Math.abs(maxHiStock.getDayChgPct());
		
		if(KHelper.isDoji(maxHiStock) ||
			(maxHiStock.getBodyDailyChgPct()< absDayChgPct && maxHiStock.getBodyDailyChgPct()/absDayChgPct<0.5)
			) {
			txt +=" (小波幅)";
		}
		
//		if(maxHiStock.getBodyDailyChgPct()< absDayChgPct) {)
//			txt +=" (小波幅)";
//		}
		return txt;
	}
	
	
	private boolean findMonoPeakStock(List<StockBean> orgStockList, StockBean maxVolStock) {
		boolean b = true;
		
		int maxVolIdx = StreamTransformHelper.findIndex(orgStockList, maxVolStock.getTxnDate());
		
		if(maxVolIdx==0)
			return false;
		
//		StockBean maxVolStock = orgStockList.get(maxVolIdx);
		StockBean leftStock = orgStockList.get(maxVolIdx-1);
		StockBean rightStock = null;
		double avgAVol = 0.0;
		double avgBVol = 0.0;
		boolean isMaxVolEqLastStock = false;
		
		
		if(maxVolIdx == orgStockList.size()-1) {
			rightStock = maxVolStock;
			isMaxVolEqLastStock = true;
		}else {
			rightStock = orgStockList.get(maxVolIdx+1);
		}
		
		int endIdx = orgStockList.size();
		if(endIdx>maxVolIdx+7)
			endIdx = maxVolIdx+7;
		
		if(maxVolIdx-10 < 0)
			return false;
		
		List<StockBean> listA = orgStockList.subList(maxVolIdx-10, maxVolIdx);
		List<StockBean> listB = (isMaxVolEqLastStock==true)?new ArrayList<StockBean>(0):orgStockList.subList(maxVolIdx+1, endIdx);
		avgAVol = listA.stream().mapToDouble(StockBean::getVolume).average().orElse(Double.NaN);
		avgBVol = listB.stream().mapToDouble(StockBean::getVolume).average().orElse(Double.NaN);
		
		double maxListAVol = listA.stream().mapToDouble(StockBean::getVolume).max().orElse(Double.NaN);
		double maxListBVol = listB.stream().mapToDouble(StockBean::getVolume).max().orElse(Double.NaN);
		
		double maxListAVolRatio = (double)maxVolStock.getVolume()/maxListAVol;
		double maxListBVolRatio = (isMaxVolEqLastStock==true)?0.0:maxVolStock.getVolume()/maxListBVol;
		
		double maxVol2AvgARatio = (double)maxVolStock.getVolume()/avgAVol;
		double maxVol2AvgBRatio = (isMaxVolEqLastStock==true)?0.0:maxVolStock.getVolume()/avgBVol;
		
		double maxVol2LeftRatio = (double)maxVolStock.getVolume()/leftStock.getVolume();
		double maxVol2rightRatio = (isMaxVolEqLastStock==true)?0.0: (double)maxVolStock.getVolume()/(double)rightStock.getVolume();
		
		double monoPeakRatio = 2.0;
		
		if( maxListAVolRatio>monoPeakRatio && (maxListBVolRatio!=0.0 && maxListBVolRatio>monoPeakRatio) 
			&& maxVol2AvgARatio>monoPeakRatio && (maxVol2AvgBRatio!=0.0 && maxVol2AvgBRatio>monoPeakRatio) 
			&& maxVol2LeftRatio>monoPeakRatio && (maxVol2rightRatio!=0.0 && maxVol2rightRatio>monoPeakRatio)
		)
			return true;
		return false;
	}
	
//	public boolean findGapDownAndStandUp(List<StockBean> orgStockList) {
//		
//		StockBean last = orgStockList.get(orgStockList.size()-1);
//		StockBean fist2 = orgStockList.get(orgStockList.size()-2);
//		StockBean first = orgStockList.get(orgStockList.size()-3);
//		StockBean firstPrev = orgStockList.get(orgStockList.size()-4);
//		boolean b =  gapupStandRule.findGapDownAndStandUp(last, fist2, first, firstPrev);
//		return b;
//	}
	
	
	private double getVolIncreaseRatio(StockBean prev, StockBean curr) {
		return curr.getVolume() / prev.getVolume();
	}

	public String findExtreamLessVol(List<StockBean> stockList) {
		try {
		StockBean last = stockList.get(stockList.size()-1);
		
		OptionalDouble youngestIndex = stockList.stream()
                .mapToDouble(x -> x.getVolume())
                .min();

		OptionalDouble secondYoungestIndex = stockList.stream()
                .filter(x -> x.getVolume() != youngestIndex.getAsDouble())
                .mapToDouble(x -> x.getVolume())
                .min();
		
		
		int smallestVolIndex = stockList.indexOf(
				stockList.stream()
                        .filter(x -> x.getVolume() == youngestIndex.getAsDouble())
                        .findFirst()
                        .get()
                        );
		
		int secondSmallestVolIndex = stockList.indexOf(
				stockList.stream()
                        .filter(x -> x.getVolume() == secondYoungestIndex.getAsDouble())
                        .findFirst()
                        .get()
                        );
		
		if(smallestVolIndex > 1 && secondSmallestVolIndex > 1 && Math.abs(smallestVolIndex -secondSmallestVolIndex ) > 5) 
		{
			int count =0;
			String dateResult = "";

			StockBean smallest = stockList.get(smallestVolIndex);
			
			if(smallest.getTxnDateInt() == last.getTxnDateInt()) {
				count++;
				dateResult += " " + smallest.getTxnDate();
			}else {
				StockBean nextSmallest = stockList.get(smallestVolIndex+1);
				
				
				if( (nextSmallest.getVolume() / smallest.getVolume())>=1.8 ) {
					count++;
					dateResult += " " + smallest.getTxnDate();
				}
			}
			
			StockBean secondSmallest = stockList.get(secondSmallestVolIndex);
			
			if(secondSmallest.getTxnDateInt() == last.getTxnDateInt()) {
				count++;
				dateResult +=", " + secondSmallest.getTxnDate();	
			}else {
				StockBean nextSecondSmallest = stockList.get(secondSmallestVolIndex+1);
				if( (nextSecondSmallest.getVolume() / secondSmallest.getVolume())>= 1.8 ) {
					count++;
					dateResult +=", " + secondSmallest.getTxnDate();
				}
			}
			
			//finally, still is null or empty
			if(dateResult == null || dateResult.isEmpty())
				return "";
			else
				return count+" : " + dateResult.substring(1);
		}
		}catch(Exception e) {
			log.error(null, e);

		}
		return "";
	}
	
//	public static void main(String arg[]) {
//		double num = 1.07E+04;
//		String formatted = String.format("%.2f", num);
//		System.out.println(formatted);
//	}
}
