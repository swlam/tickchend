package com.sjm.test.yahdata.analy.ta.rule.combination;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;
import com.sjm.test.yahdata.analy.ta.rule.event.GapUpBearishShootingRule;
import com.sjm.test.yahdata.analy.ta.rule.event.WeeklyHighCloseRule;

public class CombineRule extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.COMBINE_GAP_UP_BEARISH_SHOOTING_WEEKLY_H_CLOSE;
	
	
	private GapUpBearishShootingRule r1;
	private WeeklyHighCloseRule r2;
	
	public CombineRule() {
		r1 = new GapUpBearishShootingRule();
		r2 = new WeeklyHighCloseRule();
	}
	
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
//		StockBean prev = prevList.get(prevList.size()-1);
		boolean b1 = r1.detect(prevList, curr);
		boolean b2 = r2.detect(prevList, curr);
		
		if(b1 ==true && b2 ==true)
			return true;
		
		return false;
	}
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}

}
