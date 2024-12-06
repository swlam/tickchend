package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.conts.ta.RuleConst;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;

/*
the highest price

 */

public class MonthlyHighRule extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_MONTHLY_H;
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		
		
		
		boolean isMonthHighCloseToday = false;
		try {
			StockBean mHBean = StreamTransformHelper.getPeriodHighest(prevList, RuleConst.MONTH_NUM_OF_DAYS);
			if( curr.getH() > mHBean.getH() && curr.getBodyTop() > mHBean.getBodyTop())
				isMonthHighCloseToday = true;
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return isMonthHighCloseToday;
		

	}	
	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}

}