//package com.sjm.test.yahdata.analy.ta.rule.combination;
//
//import java.util.List;
//
//import com.sjm.test.yahdata.analy.bean.StockBean;
//import com.sjm.test.yahdata.analy.ta.VolRuleBase;
//import com.sjm.test.yahdata.analy.ta.conts.CandleTagEnum;
//import com.sjm.test.yahdata.analy.ta.rule.ma.AboveMA50Rule;
//import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossUp10D20DRule;
//
//
//public class CombineRule3 extends VolRuleBase{
//
//	public static CandleTagEnum SIGN = CandleTagEnum.COMBINE_10X20_CROSSUP__CLOSE_ABOVE_50D;
//	
//	
//	private CrossUp10D20DRule r1;
//	private AboveMA50Rule r2;
////	private AboveMA50Rule r3;
//	
//	public CombineRule3() {
//		r1 = new CrossUp10D20DRule();
//		r2 = new AboveMA50Rule();
////		r3 = new AboveMA50Rule();
////		r3.setNumOfDays(200);
//	}
//	
//	
//	public boolean detect(List<StockBean> prevList, StockBean curr) {
////		StockBean prev = prevList.get(prevList.size()-1);
//		boolean b1 = r1.detect(prevList, curr);
//		boolean b2 = r2.detect(prevList, curr);
////		boolean b3 = r3.detect(prevList, curr);
//		if(b1 ==true && b2 ==true )
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
