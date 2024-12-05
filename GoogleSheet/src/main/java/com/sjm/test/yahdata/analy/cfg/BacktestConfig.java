package com.sjm.test.yahdata.analy.cfg;

public class BacktestConfig {

	//CandleTagConst
	public static boolean isPrintCandleTag = false;

	public static int FEW_DAY_START = 5;
	public static int FEW_DAY_END = 20;

	//print backtest result list
	public static int PRINT_NUMBER_OF_RECORD = 3;
	
	public static boolean IS_ABV = false;
	public static boolean IS_BLW = false;
	
	public static MAValidateCfgBean MA_VALIDATE_CFG = new MAValidateCfgBean(50,200, IS_ABV, IS_BLW);
//	public static MAValidateCfgBean ma200Cfg = new MAValidateCfgBean(200, IS_ABV, IS_BLW);
	
}
