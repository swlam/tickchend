package com.sjm.test.yahdata.analy.ta.rule.ma.event;

import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;

/**
 * 
 *  
 * 
 * @author samswl
 *
 */
public class CrossUp10D50DRule  extends MACrossUpRule{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_CROSS_UP_10D_50D;
	
	public static int SHORT_TERM = 10;
	public static int LONG_TERM = 50;
	
	public CrossUp10D50DRule() {
		super(SHORT_TERM, LONG_TERM);
	}
	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	

	
	

}
