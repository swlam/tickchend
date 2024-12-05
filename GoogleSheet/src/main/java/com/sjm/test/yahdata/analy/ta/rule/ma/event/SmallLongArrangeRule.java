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
public class SmallLongArrangeRule  extends VolRuleBase{

	public CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_SMALL_LONG_ARRANGE;
	
//	public static int SHORT = 10;
//	public static int LONG = 20;
	
	public SmallLongArrangeRule() {
	}
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {	
		boolean b = validate(prevList, curr);
		
		if(b==false)
			return b;
		
		StockBean prev = prevList.get(prevList.size()-1);
		
		if(prev.getPriceSma()==null)
			return false;
		
		

		double ma5 = curr.getPriceSma().getMa5();
		double ma10 = curr.getPriceSma().getMa10();
		double ma20 = curr.getPriceSma().getMa20();
		
		double prevma5 = prev.getPriceSma().getMa5();
		double prevma10 = prev.getPriceSma().getMa10();
		double prevma20 = prev.getPriceSma().getMa20();
		
		boolean isCurrLongArrange = ma5>ma10 && ma10>ma20;
		boolean isPrevLongArrange = prevma5>prevma10 && prevma10>prevma20;
		if(isCurrLongArrange==true && isPrevLongArrange==false )
			return true;
		return false;
	}
	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	

}
