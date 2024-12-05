package com.sjm.test.yahdata.analy.ta.rule.ma.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;

/**
 * 
 *  
 * 
 * @author samswl
 *
 */
public class UpBreak50DRule  extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_UP_BREAK_50D;
	
//	public static int SHORT_TERM = 10;
	public static int LONG_TERM = 50;
	
	public UpBreak50DRule() {
//		super(SHORT_TERM, LONG_TERM);
	}
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {	
		boolean b = validate(prevList, curr);
//		if("2022-04-09".equalsIgnoreCase(curr.getTxnDate()))
//			System.out.println("pause");
		
		if(b==false)
			return b;
		
		
		double ma50 = curr.getPriceSma().getMa50();
		
		if(curr.getC() > ma50)
			return true;
		return false;
	}
	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	

	
	

}
