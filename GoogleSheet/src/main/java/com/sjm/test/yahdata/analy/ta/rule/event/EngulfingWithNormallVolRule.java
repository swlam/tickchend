package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EngulfingWithNormallVolRule  extends BullishEngulfingRule{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_PRICE_ENGULFING_NORMAL_VOL;
	
	
	
	
	private static double VOL_ENGULFING_RATE = 1.05; // example: prev-vol: 10, cur-vol: 12
	
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
		double bottomPrev =KHelper.getBodyBottomValue(prev);
		
		boolean bCase1 = (curr.getC()> topPrev && curr.getO()<bottomPrev)?true:false;
		
		boolean volumneRequirement = false;
		//volume requirement
		try {
			if( curr.getVolume()> prev.getVolume() ) {
				if(curr.getVolume() / prev.getVolume() >=VOL_ENGULFING_RATE) {
					volumneRequirement = true;

				}
			}
		}catch(Exception e) {
			log.error("Error, PREV: "+prev.getTxnDate()+", vol:"+prev.getVolume()+", CURR: "+curr.getTxnDate()+", vol:"+curr.getVolume(), e);
		}
		
		
		if( bCase1 && volumneRequirement ) 
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
