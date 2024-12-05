//package com.sjm.test.yahdata.analy.ta.rule.event;
//
//import java.util.List;
//
//import com.sjm.test.yahdata.analy.bean.raw.StockBean;
//import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
//import com.sjm.test.yahdata.analy.ta.KHelper;
//import com.sjm.test.yahdata.analy.ta.VolRuleBase;
//
//
//public class FakeRedTrueBlackRule  extends VolRuleBase{
//
//	//假陽真陰
//	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_FAKE_RED_TRUE_BLACK;
//
//	public boolean detect(List<StockBean> prevList, StockBean curr) {
//
//		StockBean prev = prevList.get(prevList.size()-1);
//
//		boolean b = validate(prevList, curr);
//		if(b==false)
//			return false;
//
//
//		boolean condition1 = KHelper.isBullishCandle(curr);
//		boolean condition2 = false;
//		double bottomPrev = KHelper.getBodyBottomValue(prev);//.getBodyTopValue(prev);
//
//		if(curr.getC() < bottomPrev) {
//			condition2 = true;
//		}
//		boolean rtn = (condition1 && condition2);
//		return rtn;
//	}
//
//
//	@Override
//	public CandleEventTagEnum getBenchmarkCandleTag() {
//		return SIGN;
//	}
//}
