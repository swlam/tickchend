package com.sjm.test.yahdata.analy.ta.rule.ma.event;

import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CrossDown50D200DRule  extends MACrossDownRule{
	public CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_CROSS_DOWN_50D_200D;
	
	public static int SHORT_DAY = 50;
	public static int LONG_DAY = 200;
	
	
	public CrossDown50D200DRule() {
		super(SHORT_DAY, LONG_DAY);
	}
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	

}
