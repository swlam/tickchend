package com.sjm.test.yahdata.analy.instantresult;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.helper.CFGHelper;
import com.sjm.test.yahdata.analy.model.InstantPerformanceResult;
import com.sjm.test.yahdata.analy.probability.bean.BenchmarkBeanResult;
/*



	
	
 */
public class TrendGettingStrongerF extends BaseTrendGettingDirection{

	public TrendGettingStrongerF() {
	}
	
	@Override
	public String goAnalyze(InstantPerformanceResult x) {
		boolean isHit = this.isCategoryHit(x);
		
		if(isHit)
			return StockPickTagger.LONG_6;
		
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
		
		double avgC2hScore = benchmarkResult.getC2phStat().getAvg() - elemt.getMtdChangeO2PHPct();
//		double min2ndC2hScore = benchmarkResult.getC2hStat().getMin2nd() - elemt.getMtdChangeO2PeriodHPct();
//		double avgC2lScore = benchmarkResult.getC2lStat().getAvg() - elemt.getMtdChangeO2PeriodLPct();
//		double min2ndC2lScore = benchmarkResult.getC2lStat().getMin2nd() - elemt.getMtdChangeO2PeriodLPct();			
		
		if(benchmarkResult.getRisesRatioC2C()<0.6)
			return false;
		
//		if(avgC2hScore > 0 && min2ndC2hScore>=0)
		if(avgC2hScore > 0 && benchmarkResult.getC2phStat().getAvg()>0 && benchmarkResult.getC2plStat().getAvg()<0) {
			if( (benchmarkResult.getC2phStat().getAvg() + benchmarkResult.getC2plStat().getAvg()) > 0){
				return true;
			}
		}
		
		return false;
		
			
	}
	
	public String toDescription() {
		return "月統計佔優";
	}
}
