package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PriceOverNDoubleVolRule  extends VolRuleBase{
	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_PRICE_OVER_DOUBLE_VOL;
	
	private static double VOL_ENGULFING_RATE = 2.0; // example: prev-vol: 10, cur-vol: 12
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		
		StockBean prev = prevList.get(prevList.size()-1);
		
		

		double topPrev = KHelper.getBodyTopValue(prev);
		
		boolean bCase1 = (curr.getC()> topPrev)?true:false;
		
		boolean volumneRequirement = false;
		//volume requirement
		try {
			if( curr.getVolume()> prev.getVolume() ) {
				if(curr.getVolume() / prev.getVolume() >=VOL_ENGULFING_RATE) {
					volumneRequirement = true;

				}
			}
		}catch(Exception e) {			
			log.error("PREV: "+prev.getTxnDate()+", vol:"+prev.getVolume()+", CURR: "+curr.getTxnDate()+", vol:"+curr.getVolume(), e);
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
