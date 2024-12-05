package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.conts.ta.RuleConst;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;

/*
the highest close price

 */

public class QuarterlyHighCloseRule extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_QUARTERLY_H_CLOSE;
//	private static int NUM_OF_DAYS = RuleConst.QUARTER_NUM_OF_DAYS;
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		
		
		boolean bRtn = false;
		try {
			StockBean wHBean = StreamTransformHelper.getPeriodHighest(prevList, RuleConst.QUARTER_NUM_OF_DAYS);
			if( curr.getC() > wHBean.getH())
				bRtn = true;
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return bRtn;
	}	
	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
}
