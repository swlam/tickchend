package com.sjm.test.yahdata.analy.pv.cntdoublevol;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.type.MovingAvgType;
import com.sjm.test.yahdata.analy.ta.helper.MovingAvgHelper;

public class DoubleVolumeMarkerHelper {

	public static final int MIN_DAYDIFF = 4;
	public static final int NUM_OF_TXN_DATA = Const.CHART_SHOW_DAYS;
	
	public DoubleVolumeMarkerHelper() {
		// TODO Auto-generated constructor stub
	}
	
//	public static void main(String arg[]) {
//		List<DoubleVolMarker> dvmkrList = new ArrayList<DoubleVolMarker>();
//		
//		DoubleVolMarker last = dvmkrList.isEmpty()?null:dvmkrList.stream().reduce((one, two) -> two).get();
//		System.out.println(last);
//	}
	
	public double getVolumeMA(List<StockBean> stockList, int numOfDays) {
		List<StockBean> prevList = stockList.subList(0, stockList.size()-1);
		StockBean curr = stockList.get(stockList.size()-1);
		return MovingAvgHelper.getMAbyLength(prevList, curr, numOfDays, MovingAvgType.VOLUME);
	}
	
	public List<DoubleVolMarker> findDoubleVolDateList(List<StockBean> stockList) {
		List<DoubleVolMarker> dvmkrList = new ArrayList<DoubleVolMarker>();		
		int size = stockList.size();		
		if(size<10)
			return dvmkrList;
		
		DoubleVolMarker last = null;
		int startIdx = size>NUM_OF_TXN_DATA?size -NUM_OF_TXN_DATA:10;
		
//		List<StockBean> subList = stockList.subList(startIdx, size);
		int stockListSize = stockList.size();
		for(int i=startIdx; i<stockListSize; i++) {
			StockBean prev = stockList.get(i-1);
			StockBean now = stockList.get(i);
			
			StockBean next = null;
			if(i < stockListSize-1)
				next = stockList.get(i+1);
			else
				next =null;
			
			DoubleVolMarker dvm = this.markDoubleVol(prev, now, next, i);
			if(dvm ==null)
				continue;
						
			int subListStartIdx = i-10;
			if(subListStartIdx<0)
				subListStartIdx = 0;
			
			List<StockBean> subList = stockList.subList(subListStartIdx, i);
//			
//			double avgVol = subList.stream().mapToDouble(StockBean::getVolume).average().orElse(Double.NaN);

			
			double avgDaysVol = getVolumeMA(subList, 10);
			double ratioOfAvg = (double)dvm.getNow().getVolume() / avgDaysVol;
			boolean doubleAvg = ratioOfAvg > 1.3;
			
			if(dvmkrList.size()>0 && doubleAvg){
//				last = dvmkrList.stream().reduce((one, two) -> two).get();
				last = dvmkrList.isEmpty()?null:dvmkrList.stream().reduce((one, two) -> two).get();
				if(last ==null)
					continue;
				
				int dayDiff = dvm.getListIndex() - last.getListIndex();
				
				if(dayDiff >= MIN_DAYDIFF && doubleAvg) {
					dvmkrList.add(dvm);
				}
			}else if(doubleAvg){
				dvmkrList.add(dvm);
			}
			
		}
		
		
		if(dvmkrList.isEmpty())
			return dvmkrList;
		
		// Sort the list in descending order based on DoubleVolMarker.now.orderNumber
        
        List<DoubleVolMarker> descList = dvmkrList.parallelStream()
        .sorted((marker1, marker2) -> Integer.compare(marker2.getNow().getTxnDateInt(), marker1.getNow().getTxnDateInt()))
        .collect(Collectors.toList());
        
        return descList;
	}
	
//	public boolean isUpTrendDoubleVol(List<DoubleVolMarker> dvMkrList) {
//		
//	}
	
	public List<ShrinkageVolMarker> findShrinkageVolDateList(List<StockBean> stockList) {
		List<ShrinkageVolMarker> dvmkrList = new ArrayList<ShrinkageVolMarker>();
		ShrinkageVolMarker last = null;
		int size = stockList.size();
		int startIdx = size>NUM_OF_TXN_DATA?size -NUM_OF_TXN_DATA:0;
		
		List<StockBean> subList = stockList.subList(startIdx, size);
		
		StockBean minVolSk = stockList.stream().min(Comparator.comparingDouble(StockBean::getVolume)).get();
		
		for(int idx=1; idx<subList.size(); idx++) {
			StockBean prev = subList.get(idx-1);
			StockBean now = subList.get(idx);
//			StockBean next = subList.get(idx+1);
			
			ShrinkageVolMarker dvm = null;
			if(now.getTxnDateInt()==minVolSk.getTxnDateInt()) {
				dvm = new ShrinkageVolMarker();
				dvm.setListIndex(idx);
				dvm.setNow(now);
			}else {
//				dvm = this.markShrinkage(prev, now, next, idx);
				boolean hasMinStockInPeriod = isValidMinStockBean(subList, idx);
				if(hasMinStockInPeriod) {
					dvm = new ShrinkageVolMarker();
					dvm.setListIndex(idx);
					dvm.setNow(now);
				}
				if( dvm ==null) {				
					continue;
				}
			}
				
			if(dvmkrList.size()>0){
//				last = dvmkrList.stream().reduce((one, two) -> two).get();
				last = dvmkrList.isEmpty()?null:dvmkrList.stream().reduce((one, two) -> two).get();
				if(last ==null)
					continue;
				
				int dayDiff = dvm.getListIndex() - last.getListIndex();
				
				if(dayDiff >= MIN_DAYDIFF) {
					dvmkrList.add(dvm);
				}
			}else {
				dvmkrList.add(dvm);
			}
			
		}
		return dvmkrList;
	}
	
	
	private DoubleVolMarker markDoubleVol(StockBean prev, StockBean now, StockBean next, int currIdx) {
		boolean isValid = this.isValidDoubleOnlyPositiveVol(prev, now, next);
		if(isValid == true) {
			DoubleVolMarker dvm = new DoubleVolMarker();
			dvm.setListIndex(currIdx);
			dvm.setNow(now);
			return dvm;
		}else {
			return null;
		}
		
	}
	
	private boolean isValidMinStockBean(List<StockBean> skList, int currIdx) {
		int DEFAULT_DAYS = 6;
		
		int leftShiftIdx = 0;
		if(currIdx > DEFAULT_DAYS) {
			leftShiftIdx = currIdx - DEFAULT_DAYS;
		}else {
			leftShiftIdx = 0;
		}
		///////////////////
		int rightShiftIdx = 0;
		if(currIdx > (skList.size()-DEFAULT_DAYS-1)) {
			rightShiftIdx = skList.size() - 1;
		}else {
			rightShiftIdx = currIdx + DEFAULT_DAYS;
		}
		
		List<StockBean> subList = skList.subList(leftShiftIdx, rightShiftIdx+1);
		
		StockBean currtSk = skList.get(currIdx);
		
		StockBean prevSk = null;
		StockBean nextSk = null;
		if((skList.size()-1) <=currIdx )
			nextSk = null;
		else
			nextSk = skList.get(currIdx+1);
		
		if(currIdx ==0 )
			prevSk = null;
		else
			prevSk = skList.get(currIdx-1);
//		StockBean prevSk = skList.get(currIdx-1);
//		StockBean nextSk = skList.get(currIdx+1);

		StockBean minStock = subList.stream().min(Comparator.comparingDouble(StockBean::getVolume)).get();
		StockBean maxStock = subList.stream().max(Comparator.comparingDouble(StockBean::getVolume)).get();
		
		double minOfMaxRatio = (double)minStock.getVolume() / (double)maxStock.getVolume();
		
		if(prevSk==null || (minOfMaxRatio<0.3 && (prevSk.getTxnDateInt()==maxStock.getTxnDateInt())))
			return false;
		
		if(nextSk==null || (minOfMaxRatio<0.3 && (nextSk.getTxnDateInt()==maxStock.getTxnDateInt())))
				return false;
		
		if(minStock.getTxnDateInt() ==currtSk.getTxnDateInt() &&  minOfMaxRatio < 0.5)
			return true;
		
		return false;
	}
	
	
	
	private boolean isValidDoubleOnlyPositiveVol(StockBean prev, StockBean now, StockBean next) {
		boolean bBase = false ;
		
		if(next ==null) {
			bBase = (
					(prev.getDayVolumeChgPct()>0.7 && now.getDayVolumeChgPct() > 1.7)
					|| 
					(prev.getDayVolumeChgPct()<0.7 && now.getDayVolumeChgPct() > 1.9)
					)
					&& (now.getC()> prev.getC() || now.getC() > prev.getBodyTop() )
					;
		}else { 
		
			bBase = (
						(prev.getDayVolumeChgPct()>0.7 && now.getDayVolumeChgPct() > 1.7)
						|| 
						(prev.getDayVolumeChgPct()<0.7 && now.getDayVolumeChgPct() > 1.9)
						)
						&& (now.getC()> prev.getC() || now.getC() > prev.getBodyTop() ) 
						&& next.getL() >= now.getO()
						&& (next.getBodyBottom() >= now.getBodyBottom() || next.getL() >= now.getL());
		}
		
		
		return bBase;
	}
	
//	public boolean isDownTrend(List<Double> numbers) {
//	    if (numbers.size() < 2) {
//	        return false; // A trend requires at least two numbers
//	    }
//	    double prev = numbers.get(0);
//	    for (int i = 1; i < numbers.size(); i++) {
//	        double current = numbers.get(i);
//	        double deviation = (current - prev) / prev;	        
//	        if (deviation > 0.1) {
//	            return false; // If the deviation is too large, it's not a downward trend
//	        }
//	        prev = current;
//	    }
//	    return true;
//	}
	
//	public boolean isUpTrend(List<Double> numbers) {
//	    if (numbers.size() < 2) {
//	        return false; // A trend requires at least two numbers
//	    }
//	    double prev = numbers.get(0);
//	    for (int i = 1; i < numbers.size(); i++) {
//	        double current = numbers.get(i);
//	        double deviation = (current - prev) / prev;	        
//	        if (deviation < -0.1) {
//	            return false; // If the deviation is too small (negative), it's not a up trend
//	        }
//	        prev = current;
//	    }
//	    return true;
//	}
	
	
//	public static void main(String arg[]) {
//		DoubleVolumeMarkerHelper app = new DoubleVolumeMarkerHelper();
//		List<Double> num = new ArrayList<Double>();
//		num.add(99.0);
//		num.add(95.0);
//		num.add(93.0);
//		num.add(92.0);
//		num.add(91.0);
//		boolean isUpTrend =app.isUpTrend(num);
//		System.out.println("IS UP TREND: "+ isUpTrend);
//	}

}
