package com.sjm.test.yahdata.analy.ta.rule.ma.event;

import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;

public class CrossUp150D200DRule  extends MACrossUpRule{

	public CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_CROSS_UP_150D_200D;
	
	public static int SHORT = 150;
	public static int LONG = 200;
	
	public CrossUp150D200DRule() {
		super(SHORT, LONG);
	}
	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	
	

}
