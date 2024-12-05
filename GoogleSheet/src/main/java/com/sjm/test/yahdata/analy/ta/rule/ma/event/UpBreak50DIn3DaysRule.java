package com.sjm.test.yahdata.analy.ta.rule.ma.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;

public class UpBreak50DIn3DaysRule  extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_UP_BREAK_50_3DAYS;
	
//	public static int SHORT_TERM = 10;
	public static int LONG_TERM = 50;
	
	public UpBreak50DIn3DaysRule() {
//		super(SHORT_TERM, LONG_TERM);
	}
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {	
		boolean b = validate(prevList, curr);

		if(b==false)
			return b;
		
		double ma50 = curr.getPriceSma().getMa50();
		
		StockBean minus1 = prevList.get(prevList.size()-1);
		StockBean minus2 = prevList.get(prevList.size()-2);
		double ma50Minus1 = minus1.getPriceSma().getMa50(); //MovingAvgHelper.getPriceSma().getMabyLength(prevList.subList(0, prevList.size()-1), minus1, 50);
		double ma50Minus2 = minus2.getPriceSma().getMa50();//MovingAvgHelper.getPriceSma().getMabyLength(prevList.subList(0, prevList.size()-2), minus2, 50);
		
		if(curr.getC() > ma50 && minus1.getC()> ma50Minus1 && minus2.getC()>ma50Minus2)
			return true;
		return false;
	}
	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	

	
	

}
