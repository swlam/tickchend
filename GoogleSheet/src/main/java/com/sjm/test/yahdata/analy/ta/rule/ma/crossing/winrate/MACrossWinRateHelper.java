package com.sjm.test.yahdata.analy.ta.rule.ma.crossing.winrate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.maas.util.DateHelper;
import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.helper.AnalyGeneralHelper;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.backtest.PeriodHighLowResultBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MACrossWinRateHelper {
	
	private static RuleMACrossingHandler ruleHandler = null;
	static {
		ruleHandler = new RuleMACrossingHandler();
	}
	
	public static final List<Integer> MA_LIST = Arrays.asList(2,3,5,6,7,8,9,10,11,12,13,15,18,19,20,25,30,35,50,100,200,250);

	
	public MACrossWinRateHelper() {}

	public static MACrossWinRateResultSummary toMACrossWinRateResultSummary(List<StockBean> trunkList, List<MaTagStockBean> maTagStockList, String targetPattern, boolean isRequiredFixEndDate, int nextFewDays){

		List<PeriodHighLowResultBean> backtestResultList = new ArrayList<PeriodHighLowResultBean>();
		
		String lastHappenedDate = "";
		
		for (MaTagStockBean currCandleStockBean : maTagStockList) 
		{
			int idx = currCandleStockBean.getChainIdx();
			
			boolean isFoundPattern = currCandleStockBean.getTagSet().contains(targetPattern);
			
			if(isFoundPattern) {

				int nextIdx = idx + nextFewDays;
				
				if(nextIdx > trunkList.size()-1 ) {
					lastHappenedDate = currCandleStockBean.getTxnDate();
					
				}else {
				
					PeriodHighLowResultBean bResult = new PeriodHighLowResultBean();
					
//					StockBean futureBbean = trunkList.get(nextIdx);
					
					StockBean targetDateStockBean =  trunkList.get(nextIdx);
					bResult.setCurrentStockBean(currCandleStockBean);
					bResult.setPeriodEndStockBean(targetDateStockBean);
					
					String fromDatePattern = currCandleStockBean.getTxnDate();
					String toDatePattern = trunkList.get(nextIdx).getTxnDate();
							
					
//					List<StockBean> stockPeriodMonthList = StreamTransformHelper.extractData(trunkList, yyyy+"-"+startMMdd, yyyy+"-"+endMMdd);
					List<StockBean> stockPeriodList = StreamTransformHelper.extractData(trunkList, fromDatePattern, toDatePattern);
					
					StockBean maxDataPeriod = stockPeriodList.parallelStream().max(Comparator.comparingDouble(StockBean::getH)).get();
					StockBean minDataPeriod = stockPeriodList.parallelStream().min(Comparator.comparingDouble(StockBean::getL)).get();
					
					StockBean maxCloseDataPeriod = stockPeriodList.parallelStream().max(Comparator.comparingDouble(StockBean::getC)).get();
					StockBean minCloseDataPeriod = stockPeriodList.parallelStream().min(Comparator.comparingDouble(StockBean::getC)).get();
					
					bResult.setNextHighCloseStockBean(maxCloseDataPeriod);
					bResult.setNextHStockBean(maxDataPeriod);
					
					bResult.setNextLowCloseStockBean(minCloseDataPeriod);
					bResult.setNextLStockBean(minDataPeriod);
					
					
					double percentageC2H = (maxDataPeriod.getH() - bResult.getCurrentStockBean().getC())/bResult.getCurrentStockBean().getC();
					double percentageC2L = (minDataPeriod.getL() - bResult.getCurrentStockBean().getC())/bResult.getCurrentStockBean().getC();
					double percentageC2HC = (maxCloseDataPeriod.getC() - bResult.getCurrentStockBean().getC())/bResult.getCurrentStockBean().getC();
					double percentageC2LC = (minCloseDataPeriod.getC() - bResult.getCurrentStockBean().getC())/bResult.getCurrentStockBean().getC();
					double percentagePeriodEndC2C = (targetDateStockBean.getC() - bResult.getCurrentStockBean().getC())/bResult.getCurrentStockBean().getC();
					
					bResult.setPctC2Highest(percentageC2H);
					bResult.setPercentageC2HC(percentageC2HC);
					bResult.setPctC2Lowest(percentageC2L);
					bResult.setPercentageC2LC(percentageC2LC);
					bResult.setPctC2C(percentagePeriodEndC2C); //bResult.setPercentagePeriodEndC2C(percentagePeriodEndC2C);
					
					int dNextHigh = DateHelper.dayBetween(currCandleStockBean.getTxnDate(), bResult.getNextHStockBean().getTxnDate());
					int dNextHighC = DateHelper.dayBetween(currCandleStockBean.getTxnDate(), bResult.getNextHighCloseStockBean().getTxnDate());
					int dNextLow = DateHelper.dayBetween(currCandleStockBean.getTxnDate(), bResult.getNextLStockBean().getTxnDate());
					int dNextLowC = DateHelper.dayBetween(currCandleStockBean.getTxnDate(), bResult.getNextLowCloseStockBean().getTxnDate());
					
					bResult.setNumOfDaysUntilNextH(dNextHigh);
					bResult.setNumOfDaysUntilNextHClose(dNextHighC);
					bResult.setNumOfDaysUntilNextL(dNextLow);
					bResult.setNumOfDaysUntilNextLClose(dNextLowC);

					
					if(isRequiredFixEndDate) {
						if(targetDateStockBean.getC() > currCandleStockBean.getC()) 
						{
							bResult.setMatch(true);
						}else {
							bResult.setMatch(false);					
						}
						
					}else {
						if(maxDataPeriod.getC() > currCandleStockBean.getC()) 
						{
							bResult.setMatch(true);
						}else {
							bResult.setMatch(false);					
						}
					}
														
					
					backtestResultList.add(bResult);
					lastHappenedDate = 	bResult.getCurrentStockBean().getTxnDate();
				}
								
				
			}
		}// end loop
		
	
		long countAll = backtestResultList.parallelStream().collect(Collectors.counting());
		long countMatch = backtestResultList.parallelStream().filter(x -> x.isMatch()==true).collect(Collectors.counting());
		
		
		
		List<PeriodHighLowResultBean> hitList = backtestResultList.parallelStream().filter(x -> x.isMatch()==true).collect(Collectors.toList());
		List<PeriodHighLowResultBean> missList = backtestResultList.parallelStream().filter(x -> x.isMatch()==false).collect(Collectors.toList());
		
		String lastHitDate  = "";
		String lastMissDate  = "";
		if(hitList.size()>0) {
			lastHitDate = hitList.get(hitList.size()-1).getCurrentStockBean().getTxnDate();
		}
		
		if(missList.size()>0) {
			lastMissDate = missList.get(missList.size()-1).getCurrentStockBean().getTxnDate();
		}
		
		double hitRatio = (double)countMatch / (double)countAll;
		
		String stockCode = trunkList.get(0).getStockCode();
		MACrossWinRateResultSummary sumy = new MACrossWinRateResultSummary(stockCode, targetPattern, nextFewDays);
		sumy.setLastHappenedDate(lastHappenedDate);
		sumy.setLastHitDate(lastHitDate);
		sumy.setLastMissDate(lastMissDate);
		sumy.setUpCount(countMatch);
		sumy.setHitRatio(hitRatio);
		sumy.setTotal(countAll);
		sumy.setTag(targetPattern);
		
		WinRateResultDetail hitResult = toWinRateResultDetail(hitList, trunkList.get(0).getStockCode(), targetPattern);
		WinRateResultDetail missResult = toWinRateResultDetail(missList, trunkList.get(0).getStockCode(), targetPattern);
		
		sumy.setHitResult(hitResult);
		sumy.setMissResult(missResult);
		
		return sumy;
	}
	
	protected static WinRateResultDetail toWinRateResultDetail(List<PeriodHighLowResultBean> hitList, String stockCode, String maTag) {
		
		double avgPercentageC2H = hitList.parallelStream()
				.mapToDouble(PeriodHighLowResultBean::getPctC2Highest)
				.average()
				.orElse(Double.NaN);

		double avgPercentageC2HC = hitList.parallelStream()
				.mapToDouble(PeriodHighLowResultBean::getPercentageC2HC)
				.average()
				.orElse(Double.NaN);
		
		double avgPercentageC2L = hitList.parallelStream()
				.mapToDouble(PeriodHighLowResultBean::getPctC2Lowest)
				.average()
				.orElse(Double.NaN);
		double avgPercentageC2LC = hitList.parallelStream()
				.mapToDouble(PeriodHighLowResultBean::getPercentageC2LC)
				.average()
				.orElse(Double.NaN);
		
		double avgDaysC2H = hitList.parallelStream()
				.mapToDouble(PeriodHighLowResultBean::getNumOfDaysUntilNextH)
				.average()
				.orElse(Double.NaN);
		double avgDaysC2HC = hitList.parallelStream()
				.mapToDouble(PeriodHighLowResultBean::getNumOfDaysUntilNextHClose)
				.average()
				.orElse(Double.NaN);
		
		double avgDaysC2L = hitList.parallelStream()
				.mapToDouble(PeriodHighLowResultBean::getNumOfDaysUntilNextL)
				.average()
				.orElse(Double.NaN);
		double avgDaysC2LC = hitList.parallelStream()
				.mapToDouble(PeriodHighLowResultBean::getNumOfDaysUntilNextLClose)
				.average()
				.orElse(Double.NaN);
		
		
		double avgPercentagePeriodEndC2C = hitList.parallelStream()
				.mapToDouble(PeriodHighLowResultBean::getPctC2C)
				.average()
				.orElse(Double.NaN);
		
		
		WinRateResultDetail backtestResultBean = new WinRateResultDetail();
		
		List<Double> targetList = hitList.parallelStream().map(PeriodHighLowResultBean::getPctC2Highest).collect(Collectors.toList());
		Double medianC2Highest = AnalyGeneralHelper.getMedian(targetList);
		
		targetList = hitList.parallelStream().map(PeriodHighLowResultBean::getPctC2Lowest).collect(Collectors.toList());
		Double medianC2Lowest = AnalyGeneralHelper.getMedian(targetList);
		
		targetList = hitList.parallelStream().map(PeriodHighLowResultBean::getPctC2C).collect(Collectors.toList());
		Double medianC2C = AnalyGeneralHelper.getMedian(targetList);
		
		backtestResultBean.setAvgDaysC2H(avgDaysC2H);
		backtestResultBean.setAvgDaysC2HC(avgDaysC2HC);
		backtestResultBean.setAvgDaysC2L(avgDaysC2L);
		backtestResultBean.setAvgDaysC2LC(avgDaysC2LC);
		
		backtestResultBean.setAvgPercentageC2H(avgPercentageC2H);
		backtestResultBean.setAvgPercentageC2HC(avgPercentageC2HC);
		backtestResultBean.setAvgPercentageC2L(avgPercentageC2L);
		backtestResultBean.setAvgPercentageC2LC(avgPercentageC2LC);
		backtestResultBean.setAvgPercentagePeriodEndC2C(avgPercentagePeriodEndC2C);
		
		backtestResultBean.setMedianC2Highest(medianC2Highest);
		backtestResultBean.setMedianC2Lowest(medianC2Lowest);
		backtestResultBean.setMedianC2C(medianC2C);
		
		return backtestResultBean;
	}
	
	public void printDetail(List<PeriodHighLowResultBean> orgLst, int subListLen,String title) {
		List<PeriodHighLowResultBean> lst = new ArrayList<PeriodHighLowResultBean>();
		
		if(orgLst.size()>subListLen) {
		
			int fromIndex = orgLst.size() - subListLen;
			int toIndex = orgLst.size();
			
			lst = orgLst.subList(fromIndex, toIndex);
		}else {
			lst.addAll(orgLst);
		}
		
		
		StringBuffer sb = new StringBuffer();
		for (PeriodHighLowResultBean elem : lst) {
			
			List<String> signNames = ((MaTagStockBean)elem.getCurrentStockBean()).getTagSet().parallelStream()
		              .collect(Collectors.toList());
			
			sb.append("\n"+elem.getCurrentStockBean().getTxnDate() 
					+ ", C2H: "+ GeneralHelper.toPct(elem.getPctC2Highest())
					+ ", Days: " +elem.getNumOfDaysUntilNextH()
					
					+ ", C2HC: "+ GeneralHelper.toPct(elem.getPercentageC2HC())
					+ ", Days: " +elem.getNumOfDaysUntilNextHClose()
					
					+ ", C2L: "+ GeneralHelper.toPct(elem.getPctC2Lowest())
					+ ", Days: " +elem.getNumOfDaysUntilNextL()
					
					+ ", C2LC: "+ GeneralHelper.toPct(elem.getPercentageC2LC())
					+ ", Days: " +elem.getNumOfDaysUntilNextLClose()
					
					+ " " + signNames);
		}
		log.info(sb.toString());
		log.info("\n Above is (Last "+subListLen+") "+title);
	}
	
	
	public static List<MACrossUpForWinRateRule>  generateMACrossUpRules(List<Integer> MA_LIST) {

		List<MACrossUpForWinRateRule> maCrossUpList = new ArrayList<MACrossUpForWinRateRule>(10);
		
		int cnt = 0;
		for(int i=0; i< MA_LIST.size(); i++) {
			for(int j=i; j< MA_LIST.size(); j++) {
				
				if(i==j)
					continue;
				
				int fasterD = MA_LIST.get(i);
				int slowerD = MA_LIST.get(j);
				
//				if(fasterD >= (slowerD-3))
//					continue;			
				if(Math.abs(Math.abs(fasterD) - Math.abs(slowerD))<3)
					continue;
				
				cnt++;				
				
				MACrossUpForWinRateRule maCrossUP = new MACrossUpForWinRateRule(fasterD, slowerD);
				maCrossUpList.add(maCrossUP);
			}
		}
		
		log.info("MA_LIST Number of Combination: "+cnt);
		
		return maCrossUpList;
	}
	
	
	public static RuleMACrossingHandler getRuleHandler() {
		if(ruleHandler == null)
			ruleHandler = new RuleMACrossingHandler();
		
		return ruleHandler;
	}
	
	
}
