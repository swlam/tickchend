package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;

public class FallBodyVolatileLv2VolRule extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_FALL_BODY_VOLATILE_LV2_VOL;
	private static double DEFAULT_VOL_1 = -0.05;
	private static double DEFAULT_VOL_2 = -0.1;
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		
		StockBean prev = prevList.get(prevList.size()-1);
		
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		
		boolean isDownCurr = (prev.getC()>curr.getC())?true:false;
		if(isDownCurr== false)
			return false;
		
		double topCurr = KHelper.getBodyTopValue(curr);
		double bottomCurr = KHelper.getBodyBottomValue(curr);
		
		double vHight2LowBody = -(topCurr - bottomCurr) / topCurr;
		
		if(isDownCurr==true && vHight2LowBody<= DEFAULT_VOL_1 && vHight2LowBody>= DEFAULT_VOL_2)
			return true;
		
		return false;
	}	

	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
}
