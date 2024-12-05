package com.sjm.test.yahdata.analy.ta.rule.event.pattern;

import java.util.ArrayList;
import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;

public class GapDownAndStandUpRule extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_GAP_DOWN_AND_STAND_UP;
//	private static double GAP_VOLATILE_RATIO = 0.02;
	private static int SAMPLE_SIZE = 10;
	public GapDownAndStandUpRule() {
	}
	
	public boolean detect(List<StockBean> prevList, StockBean last) {
		
		boolean b = validate(prevList, last);
		if(b==false)
			return false;
		
		return findGapDownAndStandUpV2(prevList, last);
//		StockBean first2 = prevList.get(prevList.size()-1);
//		StockBean first = prevList.get(prevList.size()-2);
//		StockBean firstPrev = prevList.get(prevList.size()-3);
//		return findGapDownAndStandUp(last, first2, first, firstPrev);

	}	
	
//	public boolean findGapDownAndStandUp(StockBean last, StockBean fist2, StockBean first, StockBean firstPrev) {
//		
////		StockBean last = orgStockList.get(orgStockList.size()-1);
////		StockBean fist2 = orgStockList.get(orgStockList.size()-2);
////		StockBean first = orgStockList.get(orgStockList.size()-3);
////		StockBean firstPrev = orgStockList.get(orgStockList.size()-4);
//		boolean isGapDownFirstElem = CandleStickHelper.isGapDown(firstPrev, first);
//		boolean isDoubleVolFirstElem = CandleStickHelper.isAbove2XVolume(firstPrev.getVolume(), first.getVolume());
//		boolean isBelowHalfVolFirstElem2 = CandleStickHelper.isBelowHalfVolume(first.getVolume(), fist2.getVolume());
//		boolean isStandOn = last.getBodyTop()>fist2.getBodyTop()&& last.getC()>fist2.getC();
//		boolean isFillTheDownGap = last.getH()>firstPrev.getL() || fist2.getH()>firstPrev.getL();
//		return isGapDownFirstElem && isDoubleVolFirstElem && isBelowHalfVolFirstElem2 && isStandOn && isFillTheDownGap;
//	}
	
	
	public boolean findGapDownAndStandUpV2(List<StockBean> prevList, StockBean last) {
		
		int SIZE = SAMPLE_SIZE;
//		if(prevList.size() <SIZE)
//			return false;
		
		if(prevList.size()-SIZE<0) {
			SIZE = prevList.size();
		}
		
		List<StockBean> subListTmp = prevList.subList(prevList.size()-SIZE, prevList.size());

		ArrayList<StockBean> subList = new ArrayList<StockBean>(SIZE);
		subList.addAll(subListTmp);
		subList.add(last);
		
		StockBean firstGapDownStock = null;
		StockBean firstGapDownPrevStock = null;

		boolean isGapDownFirstElem = false;
		
		StockBean firstStandUpStockBean = null;
		
		for (int i = 1; i < subList.size(); i++) {
			StockBean prev = subList.get(i-1);
			StockBean cur = subList.get(i);

			if(KHelper.isGapDown(prev, cur) && KHelper.isAbove2XVolume(prev.getVolume(), cur.getVolume())) {
				firstGapDownStock = cur;
				firstGapDownPrevStock = prev;								
				isGapDownFirstElem = true;
			}else {
				if(isGapDownFirstElem == false)
					continue;
			}
			
			//
			boolean isStandOnOccur = cur.getBodyTop()>firstGapDownStock.getH()&& cur.getC()>firstGapDownStock.getC();
			
			boolean isFillTheDownGapOccur = cur.getH()>=firstGapDownPrevStock.getH() 
					&& cur.getBodyTop()>=firstGapDownPrevStock.getBodyTop()
					&& cur.getC()>=firstGapDownPrevStock.getC();			
			
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
