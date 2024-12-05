package com.sjm.test.yahdata.analy.probability;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.VolatilityBean;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.helper.DisplayHelper;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.probability.bean.BenchmarkBean;
import com.sjm.test.yahdata.analy.probability.bean.MonthlyPerformance;
import com.sjm.test.yahdata.analy.repo.StockMarketRepo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MonthlyVolatilityAnalyst {

	public MonthlyVolatilityAnalyst() {
	}

//txDatePattern : "-04-"
	public void exportResult(String stockCode, String txDatePattern) {

		List<StockBean> stockHistMonthList = StreamTransformHelper.extractMonthData(StockMarketRepo.getTrunk(stockCode), txDatePattern);
		String initTxnDate = stockHistMonthList.get(0).getTxnDate();

		LocalDate localDate = LocalDate.parse(initTxnDate);

		String yyyy = "" + localDate.getYear();	//init yyyy

		stockHistMonthList = StreamTransformHelper.trimThisMonth(stockHistMonthList);

		List<StockBean> ymList = new ArrayList<StockBean>(10);
		List<VolatilityBean> volatilityList = new ArrayList<VolatilityBean>();

		do {

			ymList = StreamTransformHelper.extractMonthData(stockHistMonthList, yyyy);
			if (ymList.isEmpty())
				break;

			StockBean prevMonthCloseStock = ProbabilityHelper.getPrevMonthStockData(stockCode, ymList.get(0).getTxnDate());

			VolatilityBean vol = this.analysisMonthVolatilityUpDownPercentage(yyyy + "-" + DisplayHelper.toMonth(txDatePattern), ymList, prevMonthCloseStock);
			if(vol==null)
				continue;
			
			volatilityList.add(vol);

			log.debug(" Process: " + yyyy + "-" + DisplayHelper.toMonth(txDatePattern));

			localDate = localDate.plusYears(1);
			yyyy = "" + localDate.getYear();


			
		} while (!ymList.isEmpty());

		this.exportMonthRangeProbability(stockCode, txDatePattern, volatilityList);

	}

//Range as a measure of volatility reference: https://www.macroption.com/volatility-true-range-atr/
	public void exportMonthRangeProbability(String stockCode, String mmPattern, List<VolatilityBean> volatilityList) {
		DoubleSummaryStatistics volLHStatistics = volatilityList.parallelStream().mapToDouble(x -> x.getvLow2Hight())
				.summaryStatistics();
		DoubleSummaryStatistics volHLStatistics = volatilityList.parallelStream().mapToDouble(x -> x.getvHight2Low())
				.summaryStatistics();

		String txt = "\n\n Historical Month Range(" + DisplayHelper.toMonth(mmPattern) + ") Probability: "
				+ volatilityList.size() + " years data:";

		txt += "\n Month Volatility Range(if-UP): Avg = " + GeneralHelper.toPct(volLHStatistics.getAverage())
				+ ", Min = " + GeneralHelper.toPct(volLHStatistics.getMin()) + ", Max = "
				+ GeneralHelper.toPct(volLHStatistics.getMax());
		txt += "\n Month Volatility Range(if-DOWN): Avg = "
				+ GeneralHelper.toPct(volHLStatistics.getAverage()) + ", Min = "
				+ GeneralHelper.toPct(volHLStatistics.getMax()) + ", Max = "
				+ GeneralHelper.toPct(volHLStatistics.getMin());
		log.info(txt);

		List<StockBean> targetList = StockMarketRepo.getTrunk(stockCode);
		targetList = StreamTransformHelper.trimThisMonth(targetList);
		StockBean lastBean = targetList.get(targetList.size() - 1);

		double ctoHAvg = lastBean.getC() * (1 + volLHStatistics.getAverage());
		double ctoHMin = lastBean.getC() * (1 + volLHStatistics.getMin());
		double ctoHMax = lastBean.getC() * (1 + volLHStatistics.getMax());

		double ctoLAvg = lastBean.getC() * (1 + volHLStatistics.getAverage());
		double ctoLMin = lastBean.getC() * (1 + volHLStatistics.getMin());
		double ctoLMax = lastBean.getC() * (1 + volHLStatistics.getMax());

		
		String txtEestimation = lastBean.getStockCode() + " " + lastBean.getTxnDate() + " Close Price: " + lastBean.getC();
		txtEestimation += "\n\tUP Scenario Theo-Price Hight: Avg: " + GeneralHelper.format( ctoHAvg) + ", Min: " + GeneralHelper.format( ctoHMin) + ", Max: " + GeneralHelper.format( ctoHMax) 
				+ "\n\tDOWN Scenario Theo-Price Low: Avg: " + GeneralHelper.format( ctoLAvg) + ", Min: " + GeneralHelper.format( ctoLMin) + ", Max: " + GeneralHelper.format( ctoLMax);
		System.out.println(txtEestimation);
		
		double avgHLDiff = volatilityList.parallelStream().mapToDouble(VolatilityBean::getActualHLDifference).average().orElse(Double.NaN);
		double minHLDiff = volatilityList.parallelStream().mapToDouble(VolatilityBean::getActualHLDifference).min().orElse(Double.NaN);
		double maxHLDiff = volatilityList.parallelStream().mapToDouble(VolatilityBean::getActualHLDifference).max().orElse(Double.NaN);
		
		System.out.println("\t Avg H-L: "+avgHLDiff);
		System.out.println("\t Max H-L: "+maxHLDiff);
		System.out.println("\t Min H-L: "+minHLDiff);
		System.out.println();
	}

	public VolatilityBean analysisMonthVolatilityUpDownPercentage(String yyyyMM, List<StockBean> ymList, StockBean prevMonth) {
		if(prevMonth==null)
			return null;
		double prevMonthEndClose = prevMonth.getC();
		
		double monthH = prevMonthEndClose;
		double monthL = prevMonthEndClose;
		////////////////////////////////////////////
		StockBean monthlyHightStock = ymList.parallelStream().max(Comparator.comparing(StockBean::getH)).get();
		StockBean monthlyLowStock = ymList.parallelStream().min(Comparator.comparing(StockBean::getL)).get();
		
		if(monthlyHightStock.getH() > prevMonthEndClose) {
			monthH = monthlyHightStock.getH();
		}
		
		if(prevMonthEndClose > monthlyLowStock.getL()) {
			monthL = monthlyLowStock.getL();
		}
				
		
		//calculate current loop's monthly volatility
//		double vLow2Hight = (monthlyHightStock.getH() - monthlyLowStock.getL()) / monthlyLowStock.getL();
//		double vHight2Low = (monthlyHightStock.getH() - monthlyLowStock.getL()) / monthlyHightStock.getH();

		double vLow2Hight = (monthH - monthL) / monthL;
		double vHight2Low = -(monthH - monthL) / monthH;
		
		VolatilityBean vol = new VolatilityBean();
		vol.setDate(yyyyMM);
		vol.setvLow2Hight(vLow2Hight);
		vol.setvHight2Low(vHight2Low);
		vol.setActualHLDifference(monthH-monthL);
		
// vol.setPrevC(lastDayPreviouseMonth.getAdjC());

		return vol;
	}
	

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

		DoubleSummaryStatistics diffH2LStats = ls.parallelStream().mapToDouble(x -> x.getDiffHighLow()).summaryStatistics();
		
		MonthlyPerformance first = ls.get(0);
		MonthlyPerformance last = ls.get(ls.size() - 1);

		long count = upStats.getCount() + downStats.getCount() + steadyStats.getCount();
		List<String> downYearList = ls.parallelStream().filter(x -> Const.DOWN.equalsIgnoreCase(x.getDirection()))
				.map(MonthlyPerformance::getYyyy).collect(Collectors.toList());

		String txt = "Historical Month(" + DisplayHelper.toMonth(ls.get(0).getMm())
				+ ") UP/Down Probability(including GapUP/GapDown): " + ls.size() + " years data:(" + first.getYyyy()
				+ "-" + last.getYyyy() + ")";
		
		txt += "\nUP(" + upStats.getCount() + ") VS DOWN(" + downStats.getCount() + ") VS STEADY("
				+ steadyStats.getCount() + "), Up%: "
				+ GeneralHelper.toPct(((double) upStats.getCount() / (double) count));
		
		txt += "\t DOWN  " + downYearList;
		txt += "\nMonth (Historical-UP): Avg = " + GeneralHelper.toPct(upStats.getAverage()) + ", Min = "
				+ GeneralHelper.toPct(upStats.getMin()) + ", Max = "
				+ GeneralHelper.toPct(upStats.getMax());
		txt += "\nMonth (Historical-DOWN): Avg = " + GeneralHelper.toPct(downStats.getAverage()) + ", Min = "
				+ GeneralHelper.toPct(downStats.getMax()) + ", Max = "
				+ GeneralHelper.toPct(downStats.getMin());

		txt += "\nMonth H to Month L: Avg = " + diffH2LStats.getAverage() + ", Max = "+diffH2LStats.getMax() + ", Min = "
				+ diffH2LStats.getMin();
		
		StringBuffer expString = new StringBuffer();
		
//		System.out.println(txt);
		expString.append("\n"+txt);
		expString.append("\n\tMonth\tStatus\tClose\t H \t L \tLowestDate\tHighestDate\tDiff H2L");
		expString.append("\n");
		for (MonthlyPerformance elemt : ls) {
			
			expString.append("\t"+elemt.getYyyy()+"-"+elemt.getMm()+"\t"+ elemt.getDirection());
			expString.append("\t"+GeneralHelper.toPct(elemt.getPercentageClose())+"\t"+GeneralHelper.toPct(elemt.getPercentageHigh())+"\t"+GeneralHelper.toPct(elemt.getPercentageLow()));
			expString.append("\t"+elemt.getLowestDate());
			expString.append("\t"+elemt.getHighestDate());
			expString.append("\t"+elemt.getDiffHighLow());
			expString.append("\n");

		}
		log.info("\n"+expString.toString());
	}



	public MonthlyPerformance generateMonthlyPerformance(String yyyy, String mm, List<StockBean> curYearlist, StockBean preMonthLastElement) {
		StockBean thisMonthLastRecord = curYearlist.get(curYearlist.size() - 1);

		double percentageCloseCompare = 0.0;
		double percentageLowCompare = 0.0;
		double percentageHighCompare = 0.0;
		
		String direction = Const.STEADY;

		if (preMonthLastElement != null) {
			
//			if (thisMonthLastRecord.getC() > preMonthLastElement.getC()) {
//				percentage = thisMonthLastRecord.getC() / preMonthLastElement.getC() - 1;
//				direction = Const.UP;
//			} else if (thisMonthLastRecord.getC() == preMonthLastElement.getC()) {
//				percentage = 0.0;
//			} else {
//				percentage = 1 - thisMonthLastRecord.getC() / preMonthLastElement.getC();
//				direction = Const.DOWN;
//			}
			
			
			DoubleSummaryStatistics lowStats = curYearlist.parallelStream().mapToDouble(x -> x.getL()).summaryStatistics();
			DoubleSummaryStatistics highStats = curYearlist.parallelStream().mapToDouble(x -> x.getH()).summaryStatistics();
			
			percentageCloseCompare = ( thisMonthLastRecord.getC()-preMonthLastElement.getC() )/preMonthLastElement.getC();
			
			percentageLowCompare = ( lowStats.getMin()-preMonthLastElement.getC() )/preMonthLastElement.getC();
			
			percentageHighCompare = ( highStats.getMax()-preMonthLastElement.getC() )/preMonthLastElement.getC();
			if( percentageCloseCompare > 0 ) {
				direction = Const.UP;
			}else if( percentageCloseCompare == 0.0 ) {
				direction = Const.STEADY;
			}else {
				direction = Const.DOWN;
			}
			
				
		}

//		int c2HDayIdx = this.findMaxIndex(curYearlist)+1;
//		int c2LDayIdx = this.findMinIdx(curYearlist)+1;
		
		//people
//	      .stream()
//	      .min(Comparator.comparing(Person::getAge))
//	      .orElseThrow(NoSuchElementException::new);
		StockBean highest = curYearlist.parallelStream().max(Comparator.comparing(StockBean::getH)).orElseThrow(NoSuchElementException::new);
		StockBean lowest = curYearlist.parallelStream().min(Comparator.comparing(StockBean::getL)).orElseThrow(NoSuchElementException::new);
		
		MonthlyPerformance mp = new MonthlyPerformance();

		mp.setYyyy(yyyy);
		mp.setMm(mm);
		mp.setDirection(direction);
		mp.setPercentageClose(percentageCloseCompare);
		mp.setPercentageHigh(percentageHighCompare);
		mp.setPercentageLow(percentageLowCompare);
		mp.setStockCode(thisMonthLastRecord.getStockCode());
		mp.setHighestDate(highest.getTxnDate());
		mp.setLowestDate(lowest.getTxnDate());
		
		
		mp.setDiffHighLow(Math.round((highest.getH() - lowest.getL()) * 100.0) / 100.0);
//		mp.setC2HDayIdx(c2HDayIdx);
//		mp.setC2LDayIdx(c2LDayIdx);
		return mp;
	}

	
//	@Deprecated
//	public List<StockBean> ignoreCurrentMonth(List<StockBean> ymList, String yyyy, String MM) {
////if now is end of the month, DO NOT ignore
////if now is in the last week. DO not ignore
////if not is in the 1-3 week.
//
//		LocalDate now = LocalDate.now();
//
//		if (yyyy != null && yyyy.equalsIgnoreCase(now.getYear() + "")) {
//
//			int dayOfMonth = now.get(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH);
//			if (dayOfMonth > 23) {
//				List<StockBean> bList = ymList.parallelStream().filter(x -> !x.getTxnDate().contains(yyyy + "-" + MM))
//						.collect(Collectors.toList());
//				return bList;
//			} else {
//				log.info("\t SKIP current Month , current day of month : " + dayOfMonth);
//			}
//		}
//		return ymList;
//
//	}
//
//	public static void main(String arg[]) {
//		LocalDate now = LocalDate.of(2021, 2, 2);
//
//		MonthlyVolatilityAnalyst app = new MonthlyVolatilityAnalyst();
//		app.testExport12MonthProbability(null, "2004-06-16", null);
//	}

	public void testExport12MonthProbability(String stockCode, String initTxnDate, List<String> month) {

		//get init yyyy-mm
		LocalDate initDate = LocalDate.parse(initTxnDate);
		int yearIdxStart = initDate.getYear();
		int initYyyyMM = Integer.parseInt(yearIdxStart + String.format("%02d", initDate.getMonthValue()));

		// calc end yyyy-mm
		LocalDate now = LocalDate.now();
//		int dayOfMonth = now.get(ChronoField.DAY_OF_MONTH);
		int dayOfMonth = now.getDayOfMonth();
		int nowYyyyMM = 0;
		if (dayOfMonth > 23) {
			nowYyyyMM = Integer.parseInt(now.getYear() + String.format("%02d", now.getMonthValue()));
		} else {
			LocalDate prevMonth = now.plusMonths(-1);
			nowYyyyMM = Integer.parseInt(prevMonth.getYear() + String.format("%02d", prevMonth.getMonthValue()));
		}

		log.debug("yearIdxStart= " + yearIdxStart + ", initDate.getMonthValue()=" + initDate.getMonthValue());

		HistMonthlyPerformanceDataContainer histMonthPerformanceContainer = new HistMonthlyPerformanceDataContainer();

		//start loop logic
		for (int y = yearIdxStart; y <= now.getYear(); y++) {

			List<StockBean> ymList = new ArrayList<StockBean>(10);
//			List<MonthlyPerformance> histMonthlyPerformanceList = new ArrayList<MonthlyPerformance>(10);

			for (int m = 1; m <= 12; m++) {

				String curMM = String.format("%02d", m);
				int curYyyyMM = Integer.parseInt(y + curMM);
				String txDatePattern = y + "-" + String.format("%02d", m);// example: 2020-04

				if (!(initYyyyMM <= curYyyyMM && curYyyyMM <= nowYyyyMM)) {
					continue;
				} else {
					//core logic here
//					log.debug(" " + curYyyyMM);
					ymList = StreamTransformHelper.extractMonthData(StockMarketRepo.getTrunk(stockCode), txDatePattern);

					if(ymList.isEmpty())
						continue;
					StockBean prevMonthCloseStock = ProbabilityHelper.getPrevMonthStockData(stockCode, ymList.get(0).getTxnDate());

					MonthlyPerformance mp = this.generateMonthlyPerformance(y + "", DisplayHelper.toMonth(curMM), ymList, prevMonthCloseStock);
//					histMonthlyPerformanceList.add(mp);
					histMonthPerformanceContainer.put(DisplayHelper.toMonth(curMM), mp);


				}

			}
		}


		log.info("\n" + stockCode + " Month 1 - 12 summary: ");

		List<String> monthKey = new ArrayList<>(histMonthPerformanceContainer.getMap().keySet());
		Collections.sort(monthKey);
		for (String string : monthKey) {
			if (month.contains("-" + string + "-"))
				this.exportMonthProbability(histMonthPerformanceContainer.getValue(string));
		}

	}
	
	
	
	public void generateMonthProbability(String stockCode, String initTxnDate, List<String> month) {

		//get init yyyy-mm
		LocalDate initDate = LocalDate.parse(initTxnDate);
		int yearIdxStart = initDate.getYear();
		int initYyyyMM = Integer.parseInt(yearIdxStart + String.format("%02d", initDate.getMonthValue()));

		// calc end yyyy-mm
		LocalDate now = LocalDate.now();
//		int dayOfMonth = now.get(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH);
		int dayOfMonth = now.getDayOfMonth();
		int nowYyyyMM = 0;
		if (dayOfMonth > 23) {
			nowYyyyMM = Integer.parseInt(now.getYear() + String.format("%02d", now.getMonthValue()));
		} else {
			LocalDate prevMonth = now.plusMonths(-1);
			nowYyyyMM = Integer.parseInt(prevMonth.getYear() + String.format("%02d", prevMonth.getMonthValue()));
		}

		log.debug("yearIdxStart= " + yearIdxStart + ", initDate.getMonthValue()=" + initDate.getMonthValue());

		HistMonthlyPerformanceDataContainer histMonthPerformanceContainer = new HistMonthlyPerformanceDataContainer();

		//start loop logic
		for (int y = yearIdxStart; y <= now.getYear(); y++) {

			List<StockBean> ymList = new ArrayList<StockBean>(10);
//			List<MonthlyPerformance> histMonthlyPerformanceList = new ArrayList<MonthlyPerformance>(10);

			for (int m = 1; m <= 12; m++) {

				String curMM = String.format("%02d", m);
				int curYyyyMM = Integer.parseInt(y + curMM);
				String txDatePattern = y + "-" + String.format("%02d", m);// example: 2020-04

				if (!(initYyyyMM <= curYyyyMM && curYyyyMM <= nowYyyyMM)) {
					continue;
				} else {
					//core logic here
//					log.debug(" " + curYyyyMM);
					ymList = StreamTransformHelper.extractMonthData(StockMarketRepo.getTrunk(stockCode), txDatePattern);

					if(ymList.isEmpty())
						continue;
					StockBean prevMonthCloseStock = ProbabilityHelper.getPrevMonthStockData(stockCode, ymList.get(0).getTxnDate());

					MonthlyPerformance mp = this.generateMonthlyPerformance(y + "", DisplayHelper.toMonth(curMM), ymList, prevMonthCloseStock);
//					histMonthlyPerformanceList.add(mp);
					histMonthPerformanceContainer.put(DisplayHelper.toMonth(curMM), mp);

				}

			}
		}

		log.info("\n" + stockCode + " Month 1 - 12 summary: ");

		List<String> monthKey = new ArrayList<>(histMonthPerformanceContainer.getMap().keySet());
		Collections.sort(monthKey);
		for (String string : monthKey) {
			if (month.contains("-" + string + "-"))
				this.exportMonthProbability(histMonthPerformanceContainer.getValue(string));
		}

	}
	
	
	public void func2(String stockCode, int yearIdxStart, LocalDate now , int initYyyyMM, int nowYyyyMM) {
		//int yearIdxStart = initDate.getYear();
		List<StockBean> ymList = new ArrayList<StockBean>(10);
//		List<MonthlyPerformance> histMonthlyPerformanceList = new ArrayList<MonthlyPerformance>(10);
		for (int y = yearIdxStart; y <= now.getYear(); y++) {
			for (int m = 1; m <= 12; m++) {
	
				String curMM = String.format("%02d", m);
				int curYyyyMM = Integer.parseInt(y + curMM);
				String txDatePattern = y + "-" + String.format("%02d", m);// example: 2020-04
	
				if (!(initYyyyMM <= curYyyyMM && curYyyyMM <= nowYyyyMM)) {
					continue;
				} else {
					//core logic here
	//				log.debug(" " + curYyyyMM);
					ymList = StreamTransformHelper.extractMonthData(StockMarketRepo.getTrunk(stockCode), txDatePattern);
	
					if(ymList.isEmpty())
						continue;
					StockBean prevMonthCloseStock = ProbabilityHelper.getPrevMonthStockData(stockCode, ymList.get(0).getTxnDate());
	
					MonthlyPerformance mp = this.generateMonthlyPerformance(y + "", DisplayHelper.toMonth(curMM), ymList, prevMonthCloseStock);
	//				histMonthlyPerformanceList.add(mp);
	//				histMonthPerformanceContainer.put(DisplayHelper.toMonth(curMM), mp);
	
	
				}
	
			}
		}
	}
	
	
//	public int findMinIdx(List<StockBean> numbers) { 
//	    if (numbers == null || numbers.size() == 0) return -1; // Saves time for empty array 
//	     
//	    double minVal = numbers.get(0).getL(); // Keeps a running count of the smallest value so far 
//	    int minIdx = 0; // Will store the index of minVal 
//	    for(int idx=1; idx<numbers.size(); idx++) { 
//	        if(numbers.get(idx).getL() < minVal) { 
//	            minVal = numbers.get(idx).getL(); 
//	            minIdx = idx; 
//	        } 
//	    } 
//	    return minIdx; 
//	} 
//	
//	
//	public int findMaxIndex(List<StockBean> arr) {
//		if (arr == null || arr.size() == 0) return -1; // Saves time for empty array
//		
//		double max = arr.get(0).getH();  
//	     int maxIdx = 0;  
//	     for(int i = 1; i < arr.size(); i++) {  
//	          if(arr.get(i).getH() > max) {  
//	             max = arr.get(i).getH();  
//	             maxIdx = i;  
//	          }  
//	     }  
//	     return maxIdx;  
//	}

}
