package com.sjm.test.yahdata.analy.main;

import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.api.LitePerformanceService;
import com.sjm.test.yahdata.service.LiteWatchListRecentService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Deprecated
public class LitePerformanceApp extends BaseApp{
	
	private static String DEFAULT_INTERVAL = Const.INTERVAL_D;

	public static String targetMonth = "-11-";
	public static String END_DATE = "2028-10-14";


	public static String COUNTRY_MARKET = "US";//defaul.t



	public static void main(String[] args) throws Exception{
//		GlobalConfig.setYearOfStockData(YEARS);

		COUNTRY_MARKET = "US";
//		COUNTRY_MARKET = "CN";
		COUNTRY_MARKET = "HK";
		Const.IS_INTRADAY = true;

		LitePerformanceService service = new LitePerformanceService();
		service.setEndDate(END_DATE);
		service.doFunction(COUNTRY_MARKET, DEFAULT_INTERVAL, targetMonth);

	}
	
//	public static void preSet() {
//		Logger converterLogger = Logger.getLogger("org.apache.commons.beanutils.converters.DoubleConverter");

//        converterLogger.setLevel(Level.ERROR);




//	}
}
