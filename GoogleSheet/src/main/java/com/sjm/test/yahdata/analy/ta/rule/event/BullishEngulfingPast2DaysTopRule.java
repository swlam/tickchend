package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;

public class BullishEngulfingPast2DaysTopRule extends VolRuleBase {

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_PRICE_ENGULFING_PAST_2DAYS_TOP;
	
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		
		
		StockBean prev = prevList.get(prevList.size()-1);
		StockBean prevPrev = prevList.get(prevList.size()-2);
		
		boolean b = validate(prevList, curr);
		if(b==false)
			return b;
		
		
		double topPrevPrev = KHelper.getBodyTopValue(prevPrev);		
		double topPrev = KHelper.getBodyTopValue(prev);
		
		double topCurr =KHelper.getBodyTopValue(curr);
		double bottomCurr =KHelper.getBodyBottomValue(curr);
				
		if(topPrevPrev >= topPrev && topPrevPrev<= topCurr && bottomCurr<topPrev && curr.getC()>=topPrevPrev)
			return true;		
		
		return false;			
	}



	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	
	

	
	
	

}
