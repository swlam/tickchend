package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;


public class GapUpLargeRule extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVENT_NA;
//	private static double GAP_VOLATILE_RATIO = 0.02;
	private double requiredGapUpRatio = 0.0;
	
	public GapUpLargeRule( double requiredGapUpRatio) {
		this.requiredGapUpRatio = requiredGapUpRatio;
		
		if(requiredGapUpRatio>=0.1)
			SIGN = CandleEventTagEnum.EVNT_GAP_UP_10_PCT_PLUS;
		else if(requiredGapUpRatio>=0.08)
			SIGN = CandleEventTagEnum.EVNT_GAP_UP_8_PCT_PLUS;
		else if(requiredGapUpRatio>=0.06)
			SIGN = CandleEventTagEnum.EVNT_GAP_UP_6_PCT_PLUS;
		else if(requiredGapUpRatio>=0.05)
			SIGN = CandleEventTagEnum.EVNT_GAP_UP_5_PCT_PLUS;
		else if(requiredGapUpRatio>=0.04)
			SIGN = CandleEventTagEnum.EVNT_GAP_UP_4_PCT_PLUS;
		else if(requiredGapUpRatio>=0.03 )
			SIGN = CandleEventTagEnum.EVNT_GAP_UP_3_PCT_PLUS;
		else if(requiredGapUpRatio>=0.02 )
			SIGN = CandleEventTagEnum.EVNT_GAP_UP_2_PCT_PLUS;
		
		
	}
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		if(curr.getTxnDate().equalsIgnoreCase("2022-11-11")) {
			System.out.println("PAUSE");
		}
		
		StockBean prev = prevList.get(prevList.size()-1);
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		
		boolean isGapUp = KHelper.isGapUp(prev, curr);
		if(isGapUp == false)
			return false;
		
		try {
			double gapTop = KHelper.getGapTop(prev, curr);
			double gapBottom = KHelper.getGapBottom(prev, curr);
			
			double gapRatio = 0.0;
			
			if( prev.getC() < curr.getC()) {
				//329.8/327 -1
				gapRatio = gapTop / gapBottom -1;
			}else if( prev.getC() > curr.getC()) {
				//1-355/358.8
				gapRatio = 1- gapBottom/gapTop;
			}
				
			if( gapRatio >= requiredGapUpRatio)
				return true;
			
			return false;
		
		}catch(Exception e) {
//			e.printStackTrace();
			return false;
		}

	}	
	

	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
}
