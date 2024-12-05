package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.conts.ta.RuleConst;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;


public class WeeklyHighCloseRule extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_WEEKLY_H_CLOSE;
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		if(prevList.size() < RuleConst.WEEK_NUM_OF_DAYS)
			return false;
		
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		
		
		boolean isHighCloseToday = false;
		try {
			StockBean wHBean = StreamTransformHelper.getPeriodHighest(prevList, RuleConst.WEEK_NUM_OF_DAYS);
			if( curr.getC() > wHBean.getH())
				isHighCloseToday = true;
		}catch(Exception e) {
			e.printStackTrace();
			
		}
		
		return isHighCloseToday;
		

	}	
	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
}
