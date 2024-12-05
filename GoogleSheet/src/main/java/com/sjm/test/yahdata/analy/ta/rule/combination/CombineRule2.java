//package com.sjm.test.yahdata.analy.ta.rule.combination;
//
//import java.util.List;
//
//import com.sjm.test.yahdata.analy.bean.StockBean;
//import com.sjm.test.yahdata.analy.ta.VolRuleBase;
//import com.sjm.test.yahdata.analy.ta.conts.CandleTagEnum;
//import com.sjm.test.yahdata.analy.ta.rule.ma.AboveMA50Rule;
//import com.sjm.test.yahdata.analy.ta.rule.ma.MA10AboveMA50Rule;
//import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossUp3D18DRule;
//
//
//public class CombineRule2 extends VolRuleBase{
//
//	public static CandleTagEnum SIGN = CandleTagEnum.COMBINE_3D_ABOVE_18D__CLOSE_ABOVE_50D__10D_ABOVE_50D;
//	
//	
//	private MA10AboveMA50Rule r1;
//	private CrossUp3D18DRule r2;
//	private AboveMA50Rule r3;
//	
//	public CombineRule2() {
//		r1 = new MA10AboveMA50Rule();
//		r2 = new CrossUp3D18DRule();
//		r3 = new AboveMA50Rule();
//		r3.setNumOfDays(200);
//	}
//	
//	
//	public boolean detect(List<StockBean> prevList, StockBean curr) {
////		StockBean prev = prevList.get(prevList.size()-1);
//		boolean b1 = r1.detect(prevList, curr);
//		boolean b2 = r2.detect(prevList, curr);
//		boolean b3 = r3.detect(prevList, curr);
//		if(b1 ==true && b2 ==true && b3 ==true )
//			return true;
//		
//		return false;
//	}
//	
//	@Override
//	public CandleTagEnum getCandleTag() {
//		return SIGN;
//	}
//
//}
