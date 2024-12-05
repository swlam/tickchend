package com.sjm.test.yahdata.analy.ta.pattern;

import java.util.List;
import java.util.Optional;

import com.maas.util.DateHelper;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.ta.KHelper;

public class WaveHelper {

	public static final double FIB_0_5 = 0.5;
//	public static final double FIB_0_382 = 0.382;
//	public static final double FIB_0_618 = 0.618;

	public static final int DEFAULT_DAYS_DIFF = 4; // half of DAYS_BTWN_2POINT_MIN

	public WaveHelper() {}

	public StockBean findForwardHighToLow(List<StockBean> stockList) {

		StockBean absH = stockList.get(0);
		StockBean curLow = absH;

		for (int i = 1; i < stockList.size(); i++) {

			StockBean cur = stockList.get(i);
			StockBean pre = stockList.get(i - 1);

			if (absH.getL() < cur.getL())
				continue;

			int daysDiff = DateHelper.dayBetween(absH.getTxnDate(), cur.getTxnDate());
			if (Math.abs(daysDiff) <= DEFAULT_DAYS_DIFF)
				continue;

//			if (cur.getTxnDate().equalsIgnoreCase("2022-05-18"))
//				logger.debug("pause");

			if (cur.getL() < pre.getL()) {
				curLow = cur;
				continue;
			}

			boolean isMeetReboundFibRatio = this.isReboundFibRatio(absH.getH(), curLow.getL(), cur.getH());

			if (isMeetReboundFibRatio == true)
				return curLow;
		}

		return null;
	}

	public StockBean findForwardLowToHigh(List<StockBean> stockList) {

		StockBean absL = stockList.get(0);
		StockBean curH = absL;

		for (int i = 1; i < stockList.size(); i++) {

			StockBean cur = stockList.get(i);
			StockBean pre = stockList.get(i - 1);

			if (absL.getH() > cur.getH())
				continue;

			if (cur.getH() > pre.getH()) {
				curH = cur;
				continue;
			}
			if(cur.getL()>pre.getL())
				continue;

			boolean isMeetReboundFibRatio = this.isAdjustmentFibRatio(curH.getH(), absL.getL(), cur.getL());

			if (isMeetReboundFibRatio == true)
				return curH;

		}

		return null;

	}
	
	public StockBean findForwardLowToHigh(List<StockBean> stockList, int startLowestIdx) throws Exception {

		if(startLowestIdx==0) {
			throw new Exception("Cannot start from ZERO, startIdx="+startLowestIdx);
		}
		StockBean absL = stockList.get(startLowestIdx);
		StockBean curH = absL;

		for (int i = startLowestIdx; i < stockList.size(); i++) {

			StockBean cur = stockList.get(i);
			StockBean pre = stockList.get(i - 1);

			if (absL.getH() > cur.getH())
				continue;

			if (cur.getH() > pre.getH()) {
				curH = cur;
				continue;
			}
			if(cur.getL()>pre.getL())
				continue;

			boolean isMeetReboundFibRatio = this.isAdjustmentFibRatio(curH.getH(), absL.getL(), cur.getL());

			if (isMeetReboundFibRatio == true)
				return curH;

		}

		return null;

	}

	public StockBean findBackwardLowToHigh(List<StockBean> stockList, int startIdx) {

		StockBean absL = stockList.get(startIdx);
		StockBean curH = absL;
//		System.out.println("findBackwardLowToHigh -> The ABS_L stock bean: " + absL);
//		for (int i = 1; i < stockList.size(); i++) {
		for (int i = startIdx - 1; i >= 0; i--) {
			StockBean cur = stockList.get(i);
			StockBean next = stockList.get(i + 1);

			if (absL.getH() > cur.getH())
				continue;

			if (cur.getH() > next.getH()) {
				curH = cur;
				continue;
			}

			boolean isMeetReboundFibRatio = this.isAdjustmentFibRatio(curH.getH(), absL.getL(), cur.getL());

			if (isMeetReboundFibRatio == true)
				return curH;

		}

		return curH;

	}

	public StockBean findBackwardHighToLow(List<StockBean> stockList, int startIdx) {

		StockBean absH = stockList.get(startIdx);
		StockBean curLow = absH;
//		System.out.println("findBackwardHighToLow -> The ABS_H stock bean: " + absH);
		for (int i = startIdx - 1; i >= 0; i--) {

			StockBean cur = stockList.get(i);
			StockBean next = stockList.get(i + 1);

			if (absH.getL() < cur.getL())
				continue;

//			int daysDiff = DateHelper.dayBetween(absH.getTxnDate(), cur.getTxnDate());
			int daysDiff = DateHelper.dayBetween(cur.getTxnDate(), absH.getTxnDate());
			if (Math.abs(daysDiff) < DEFAULT_DAYS_DIFF)
				continue;

//			if (cur.getTxnDate().equalsIgnoreCase("2022-05-18"))
//				logger.debug("pause");

//			curLow = cur;
			if ( next.getL() < cur.getL()) {
				curLow = next;
				continue;
			}

			boolean isMeetReboundFibRatio = this.isReboundFibRatio(absH.getH(), curLow.getL(), cur.getH(), FIB_0_5);

			if (isMeetReboundFibRatio == true)
				return curLow;
		}

		return curLow;
	}

	// 94.48-(94.48-78.01)*0.618

	public boolean isAdjustmentFibRatio(Double high, Double low, Double curL) {

//		double fibPrice = high - (high - low) * FIB_0_618;
//
//		if (curL < fibPrice)
//			return true;
//
//		return false;

		return isAdjustmentFibRatio(high, low, curL, FIB_0_5);
	}

	public boolean isAdjustmentFibRatio(Double high, Double low, Double curL, double fibRatio) {

		double fibPrice = high - (high - low) * fibRatio;

		if (curL < fibPrice)
			return true;

		return false;

	}

	public boolean isReboundFibRatio(Double high, Double low, Double curH) {

		return isReboundFibRatio(high, low, curH, FIB_0_5);

	}

	public boolean isReboundFibRatio(Double high, Double low, Double curH, double fibRatio) {

		double fibPrice = low + (high - low) * fibRatio;

		if (curH > fibPrice)
			return true;

		return false;

	}

	public int maxIndex(List<StockBean> list) {

		Integer i = 0, maxIndex = -1;
		Double max = null;

		for (StockBean x : list) {

			if ((x != null) && ((max == null) || (x.getH() > max))) {
				max = x.getH();
				maxIndex = i;
			}

			i++;

		}

		return maxIndex;
	}

	public int minIndex(List<StockBean> list) {

		Integer i = 0, minIndex = -1;
		Double min = null;

		for (StockBean x : list) {
			if ((x != null) && ((min == null) || (x.getL() < min))) {
				min = x.getL();
				minIndex = i;
			}
			i++;
		}

		return minIndex;
	}
	
	
	public int findIndex(List<StockBean> list, String targetDate) {

		Integer targetIndex = -1;
		Integer i = 0;
		for (StockBean x : list) {

			if (x != null && x.getTxnDate().equalsIgnoreCase(targetDate)) {
				
				targetIndex = i;
			}
			i++;

		}

		return targetIndex;
	}
	
	public String[] getBreakDown(StockBean criticalStockBean, List<StockBean> stockList) {
		Optional<StockBean> firstOccurence = stockList.stream()
                .filter(x -> criticalStockBean.getTxnDateInt() < x.getTxnDateInt() && x.getC() <= criticalStockBean.getL() && KHelper.isBearishCandle(x))
                .findFirst();
		long count = stockList.stream()
                .filter( x -> criticalStockBean.getTxnDateInt() < x.getTxnDateInt() && x.getC() <= criticalStockBean.getL())
                .count();
		String[] breakMsg = new String[2];
		if (firstOccurence.isPresent()) 
		{
			StockBean firstOccurenceStock = firstOccurence.get();
			breakMsg[0] = firstOccurenceStock.getTxnDate();			
			if(count>1) {
				breakMsg[1] = (count-1)+""; //"隨後還有"+(count-1)+"日跌穿";
			}else {
				breakMsg[1] = "0";	
			}
		}else {
			breakMsg[0] = "";
			breakMsg[1] = "0";
		}
		
		return breakMsg;
	}
	
	public String[] getBreakUp(StockBean criticalStockBean, List<StockBean> stockList) {
		Optional<StockBean> firstOccurence = stockList.stream()
                .filter(x -> criticalStockBean.getTxnDateInt() < x.getTxnDateInt() && x.getC() >= criticalStockBean.getH() && KHelper.isBullishCandle(x))
                .findFirst();
		long count = stockList.stream()
                .filter( x -> criticalStockBean.getTxnDateInt() < x.getTxnDateInt() && x.getC() >= criticalStockBean.getH())
                .count();
//		String breakMsg = "";
		String[] breakMsg = new String[2];
		if (firstOccurence.isPresent()) 
		{
			StockBean firstOccurenceStock = firstOccurence.get();
			breakMsg[0] = firstOccurenceStock.getTxnDate();			
			if(count>1) {
				breakMsg[1] = (count-1)+""; //"隨後還有"+(count-1)+"日跌穿";
			}else {
				breakMsg[1] = "0";	
			}
		}else {
			breakMsg[0] = "";
			breakMsg[1] = "0";
		}
		
		
		return breakMsg;
	}
	
	/////
	public boolean isMhl(List<StockBean> list) {
		int minIdx = minIndex(list);
		List<StockBean> subList = list.subList(minIdx, list.size());
		
		boolean bResult = false;
		
		int maxIdx = maxIndex(subList);
		if(maxIdx>25) {
			bResult = true;
		}
		return bResult;
	}
	
	//
	public boolean isMlh(List<StockBean> list) {
		int minIdx = minIndex(list);
		List<StockBean> dateRangeList = list.subList(minIdx, list.size());
		
		boolean bResult = false;
		
		int maxIdx = maxIndex(dateRangeList);
		List<StockBean> subList = dateRangeList.subList(maxIdx, dateRangeList.size());

		StockBean rightHighStockBean = subList.get(0);

		StockBean firstLowPoint = findBackwardHighToLow(dateRangeList, maxIdx);
		if(firstLowPoint== null || firstLowPoint.getTxnDateInt()==rightHighStockBean.getTxnDateInt())
			return false;
		
		
		int firstLowPointIdx = findIndex(dateRangeList, firstLowPoint.getTxnDate());
		
		StockBean left2ndMaxHighStockBean = findBackwardLowToHigh(dateRangeList, firstLowPointIdx);

		int left2ndMaxIdx = findIndex(dateRangeList, left2ndMaxHighStockBean.getTxnDate());
		
		
		
		
		if(left2ndMaxIdx>25) {
			bResult = true;
		}
		return bResult;
	}
}
