package com.sjm.test.yahdata.analy.ta.rule.ma.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;

/**
 * 
 *  Test Target and Date: CSGP: 2021-07-30 
 * 
 * @author samswl
 *
 */
public class CrossUp3MALinesRule  extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_CROSS_UP_3MA;
	
	public static int MA_DAY5 = 5;
	public static int MA_DAY10 = 10;
	public static int MA_DAY20 = 20;
	public static int MA_DAY50 = 50;

	public boolean detect(List<StockBean> prevList, StockBean curr) {
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		
		StockBean prev = prevList.get(prevList.size()-1);
		
		
		boolean isCrossUp = false;
		
		
		double ma5 = curr.getPriceSma().getMa5();//MovingAvgHelper.getPriceSma().getMabyLength(prevList, curr, MA_DAY5);
		double ma10 = curr.getPriceSma().getMa10();//MovingAvgHelper.getPriceSma().getMabyLength(prevList, curr, MA_DAY10);
		double ma20 = curr.getPriceSma().getMa20();//MovingAvgHelper.getPriceSma().getMabyLength(prevList, curr, MA_DAY20);
		double ma50 = curr.getPriceSma().getMa50();//MovingAvgHelper.getPriceSma().getMabyLength(prevList, curr, MA_DAY50);
		
//		double ma5Prev = this.getPrevMAbyLength(prevList, MA_DAY5);
//		double ma10Prev = this.getPrevMAbyLength(prevList, MA_DAY10);
//		double ma20Prev = this.getPrevMAbyLength(prevList, MA_DAY20);
		
		double ma5Prev = prev.getPriceSma().getMa5();//MovingAvgHelper.getPriceSma().getMabyLength(prevList, prevList.get(prevList.size()-1),MA_DAY5);
		double ma10Prev = prev.getPriceSma().getMa10();//MovingAvgHelper.getPriceSma().getMabyLength(prevList, prevList.get(prevList.size()-1), MA_DAY10);
		double ma20Prev = prev.getPriceSma().getMa20();//MovingAvgHelper.getPriceSma().getMabyLength(prevList, prevList.get(prevList.size()-1), MA_DAY20);
		
		boolean currMa = ( curr.getC()  > ma5) && ( curr.getC()  > ma10) && ( curr.getC()  > ma20);
		boolean prevMa = ( prev.getC()  < ma5Prev) || ( prev.getC()  < ma10Prev) || ( prev.getC()  < ma20Prev);
		
		boolean isAllAboveMa50 = curr.getC()>ma50 && ma5>ma50 && ma10>ma50 && ma20>ma50 ;
		
		if( currMa==true  && prevMa==true && isAllAboveMa50==true)
			isCrossUp = true;
	
		
		return isCrossUp;

	}	
	
//	protected double getMAbyLength(List<StockBean> prevList, StockBean curr, int maLength) {
//		List<StockBean>  prevSubList = prevList.subList(prevList.size()- (maLength-1), prevList.size());
//		
//		List<StockBean>  avgList = new ArrayList<StockBean>(maLength);
//		avgList.addAll(prevSubList);
//		avgList.add(curr);
//		return getAverage(avgList);
//	}
//
//	
//	protected double getPrevMAbyLength(List<StockBean> prevList, int maLength) {
//		List<StockBean>  prevSubList = prevList.subList((prevList.size()- maLength), prevList.size());
//		
//		List<StockBean>  avgList = new ArrayList<StockBean>(maLength);
//		avgList.addAll(prevSubList);
//		
//		return getAverage(avgList);
//	}
	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	
//	protected double getAverage(List<StockBean> orgList) {
//		double avg = orgList.parallelStream().mapToDouble(a -> a.getC()).average().getAsDouble();
//		return avg;
//	}

	
	

}
