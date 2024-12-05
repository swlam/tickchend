package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.conts.ta.RuleConst;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;


public class BullishPriceVolPushUpRule  extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_PRICE_VOL_PUSH_UP;//CandleTagEnum.EVNT_INCREASE_DOUBLE_VOL;
	
	
	public double range1Ratio1 = 0.9;
	public double range1Ratio2 = 2.0;
	
	
//	private boolean isUpClose = true;
	public BullishPriceVolPushUpRule() {
	}
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		if(prevList.size() < 5)
			return false;
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		
		
		try {
			List<StockBean> prevPrevList = prevList.subList(0, prevList.size()-1);
			
			boolean bRtnPrev = isMatchResult(prevPrevList, prevList.get(prevList.size()-1));
			
			
			boolean bRtn = isMatchResult(prevList, curr);
			
			
			return (bRtn && bRtnPrev==false);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	private boolean isMatchResult(List<StockBean> prevList, StockBean curr) {
	
		StockBean prev1 = prevList.get(prevList.size()-1);
		StockBean prev2 = prevList.get(prevList.size()-2);
		StockBean prev3 = prevList.get(prevList.size()-3);
		
		if(prevList.size()<3 || prev1.getVolume()==0)
			return false;
		
		double ratio1 = this.getVolumeIncreaseRatio(curr, prev1);			
//		boolean isWhite1 = CandleStickHelper.isWhite(curr, prev);	
	
		double ratio2 = this.getVolumeIncreaseRatio(prev1, prev2);
		
//		boolean isWhite2 = CandleStickHelper.isWhite(prev, prevPrev);
		
		
		boolean isAccept1 = isBetweenPushUpVolRatio(ratio1);
		boolean isAccept2 = isBetweenPushUpVolRatio(ratio2);
		boolean isAccept3 = (prev2.getVolume() / prev3.getVolume())>1.6;	
		
		boolean isPricePushUp =curr.getC()> prev1.getBodyTop() && prev1.getC()> prev2.getBodyTop() && prev2.getC() > prev3.getH();
		boolean isWeekHigh = isWeeklyHigh(prevList, curr);
		
		boolean bRtn = isAccept1 && isAccept2 && isAccept3 && isPricePushUp && isWeekHigh;
		return bRtn;
	}
	
	private boolean isBetweenPushUpVolRatio(double ratio) {
		if(range1Ratio1 <= ratio && ratio <=range1Ratio2 )
			return true;
		return false;
	}
	public double getVolumeIncreaseRatio(StockBean curr, StockBean prev) {
		return curr.getVolume() / prev.getVolume();
	}
	
	protected boolean isWeeklyHigh(List<StockBean> prevList, StockBean curr) {
		int startIdx = prevList.size()- RuleConst.WEEK_NUM_OF_DAYS;
		
		startIdx = (startIdx <0)?0:startIdx;
		List<StockBean>  prevSubList = prevList.subList(startIdx, prevList.size());
		
		boolean isHighCloseToday = false;
		try {
			double maxHigh = getPeriodHigh(prevSubList, RuleConst.WEEK_NUM_OF_DAYS);
			if( curr.getC() > maxHigh)
				isHighCloseToday = true;
		}catch(Exception e) {
			e.printStackTrace();
			
		}
		return isHighCloseToday;
	}
	
	protected double getPeriodHigh(List<StockBean> orgList, int txDays) throws Exception{
		if(orgList.size()< txDays)
			throw new Exception("Check and Sort by Volume, Require list size = "+txDays);
		
		List<StockBean> sortedList = orgList.parallelStream()
				.sorted(Comparator.comparingDouble(StockBean::getC).reversed())
				.collect(Collectors.toList());
			
		return sortedList.get(0).getC();
		
	}
	
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}

}
