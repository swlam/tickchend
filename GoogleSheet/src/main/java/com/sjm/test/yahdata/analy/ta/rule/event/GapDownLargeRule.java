package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;

/*
Size: Past 5 weeks data
1. sort H by asc, current vol <= first 5. -->低量
2. prev is DOWN, curr is RAISE

 */

public class GapDownLargeRule extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_GAP_DOWN_LARGE;
	private static double GAP_VOLATILE_RATIO_1 = -0.03;
//	private static double GAP_VOLATILE_RATIO_2 = -0.05;
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		
		StockBean prev = prevList.get(prevList.size()-1);
		
		boolean isGapDown = KHelper.isGapDown(prev, curr);
		if(isGapDown == false)
			return false;
		
		try {
			double gapTop = KHelper.getGapTop(prev, curr);
			double gapBottom = KHelper.getGapBottom(prev, curr);
			
			
			double vHight2Low = (gapBottom - gapTop) / gapTop; // negative value
			
			
//			double gapRatio = 0.0;
			
//			if( prev.getC() < curr.getC()) {
//				//329.8/327 -1
//				gapRatio = gapTop / gapBottom -1;
//			}else if( prev.getC() > curr.getC()) {
//				//1-355/358.8
//				gapRatio = 1- gapBottom/gapTop;
//			}
			// -0.03 >= -0.04 
			if( GAP_VOLATILE_RATIO_1 >= vHight2Low  )
				return true;
			
			return false;
		
		}catch(Exception e) {
//			e.printStackTrace();
			return false;
		}

	}	
	

	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
}
