package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;


public class BullishEngulfingRule  extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_BULLISH_ENGULFING;
	
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		
		StockBean prev = prevList.get(prevList.size()-1);
		StockBean prevPrev = prevList.get(prevList.size()-2);
		
		boolean b = validate(prevList, curr);
		if(b==false)
			return b;
		
		
		boolean isPrevBlack = KHelper.isBlack(prev,prevPrev);
		boolean isCurrRed = !KHelper.isBlack(curr, prev);
			
		if(isPrevBlack==true && isCurrRed==true) {	
			double topPrev = KHelper.getBodyTopValue(prev);
			double bottomPrev =KHelper.getBodyBottomValue(prev);
			
			double topCurr =KHelper.getBodyTopValue(curr);
			double bottomCurr =KHelper.getBodyBottomValue(curr);
			
			if(KHelper.isBearishCandle(prev) && KHelper.isBullishCandle(prev) 
					&& topPrev< topCurr && bottomPrev>bottomCurr) {
				return true;
			}
		}
		return false;
	}

	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
}
