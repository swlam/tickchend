package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;



public class RaiseBodyVolatileVolRule extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_RAISE_BODY_VOLATILE_VOL;
	
	private static double DEFAULT_VOL = 0.03;
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		
		StockBean prev = prevList.get(prevList.size()-1);
		
		
		boolean isUpCurr = (prev.getC()<curr.getC())?true:false;
		if(isUpCurr== false)
			return false;
		
		
		double topCurr = KHelper.getBodyTopValue(curr);
		double bottomCurr = KHelper.getBodyBottomValue(curr);
		
		
		double vLow2HightBody = (topCurr - bottomCurr) / bottomCurr;
		
	
		if(isUpCurr ==true && vLow2HightBody >= DEFAULT_VOL)
			return true;
		
		return false;
			
	}	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}

}
