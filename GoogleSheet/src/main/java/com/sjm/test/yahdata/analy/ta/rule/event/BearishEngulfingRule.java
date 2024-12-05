package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;


public class BearishEngulfingRule  extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_BEARISH_ENGULFING;
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		
		StockBean prev = prevList.get(prevList.size()-1);
		
		
		boolean isPrevBlack = KHelper.isBearishCandle(prev);
		boolean isCurrBlack = KHelper.isBearishCandle(curr);
			
		if(isPrevBlack==false || isCurrBlack==false)
			return false;
				
		double topPrev = KHelper.getBodyTopValue(prev);
		double bottomPrev =KHelper.getBodyBottomValue(prev);
		
		double topCurr =KHelper.getBodyTopValue(curr);
		double bottomCurr =KHelper.getBodyBottomValue(curr);
		
		if(KHelper.isBullishCandle(prev) && KHelper.isBearishCandle(prev) 
				&& topPrev< topCurr && bottomPrev>bottomCurr) {
			return true;
		}
		
		return false;

	}
	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	
	
	

}
