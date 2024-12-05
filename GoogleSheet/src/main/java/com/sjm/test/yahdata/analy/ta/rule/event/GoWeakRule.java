package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;

/*
the highest price

 */

public class GoWeakRule extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_GO_WEAK;
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		
		List<StockBean> checkStockList = prevList.subList(prevList.size()-7, prevList.size());
		StockBean maxByHStock = checkStockList
			      .stream()
			      .max(Comparator.comparing(StockBean::getH))
			      .orElseThrow(NoSuchElementException::new);
		
		
		StockBean prev = prevList.get(prevList.size()-1);
		StockBean prev2 = prevList.get(prevList.size()-2);
		StockBean prev3 = prevList.get(prevList.size()-3);
		StockBean prev4 = prevList.get(prevList.size()-4);
		
		boolean isMaxPointNear = false;
		if(maxByHStock.getTxnDateInt() == prev.getTxnDateInt() || maxByHStock.getTxnDateInt() == prev2.getTxnDateInt() || maxByHStock.getTxnDateInt() == prev3.getTxnDateInt()) {
			isMaxPointNear = true;
		}
		
		
		boolean isCurrBelowThreeDayLow = curr.getC()< prev.getL() && curr.getC()< prev2.getL() && curr.getC()< prev3.getL();
		boolean isPrevBelowThreeDayLow = prev.getC()< prev2.getL() && prev.getC()< prev3.getL() && prev.getC()< prev4.getL();
		
		
		if(isMaxPointNear==true && isCurrBelowThreeDayLow == true && isPrevBelowThreeDayLow==false) {
			return true;
		}
		return false;
		
		

	}	
	
	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}

}
