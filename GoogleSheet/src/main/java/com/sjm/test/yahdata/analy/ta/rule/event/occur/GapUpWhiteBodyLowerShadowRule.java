package com.sjm.test.yahdata.analy.ta.rule.event.occur;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;


public class GapUpWhiteBodyLowerShadowRule  extends VolRuleBase{

	
	private CandleEventTagEnum SIGN = CandleEventTagEnum.OCCUR_EVNT_GAPUP_WHITE_BODY_WITH_LOWER_SHADOW;
	
	public GapUpWhiteBodyLowerShadowRule() {
		this.setOccurCandleTag(true);
	}
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		
		StockBean prev = prevList.get(prevList.size()-1);
		
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		
		boolean isGapUp = KHelper.isGapUp(prev, curr);
		if(isGapUp == false)
			return false;
		
		boolean condition1 = curr.getL() < KHelper.getBodyBottomValue(curr);
		
//		condition1 = (condition1==true)?curr.getL()<CandleStickHelper.getBodyTopValue(prev):false;
		return condition1;
	}

	
	@Override
	public CandleEventTagEnum getOccurCandleTag() {
		return SIGN;
		
	}
	
	
}
