package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;



public class GapUpBearishShootingRule extends GapUpRule{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_GAP_UP_BEARISH_SHOOTING;
	
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		StockBean prev = prevList.get(prevList.size()-1);
		
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;		
		
		boolean isUpCurr = (prev.getC()<curr.getC())?true:false;
		if(isUpCurr== false)
			return false;
		
		boolean isGapUpLarge = super.detect(prevList, curr);
		
		boolean isShootingStar = KHelper.isShootingStar(curr, true);
		// shooting 
		
		
		
//		double diffHL = curr.getH() - curr.getL();
//		double price21Curr = curr.getH() - diffHL/2;
		
		if(isGapUpLarge && isShootingStar) {
			return true;
		}
		
		return false;
	}	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
}
