package com.sjm.test.yahdata.analy.ta.rule.ma.event;

import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;

public class CrossDown2D19DRule  extends MACrossDownRule{

	public CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_CROSS_DOWN_2D_19D;
	
	public static int SHORT_DAY = 2;
	public static int LONG_DAY = 19;
	
	public CrossDown2D19DRule() {
		super(SHORT_DAY, LONG_DAY);
	}
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	
	

}
