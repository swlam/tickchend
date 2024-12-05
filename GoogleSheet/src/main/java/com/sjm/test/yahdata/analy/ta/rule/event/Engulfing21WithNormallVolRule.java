package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Engulfing21WithNormallVolRule  extends BullishEngulfingRule{
	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_PRICE_ENGULFING_21_NORMAL_VOL;
	private static double PRICE_DIFF = 0.02; //drop -2%
	
	private static double VOL_ENGULFING_RATE = 1.3; // example: prev-vol: 10, cur-vol: 13
	
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
		
		
		//calc the body 
		double diffPrev = topPrev - bottomPrev;
		double downPercentagePrev = diffPrev / topPrev;
		
		
		if( PRICE_DIFF > downPercentagePrev ) {
			return false;
		}
		
		boolean isUpOverPrice21 = KHelper.isUpOverPrice21(prev, curr);
		
		boolean volumneRequirement = false;

		try {
			if( curr.getVolume()> prev.getVolume() ) {
				if(curr.getVolume() / prev.getVolume() >=VOL_ENGULFING_RATE) {
					volumneRequirement = true;

				}
			}
		}catch(Exception e) {			
			log.error("Error, PREV: "+prev.getTxnDate()+", vol:"+prev.getVolume()+", CURR: "+curr.getTxnDate()+", vol:"+curr.getVolume(), e);
		}
		
		
		if( curr.getC() > prev.getC() 
			&& isUpOverPrice21	
			&& volumneRequirement
			) 
		{					  
		  return true;			
		}else {
			return false;
		}
			

	}
	
	
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}

}
