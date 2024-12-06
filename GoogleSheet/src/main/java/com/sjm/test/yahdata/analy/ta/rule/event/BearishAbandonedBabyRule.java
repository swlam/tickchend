package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;

public class BearishAbandonedBabyRule extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_BEARISH_ABANDONED_BABY;
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		
		StockBean prev = prevList.get(prevList.size()-1);
		
		boolean b = validate(prevList, curr);
		if(b==false || prevList.size()<3)
			return false;
		
		
		
		StockBean prevPrev = prevList.get(prevList.size()-2);
		
		boolean b1 = KHelper.isGapDown(prev, curr);
		b1 = (b1==true)?KHelper.isGapUp(prevPrev, prev):false;
		
//		double topCurr = CandleStickHelper.getBodyTopValue(curr);
//		double bottomCurr = CandleStickHelper.getBodyBottomValue(curr);
//		
//		double vHight2LowBody = -(topCurr - bottomCurr) / topCurr;
//	
//		
//		if(b1==true && vHight2LowBody<= DEFAULT_VOL_1 && vHight2LowBody>= DEFAULT_VOL_2)
//			return true;
		
		return b1;
	}	

	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
}