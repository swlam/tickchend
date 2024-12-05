package com.sjm.test.yahdata.backtest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.maas.util.DateHelper;
import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.VolumePriceBean;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.cfg.BacktestConfig;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.helper.BenchmarkStatisticHelper;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.probability.ProbabilityHelper;
import com.sjm.test.yahdata.analy.probability.bean.BenchmarkBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BacktestModule {
	
//	private static final String END2END_PATTERN = Const.END2END_C2C;
	public BacktestModule() { }

	
	public BacktestBenchmarkBeanResult goThruBacktestBenchmark(List<StockBean> trunkList, List<VolumePriceBean> vpStockList, CandleEventTagEnum targetPattern) {
		
		
		Set<BacktestBenchmarkBeanResult> benchMarkResultSet = new HashSet<BacktestBenchmarkBeanResult>();
		for(int days=BacktestConfig.FEW_DAY_START; days<=BacktestConfig.FEW_DAY_END; days++ ) {
			BacktestBenchmarkBeanResult result = this.performInPeriodHighLowResult(trunkList, vpStockList, targetPattern, days );
			if(result !=null)
				benchMarkResultSet.add(result);
		}
			
		
		BacktestBenchmarkBeanResult bestResult = null;
		if(benchMarkResultSet.isEmpty() == false) {
//			bestResult = benchMarkResultSet.stream().max(Comparator.comparing(BacktestBenchmarkBeanResult::getUpRatio))
//					.orElseThrow(NoSuchElementException::new);
			
			Comparator<BacktestBenchmarkBeanResult> sortByUpRatioDesc = Comparator.comparing(BacktestBenchmarkBeanResult::getUpRatio).reversed();
			Comparator<BacktestBenchmarkBeanResult> sortByDaysAsc = Comparator.comparing(BacktestBenchmarkBeanResult::getDays);
			
			Comparator<BacktestBenchmarkBeanResult>  comparrator = sortByUpRatioDesc.thenComparing(sortByDaysAsc);
			bestResult = benchMarkResultSet.parallelStream().sorted(comparrator).findFirst().get();
		}
		
		
		
		return bestResult;
		
	}
	
	public void performOccurence(List<StockBean> trunkList, List<VolumePriceBean> vpList) {
//		for (VolumePriceBean currCandleStockBean : vpList) {
//			int idx = currCandleStockBean.getChainIdx();
//			if(currCandleStockBean.isOccurenceEvent()==true) {
//				boolean isFoundPattern = currCandleStockBean.getSignSet().contains(targetPattern);	
//			}
//			
//		}
		
		long count = vpList.parallelStream().filter(x -> x.isOccurenceEvent()==true ).collect(Collectors.counting());
		double ratio = (double)count/(double)trunkList.size();
		log.info("Occuence: hit:"+count + " of "+trunkList.size() + ", Ratio: "+GeneralHelper.toPct(ratio));
	}
	
	private BacktestBenchmarkBeanResult performInPeriodHighLowResult(List<StockBean> trunkList, List<VolumePriceBean> vpList, CandleEventTagEnum targetPattern, Integer nextFewTxDays) {
		if(vpList==null || vpList.isEmpty()==true)
			return null;
		
		List<BenchmarkBean> backtestResultList = new ArrayList<BenchmarkBean>(); // base on the input VpList size
//		int nextFewDaysResult = -1;
		for (VolumePriceBean currCandleStockBean : vpList) {
			int idx = currCandleStockBean.getChainIdx();
			
			boolean isFoundPattern = currCandleStockBean.getSignSet().contains(targetPattern);
			
			if(isFoundPattern) {

				try {
					int nextIdx = idx + nextFewTxDays;
					if(nextIdx > trunkList.size()-1) {
						nextIdx = trunkList.size()-1;
//						nextFewDaysResult = -1;
					}
			
					StockBean periodEndStock = trunkList.get(nextIdx);
					
					String fromTxDatePattern = currCandleStockBean.getTxnDate();
					String toTxDatePattern = periodEndStock.getTxnDate();
												
					List<StockBean> stockPeriodList = StreamTransformHelper.extractData(trunkList, fromTxDatePattern, toTxDatePattern);
					if(stockPeriodList==null || stockPeriodList.isEmpty())
						continue;
					
					boolean isCheck = true;

					if(stockPeriodList.size()<nextFewTxDays)
						isCheck = false;
					
					BenchmarkBean benchmarkBean = ProbabilityHelper.generateBenchmarkBean(stockPeriodList.get(0).getTxnDate().substring(0,4)+"", "", stockPeriodList, stockPeriodList.get(0), isCheck);
					if(benchmarkBean!=null)
						backtestResultList.add(benchmarkBean);
				}catch(Exception e) {
					e.printStackTrace();
				}
				
			}
		}// end loop
		
	
		long countAll = backtestResultList.parallelStream().collect(Collectors.counting());
		
		long countUp = backtestResultList.parallelStream()
				.filter(x -> x.getPctC2C()>0.0 )
				.collect(Collectors.counting());
		long countDown = backtestResultList.parallelStream()
				.filter(x -> x.getPctC2C()<0.0 )
				.collect(Collectors.counting());

		
		BacktestBenchmarkBeanResult resultBean = new BacktestBenchmarkBeanResult(vpList.get(0).getStockCode());
		resultBean.setCntAll((int)countAll);
		resultBean.setCntUp((int)countUp);
		resultBean.setCntDown((int)countDown);
		resultBean.setStrategyPattern(targetPattern.getDescription());
		
		if(countAll!=0)
			resultBean.setUpRatio((double)countUp/(double)countAll);
		
		if(resultBean.getUpRatio()==0.0)
			resultBean.setDays(-1);
		else
			resultBean.setDays(nextFewTxDays);
		
		
		
//		resultBean.setEnd2EndStat(BenchmarkStatisticHelper.genearteEnd2EndLHStat(backtestResultList));
		resultBean.setC2cStat(BenchmarkStatisticHelper.genearteC2CStat(backtestResultList));
		resultBean.setC2plStat(BenchmarkStatisticHelper.genearteC2PLStat(backtestResultList));
		resultBean.setC2phStat(BenchmarkStatisticHelper.genearteC2PHStat(backtestResultList));
		resultBean.setPercentageRangeStat(BenchmarkStatisticHelper.geneartePercentageRangeStat(backtestResultList));
		if(backtestResultList==null || backtestResultList.isEmpty()) {
			resultBean.setLastTxnDate("N/A");
		}else {
			resultBean.setLastTxnDate(backtestResultList.get(backtestResultList.size()-1).getStartTxnDate());	
		}
		
		
		//calc numof years
		String initDateStr = trunkList.get(0).getTxnDate();
		String latestDateStr = trunkList.get(trunkList.size()-1).getTxnDate();
		
		LocalDate initDate = LocalDate.parse(initDateStr, DateHelper.formatteryyyy_MM_dd);
		LocalDate latestDate = LocalDate.parse(latestDateStr, DateHelper.formatteryyyy_MM_dd);
		int numOfYears = DateHelper.yearBetween(initDate, latestDate);
		resultBean.setNumOfYears(numOfYears);
		resultBean.setNoOfTxDays(trunkList.size());
		
		return resultBean;
	}


}
