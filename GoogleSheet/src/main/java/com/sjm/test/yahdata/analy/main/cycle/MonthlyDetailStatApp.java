package com.sjm.test.yahdata.analy.main.cycle;

import java.util.Arrays;
import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.cfg.GlobalConfig;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.helper.CFGHelper;
import com.sjm.test.yahdata.analy.helper.VSToolHelper;
import com.sjm.test.yahdata.analy.main.BaseApp;
import com.sjm.test.yahdata.analy.module.lowhighdist.LowHighDateDistributionModule;
import com.sjm.test.yahdata.analy.probability.MonthlyVolatilityAnalyst;
import com.sjm.test.yahdata.analy.repo.StockMarketRepo;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class MonthlyDetailStatApp extends BaseApp{
	
	
	private static final String INTERAL = Const.INTERVAL_D;

	public static String TARGET_MONTH = "-03-";
	public static String TARGET_MONTH_START_DATE = "03-01";//"04-30";
	public static String TARGET_MONTH_END_DATE = "04-01";
	
	public static String STOCK_CODE = "MSCI";//BacktestConfig.DEFAULT_STOCKCODE;
	
	public static void main(String[] args) throws Exception{
		CFGHelper.validateConfig();

		try {

			
			List<StockBean> trunkList = CFGHelper.getStockRawData(GlobalConfig.getDefaultDownloadPath(INTERAL), STOCK_CODE);
			StockMarketRepo.getTrunk().addAll(trunkList);
			
			trunkList = StockMarketRepo.getTrunk(STOCK_CODE);
//			String initialTxnDate = fullTrunkList.get(0).getTxnDate();SE
			String beginDate = trunkList.get(0).getTxnDate();
			String stockCode = trunkList.get(0).getStockCode();
			log.info("\n"+stockCode+" Load data, select data from "+beginDate +" \n");
			
			VSToolHelper  hepler = new VSToolHelper();
//			hepler.exportResultMonthToMonth(trunkList, "-06-","-07-");
//			hepler.exportResult(trunkList, "06-01", "06-30");
			LowHighDateDistributionModule sdr = new LowHighDateDistributionModule();
			sdr.exportResult(trunkList, TARGET_MONTH_START_DATE, TARGET_MONTH_END_DATE);

			//Print Monthly Volatility
			MonthlyVolatilityAnalyst monthAnalyst = new MonthlyVolatilityAnalyst();
//			monthAnalyst.exportResult(stockCode, TARGET_MONTH);			
			monthAnalyst.testExport12MonthProbability(stockCode,beginDate,  Arrays.asList(TARGET_MONTH));
			
			log.info("\n************* Original data size: "+trunkList.size()+", Actual data size: "+trunkList.size()+", Begin Date:"+beginDate+" ************* ");
			
			//find the Buy time High probability
//			RangePeriodAnalyst rangePeriodAnalyst = new RangePeriodAnalyst();
//			rangePeriodAnalyst.exportResult(stockCode);

			
		} catch (Exception e) {
			log.error(null, e);
		}		
		
	}

}
