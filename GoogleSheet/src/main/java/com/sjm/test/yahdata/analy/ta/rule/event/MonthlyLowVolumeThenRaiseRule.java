package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.conts.ta.RuleConst;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;

/*
Size: Past 5 weeks data
1. sort volume by asc, current vol <= first 5. -->低量
2. prev is DOWN, curr is RAISE

 */

public class MonthlyLowVolumeThenRaiseRule extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_MONTHLY_LOW_VOL_THEN_RAISE;

	public boolean detect(List<StockBean> prevList, StockBean curr) {
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		int subListStartIdx = prevList.size()- RuleConst.MONTH_NUM_OF_DAYS;
		if(subListStartIdx<0)
			subListStartIdx = 0;
		
		List<StockBean>  prevSubList = prevList.subList(subListStartIdx, prevList.size());
		
		StockBean prev = prevList.get(prevList.size()-1);
		
		boolean isDownPrev = (prev.getC()<prev.getO())?true:false;				
		boolean isRaiseCurr = (curr.getC()>prev.getC())?true:false;
		
		if(isDownPrev== false && isRaiseCurr == false )
			return false;
		
		
//		double price21Prev = CandleStickHelper.getBodyPrice21(prev);
//		double prevBodyTop = CandleStickHelper.getBodyTopValue(prev);
		
		boolean isUpOverPrev21 = KHelper.isUpOverPrice21(prev, curr);
		
		//volume requirement
		double ratio1 = 1.2;
		double ratio2 = 2.5;
		boolean isMeetVolumneRequirement = KHelper.isMeetVolumeChangePositiveRatio(prev, curr, ratio1, ratio2);
		
		//PrevList is low level volume
		boolean isVolumeAtLowLevel = true;
		try {
			double volLevel = getLowLevelVolume(prevSubList);
			if( prev.getVolume() >volLevel)
				isVolumeAtLowLevel = false;
		}catch(Exception e) {
			e.printStackTrace();
			isVolumeAtLowLevel = false;
		}
		
		if( curr.getC() > prev.getC() 
			&& isUpOverPrev21
			&& isMeetVolumneRequirement
			&& isVolumeAtLowLevel
		) 
		{					  
		  return true;			
		}else {
			return false;
		}
		

	}	
	
	protected double getLowLevelVolume(List<StockBean> orgList) throws Exception{
//		if(orgList.size()< 10)
//			throw new Exception("Check and Sort by Volume, Require list size >10 ");
		
		List<StockBean> sortedList = orgList.parallelStream()
				.sorted(Comparator.comparingDouble(StockBean::getVolume))
				.collect(Collectors.toList());
		
		List<StockBean> theMinList = sortedList.subList(0, 5);
		
		double theLowest5Avg = theMinList.parallelStream()
		.mapToDouble(StockBean::getVolume)
		.average()
		.orElse(Double.NaN);
		
		return theLowest5Avg;
		
	}
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}

}
