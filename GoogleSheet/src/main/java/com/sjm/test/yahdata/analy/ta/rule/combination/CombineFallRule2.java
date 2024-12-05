//package com.sjm.test.yahdata.analy.ta.rule.combination;
//
//import java.util.List;
//
//import com.sjm.test.yahdata.analy.bean.StockBean;
//import com.sjm.test.yahdata.analy.ta.VolRuleBase;
//import com.sjm.test.yahdata.analy.ta.conts.CandleTagEnum;
//import com.sjm.test.yahdata.analy.ta.rule.GapDownRule;
//import com.sjm.test.yahdata.analy.ta.rule.ma.BelowMA10Rule;
//
//
//public class CombineFallRule2 extends VolRuleBase{
//
//	public static CandleTagEnum SIGN = CandleTagEnum.COMBINE_2X19_CROSSDOWN__CLOSE_BELOW_50D;
//	
//	
//	private BelowMA10Rule r1;
//	private GapDownRule r2;
////	private AboveMA50Rule r3;
//	
//	public CombineFallRule2() {
//		r1 = new BelowMA10Rule();
//		r2 = new GapDownRule();
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
