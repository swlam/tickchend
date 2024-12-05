package com.sjm.test.yahdata.analy.ta.rule.ma.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;
import com.sjm.test.yahdata.analy.ta.helper.MovingAvgHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class MACrossDownRule  extends VolRuleBase{
	
	private int shortTermMA = 2;
	private int longTermMA = 19;
	
	//short-term moving average
	public MACrossDownRule(int shortTermMA, int longTermMA) {
		this.shortTermMA = shortTermMA;
		this.longTermMA = longTermMA;
	}
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		
		
		
		boolean isCrossDown = false;
		
		try {
			double maShort = MovingAvgHelper.getMAPricebyLength(prevList, curr, shortTermMA);
			double maLong = MovingAvgHelper.getMAPricebyLength(prevList, curr, longTermMA);
			
			
//			double maShortPrev = this.getPrevMAbyLength(prevList, shortTermMA);
//			double maLongPrev = this.getPrevMAbyLength(prevList, longTermMA);
//			List<StockBean> prevPrevList = prevList.subList(0, prevList.size()-2);
//			double maShortPrev = MovingAvgHelper.getMAbyLength(prevPrevList, prevList.get(prevList.size()-1),shortTermMA);
//			double maLongPrev = MovingAvgHelper.getMAbyLength(prevPrevList, prevList.get(prevList.size()-1), longTermMA);
			double maShortPrev = MovingAvgHelper.getPrevMAbyLength(prevList, shortTermMA);
			double maLongPrev = MovingAvgHelper.getPrevMAbyLength(prevList, longTermMA);
			
			boolean currMaCondition = ( maShort <= maLong);
			boolean prevMaCondition = ( maShortPrev  > maLongPrev);
		
			
			if( currMaCondition==true  && prevMaCondition==true)
				isCrossDown = true;
		}catch(Exception e) {
			log.error(curr.getTxnDate(), e);
			
		}
		
		return isCrossDown;
	}	
	
	
	public abstract CandleEventTagEnum getBenchmarkCandleTag();

}
