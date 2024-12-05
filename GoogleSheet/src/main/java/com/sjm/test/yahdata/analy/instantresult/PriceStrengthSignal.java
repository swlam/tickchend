package com.sjm.test.yahdata.analy.instantresult;

import com.sjm.test.yahdata.analy.model.InstantPerformanceResult;

public class PriceStrengthSignal extends BaseTrendGettingDirection{

	public static final Double DEFAULT_RATIO = 0.03;
	
	public PriceStrengthSignal() {}

	@Override
	public String goAnalyze(InstantPerformanceResult x) {
		return this.checkRsiStatus(x);
	}
	
	
	@Override
	public boolean isCategoryHit(InstantPerformanceResult x) {
		return false;
	}

	
	
	@Override
	public String toDescription() {
		return "RSI Signal";
	}
}
