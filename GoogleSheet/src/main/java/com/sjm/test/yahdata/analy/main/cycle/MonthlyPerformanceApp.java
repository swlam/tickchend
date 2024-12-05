package com.sjm.test.yahdata.analy.main.cycle;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.cfg.CNStockListConfig;
import com.sjm.test.yahdata.analy.cfg.GlobalConfig;
import com.sjm.test.yahdata.analy.cfg.HKStockListConfig;
import com.sjm.test.yahdata.analy.cfg.USStockListConfig;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.helper.AnalyGeneralHelper;
import com.sjm.test.yahdata.analy.helper.CFGHelper;
import com.sjm.test.yahdata.analy.helper.DisplayHelper;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.main.BaseApp;
import com.sjm.test.yahdata.analy.module.benchmarks.d2d.BenchmarkModule;
import com.sjm.test.yahdata.analy.module.lowhighdist.LowHighDateHelper;
import com.sjm.test.yahdata.analy.module.lowhighdist.bean.LowHighDateSimplifyResult;
import com.sjm.test.yahdata.analy.probability.ProbabilityHelper;
import com.sjm.test.yahdata.analy.probability.bean.BenchmarkBean;
import com.sjm.test.yahdata.analy.probability.bean.BenchmarkBeanResult;
import com.sjm.test.yahdata.analy.repo.StockMarketRepo;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class MonthlyPerformanceApp extends BaseApp{
	
	private static final String INTERAL = Const.INTERVAL_D;
	
	public static final int REQUIRE_MIN_DAYS = -1;
	public static final double REQUIRE_MIN_WIN_RATE = 0.5;
	public static final int REQUIRE_MIN_YRS_DATA = 5;
	public static final String END2END_PATTERN = Const.END2END_C2C;
	public static int YEARS = 10;
	
	public static List<String> targetMonth = Arrays.asList("-10-");
//	private static  List<String> CODE_POOL = USStockListConfig.ETF;
	public static  List<String> CODE_POOL =Stream.of(  
//			HKStockListConfig.ALL,
			USStockListConfig.ALL
//			USStockListConfig.ETF_COUNTRY,
//			USStockListConfig.ETF_INVERSE,
//			USStockListConfig.CURRENCY,
//			USStockListConfig.CRYPTO,
//			HKStockListConfig.STOCK_INDEX, HKStockListConfig.ETF
//			CNStockListConfig.ALL_AVAILABLE
			)
	    .flatMap(Collection::stream).distinct().collect(Collectors.toList());
//	private static  List<String> CODE_POOL = Arrays.asList("TWD=X");
	
	
//	public static  List<String> CODE_POOL = Stream.of(HKStockListConfig.HSI_ELEMENT, 
//			HKStockListConfig.HSCEI_ELEMENT, HKStockListConfig.ETF, 
//			HKStockListConfig.STOCK_INDEX)
//			.flatMap(Collection::stream)
//			.distinct().collect(Collectors.toList());


	private static BenchmarkModule module = new BenchmarkModule();
	
	public static void main(String[] args) throws Exception{
		GlobalConfig.setYearOfStockData(YEARS);
		
		CFGHelper.validateConfig();
		CFGHelper.loadMetaInfo(GlobalConfig.DEFAULT_DEFAULT_PATH);

		GlobalConfig.TARGET_MONTH = targetMonth;
		
		List<StockBean> fullTrunkList = loadStockData(CODE_POOL, INTERAL);	
			
		StockMarketRepo.getTrunk().addAll(fullTrunkList);
		
		List<String> monthList = new ArrayList<>(GlobalConfig.TARGET_MONTH);
		Collections.sort(monthList);
		for (String targetWatchMonth : monthList) {
			exportResult(targetWatchMonth, CODE_POOL, fullTrunkList);
		}
		System.out.println("Month Performance App DONE");
	}
	
	
	public static void exportResult(String targetWatchMonth, List<String> stockPool, List<StockBean> fullTrunkList) {
		
			List<BenchmarkBeanResult> stockPerformanceResultList = new ArrayList<BenchmarkBeanResult>(10);
			
			for (String code : stockPool) {
				log.debug("\nprocessing "+code);
				List<StockBean> list = StreamTransformHelper.extractByStockCode(fullTrunkList, code);				
				try{
					String beginDate = list.get(0).getTxnDate();
					log.debug("\n"+code+" Load data, select data from "+beginDate +" \n");
				
					BenchmarkBeanResult stockResult = generateMonthProbability(code, beginDate, targetWatchMonth);
					if(stockResult!=null && stockResult.getNumOfYears()>=REQUIRE_MIN_YRS_DATA) {
						stockPerformanceResultList.add(stockResult);
					}else {
						log.debug(code+" don't have "+targetWatchMonth+" data");		
					}
				}catch(Exception e) {
					log.error("SKIP, due to error on Stock: "+code+", list size = "+list.size(), e);
				}
			}
				
		
        
		//ordering
        List<BenchmarkBeanResult> descList = stockPerformanceResultList.parallelStream()
        	    .sorted(Comparator.comparingDouble(BenchmarkBeanResult::getRisesRatioC2C).reversed()).collect(Collectors.toList());
        
        
//        log.info("\n\t# Watch Month:"+targetWatchMonth.replace("-", "")+", Win Rate: [BEST: "+best.getStockCode()+" "+GeneralHelper.to100Percentage(best.getUpRatio())+", WORST: "+worst.getStockCode()+" " + GeneralHelper.to100Percentage(worst.getUpRatio())+"]");
        	        
        String header = "\n\t月份:"+targetWatchMonth.replace("-", "")+", 方向: "+GlobalConfig.STRATEGRY;
        header +="\nMonth\tCODE\tName\tSector\t所屬ETF\t年數\t起始年份"        		
        		+ "\t升(C2C)\t升(C2PH)\t跌(C2PL)\t升(O2C)\t升(O2PH)\t跌(O2PL)"
        		+ "\t升(LH)\t跌(LH)\t升(HL)\t跌(HL)"
				+"\t下跌年份" 
				+ "\t低位日期\t低位日子數\t低位日子Prob.\t高位日期\t高位日子數\t高位日子Prob."//\t開始日是最低價的日子"
        		+ "\t回落到負回報率比例\t沒跌回起點的日期"
        		+ "\t中位(波幅)\tAvg(波幅)\tMax(波幅)\tMin(波幅)\t9成(波幅)\tSD"
        		+ "\t中位(C2C)\tAvg(C2C)\tMax(C2C)\tMin(C2C)\t9成升跌幅(C2C)\tSD"        		
        		+ "\t中位(C2PH)\tAvg(C2PH)\tMax(C2PH)\tMin(C2PH)\t9成升/跌幅(C2PH)\tSD"
        		+ "\t中位(C2PL)\tAvg(C2PL)\tMax(C2PL)\tMin(C2PL)\t9成升/跌幅(C2PL)\tSD"
        		+ "\t中位(O2C)\tAvg(O2C)\tMax(O2C)\tMin(O2C)\t9成升跌幅(O2C)\tSD"        		
        		+ "\t中位(O2PH)\tAvg(O2PH)\tMax(O2H)\tMin(O2PH)\t9成升/跌幅(O2PH)\tSD"
        		+ "\t中位(O2PL)\tAvg(O2PL)\tMax(O2PL)\tMin(O2PL)\t9成升/跌幅(O2PL)\tSD"
        		
				+ "\t中位(E2E_LH)\tAvg(E2E_LH)\tMax(E2E_LH)\tMin(E2E_LH)\t9成升/跌幅(E2E_LH)\tSD"
				+ "\t中位(E2E_HL)\tAvg(E2E_HL)\tMax(E2E_HL)\tMin(E2E_HL)\t9成升/跌幅(E2E_HL)\tSD"


        		+ "\t最後一年同期 "
        		+ "\tC-C \tVolatility(L2H)\tC-L \tC-H "
        		+ "\tPATTERN (CC)"
        		+ "\t最近同期 "
        		+ "\tC-C"
        		+ "\t波幅"
        		+ "\tC-L "
        		+ "\tC-H "
        		+ "\tPATTERN (CC)";
        
        StringBuilder sb = new StringBuilder();
        sb.append(header+"\n");
        for(int i=0; i< descList.size(); i++) {
        	BenchmarkBeanResult sumy = descList.get(i);       
			sb.append(""+targetWatchMonth.replace("-", "")+"\t "+sumy.getStockCode().replace(".HK", "") );
			sb.append("\t"+ sumy.getStockName() );		
			sb.append("\t"+ sumy.getSector() );
			sb.append("\t"+ sumy.getBelongETF() );
			sb.append("\t"+ sumy.getNumOfYears() );			
			sb.append("\t"+ sumy.getInitDate()); //GlobalConfig.CUSTOM_START_YEAR );	
			sb.append("\t" + GeneralHelper.toPct(sumy.getRisesRatioC2C()));
			sb.append("\t" + GeneralHelper.toPct(sumy.getRisesRatioC2ph()));
			sb.append("\t" + GeneralHelper.toPct(sumy.getFallsRatioC2pl()));
			sb.append("\t" + GeneralHelper.toPct(sumy.getRisesRatioO2C()));
			sb.append("\t" + GeneralHelper.toPct(sumy.getRisesRatioO2ph()));
			sb.append("\t" + GeneralHelper.toPct(sumy.getFallsRatioO2pl()));
//			sb.append("\t" + GeneralHelper.to100Pct(sumy.getC2HPositiveReturnProportion()));
//			sb.append("\t" + GeneralHelper.to100Pct(sumy.getC2LNegativeReturnProportion()));
			sb.append("\t" + GeneralHelper.toPct(sumy.getRisesRatioEnd2EndL2H()));
			sb.append("\t" + GeneralHelper.toPct(sumy.getFallsRatioEnd2EndL2H()));
			sb.append("\t" + GeneralHelper.toPct(sumy.getRisesRatioEnd2EndH2L()));
			sb.append("\t" + GeneralHelper.toPct(sumy.getFallsRatioEnd2EndH2L()));
			
			sb.append("\t" + sumy.getO2cDownYearList() );//targetMonth
			sb.append("\t"+ ((sumy.getLowHighDateSimplifyResult()==null)?" \t \t \t ":sumy.getLowHighDateSimplifyResult().toPrintResult())) ;
//			sb.append("\t" + sumy.getStartDateIsLowestPriceDayList());
			sb.append("\t" + GeneralHelper.toPct(sumy.getFallBackToNegativeReturnRatio()));
			sb.append("\t" + sumy.getNonNegativeRatioDates()); //沒跌回起點的日期
			sb.append("\t" + sumy.getPercentageRangeStat().getDescription());//波幅
			
			sb.append("\t" + sumy.getC2cStat().getDescription());			
			sb.append("\t" + sumy.getC2phStat().getDescription());
			sb.append("\t" + sumy.getC2plStat().getDescription());
			
			sb.append("\t" + sumy.getO2cStat().getDescription());			
			sb.append("\t" + sumy.getO2phStat().getDescription());
			sb.append("\t" + sumy.getO2plStat().getDescription());
			

			sb.append("\t" + sumy.getEnd2EndLHStat().getDescription());
			sb.append("\t" + sumy.getEnd2EndHLStat().getDescription());	
			
			sb.append("\t" + sumy.getRecentBenchmarkBean().getStartTxnDate()  +"_"+ sumy.getRecentBenchmarkBean().getEndTxnDate() );
			sb.append("\t" + GeneralHelper.toPct(sumy.getRecentBenchmarkBean().getPctC2C()) );
			sb.append("\t" + GeneralHelper.toPct(sumy.getRecentBenchmarkBean().getPercentageRange()) );
			sb.append("\t" + GeneralHelper.toPct(sumy.getRecentBenchmarkBean().getPctC2Lowest()) );
			sb.append("\t" + GeneralHelper.toPct(sumy.getRecentBenchmarkBean().getPctC2Highest()) );
			sb.append("\t" + AnalyGeneralHelper.getDirectionText(sumy.getRecentBenchmarkBean().getPctC2C()) );
//			sb.append("\t" + AnalyGeneralHelper.getDirectionText(sumy.getRecentBenchmarkBean().getPctEnd2EndLH()) );
//			sb.append("\t" + AnalyGeneralHelper.getDirectionText(sumy.getRecentBenchmarkBean().getPctEnd2EndHL()) );
			
			if(sumy.getCurrentBenchmarkBean()!=null) {
				sb.append("\t" + sumy.getCurrentBenchmarkBean().getStartTxnDate() +"_"+ sumy.getCurrentBenchmarkBean().getEndTxnDate() );
				sb.append("\t" + GeneralHelper.toPct(sumy.getCurrentBenchmarkBean().getPctC2C()) );
				sb.append("\t" + GeneralHelper.toPct(sumy.getCurrentBenchmarkBean().getPercentageRange()) );
				sb.append("\t" + GeneralHelper.toPct(sumy.getCurrentBenchmarkBean().getPctC2Lowest()) );
				sb.append("\t" + GeneralHelper.toPct(sumy.getCurrentBenchmarkBean().getPctC2Highest()) );
				sb.append("\t" + AnalyGeneralHelper.getDirectionText(sumy.getCurrentBenchmarkBean().getPctC2C()) );
//				sb.append("\t" + AnalyGeneralHelper.getDirectionText(sumy.getCurrentBenchmarkBean().getPctEnd2EndLH()) );
//				sb.append("\t" + AnalyGeneralHelper.getDirectionText(sumy.getCurrentBenchmarkBean().getPctEnd2EndHL()) );
			}
			sb.append("\n");
		}
        log.info( sb.toString());
		
	}
	
	public static BenchmarkBeanResult generateMonthProbability(String stockCode, String initTxnDate, String month) {

		//get init yyyy-mm
		LocalDate initDate = LocalDate.parse(initTxnDate);
		int yearIdxStart = initDate.getYear();
		int initYyyyMM = Integer.parseInt(yearIdxStart + String.format("%02d", initDate.getMonthValue()));

		// calc end yyyy-mm
		LocalDate now = LocalDate.now();
//		int dayOfMonth = now.get(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH);
		int dayOfMonth = now.getDayOfMonth();
		int nowYyyyMM = 0;
		if (dayOfMonth > GlobalConfig.LAST_DATE_OF_MONTH) {
			nowYyyyMM = Integer.parseInt(now.getYear() + String.format("%02d", now.getMonthValue()));
		} else {
			LocalDate prevMonth = now.plusMonths(-1);
			nowYyyyMM = Integer.parseInt(prevMonth.getYear() + String.format("%02d", prevMonth.getMonthValue()));
		}

		log.debug("yearIdxStart= " + yearIdxStart + ", initDate.getMonthValue()=" + initDate.getMonthValue());
		
		List<BenchmarkBean> monthlyBenchmarkList = new ArrayList<BenchmarkBean>(10);

		List<StockBean > stockList = StockMarketRepo.getTrunk(stockCode);
		StockBean recentStock = stockList.get(stockList.size()-1);
		
		List<StockBean > currentMonthList = StreamTransformHelper.extractMonthData(StockMarketRepo.getTrunk(stockCode), recentStock.getTxnDate().substring(0,7));
		
		String tmpY = recentStock.getTxnDate().substring(0,4);
		String tmpM = recentStock.getTxnDate().substring(5, 7);
		
		log.debug("generateMonthProbability, stockCode: "+stockCode);
		BenchmarkBean currentMonthPerformance = null;
		try {
			StockBean prevMonthClose = ProbabilityHelper.getPrevMonthStockData(stockCode, recentStock.getTxnDate());
			if(prevMonthClose != null)	
				currentMonthList.add(0, prevMonthClose);
			currentMonthPerformance = ProbabilityHelper.generateBenchmarkBean(tmpY, tmpM, currentMonthList, prevMonthClose, true);
			log.debug("currentMonthPerformance: "+currentMonthPerformance);
		}catch(Exception e) {
			log.error(null, e);
		}
		
		
		//start loop logic
		for (int y = yearIdxStart; y <= now.getYear(); y++) {
			List<StockBean> ymList = new ArrayList<StockBean>(10);

			for (int m = 1; m <= 12; m++) {

				String curMM = String.format("%02d", m);
				
				if( month.contains(curMM) ==false)
					continue;
				
				int curYyyyMM = Integer.parseInt(y + curMM);
				String txDatePattern = y + "-" + String.format("%02d", m);// example: 2020-04

				if (!(initYyyyMM <= curYyyyMM && curYyyyMM <= nowYyyyMM)) {
					continue;
				} else {
					//core logic here

					ymList = StreamTransformHelper.extractMonthData(StockMarketRepo.getTrunk(stockCode), txDatePattern);

					if(ymList.isEmpty())
						continue;
					
//					if( ymList.get(0).getTxnDate().equalsIgnoreCase("2010-09-02") && ymList.get(0).getStockCode().equalsIgnoreCase("2318.HK"))
//						System.out.println("debug here");
					
					StockBean prevMonthCloseStock = ProbabilityHelper.getPrevMonthStockData(stockCode, ymList.get(0).getTxnDate());
					if(prevMonthCloseStock != null)						
						ymList.add(0, prevMonthCloseStock);
					BenchmarkBean mp = null;
					try {
						mp = ProbabilityHelper.generateBenchmarkBean(y + "", DisplayHelper.toMonth(curMM), ymList, prevMonthCloseStock, true);
						if(mp!=null) {
							LocalDate prevMonthCloseStockDate = LocalDate.parse(prevMonthCloseStock.getTxnDate());
							
							StockBean lastStockThisMonth = ymList.get(ymList.size()-1);
							LocalDate currMonthCloseStockDate = LocalDate.parse(lastStockThisMonth.getTxnDate());

							String startMMDD = GeneralHelper.to2DigitsWithZeroPadded(prevMonthCloseStockDate.getMonthValue()) +"-"+ GeneralHelper.to2DigitsWithZeroPadded(prevMonthCloseStockDate.getDayOfMonth());
							String endMMDD = GeneralHelper.to2DigitsWithZeroPadded(currMonthCloseStockDate.getMonthValue()) +"-"+ GeneralHelper.to2DigitsWithZeroPadded(currMonthCloseStockDate.getDayOfMonth());
							mp.setStartMMdd(startMMDD);
							mp.setEndMMdd(endMMDD);
							monthlyBenchmarkList.add(mp);
						}
					}catch(Exception e) {
						log.error(null, e);
						continue;
					}
					
				}
			}
		}// end loop
		
		
		LocalDate inputMonthFirstDate = LocalDate.parse(now.getYear()+""+month+"01");
		LocalDate lastDayOfInputMonth = inputMonthFirstDate.with(TemporalAdjusters.lastDayOfMonth());
		
		
		String MMDD_START = month.substring(1) + "01";
		String MMDD_END = lastDayOfInputMonth.toString().substring(5);

		BenchmarkBeanResult stockResult = module.generateBenchmarkResult(monthlyBenchmarkList,currentMonthPerformance, REQUIRE_MIN_WIN_RATE, REQUIRE_MIN_DAYS, END2END_PATTERN);
		if(stockResult!=null) {
			LowHighDateSimplifyResult lowHighResult = LowHighDateHelper.generate(stockList, MMDD_START, MMDD_END);
			stockResult.setLowHighDateSimplifyResult(lowHighResult);
			
			stockResult.setStockName((CFGHelper.getStockProfileMap().get(stockResult.getStockCode())==null?"NA":CFGHelper.getStockProfileMap().get(stockResult.getStockCode()).getName()));
			stockResult.setSector((CFGHelper.getStockProfileMap().get(stockResult.getStockCode())==null?"NA":CFGHelper.getStockProfileMap().get(stockResult.getStockCode()).getSector()));
			stockResult.setInitDate(initDate.toString().substring(0,10));
		}
		return stockResult;
	}
	
	public static BenchmarkBeanResult generateMonthProbability(String stockCode, String initTxnDate, String month, String o2cOrC2) {

		//get init yyyy-mm
		LocalDate initDate = LocalDate.parse(initTxnDate);
		int yearIdxStart = initDate.getYear();
		int initYyyyMM = Integer.parseInt(yearIdxStart + String.format("%02d", initDate.getMonthValue()));

		// calc end yyyy-mm
		LocalDate now = LocalDate.now();
//		int dayOfMonth = now.get(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH);
		int dayOfMonth = now.getDayOfMonth();
		int nowYyyyMM = 0;
		if (dayOfMonth > GlobalConfig.LAST_DATE_OF_MONTH) {
			nowYyyyMM = Integer.parseInt(now.getYear() + String.format("%02d", now.getMonthValue()));
		} else {
			LocalDate prevMonth = now.plusMonths(-1);
			nowYyyyMM = Integer.parseInt(prevMonth.getYear() + String.format("%02d", prevMonth.getMonthValue()));
		}

		log.debug("yearIdxStart= " + yearIdxStart + ", initDate.getMonthValue()=" + initDate.getMonthValue());
		
		List<BenchmarkBean> monthlyBenchmarkList = new ArrayList<BenchmarkBean>(10);

		List<StockBean > stockList = StockMarketRepo.getTrunk(stockCode);
		StockBean recentStock = stockList.get(stockList.size()-1);
		
		List<StockBean > currentMonthList = StreamTransformHelper.extractMonthData(StockMarketRepo.getTrunk(stockCode), recentStock.getTxnDate().substring(0,7));
		
		String tmpY = recentStock.getTxnDate().substring(0,4);
		String tmpM = recentStock.getTxnDate().substring(5, 7);
		
		log.debug("generateMonthProbability, stockCode: "+stockCode);
		BenchmarkBean currentMonthPerformance = null;
		try {
			StockBean prevMonthClose = ProbabilityHelper.getPrevMonthStockData(stockCode, recentStock.getTxnDate());
			if(prevMonthClose != null)	
				currentMonthList.add(0, prevMonthClose);
			currentMonthPerformance = ProbabilityHelper.generateBenchmarkBean(tmpY, tmpM, currentMonthList, prevMonthClose, true);
			log.debug("currentMonthPerformance: "+currentMonthPerformance);
		}catch(Exception e) {
			log.error(null, e);
		}
		
		
		//start loop logic
		for (int y = yearIdxStart; y <= now.getYear(); y++) {
			List<StockBean> ymList = new ArrayList<StockBean>(10);

			for (int m = 1; m <= 12; m++) {

				String curMM = String.format("%02d", m);
				
				if( month.contains(curMM) ==false)
					continue;
				
				int curYyyyMM = Integer.parseInt(y + curMM);
				String txDatePattern = y + "-" + String.format("%02d", m);// example: 2020-04

				if (!(initYyyyMM <= curYyyyMM && curYyyyMM <= nowYyyyMM)) {
					continue;
				} else {
					//core logic here

					ymList = StreamTransformHelper.extractMonthData(StockMarketRepo.getTrunk(stockCode), txDatePattern);

					if(ymList.isEmpty())
						continue;
					
//					if( ymList.get(0).getTxnDate().equalsIgnoreCase("2010-09-02") && ymList.get(0).getStockCode().equalsIgnoreCase("2318.HK"))
//						System.out.println("debug here");
					StockBean referenceCalcStartingStockBean = null;
					if(Const.END2END_O2C.equalsIgnoreCase(o2cOrC2)) {
						referenceCalcStartingStockBean = ProbabilityHelper.getPrevMonthStockData(stockCode, ymList.get(0).getTxnDate());
						if(referenceCalcStartingStockBean != null)						
							ymList.add(0, referenceCalcStartingStockBean);
					}else {
						referenceCalcStartingStockBean = ymList.get(0);
					}
					
					BenchmarkBean mp = null;
					try {
						mp = ProbabilityHelper.generateBenchmarkBean(y + "", DisplayHelper.toMonth(curMM), ymList, referenceCalcStartingStockBean, true);
						if(mp!=null) {
							LocalDate prevMonthCloseStockDate = null;
							if(referenceCalcStartingStockBean==null) {
								prevMonthCloseStockDate = LocalDate.parse(ymList.get(0).getTxnDate());
							}else {
								prevMonthCloseStockDate = LocalDate.parse(referenceCalcStartingStockBean.getTxnDate());	
							}
								
							
							StockBean lastStockThisMonth = ymList.get(ymList.size()-1);
							LocalDate currMonthCloseStockDate = LocalDate.parse(lastStockThisMonth.getTxnDate());

							String startMMDD = GeneralHelper.to2DigitsWithZeroPadded(prevMonthCloseStockDate.getMonthValue()) +"-"+ GeneralHelper.to2DigitsWithZeroPadded(prevMonthCloseStockDate.getDayOfMonth());
							String endMMDD = GeneralHelper.to2DigitsWithZeroPadded(currMonthCloseStockDate.getMonthValue()) +"-"+ GeneralHelper.to2DigitsWithZeroPadded(currMonthCloseStockDate.getDayOfMonth());
							mp.setStartMMdd(startMMDD);
							mp.setEndMMdd(endMMDD);
							monthlyBenchmarkList.add(mp);
						}
					}catch(Exception e) {
						log.error(""+stockCode+" first Stock Date="+ymList.get(0), e);
						continue;
					}
					
				}
			}
		}// end loop
		
		
		LocalDate inputMonthFirstDate = LocalDate.parse(now.getYear()+""+month+"01");
		LocalDate lastDayOfInputMonth = inputMonthFirstDate.with(TemporalAdjusters.lastDayOfMonth());
		
		
		String MMDD_START = month.substring(1) + "01";
		String MMDD_END = lastDayOfInputMonth.toString().substring(5);

		BenchmarkBeanResult stockResult = module.generateBenchmarkResult(monthlyBenchmarkList,currentMonthPerformance, REQUIRE_MIN_WIN_RATE, REQUIRE_MIN_DAYS, END2END_PATTERN);
		if(stockResult!=null) {
			LowHighDateSimplifyResult lowHighResult = LowHighDateHelper.generate(stockList, MMDD_START, MMDD_END);
			stockResult.setLowHighDateSimplifyResult(lowHighResult);
			
			stockResult.setStockName((CFGHelper.getStockProfileMap().get(stockResult.getStockCode())==null?"NA":CFGHelper.getStockProfileMap().get(stockResult.getStockCode()).getName()));
			stockResult.setSector((CFGHelper.getStockProfileMap().get(stockResult.getStockCode())==null?"NA":CFGHelper.getStockProfileMap().get(stockResult.getStockCode()).getSector()));
			stockResult.setInitDate(initDate.toString().substring(0,10));
		}
		return stockResult;
	}

	
}
