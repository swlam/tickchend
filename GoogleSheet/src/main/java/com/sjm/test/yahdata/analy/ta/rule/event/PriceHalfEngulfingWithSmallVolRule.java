package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;

public class PriceHalfEngulfingWithSmallVolRule extends BullishEngulfingRule {

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_PRICE_ENGULFING_21_SMALL_VOL;
	
	private static double PRICE_DIFF = 0.02; //drop -2%
	
	
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		
		boolean isBullEngul = super.detect(prevList, curr);
		if(isBullEngul == false) {
			return false;
		}
		
		
		StockBean prev = prevList.get(prevList.size()-1);
		
		
		double topPrev = KHelper.getBodyTopValue(prev);
		double bottomPrev = KHelper.getBodyBottomValue(prev);
				
		
		//Calculate body distance
		double diffPrev = topPrev - bottomPrev;
		double downPercentagePrev = diffPrev / topPrev;
		
		
		if( PRICE_DIFF > downPercentagePrev ) {
			return false;
		}
		
//		double price21Prev = topPrev - diffPrev/2;
		boolean isUpOverPrice21 = KHelper.isUpOverPrice21(prev, curr);
		
		if( curr.getC() > prev.getC() 
			&& isUpOverPrice21	
			&& (prev.getVolume() >= curr.getVolume())) 
		{					  
		  return true;			
		}else {
			return false;
		}
			

	}



	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	
	

	
	
	

}
