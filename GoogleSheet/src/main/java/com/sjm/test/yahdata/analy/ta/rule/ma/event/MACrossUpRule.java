package com.sjm.test.yahdata.analy.ta.rule.ma.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;
import com.sjm.test.yahdata.analy.ta.helper.MovingAvgHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class MACrossUpRule  extends VolRuleBase{
	
	private int shortTermMA = 0;
	private int longTermMA = 0;
	
	//short-term moving average
	public MACrossUpRule(int shortTermMA, int longTermMA) {
		this.shortTermMA = shortTermMA;
		this.longTermMA = longTermMA;
	}
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		boolean b = validate(prevList, curr);
		
		if(b==false || curr.getPriceSma() ==null)
			return false;

		
		boolean rtnB = false;
		
		try {
		
			List<StockBean> prevprevList = prevList.subList(0,  prevList.size()-1);
			StockBean prevStock = prevList.get(prevList.size()-1);
		
//			double maShort = MovingAvgHelper.getPriceSma().getMaPricebyLength(prevList, curr, shortTermMA);
//			double maLong = MovingAvgHelper.getPriceSma().getMaPricebyLength(prevList, curr, longTermMA);
//			double maShortPrev = MovingAvgHelper.getPrevMAbyLength(prevList, shortTermMA);
//			double maLongPrev = MovingAvgHelper.getPrevMAbyLength(prevList, longTermMA);
			
			
			double maShort = func(prevList, curr, shortTermMA);
			double maLong = func(prevList, curr, longTermMA);
			
			double maShortPrev = func(prevprevList, prevStock, shortTermMA);
			double maLongPrev = func(prevprevList, prevStock, longTermMA);
			
			boolean currMaCondition = ( maShort >= maLong);
			boolean prevMaCondition = ( maShortPrev < maLongPrev);
		
			boolean isCrossUp = false;
			if( currMaCondition==true  && prevMaCondition==true)
				isCrossUp = true;
			
			if(isCrossUp == true && curr.getL() < maLong && curr.getC() > maShort) {
				rtnB = isCrossUp;
			}
		}catch(Exception e) {
			log.warn(curr.getTxnDate(), e);
		}
		
		return rtnB;

	}	
	
	public abstract CandleEventTagEnum getBenchmarkCandleTag();
	
	private double func(List<StockBean> prevList, StockBean currStock, int maField) {
		if(currStock.getPriceSma()==null)
			return 0.0;
		
		double rtnMaValue =0.0;
		switch (maField){
			case 2: rtnMaValue = currStock.getPriceSma().getMa2();
				break;
			case 5: rtnMaValue = currStock.getPriceSma().getMa5();
				break;
			case 10: rtnMaValue = currStock.getPriceSma().getMa10();
				break;
			case 19: rtnMaValue = currStock.getPriceSma().getMa19();
				break;
			case 20: rtnMaValue = currStock.getPriceSma().getMa20();
				break;
			case 50: rtnMaValue = currStock.getPriceSma().getMa50();
				break;
			case 100: rtnMaValue = currStock.getPriceSma().getMa100();
				break;
			case 200: rtnMaValue = currStock.getPriceSma().getMa200();
				break;
			case 250: rtnMaValue = currStock.getPriceSma().getMa250();
				break;
			default:
				rtnMaValue = MovingAvgHelper.getMAPricebyLength(prevList, currStock, maField);
		}
		
		return rtnMaValue;
	}

}
