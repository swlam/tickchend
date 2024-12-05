package com.sjm.test.yahdata.analy.ta.rule.ma.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;

/**
 * 
 *  
 *
 */
public class MidTermLongArrangeRule  extends VolRuleBase{

	public CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_MID_TERM_LONG_ARRANGE;
	
	
	public MidTermLongArrangeRule() {
	}
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {	
		boolean b = validate(prevList, curr);
		
		StockBean prev = prevList.get(prevList.size()-1);
		
		if(b==false || curr.getPriceSma()==null || prev.getPriceSma()==null)
			return b;
		

		double ma10 = curr.getPriceSma().getMa10();
		double ma20 = curr.getPriceSma().getMa20();
		double ma50 = curr.getPriceSma().getMa50();
		
		double prevma10 = prev.getPriceSma().getMa10();
		double prevma20 = prev.getPriceSma().getMa20();
		double prevma50 = prev.getPriceSma().getMa50();
		
		boolean isCurrLongArrange = ma10>ma20 && ma20>ma50;
		boolean isPrevLongArrange = prevma10>prevma20 && prevma20>prevma50;
		if(isCurrLongArrange==true && isPrevLongArrange==false )
			return true;
		return false;
	}
	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	

}
