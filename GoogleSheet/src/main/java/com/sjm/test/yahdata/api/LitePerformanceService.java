package com.sjm.test.yahdata.api;

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

import com.sjm.test.yahdata.analy.module.lowhighdist.bean.LowHighDateSimplifyResult;
import com.sjm.test.yahdata.analy.probability.bean.BenchmarkBeanResult;
import com.sjm.test.yahdata.analy.report.DailySummaryReport;
import com.sjm.test.yahdata.analy.ta.helper.SectorAnalystHelper;
import com.sjm.test.yahdata.api.dto.StockPerformanceDto;
import com.sjm.test.yahdata.service.BaseApp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@Deprecated
public class LitePerformanceService extends BaseApp {
	
	public static final String START_DATE = "2010-01-01";
	public static String END_DATE = "2028-12-03";
	
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
			CODE_POOL =   USStockListConfig.MAIN;
//			CODE_POOL =   Stream.of(USStockListConfig.QQQ_COMPONENTS).flatMap(Collection::stream) .collect(Collectors.toList());

//			CODE_POOL = Arrays.asList("AMD","QQQ","DIA","SPY","TSLA","BABA","MSFT","AAPL","NVDA","AMZN");//USStockListConfig.ETF;

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
		PerformanceApi bchkProgram = new PerformanceApi();
		
		List<StockPerformanceDto> instantPerformanceResultList = null;

		
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
		List<StockPerformanceDto> results = instantPerformanceResultList.stream().filter(x-> x.getCurrentStockBean().getTxnDateInt() == lastBean.getTxnDateInt()).toList();
		this.exportSimple(results, DEFAULT_INTERVAL, targetMonth);


		DailyPerformanceSummaryReport.printSimpleStatisticsResult(results, COUNTRY_MARKET, ICONIC_CODE);


		System.out.println();
	}
	

	
	
	
	

	
	public void exportSimple(List<StockPerformanceDto> recentHighList,String interval, String targetMonth) {
		List<StockPerformanceDto>  sortedList = recentHighList.parallelStream()
				.sorted(Comparator.comparing(StockPerformanceDto::getDailyCandleStatus))
				.sorted(Comparator.comparingDouble(StockPerformanceDto::getEstTradeAmount).reversed())
				.toList();
		
		StringBuilder msg = new StringBuilder();
		msg.append("CODE ("+interval+")\t數據時間\tName\tSector\t所屬ETF\tDATE\t現價\tD%\t3D(o2c)%\t5D(o2c)%\tVol(-1D)%\tVol%\tEst.金額(B)");
		msg.append("\tK线Status(D-1)\tK线(Status)\tK线(Desc)");	//msg.append("\tVVV\tVVV\tVVV");
		msg.append("\t小浪型\t小浪型state");
		msg.append("\tVol(5D vs 20D)");

		msg.append("\t近日Last穿頭破腳/破腳穿頭");
		msg.append("\tPrice Status\tVolume Status");

		msg.append("\t反轉型態\t反轉突破日\t反轉型態詳細");
		msg.append("\t近日上破BB日期\t月內變多頭排列日期\t近日大陰/大量陰日子\t昨今日平均線有支持/阻力");
				
		msg.append("\tRSI(9)\tRSI DIVER.\tRSI DIVER. DATES\tRSI DIVER.加劇");

		msg.append("\t近期GAP Type\t裂口大小%\t裂口Vol Pct\tGAP Type日期");

		msg.append("\t近期島型\t島型日期\t島型日數");
//		msg.append("\t倍量數\t倍量日子[MA數]");
//		msg.append("\t1D-Vol\t5D-Vol\t50D-Vol\t5X50D-Vol(UP)");
		msg.append("\t大於5D\t大於10D\t大於20D\t大於50D\t大於100D\t大於200D");		
//		msg.append("\tMA-CrossUP");
		msg.append("\tMTD(O2C)%\tMTD(O2PH)%\tMTD(O2PL)%\tMTD 波幅");
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
		msg.append("\n");
		
		
		for (StockPerformanceDto elemt : sortedList) {
			try {

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
				msg.append("\t"+ GeneralHelper.toDecimalPlaces(elemt.getEstTradeAmount(), 4));	//Est.金額(B)


				msg.append("\t"+ elemt.getPrev1DayCandleStatus());//K线Status(D-1)
				msg.append("\t"+ elemt.getDailyCandleStatus()); 			//K线(Status)
				msg.append("\t"+ elemt.getDailyImportantCandlestickTradingPattern());	//K线(Desc)
				msg.append("\t"+ (elemt.getWaveShape()==null?"":elemt.getWaveShape().getShapeResult()));//小浪型
				msg.append("\t"+ (elemt.getWaveShape()==null?"":elemt.getWaveShape().getWaveSituation()));//小浪型state

				msg.append("\t"+ elemt.getRecentMonthDaysVolumeStatus());
				msg.append("\t"+ elemt.getLastEngulfingInRecentDays());

				msg.append("\t"+ elemt.getPriceStatus());					//Price Status
				msg.append("\t"+ elemt.getVolumeStatus());	//Volume Status

				
				msg.append("\t"+ (elemt.getReversalPatterns()==null?Const.SPACE:elemt.getReversalPatterns().getType()));
				msg.append("\t"+ (elemt.getReversalPatterns()==null?Const.SPACE:elemt.getReversalPatterns().getBreakingDate()));
				msg.append("\t"+ (elemt.getReversalPatterns()==null?Const.SPACE:"("+elemt.getReversalPatterns().getCriticalPointBtwn().getTxnDate() +") " +elemt.getReversalPatterns().getSupportMessage() ));
				
//				msg.append("\t"+ elemt.getYearHighAchievedThisMonth());	//月內曾創新高
				msg.append("\t"+ elemt.getBbUpBreakForATime());	//近日上破BB日期
				msg.append("\t"+ elemt.getMovingAvgLongArrangementWithinTheMonth());	//月內曾變多頭排列日期
//				msg.append("\t"+ elemt.getUpDownBreakThreeWavePointToday());	//今天攻破小頂底
//				msg.append("\t"+ elemt.getLargeVolumeWithinTheMonth());	//
				msg.append("\t"+ elemt.getBigDarkBodyWithMoreVol());	//近日大陰/大量陰日子
//				msg.append("\t"+ elemt.getBigDarkBodyWithLessVol());	//月內大陰少量日期
				msg.append("\t"+ elemt.getMovingAverageLongSideSupport());	//昨今日平均線有支持/阻力
				
				msg.append("\t"+ GeneralHelper.to100(elemt.getRsi9()));	//RSI
				msg.append("\t"+ (elemt.getRsiDiverence().getDivergenceType()==null?"":elemt.getRsiDiverence().getDivergenceType()));	//RSI DIVERENCE
				
				String lastDate = elemt.getRsiDiverence().getDates().stream().reduce((first, second) -> second).orElse(null);
				msg.append("\t"+ ((lastDate==null || lastDate.isEmpty())?Const.SPACE:elemt.getRsiDiverence().getDates()));//RSI DIVERGENCE DATES
				msg.append("\t"+ elemt.getRsiDiverence().getRemark());	//RSI DIVERGENCE加劇
				

				
				msg.append("\t"+ elemt.getGapType());	//近期GAP Type\t裂口大小%\tGAP Type日期
				
				msg.append("\t"+ elemt.getIsland().getIslandType());	//近期島型
				msg.append("\t"+ elemt.getIsland().getIslandDate());	//島型日期
				msg.append("\t"+ elemt.getIsland().getNumOfTxnDates());	//島型日數
				

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
//				msg.append("\t"+ elemt.getMovingAverageCrossUpSummary());

				msg.append("\t"+ (elemt.getMtdChangeO2CPct()==null?Const.SPACE:GeneralHelper.toPct(elemt.getMtdChangeO2CPct())));
				msg.append("\t"+ (elemt.getMtdChangeO2PHPct()==null?Const.SPACE:GeneralHelper.toPct(elemt.getMtdChangeO2PHPct())));
				msg.append("\t"+ (elemt.getMtdChangeO2PLPct()==null?Const.SPACE:GeneralHelper.toPct(elemt.getMtdChangeO2PLPct())));
				msg.append("\t"+ (elemt.getMtdPctRange()==null?Const.SPACE:GeneralHelper.toPct(elemt.getMtdPctRange())));

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
				msg.append("\t").append(strongSideAnalystResult);
				msg.append("\t").append(weakSideAnalystResult);
				
				msg.append("\t").append(skProf == null ? Const.SPACE : skProf.getIndustry());
				msg.append("\t").append(skProf == null ? Const.SPACE : skProf.getEarningsDate());
				
//				msg.append("\t"+ (elemt.getBullishPattern()==null?Const.SPACE:elemt.getBullishPattern().toString()));
//				msg.append("\t"+ (elemt.getBearishPattern()==null?Const.SPACE:elemt.getBearishPattern().toString()));
				
				msg.append("\t").append(elemt.getYtd() == null ? Const.NA : GeneralHelper.toPct(elemt.getYtd()));
				msg.append("\t"+ (elemt.getQ1()==null?Const.NA:GeneralHelper.toPct(elemt.getQ1())));
				msg.append("\t"+ (elemt.getQ2()==null?Const.NA:GeneralHelper.toPct(elemt.getQ2())));
				msg.append("\t"+ (elemt.getQ3()==null?Const.NA:GeneralHelper.toPct(elemt.getQ3())));
				msg.append("\t"+ (elemt.getQ4()==null?Const.NA:GeneralHelper.toPct(elemt.getQ4())));
				msg.append("\t"+ (skProf==null?Const.SPACE:MarketValueDifferenceCalculator.convertToBillian(skProf.getMarketCap())));
				msg.append("\t"+ (skProf==null?Const.SPACE:skProf.getPeRatio()));
				msg.append("\t"+ elemt.getBelongSouthbound());
				
			}catch(Exception e) {
				log.error("Error on "+elemt.getCurrentStockBean(), e);
			}
			msg.append("\n");	
		}
		System.out.println("\n"+msg);
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
