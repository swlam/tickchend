package com.sjm.test.yahdata.analy.ta.rule.ma.event;

import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;

/**
 * 
 *  
 * 
 * @author samswl
 *
 */
public class CrossUp2D19DRule  extends MACrossUpRule{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_CROSS_UP_2D_19D;
	
	public static int SHORT_DAY = 2;
	public static int LONG_DAY = 19;
	
	public CrossUp2D19DRule() {
		super(SHORT_DAY, LONG_DAY);
	}
	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	
	

}
