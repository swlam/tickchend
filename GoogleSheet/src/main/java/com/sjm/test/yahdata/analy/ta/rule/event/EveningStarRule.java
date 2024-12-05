package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;

public class EveningStarRule extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_EVENING_STAR;
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		
		StockBean prev = prevList.get(prevList.size()-1);
		
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		

		StockBean prevPrev = prevList.get(prevList.size()-2);
		
		boolean isPrevDoji = KHelper.isDoji(prev);		
		
		double prevBodyBottom = KHelper.getBodyBottomValue(prev);
		
		if(isPrevDoji==true 
				&& KHelper.getBodyTopValue(prevPrev)< prevBodyBottom && KHelper.getBodyTopValue(curr)<prevBodyBottom
				&& curr.getC() <curr.getO()
		)
			return true;
		
		return false;
	}	

	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
}
