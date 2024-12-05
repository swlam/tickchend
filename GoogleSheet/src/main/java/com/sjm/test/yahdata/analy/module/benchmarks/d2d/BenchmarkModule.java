package com.sjm.test.yahdata.analy.module.benchmarks.d2d;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.maas.util.DateHelper;
import com.sjm.test.yahdata.analy.bean.RisesFallsRatio;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.cfg.GlobalConfig;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.helper.BenchmarkStatisticHelper;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.probability.ProbabilityHelper;
import com.sjm.test.yahdata.analy.probability.bean.BenchmarkBean;
import com.sjm.test.yahdata.analy.probability.bean.BenchmarkBeanResult;
import com.sjm.test.yahdata.analy.ta.helper.SectorAnalystHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BenchmarkModule {
	

	public BenchmarkModule() {}

	public BenchmarkBean getRecentBenchmarkBean(List<StockBean > stockList, String startMMDD, String endMMDD) throws Exception, Exception {

		LocalDate now = LocalDate.now();
		
		List<StockBean> subList = StreamTransformHelper.extractData(stockList, now.getYear() + "-" + startMMDD, now.getYear() + "-" + endMMDD);
		if(subList.isEmpty())
			return null;
		
		BenchmarkBean benchmarkBean = ProbabilityHelper.generateBenchmarkBean(now.getYear()+"", "", subList, subList.get(0), true);
		
		if(benchmarkBean==null)
			return null;
//		BenchmarkBean benchmarkBean = new BenchmarkBean(stockList.get(0).getStockCode());
//		BeanUtils.copyProperties(benchmarkBean, mpe);
		
		benchmarkBean.setStartMMdd(startMMDD);
		benchmarkBean.setEndMMdd(endMMDD);
		return benchmarkBean;
		
	}
	
	public List<BenchmarkBean> doDataRangeLogic(List<StockBean> allSameMonthDataList, String startMMDD, String endMMDD
			) throws Exception, Exception {

		String firstTxnDate = allSameMonthDataList.get(0).getTxnDate();
		String lastTxnDate = allSameMonthDataList.get(allSameMonthDataList.size() - 1).getTxnDate();

		LocalDate localDate = LocalDate.parse(firstTxnDate);

		LocalDate lastTxLocalDate = LocalDate.parse(lastTxnDate);

		int lastYYYY = lastTxLocalDate.getYear();
		if (lastTxLocalDate.getDayOfMonth() <  GlobalConfig.LAST_DATE_OF_MONTH) {
			lastYYYY = lastYYYY - 1;
		}

		int yyyy = localDate.getYear();

		List<BenchmarkBean> rtnBenchmarkList = new ArrayList<BenchmarkBean>();

		do {

			LocalDate startDate = LocalDate.parse(yyyy + "-" + startMMDD);
			LocalDate endDate = LocalDate.parse(yyyy + "-" + endMMDD);
			List<String> dateStrList = new ArrayList<String>();

			// build calendar date range matching
			for (LocalDate date = startDate; date.isBefore(endDate) || date.isEqual(endDate); date = date.plusDays(1)) {
				dateStrList.add(date.toString());
			}
			
			List<StockBean> subList = StreamTransformHelper.extractData(allSameMonthDataList, dateStrList.get(0), dateStrList.get(dateStrList.size()-1));
			if(subList.isEmpty() ==false) {
				//IMPORTANT HERE
				BenchmarkBean benchmarkBean = ProbabilityHelper.generateBenchmarkBean(yyyy+"", "", subList, subList.get(0), true);
				if(benchmarkBean!=null) {
					
					benchmarkBean.setStartMMdd(startMMDD);
					benchmarkBean.setEndMMdd(endMMDD);
		
					rtnBenchmarkList.add(benchmarkBean);
				}
			}
			
			yyyy++;
		} while (yyyy <= lastYYYY);

		return rtnBenchmarkList;
	}
	
	public BenchmarkBeanResult generateBenchmarkResult(List<BenchmarkBean> benchmarkBeanlist, BenchmarkBean recentMonthlyPerfromance, double minimumRatio, int requireMinNoOfDays, String end2EndPattern) {
		if(benchmarkBeanlist==null || benchmarkBeanlist.isEmpty())
			return null;
		
		BenchmarkBean first = (BenchmarkBean)benchmarkBeanlist.get(0);
		BenchmarkBean last = (BenchmarkBean)benchmarkBeanlist.get(benchmarkBeanlist.size()-1);
		String yearHeading = "("+first.getYyyy()+ " - "+ last.getYyyy()+") ";
		
		
		int periodNoOfDays = 0; 
		if(first.getStartMMdd()!=null && first.getEndMMdd()!=null) {
			LocalDate begin = LocalDate.parse(first.getYyyy() + "-"+first.getStartMMdd());
			LocalDate end = LocalDate.parse(first.getYyyy() + "-"+first.getEndMMdd());
			periodNoOfDays = DateHelper.dayBetween(begin, end);
			
			if(requireMinNoOfDays>=0 && (periodNoOfDays <  requireMinNoOfDays || requireMinNoOfDays<3 ))
				return null;
		}
				
		RisesFallsRatio e2eL2HRiseFallRatio = BenchmarkStatisticHelper.getRisesFallsRatioResult(benchmarkBeanlist, Const.END2END_L2H);
		RisesFallsRatio e2eH2LRiseFallRatio = BenchmarkStatisticHelper.getRisesFallsRatioResult(benchmarkBeanlist, Const.END2END_H2L);
		RisesFallsRatio e2eC2CRiseFallRatio = BenchmarkStatisticHelper.getRisesFallsRatioResult(benchmarkBeanlist, Const.END2END_C2C);
		RisesFallsRatio e2eO2CRiseFallRatio = BenchmarkStatisticHelper.getRisesFallsRatioResult(benchmarkBeanlist, Const.END2END_O2C);		
		RisesFallsRatio c2phRiseFallRatio = BenchmarkStatisticHelper.getRisesFallsRatioResult(benchmarkBeanlist, Const.C2PH);
		RisesFallsRatio c2plRiseFallRatio = BenchmarkStatisticHelper.getRisesFallsRatioResult(benchmarkBeanlist, Const.C2PL);
		RisesFallsRatio o2phRiseFallRatio = BenchmarkStatisticHelper.getRisesFallsRatioResult(benchmarkBeanlist, Const.O2PH);
		RisesFallsRatio o2plRiseFallRatio = BenchmarkStatisticHelper.getRisesFallsRatioResult(benchmarkBeanlist, Const.O2PL);
		
		if( this.isMeetMinimumRatio(e2eC2CRiseFallRatio,e2eL2HRiseFallRatio, e2eH2LRiseFallRatio, minimumRatio, end2EndPattern) == false) {
			return null;
		}
		
		//The proportion of positive returns.
//		long c2phUpCnt = benchmarkBeanlist.parallelStream().filter(x -> x.getPctC2Highest()>0.0).count();	
//		double c2phPositiveRatio = (double) c2phUpCnt / (double) benchmarkBeanlist.size();		

		//The proportion of negative returns.
//		long c2plDownCnt = benchmarkBeanlist.parallelStream().filter(x -> x.getPctC2Lowest()<0.0).count();	
//		double c2plNegativeRatio = (double) c2plDownCnt / (double) benchmarkBeanlist.size();
		
		
//		long o2phUpCnt = benchmarkBeanlist.parallelStream().filter(x -> x.getPctO2Highest()>0.0).count();	
//		double o2phPositiveRatio = (double) o2phUpCnt / (double) benchmarkBeanlist.size();		

		//The proportion of negative returns.
//		long o2plDownCnt = benchmarkBeanlist.parallelStream().filter(x -> x.getPctO2Lowest()<0.0).count();	
//		double o2plNegativeRatio = (double) o2plDownCnt / (double) benchmarkBeanlist.size();
		
		
		List<String> downYearList = benchmarkBeanlist.parallelStream().filter(x -> x.getPctO2C()<0.0).map(BenchmarkBean::getYyyy).collect(Collectors.toList());

		
		List<BenchmarkBean> positiveReturnList = benchmarkBeanlist.parallelStream().filter(x-> x.getPctO2C()>0.0).collect(Collectors.toList());
		long cntNegativeReturn = positiveReturnList.parallelStream().filter(x-> x.getPctO2Lowest()<0).count();
		double negativeReturnRatio = (double) cntNegativeReturn / (double) positiveReturnList.size();
		
		
		List<String> nonNegativeRatioDates = benchmarkBeanlist.parallelStream().filter(x-> x.getPctO2Lowest()>=0).map(BenchmarkBean::getPeriodLDate).collect(Collectors.toList());
		
		
		//The starting date is the lowest price date 
//		List<String> startDateIsLowestPriceDayList = benchmarkBeanlist.parallelStream().filter(x-> x.getPeriodLDate().equalsIgnoreCase(x.getStartMMdd())).map(BenchmarkBean::getPeriodLDate).collect(Collectors.toList());
		
		
		
		BenchmarkBeanResult result = new BenchmarkBeanResult(first.getStockCode());
		
		
		List<String> belongEtfs = SectorAnalystHelper.getETFBelongsTo(first.getStockCode());
		if(belongEtfs.isEmpty())
			result.setBelongETF(Const.NA);
		else
			result.setBelongETF(belongEtfs.toString().substring(1,belongEtfs.toString().length()-1));
		
		
//		result.setEnd2EndOCCStat(BenchmarkStatisticHelper.genearteEnd2EndOCCStat(benchmarkBeanlist));
		result.setEnd2EndLHStat(BenchmarkStatisticHelper.genearteEnd2EndLHStat(benchmarkBeanlist));
		result.setEnd2EndHLStat(BenchmarkStatisticHelper.genearteEnd2EndHLStat(benchmarkBeanlist));
		
		result.setC2cStat(BenchmarkStatisticHelper.genearteC2CStat(benchmarkBeanlist));		
		result.setC2phStat(BenchmarkStatisticHelper.genearteC2PHStat(benchmarkBeanlist));
		result.setC2plStat(BenchmarkStatisticHelper.genearteC2PLStat(benchmarkBeanlist));
		
		
		result.setO2cStat(BenchmarkStatisticHelper.genearteO2CStat(benchmarkBeanlist));
		result.setO2phStat(BenchmarkStatisticHelper.genearteO2PHStat(benchmarkBeanlist));
		result.setO2plStat(BenchmarkStatisticHelper.genearteO2PLStat(benchmarkBeanlist));
		
		result.setPercentageRangeStat(BenchmarkStatisticHelper.geneartePercentageRangeStat(benchmarkBeanlist));
		
		result.setNumOfYears(benchmarkBeanlist.size());
		result.setStockCode(first.getStockCode());
		
		result.setRisesRatioC2C(e2eC2CRiseFallRatio.getRisesRatio());
		result.setFallsRatioC2C(e2eC2CRiseFallRatio.getFallsRatio());
		
		result.setRisesRatioO2C(e2eO2CRiseFallRatio.getRisesRatio());
		result.setFallsRatioO2C(e2eO2CRiseFallRatio.getFallsRatio());
		
		result.setRisesRatioEnd2EndH2L(e2eH2LRiseFallRatio.getRisesRatio());
		result.setFallsRatioEnd2EndH2L(e2eH2LRiseFallRatio.getFallsRatio());
		
		result.setRisesRatioEnd2EndL2H(e2eL2HRiseFallRatio.getRisesRatio());
		result.setFallsRatioEnd2EndL2H(e2eL2HRiseFallRatio.getFallsRatio());
		

		result.setRisesRatioC2ph(c2phRiseFallRatio.getRisesRatio());
		result.setFallsRatioC2pl(c2plRiseFallRatio.getFallsRatio());
		
		result.setRisesRatioO2ph(o2phRiseFallRatio.getRisesRatio());
		result.setFallsRatioO2pl(o2plRiseFallRatio.getFallsRatio());
		
		result.setO2cDownYearList(downYearList);
//		result.setToLowestNegativeRatio(c2LNegativeRatio);
		result.setFallBackToNegativeReturnRatio(negativeReturnRatio);
		result.setNonNegativeRatioDates(nonNegativeRatioDates);
//		result.setStartDateIsLowestPriceDayList(startDateIsLowestPriceDayList);
		result.setYearHeading(yearHeading);
		result.setStartMMdd(first.getStartMMdd());
		result.setEndMMdd(first.getEndMMdd());
		result.setPeriodNoOfDays(periodNoOfDays);
		
		if(recentMonthlyPerfromance!=null && recentMonthlyPerfromance.getMm().equals(benchmarkBeanlist.get(0).getMm())) {
			result.setCurrentBenchmarkBean((BenchmarkBean)recentMonthlyPerfromance);
		}
		result.setRecentBenchmarkBean((BenchmarkBean)benchmarkBeanlist.get(benchmarkBeanlist.size()-1));

		
		return result;
		
	}
	
	public boolean isMeetMinimumRatio(RisesFallsRatio e2eC2CRatio, RisesFallsRatio e2eL2HRatio, RisesFallsRatio e2eH2LRatio, Double requireWinRatio, String end2EndPattern) {
		
		boolean isMeetRequirement = false;
		
		if(Const.STRATEGY_FILTER_UPSIDE.equalsIgnoreCase(GlobalConfig.STRATEGRY)) {
			if( e2eH2LRatio.getFallsRatio()<=0.25 && e2eC2CRatio.getRisesRatio() >= requireWinRatio )
//				&& (e2eC2CRatio.getUpRatio()/ e2eH2LRatio.getDownRatio()>=2 && e2eL2HRatio.getUpRatio() / e2eH2LRatio.getDownRatio()>=2) ) 
			{
				isMeetRequirement = true;
			}else {
				isMeetRequirement = false;
			}
		}else if(Const.STRATEGY_FILTER_DOWNSIDE.equalsIgnoreCase(GlobalConfig.STRATEGRY)) {
			if( e2eH2LRatio.getFallsRatio()>=requireWinRatio && e2eC2CRatio.getRisesRatio()< 0.5){
//				&& (e2eH2LRatio.getDownRatio()/e2eC2CRatio.getUpRatio()>=2 && e2eH2LRatio.getDownRatio()/e2eL2HRatio.getUpRatio()>=2) ) {
				isMeetRequirement = true;
			}else {
				isMeetRequirement = false;
			}
		}else {
			//GlobalConfig.STRATEGRY is NONE or NULL
			if(Const.END2END_C2C.equals(end2EndPattern)) {
				if(e2eC2CRatio.getRisesRatio()>=requireWinRatio || e2eC2CRatio.getFallsRatio()>=requireWinRatio)
					isMeetRequirement = true;//return true;
			}else if(Const.END2END_L2H.equals(end2EndPattern)) {
				if(e2eL2HRatio.getRisesRatio()>=requireWinRatio || e2eL2HRatio.getFallsRatio()>=requireWinRatio)
					isMeetRequirement = true;//return true;
			}else if(Const.END2END_H2L.equals(end2EndPattern)) {
				if(e2eH2LRatio.getRisesRatio()>=requireWinRatio || e2eH2LRatio.getFallsRatio()>=requireWinRatio)
					isMeetRequirement = true;//return true;
				
			}else {
				if(e2eC2CRatio.getRisesRatio()>=requireWinRatio || e2eC2CRatio.getFallsRatio()>=requireWinRatio
				|| e2eL2HRatio.getRisesRatio()>=requireWinRatio || e2eL2HRatio.getFallsRatio()>=requireWinRatio
				|| e2eH2LRatio.getRisesRatio()>=requireWinRatio || e2eH2LRatio.getFallsRatio()>=requireWinRatio
				)
					isMeetRequirement = true;//return true;
			}
			
		}
		
		return isMeetRequirement ;
		
	}

}

