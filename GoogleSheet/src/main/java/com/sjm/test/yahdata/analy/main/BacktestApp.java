package com.sjm.test.yahdata.analy.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.VolumePriceBean;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.cfg.BacktestConfig;
import com.sjm.test.yahdata.analy.cfg.GlobalConfig;
import com.sjm.test.yahdata.analy.cfg.USStockListConfig;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.helper.CFGHelper;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.ta.EventRuleHandler;
import com.sjm.test.yahdata.backtest.BacktestBenchmarkBeanResult;
import com.sjm.test.yahdata.backtest.BacktestModule;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class BacktestApp extends BaseApp {

	
    public static boolean IS_USE_RECENT_PATTERN = false;

//	 public static List<String> TICKER_POOL= Stream.of(USStockListConfig.SOFTWARE,
//			 USStockListConfig.QQQ_ELEMENTS 
//				)
//	    .flatMap(Collection::stream).distinct()
//	    .collect(Collectors.toList());

//	public static List<String> TICKER_POOL = Arrays.asList("SPY");
	public static List<String> TICKER_POOL = Stream.of( //USStockListConfig.ETF, 
//			USStockListConfig.QQQ_COMPONENTS, 
//			USStockListConfig.SPX_COMPONENTS, 
//			USStockListConfig.ETF,
//			USStockListConfig.CHINA_CONCEPT,
			USStockListConfig.magnificent)
    .flatMap(Collection::stream).distinct()
    .collect(Collectors.toList());

//    public static List<String> TICKER_POOL = USStockListConfig.ETF;

	public static String startDate = "2000-01-01";
	public static String endDate = "2025-01-30";
	
	public BacktestApp() {
	}

	public static void main(String args[]) {
//		GlobalConfig.DEFAULT_DOWNLOAD_INTERVAL = "D";
		String interval = Const.INTERVAL_D; 
		List<StockBean> fullTrunkList = loadStockData(TICKER_POOL, interval, startDate, endDate);
		CFGHelper.loadMetaInfo(GlobalConfig.DEFAULT_DEFAULT_PATH);
		
		log.info("ticker size: " + TICKER_POOL.size());

		BacktestModule backtest = new BacktestModule();
		EventRuleHandler ruleHandler = new EventRuleHandler();

		
		List<BacktestBenchmarkBeanResult> fullResultList = new ArrayList<BacktestBenchmarkBeanResult>();
		
//		 List<CandleTagEnum> BACKTEST_PATTERN_ARY =	Arrays.asList(CandleTagEnum.values()); 
		 List<CandleEventTagEnum> BACKTEST_PATTERN_ARY= Arrays.asList(
//				 CandleTagEnum.EVNT_GO_STRONG//.EVNT_PRICE_VOL_PUSH_UP
//				 ,CandleTagEnum.EVNT_GO_WEAK//.EVNT_PRICE_VOL_PUSH_UP
//				 CandleTagEnum.EVNT_GBUB
				 CandleEventTagEnum.EVNT_GAP_UP
//				 CandleTagEnum.EVNT_DAY_BEFORE_THANKSGIVING
//				 CandleTagEnum.EVNT_GAP_UP_2_PCT_PLUS
			);
		 

		for (String code : TICKER_POOL) 
		{
			Set<CandleEventTagEnum> recentCandleTagSet = new HashSet<CandleEventTagEnum>(10);
			try {
				List<StockBean> trunkList = StreamTransformHelper.extractByStockCode(fullTrunkList, code);
				List<VolumePriceBean> vpStockList = ruleHandler.goThruRules(trunkList, BACKTEST_PATTERN_ARY);

				
				// print 近期Price Vol情況
				int cnt = 0;
				if (vpStockList != null && vpStockList.size() < 2) {
					cnt = vpStockList.size() - 1;
				} else if (vpStockList != null && vpStockList.size() >= 4) {
					cnt = BacktestConfig.PRINT_NUMBER_OF_RECORD;
				}

				String txt = "";
				int idx = 0;
				
				for (int i = vpStockList.size() - 1; i >= 0; i--) {
					txt += vpStockList.get(i) + "\n"; 
					recentCandleTagSet.addAll(vpStockList.get(i).getSignSet());
					
					idx++;
					if (idx == cnt)
						break;

				}
				if(IS_USE_RECENT_PATTERN == false) {
					recentCandleTagSet.clear();
//					recentCandleTagSet.add(CandleTagEnum.EVNT_STATS_LOWER_SHADOW);
					recentCandleTagSet.addAll(BACKTEST_PATTERN_ARY);
					
				}
				
				
				
				log.info("BACK-TEST config: " + BacktestConfig.MA_VALIDATE_CFG);
				log.info("\n"+code+" 近期情況:\n" + txt);
				

				List<BacktestBenchmarkBeanResult> unsortResultList = new ArrayList<BacktestBenchmarkBeanResult>();
				for (CandleEventTagEnum candleEventTagEnum : recentCandleTagSet) {

					BacktestBenchmarkBeanResult r = backtest.goThruBacktestBenchmark(trunkList, vpStockList, candleEventTagEnum);
					if (r != null && r.getCntAll() > 0.0)
						unsortResultList.add(r);
				}

				backtest.performOccurence(trunkList, vpStockList); 
				
				
				Comparator<BacktestBenchmarkBeanResult> sortByLastTxnDateDesc = Comparator.comparing(BacktestBenchmarkBeanResult::getLastTxnDate).reversed();
				List<BacktestBenchmarkBeanResult> resultList = unsortResultList.parallelStream().sorted(sortByLastTxnDateDesc).collect(Collectors.toList());;

				fullResultList.addAll(resultList);
				
			} catch (Exception e) {
				log.error(null, e);
			}
		}// end Ticker pool loop
		
		///////////// print result ///////////
		
		StringBuilder sb = new StringBuilder();

		String header = "\nBenchmarks Reulst: " + BacktestConfig.MA_VALIDATE_CFG + 
				"\nCode\tName\tPattern\tYrs\t上升比例\t正回報次數\t出現總次數\t交易日數\tLast Meet Date"
				+ "\t中位(C-C)\tAvg(C-C)\tMax(C-C)\tMin(C-C)\t9成升幅\tSD"
				
				
				+ "\t中位(C-H)\tAvg(C-H)\tMax(C-H)\tMin(C-H)\t9成升幅\tSD" 
				+ "\t中位(C-L)\tAvg(C-L)\tMax(C-L)\tMin(C-L)\t9成升幅\tSD"
				+ "\t中位(L-H)\tAvg(L-H)\tMax(L-H)\tMin(L-H)\t9成升幅\tSD";
//				+ "\t中位(E-E,LH)\tAvg(E-E,LH)\tMax(E-E,LH)\tMin(E-E,LH)\t9成升幅\tSD";
		sb.append(header);
		for (BacktestBenchmarkBeanResult s : fullResultList) {
			sb.append("\n" + s.getCode() 
			+ "\t" + (CFGHelper.getStockProfileMap().get(s.getCode())==null?"NA":CFGHelper.getStockProfileMap().get(s.getCode()).getName())
//			+ "\t" + (CFGHelper.getStockMetaMap().get(s.getCode())==null?"NA":CFGHelper.getStockMetaMap().get(s.getCode()).getSector())
			+ "\t" + s.getStrategyPattern() + "\t" + s.getNumOfYears() //+ "\t" +s.getNoOfTxDays() 
//			+ "\t" + GeneralHelper.to100Percent((double)s.getCntAll()/(double)s.getNoOfTxDays())
			+ "\t" + GeneralHelper.toPct(s.getUpRatio()) 
			+ "\t" + s.getCntUp() + "\t" + s.getCntAll()
			+ "\t" + s.getDays() + "\t" + s.getLastTxnDate() 
					
			+ "\t" + s.getC2cStat().getDescription() 
			+ "\t" + s.getC2phStat().getDescription()
			+ "\t" + s.getC2plStat().getDescription() 
			+ "\t" + s.getPercentageRangeStat().getDescription()
//			+ "\t" + s.getEnd2EndStat().getDescription()
			);
		}
		 
		
		
		log.info(sb.toString());
		log.debug("Application DONE");

	}

}
