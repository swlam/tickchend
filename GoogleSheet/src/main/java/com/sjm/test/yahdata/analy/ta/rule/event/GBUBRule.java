package com.sjm.test.yahdata.analy.ta.rule.event;

import java.util.List;
import java.util.stream.Collectors;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.helper.MovingAvgHelper;


//Gapup + Black Candle , and then Up Break

/*
1. current -> must RED candle  
2. go backward to search the nearest GapUP-candle(X)
3. GapUP-candle found, and (X+1) is Black Candle, Here the Top is the R(key-position) 
4. current.close > R
 */

public class GBUBRule extends GapUpRule{

	private CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_GBUB;
	
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
//		Double maxClose = subtargetStockList.parallelStream().mapToDouble(StockBean::getC).max().orElse(Double.NaN);
		
		
		
		if(nearestGapupStockBean != null ) {
			double nearGapupH = nearestGapupStockBean.getH();
//			Long cntOverNearestGapup = subtargetStockList.parallelStream().filter(x -> x.getC()> nearGapupH ).count();
			List<StockBean> subList2 = subtargetStockList.parallelStream().filter(x->  x.getC() > nearGapupH ).collect(Collectors.toList());
			if(subList2==null || subList2.isEmpty())
				return false;
			
			StockBean firstUpBreakStock = subList2.get(0);
//			double avgVolume = getPastDayAvgVolume(prevList);
//			if(firstUpBreakStock.getVolume()<avgVolume)
//				return false;
			
			List<StockBean> foundBreakList = subtargetStockList.parallelStream().filter(
					x-> StreamTransformHelper.yyyyMMddtoInt(x.getTxnDate())> StreamTransformHelper.yyyyMMddtoInt(firstUpBreakStock.getTxnDate())
					    && x.getC()> firstUpBreakStock.getH()
					).collect(Collectors.toList());
			
			// I suppose foundBreakList is EMPTY
			if((foundBreakList==null || foundBreakList.isEmpty() || foundBreakList.size()==0) && curr.getC()>firstUpBreakStock.getH()) {//			
//				System.out.print("Hit: GapUPBlack: "+nearGapupH+"\tFirstBreak: "+firstUpBreakStock + "\t Up Break : "+curr);
				return true; // found curr-stock bean is the first break in past elements
			}
		}
		
		
		
		return false;
	}	
	
	public double getPastDayAvgVolume(List<StockBean> prevList) {
		List<StockBean> orgList = MovingAvgHelper.getNumOfDaysData(prevList, 20);
		double avgVol = orgList.parallelStream().filter(x->x.getVolume()>0).mapToDouble(a -> a.getVolume()).average().getAsDouble();
		return avgVol ;
	}
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
}
