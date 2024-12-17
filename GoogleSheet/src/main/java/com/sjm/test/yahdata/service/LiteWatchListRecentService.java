package com.sjm.test.yahdata.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import com.skpk.GoogleSheetsCreateAndUploadExample;
import org.springframework.stereotype.Service;

import com.maas.util.GeneralHelper;
import com.maas.util.MarketValueDifferenceCalculator;
import com.sjm.test.yahdata.analy.bean.StockProfileBean;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.cfg.CNStockListConfig;
import com.sjm.test.yahdata.analy.cfg.GlobalConfig;
import com.sjm.test.yahdata.analy.cfg.HKStockListConfig;
import com.sjm.test.yahdata.analy.cfg.USStockListConfig;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.helper.CFGHelper;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.instantresult.StockPickTagger;
import com.sjm.test.yahdata.analy.model.InstantPerformanceResult;
import com.sjm.test.yahdata.analy.model.StatisticsResult;
import com.sjm.test.yahdata.analy.module.benchmarks.BenchmarksPrograms;
import com.sjm.test.yahdata.analy.module.lowhighdist.bean.LowHighDateSimplifyResult;
import com.sjm.test.yahdata.analy.probability.bean.BenchmarkBeanResult;
import com.sjm.test.yahdata.analy.report.DailySummaryReport;
import com.sjm.test.yahdata.analy.ta.helper.SectorAnalystHelper;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class LiteWatchListRecentService extends BaseApp{
	
	public static final String START_DATE = "1990-01-01";
	public static String END_DATE = "2028-11-06";
	
	private static boolean isRunSummaryStat = false;
	
	private static int NO_OF_DAYS_PROCESS = 1;

	public static String ICONIC_CODE = "";
	public static String BASE_STOCK_B = "";
	public static String BASE_STOCK_C = "";
	public static String BASE_STOCK_D = "";
		
	public static int YEARS_DATA_10 = 10;
	public static int YEARS_DATA_20 = 20;
	
	public static final DateFormat csvFileFormatter =  new SimpleDateFormat("MM-dd HH:mm");
	
//	public LiteWatchListRecentService(String endDate) {
//		END_DATE = endDate;
//	}

	public void setEndDate(String endDate) {
		END_DATE = endDate;
	}
	
	public void doFunction(String COUNTRY_MARKET, String DEFAULT_INTERVAL, String targetMonth) throws Exception{
		CFGHelper.validateConfig();
		
		if(Const.MARKET_US.equalsIgnoreCase(COUNTRY_MARKET)){
			CODE_POOL =   USStockListConfig.ALL;
//			CODE_POOL =   USStockListConfig.MAIN;
//			CODE_POOL =   Stream.of(USStockListConfig.QQQ_COMPONENTS).flatMap(Collection::stream) .collect(Collectors.toList());S

//			CODE_POOL = Arrays.asList("RDDT","QQQ","DIA","SPY");//USStockListConfig.ETF;

			ICONIC_CODE = "SPY";
			BASE_STOCK_B = "QQQ";
			BASE_STOCK_C = "DIA";
			BASE_STOCK_D = "DX=F";
		}else if(Const.MARKET_HK.equalsIgnoreCase(COUNTRY_MARKET)){			
			CODE_POOL =   HKStockListConfig.ALL;
//			CODE_POOL =   HKStockListConfig.ETF;
//			CODE_POOL = Arrays.asList("9988.HK","2800.HK","2822.HK","3033.HK");//USStockListConfig.ETF;

			ICONIC_CODE = "2800.HK";  
			BASE_STOCK_B = "2822.HK";
			BASE_STOCK_C = "3033.HK";
			BASE_STOCK_D = "DX=F";
		}else if(Const.MARKET_CN.equalsIgnoreCase(COUNTRY_MARKET)){
			CODE_POOL = CNStockListConfig.ALL_AVAILABLE;
			
//			CODE_POOL = Arrays.asList("300750.SZ","000001.SS","000300.SS","399001.SZ", "399006.SZ");
			ICONIC_CODE = "000001.SS";  
			BASE_STOCK_B = "399001.SZ";
			BASE_STOCK_C = "000300.SS";
			BASE_STOCK_D = "399006.SZ";
		}
		
		CFGHelper.loadMetaInfo(GlobalConfig.DEFAULT_DEFAULT_PATH);
		CFGHelper.loadMonthBenchmarkData(COUNTRY_MARKET, YEARS_DATA_10, targetMonth);
		CFGHelper.loadMonthBenchmarkData(COUNTRY_MARKET, YEARS_DATA_20, targetMonth);
		CFGHelper.loadIndividualBenchmarkData(CODE_POOL, YEARS_DATA_20);

		log.info("Process MARKET: "+COUNTRY_MARKET+ ", No of stocks: "+CODE_POOL.size() +", Is Intraday: "+ Const.IS_INTRADAY);
		
//		LiteWatchListRecentService app = new LiteWatchListRecentService();
		List<StockBean> fullTrunkList = loadStockData(CODE_POOL, DEFAULT_INTERVAL);
		log.debug("Load stock "+COUNTRY_MARKET+" data finished.");
		BenchmarksPrograms bchkProgram = new BenchmarksPrograms();
		
		List<InstantPerformanceResult> instantPerformanceResultList = null;
		List<StatisticsResult> statisticsResultList = new ArrayList<StatisticsResult>();
		
		List<StockBean> spyStockList = StreamTransformHelper.extractDataByStockCodeAndPeriod(fullTrunkList, ICONIC_CODE, START_DATE, END_DATE);

		StockBean lastBean = spyStockList.get(spyStockList.size()-1);
		if(isRunSummaryStat) {
//			
			
			int startIdx = spyStockList.size()-NO_OF_DAYS_PROCESS; // calculate4 one day
			log.debug("Process: "+NO_OF_DAYS_PROCESS+" days, Period: "+ DEFAULT_INTERVAL +", Market: "+COUNTRY_MARKET);

			for( int i=startIdx; i<spyStockList.size(); i++) {
				StockBean spyCurrent = spyStockList.get(i);
//				END_DATE = spyCurrent.getTxnDate();
				int endDateInt = spyCurrent.getTxnDateInt();
				int parseEndDate = Integer.parseInt(END_DATE.replaceAll("-", ""));
				
				if( parseEndDate > spyCurrent.getTxnDateInt()) {
					endDateInt = parseEndDate;
				}
					
				
				log.debug("Process: "+START_DATE+" to "+END_DATE);
				List<StockBean> stockPeriodList = StreamTransformHelper.extractData(fullTrunkList, START_DATE, END_DATE);
				
				
				instantPerformanceResultList = bchkProgram.doBenchmarks(CODE_POOL, stockPeriodList, DEFAULT_INTERVAL, endDateInt);
//				StatisticsResult statResult = this.doStatisticsResultProcess(instantPerformanceResultList);
//				statisticsResultList.add(statResult);
			}
		}else {
			spyStockList.clear();
			spyStockList = StreamTransformHelper.extractData(fullTrunkList, ICONIC_CODE, START_DATE, END_DATE);
			List<StockBean> stockPeriodList = StreamTransformHelper.extractData(fullTrunkList, START_DATE, END_DATE);
			instantPerformanceResultList = bchkProgram.doBenchmarks(CODE_POOL, stockPeriodList, DEFAULT_INTERVAL, lastBean.getTxnDateInt());
		}
		
		
		
		if(instantPerformanceResultList==null) {
			System.out.println("NO data EXPORT");
			return;
		}
			log.info("Interval (D/W/M): "+DEFAULT_INTERVAL);
//			lastBean.getTxnDateInt()
			List<InstantPerformanceResult> results = instantPerformanceResultList.stream().filter(x-> x.getCurrentStockBean().getTxnDateInt() == lastBean.getTxnDateInt()).toList();
			this.exportSimple(results, COUNTRY_MARKET, DEFAULT_INTERVAL, targetMonth);

			if(!statisticsResultList.isEmpty())
				DailySummaryReport.exportStat(statisticsResultList);

		DailySummaryReport.printSimpleStatisticsResult(results, COUNTRY_MARKET, ICONIC_CODE);
		//export the stock pick result 
//		exportStockPickResult(instantPerformanceResultList);
		System.out.println();
	}
	

	@Deprecated
	public static StatisticsResult doStatisticsResultProcess(List<InstantPerformanceResult> performanceResultList) {
		List<InstantPerformanceResult> targetResultList = performanceResultList.stream()
			.filter(x-> x.getEstTradeAmount() >1.0 && x.getSector() !=null	&& !(x.getSector().contains("ETF")|| x.getSector().contains("指數") || x.getSector().contains("貨幣") || x.getSector().contains("Crypto") || x.getSector().contains("債券"))
			).collect(Collectors.toList());
		
		List<InstantPerformanceResult> strongList = targetResultList.stream().filter(x->x.getStrongWeakTypeToday().getType().contains("->強") ).collect(Collectors.toList());
		List<InstantPerformanceResult> weakList = targetResultList.stream().filter(x->x.getStrongWeakTypeToday().getType().contains("->弱")).collect(Collectors.toList());

		long countAbv20D = targetResultList.stream().filter(c -> c.getAbv20D()>0).count();
		long countAbv50D = targetResultList.stream().filter(c -> c.getAbv50D()>0).count();
		long countAbv100D = targetResultList.stream().filter(c -> c.getAbv100D()>0).count();
		long countAbv200D = targetResultList.stream().filter(c -> c.getAbv200D()>0).count();
		
		long countRsi9Abv50 = targetResultList.stream().filter(c -> c.getRsi9()>0.5).count();
		
//		long countAbv2D = targetResultList.stream().filter(c -> c.isAbv2D()==true).count();
//		long countAbv19D = targetResultList.stream().filter(c -> c.isAbv19D()==true).count();
		
		long count50DABV200D = targetResultList.stream().filter(c -> c.getMa50200DStatus().contains("50 > 200")).count();
		long count2DABV19D = targetResultList.stream().filter(c -> c.getMa219DStatus().contains("2 > 19")).count();
		
		long stockDailyPositiveReturnCnt = targetResultList.stream().filter( c-> c.getCurrentStockBean().getDayChgPct()>0.01).count();
		long stockDailyNegativeReturnCnt = targetResultList.stream().filter( c-> c.getCurrentStockBean().getDayChgPct()<-0.01).count();
		
		double ratioDailyPosiReturn = (double)stockDailyPositiveReturnCnt / (double)targetResultList.size();
		double ratioDailyNegReturn = (double)stockDailyNegativeReturnCnt / (double)targetResultList.size();

		//posiReturn with Portion ---- START
		long cntHSIs = targetResultList.stream().filter(x -> x.getBelongETF().contains(SectorAnalystHelper.CODE_INDEX_HSI)).count();
		long upCntHSIs = targetResultList.stream().filter( x-> x.getCurrentStockBean().getDayChgPct()>0.01 && x.getCurrentStockBean().getDayVolumeChgPct()>1.2 && x.getBelongETF().contains(SectorAnalystHelper.CODE_INDEX_HSI)).count();
		
		long cntDualCounter = targetResultList.stream().filter(x -> x.getBelongSouthbound().contains(SectorAnalystHelper.LABEL_HK_DUAL_COUNTER_CNH)).count();
		long upCntDualCounter = targetResultList.stream().filter( x-> x.getCurrentStockBean().getDayChgPct()>0.01 && x.getCurrentStockBean().getDayVolumeChgPct()>1.2 && x.getBelongSouthbound().contains(SectorAnalystHelper.LABEL_HK_DUAL_COUNTER_CNH)).count();
		
		long cntHSTechs = targetResultList.stream().filter(x -> x.getBelongETF().contains(SectorAnalystHelper.CODE_ETF_HK_TECH)).count();
		long upCntHSTechs = targetResultList.stream().filter( x-> x.getCurrentStockBean().getDayChgPct()>0.01 && x.getCurrentStockBean().getDayVolumeChgPct()>1.2 && x.getBelongETF().contains(SectorAnalystHelper.CODE_ETF_HK_TECH)).count();
		
		long cntRedChps = targetResultList.stream().filter(x -> x.getBelongETF().contains(SectorAnalystHelper.LABEL_HK_RED_CHIP)).count();
		long upCntRedChips = targetResultList.stream().filter( x-> x.getCurrentStockBean().getDayChgPct()>0.01 && x.getCurrentStockBean().getDayVolumeChgPct()>1.2 && x.getBelongETF().contains(SectorAnalystHelper.LABEL_HK_RED_CHIP)).count();
		
		long cntSPY = targetResultList.stream().filter(x -> x.getBelongETF().contains(SectorAnalystHelper.CODE_ETF_SPY)).count();
		long upCntSPY = targetResultList.stream().filter( x-> x.getCurrentStockBean().getDayChgPct()>0.01 && x.getBelongETF().contains(SectorAnalystHelper.CODE_ETF_SPY)).count();
		
		long cntQQQ = targetResultList.stream().filter(x -> x.getBelongETF().contains(SectorAnalystHelper.CODE_ETF_QQQ)).count();
		long upCntQQQ = targetResultList.stream().filter( x-> x.getCurrentStockBean().getDayChgPct()>0.01 && x.getBelongETF().contains(SectorAnalystHelper.CODE_ETF_QQQ)).count();
		
		long cntDIA = targetResultList.stream().filter(x -> x.getBelongETF().contains(SectorAnalystHelper.CODE_ETF_DIA)).count();
		long upCntDIA = targetResultList.stream().filter( x-> x.getCurrentStockBean().getDayChgPct()>0.01 && x.getBelongETF().contains(SectorAnalystHelper.CODE_ETF_DIA)).count();
		
		long cntChinaConcept = targetResultList.stream().filter(x -> x.getBelongETF().contains(SectorAnalystHelper.CODE_US_CHINA_CONCEPT)).count();
		long upCntChinaConcept = targetResultList.stream().filter( x-> x.getCurrentStockBean().getDayChgPct()>0.01 && x.getBelongETF().contains(SectorAnalystHelper.CODE_US_CHINA_CONCEPT)).count();
		
		double ratioUpHSI = (double)upCntHSIs / (double)cntHSIs;
		double ratioUpHSTech = (double)upCntHSTechs / (double)cntHSTechs;
		double ratioUpRedChips = (double)upCntRedChips / (double)cntRedChps;
		double ratioUpDualCounter = (double)upCntDualCounter / (double)cntDualCounter;
		
		double ratioUpSPY = (double)upCntSPY / (double)cntSPY;
		double ratioUpQQQ = (double)upCntQQQ / (double)cntQQQ;
		double ratioUpDIA = (double)upCntDIA / (double)cntDIA;
		double ratioUpChinaConcept = (double)upCntChinaConcept / (double)cntChinaConcept;
		
		////////////////---- >END
		List<InstantPerformanceResult> leadingResultList = strongList.stream()
				.filter( x -> !x.getSector().contains("index") && x.getSysPickLongCategory().contains("L"))
		.sorted(Comparator.comparingDouble(InstantPerformanceResult::getEstTradeAmount).reversed())
		.limit(8)
		.toList();
		
		List<InstantPerformanceResult> tailResultList = weakList.stream()
				.filter( x -> !x.getSector().contains("index") && x.getSysPickShortCategory().contains("S"))
		.sorted(Comparator.comparingDouble(InstantPerformanceResult::getEstTradeAmount).reversed())
		.limit(8)
		.toList();
		
//		StringBuilder codes = new StringBuilder();
		String leadingValue = null;
		String leadingcodes = "";
		for (InstantPerformanceResult m : leadingResultList) {			
			leadingcodes += m.getCurrentStockBean().getStockCode()+ ": " + GeneralHelper.toPct(m.getCurrentStockBean().getDayChgPct())+ " , ";
		} 
		if(leadingcodes.length()>2) {
			leadingValue = leadingcodes.substring(0, leadingcodes.length()-2);
		}
		/////////
		String tailValue = null;
		String tailcodes = "";
		for (InstantPerformanceResult m : tailResultList) {
			tailcodes += m.getCurrentStockBean().getStockCode() + ": " + GeneralHelper.toPct(m.getCurrentStockBean().getDayChgPct())+" , ";
		} 
		if(tailcodes.length()>2) {
			tailValue = tailcodes.substring(0, tailcodes.length()-2);
		}
		
		
		
//		Optional<InstantPerformanceResult> max = targetResultList.stream().filter(product -> product.getCurrentStockBean() != null)
//	            .max(Comparator.comparingDouble(product -> 
//	                    product.getCurrentStockBean().getH()));
//		Optional<InstantPerformanceResult> min = targetResultList.stream().filter(product -> product.getCurrentStockBean() != null)
//	            .min(Comparator.comparingDouble(product -> 
//	                    product.getCurrentStockBean().getH()));
//		
//		InstantPerformanceResult maxResult = max.get();
//		InstantPerformanceResult minResult = min.get();
		
		InstantPerformanceResult spy = performanceResultList.stream().filter(x-> x.getCurrentStockBean().getStockCode().equalsIgnoreCase(ICONIC_CODE)).findAny().orElse(null);
		InstantPerformanceResult qqq = performanceResultList.stream().filter(x-> x.getCurrentStockBean().getStockCode().equalsIgnoreCase(BASE_STOCK_B)).findAny().orElse(null);
		InstantPerformanceResult dia = performanceResultList.stream().filter(x-> x.getCurrentStockBean().getStockCode().equalsIgnoreCase(BASE_STOCK_C)).findAny().orElse(null);
		InstantPerformanceResult dxy = performanceResultList.stream().filter(x-> x.getCurrentStockBean().getStockCode().equalsIgnoreCase(BASE_STOCK_D)).findAny().orElse(null);
		
		if(spy==null) {
			log.error(ICONIC_CODE + " is null, spy object: "+spy);
		}
		
		StatisticsResult statresult = new StatisticsResult();
		statresult.setTxnDate(spy.getCurrentStockBean().getTxnDate());
		statresult.setNumOfStock(targetResultList.size());
		statresult.setStrongCnt(strongList.size());
		statresult.setWeakCnt(weakList.size());
		statresult.setLeadingStock(leadingValue);
		statresult.setTailStock(tailValue);
		
		statresult.setRatioDailyPositiveReturn(ratioDailyPosiReturn);
		statresult.setRatioDailyNegativeReturn(ratioDailyNegReturn);
		
		statresult.setRsi9Abv50Cnt(countRsi9Abv50);
		statresult.setAbv20DCnt(countAbv20D);
		statresult.setAbv50DCnt(countAbv50D);
		statresult.setAbv100DCnt(countAbv100D);
		statresult.setAbv200DCnt(countAbv200D);
		statresult.setMa2AbvMA19Cnt(count2DABV19D);
		statresult.setMa50AbvMA200Cnt(count50DABV200D);
		
		statresult.setRatioUpHSI(ratioUpHSI);
		statresult.setRatioUpHSTech(ratioUpHSTech);
		statresult.setRatioUpRedChip(ratioUpRedChips);
		statresult.setRatioUpDualCounter(ratioUpDualCounter);
		statresult.setRatioUpSPY(ratioUpSPY);
		statresult.setRatioUpQQQ(ratioUpQQQ);
		statresult.setRatioUpDIA(ratioUpDIA);
		statresult.setRatioUpChinaConcept(ratioUpChinaConcept);
		
	if(spy!=null) {
			statresult.setIconicAClose(spy.getCurrentStockBean().getC());
			statresult.setIconicAPct(spy.getDailyChangePct());
			statresult.setIconicARsi9(spy.getRsi9());
			statresult.setIconicARsi14(spy.getRsi14());
		}
		
		if(qqq!=null) {
			statresult.setIconicBClose(qqq.getCurrentStockBean().getC());
			statresult.setIconicBPct(qqq.getDailyChangePct());
			statresult.setIconicBRsi9(qqq.getRsi9());
			statresult.setIconicBRsi14(qqq.getRsi14());
		}
		
		if(dia!=null) {
			statresult.setIconicCClose(dia.getCurrentStockBean().getC());
			statresult.setIconicCPct(dia.getDailyChangePct());
			statresult.setIconicCRsi9(dia.getRsi9());
			statresult.setIconicCRsi14(dia.getRsi14());
		}
		
		if(dxy!=null) {
			statresult.setIconicDClose(dxy.getCurrentStockBean().getC());
			statresult.setIconicDPct(dxy.getDailyChangePct());
			statresult.setIconicDRsi9(dxy.getRsi9());
			statresult.setIconicDRsi14(dxy.getRsi14());
		}

		return statresult;
	}

	
	public void exportSimple(List<InstantPerformanceResult> recentHighList, String market , String interval, String targetMonth) {
		List<InstantPerformanceResult>  sortedList = recentHighList.parallelStream()
				.sorted(Comparator.comparing(InstantPerformanceResult::getDailyCandleStatus))
				.sorted(Comparator.comparingDouble(InstantPerformanceResult::getEstTradeAmount).reversed())
				.collect(Collectors.toList());
		
		StringBuilder msg = new StringBuilder();
		msg.append("CODE ("+interval+")\t數據時間\tName\tSector\t所屬ETF\tDATE\t現價\tD%\t3D(o2c)%\t5D(o2c)%\tVol(-1D)%\tVol%\tEst.金額(B)");
		msg.append("\tB-type\tS-type\tFLAT-type");

		msg.append("\t小浪型\t小浪型state");
		msg.append("\t強弱(-1D)\t今天強弱");
		msg.append("\tK线Status(D-1)\tK线(Status)");

		msg.append("\tVol(5D vs 20D)");
//		msg.append("\t"+WavePointAnalyticalResult.getColumnHeader());	//小浪方向\t突破Pct(小浪)\t小浪型狀\t上一個小浪頂底日

		msg.append("\tPrice Status\tVolume Status");
		//msg.append("\t價量狀態\t價量狀態開始日期");
		msg.append("\t反轉型態\t反轉突破日\t反轉型態詳細");
		msg.append("\t近日上破BB\t月內變多頭排列\t與50D最高價的差異\t近日出收集三胞胎形態");
		msg.append("\t近日下破大陽/大陽量日子\t近日上破大陰/大陰量日子\t昨今日平均線有支持/阻力");
		msg.append("\t近日Last穿頭破腳/破腳穿頭");

		msg.append("\tRSI(9)\tRSI DIVER.\tRSI DIVER. DATES\tRSI DIVER.加劇");
		msg.append("\tMTD(O2C)%\tMTD(O2PH)%\tMTD(O2PL)%\tMTD 波幅");
		msg.append("\t近期GAP Type\t裂口大小%\tGAP Type日期");
		msg.append("\t近期島型\t島型日期\t島型日數");
		msg.append("\t倍量數\t倍量日子[MA數]");
//		msg.append("\t1D-Vol\t5D-Vol\t50D-Vol\t5X50D-Vol(UP)");
		msg.append("\t大於5D\t大於10D\t大於20D\t大於50D\t大於100D\t大於200D");		
		msg.append("\tMA-CrossUP");
		
		msg.append("\t數據(10Y)\t"+targetMonth.replace("-", "")+"月上升機率");		
		msg.append("\t歷史Avg(波幅)\tAVG(C2H)\t中位數(C2H)\t9成(C2-Peri-H)\tAVG(C2L)\t中位數(C2L)\t9成(C2-Peri-L)");
		msg.append("\t月低位日期\t月低位日數\t月低位日Prob.\t月高位日期\t月高位日數\t月高位日Prob");
		
		msg.append("\t數據(20Y)\t"+targetMonth.replace("-", "")+"月上升機率");
		
		msg.append("\t歷史Avg(波幅)\tAVG(C2H)\t中位數(C2H)\t9成(C2-Peri-H)\tAVG(C2L)\t中位數(C2L)\t9成(C2-Peri-L)");
		msg.append("\t月低位日期\t月低位日數\t月低位日Prob.\t月高位日期\t月高位日數\t月高位日Prob");
		msg.append("\t本月"+YEARS_DATA_20+"向好時段數目\t本月"+YEARS_DATA_20+"Y O2H最佳時段");
		msg.append("\t本月"+YEARS_DATA_20+"向淡時段數目\t本月"+YEARS_DATA_20+"Y O2L最佳時段");
		msg.append("\tIndustry");
		msg.append("\t業績公佈日");
//		msg.append("\t最近看好pattern\t最近看淡pattern");
		msg.append("\tYTD %\tQ1 %\tQ2 %\tQ3 %\tQ4 %");
		msg.append("\tMktCap.(B)\tPE\t所屬ETF");
		
//		msg.append("\tVVV\tVVV\tVVV");
//		
//		//////////////////////////
//		msg.append("\t3Days(O2C) %\t3Days(O2H) %\tMTD(O2C)%\tMTD(O2PH)%\tMTD(O2PL)%\tMTD 波幅");
//		msg.append("\tRSI(9)\t近日RSI(9)");
//		//msg.append("\tTop3成交密集區(TRY)\t最近的成交密集區(TRY)");
//		
//		msg.append("\tVolume(-2D)\tVolume(-1D)\tVolume\tVolume比例\tVolume(1D vs 30D)\tVolume(5D vs 20D)\tVolume(10D vs 60D)\tPrice Status\tVolume Status");
//		
//		msg.append("\t強弱(-4D)\t強弱(-3D)\t強弱(-2D)\t強弱(-1D)\t今天強弱\t支持/阻力距離");
////		msg.append("\t平均波幅(5D)\t平均波幅(20D)");
//		msg.append("\t月內曾創新高");
//		msg.append("\t反轉型態");
//		msg.append("\t近期GAP Type\t裂口大小%\tGAP Type日期");
//		msg.append("\t即日短線均線變化");
//		msg.append("\t近期島型\t島型日期\t島型日數");
//		msg.append("\t"+WavePointAnalyticalResult.getColumnHeader());  // "方向(小浪)\t突破Pct(小浪)\t突破Max/Min Pct(小浪)\t被突破的上一個頂底日期\t反轉型態(小浪)\t反轉日期(小浪)\tREMARK(小浪)\tShape";
//
//		msg.append("\tDownTrend Reversal Date");
//		
//		msg.append("\t本月MTD高位日期\t本月MTD低位日期");
//		msg.append("\t歷史Avg(波幅)\t歷史中位(波幅)\t9成發生波幅");
//		msg.append("\t數據年數\t"+targetMonth.replace("-", "")+"月上升機率");
//		msg.append("\t月低位日期\t月低位日數\t月低位日Prob.\t月高位日期\t月高位日數\t月高位日Prob");
//		msg.append("\tAvg(C2PH) - MTD_H\t90%(C2PH) - MTD_H\tAvg(C2PL) - MTD_L\t90%(C2PL) - MTD_L");				
//		msg.append("\tAvg(C2P-H)\t中位(C2P-H))\t90%(C2P-H)\tAvg(C2P-L)\t中位(C2P-L)\t(9成C2P-L)");
//		
////		msg.append("\tGap Down And StandUp");
////		msg.append("\t最近看好pattern\t最近看淡pattern");
//		
//		msg.append("\t與上月高低點位置");
//		msg.append("\t(短+中線)MA距離幅度\t最近(短+中線)MA距離幅度");
//		msg.append("\t短線MA距離幅度\t中線MA距離幅度");
//		
//		msg.append("\t大於5D\t大於10D\t大於20D\t大於50D\t大於100D\t大於200D");
//		msg.append("\t2 & 19情況");
//		msg.append("\t50 & 200情況");
//		msg.append("\t50 & 250情況");
//		msg.append("\t交叉時50D\t50X200黃金交叉日期\t現價大於交叉");
//		msg.append("\t50X250黃金交叉日期\t現價大於交叉");
//		msg.append("\t連續5天 & MA50情況");
//		msg.append("\t連續5天 & MA200情況");
//		msg.append("\t平均線排列");
//		msg.append("\tBB幅度\tBB(U-Band)\tBB(L-Band)");
//		msg.append("\t價量狀態\t價量狀態開始日期");
//		msg.append("\tRSI DIVERGENCE加劇");
//		msg.append("\tRSI DIVERGENCE\tRSI divergence Dates");
//		msg.append("\tRSI(14)\tRSI(14) Trends");
//		msg.append("\t最低價日子\t最低價");
//		msg.append("\t最低至現價的上升幅度");
//		msg.append("\t最高至最低的下跌幅度");		
//		msg.append("\t最極縮量日期");
//		msg.append("\t高量日\t高量日燭身pct\t高量日當天狀態\t高量日位置狀態(TRAIL)\t高量日H-L有否被跨越\t現價VS高量日(燭身頂OR燭身底)\t是否單峰量");
//		msg.append("\t縮量數\t縮量日子\t倍量數\t倍量日子");
//		
//		msg.append("\tQualified\tLeft-Low\tLowest\tRight-Low");
//		msg.append("\tYTD %\tQ1 %\tQ2 %\tQ3 %\tQ4 %");


		System.out.println(msg);

		String message = msg.toString();
		List<List<Object>> values = new ArrayList<>();
		String[] lines = message.split("\t");
		values.add(Arrays.asList(lines));

//		msg.append("\n");
		msg = new StringBuilder();
		
		
		for (InstantPerformanceResult elemt : sortedList) {
			try {
					
	//			String name = (CFGHelper.getStockMetaMap().get(elemt.getCurrentStockBean().getStockCode())==null?Const.NA:CFGHelper.getStockMetaMap().get(elemt.getCurrentStockBean().getStockCode()).getName());
//				String sector = (CFGHelper.getStockProfileMap().get(elemt.getCurrentStockBean().getStockCode())==null?Const.NA:CFGHelper.getStockProfileMap().get(elemt.getCurrentStockBean().getStockCode()).getSector());
				
				StockProfileBean skProf = CFGHelper.getStockProfileMap().get(elemt.getCurrentStockBean().getStockCode());
				
				msg.append(elemt.getCurrentStockBean().getStockCode().replace(".HK", "") );	
				msg.append("\t"+ csvFileFormatter.format(elemt.getCurrentStockBean().getLastModifiedDateTime()));
				msg.append("\t"+ GeneralHelper.truncateOrPadString(elemt.getCurrentStockBean().getStockName(), 50, " "));
				msg.append("\t"+ (skProf==null?Const.SPACE:skProf.getSector()));
				msg.append("\t"+ elemt.getBelongETF());

				
				
				msg.append("\t"+ elemt.getCurrentStockBean().getTxnDate().substring(5, 10) +"\t"+ GeneralHelper.format(elemt.getCurrentStockBean().getC()));
				msg.append("\t"+ (elemt.getDailyChangePct()==null?Const.SPACE:GeneralHelper.toPct(elemt.getDailyChangePct())));
				msg.append("\t"+ (elemt.getThreeDaysChangeO2CPct()==null?Const.NA:GeneralHelper.toPct(elemt.getThreeDaysChangeO2CPct())));
				msg.append("\t"+ (elemt.getWeeksChangeO2CPct()==null?Const.NA:GeneralHelper.toPct(elemt.getWeeksChangeO2CPct())));
				msg.append("\t"+ GeneralHelper.toPct(elemt.getPrevStockBean().getDayVolumeChgPct()));	//Volume pct change (-1D)
				msg.append("\t"+ GeneralHelper.toPct(elemt.getCurrentStockBean().getDayVolumeChgPct()));	//Volume pct change
				msg.append("\t"+ GeneralHelper.toDecimalPlaces(elemt.getEstTradeAmount(), 4));
	
				msg.append("\t"+ elemt.getSysPickLongCategory());
				msg.append("\t"+ elemt.getSysPickShortCategory());
				msg.append("\t"+ elemt.getSysPickStagnantCategory());

				msg.append("\t"+ (elemt.getWaveShape()==null?"":elemt.getWaveShape().getShapeResult()));//小浪型
				msg.append("\t"+ (elemt.getWaveShape()==null?"":elemt.getWaveShape().getWaveSituation()));//小浪型state

				msg.append("\t"+ elemt.getStrongWeakTypePrevDay().getType());	//昨天強弱
				msg.append("\t"+ elemt.getStrongWeakTypeToday().getType());	//今天強弱

				msg.append("\t"+ elemt.getPrev1DayCandleStatus());//K线Status(D-1)
				msg.append("\t"+ elemt.getDailyCandleStatus()); 			//K线(Status)
//				msg.append("\t"+ elemt.getDailyImportantCandlestickTradingPattern());	//K线(Desc)


//				msg.append("\t"+ elemt.getPrev1DayVolDescription());	//Volume(-1D)
//				msg.append("\t"+ elemt.getDailyVolDescription());	//Volume
				msg.append("\t"+ elemt.getRecentMonthDaysVolumeStatus());

				
				//方向(小浪)  e.g. result : 升破前頂-堅-Day(1)
//				String pnt = elemt.getWaveTopBottomStatus().getStockTrendStatus()==null?"":elemt.getWaveTopBottomStatus().getStockTrendStatus()+"-"+elemt.getWaveTopBottomStatus().getBreakDegree()+"-"+elemt.getWaveTopBottomStatus().getStockTrendRemark();
//				msg.append("\t"+ pnt);
				//突破Pct(小浪)
//				msg.append("\t"+ (elemt.getWaveTopBottomStatus()==null?"":GeneralHelper.to100Pct(elemt.getWaveTopBottomStatus().getBreakPct())));
		
//				msg.append("\t"+ (elemt.getWaveTopBottomStatus()==null?" \t \t \t":elemt.getWaveTopBottomStatus().getColumnData()));
				
				msg.append("\t"+ elemt.getPriceStatus());					//Price Status
				msg.append("\t"+ elemt.getVolumeStatus());	//Volume Status
	//			msg.append("\t"+ (elemt.getWaveTopBottomStatus()==null?"":elemt.getWaveTopBottomStatus().getShape()));)
//				msg.append("\t"+ (elemt.getPvDiverence()==null?Const.SPACE:(elemt.getPvDiverence().getDivergenceType())));	//價量狀態
//				msg.append("\t"+ (elemt.getPvDiverence()==null?Const.SPACE:(elemt.getPvDiverence().getDates())));	//價量狀態開始日期
				
				msg.append("\t"+ (elemt.getReversalPatterns()==null?Const.SPACE:elemt.getReversalPatterns().getType()));
				msg.append("\t"+ (elemt.getReversalPatterns()==null?Const.SPACE:elemt.getReversalPatterns().getBreakingDate()));
				msg.append("\t"+ (elemt.getReversalPatterns()==null?Const.SPACE:"("+elemt.getReversalPatterns().getCriticalPointBtwn().getTxnDate() +") " +elemt.getReversalPatterns().getSupportMessage() ));
				
//				msg.append("\t"+ elemt.getYearHighAchievedThisMonth());	//月內曾創新高
				msg.append("\t"+ elemt.getBbUpBreakForATime());	//近日上破BB日期
				msg.append("\t"+ elemt.getMovingAvgLongArrangementWithinTheMonth());	//月內變多頭排列
				msg.append("\t"+ elemt.getPercentageDifferenceFromHighestPrice());	//與50D最高價的差異
				msg.append("\t"+ elemt.getTriplePregnancyInPassFewDays());	//近日出收集三胞胎形態日期
//				msg.append("\t"+ elemt.getUpDownBreakThreeWavePointToday());	//今天攻破小頂底
//				msg.append("\t"+ elemt.getLargeVolumeWithinTheMonth());	//
				msg.append("\t"+ elemt.getBigWhiteBodyWithMoreVol());	//近日下破大陽/大陽量日子
				msg.append("\t"+ elemt.getBigDarkBodyWithMoreVol());	//近日上破大陰/大陰量日子

//				msg.append("\t"+ elemt.getBigDarkBodyWithLessVol());	//月內大陰少量日期
				msg.append("\t"+ elemt.getMovingAverageLongSideSupport());	//昨今日平均線有支持/阻力
				msg.append("\t"+ elemt.getLastEngulfingInRecentDays());	//近日Last穿頭破腳/破腳穿頭

				msg.append("\t"+ GeneralHelper.to100(elemt.getRsi9()));	//RSI
				msg.append("\t"+ (elemt.getRsiDiverence().getDivergenceType()==null?"":elemt.getRsiDiverence().getDivergenceType()));	//RSI DIVERENCE
				
				String lastDate = elemt.getRsiDiverence().getDates().stream().reduce((first, second) -> second).orElse(null);
				msg.append("\t"+ ((lastDate==null || lastDate.isEmpty())?Const.SPACE:elemt.getRsiDiverence().getDates()));//RSI DIVERGENCE DATES
				msg.append("\t"+ elemt.getRsiDiverence().getRemark());	//RSI DIVERGENCE加劇
				
				msg.append("\t"+ (elemt.getMtdChangeO2CPct()==null?Const.SPACE:GeneralHelper.toPct(elemt.getMtdChangeO2CPct())));
				msg.append("\t"+ (elemt.getMtdChangeO2PHPct()==null?Const.SPACE:GeneralHelper.toPct(elemt.getMtdChangeO2PHPct())));
				msg.append("\t"+ (elemt.getMtdChangeO2PLPct()==null?Const.SPACE:GeneralHelper.toPct(elemt.getMtdChangeO2PLPct())));
				msg.append("\t"+ (elemt.getMtdPctRange()==null?Const.SPACE:GeneralHelper.toPct(elemt.getMtdPctRange())));
				
				msg.append("\t"+ elemt.getGapType());	//近期GAP Type\t裂口大小%\tGAP Type日期
				
				msg.append("\t"+ elemt.getIsland().getIslandType());	//近期島型
				msg.append("\t"+ elemt.getIsland().getIslandDate());	//島型日期
				msg.append("\t"+ elemt.getIsland().getNumOfTxnDates());	//島型日數
				
				msg.append("\t"+ (elemt.getPriceVolStockBean()==null?Const.SPACE:elemt.getPriceVolStockBean().getNumOfDoubleVolumeDate()));
				msg.append("\t"+ (elemt.getPriceVolStockBean()==null?Const.SPACE:elemt.getPriceVolStockBean().getDoubleVolumeDateMsg()));
//				msg.append("\t"+ GeneralHelper.to2DecimalPlaces((double)elemt.getCurrentStockBean().getVolume()/10000.0));
//				msg.append("\t"+ GeneralHelper.to2DecimalPlaces((double)elemt.getCurrentStockBean().getVolumeSma().getMa5()/10000.0));
//				msg.append("\t"+ GeneralHelper.to2DecimalPlaces((double)elemt.getCurrentStockBean().getVolumeSma().getMa50()/10000.0));
//				msg.append("\t"+ (elemt.getVolumeMASituation().getMa5x50CrossUpDate()==null?"":elemt.getVolumeMASituation().getMa5x50CrossUpDate().getCrossDate()));
				msg.append("\t"+ GeneralHelper.toPct(elemt.getAbv5D()));
				msg.append("\t"+ GeneralHelper.toPct(elemt.getAbv10D()));
				msg.append("\t"+ GeneralHelper.toPct(elemt.getAbv20D()));
				msg.append("\t"+ GeneralHelper.toPct(elemt.getAbv50D()));
				msg.append("\t"+ GeneralHelper.toPct(elemt.getAbv100D()));
				msg.append("\t"+ GeneralHelper.toPct(elemt.getAbv200D()));
				msg.append("\t"+ elemt.getMovingAverageCrossUpSummary());
				
				BenchmarkBeanResult benchmarkResult10Y = CFGHelper.getMonthBenchmark(elemt.getCurrentStockBean().getStockCode(), targetMonth, YEARS_DATA_10);
				msg.append("\t" + ((benchmarkResult10Y==null)?"":benchmarkResult10Y.getNumOfYears()));
				msg.append("\t" + ((benchmarkResult10Y==null)?"":GeneralHelper.toPct(benchmarkResult10Y.getRisesRatioC2C()))); //月上升機率
				msg.append("\t" + ((benchmarkResult10Y==null)?"":GeneralHelper.toPct(benchmarkResult10Y.getPercentageRangeStat().getAvg())));
	
				//Avg (C2H) , Avg (C2L)
				msg.append("\t" + ((benchmarkResult10Y==null)?"":GeneralHelper.toPct(benchmarkResult10Y.getC2phStat().getAvg())));
				msg.append("\t" + ((benchmarkResult10Y==null)?"":GeneralHelper.toPct(benchmarkResult10Y.getC2phStat().getMedian())));
				msg.append("\t" + ((benchmarkResult10Y==null)?"":GeneralHelper.toPct(benchmarkResult10Y.getC2phStat().getMin2nd())));//9成(C2-Period-H)
				msg.append("\t" + ((benchmarkResult10Y==null)?"":GeneralHelper.toPct(benchmarkResult10Y.getC2plStat().getAvg())));
				msg.append("\t" + ((benchmarkResult10Y==null)?"":GeneralHelper.toPct(benchmarkResult10Y.getC2plStat().getMedian())));
				msg.append("\t" + ((benchmarkResult10Y==null)?"":GeneralHelper.toPct(benchmarkResult10Y.getC2plStat().getMin2nd())));//9成(C2-Period-L)
				//"月低位日期\t月低位日數\t月低位日Prob.\t月高位日期\t月高位日數\t月高位日Prob"
				msg.append("\t" + ((benchmarkResult10Y==null)?LowHighDateSimplifyResult.getEmptyData():CFGHelper.getMonthBenchmark(elemt.getCurrentStockBean().getStockCode(), targetMonth, YEARS_DATA_10).getLowHighDateSimplifyResult().toPrintResult())); 
				
				
				BenchmarkBeanResult benchmarkResult20Y = CFGHelper.getMonthBenchmark(elemt.getCurrentStockBean().getStockCode(), targetMonth, YEARS_DATA_20);
				msg.append("\t" + ((benchmarkResult20Y==null)?"":benchmarkResult20Y.getNumOfYears()));
				msg.append("\t" + ((benchmarkResult20Y==null)?"":GeneralHelper.toPct(benchmarkResult20Y.getRisesRatioC2C()))); //月上升機率
				msg.append("\t" + ((benchmarkResult20Y==null)?"":GeneralHelper.toPct(benchmarkResult20Y.getPercentageRangeStat().getAvg())));
	
				//Avg (C2H) , Avg (C2L)
				msg.append("\t" + ((benchmarkResult20Y==null)?"":GeneralHelper.toPct(benchmarkResult20Y.getC2phStat().getAvg())));
				msg.append("\t" + ((benchmarkResult20Y==null)?"":GeneralHelper.toPct(benchmarkResult20Y.getC2phStat().getMedian())));
				msg.append("\t" + ((benchmarkResult20Y==null)?"":GeneralHelper.toPct(benchmarkResult20Y.getC2phStat().getMin2nd())));//9成(C2-Period-H)
				msg.append("\t" + ((benchmarkResult20Y==null)?"":GeneralHelper.toPct(benchmarkResult20Y.getC2plStat().getAvg())));
				msg.append("\t" + ((benchmarkResult20Y==null)?"":GeneralHelper.toPct(benchmarkResult20Y.getC2plStat().getMedian())));
				msg.append("\t" + ((benchmarkResult20Y==null)?"":GeneralHelper.toPct(benchmarkResult20Y.getC2plStat().getMin2nd())));//9成(C2-Period-H)
				//"月低位日期\t月低位日數\t月低位日Prob.\t月高位日期\t月高位日數\t月高位日Prob"
				msg.append("\t" + ((benchmarkResult20Y==null)?LowHighDateSimplifyResult.getEmptyData():CFGHelper.getMonthBenchmark(elemt.getCurrentStockBean().getStockCode(), targetMonth, YEARS_DATA_20).getLowHighDateSimplifyResult().toPrintResult())); 
				
				//
				List<BenchmarkBeanResult> individualStockBenchmarkResult20YList = CFGHelper.getIndividualStockBenchmark(elemt.getCurrentStockBean().getStockCode(), YEARS_DATA_20);					
				String strongSideAnalystResult = doStatcBenchmarkAnalyst(individualStockBenchmarkResult20YList, targetMonth, true);
				String weakSideAnalystResult = doStatcBenchmarkAnalyst(individualStockBenchmarkResult20YList, targetMonth, false);
				msg.append("\t"+strongSideAnalystResult);
				msg.append("\t"+weakSideAnalystResult);
				
				msg.append("\t"+ (skProf==null?Const.SPACE:skProf.getIndustry()));
				msg.append("\t"+ (skProf==null?Const.SPACE:skProf.getEarningsDate()));
				
//				msg.append("\t"+ (elemt.getBullishPattern()==null?Const.SPACE:elemt.getBullishPattern().toString()));
//				msg.append("\t"+ (elemt.getBearishPattern()==null?Const.SPACE:elemt.getBearishPattern().toString()));
				
				msg.append("\t"+ (elemt.getYtd()==null?Const.NA:GeneralHelper.toPct(elemt.getYtd())));
				msg.append("\t"+ (elemt.getQ1()==null?Const.NA:GeneralHelper.toPct(elemt.getQ1())));
				msg.append("\t"+ (elemt.getQ2()==null?Const.NA:GeneralHelper.toPct(elemt.getQ2())));
				msg.append("\t"+ (elemt.getQ3()==null?Const.NA:GeneralHelper.toPct(elemt.getQ3())));
				msg.append("\t"+ (elemt.getQ4()==null?Const.NA:GeneralHelper.toPct(elemt.getQ4())));
				msg.append("\t"+ (skProf==null?Const.SPACE:MarketValueDifferenceCalculator.convertToBillian(skProf.getMarketCap())));
				msg.append("\t"+ (skProf==null?Const.SPACE:skProf.getPeRatio()));
				msg.append("\t"+ elemt.getBelongSouthbound());

				message = msg.toString();
				String[] datalines = message.split("\t");
				values.add(Arrays.asList(datalines));
				System.out.println(msg);
				msg.setLength(0);
			}catch(Exception e) {
				log.error("Error on "+elemt.getCurrentStockBean(), e);
			}
//			System.out.println("\n"+msg);
		}
		System.out.println("\n"+msg);
        try {
			String sheetName = Const.IS_INTRADAY?"ONDAY_"+market+interval:"AUTO_"+market+interval;
            GoogleSheetsCreateAndUploadExample.upload(sheetName, values);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	

	public static String doStatcBenchmarkAnalyst(List<BenchmarkBeanResult> benchResultList, String month, boolean isStrongSide) {
		String rtnResult=" \t ";
		
		if(benchResultList==null || benchResultList.isEmpty())
			return rtnResult;
		int sampleSize = benchResultList.get(0).getNumOfYears();
		LocalDate now = LocalDate.now();
		LocalDate inputMonthFirstDate = LocalDate.parse(now.getYear()+""+month+"01");
		LocalDate lastDayOfInputMonth = inputMonthFirstDate.with(TemporalAdjusters.lastDayOfMonth());
		
		
		String numericString = month.replace("-", "")+"01";
        int numericStartDateValue = Integer.parseInt(numericString);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMdd");
        String formattedDate = lastDayOfInputMonth.format(formatter);
        
        
        int numericEndDateValue = Integer.parseInt(formattedDate);
        List<BenchmarkBeanResult> returnList = null;
        
        double probabilityRise = (sampleSize>10)?0.8:0.9;
        double probabilityO2PeriodH = (sampleSize>10)?0.85:0.9;
        
        
        double probabilityFall = (sampleSize>10)?0.7:0.8;
        double probabilityO2PeriodL = (sampleSize>10)?0.85:0.9;
        
        if(isStrongSide) {
        	returnList = benchResultList.parallelStream()
        			.filter( x->  //x.getRisesRatioC2C() > 0.8 
        				x.getRisesRatioO2C() > probabilityRise
						&& numericStartDateValue <= Integer.parseInt(x.getStartMMdd().replace("-", ""))
						&& numericEndDateValue >= Integer.parseInt(x.getEndMMdd().replace("-", ""))
					).toList();
        	
        	if(returnList.size() ==0)
            	return rtnResult;
        	
        	
//        	BenchmarkBeanResult earliestBenchmark = returnList.parallelStream()
//                    .min(Comparator.comparing(b -> b.getStartMMdd()))
//                    .orElse(null);
//            
//            BenchmarkBeanResult latestBenchmark = returnList.parallelStream()
//                    .max(Comparator.comparing(b -> b.getEndMMdd()))
//                    .orElse(null);
            //本月向好時段數目 
            rtnResult = returnList.size() +"";
            
            //本月O2PH最佳時段
            String toHMedianResult = "\t";//default
//            Optional<BenchmarkBeanResult> largestMedianBenchmarkBeanResult = returnList.parallelStream()
//            		.filter(x -> x.getRisesRatioO2C()>probabilityRise && x.getRisesRatioO2ph()>= probabilityO2PeriodH ) //&& x.getPeriodNoOfDays()<15)
//                    .max(Comparator.comparingDouble(result -> result.getO2phStat().getMedian()));

            Optional<BenchmarkBeanResult> largestMedianBenchmarkBeanResult = returnList.parallelStream()
            		.filter(x -> x.getRisesRatioO2C()>probabilityRise && x.getRisesRatioO2ph()>= probabilityO2PeriodH )
                    .max(Comparator.comparingDouble(result -> result.getC2phRatingOfEffectiveness()));
            
            
            if (largestMedianBenchmarkBeanResult.isPresent()) {
                BenchmarkBeanResult result = largestMedianBenchmarkBeanResult.get();
                
                toHMedianResult = "\t" +result.getStartMMdd() +"_"+ result.getEndMMdd()+" (O2PH.中位:"+ GeneralHelper.toPct(result.getO2phStat().getMedian())+ ", 9成:"+ GeneralHelper.toPct(result.getO2phStat().getMin2nd())+")";
            } else {
//                System.out.println("列表為空，沒有最大值");
            }
            rtnResult = rtnResult + toHMedianResult;
            
        	
        }else {
        	returnList = benchResultList.parallelStream()
        			.filter( x->  x.getFallsRatioO2C() > probabilityFall 
    					&& numericStartDateValue <= Integer.parseInt(x.getStartMMdd().replace("-", ""))
    					&& numericEndDateValue >= Integer.parseInt(x.getEndMMdd().replace("-", ""))
        			).toList();
        	
        	if(returnList.size() ==0)
            	return rtnResult;
        	
//        	BenchmarkBeanResult earliestBenchmark = returnList.parallelStream()
//                    .min(Comparator.comparing(b -> b.getStartMMdd()))
//                    .orElse(null);
//            
//            BenchmarkBeanResult latestBenchmark = returnList.parallelStream()
//                    .max(Comparator.comparing(b -> b.getEndMMdd()))
//                    .orElse(null);
            
            //本月向好時段數目
            rtnResult = returnList.size() +"";
            
            //本月O2PL最佳時段
            String toLMedianResult = "\t";//default
//            Optional<BenchmarkBeanResult> smallestMedianBenchmarkBeanResult = returnList.parallelStream()
//            		.filter(x -> x.getFallsRatioO2C()>probabilityFall && x.getFallsRatioO2pl()>=probabilityO2PeriodL )//&& x.getPeriodNoOfDays()<15)
//                    .min(Comparator.comparingDouble(result -> result.getO2plStat().getMedian()));
            Optional<BenchmarkBeanResult> smallestMedianBenchmarkBeanResult = returnList.parallelStream()
            		.filter(x -> x.getFallsRatioO2C()>probabilityFall && x.getFallsRatioO2pl()>=probabilityO2PeriodL )//&& x.getPeriodNoOfDays()<15)
                    .max(Comparator.comparingDouble(result -> result.getO2plRatingOfEffectiveness()));
            if (smallestMedianBenchmarkBeanResult.isPresent()) {
                BenchmarkBeanResult result = smallestMedianBenchmarkBeanResult.get();
                
                toLMedianResult = "\t" +result.getStartMMdd() +"_"+ result.getEndMMdd()+" (O2PL.中位:"+ GeneralHelper.toPct(result.getO2plStat().getMedian()) + ", 9成:"+ GeneralHelper.toPct(result.getO2plStat().getMin2nd())+")";
            } else {
//                System.out.println("列表為空，沒有最min值");
            }
            rtnResult = rtnResult + toLMedianResult;
            
        }
        return rtnResult;
	}
	
}
