package com.sjm.test.yahdata.analy.instantresult;

import com.sjm.test.yahdata.analy.helper.CFGHelper;
import com.sjm.test.yahdata.analy.model.InstantPerformanceResult;
import com.sjm.test.yahdata.analy.probability.bean.BenchmarkBeanResult;
/*

	
 */
public class TrendGettingWeakerF extends BaseTrendGettingDirection{
	
	@Override
	public String goAnalyze(InstantPerformanceResult x) {
		boolean isHit = this.isCategoryHit(x);
		
		if(isHit)
			return StockPickTagger.SHORT_4;
		
		return null;
	}

	@Override
	public boolean isCategoryHit(InstantPerformanceResult elemt) {
		//2024-01-01;
		String targetMonth = elemt.getCurrentStockBean().getTxnDate().substring(4, 8);
		
//		String targetMonth = "-01-";
		
		BenchmarkBeanResult benchmarkResult = CFGHelper.getMonthBenchmark(elemt.getCurrentStockBean().getStockCode(), targetMonth, BaseTrendGettingDirection.DEFAULT_YEARS_OF_DATA);
		if(benchmarkResult == null)
			return false;
		
//		double avgC2hScore = benchmarkResult.getC2hStat().getAvg() - elemt.getMtdChangeO2PeriodHPct();
//		double min2ndC2hScore = benchmarkResult.getC2hStat().getMin2nd() - elemt.getMtdChangeO2PeriodHPct();
		double avgC2lScore = benchmarkResult.getC2plStat().getAvg() - elemt.getMtdChangeO2PLPct();
//		double min2ndC2lScore = benchmarkResult.getC2lStat().getMin2nd() - elemt.getMtdChangeO2PeriodLPct();			
		
		if(benchmarkResult.getRisesRatioC2C()<0.55)
			return true;
		
//		if(avgC2hScore > 0 && min2ndC2hScore>=0)
		if(avgC2lScore <0 && benchmarkResult.getC2phStat().getAvg()>0 && benchmarkResult.getC2plStat().getAvg()<0) {
//			if( (benchmarkResult.getC2hStat().getAvg() + benchmarkResult.getC2lStat().getAvg()) > 0){
				return true;
//			}
		}
		
		return false;
		
			
	}
	
	public String toDescription() {
		return "月統計佔優";
	}
}
