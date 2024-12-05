//package com.sjm.test.yahdata.analy.ta.rule.combination;
//
//import java.util.List;
//
//import com.sjm.test.yahdata.analy.bean.StockBean;
//import com.sjm.test.yahdata.analy.ta.VolRuleBase;
//import com.sjm.test.yahdata.analy.ta.conts.CandleTagEnum;
//import com.sjm.test.yahdata.analy.ta.rule.ma.BelowMA50Rule;
//import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossDown2D19DRule;
//
//
//public class CombineFallRule extends VolRuleBase{
//
//	public static CandleTagEnum SIGN = CandleTagEnum.COMBINE_BELOW_10D__GAP_DOWN;
//	
//	
//	private BelowMA50Rule r1;
//	private CrossDown2D19DRule r2;
////	private AboveMA50Rule r3;
//	
//	public CombineFallRule() {
//		r1 = new BelowMA50Rule();
//		r2 = new CrossDown2D19DRule();
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
