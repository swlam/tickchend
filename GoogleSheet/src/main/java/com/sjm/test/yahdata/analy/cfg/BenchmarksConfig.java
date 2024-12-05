package com.sjm.test.yahdata.analy.cfg;

import java.util.Arrays;
import java.util.List;

public class BenchmarksConfig {

	public static List<String> WATCH_LIST 
	= Arrays.asList("AAPL","FB","MSFT","AMZN","GOOGL",
			"TWTR","SHOP","SQ","SE","ADBE","TSLA","TSM",
			"NVDA","AMD","SPGI","NKE","NFLX","DIS",
			"WFC","JPM","BLK","LULU");//,"LVMUY");
	
	public static List<String> ETF_POOL = Arrays.asList(
			"DIA","SPY","XLB","XLC","XLE","XLF","XLK","XLP","XLRE","XLU","XLV","XLY","XSD","QQQ","BTC-USD","^VIX","DX=F","^TNX","SOXX","ARKK","ARKW","GDX","COPX");
	
	/*Sector ETF
	XLB	//Materials Sector(LIN,SHW)原材料
	XLC	//通訊服務類股,Communication Services (FB,GOOGL,GOOG,CMCSA,DIS, AT&T)
	XLE	//SPDR能源類股ET
	XLF	//Financial  Sector (BRK.B, JPM, BAC, WFC)
	XLI	//Industrial Sector工業
	XLK	//Technology  Sector (AAPL,MSFT,NVDA,V,MA,PYPL) 
	XLP	//consumer staple 必需性消費 (PG, KO, PEP,WMT,COST) 
	XLRE	// Real  Estate
	VNQ// 跟蹤的是摩根士丹利的美國房地產投資信託基金指數（the MSCI US REIT Index）
	XLU	//Utilities,公共事業指數
	XLV	//health care(JNJ, UNH PFE)
	XLY	//非必需性消費 (AMZN,TSLA, HD, NKE,MCD)
	XSD semi conductor
	*/
	public static List<String> STOCK_HK_POOL = Arrays.asList(
			"0027.HK","0175.HK","0388.HK","0688.HK","0669.HK","0700.HK","0823.HK","0939.HK","0941.HK",
			"0968.HK","0992.HK",
			"1024.HK","1211.HK","1299.HK","1347.HK","1398.HK","1658.HK","1810.HK", "1883.HK",
			"1772.HK",
			"1928.HK","2020.HK","2196.HK","2269.HK","2331.HK",
			"2318.HK","2331.HK","2382.HK",
			"3690.HK","3968.HK"	,"^HSI",
			"^HSCE",			"000001.SS"
			);
		
	
	public static List<Integer> PAST_NUM_OF_DAYS = Arrays.asList(1,3,5,10,15,20);
	public static final String START_DATE = "2021-08-01";
	
	
//	public static List<String> STOCK_FULL_POOL =ListUtils.union(STOCK_HK_POOL, STOCK_US_POOL);
	
	public static List<String> LEAD_HORSE = Arrays.asList("0189.HK","1811.HK","2380.HK","1071.HK","0855.HK",
															"2343.HK","2039.HK","0956.HK","3998.HK","1618.HK",
															"3818.HK","3759.HK","1963.HK","1381.HK","1308.HK",															
															"1108.HK","1088.HK","0991.HK","0902.HK",//"0606.HK",
															"0568.HK","0215.HK","0135.HK");
	
}
