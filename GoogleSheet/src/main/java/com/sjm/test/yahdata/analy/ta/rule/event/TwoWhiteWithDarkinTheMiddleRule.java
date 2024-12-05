package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;


public class TwoWhiteWithDarkinTheMiddleRule  extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_TWO_WHITE_WITH_BLACK;
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		
		StockBean prev1 = prevList.get(prevList.size()-1);
		StockBean prev2 = prevList.get(prevList.size()-2);
		
		boolean isCurrWhite = KHelper.isBullishCandle(curr);
		boolean isPrev1Black = KHelper.isBearishCandle(prev1);
		boolean isPrev2White = KHelper.isBullishCandle(prev2);
			
		if( !(isCurrWhite && isPrev1Black && isPrev2White) )
			return false;
		
		boolean isPass = false;
		//c
		if(curr.getC()> prev1.getH() && curr.getC()> prev2.getH()) {
			isPass = true;
		}
		
		if(isPass && prev1.getDayVolumeChgPct()< 0.08 && prev2.getVolume() < curr.getVolume() && curr.getDayVolumeChgPct() >1.8)
			return true;
		
		return false;

	}
	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	
	
	

}
