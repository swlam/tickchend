package com.sjm.test.yahdata.analy.ta.rule.ma.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;

/**
 * 
 *  
 *
 */
public class ShortArrangeRule  extends VolRuleBase{

	public CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_SHORT_ARRANGE;
	
	public ShortArrangeRule() {
	}
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {	
		boolean b = validate(prevList, curr);
		if("2022-03-21".equalsIgnoreCase(curr.getTxnDate()))
			System.out.println("pause");
		
		if(b==false)
			return b;
		
		StockBean prev = prevList.get(prevList.size()-1);
		
		
//		double ma5 = curr.getPriceSma().getMa5();
		double ma10 = curr.getPriceSma().getMa10();
		double ma20 = curr.getPriceSma().getMa20();
		double ma50 = curr.getPriceSma().getMa50();
//		double ma100 = curr.getPriceSma().getMa100();
		double ma200 = curr.getPriceSma().getMa200();
//		double ma250 = curr.getPriceSma().getMa250();
		
		
//		double prevma5 = prev.getPriceSma().getMa5();
		double prevma10 = prev.getPriceSma().getMa10();
		double prevma20 = prev.getPriceSma().getMa20();
		double prevma50 = prev.getPriceSma().getMa50();
		double prevma200 = prev.getPriceSma().getMa200();
		
//		double prevma3 = MovingAvgHelper.getPriceSma().getMabyLength(prevPrevList,  prevList.get(prevList.size()-1), 3);
		
		boolean isCurrShortArrange = ma10<ma20 && ma20<ma50 && ma50<ma200;
		boolean isPrevShortArrange = prevma10<prevma20 && prevma20<prevma50 && prevma50<prevma200;
		if(isCurrShortArrange==true && isPrevShortArrange==false )
			return true;
		return false;
	}
	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	

}
