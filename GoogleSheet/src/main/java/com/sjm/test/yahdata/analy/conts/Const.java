
package com.sjm.test.yahdata.analy.conts;

public class Const {

	public static boolean VERBOSE_ENABLE = false;
	public static boolean IS_INTRADAY = false; 
	
	public final static String MARKET_US = "US";
	public final static String MARKET_HK = "HK";
	public final static String MARKET_CN = "CN";
	
	public final static String INTERVAL_D = "D";
	public final static String INTERVAL_W = "W";
	public final static String INTERVAL_M = "M";
	
	public static final String CALC_START = Const.L;
	public static final String CALC_END = Const.H;
	
	public static final int CHART_SHOW_DAYS = 50;
//	public static String CALC_FALL_START = Const.H;
//	public static String CALC_FALL_END = Const.L;
	
	public static final String O = "O";
	public static final String C = "C";
	public static final String L = "L";
	public static final String H = "H";

	public static final String UP = "Up";
	public static final String DOWN = "Dn";
	public static final String STEADY = "STEADY";
	public static final String D0 = "D0";
	public static final String D1 = "D1";

	public static final String WAIT = "待";
	public static final String TOP = "TOP";
	public static final String BOT = "BOT";

	public static final String END2END_C2C = "CC";
	public static final String END2END_C2L = "CL";
	public static final String END2END_C2H = "CH";
	public static final String END2END_L2H = "LH";
	public static final String END2END_L2C = "LC";
	public static final String END2END_H2L = "HL";
	public static final String END2END_O2O = "OO";
	public static final String END2END_O2C = "OC";
	public static final String END2END_O2H = "OH";
	
	public static final String O2PH = "O2PH";
	public static final String O2PL = "O2PL";
	public static final String C2PH = "C2PH";//C2H c to period h
	public static final String C2PL = "C2PL";
	public static final String PCT_RANGE = "PCT-RANGE";
	
	public static final String STRATEGY_FILTER_UPSIDE = "UPSIDE";
	public static final String STRATEGY_FILTER_DOWNSIDE = "DOWNSIDE";
	public static final String STRATEGY_FILTER_NONE = "NONE";
	
	
//	public final static String MA_TYPE_CLOSE = "MA_C";
//	public final static String MA_TYPE_VOLUME = "MA_V";

	public final static String EMPTY = "";
	public final static String SPACE = " "; // if use empty string "", it will get the incorrect result from the function xxx.contains(...) 
	public final static String NA = "NA";
	public final static String Y = "Y";
	public final static String N = "N";
	
	
	
}
