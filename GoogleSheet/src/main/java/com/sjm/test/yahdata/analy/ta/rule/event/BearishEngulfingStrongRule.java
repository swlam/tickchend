package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;


public class BearishEngulfingStrongRule  extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_BEARISH_ENGULFING_2;
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		
		StockBean prev = prevList.get(prevList.size()-1);
		double bottomCurr =KHelper.getBodyBottomValue(curr);
		
		boolean b1 = KHelper.isBullishCandle(prev) && KHelper.isBearishCandle(curr) 
				&& prev.getH()< curr.getO() && prev.getL()>bottomCurr;
		boolean b2 = curr.getVolume() / prev.getVolume() >= 1.05;
		
		return b1&&b2;
		
//		boolean isPrevBlack = CandleStickHelper.isBearishCandle(prev);
//		boolean isCurrBlack = CandleStickHelper.isBearishCandle(curr);
//			
//		if(isPrevBlack==false || isCurrBlack==false)
//			return false;
//				
//		double topPrev = CandleStickHelper.getBodyTopValue(prev);
//		double bottomPrev =CandleStickHelper.getBodyBottomValue(prev);
//		
//		double topCurr =CandleStickHelper.getBodyTopValue(curr);
//		double bottomCurr =CandleStickHelper.getBodyBottomValue(curr);
//		
//		if(CandleStickHelper.isBullishCandle(prev) && CandleStickHelper.isBearishCandle(prev) 
//				&& topPrev< topCurr && bottomPrev>bottomCurr) {
//			return true;
//		}
//		
//		return false;

	}
	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	
	
	

}
