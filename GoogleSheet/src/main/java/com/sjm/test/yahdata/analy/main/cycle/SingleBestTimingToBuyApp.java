package com.sjm.test.yahdata.analy.main.cycle;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.cfg.GlobalConfig;
import com.sjm.test.yahdata.analy.cfg.HKStockListConfig;
import com.sjm.test.yahdata.analy.cfg.USStockListConfig;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.helper.AnalyGeneralHelper;
import com.sjm.test.yahdata.analy.helper.CFGHelper;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.helper.UserCsvWriter;
import com.sjm.test.yahdata.analy.main.BaseApp;
import com.sjm.test.yahdata.analy.module.benchmarks.d2d.BenchmarkModule;
import com.sjm.test.yahdata.analy.module.benchmarks.d2d.DateRangeOverlapAnalyst;
import com.sjm.test.yahdata.analy.module.lowhighdist.LowHighDateHelper;
import com.sjm.test.yahdata.analy.module.lowhighdist.bean.LowHighDateSimplifyResult;
import com.sjm.test.yahdata.analy.probability.bean.BenchmarkBean;
import com.sjm.test.yahdata.analy.probability.bean.BenchmarkBeanResult;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class SingleBestTimingToBuyApp extends BaseApp{
//private static final log log = log.getlog(BestTimingToBuyApp.class);
	
	private static final String INTERAL = Const.INTERVAL_D;
	
	public static int RESULT_LIMIT_SIZE = 200;
	private static final String END2END_PATTERN = Const.END2END_C2C;
	private static final double REQUIRE_MIN_RATIO = 0.7;
	private static final int REQUIRE_MIN_DAY_DURATION = 4;

	private static final int CUSTOM_STOCK_START_YEAR = 2010;
	public static String START_MMDD= "11-29";
	public static String END_MMDD= "12-31";
	public static final int REQUIRED_YRS = 5; 
	
//	private static  List<String> TICKER_POOL = Stream.of(//USStockListConfig.INDEX,
//			CNStockListConfig.SZSE_ChiNext_ETF, CNStockListConfig.CSI_300_ETF).flatMap(Collection::stream).distinct().collect(Collectors.toList());

	public static  List<String> ADDITIONAL_TICKER = Arrays.asList("TSLA","NVDA","GOOGL","MSFT","META","NFLX","AAPL");
//	public static  List<String> TICKER_POOL = Arrays.asList("2318.HK");
//	private static  List<String> TICKER_POOL =  Stream.of( HKStockListConfig.STOCK_INDEX)
//		    .flatMap(Collection::stream).distinct().collect(Collectors.toList());
//	TICKER_POOL = Stream.of( USStockListConfig.MAIN)
//		    .flatMap(Collection::stream).distinct().collect(Collectors.toList());
	
	public static  List<String> TICKER_POOL = Stream.of( ADDITIONAL_TICKER,
			USStockListConfig.INDEX, HKStockListConfig.ETF, HKStockListConfig.STOCK_INDEX, USStockListConfig.ETF, USStockListConfig.CRYPTO)
		    .flatMap(Collection::stream).distinct().collect(Collectors.toList());

	

	public static void main(String[] args)  throws Exception{
//		GlobalConfig.STOCK_START_YEAR = CUSTOM_STOCK_START_YEAR;
		GlobalConfig.setYearOfStockData(20);
		CFGHelper.validateConfig();
		CFGHelper.loadMetaInfo(GlobalConfig.DEFAULT_DEFAULT_PATH);
		
		List<StockBean> fullTrunkList = loadStockData(TICKER_POOL, INTERAL);	
		
		List<String> stockMovingAvgResult = new ArrayList<String>(10);
		
		List<BenchmarkBeanResult>  unsortResultList = new ArrayList<BenchmarkBeanResult>(10); 
		log.info("REQUIRE_MINIMUM_C2C_RATIO: "+ GeneralHelper.toPct(GlobalConfig.REQUIRE_MINIMUM_C2C_RATIO) );
		log.info("Ticker size = "+TICKER_POOL.size());
		
		TICKER_POOL.parallelStream().forEach(productCode -> {
//			log.debug("\n\n============== "+productCode+" ============== Start Date: "+startDate);
			try {
				List<StockBean> productStockList = StreamTransformHelper.extractByStockCode(fullTrunkList, productCode);	
				List<BenchmarkBeanResult> descBenmrkList = findTopBenchmark(productStockList, productCode, START_MMDD, END_MMDD, REQUIRE_MIN_RATIO );
				unsortResultList.addAll(descBenmrkList);
				
				
//				stockMovingAvgResult.add(MovingAvgReport.exportMovingAvgStatus(productStockList));
			} catch (Exception e) {
				log.error(productCode, e);
			}    
		});
		
//		Comparator<BenchmarkBeanResult> sortByYrs = (p, o) -> Double.compare(p.getNumOfYears(), o.getNumOfYears());
		Comparator<BenchmarkBeanResult> sortByTicker = Comparator.comparing(BenchmarkBeanResult::getStockCode);
		Comparator<BenchmarkBeanResult> sortByStartMMdd = Comparator.comparing(BenchmarkBeanResult::getStartMMdd);
		Comparator<BenchmarkBeanResult> sortByEndMMdd = Comparator.comparing(BenchmarkBeanResult::getEndMMdd);
		
        Comparator<BenchmarkBeanResult> sortByC2HMedian = (p, o) -> Double.compare(p.getC2phStat().getMedian(), o.getC2phStat().getMedian());                 
        Comparator<BenchmarkBeanResult> comparator1 = sortByTicker.reversed().thenComparing(sortByStartMMdd);
        Comparator<BenchmarkBeanResult> comparator2 = comparator1.thenComparing(sortByEndMMdd);
        
        Comparator<BenchmarkBeanResult> comparator3 = comparator2.thenComparing(sortByC2HMedian.reversed());

        List<BenchmarkBeanResult> resultList = unsortResultList.parallelStream().sorted(comparator3).collect(Collectors.toList());
		
        String  header ="\nSORT by 上升比例 , " +END2END_PATTERN +", 方向: "+GlobalConfig.STRATEGRY
        		+ ", By Date Range: " + START_MMDD +" to "+ END_MMDD
        				+ "\nCODE\tName\tSector"
        				+"\t開始日期\t結束日期\t年數\t天數"
        								
				+"\t上升機率(CC)\t下跌機率(CC)\t上升機率(C2PeriodH)\t下跌機率(C2PeriodL)\t上升機率(LH)\t下跌機率(LH)\t上升機率(HL)\t下跌機率(HL)"
				+"\t下跌年份" 
				+ "\t低位日期\t低位日子數\t低位日子Prob.\t高位日期\t高位日子數\t高位日子Prob."//\t開始日是最低價的日子"				
        		+ "\t回落到负回报比例(上升年年份/不計開始日)\t沒跌回起點的日期(不計開始日)"        		
        		+ "\t升跌幅中位(C-C)\t升跌幅平均(C-C)\tMax升跌幅(C-C)\tMin升跌幅(C-C)\t9成升跌幅(C-C)\tSD"        		
        		+ "\t升跌幅中位(C2PH)\t升跌幅平均(C2PH)\tMax升跌幅(C2PH)\tMin升跌幅(C2PH)\t9成升跌幅(C2PH)\tSD"
        		+ "\t升跌幅中位(C2PL)\t升跌幅平均(C2PL)\tMax升跌幅(C2PL)\tMin升跌幅(C2PL)\t9成升跌幅(C2PL)\tSD"
        		+ "\t波幅(中位)\t波幅(平均)\t波幅(Max)\t波幅(Min)\t9成(波幅)\tSD"
        		
				+ "\t升跌幅中位(E2E_LH)\t升跌幅平均(E2E_LH)\tMax升跌幅(E2E_LH)\tMin升跌幅(E2E_LH)\t9成升跌幅(E2E_LH)\tSD"
				+ "\t升跌幅中位(E2E_HL)\t升跌幅平均(E2E_HL)\tMax升跌幅(E2E_HL)\tMin升跌幅(E2E_HL)\t9成升跌幅(E2E_HL)\tSD"

        		+ "\t最後一年同期\tC-C\t波幅\tC-L\tC-H\tPATTERN (CC)"
				+ "\t最近同期\tC-C \t波幅 \tC-L \tC-H \tPATTERN (CC)";
        StringBuilder sb = new StringBuilder();
        sb.append(header+"\n");
        for(int i=0; i< resultList.size(); i++) {
        	BenchmarkBeanResult sumy = resultList.get(i);          	
			sb.append(sumy.getStockCode().replace(".HK", "") );			
			sb.append("\t" + (CFGHelper.getStockProfileMap().get(sumy.getStockCode())==null?"NA":CFGHelper.getStockProfileMap().get(sumy.getStockCode()).getName())); 
			sb.append("\t" + (CFGHelper.getStockProfileMap().get(sumy.getStockCode())==null?"NA":CFGHelper.getStockProfileMap().get(sumy.getStockCode()).getSector()));
						
			sb.append("\t" + sumy.getStartMMdd());
			sb.append("\t" + sumy.getEndMMdd() );
			sb.append("\t" + sumy.getNumOfYears() );
			sb.append("\t" + sumy.getPeriodNoOfDays() );
//			sb.append("\t" + GeneralHelper.format(sumy.c2cProfitAndDaysRatio())); //性價比(C2C)
			sb.append("\t" + GeneralHelper.toPct(sumy.getRisesRatioC2C()));
			sb.append("\t" + GeneralHelper.toPct(sumy.getFallsRatioC2C()));
			sb.append("\t" + GeneralHelper.toPct(sumy.getRisesRatioC2ph()));
			sb.append("\t" + GeneralHelper.toPct(sumy.getFallsRatioC2pl()));
			sb.append("\t" + GeneralHelper.toPct(sumy.getRisesRatioEnd2EndL2H()));
			sb.append("\t" + GeneralHelper.toPct(sumy.getFallsRatioEnd2EndL2H()));			
			sb.append("\t" + GeneralHelper.toPct(sumy.getRisesRatioEnd2EndH2L()));
			sb.append("\t" + GeneralHelper.toPct(sumy.getFallsRatioEnd2EndH2L()));
			sb.append("\t" + sumy.getO2cDownYearList() );
			sb.append("\t"+ ((sumy.getLowHighDateSimplifyResult()==null)?" \t \t \t ":sumy.getLowHighDateSimplifyResult().toPrintResult())) ;
//			sb.append("\t" + sumy.getStartDateIsLowestPriceDayList());
			sb.append("\t" + GeneralHelper.toPct(sumy.getFallBackToNegativeReturnRatio()));
			sb.append("\t" + sumy.getNonNegativeRatioDates());
			
			sb.append("\t" + sumy.getC2cStat().getDescription());	
									
			sb.append("\t" + sumy.getC2phStat().getDescription());
			sb.append("\t" + sumy.getC2plStat().getDescription());
			sb.append("\t" + sumy.getPercentageRangeStat().getDescription());

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
        sb.append("\n Total number of records: " + resultList.size());
        log.info( sb.toString());
        UserCsvWriter.appendToCSV("BST.csv", sb.toString());
        log.info("\nStock Moving Average summary");
        stockMovingAvgResult.forEach(s -> System.out.println(s));
        
        log.info("\n############################################");
        List<BenchmarkBeanResult> filteredList = resultList.stream().filter(x ->x.getRisesRatioC2C()>0.75).collect(Collectors.toList());
        DateRangeOverlapAnalyst.doAnalysis(filteredList);
	}
	
	public static BenchmarkBeanResult findBenchmark(BenchmarkModule module, List<StockBean> productStockList, String MMDD_START, String MMDD_END, double minimumRatio, String end2EndPattern) throws Exception {
					
			List<BenchmarkBean> bencharmkBeanList = module.doDataRangeLogic(productStockList, MMDD_START, MMDD_END);
			if(bencharmkBeanList.size() < REQUIRED_YRS)
				return null;

			BenchmarkBean recentMonthlyPerfromance = module.getRecentBenchmarkBean(productStockList, MMDD_START, MMDD_END);
//			if(MMDD_START.equalsIgnoreCase("11-12") && MMDD_END.equalsIgnoreCase("11-30")) {
//				System.out.println("PAUSE");
//			}
			BenchmarkBeanResult stockResult = module.generateBenchmarkResult(bencharmkBeanList, recentMonthlyPerfromance, minimumRatio, REQUIRE_MIN_DAY_DURATION, end2EndPattern);
			
			if(stockResult!=null) {
				LowHighDateSimplifyResult lowHighResult = LowHighDateHelper.generate(productStockList, MMDD_START, MMDD_END);
				stockResult.setLowHighDateSimplifyResult(lowHighResult);
			}
			
			return stockResult;
	}
	
	
	public static List<BenchmarkBeanResult> findTopBenchmark(List<StockBean> productStockList, String code, String startMMDD, String endMMDD, double minimumRatio ) throws Exception {
		
		List<BenchmarkBeanResult> stockPerformanceResultList = new ArrayList<BenchmarkBeanResult>(10);
		BenchmarkModule module = new BenchmarkModule();
		List<String> allMMDDList = getAllMMDDBetweenTwoDates(startMMDD, endMMDD);
		
		List<String> allDateMMDDRange = getDateArrangeCombinations(REQUIRE_MIN_DAY_DURATION, allMMDDList);
		for (String string : allDateMMDDRange) {
			String[] mmdd = string.split("_");
			BenchmarkBeanResult result = findBenchmark(module, productStockList, mmdd[0], mmdd[1], minimumRatio, END2END_PATTERN );
			if(result!=null)
				stockPerformanceResultList.add(result);
		}
		
		
		Comparator<BenchmarkBeanResult> sortByUpRatio = (p, o) -> Double.compare(p.getRisesRatioC2C(), o.getRisesRatioC2C()); 
        Comparator<BenchmarkBeanResult> sortByC2HMedian = (p, o) -> Double.compare(p.getC2phStat().getMedian(), o.getC2phStat().getMedian());                 
        Comparator<BenchmarkBeanResult> multipleFieldsComparator2 = sortByUpRatio.reversed().thenComparing(sortByC2HMedian.reversed());
        
		
//        Comparator<BenchmarkBeanResult> sortByC2CMedian = (p, o) -> Double.compare(p.getC2cStat().getMedian(), o.getC2cStat().getMedian());         
        		
        List<BenchmarkBeanResult> descList = stockPerformanceResultList.parallelStream()
        	    .sorted(multipleFieldsComparator2)
        	    .collect(Collectors.toList());
        
        List<BenchmarkBeanResult> limitList = descList.parallelStream().limit(RESULT_LIMIT_SIZE)
        	    .collect(Collectors.toList());
//		List<BenchmarkBeanResult> descList = stockPerformanceResultList.parallelStream()
//        	    .sorted(Comparator.comparingDouble(BenchmarkBeanResult::getUpRatio).reversed()).collect(Collectors.toList());
		
		return limitList;
	}
	
	
	
	public static List<String> getAllMMDDBetweenTwoDates( String startMMDD, String endMMDD){
		LocalDate startDate = LocalDate.parse("2021-"+startMMDD);
		LocalDate endDate = LocalDate.parse("2021-"+endMMDD).plusDays(1);
		
		 
		long numOfDays = ChronoUnit.DAYS.between(startDate, endDate);
		          
		List<LocalDate> listOfDates1 = Stream.iterate(startDate, date -> date.plusDays(1))
		                                    .limit(numOfDays)
		                                    .collect(Collectors.toList());
		List<String> mmddList = new ArrayList<String>(10);
		for (LocalDate s : listOfDates1) {
			String mmdd = String.format("%02d", s.getMonthValue())+"-"+String.format("%02d", s.getDayOfMonth());
			mmddList.add(mmdd);
		} 
		
		return mmddList;
	}
	
	public static List<String> getDateArrangeCombinations(int dayGaps, List<String> intList){

		List<String> rtnList = new ArrayList<String>();
		for (int i = 0; i < intList.size(); i++) {

			String leftValue = intList.get(i);
			int jStartIdx = i + dayGaps;

			for (int j = jStartIdx; j < intList.size(); j++) {
				rtnList.add(leftValue + "_" + intList.get(j));
			}
			
		}
		return rtnList;
	}
	
}
