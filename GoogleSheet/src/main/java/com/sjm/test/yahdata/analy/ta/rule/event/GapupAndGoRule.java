package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.KHelper;


//Gapup + Black Candle , and then Up Break

/*
1. current -> must RED candle  
2. go backward to search the nearest GapUP-candle(X)
3. GapUP-candle found, and (X+1) is Black Candle, Here the Top is the R(key-position) 
4. current.close > R
 */

public class GapupAndGoRule extends GapUpRule{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_GAPUP_AND_GO;
	
	private int WINDOW_SIZE = 20;
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		
		
		
		boolean b = validate(prevList, curr);
		if(b==false)
			return false;		
		
		
		
		int fromIndex = prevList.size() - WINDOW_SIZE;
		if(fromIndex<0) {
			return false;
		}
		
		StockBean nearestGapupStockBean = null;
		List<StockBean> targetStockList = prevList.subList(fromIndex, prevList.size());
		
		int targetIdx = -1;
		
		for( int i=targetStockList.size()-1; i>=1; i--) {
			StockBean sbcurr = targetStockList.get(i);
			StockBean sbPtev = targetStockList.get(i-1);
			if(KHelper.isGapUp(sbPtev, sbcurr) && KHelper.isBearishCandle(sbcurr)) {
				nearestGapupStockBean = sbcurr;
				targetIdx = i;
				break;
			};
		}
		if(targetIdx==-1) {
			return false;
		}
		targetIdx = targetIdx+1;
		List<StockBean> subtargetStockList = targetStockList.subList(targetIdx, targetStockList.size());
		Double maxClose = subtargetStockList.parallelStream().mapToDouble(StockBean::getC).max().orElse(Double.NaN);
		
		if(nearestGapupStockBean!=null ) {
			if(nearestGapupStockBean.getH()< curr.getC() && maxClose.isNaN() ) 
				return true; // found curr-stock bean is the first break in past elements
		}
		
		
		return false;
	}	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
}
