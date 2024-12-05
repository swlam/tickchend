package com.sjm.test.yahdata.analy.instantresult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sjm.test.yahdata.analy.model.InstantPerformanceResult;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class StockPickTagger {

	public static final String LONG_1 = "L1";
	public static final String LONG_2 = "L2";
	public static final String LONG_3 = "L3";
	public static final String LONG_4 = "L4";
	public static final String LONG_5 = "L5";
	public static final String LONG_6 = "L6"; //月統計佔優
//	public static final String LONG_BOOM = "Lboom";
	public static final String STAGNANT = "待變";
	public static final String STAGNANT_SHORT_MID_TERM = "待變(短中)";
	public static final String STAGNANT_SHORT_TERM = "待變(短)";
	public static final String STAGNANT_MID_TERM = "待變(中)";
	
	public static final String SHORT_1 = "S1";
	public static final String SHORT_2 = "S2";
	public static final String SHORT_3 = "S3";
	public static final String SHORT_4 = "S4"; // refer to 月統計佔優
	public static final String RSI_SIGNAL = "SIGNAL"; // Custom selection 2
	
	public static final String B1 = "B1"; // Custom selection 1
	public static final String B2 = "B2"; // Custom selection 2
	
	private static Map<String, BaseTrendGettingDirection> longTrendMap = null;
	private static Map<String, BaseTrendGettingDirection> shortTrendMap = null;
	private static Map<String, BaseTrendGettingDirection> stagnantTrendMap = null;

	
	static {
		longTrendMap = new HashMap<String , BaseTrendGettingDirection>();
		longTrendMap.put(LONG_1,new TrendGettingStrongerA());
		longTrendMap.put(LONG_2,new TrendGettingStrongerB());
		longTrendMap.put(LONG_3,new TrendGettingStrongerC());
//		trendDirectionMap.put(LONG_D,new TrendGettingStrongerD());
		longTrendMap.put(LONG_5,new TrendGettingStrongerE());
		longTrendMap.put(LONG_6,new TrendGettingStrongerF());
		longTrendMap.put(B1,new CustomBuySignalA());
		longTrendMap.put(B2,new CustomBuySignalB());
		shortTrendMap = new HashMap<String , BaseTrendGettingDirection>();
		shortTrendMap.put(SHORT_1,new TrendGettingWeakerA());
		shortTrendMap.put(SHORT_2,new TrendGettingWeakerB());
		shortTrendMap.put(SHORT_3,new TrendGettingWeakerC());
		shortTrendMap.put(SHORT_4,new TrendGettingWeakerF());
		
		stagnantTrendMap = new HashMap<String , BaseTrendGettingDirection>();
		stagnantTrendMap.put(STAGNANT,		new TrendStagnant());
		stagnantTrendMap.put(STAGNANT_SHORT_TERM,		new TrendStagnantShortTerm());
		stagnantTrendMap.put(STAGNANT_SHORT_MID_TERM,	new TrendStagnantSMTerm());
		stagnantTrendMap.put(STAGNANT_MID_TERM,		new TrendStagnantMidTerm());
		stagnantTrendMap.put(RSI_SIGNAL,		new PriceStrengthSignal());
		
	}
	
	
	public static BaseTrendGettingDirection getTrendGettingDirection(String key) {
		BaseTrendGettingDirection rtn = null;
		if(longTrendMap.get(key)!=null) {
			return longTrendMap.get(key);
		}
		
		if(stagnantTrendMap.get(key)!=null) {
			return stagnantTrendMap.get(key);
		}
		
		if(shortTrendMap.get(key)!=null) {
			return shortTrendMap.get(key);
		}
		return rtn;
	}
	
	
	public String getTodayLongTrendTag(InstantPerformanceResult x) {
		String msg= "";
		try {
			msg= getTodayTrendTag(x, longTrendMap);
		}catch(Exception e) {			
			log.warn(x.getCurrentStockBean().getStockCode()+" "+e.getMessage());
		}
		return msg;
	}
	
	public String getTodayStagnantTrendTag(InstantPerformanceResult x) {
		
		String msg= "";
		try {
			msg= getTodayTrendTag(x, stagnantTrendMap);
		}catch(Exception e) {
			log.warn(x.getCurrentStockBean().getStockCode()+" "+e.getMessage());		}
		return msg;
	}
	
	public String getTodayShortTrendTag(InstantPerformanceResult x) {
		String msg= "";
		try {
			msg= getTodayTrendTag(x, shortTrendMap);
		}catch(Exception e) {
			log.warn(x.getCurrentStockBean().getStockCode()+" "+e.getMessage());		}
		return msg;
	}
	
	public String getTodayTrendTag(InstantPerformanceResult x, Map<String , BaseTrendGettingDirection> trendMap) {
		
		Set<String> resultSet = new HashSet<String>();
		Iterator<BaseTrendGettingDirection> iterator = trendMap.values().iterator();
	    while (iterator.hasNext()) {
	    	BaseTrendGettingDirection elemt = iterator.next();
	    	String isHitResult = elemt.goAnalyze(x);
			if(isHitResult!=null) {
				resultSet.add(isHitResult);
			}
	    }
	    return resultSet.toString();
		
	}
	
	

}
