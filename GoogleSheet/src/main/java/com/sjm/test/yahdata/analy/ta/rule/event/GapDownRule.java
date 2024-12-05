package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;



public class GapDownRule extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_GAP_DOWN;
	
	public GapDownRule() {
	}
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		boolean b = validate(prevList, curr);
		
		if(b==false)
			return false;
		
		
		StockBean prev = prevList.get(prevList.size()-1);
		
		try {
			boolean isgd = KHelper.isGapDown(prev, curr);
			
			return isgd;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}

	}	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
}
