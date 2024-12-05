package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;


public class GapDownHammerRule  extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_GAP_DOWN_HAMMER;
	
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		
		StockBean prev = prevList.get(prevList.size()-1);
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		
		
		boolean isHammer= KHelper.isHammer(curr, false);
		boolean isGapDown = KHelper.isGapDown(prev, curr);
		
//		boolean isPrevBlack = CandleStickHelper.isBlack(prev,prevPrev);
//		boolean isCurrRed = !CandleStickHelper.isBlack(curr, prev);
			
		if(isGapDown==true && isHammer==true) {	
			return true;
		}
		return false;
	}

	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
}
