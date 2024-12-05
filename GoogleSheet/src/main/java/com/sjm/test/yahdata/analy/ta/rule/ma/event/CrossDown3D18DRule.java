package com.sjm.test.yahdata.analy.ta.rule.ma.event;

import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;

public class CrossDown3D18DRule  extends MACrossDownRule{

	public CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_CROSS_DOWN_3D_18D;
	
	public static int SHORT_DAY = 3;
	public static int LONG_DAY = 18;
	
	public CrossDown3D18DRule() {
		super(SHORT_DAY, LONG_DAY);
	}
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	
	

}
