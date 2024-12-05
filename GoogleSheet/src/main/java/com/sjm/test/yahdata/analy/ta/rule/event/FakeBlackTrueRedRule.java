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
//public class FakeBlackTrueRedRule  extends VolRuleBase{
//
//	//假陰真陽
//	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_FAKE_BLACK_TRUE_WHITE_BODY;
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
//		boolean condition1 = KHelper.isBearishCandle(curr);
//		boolean condition2 = false;
//		double topPrev = KHelper.getBodyTopValue(prev);
//
//		if(curr.getC() > topPrev) {
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
