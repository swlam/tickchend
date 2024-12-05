package com.sjm.test.yahdata.analy.ta.rule.ma.event;

import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;

/**
 * 
 *  
 * 
 * @author samswl
 *
 */
public class CrossUp5D10DRule  extends MACrossUpRule{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_CROSS_UP_5D_10D;
	
	public static int SHORT_DAY = 5;
	public static int LONG_DAY = 10;
	
	public CrossUp5D10DRule() {
		super(SHORT_DAY, LONG_DAY);
	}
	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	
	

}
