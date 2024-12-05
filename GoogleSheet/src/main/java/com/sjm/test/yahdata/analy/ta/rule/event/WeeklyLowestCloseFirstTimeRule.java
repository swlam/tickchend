package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.conts.ta.RuleConst;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;


public class WeeklyLowestCloseFirstTimeRule extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_WEEKLY_L_CLOSE_FIRSTTIME;
	
	private static final int NUM_OF_DAYS = RuleConst.WEEK_NUM_OF_DAYS;
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		
		
		boolean isPassYesterday = false;
		boolean isPassToday = false;
		try {
			StockBean prev = prevList.get(prevList.size()-1);
			List<StockBean>  prevPrevList = prevList.subList(prevList.size()- NUM_OF_DAYS-1, prevList.size()-1);
			StockBean lowBeanPrev = StreamTransformHelper.getPeriodHighest(prevPrevList, NUM_OF_DAYS);
			if( prev.getC() < lowBeanPrev.getL())
				isPassYesterday = true;
			StockBean lowBean = StreamTransformHelper.getPeriodHighest(prevList, NUM_OF_DAYS);
			if( curr.getC() < lowBean.getL())
				isPassToday = true;
			
		}catch(Exception e) {
			e.printStackTrace();
			
		}
		
		return (isPassYesterday==false && isPassToday==true);
	}
	
	

	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
}
