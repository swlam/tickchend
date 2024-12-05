package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;


public class BullishEngulfingStrongRule  extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_BULLISH_ENGULFING_2;
	
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		
		StockBean prev = prevList.get(prevList.size()-1);
		
		boolean b = validate(prevList, curr);
		if(b==false)
			return b;
		
		
		boolean b1 = (KHelper.isBearishCandle(prev) && KHelper.isBullishCandle(curr) 
				&& prev.getH() < curr.getC() && prev.getL()> curr.getO()); 
		boolean b2 = curr.getVolume() / prev.getVolume() >= 1.5;
		
		return b1 && b2;
		
		
	}

	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
}
