package com.sjm.test.yahdata.analy.ta.rule.event.pattern;

import java.util.ArrayList;
import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;

public class BigDropAndStandUpFormRule extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_BIG_DROP_AND_STAND_UP;

	private static int SAMPLE_SIZE = 10;
	
	public BigDropAndStandUpFormRule() {
	}
	
	public boolean detect(List<StockBean> prevList, StockBean last) {
		
		boolean b = validate(prevList, last);
		if(b==false)
			return false;
		
		return isBigDropAndStandUp(prevList, last);


	}	
	
	
//	private boolean checkBigDropAndStandUp(List<StockBean> prevList, StockBean last) {
//		
//		
//		int SIZE = 20;
//		StockBean stockA = prevList.get(prevList.size()-4);
//		StockBean stockB = prevList.get(prevList.size()-3);
//		StockBean stockC = prevList.get(prevList.size()-2);
//		StockBean stockD = prevList.get(prevList.size()-1);
//		
//		List<StockBean> subListTmp = prevList.subList(prevList.size()-SIZE, prevList.size());
//		ArrayList<StockBean> subList = new ArrayList<StockBean>(SIZE);
//		subList.addAll(subListTmp);
//		
//		
//		List<StockBean> orderByLowestList = subList.parallelStream()
//				.sorted(Comparator.comparing(StockBean::getL))
//				.collect(Collectors.toList());
//		boolean istheLowestPoint = false;
//		if(orderByLowestList.get(0).getTxnDateInt()==stockD.getTxnDateInt() && orderByLowestList.get(1).getTxnDateInt() == stockC.getTxnDateInt())
//			istheLowestPoint = true;
//		
//		boolean isStockCBigDrop = this.isBigDrop(stockC);
//		boolean isStockCVolMoreThanStockD = stockC.getVolume()> stockD.getVolume() *1.18;
//		boolean isLastVolMoreThanStockCD = last.getVolume()> stockD.getVolume() *1.6 && last.getVolume()>stockC.getVolume();
//		boolean isLastEngulfingWithStockC = CandleStickHelper.isWhite(last, stockD) && last.getC()> stockC.getH();
//		boolean isABvolumeless = stockA.getVolume()< stockC.getVolume() && stockB.getVolume() < stockC.getVolume();
//		boolean isStockDWhite = stockD.getC()>stockC.getC();
//		boolean isEngulfing = last.getC()> stockA.getBodyTop() && last.getC()> stockB.getBodyTop() && last.getC()> stockC.getBodyTop();
//		boolean b = isEngulfing && istheLowestPoint && isStockCBigDrop && isStockCVolMoreThanStockD && isLastVolMoreThanStockCD && isLastEngulfingWithStockC && isABvolumeless && isStockDWhite;
//		return b;
//	}
	
	
//	private boolean isBigDrop(StockBean stock) {
//		double downRatio = (stock.getC() - stock.getO()) / stock.getO();
//		if(downRatio < -0.05) {
//			return true;
//		}
//		return false;
//	}
	
//	private boolean isLowest(List<StockBean> subListTmp, StockBean targetStock) {
//		List<StockBean> orderByLowestList = subListTmp.parallelStream()
//				.sorted(Comparator.comparing(StockBean::getL))
//				.collect(Collectors.toList());
//		boolean istheLowestPoint = false;
//		if(orderByLowestList.get(0).getTxnDateInt()==targetStock.getTxnDateInt())
//			istheLowestPoint = true;
//		return istheLowestPoint;
//	}

	private boolean isBigDrop(StockBean prev, StockBean cur) {
		double downRatio = (cur.getC() - cur.getO()) / cur.getO();
		double downRatio2 = (cur.getC() - prev.getC()) / prev.getC();
		if(downRatio < -0.05 && downRatio2<-0.05 && cur.getC()< prev.getBodyBottom()) {
			return true;
		}
		return false;
	}
	
	/*
	  find period(3-5 days) fall(left-H to right-L), including 3 days are drop
	  the last dark and first whit should be more vol
	*/
	
	
//	public boolean isCurrentBigDropAndStandUp(List<StockBean> prevList, StockBean last) {
//		int SIZE = 10;
//		List<StockBean> subListTmp = prevList.subList(prevList.size()-SIZE, prevList.size());
//		ArrayList<StockBean> subList = new ArrayList<StockBean>(SIZE);
//		subList.addAll(subListTmp);
//		subList.add(last);
//		
//		//step 2
//		StockBean firstGapDownStock = null;
//		StockBean firstGapDownPrevStock = null;
//		StockBean firstStandUpStockBean = null;
//		boolean isGapDownFirstElem = false;
//		
//		for (int i = 1; i < subList.size(); i++) {
//			StockBean prev = subList.get(i-1);
//			StockBean cur = subList.get(i);
//			
//			if(this.isBigDrop(prev, cur) && CandleStickHelper.isAboveVolume(prev.getVolume(), cur.getVolume(), 1.5)) {
//				firstGapDownStock = cur;
//				firstGapDownPrevStock = prev;								
//				isGapDownFirstElem = true;
//			}else {
//				if(isGapDownFirstElem == false)
//					continue;
//			}
//			
//			boolean isStandOnOccur = cur.getBodyTop()>firstGapDownStock.getH()&& cur.getC()>firstGapDownStock.getC();
//			boolean isFillTheDownGapOccur = cur.getH()>firstGapDownPrevStock.getBodyTop() && cur.getC()>firstGapDownPrevStock.getBodyBottom();	
//			if(isStandOnOccur==true && isFillTheDownGapOccur ==true) {
//				firstStandUpStockBean = cur;
//				break;
//			}
//		}
//	}
	
	public boolean isBigDropAndStandUp(List<StockBean> prevList, StockBean last) {
		int SIZE = SAMPLE_SIZE;
		if(prevList.size()-SIZE<0) {
			SIZE = prevList.size();
		}
		
		List<StockBean> subListTmp = prevList.subList(prevList.size()-SIZE, prevList.size());

		
		ArrayList<StockBean> subList = new ArrayList<StockBean>(SIZE);
		subList.addAll(subListTmp);
		subList.add(last);
		
		StockBean firstBigDropStock = null;
		StockBean firstBigDropPrevStock = null;
		
		boolean isBigDropFirstElem = false;
		
		StockBean firstStandUpStockBean = null;
		
		for (int i = 2; i < subList.size(); i++) {
			StockBean prev2 = subList.get(i-2);
			StockBean prev = subList.get(i-1);
			StockBean cur = subList.get(i);

			if(this.isBigDrop(prev2, prev) && KHelper.isAboveVolume(prev.getVolume(), cur.getVolume(), 1.3)) {
				firstBigDropStock = prev;
				firstBigDropPrevStock = prev2;								
				isBigDropFirstElem = true;
			}else {
				if(isBigDropFirstElem == false)
					continue;
			}
			
			
			boolean isStandOnOccur = cur.getBodyTop()>firstBigDropStock.getH()&& cur.getC()>firstBigDropStock.getC();
			
			boolean isFillTheDownGapOccur = cur.getH()>firstBigDropPrevStock.getH() 
					&& cur.getBodyTop()>firstBigDropPrevStock.getBodyTop() 
					&& cur.getC()>firstBigDropPrevStock.getC();			
			
			if(isStandOnOccur==true && isFillTheDownGapOccur ==true) {
				firstStandUpStockBean = cur;
				break;
			}
			
		}
		
		if(firstStandUpStockBean!=null && firstStandUpStockBean.getTxnDateInt() == last.getTxnDateInt())
			return true;
		return  false;
	}
	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
}
