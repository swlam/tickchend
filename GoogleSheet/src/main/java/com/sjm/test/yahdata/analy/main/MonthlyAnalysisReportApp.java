package com.sjm.test.yahdata.analy.main;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.cfg.CNStockListConfig;
import com.sjm.test.yahdata.analy.cfg.GlobalConfig;
import com.sjm.test.yahdata.analy.cfg.HKStockListConfig;
import com.sjm.test.yahdata.analy.cfg.USStockListConfig;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.helper.CFGHelper;
import com.sjm.test.yahdata.analy.main.cycle.MonthlyPerformanceApp;
import com.sjm.test.yahdata.analy.probability.bean.BenchmarkBeanResult;
import com.sjm.test.yahdata.analy.repo.BenchmarkProbabilityAnalysisReportDataSourceManager;
import com.sjm.test.yahdata.analy.repo.StockMarketRepo;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class MonthlyAnalysisReportApp {
//	private static final Logger logger = Logger.getLogger(MonthlyAnalysisReportApp.class);


	public static  List<String> CODE_POOL = Stream.of(USStockListConfig.ALL, HKStockListConfig.ALL, CNStockListConfig.ALL_AVAILABLE).flatMap(Collection::stream) .collect(Collectors.toList());
	
	public static String COUNTRY_MARKET = "US";
	public static String targetMonth = "-12-";
	public static String BENCHMARK_BOUDARY = Const.END2END_O2C;
	
	public static int YEARS = 20;
	
	public static final boolean IS_EXPORT_FILE = true;
	
 	public MonthlyAnalysisReportApp() {}
 	
//    private static  List<String> CODE_POOL = Arrays.asList("TSLA");
//    private static  List<String> CODE_POOL = USStockListConfig.ALL;
// 	private static  List<String> CODE_POOL = HKStockListConfig.ALL;
    
    public static void main(String arg[])  throws Exception{
    	GlobalConfig.setYearOfStockData(YEARS);
    	log.info(COUNTRY_MARKET +" and " + targetMonth);
    	
    	if(Const.MARKET_US.equalsIgnoreCase(COUNTRY_MARKET)){
			CODE_POOL =   USStockListConfig.MAIN;
//			CODE_POOL =   USStockListConfig.MAIN_CORE;
//			CODE_POOL = Arrays.asList("BTC-USD");
    	}else if(Const.MARKET_HK.equalsIgnoreCase(COUNTRY_MARKET)){			
			CODE_POOL =   HKStockListConfig.MAIN;
    	}else if(Const.MARKET_CN.equalsIgnoreCase(COUNTRY_MARKET)){
			CODE_POOL = CNStockListConfig.ALL_AVAILABLE;
    	}
    	
    	
    	
    	generateMonthBenchmarkData(COUNTRY_MARKET, BENCHMARK_BOUDARY, IS_EXPORT_FILE);
    }
    
    public static void loadMonthBenchmarkData() {
    	String dataFile = GlobalConfig.getDefaultMonthlyProbPath()+COUNTRY_MARKET+targetMonth+"data.json";
    	List<BenchmarkBeanResult> resultList = BenchmarkProbabilityAnalysisReportDataSourceManager.loadDataFromDisk( dataFile);
//    	for (BenchmarkBeanResult benchmarkBeanResult : resultList) {
//    		BaseSummaryStatistics stat = benchmarkBeanResult.getC2cStat();
//    		System.out.println();
//		}
    	log.info("DONE : "+dataFile+", size = "+resultList.size());
    }
    
    
    public static void generateMonthBenchmarkData(String countryMarket, String o2cOrC2, boolean IS_EXPORT_FILE)  throws Exception{

    	log.info("Generate Report:  "+IS_EXPORT_FILE);
		log.info("start, calc data years: "+GlobalConfig.getYearOfStockData() + ", month="+targetMonth +", data size = "+CODE_POOL.size());
		
		CFGHelper.validateConfig();
		CFGHelper.loadMetaInfo(GlobalConfig.DEFAULT_DEFAULT_PATH);

		
		List<StockBean> fullTrunkList = MonthlyPerformanceApp.loadStockData(CODE_POOL, Const.INTERVAL_D);	
			
		StockMarketRepo.getTrunk().addAll(fullTrunkList);
		
		
		String exportFileName = GlobalConfig.getYearOfStockData() + "Y-" + countryMarket+targetMonth + "data.json";
		
		List<BenchmarkBeanResult> benchResultList = BenchmarkProbabilityAnalysisReportDataSourceManager.generateBenchmarkResult(targetMonth, CODE_POOL, fullTrunkList, o2cOrC2);
		
		if(IS_EXPORT_FILE) {
			String filePath = GlobalConfig.getDefaultMonthlyProbPath() + exportFileName; //10Y-TSLA-09-data.json
			BenchmarkProbabilityAnalysisReportDataSourceManager.saveDataToDisk(benchResultList, filePath);
		}
	}
	
    
    public static void saveBstBenchmarkDataToDisk(String stockCode , List<BenchmarkBeanResult> benchResultList, int yearsOfData)  throws Exception{

			String exportFileName = stockCode + "-" + yearsOfData + "Y-" + "data.json";
			String filePath = GlobalConfig.getDefaultIndividualStockProbPath() + exportFileName; //10Y-TSLA-09-data.json
			log.info(stockCode+"\t"+exportFileName+ "\t" + benchResultList.size());
			BenchmarkProbabilityAnalysisReportDataSourceManager.saveDataToDisk(benchResultList, filePath);
	}
	

	
}
