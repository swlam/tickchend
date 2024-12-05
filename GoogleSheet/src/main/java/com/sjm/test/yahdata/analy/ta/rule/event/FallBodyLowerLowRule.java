package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;

public class FallBodyLowerLowRule extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_FALL_BODY_LOWER_LOW;
	
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		
		StockBean prev = prevList.get(prevList.size()-1);
		
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		
		boolean isDownCurr = (prev.getC()>curr.getC())?true:false;
		if(isDownCurr== false)
			return false;
		
		double bottomPrev = KHelper.getBodyBottomValue(prev);
		double bottomCurr = KHelper.getBodyBottomValue(curr);
		
		
		
		if(bottomPrev > bottomCurr && prev.getL() > curr.getL())
			return true;
		
		return false;
	}	

	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
}
