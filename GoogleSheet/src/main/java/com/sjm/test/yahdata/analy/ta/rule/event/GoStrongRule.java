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

public class GoStrongRule extends VolRuleBase{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_GO_STRONG;
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;
		
		// week low
				List<StockBean> checkStockList = prevList.subList(prevList.size()-7, prevList.size());
				StockBean minByLStock = checkStockList
					      .stream()
					      .min(Comparator.comparing(StockBean::getL))
					      .orElseThrow(NoSuchElementException::new);

				 
				 
				StockBean prev = prevList.get(prevList.size()-1);
				StockBean prev2 = prevList.get(prevList.size()-2);
				StockBean prev3 = prevList.get(prevList.size()-3);
				StockBean prev4 = prevList.get(prevList.size()-4);
				
				boolean isMinPointNear = false;
				if(minByLStock.getTxnDateInt() == prev.getTxnDateInt() || minByLStock.getTxnDateInt() == prev2.getTxnDateInt()) {
					isMinPointNear = true;
				}
				
				boolean isCurrAboveTwoDayH = curr.getC()> prev.getH() && curr.getC()> prev2.getH() && curr.getC()> prev3.getH();
				boolean isPrevAboveTwoDayH = prev.getC()> prev2.getH() && prev.getC()> prev3.getH() && prev.getC()> prev4.getH();
						
				if(isMinPointNear==true && isCurrAboveTwoDayH==true && isPrevAboveTwoDayH==false) {
					return true;
				}
				return false;
		
		

	}	
	
	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}

}
