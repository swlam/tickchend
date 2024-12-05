package com.sjm.test.yahdata.analy.ta.rule.event;

import java.time.LocalDate;
import java.util.List;

import com.maas.util.DateHelper;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;

/*
 * 
 * 
 * prev is star, today is up close to the prev-H
 * 
 */
public class DayBeforeThanksgivingRule  extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_DAY_BEFORE_THANKSGIVING;
	
	
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {	
		boolean b = validate(prevList, curr);
		
		if(b==false)
			return b;
		
		try {
			int txnDateInt = curr.getTxnDateInt();
			
			LocalDate ld = LocalDate.parse((txnDateInt+1)+"", DateHelper.formatteryyyyMMdd);
			boolean isHit = DateHelper.isThanksgivingDate(ld);
			return isHit;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	
	
	

}
