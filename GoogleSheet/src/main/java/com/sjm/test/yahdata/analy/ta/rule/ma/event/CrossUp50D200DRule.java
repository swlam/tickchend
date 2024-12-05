package com.sjm.test.yahdata.analy.ta.rule.ma.event;

import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;

public class CrossUp50D200DRule  extends MACrossUpRule{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_CROSS_UP_50D_200D;
	
	public static int SHORT = 50;
	public static int LONG = 200;
	
	public CrossUp50D200DRule() {
		super(SHORT, LONG);
	}
	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	
	

}
