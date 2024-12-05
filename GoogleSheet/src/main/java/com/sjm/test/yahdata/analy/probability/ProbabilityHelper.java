package com.sjm.test.yahdata.analy.probability;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.cfg.GlobalConfig;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.helper.AnalyGeneralHelper;
import com.sjm.test.yahdata.analy.helper.DisplayHelper;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.probability.bean.BenchmarkBean;
import com.sjm.test.yahdata.analy.probability.bean.MonthlyPerformance;
import com.sjm.test.yahdata.analy.repo.StockMarketRepo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProbabilityHelper {

	public static BenchmarkBean generateBenchmarkBean(String yyyy, String mm, List<StockBean> stocklist, StockBean referenceCalcStartingStockBean, boolean isCheck) {
				
		if(stocklist.size()==1) {

			BenchmarkBean bb = new BenchmarkBean(stocklist.get(0).getStockCode());
			bb.setYyyy(yyyy);
			bb.setMm(mm);
			bb.setStartTxnDate(stocklist.get(0).getTxnDate());
			bb.setEndTxnDate(stocklist.get(0).getTxnDate());
			return bb;
		}
		
		
		if(isCheck == false) {
			return null;
		}
		
		StockBean endRecord = stocklist.get(stocklist.size() - 1);

		double pctC2C = 0.0; // end to end
		double pctC2Lowest = 0.0;
		double pctC2Highest = 0.0;
		
		double pctO2C = 0.0;// end to end
		double pctO2Lowest = 0.0;
		double pctO2Highest = 0.0;
		
		double pctL2C = 0.0;// end to end
		double pctL2Lowest = 0.0;
		double pctL2Highest = 0.0;
		
		double pctH2C = 0.0;// end to end
		double pctH2Lowest = 0.0;
		double pctH2Highest = 0.0;
		
		double pctRange = 0.0; //Highest / Lowest – 1 OR (Highest - Lowest) / Lowest
		
		
		String startTxdate = stocklist.get(0).getTxnDate();
		
		if (referenceCalcStartingStockBean != null)
		{			
			List<StockBean> subStockList = stocklist.subList(1, stocklist.size()); //excluse the 1st element.
			
			startTxdate = referenceCalcStartingStockBean.getTxnDate();
			
			StockBean minByL = subStockList.stream()
				      .min(Comparator.comparing(StockBean::getL))
				      .orElseThrow(NoSuchElementException::new);
			StockBean maxByH = subStockList.stream()
				      .max(Comparator.comparing(StockBean::getH))
				      .orElseThrow(NoSuchElementException::new);
			double lowest = minByL.getL();
			double highest = maxByH.getH();
			
			pctC2C = ( endRecord.getC()-referenceCalcStartingStockBean.getC() )/referenceCalcStartingStockBean.getC();			
			pctC2Lowest = ( lowest-referenceCalcStartingStockBean.getC() )/referenceCalcStartingStockBean.getC();			
			pctC2Highest = ( highest-referenceCalcStartingStockBean.getC() )/referenceCalcStartingStockBean.getC();
			
			pctO2C = ( endRecord.getC()-referenceCalcStartingStockBean.getO() )/referenceCalcStartingStockBean.getO();
			pctO2Lowest = ( lowest-referenceCalcStartingStockBean.getO() )/referenceCalcStartingStockBean.getO();			
			pctO2Highest = ( highest-referenceCalcStartingStockBean.getO() )/referenceCalcStartingStockBean.getO();		
			
			pctL2C = ( endRecord.getC()-referenceCalcStartingStockBean.getL() )/referenceCalcStartingStockBean.getL();
			pctL2Lowest = ( lowest-referenceCalcStartingStockBean.getL() )/referenceCalcStartingStockBean.getL();			
			pctL2Highest = ( highest-referenceCalcStartingStockBean.getL() )/referenceCalcStartingStockBean.getL();	
			
			pctH2C = ( endRecord.getC()-referenceCalcStartingStockBean.getH() )/referenceCalcStartingStockBean.getH();
			pctH2Lowest = ( lowest-referenceCalcStartingStockBean.getH() )/referenceCalcStartingStockBean.getH();			
			pctH2Highest = ( highest-referenceCalcStartingStockBean.getH() )/referenceCalcStartingStockBean.getH();	
			
			pctRange = (Math.abs(pctC2Highest)>Math.abs(pctC2Lowest))?Math.abs(pctC2Highest):Math.abs(pctC2Lowest);

			if(GlobalConfig.REQUIRE_MINIMUM_C2C_RATIO!=null && GlobalConfig.REQUIRE_MINIMUM_C2C_RATIO < pctC2C)
				return null;
				
			
			
			BenchmarkBean mp = new BenchmarkBean(endRecord.getStockCode());

			mp.setYyyy(yyyy);
			mp.setMm(mm);
			mp.setStartTxnDate(startTxdate);
			mp.setEndTxnDate(endRecord.getTxnDate());
			
			mp.setPctC2C(pctC2C);
			mp.setPctC2Highest(pctC2Highest);
			mp.setPctC2Lowest(pctC2Lowest);
			
			mp.setPctO2C(pctO2C);
			mp.setPctO2Highest(pctO2Highest);
			mp.setPctO2Lowest(pctO2Lowest);

			mp.setPctL2C(pctL2C);
			mp.setPctL2Highest(pctL2Highest);
			mp.setPctL2Lowest(pctL2Lowest);
			
			mp.setPctH2C(pctH2C);
			mp.setPctH2Highest(pctH2Highest);
			mp.setPctH2Lowest(pctH2Lowest);
			
			mp.setPercentageRange(pctRange);
			
			mp.setPeriodLDate(minByL.getTxnDate());
			mp.setPeriodHDate(maxByH.getTxnDate());
			
			
			double pctEnd2EndLH = calcEnd2EndPct(referenceCalcStartingStockBean, endRecord, Const.END2END_L2H);
			double pctEnd2EndHL = calcEnd2EndPct(referenceCalcStartingStockBean, endRecord, Const.END2END_H2L);
//			
			mp.setPctEnd2EndHL(pctEnd2EndHL);
			mp.setPctEnd2EndLH(pctEnd2EndLH);

			return mp;
		}else {
			return null;
		}		
	}
	
	private static double calcEnd2EndPct(StockBean startRecord, StockBean endRecord, String end2EndPattern) {
		
				
		double percentage = 0.0;
		if(Const.END2END_C2C.equalsIgnoreCase(end2EndPattern)) {
			percentage = (endRecord.getC() - startRecord.getC())/ startRecord.getC();
		}else if(Const.END2END_C2L.equalsIgnoreCase(end2EndPattern)) {
			percentage = (endRecord.getL() - startRecord.getC())/ startRecord.getC();
		}else if(Const.END2END_C2H.equalsIgnoreCase(end2EndPattern)) {
			percentage = (endRecord.getH() - startRecord.getC())/ startRecord.getC();
		}else if(Const.END2END_L2H.equalsIgnoreCase(end2EndPattern)) {
			percentage = (endRecord.getH() - startRecord.getL())/ startRecord.getL();
		}else if(Const.END2END_H2L.equalsIgnoreCase(end2EndPattern)) {
			percentage = (endRecord.getL() - startRecord.getH())/ startRecord.getH();
		}else if(Const.END2END_O2O.equalsIgnoreCase(end2EndPattern)) {
			percentage = (endRecord.getO() - startRecord.getO())/ startRecord.getO();
		}else if(Const.END2END_O2C.equalsIgnoreCase(end2EndPattern)) {
			percentage = (endRecord.getC() - startRecord.getO())/ startRecord.getO();
//		}else if(Const.END2END_OC2C.equalsIgnoreCase(end2EndPattern)) {
//			double frCO = (startRecord.getC()>startRecord.getO())?startRecord.getC():startRecord.getO();
//			percentage = ( endRecord.getC()-frCO )/frCO;
//			
//			if(startRecord.getTxnDate().contains("11-20") && endRecord.getTxnDate().contains("11-25")) {
//				System.out.println("PAUSE");
//			}
		}else {
			log.warn("\tUnknow End-to-End pattern: "+end2EndPattern);
		}
		
		
		
		
		return percentage;
	}
	
	
//	public void func(String end2EndPattern) {
//		List<LHProbSummaryBean> descList = null;
//		if(Const.END2END_L2H.equalsIgnoreCase(end2EndPattern)) {
//			descList = restultLHProbSummaryBeanList.parallelStream()
//					.filter(x -> x.getNoOfL2hUpRatio() > minWinRate)
//					.sorted(l2hComparator)
//					.collect(Collectors.toList());
//		}else if(Const.END2END_C2L.equalsIgnoreCase(end2EndPattern)) {
//			descList = restultLHProbSummaryBeanList.parallelStream()
//					.filter(x -> x.getNoOfC2lUpRatio() > minWinRate)
//					.sorted(c2lComparator)
//					.collect(Collectors.toList());
//		}else if(Const.END2END_C2H.equalsIgnoreCase(end2EndPattern)) {
//			descList = restultLHProbSummaryBeanList.parallelStream()
//					.filter(x -> x.getNoOfC2lUpRatio() > minWinRate)
//					.sorted(c2hComparator)
//					.collect(Collectors.toList());
//		}else {
//			// Default is C2C
//			System.out.println("doing the C2C comparison");
//			descList = restultLHProbSummaryBeanList.parallelStream()
//					.filter(x -> x.getNoOfC2cUpRatio() > minWinRate)
//					.sorted(c2cComparator)
//					.collect(Collectors.toList());
//		}
//	}
	
	public void exportMonthProbability(List<MonthlyPerformance> ls) {
		if (ls == null || ls.isEmpty()) {
			log.info("NO MonthlyPerformance List to process");
			return;
		}
		DoubleSummaryStatistics steadyStats = ls.parallelStream().filter(x -> Const.STEADY.equalsIgnoreCase(x.getDirection()))
				.mapToDouble(x -> x.getPercentageClose()).summaryStatistics();
		DoubleSummaryStatistics upStats = ls.parallelStream().filter(x -> Const.UP.equalsIgnoreCase(x.getDirection()))
				.mapToDouble(x -> x.getPercentageClose()).summaryStatistics();
		DoubleSummaryStatistics downStats = ls.parallelStream().filter(x -> Const.DOWN.equalsIgnoreCase(x.getDirection()))
				.mapToDouble(x -> x.getPercentageClose()).summaryStatistics();

		MonthlyPerformance first = ls.get(0);
		MonthlyPerformance last = ls.get(ls.size() - 1);

		long count = upStats.getCount() + downStats.getCount() + steadyStats.getCount();
		List<String> downYearList = ls.parallelStream().filter(x -> Const.DOWN.equalsIgnoreCase(x.getDirection()))
				.map(MonthlyPerformance::getYyyy).collect(Collectors.toList());

		String txt = "\n Historical Month(" + DisplayHelper.toMonth(ls.get(0).getMm())
				+ ") UP/Down Probability(including GapUP/GapDown): " + ls.size() + " years data:(" + first.getYyyy()
				+ "-" + last.getYyyy() + ")";
		
		txt += "\n UP(" + upStats.getCount() + ") VS DOWN(" + downStats.getCount() + ") VS STEADY("
				+ steadyStats.getCount() + "), Up%: "
				+ GeneralHelper.toPct(((double) upStats.getCount() / (double) count));
		
		txt += "\t DOWN  " + downYearList;
		txt += "\n Month (Historical-UP): Avg = " + GeneralHelper.toPct(upStats.getAverage()) + ", Min = "
				+ GeneralHelper.toPct(upStats.getMin()) + ", Max = "
				+ GeneralHelper.toPct(upStats.getMax());
		txt += "\n Month (Historical-DOWN): Avg = " + GeneralHelper.toPct(downStats.getAverage()) + ", Min = "
				+ GeneralHelper.toPct(downStats.getMax()) + ", Max = "
				+ GeneralHelper.toPct(downStats.getMin());

		System.out.println(txt); 
		System.out.println("\tMonth\tStatus\tClose\t H \t L");
		for (MonthlyPerformance elemt : ls) {
			System.out.println("\t"+elemt.getYyyy()+"-"+elemt.getMm() +"\t"+ elemt.getDirection()+"\t"+GeneralHelper.toPct(elemt.getPercentageClose())+"\t"+GeneralHelper.toPct(elemt.getPercentageHigh())+"\t"+GeneralHelper.toPct(elemt.getPercentageLow()));

		}
	}
	
	
	public static StockBean getPrevMonthStockData(String stockCode, String yyyyMMdd) {

		String prevMonthyyyyMM = AnalyGeneralHelper.getPreviousYYYYMMFormat(yyyyMMdd);

		List<StockBean> yyyyMMList = StreamTransformHelper.extractMonthData(StockMarketRepo.getTrunk(stockCode),
				prevMonthyyyyMM);
		if (yyyyMMList == null || yyyyMMList.isEmpty())
			return null;
		StockBean last = yyyyMMList.get(yyyyMMList.size() - 1);

		return last;
	}

}
