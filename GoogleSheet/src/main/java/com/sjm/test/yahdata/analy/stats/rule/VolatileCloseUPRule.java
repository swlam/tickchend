package com.sjm.test.yahdata.analy.stats.rule;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;

public class VolatileCloseUPRule extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVENT_NA;
	
	private boolean isUpClose = true;
	private double minRatio = 0.0;
	
	public VolatileCloseUPRule(boolean isUpClose, double minRatio) {
		
		this.isUpClose = isUpClose;
		this.minRatio = minRatio;
		
		if(isUpClose) {						
			if(minRatio>=0.1)
				SIGN = CandleEventTagEnum.EVNT_VOLATILE_CLOSE_UP_10_PLUS_PCT;
			else if(minRatio>=0.08)
				SIGN = CandleEventTagEnum.EVNT_VOLATILE_CLOSE_UP_8_PCT_PLUS;
			else if(minRatio>=0.06)
				SIGN = CandleEventTagEnum.EVNT_VOLATILE_CLOSE_UP_6_PCT_PLUS;
			else if(minRatio>=0.04)
				SIGN = CandleEventTagEnum.EVNT_VOLATILE_CLOSE_UP_4_PCT_PLUS;
			else if(minRatio>=0.03 )
				SIGN = CandleEventTagEnum.EVNT_VOLATILE_CLOSE_UP_3_PCT_PLUS;
		}else {
			
			if(minRatio <= -0.1)
				SIGN = CandleEventTagEnum.EVNT_VOLATILE_CLOSE_DOWN_10_PLUS_PCT;
			else if(minRatio <= -0.08)
				SIGN = CandleEventTagEnum.EVNT_VOLATILE_CLOSE_DOWN_8_PCT_PLUS;
			else if(minRatio <= -0.06)
				SIGN = CandleEventTagEnum.EVNT_VOLATILE_CLOSE_DOWN_6_PCT_PLUS;
			else if(minRatio <= -0.04)
				SIGN = CandleEventTagEnum.EVNT_VOLATILE_CLOSE_DOWN_4_PCT_PLUS;
			else if(minRatio <= -0.03 )
				SIGN = CandleEventTagEnum.EVNT_VOLATILE_CLOSE_DOWN_3_PCT_PLUS;
			
		}
		
	}

	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		
		StockBean prev = prevList.get(prevList.size()-1);
		
		
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		
		
		
		
		boolean isUpCurr = (curr.getC()>prev.getC())?true:false;
//		if(isUpCurr== false)
//			return false;
		
		if(isUpClose == true && isUpCurr==true) {
			double vLow2HightBody = (curr.getH() - curr.getL()) / curr.getL();
		
			if(vLow2HightBody>=minRatio)
				return true;
		}else if(isUpClose == false && isUpCurr==false) {
			double vHight2LowBody = -(curr.getH() - curr.getL()) / curr.getH();
			
			if(vHight2LowBody<=minRatio)
				return true;
		}
		
		
		return false;
	}	

	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
}
