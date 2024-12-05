package com.sjm.test.yahdata.analy.instantresult;

import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.model.InstantPerformanceResult;
import com.sjm.test.yahdata.analy.ta.PatternTrendHelper;

public abstract class BaseTrendGettingStrongDirection extends BaseTrendGettingDirection{
	
	
	public String goAnalyze(InstantPerformanceResult x) {
		boolean isHit = this.isCategoryHit(x);
		
		
		String rtn = null;
		if(isHit)
			rtn = this.toDescription();
		
			
		return rtn;
	}
	
	
	
}

