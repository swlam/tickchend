package com.sjm.test.yahdata.analy.ta.rule.ma.event;

import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;

/**
 * 
 *  
 *
 */
public class CrossUp10D20DRule  extends MACrossUpRule{

	public CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_CROSS_UP_10D_20D;
	
	public static int SHORT = 10;
	public static int LONG = 20;
	
	public CrossUp10D20DRule() {
		super(SHORT, LONG);
	}
	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	

}
