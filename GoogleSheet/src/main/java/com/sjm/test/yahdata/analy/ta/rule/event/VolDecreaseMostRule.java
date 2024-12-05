package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;


public class VolDecreaseMostRule  extends VolRuleBase{

	private CandleEventTagEnum SIGN = null;//CandleTagEnum.EVNT_DECREASE_MOST_VOL;
	
	public double range1Ratio1 = 0.4;
	private boolean isUpClose = true;
	
	public VolDecreaseMostRule(boolean isUpClose) {
		
		this.isUpClose = isUpClose;
		if(isUpClose) {
			SIGN = CandleEventTagEnum.EVNT_DECREASE_MOST_VOL_RED;
		}else {
			SIGN = CandleEventTagEnum.EVNT_DECREASE_MOST_VOL_BLACK;
		}
		
	}

	public boolean detect(List<StockBean> prevList, StockBean curr) {
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		
		try {
			StockBean prev = prevList.get(prevList.size()-1);
			if(prev.getVolume()==0)
				return false;
			
			double ratio = this.getIncreaseRatio(curr, prev);
			
			
			boolean bb= false;
			if(isUpClose) {
				bb = KHelper.isWhite(curr, prev);	
			}else {
				bb = KHelper.isBlack(curr, prev);
			}
			
			if(range1Ratio1 > ratio && bb==true)
				return true;
			}catch(Exception e) {
				e.printStackTrace();
				return false;
			}
		return false;
	}
	
	public double getIncreaseRatio(StockBean curr, StockBean prev) {
		return (double)curr.getVolume() / (double)prev.getVolume();
	}
	
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}

}
