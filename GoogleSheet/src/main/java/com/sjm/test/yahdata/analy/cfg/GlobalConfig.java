package com.sjm.test.yahdata.analy.cfg;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sjm.test.yahdata.analy.conts.Const;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class GlobalConfig {
//	public static String DEFAULT_STOCKCODE = "GOOGL";//"^HSI";//"0700.HK";
	//MSFT： 2014, AAPL: 2011
//	private static final Logger logger = Logger.getLogger(GlobalConfig.class);

	
	public static final int LAST_DATE_OF_MONTH = 29;
	

	public static String STRATEGRY = Const.STRATEGY_FILTER_NONE;
	
	public static final boolean TO_PERCENTAGE_ENABLE = false;
	public static final Double REQUIRE_MINIMUM_C2C_RATIO = null;
	
//	public static final String DEFAULT_DEFAULT_PATH = "/Users/sam2lam/development/stock_respository/";	
	
	public static String DEFAULT_DEFAULT_PATH = "C:/Development/workspace/stock_respository/";

//	public static String DEFAULT_DOWNLOAD_INTERVAL = "D";//M/W
//	public static final String DEFAULT_DOWNLOAD_PATH = DEFAULT_DEFAULT_PATH+"Download/"+DEFAULT_DOWNLOAD_INTERVAL+"/";
	
	public static String getDefaultDownloadPath(String interval) {
		return DEFAULT_DEFAULT_PATH+"Download/"+interval+"/";	
	}
	public static String getDefaultMonthlyProbPath() {
		return DEFAULT_DEFAULT_PATH+"MonthlyProb/";	
	}
	
	public static String getDefaultIndividualStockProbPath() {
		return DEFAULT_DEFAULT_PATH+"IndividualProb/";	
	}
	
	
	public static String getDefaultStockProfilePath() {
		return DEFAULT_DEFAULT_PATH+"Tickers/";	
	}
//	public static String getDefaultDownloadPath() {
//		return DEFAULT_DEFAULT_PATH+"Download/"+Const.INTERVAL_D+"/";	
//	}
//	
	public static List<String> TARGET_MONTH = Arrays.asList("-12-");
//	public static List<String> TARGET_MONTH = Arrays.asList("-09-","-10-","-11-");
	
	private static int STOCK_START_YEAR = 2010;
	private static int YEAR_OF_STOCK_DATA = 10;
	
	public static List<String> POOL_HK = HKStockListConfig.ALL;  
	public static List<String> POOL_US = USStockListConfig.ALL;
	public static List<String> POOL_CN = CNStockListConfig.ALL;
	
	public static List<String> ETF_POOL = Stream.of(USStockListConfig.ETF ,USStockListConfig.INDEX,
			HKStockListConfig.STOCK_INDEX,USStockListConfig.CURRENCY,USStockListConfig.CRYPTO,
			HKStockListConfig.ETF).flatMap(Collection::stream).distinct().collect(Collectors.toList());


	static {

		STOCK_START_YEAR = LocalDate.now().minusYears(YEAR_OF_STOCK_DATA).getYear();
		String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac")) {
            log.info("This is a macOS system.");
            DEFAULT_DEFAULT_PATH = "/Users/sam2lam/development/stock_respository/";

        } else if (os.contains("win")) {
        	DEFAULT_DEFAULT_PATH = "C:/Development/workspace/stock_respository/";
        } else {
        	log.info("This is neither a macOS nor a Windows system.");

        }

	}
	
	public static int getStockDataStartYear() {
		return STOCK_START_YEAR;
	}
		
	public static int getYearOfStockData() {
		log.info("STOCK_START_YEAR = "+STOCK_START_YEAR );
		return YEAR_OF_STOCK_DATA;
	}
	public static void setYearOfStockData(int yearOfStockData) {
		YEAR_OF_STOCK_DATA = yearOfStockData;
		STOCK_START_YEAR = LocalDate.now().minusYears(yearOfStockData).getYear();
		log.info("STOCK_START_YEAR = "+STOCK_START_YEAR );
	}
}
