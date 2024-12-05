package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;


public class OpenHighCloseLowVolumeIncrementalRule extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_OPEN_HIGH_CLOSE_LOW_VOL_INCREMENTAL_ABV_BODY_TOP;

	public boolean detect(List<StockBean> prevList, StockBean curr) {
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		
		StockBean prev = prevList.get(prevList.size()-1);
		
		boolean isOpenHighSustain = KHelper.isOpenHighCloseLowAbvBodyTop(prev, curr);
	
		//volume requirement
		double ratio1 = 1.05;
		double ratio2 = 1.5;
		boolean isMeetVolumneRequirement = KHelper.isMeetVolumeChangePositiveRatio(prev, curr, ratio1, ratio2);
		
		
		if( isOpenHighSustain && isMeetVolumneRequirement ) 
		{					  
		  return true;			
		}else {
			return false;
		}

	}
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	

}
