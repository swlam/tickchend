package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;

/*
 * 
 * 
 * prev is star, today is up close to the prev-H
 * 
 */
public class CloseBelowStarRule  extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_CLOSE_BELOW_LOWER_SHADOW;
	
	
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {		
		boolean b = validate(prevList, curr);
		if(b==false)
			return b;
		
		try {
			StockBean prev = prevList.get(prevList.size()-1);
			
			if(prev.getVolume()==0)
				return false;
			
			boolean isHammer = KHelper.isHammer(prev, false);
			double bottomPrev =KHelper.getBodyBottomValue(prev);
			
			if(isHammer==true && bottomPrev> curr.getC() )
				return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	
	
	

}
