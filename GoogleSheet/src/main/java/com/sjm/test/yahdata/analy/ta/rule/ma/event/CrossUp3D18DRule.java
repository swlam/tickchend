package com.sjm.test.yahdata.analy.ta.rule.ma.event;

import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;

/**
 * 
 *  
 * just count one time MA3 > MA18
 *  
 *  
 * 
 * @author samswl
 *
 */
public class CrossUp3D18DRule  extends MACrossUpRule{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_CROSS_UP_3D_18D;
	
	public static int SHORT_TERM = 3;
	public static int LONG_TERM = 18;
	
	public CrossUp3D18DRule() {
		super(SHORT_TERM, LONG_TERM);
	}
	
//	public boolean detect(List<StockBean> prevList, StockBean curr) {
//		return super.detect(prevList, curr);
//	}
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	

	
	

}
