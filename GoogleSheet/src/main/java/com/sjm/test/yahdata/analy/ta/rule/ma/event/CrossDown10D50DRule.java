package com.sjm.test.yahdata.analy.ta.rule.ma.event;

import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CrossDown10D50DRule  extends MACrossDownRule{
	
	private static final int SHORT_DAY = 10;
	private static final int LONG_DAY = 50;
	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_CROSS_DOWN_10D_50D;
	
	
	public CrossDown10D50DRule() {
		super(SHORT_DAY, LONG_DAY);
	}
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	
}
