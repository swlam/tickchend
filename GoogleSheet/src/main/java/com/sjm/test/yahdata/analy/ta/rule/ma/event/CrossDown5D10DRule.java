package com.sjm.test.yahdata.analy.ta.rule.ma.event;

import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;

public class CrossDown5D10DRule  extends MACrossDownRule{

	public CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_CROSS_DOWN_5D_10D;
	
	public static int SHORT_DAY = 2;
	public static int LONG_DAY = 10;
	
	public CrossDown5D10DRule() {
		super(SHORT_DAY, LONG_DAY);
	}
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	
	

}
