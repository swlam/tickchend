package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.ArrayList;
import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;


//Gapup + Black Candle , and then Up Break

/*
1. current -> must RED candle  
2. go backward to search the nearest GapUP-candle(X)
3. GapUP-candle found, and (X+1) is Black Candle, Here the Top is the R(key-position) 
4. current.close > R
 */

public class MA510DownAndUpBreakRule extends GapUpRule{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_MA_CROSS_DOWN_THEN_UP_BREAK;
	
	private int WINDOW_SIZE = 50;
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;		
		
		
		int fromIndex = prevList.size() - WINDOW_SIZE;
		List<StockBean> prevSubList = prevList.subList(fromIndex, prevList.size());
				
		List<StockBean> targetList = new ArrayList<StockBean>(WINDOW_SIZE);
		targetList.addAll(prevSubList);
		targetList.add(curr);		
		 
		boolean isFound = false;
		// find the starting index to loop thru the list
		int noOfDaysToCheck = 5;
		int EXTRACT_START_IDX = 20;
		int firstDownStockIdx = -1;
		
		
		StockBean firstDownStock = null;
		
		for(int i= EXTRACT_START_IDX; i < (targetList.size()- noOfDaysToCheck); i++) 
		{
			List<StockBean> subList = targetList.subList(i, targetList.size());
			StockBean sk = this.findMACrossDown(subList, noOfDaysToCheck);
			
			if(firstDownStock!=null) {
				// go to find the UP break
				// it already found the  firstDownStock
				break;
			}else {
				if(sk!=null) {				
					firstDownStock = sk;
					firstDownStockIdx = i;
					break; // break the loop
				}else
					continue; //continue to find the firstDownStock
			}
		}
		
		if(firstDownStockIdx == targetList.size()-1 || firstDownStockIdx==-1) {
			//today is the firsDownStock, skip 
			return false;
		}
		
		
		//
		List<StockBean> potentialList = targetList.subList(firstDownStockIdx + 1, targetList.size());
		
		for(int i= 0; i < potentialList.size(); i++) 
		{
			StockBean sk = potentialList.get(i);
			if(i>=potentialList.size()-2 && sk.getC() > firstDownStock.getBodyTop() 
					&& sk.isRiseToday() && sk.getDayVolumeChgPct() > 1.2) 
			{
				isFound = true;
			}
		}
		
		return isFound;
	}	
	
	public StockBean findMACrossDown(List<StockBean> skList, int noOfDaysToCheck) {
		StockBean curr = skList.get(skList.size()-1);
		StockBean prev = skList.get(skList.size()-1);
		
		
		boolean isMa5Downing = isContinueDaysDown(skList, noOfDaysToCheck, 5);
		boolean isMa10Uping = isContinueDaysUp(skList, noOfDaysToCheck, 10);
		
		boolean isMeetCondition1 = !curr.isRiseToday() 
									&& (curr.getBodyTop() < curr.getPriceSma().getMa5() && curr.getBodyTop() < curr.getPriceSma().getMa10() ) 
									&& prev.getPriceSma().getMa5() >= prev.getPriceSma().getMa10() && curr.getPriceSma().getMa5() < curr.getPriceSma().getMa10() 
									;
		
		if(isMeetCondition1 && isMa5Downing && isMa10Uping)
			return curr;
		
		return null;
	}
	
//	public double getPastDayAvgVolume(List<StockBean> prevList) {
//		List<StockBean> orgList = MovingAvgHelper.getNumOfDaysData(prevList, 20);
//		double avgVol = orgList.parallelStream().filter(x->x.getVolume()>0).mapToDouble(a -> a.getVolume()).average().getAsDouble();
//		return avgVol ;
//	}
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	
	public boolean isContinueDaysDown(List<StockBean> list, int noOfDaysToCheck, int days) {
		int startIdx = list.size() - noOfDaysToCheck;
		
		for(int i=startIdx; i< list.size(); i++) {
			double prevMa = this.getMovingAvgValue(list.get(i-1), days);
			double currMa = this.getMovingAvgValue(list.get(i), days);
			if(currMa > prevMa)
				return false;
		}
		
		return true;
	}
	
	public boolean isContinueDaysUp(List<StockBean> list, int noOfDaysToCheck, int days) {
		int startIdx = list.size() - noOfDaysToCheck;
		
		for(int i=startIdx; i< list.size(); i++) {
			double prevMa = this.getMovingAvgValue(list.get(i-1), days);
			double currMa = this.getMovingAvgValue(list.get(i), days);
			if(currMa < prevMa)
				return false;
		}
		
		return true;
	}
	
	public double getMovingAvgValue(StockBean sk, int maField) {
		double rtnMa = 0.0;
		
		switch (maField) {
		case 5:
			rtnMa = sk.getPriceSma().getMa5();
			break;
		case 10:
			rtnMa = sk.getPriceSma().getMa10();
			break;
		case 20:
			rtnMa = sk.getPriceSma().getMa20();
		case 50:
			rtnMa = sk.getPriceSma().getMa50();
			break;
		case 100:
			rtnMa = sk.getPriceSma().getMa100();
			break;
		case 200:
			rtnMa = sk.getPriceSma().getMa200();
			break;
		default:
			;
		}
		return rtnMa;
	}
	
}
