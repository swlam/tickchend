package com.sjm.test.yahdata.analy.ta.rule.event.stats;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;

public class StatsLowerShadowRule extends VolRuleBase{
//lowerShadow
	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_STATS_LOWER_SHADOW;
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		
		if(curr.getL() <curr.getO() && curr.getL() <curr.getC())
			return true;
		
		
		return false;
	}	

	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
}
