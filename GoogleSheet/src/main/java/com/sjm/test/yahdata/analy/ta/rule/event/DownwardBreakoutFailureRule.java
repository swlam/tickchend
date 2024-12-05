package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;

public class DownwardBreakoutFailureRule extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_DOWNWARD_BREAKOUT_FAILURE;
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		
		if(prevList.size()  < 2)
			return false;
		
		StockBean prev = prevList.get(prevList.size()-1);
		
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		
		
		
//		boolean isDownCurr = (prev.getC()>curr.getC())?true:false;
//		if(isDownCurr== false)
//			return false;
		
		
		StockBean prevPrev = prevList.get(prevList.size()-2);
		
		
		double prepreBodyBottom = KHelper.getBodyBottomValue(prevPrev);
		double curBodyBottom = KHelper.getBodyBottomValue(curr);
		
		if(prepreBodyBottom > prev.getC()  && prev.getC()<curBodyBottom && curr.getC()>curr.getO())
			return true;
		
		return false;
	}	

	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
}
