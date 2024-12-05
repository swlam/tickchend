package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;

public class GapUpRule extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_GAP_UP;
//	private static double GAP_VOLATILE_RATIO = 0.02;
	
	public GapUpRule() {
	}
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {		
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		
		StockBean prev = prevList.get(prevList.size()-1);
		boolean isGapUp = false;
		try {
			isGapUp = KHelper.isGapUp(prev, curr);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return isGapUp;
//		if(isGapUp && curr.getDayVolumeChgPct() >= 2.0) {			
//			return true;
//		}else {
//			return false;
//		}
		

	}	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
}
