package com.sjm.test.yahdata.analy.module.benchmarks;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.maas.util.DateHelper;
import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.analyzer.LargeCandleStickAnalyzer;
import com.sjm.test.yahdata.analy.bean.PvrStockBean;
import com.sjm.test.yahdata.analy.bean.StrongWeakTypeBean;
import com.sjm.test.yahdata.analy.bean.VolumePriceBean;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.conts.ta.MAStatusEnum;
import com.sjm.test.yahdata.analy.conts.ta.RuleConst;
import com.sjm.test.yahdata.analy.conts.type.CandlestickTradingPatternType;
import com.sjm.test.yahdata.analy.conts.type.KType;
import com.sjm.test.yahdata.analy.conts.type.WaveType;
import com.sjm.test.yahdata.analy.helper.AnalyGeneralHelper;
import com.sjm.test.yahdata.analy.helper.CFGHelper;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.instantresult.StockPickTagger;
import com.sjm.test.yahdata.analy.model.DownBreakAndStandUpBean;
import com.sjm.test.yahdata.analy.model.InstantPerformanceResult;
import com.sjm.test.yahdata.analy.module.wavepoint.RulerDNTrendReversal;
import com.sjm.test.yahdata.analy.module.wavepoint.PriceTopBotWavePointHandler;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WavePoint;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WaveShape;
import com.sjm.test.yahdata.analy.pv.PriceVolumeRelationship;
import com.sjm.test.yahdata.analy.ta.EventRuleHandler;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.PatternTrendHelper;
import com.sjm.test.yahdata.analy.ta.helper.SectorAnalystHelper;
import com.sjm.test.yahdata.analy.ta.helper.VolumePriceStructureHelper;
import com.sjm.test.yahdata.analy.ta.indicator.divergence.DivergenceFinder;
import com.sjm.test.yahdata.analy.ta.indicator.rsi.DivergenceResult;
import com.sjm.test.yahdata.analy.ta.pattern.BaseMWShapeInfo;
import com.sjm.test.yahdata.analy.ta.pattern.BollingerBandApi;
import com.sjm.test.yahdata.analy.ta.pattern.MPatternAnaly;
import com.sjm.test.yahdata.analy.ta.rule.ma.situation.bean.PriceMASituationSummaryBean;
import com.sjm.test.yahdata.analy.ta.rule.ma.situation.bean.VolumeMASituationSummaryBean;
import com.sjm.test.yahdata.analy.ta.rule.ma.situation.engine.PriceMASituationEngine;
import com.sjm.test.yahdata.analy.ta.rule.ma.situation.engine.VolumeMASituationEngine;
import com.sjm.test.yahdata.analy.vol.ConcentratedVolumeAnalyzer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BenchmarksPrograms {

	private static int ANALYSIS_VOL_PRICE_DAYS = 30;

	private MPatternAnaly mpatternAnaly = new MPatternAnaly();
	private PriceVolumeRelationship pvr = new PriceVolumeRelationship();
//	private WaveHelper waveHelper = new WaveHelper();

	private PriceTopBotWavePointHandler waveTopBotHandler = new PriceTopBotWavePointHandler();
	private RulerDNTrendReversal downTrendReversal = new RulerDNTrendReversal();
//	private WaveRsiTopBotHandler waveRsiTopBotHandler = new WaveRsiTopBotHandler();
	
	private ConcentratedVolumeAnalyzer stockAnalyzer = new ConcentratedVolumeAnalyzer();
	
	private BollingerBandApi bollingerBandApi = new BollingerBandApi();
	
	public BenchmarksPrograms() {
	}

	public List<InstantPerformanceResult> doBenchmarks(List<String> stockPool, List<StockBean> fullTrunkList,
			String interval, int inputEndTxnDateInt) {
		List<CandleEventTagEnum> BACKTEST_PATTERN_ARY = Arrays.asList(
				 // CandleTagEnum.EVNT_UP_BREAKOUT_FAILURE,
				CandleEventTagEnum.EVNT_BULLISH_ABANDONED_BABY, CandleEventTagEnum.EVNT_BEARISH_ABANDONED_BABY,
				CandleEventTagEnum.EVNT_GBUB, CandleEventTagEnum.EVNT_GAPUP_AND_GO, CandleEventTagEnum.EVNT_GAP_UP,
				CandleEventTagEnum.EVNT_GAP_DOWN, CandleEventTagEnum.EVNT_GAP_DOWN_AND_STAND_UP, CandleEventTagEnum.EVNT_TWO_WHITE_WITH_BLACK,
				
				CandleEventTagEnum.EVNT_BIG_DROP_AND_STAND_UP, CandleEventTagEnum.EVNT_SMALL_LONG_ARRANGE,
				CandleEventTagEnum.EVNT_MID_TERM_LONG_ARRANGE, CandleEventTagEnum.EVNT_INCREASE_DOUBLE_VOL_RED,
				CandleEventTagEnum.EVNT_WEEKLY_H, CandleEventTagEnum.EVNT_WEEKLY_H_CLOSE, CandleEventTagEnum.EVNT_WEEKLY_L,
				CandleEventTagEnum.EVNT_WEEKLY_L_CLOSE, // use to Strong2Weak, Weak2Strong purpose
				//CandleTagEnum.EVNT_WEEKLY_H_FIRSTTIME, CandleTagEnum.EVNT_WEEKLY_H_CLOSE_FIRSTTIME,
				//CandleTagEnum.EVNT_WEEKLY_L_FIRSTTIME, CandleTagEnum.EVNT_WEEKLY_L_CLOSE_FIRSTTIME,
				CandleEventTagEnum.EVNT_FALL_BODY_LOWER_LOW, CandleEventTagEnum.EVNT_PRICE_VOL_PUSH_UP,
				CandleEventTagEnum.EVNT_UP_BREAKOUT_FAILURE, CandleEventTagEnum.EVNT_DOWNWARD_BREAKOUT_FAILURE,
				CandleEventTagEnum.EVNT_BULLISH_ENGULFING_2, CandleEventTagEnum.EVNT_BEARISH_ENGULFING_2
//				CandleTagEnum.EVNT_LONG_BOOM
				);
		
		List<CandleEventTagEnum> BACKTEST_MA_CROSS_UP_ARY = Arrays.asList(
				CandleEventTagEnum.EVNT_CROSS_UP_5D_10D, //CandleTagEnum.EVNT_CROSS_UP_3D_18D,
				CandleEventTagEnum.EVNT_CROSS_UP_2D_19D, CandleEventTagEnum.EVNT_CROSS_UP_10D_20D, CandleEventTagEnum.EVNT_CROSS_UP_10D_50D,
				CandleEventTagEnum.EVNT_CROSS_UP_50D_200D, CandleEventTagEnum.EVNT_CROSS_UP_50D_250D
				);

		List<InstantPerformanceResult> recentHighList = new ArrayList<InstantPerformanceResult>(10);

		EventRuleHandler ruleHandler = new EventRuleHandler();
		PriceMASituationEngine maStatusengine = new PriceMASituationEngine();
		VolumeMASituationEngine volumeMAStatusengine = new VolumeMASituationEngine();
//			PriceVolumeRelationship pvr = new PriceVolumeRelationship();
//			EventMACrossRuleHandler maCrossRuleHandler = new EventMACrossRuleHandler();
		StockPickTagger stockTagger = new StockPickTagger();
		
//		List<String> skippedTickers = new ArrayList<String>(10);
		int cntSk = 0;
		for (String code : stockPool) {
			cntSk++;
			try {

				List<StockBean> stockList = StreamTransformHelper.extractByStockCode(fullTrunkList, code);
				if (stockList == null || stockList.size() < 10) {					
					log.warn("SKIP " + code + " due to Hist data size = "+stockList.size()+". The " + cntSk + " stock has currently been processed.");
					continue;
				}

//				StockBean lastStock = stockList.get(stockList.size() - 1);
//				if (lastStock.getTxnDateInt() < inputEndTxnDateInt) {
//					log.warn("SKIP " + code + ", Reason is current txn date = " + lastStock.getTxnDate()
//							+ " , But require: " + inputEndTxnDateInt +", current index = "+cntSk);
//					skippedTickers.add(code);
//					continue;
//				}


				List<VolumePriceBean> vpStockList = ruleHandler.goThruRules(stockList, ANALYSIS_VOL_PRICE_DAYS, BACKTEST_PATTERN_ARY);

				
				List<VolumePriceBean> vpMaCrossUPStockList = ruleHandler.goThruRules(stockList, 5, BACKTEST_MA_CROSS_UP_ARY);
//				List<VolumePriceBean> maCrossVPStockList = maCrossRuleHandler.goThruRules(stockList, ANALYSIS_VOL_PRICE_DAYS);
				PriceMASituationSummaryBean maSituation = maStatusengine.detect(stockList);
				VolumeMASituationSummaryBean volMASituation = volumeMAStatusengine.detect(stockList);
//				MovingAvgCrossResultBean ma50200CrossUpDate = maStatusengine.findRecentMACrossUpDate(stockList, 50, 200);
//				MovingAvgCrossResultBean ma50250CrossUpDate = maStatusengine.findRecentMACrossUpDate(stockList, 50, 250);
//				MovingAvgCrossResultBean ma50200CrossDownDate = maStatusengine.findRecentMACrossDownDate(stockList, 50, 200);

				InstantPerformanceResult recentHBean = this.findRecordHigh(stockList);
				recentHBean.setInterval(interval);
				recentHBean.setMaSituation(maSituation);
				recentHBean.setVolumeMASituation(volMASituation);
				// calc RSI(9)
//				List<RSIMeta> rsi9MetaList = rsiCalc.calculateAll(stockList, 9);
//				if (rsi9MetaList != null && rsi9MetaList.isEmpty() == false) {
//					double last1 = rsi9MetaList.get(rsi9MetaList.size() - 1).getRsiValue();
//					double last2 = rsi9MetaList.get(rsi9MetaList.size() - 2).getRsiValue();
//					double last3 = rsi9MetaList.get(rsi9MetaList.size() - 3).getRsiValue();
					StockBean last1 = stockList.get(stockList.size() - 1);
					StockBean last2 = stockList.get(stockList.size() - 2);
					StockBean last3 = stockList.get(stockList.size() - 3);					
					StockBean last4 = stockList.get(stockList.size() - 4);
					StockBean last5 = stockList.get(stockList.size() - 5);
					recentHBean.setRsi9(last1.getRsi9());
					String rsiTrend = GeneralHelper.to2DecimalPlaces(last5.getRsi9())
							+ " _ " + GeneralHelper.to2DecimalPlaces(last4.getRsi9())
							+ " _ " + GeneralHelper.to2DecimalPlaces(last3.getRsi9()) 
							+ " _ " + GeneralHelper.to2DecimalPlaces(last2.getRsi9())
					 		+ " _ " + GeneralHelper.to2DecimalPlaces(last1.getRsi9());

					recentHBean.setRsi9TrendIn5Days(rsiTrend);
//				}
				// calc RSI(14)
//				List<RSIMeta> rsi14MetaList = rsiCalc.calculateAll(stockList, 14);
//				if (rsi14MetaList != null && rsi14MetaList.isEmpty() == false) {
//					double last1 = rsi14MetaList.get(rsi14MetaList.size() - 1).getRsiValue();
//					double last2 = rsi14MetaList.get(rsi14MetaList.size() - 2).getRsiValue();
//					double last3 = rsi14MetaList.get(rsi14MetaList.size() - 3).getRsiValue();

					recentHBean.setRsi14(last1.getRsi14());
					String rsi14Trend = GeneralHelper.to2DecimalPlaces(last3.getRsi14()) + " _ "
							+ GeneralHelper.to2DecimalPlaces(last2.getRsi14()) + " _ " + GeneralHelper.to2DecimalPlaces(last1.getRsi14());

					recentHBean.setRsi14TrendIn3Days(rsi14Trend);
//				}

				// calc past 3 days strong weak status
//				List<VolumePriceBean> vpStockList = ruleHandler.goThruRules(stockList, ANALYSIS_VOL_PRICE_DAYS, BACKTEST_PATTERN_ARY);
//				PatternTrendHelper.getStrongWeakType(stockList, vpStockList);
				
				StrongWeakTypeBean todayDayStrongWeakType = PatternTrendHelper.getStrongWeakType(stockList, vpStockList);
					
				List<StockBean> stockListPrev1 = stockList.subList(0, stockList.size() - 1);
				List<VolumePriceBean> vpStockListPrev1 = ruleHandler.goThruRules(stockListPrev1, ANALYSIS_VOL_PRICE_DAYS, BACKTEST_PATTERN_ARY);
				StrongWeakTypeBean prevDayStrongWeakType = PatternTrendHelper.getStrongWeakType(stockListPrev1, vpStockListPrev1);

				List<StockBean> stockListPrev2 = stockList.subList(0, stockList.size() - 2);
				List<VolumePriceBean> vpStockListPrev2 = ruleHandler.goThruRules(stockListPrev2, ANALYSIS_VOL_PRICE_DAYS, BACKTEST_PATTERN_ARY);
				StrongWeakTypeBean prev2dayStrongWeakType = PatternTrendHelper.getStrongWeakType(stockListPrev2, vpStockListPrev2);

				List<StockBean> stockListPrev3 = stockList.subList(0, stockList.size() - 3);
				List<VolumePriceBean> vpStockListPrev3 = ruleHandler.goThruRules(stockListPrev3, ANALYSIS_VOL_PRICE_DAYS, BACKTEST_PATTERN_ARY);
				StrongWeakTypeBean prev3dayStrongWeakType = PatternTrendHelper.getStrongWeakType(stockListPrev3, vpStockListPrev3);
				
				List<StockBean> stockListPrev4 = stockList.subList(0, stockList.size() - 4);
				List<VolumePriceBean> vpStockListPrev4 = ruleHandler.goThruRules(stockListPrev4, ANALYSIS_VOL_PRICE_DAYS, BACKTEST_PATTERN_ARY);
				StrongWeakTypeBean prev4dayStrongWeakType = PatternTrendHelper.getStrongWeakType(stockListPrev4, vpStockListPrev4);
				
				
				

				DownBreakAndStandUpBean dbsuBean = mpatternAnaly.findFallAndStandPattern(stockList);

				if (recentHBean != null) {
//					PvrStockBean pvrbean = pvr.findPriceVolRelationship(stockList, CHART_SHOW_DAYS);

//					List<StockBean> subStockList = stockList.subList(stockList.size()- CHART_SHOW_DAYS, stockList.size());

//					recentHBean.setPriceVolRelationship(pvrbean);
					recentHBean.setPriceStatus(VolumePriceStructureHelper.getPriceStatus(stockList));
					recentHBean.setVolumeStatus(VolumePriceStructureHelper.getVolumeStatus(stockList));
//					recentHBean.setVolumeExtreameShrankDate(VolumePriceStructureHelper.findExtreamLessVol(subStockList));
//					recentHBean.setMovingAvgSummary(maSituatioin);
//					recentHBean.setMaBullishPattern(isMABullishPattern);
					recentHBean.setSector(
							(CFGHelper.getStockProfileMap().get(recentHBean.getCurrentStockBean().getStockCode()) == null
									? Const.NA
									: CFGHelper.getStockProfileMap().get(recentHBean.getCurrentStockBean().getStockCode())
											.getSector()));
					recentHBean.setIndustry(
							(CFGHelper.getStockProfileMap().get(recentHBean.getCurrentStockBean().getStockCode()) == null
									? Const.NA
									: CFGHelper.getStockProfileMap().get(recentHBean.getCurrentStockBean().getStockCode())
											.getIndustry()));
					
//					recentHBean.setMaCrossList(maCrossVPStockList);
//					recentHBean.setStrongWeakType(PatternTrendHelper.getStrongWeakType(stockList, vpStockList));
//					recentHBean.setCurrentStrongType(PatternTrendHelper.getGoStrongType(stockList, vpStockList));
//					recentHBean.setCurrentWeakType(PatternTrendHelper.getGoWeakType(stockList, vpStockList));
					recentHBean.setStrongWeakTypeToday(todayDayStrongWeakType);
					recentHBean.setStrongWeakTypePrevDay(prevDayStrongWeakType);
					recentHBean.setStrongWeakTypePrev2Days(prev2dayStrongWeakType);
					recentHBean.setStrongWeakTypePrev3Days(prev3dayStrongWeakType);
					recentHBean.setStrongWeakTypePrev4Days(prev4dayStrongWeakType);
					
					recentHBean.setGapType(KHelper.getGapTypeDisplayMessage(stockList));
					recentHBean.setShortTermMAchange(KHelper.getShortTermMovingAverageChanges(stockList));
//					recentHBean.setGapDownStandUpSummary(gapdownAndStandUp);
//					recentHBean.setPriceBreakUpVolPushUpSummary(priceBreakVolPushUp);

					double estPrice = recentHBean.getCurrentStockBean().getL()
							+ (recentHBean.getCurrentStockBean().getH() - recentHBean.getCurrentStockBean().getL());
//					String txAmt = GeneralHelper.to2DecimalPlaces((estPrice*recentHBean.getCurrentStockBean().getVolume())/1000000);
					double estTradeAmount = (estPrice * recentHBean.getCurrentStockBean().getVolume()) / 1000000000;

					recentHBean.setEstTradeAmount(estTradeAmount);
					String isIncreaseVolTodayForMonthly = VolumePriceStructureHelper.getRecentDaysVolumeDescription(stockList, 1, 30);
					String isIncreaseVolForMonthly = VolumePriceStructureHelper.getRecentDaysVolumeDescription(stockList, 5, 20);
					String isIncreaseVolForQuarter = VolumePriceStructureHelper.getRecentDaysVolumeDescription(stockList, 10, 60);
					
//					String recentMonthDaysPctStatus = VolumePriceStructureHelper.getRecentDaysPctDescription(stockList, 3, 60);
					
					recentHBean.setTodayMonthDaysVolumeStatus(isIncreaseVolTodayForMonthly);
					recentHBean.setRecentMonthDaysVolumeStatus(isIncreaseVolForMonthly);
					recentHBean.setRecentQuarterDaysVolumeStatus(isIncreaseVolForQuarter);
//					recentHBean.setRecentMonthDaysPricePctStatus(recentMonthDaysPctStatus);
					
					recentHBean.setIsland(VolumePriceStructureHelper.getIsland(stockList));
					
					recentHBean.setReversalPatterns(this.getWMShapePattern(stockList));

					
					recentHBean.setStagnantMARatio((maSituation==null)?null:maSituation.getStagnantMARatio());
					recentHBean.setStagnantPrev1MARatio((maSituation==null)?null:maSituation.getStagnantPrev1MARatio());
					recentHBean.setStagnantPrev2MARatio((maSituation==null)?null:maSituation.getStagnantPrev2MARatio());
					recentHBean.setStagnantPrev3MARatio((maSituation==null)?null:maSituation.getStagnantPrev3MARatio());
					recentHBean.setStagnantMidTermMARatio((maSituation==null)?null:maSituation.getStagnantMidTermMARatio());
					recentHBean.setStagnantShortTermMARatio((maSituation==null)?null:maSituation.getStagnantShortTermMARatio());
					recentHBean.setAbv10D((maSituation==null)?null:maSituation.getAbv10D());
					recentHBean.setAbv20D((maSituation==null)?null:maSituation.getAbv20D());
					recentHBean.setAbv50D((maSituation==null)?null:maSituation.getAbv50D());
					recentHBean.setAbv100D((maSituation==null)?null:maSituation.getAbv100D());
					recentHBean.setAbv200D((maSituation==null)?null:maSituation.getAbv200D());
					recentHBean.setAbv5D((maSituation==null)?null:maSituation.getAbv5D());
//					recentHBean.setAbv2D(maSituatioin.isAbv2D());
//					recentHBean.setAbv19D(maSituatioin.isAbv19D());

					String ma200Situatioin = "";
					if (maSituation!=null && maSituation.isConsecutive5daysAbvCurrent200D()) {
						ma200Situatioin = "> 200D";
					} else if (maSituation!=null && maSituation.isConsecutive5daysBlwCurrent200D()) {
						ma200Situatioin = "< 200D";
					}
					recentHBean.setConsecutive5days200DStatus(ma200Situatioin);
					String ma50Situatioin = "";
					if (maSituation!=null && maSituation.isConsecutive5daysAbvCurrent50D()) {
						ma50Situatioin = "> 50D";
					} else if (maSituation!=null && maSituation.isConsecutive5daysBlwCurrent50D()) {
						ma50Situatioin = "< 50D";
					}
					recentHBean.setConsecutive5days50DStatus(ma50Situatioin);

//					recentHBean.setHighest2LowestPct(this.getTopToDonwRate(stockList)); // MA50 & MA200 relationship

					String msgMa50250 = Const.EMPTY;
					String msgma219 = Const.EMPTY;
					String msgMa50200 = Const.EMPTY;
					if(maSituation!=null && maSituation.getPriceSma()!=null) {
						double ma2 = maSituation.getPriceSma().getMa2();
						Double ma2Prev1 = maSituation.getMa2Prev1();
						Double ma2Prev2 = maSituation.getMa2Prev2();
						Double ma2Prev3 = maSituation.getMa2Prev3();

						double ma19 = maSituation.getPriceSma().getMa19();
						Double ma19Prev1 = maSituation.getMa19Prev1();
						Double ma19Prev2 = maSituation.getMa19Prev2();
						Double ma19Prev3 = maSituation.getMa19Prev3();

						double ma50 = maSituation.getPriceSma().getMa50();
						Double ma50Prev1 = maSituation.getMa50Prev1();
						Double ma50Prev2 = maSituation.getMa50Prev2();
						Double ma50Prev3 = maSituation.getMa50Prev3();

						double ma200 = maSituation.getPriceSma().getMa200();
						Double ma200Prev1 = maSituation.getMa200Prev1();
						Double ma200Prev2 = maSituation.getMa200Prev2();
						Double ma200Prev3 = maSituation.getMa200Prev3();

						double ma250 = maSituation.getPriceSma().getMa250();
						Double ma250Prev1 = maSituation.getMa250Prev1();
						Double ma250Prev2 = maSituation.getMa250Prev2();
						Double ma250Prev3 = maSituation.getMa250Prev3();


						if (ma19Prev1 != null && ma19Prev2 != null && ma19Prev3 != null) {
							if (ma2 > ma19) {
								msgma219 = "2 > 19";

								if (ma2Prev1 < ma19Prev1 && ma2Prev2 < ma19Prev2 && ma2Prev3 < ma19Prev3) {
									msgma219 += " NEW";
								} else if (ma2Prev1 > ma19Prev1 && ma2Prev2 < ma19Prev2 && ma2Prev3 < ma19Prev3) {
									msgma219 += " NEW (-1D)";
								} else if (ma2Prev1 > ma19Prev1 && ma2Prev2 > ma19Prev2 && ma2Prev3 < ma19Prev3) {
									msgma219 += " NEW (-2D)";
								}

							} else if (ma2 < ma19) {
								msgma219 = "2 < 19";

								if (ma2Prev1 > ma19Prev1 && ma2Prev2 > ma19Prev2 && ma2Prev3 > ma19Prev3) {
									msgma219 += " NEW";
								} else if (ma2Prev1 < ma19Prev1 && ma2Prev2 > ma19Prev2 && ma2Prev3 > ma19Prev3) {
									msgma219 += " NEW (-1D)";
								} else if (ma2Prev1 < ma19Prev1 && ma2Prev2 < ma19Prev2 && ma2Prev3 > ma19Prev3) {
									msgma219 += " NEW (-2D)";
								}
							}

							if(recentHBean.getCurrentStockBean().getC()>=ma2)
								msgma219 +=" 收在2D之上";
							else if(recentHBean.getCurrentStockBean().getC()>=ma19)
								msgma219 +=" 收在19D之上";
							else
								msgma219 +=" 收在19D之下";
						}

						// MA50 AND MA200 status

						if (ma200Prev1 != null && ma200Prev2 != null && ma200Prev3 != null) {
							if (ma50 > ma200) {
								msgMa50200 = "50 > 200";

								if (ma50Prev1 < ma200Prev1 && ma50Prev2 < ma200Prev2 && ma50Prev3 < ma200Prev3) {
									msgMa50200 += " NEW";
								} else if (ma50Prev1 > ma200Prev1 && ma50Prev2 < ma200Prev2 && ma50Prev3 < ma200Prev3) {
									msgMa50200 += " NEW (-1D)";
								} else if (ma50Prev1 > ma200Prev1 && ma50Prev2 > ma200Prev2 && ma50Prev3 < ma200Prev3) {
									msgMa50200 += " NEW (-2D)";
								}

							} else if (ma50 < ma200) {
								msgMa50200 = "50 < 200";

								if (ma50Prev1 > ma200Prev1 && ma50Prev2 > ma200Prev2 && ma50Prev3 > ma200Prev3) {
									msgMa50200 += " NEW";
								} else if (ma50Prev1 < ma200Prev1 && ma50Prev2 > ma200Prev2 && ma50Prev3 > ma200Prev3) {
									msgMa50200 += " NEW (-1D)";
								} else if (ma50Prev1 < ma200Prev1 && ma50Prev2 < ma200Prev2 && ma50Prev3 > ma200Prev3) {
									msgMa50200 += " NEW (-2D)";
								}
							}
							if(recentHBean.getCurrentStockBean().getC()>=ma50)
								msgMa50200 +=" 收50D之上";
							else
								msgMa50200 +=" 收在50D之下";
						}

						// MA50 AND MA250 status

						if (ma250Prev1 != null && ma250Prev2 != null && ma250Prev3 != null) {
							if (ma50 > ma250) {
								msgMa50250 = "50 > 250";

								if (ma50Prev1 < ma250Prev1 && ma50Prev2 < ma250Prev2 && ma50Prev3 < ma250Prev3) {
									msgMa50250 += " NEW";
								} else if (ma50Prev1 > ma250Prev1 && ma50Prev2 < ma250Prev2 && ma50Prev3 < ma250Prev3) {
									msgMa50250 += " NEW (-1D)";
								} else if (ma50Prev1 > ma250Prev1 && ma50Prev2 > ma250Prev2 && ma50Prev3 < ma250Prev3) {
									msgMa50250 += " NEW (-2D)";
								}

							} else if (ma50 < ma250) {
								msgMa50250 = "50 < 250";

								if (ma50Prev1 > ma250Prev1 && ma50Prev2 > ma250Prev2 && ma50Prev3 > ma250Prev3) {
									msgMa50250 += " NEW";
								} else if (ma50Prev1 < ma250Prev1 && ma50Prev2 > ma250Prev2 && ma50Prev3 > ma250Prev3) {
									msgMa50250 += " NEW (-1D)";
								} else if (ma50Prev1 < ma250Prev1 && ma50Prev2 < ma250Prev2 && ma50Prev3 > ma250Prev3) {
									msgMa50250 += " NEW (-2D)";
								}
							}

							if(recentHBean.getCurrentStockBean().getC()>=ma50)
								msgMa50200 +=" 收50D之上";
							else
								msgMa50200 +=" 收在50D之下";
						}
					}




					recentHBean.setMa219DStatus(msgma219);
					recentHBean.setMa50200DStatus(msgMa50200);
					recentHBean.setMa50250DStatus(msgMa50250);
//					recentHBean.setMa50250CrossUpDate(ma50250CrossUpDate);
//					recentHBean.setMa50200CrossUpDate(ma50200CrossUpDate);
//					recentHBean.setMa50200CrossDownDate(ma50200CrossDownDate);

					recentHBean.setDownBreakStandUpBean(dbsuBean);

					List<String> belongEtfs = SectorAnalystHelper
							.getETFBelongsTo(recentHBean.getCurrentStockBean().getStockCode());

					if (belongEtfs.isEmpty())
						recentHBean.setBelongETF(Const.NA);
					else
						recentHBean
								.setBelongETF(belongEtfs.toString().substring(1, belongEtfs.toString().length() - 1));

					List<String> belongSouthbound = SectorAnalystHelper
							.getSouthboundBelongsTo(recentHBean.getCurrentStockBean().getStockCode());
					if (belongSouthbound.isEmpty())
						recentHBean.setBelongSouthbound(Const.NA);
					else {
						recentHBean.setBelongSouthbound(
								belongSouthbound.toString().substring(1, belongSouthbound.toString().length() - 1));
					}

					PvrStockBean pvrbean = pvr.findPriceVolRelationship(stockList, Const.CHART_SHOW_DAYS);
					if (pvrbean!=null )//&& pvrbean.getVolumeHighRatio() > 2.0) {
					{
						//recentHBean.setIsCurrentAboveMaxVolStockBodyTop( (pvrbean.isCurrentAboveMaxVolStockBodyTop() ? "Y" : ""));
						//recentHBean.setIsCurrentBelowMaxVolStockBodyBottom( pvrbean.isCurrentBelowMaxVolStockBodyBottom() ? "Y" : "");
						//recentHBean.setMaxVolDate(pvrbean.getPriceSma().getMaxVolStockBean()==null?Const.NA:pvrbean.getPriceSma().getMaxVolStockBean().getTxnDate());
						//recentHBean.setMaxVolDateDesc(pvrbean.getPriceSma().getMaxVolStockBean()==null?Const.NA:pvrbean.getPriceSma().getMaxVolDesc());
						//recentHBean.setIsMonoPeakVolStockBean(pvrbean.isMonoPeakVolStockBean() ? "Y" : "");
						
//						pvrbean.getPriceSma().getMaxVolStockBean().getBodyDailyChgPct();
						//recentHBean.setNumOfDoubleVolumeDate(pvrbean.getNumOfDoubleVolumeDate());
						//recentHBean.setDoubleVolumeDateMsg(pvrbean.getDoubleVolumeDateMsg());

						//recentHBean.setNumOfShrinkageVolumeDate(pvrbean.getNumOfShrinkageVolumeDate());
						//recentHBean.setShrinkageVolumeDateMsg(pvrbean.getShrinkageVolumeDateMsg());
//						recentHBean.setShrinkageVolumeDateMsg(null);)
						
						//recentHBean.setHighVolumeDaySituation(pvrbean.getPriceSma().getMaxVolStockInRange());
						
//						VolumePriceStructureHelper.getHighVolumeDaySituation(stockList, CHART_SHOW_DAYS));
						recentHBean.setPriceVolStockBean(pvrbean);
						
						double volLowRatio = pvrbean.getVolumeLowRatio();
						double DEFINE_RATIO = 0.5;

						recentHBean.setExtremelyLowVolumeDate(
								(volLowRatio > DEFINE_RATIO) ? "" : pvrbean.getMinVolStockBean().getTxnDate());
						recentHBean.setExtremelyLowVolumeLowAndHigh((volLowRatio > DEFINE_RATIO) ? ""
								: pvrbean.getMinVolStockBean().getL() + " - " + pvrbean.getMinVolStockBean().getH());
					}

					

					StockBean histH = stockList.parallelStream().max(Comparator.comparingDouble(StockBean::getH)).get();

					List<StockBean> histHtoNowList = StreamTransformHelper.extractData(stockList, histH.getTxnDate(),
							recentHBean.getCurrentStockBean().getTxnDate());
					StockBean subsequentPeriodLowest = histHtoNowList.parallelStream()
							.min(Comparator.comparingDouble(StockBean::getL)).get(); // StockBean subsequentPeriodLowest
																						// =
																						// getLowestStockBean(histHtoNowList);
					double lowestPrice2CurrentPriceRatio = (recentHBean.getCurrentStockBean().getC()
							- subsequentPeriodLowest.getL()) / subsequentPeriodLowest.getL();

					recentHBean.setLowestStockBean(subsequentPeriodLowest);
					recentHBean.setLowest2CurrentRatio(lowestPrice2CurrentPriceRatio);

					//
//					String gapdownAndStandUp = Const.NA;
//					List<VolumePriceBean> gapDownStandUpLSs = vpStockList.parallelStream()
//							.filter(x -> x.getSignSet().contains(CandleTagEnum.EVNT_GAP_DOWN_AND_STAND_UP)
//									|| x.getSignSet().contains(CandleTagEnum.EVNT_BIG_DROP_AND_STAND_UP)
//									|| x.getSignSet().contains(CandleTagEnum.EVNT_UP_BREAKOUT_FAILURE)
//									|| x.getSignSet().contains(CandleTagEnum.EVNT_DOWNWARD_BREAKOUT_FAILURE)
//									|| x.getSignSet().contains(CandleTagEnum.EVNT_BULLISH_ABANDONED_BABY)
//									|| x.getSignSet().contains(CandleTagEnum.EVNT_BEARISH_ABANDONED_BABY)
//									|| x.getSignSet().contains(CandleTagEnum.EVNT_GBUB)
//									|| x.getSignSet().contains(CandleTagEnum.EVNT_GAPUP_AND_GO))
//							.collect(Collectors.toList());
//					if (gapDownStandUpLSs.isEmpty() == false) {
//						gapdownAndStandUp = gapDownStandUpLSs.get(gapDownStandUpLSs.size() - 1).toString();
//					}
//					recentHBean.setGapDownStandUpSummary(gapdownAndStandUp);
					

					

					//Bearish Pattern 看跌
					String bearishPatternDesc = Const.SPACE;
					List<VolumePriceBean> bearishPatternList = vpStockList.parallelStream()
							.filter(x -> x.getSignSet().contains(CandleEventTagEnum.EVNT_BEARISH_ENGULFING_2) //bad
									
									|| x.getSignSet().contains(CandleEventTagEnum.EVNT_UP_BREAKOUT_FAILURE) //bad
									
									|| x.getSignSet().contains(CandleEventTagEnum.EVNT_BEARISH_ABANDONED_BABY)) //bad
									
							.toList();
					//Bullish Pattern 看漲
					String bullishPatternDesc = Const.SPACE;
					List<VolumePriceBean> bullishPatternList = vpStockList.parallelStream()
							.filter(x -> x.getSignSet().contains(CandleEventTagEnum.EVNT_GAP_DOWN_AND_STAND_UP) //good
									|| x.getSignSet().contains(CandleEventTagEnum.EVNT_BIG_DROP_AND_STAND_UP) //good
									|| x.getSignSet().contains(CandleEventTagEnum.EVNT_BULLISH_ENGULFING_2) //good
									|| x.getSignSet().contains(CandleEventTagEnum.EVNT_DOWNWARD_BREAKOUT_FAILURE)//good
									|| x.getSignSet().contains(CandleEventTagEnum.EVNT_BULLISH_ABANDONED_BABY) //good
									|| x.getSignSet().contains(CandleEventTagEnum.EVNT_TWO_WHITE_WITH_BLACK) //good
									|| x.getSignSet().contains(CandleEventTagEnum.EVNT_GBUB) //good
									|| x.getSignSet().contains(CandleEventTagEnum.EVNT_GAPUP_AND_GO)//good
									)
							.toList();

					VolumePriceBean bullishVP = null;
					VolumePriceBean bearishVP = null;
					if (!bearishPatternList.isEmpty()) {
//						bearishPatternDesc = bearishPatternList.get(bearishPatternList.size() - 1).toString();
						bearishVP = bearishPatternList.get(bearishPatternList.size() - 1);
					}
					
					if (!bullishPatternList.isEmpty()) {
//						bullishPatternDesc = bullishPatternList.get(bullishPatternList.size() - 1).toString();
						bullishVP = bullishPatternList.get(bullishPatternList.size() - 1);
					}
					
					if( bullishVP!=null && bearishVP!=null && bullishVP.getTxnDateInt() > bearishVP.getTxnDateInt() ) {
						bullishPatternDesc = bullishVP.toString();
					}else if( bullishVP!=null && bearishVP!=null && bullishVP.getTxnDateInt() < bearishVP.getTxnDateInt() ) {
						bearishPatternDesc = bearishVP.toString();
					}else if( bullishVP!=null && bearishVP==null  ) {
						bullishPatternDesc = bullishVP.toString();
					}else if( bullishVP==null && bearishVP!=null  ) {
						bearishPatternDesc = bearishVP.toString();
					}
					
					recentHBean.setBullishPattern(bullishVP);
					recentHBean.setBearishPattern(bearishVP);


					
					recentHBean.setSysPickLongCategory(stockTagger.getTodayLongTrendTag(recentHBean));
					recentHBean.setSysPickStagnantCategory(stockTagger.getTodayStagnantTrendTag(recentHBean));
					recentHBean.setSysPickShortCategory(stockTagger.getTodayShortTrendTag(recentHBean));
//					recentHBean.setSysPickCategory(stockTagger.tagStockPick(recentHBean));
					
					recentHBean.setMovingAverageCrossUpSummary(vpMaCrossUPStockList.toString());
					
					
					
					
					
					List<String> concentratedVP = stockAnalyzer.findTop3ConcentratedVolumePriceRanges(stockList, 100);
					String closestPriceRange = stockAnalyzer.findClosestPriceRange(recentHBean.getCurrentStockBean(), concentratedVP);
					recentHBean.setConcentratedVolumePriceRange(concentratedVP.toString());
					recentHBean.setClosestVolumePriceRange(closestPriceRange);
					
//					MACDDataCaculator macd = new MACDDataCaculator();
//					//macd.calculateMACD(stockList);
//					recentHBean.setMacdData(macd.calculateIndicators(stockList));
					
					recentHighList.add(recentHBean);
					
					

				} 
			} catch (Exception e) {
				log.error("ERROR to process " + code, e);
			}
		}
		
//		log.info("Skipped Ticker: "+skippedTickers.size());
		
		return recentHighList;
	}

	public InstantPerformanceResult findRecordHigh(List<StockBean> stockList) {

		StockBean nowStock = stockList.get(stockList.size() - 1);
		StockBean prevStock = stockList.get(stockList.size() - 2);
		StockBean prev2Stock = stockList.get(stockList.size() - 3);
		StockBean prev3Stock = stockList.get(stockList.size() - 3);


		InstantPerformanceResult rtn = new InstantPerformanceResult(stockList);

		rtn.setCurrentStockBean(nowStock);
		rtn.setPrevStockBean(prevStock);
		rtn.setPrev2StockBean(prevStock);
		
		Double ytd = this.getPerformance(stockList, "01-01", null);
		Double q1 = this.getPerformance(stockList, "01-01", "03-31");
		Double q2 = this.getPerformance(stockList, "04-01", "06-30");
		Double q3 = this.getPerformance(stockList, "07-01", "09-30");
		Double q4 = this.getPerformance(stockList, "10-01", "12-31");

		String breakPrevMonthStatus = this.funcLastMonthAndCurrentMonthRelationship(stockList);
		rtn.setBreakPrevMonthStatus(breakPrevMonthStatus);
		rtn.setYtd(ytd);
		rtn.setQ1(q1);
		rtn.setQ2(q2);
		rtn.setQ3(q3);
		rtn.setQ4(q4);

		rtn.setDailyChangePct(AnalyGeneralHelper.getDailyChangePct(stockList));
		rtn.setDailyChangeHighestPct(AnalyGeneralHelper.getDailyChangeHighestPct(stockList));
		rtn.setDailyChangeLowestPct(AnalyGeneralHelper.getDailyChangeLowestPct(stockList));
		
		rtn.setThreeDaysChangeO2CPct(AnalyGeneralHelper.getThreeDaysPct(stockList, KType.END2END_O2C));				
		rtn.setThreeDaysChangeO2HPct(AnalyGeneralHelper.getDaysPct(stockList, KType.O2PH, 3));
		rtn.setThreeDaysChangeO2LPct(AnalyGeneralHelper.getDaysPct(stockList, KType.O2PL, 3));
		
		rtn.setWeeksChangeO2CPct(AnalyGeneralHelper.getDaysPct(stockList, KType.END2END_O2C, 5));
		rtn.setWeeksChangeO2PHPct(AnalyGeneralHelper.getDaysPct(stockList, KType.O2PH, 5));
		rtn.setWeeksChangeO2PLPct(AnalyGeneralHelper.getDaysPct(stockList, KType.O2PL, 5));
		
		rtn.setMtdChangeO2CPct(AnalyGeneralHelper.getMTDPct(stockList, KType.END2END_O2C));		
		rtn.setMtdChangeO2PHPct(AnalyGeneralHelper.getMTDPct(stockList, KType.O2PH));
		rtn.setMtdChangeO2PLPct(AnalyGeneralHelper.getMTDPct(stockList, KType.O2PL));
		
		rtn.setMtdPctRange(AnalyGeneralHelper.getMTDPctRange(stockList));
		rtn.setMtdPeriodHDate(AnalyGeneralHelper.getMTDPeriodH(stockList).getTxnDate());
		rtn.setMtdPeriodLDate(AnalyGeneralHelper.getMTDPeriodL(stockList).getTxnDate());
		
		
//		rtn.setDailyCandleDescription(CandleStickHelper.getTodayCandleDescription(prevStock, nowStock));
		rtn.setDailyImportantCandlestickTradingPattern(KHelper.getTodayImportantCandlestickTradingPattern(stockList));
		rtn.setDailyCandleStatus(KHelper.isBigBodyToday(stockList));
		rtn.setPrev1DayCandleStatus(KHelper.isBigBodyPrevDay(stockList));
		rtn.setDailyVolDescription(VolumePriceStructureHelper.getComparisonOfTwoDaysTradingVolumes(prevStock, nowStock));
		rtn.setPrev1DayVolDescription(VolumePriceStructureHelper.getComparisonOfTwoDaysTradingVolumes(prev2Stock, prevStock));
		rtn.setPrev2DayVolDescription(VolumePriceStructureHelper.getComparisonOfTwoDaysTradingVolumes(prev3Stock, prev2Stock));
//		WavePointAnalyticalResult wavePointResult = waveTopBotHandler.doTopBot(stockList);
		WaveShape waveShape = waveTopBotHandler.doTopBot(stockList);
		rtn.setWaveShape(waveShape);
//		rtn.setWaveShape(waveShape.getShapeResult());
//		rtn.setWaveTopBottomStatus(wavePointResult);
		rtn.setDownTreandReversalDate(downTrendReversal.doIt(stockList));
		
		List<WavePoint> topList = waveShape.getSortedTopList();
		List<WavePoint> botList = waveShape.getSortedBotList();
//		String rsiDivr = this.calcRsiDiverence(stockList, topList, botList);
//		DivergenceResult rsiDivr = RSICalculator.getInstance().calcRsiDiverence(stockList, topList, botList);
		
		DivergenceResult rsiDivr = DivergenceFinder.getInstance().findRsiDiverence(stockList, topList, botList);		
//		DivergenceResult pvDivr = DivergenceFinder.getInstance().findPriceVolDiverence(stockList);
		
//		if(pvDivr!=null) {
//			log.info(nowStock.getStockCode() + "\t"+KPatternConst.DIVERGENCE_PRICE_RISE_VOL_SHRINK + "\t"+nowStock.getTxnDate() );
//		}
		
		rtn.setRsiDiverence(rsiDivr);
//		rtn.setPvDiverence(waveShape.getRecentVolPriceDivergencyResult());
		int startIdx = stockList.size() - 1;
		int absLIndex = startIdx;
		int absHIndex = startIdx;

//		StockBean backL2H = waveHelper.findBackwardLowToHigh(stockList, absLIndex);
//		StockBean backH2L = waveHelper.findBackwardHighToLow(stockList, absHIndex);
		int days = 60;
//		String yearHighAchievedThisMonth = this.getNewHighReachedWithinTheMonth(stockList, days);
//		rtn.setYearHighAchievedThisMonth(yearHighAchievedThisMonth);

		String maLongArrangeWithinTheMonth = this.getMovingAvgLongArrangementWithinTheMonth(stockList, days);					
		rtn.setMovingAvgLongArrangementWithinTheMonth(maLongArrangeWithinTheMonth);

		rtn.setPercentageDifferenceFromHighestPrice(this.getPercentageDifferenceFromHighestPrice(stockList, 50));
		rtn.setTriplePregnancyInPassFewDays(this.findTriplePregnancyInPassFewDays(stockList, 10));
		

		rtn.setBigDarkBodyWithMoreVol(LargeCandleStickAnalyzer.findLargeCandlestickAndHigherClose(stockList, 10));
		rtn.setBigWhiteBodyWithMoreVol(LargeCandleStickAnalyzer.findLargeCandlestickAndLowerClose(stockList, 10));
		
//		String upDownBreakThreeWavePointToday = this.getUpDownBreak3WavePoint(stockList, waveShape, days);
//		rtn.setUpDownBreakThreeWavePointToday(upDownBreakThreeWavePointToday);
		
		String largeVolumeWithinTheMonth = this.getLargeVolumeWithinTheMonth(stockList, days);
		rtn.setLargeVolumeWithinTheMonth(largeVolumeWithinTheMonth);
		
		String maLongSideSupport = this.getMALongSideSupportSingalInRecentDays(stockList, 3);
		rtn.setMovingAverageLongSideSupport(maLongSideSupport);
		
		String bbUpBreakAtime = this.getBollingerUpBreakForATimeInRecentDays(stockList, 5);
		rtn.setBbUpBreakForATime(bbUpBreakAtime);
		rtn.setLastEngulfingInRecentDays(this.getLastEngulfingInRecentDays(stockList,topList, botList, 5));

		return rtn;

	}

	public Double getPerformance(List<StockBean> stockList, String START_MM_DD, String END_MM_DD) {
		StockBean nowStock = stockList.get(stockList.size() - 1);
		LocalDate now = LocalDate.parse(nowStock.getTxnDate());

		List<StockBean> subList = null;
		if (END_MM_DD != null)
			subList = StreamTransformHelper.extractData(stockList, now.getYear() + "-" + START_MM_DD,
					now.getYear() + "-" + END_MM_DD);
		else
			subList = StreamTransformHelper.extractData(stockList, now.getYear() + "-" + START_MM_DD,
					nowStock.getTxnDate());

		if (subList.isEmpty()) {
			return null;
		} else {
			double percentage = (subList.get(subList.size() - 1).getC() - subList.get(0).getO())
					/ subList.get(0).getO();
			return percentage;
		}

	}

	public BaseMWShapeInfo getWMShapePattern(List<StockBean> orgstockList) {
//		String typeSB = "";
		BaseMWShapeInfo actualShapInfo = null;
		int numOfDays = RuleConst.DAYS_60;
		if (orgstockList.size() < numOfDays) {

			numOfDays = orgstockList.size();
		}
		try {
			List<StockBean> dateRangeTrunkList = orgstockList.subList(orgstockList.size() - numOfDays,
					orgstockList.size());
			BaseMWShapeInfo patternInfoM1 = mpatternAnaly.doMPatternHL(dateRangeTrunkList);
			BaseMWShapeInfo patternInfoM2 = mpatternAnaly.doMPatternLH(dateRangeTrunkList);
			
			BaseMWShapeInfo wPatternInfoW1 = mpatternAnaly.doWPatternLH(dateRangeTrunkList);
			BaseMWShapeInfo wPatternInfoW2 = mpatternAnaly.doWPatternHL(dateRangeTrunkList);

			int tnxDateInt = 0; 
			
			if (patternInfoM1 != null) {
//			mResultList.add(patternInfoM1);
//				typeSB += (patternInfoM1.getType() + ",");
				tnxDateInt = patternInfoM1.getCriticalPointBtwn().getTxnDateInt();
				actualShapInfo = patternInfoM1;
			}
			if (patternInfoM2 != null) {
//				typeSB += (patternInfoM2.getType() + ",");
				if( patternInfoM2.getCriticalPointBtwn().getTxnDateInt()>tnxDateInt) {
					tnxDateInt = patternInfoM2.getCriticalPointBtwn().getTxnDateInt();
					actualShapInfo = patternInfoM2;
				}
				
			}

			if (wPatternInfoW1 != null) {
//				typeSB += (wPatternInfoW1.getType() + ",");
				if( wPatternInfoW1.getCriticalPointBtwn().getTxnDateInt()>tnxDateInt) {
					tnxDateInt = wPatternInfoW1.getCriticalPointBtwn().getTxnDateInt();
					actualShapInfo = wPatternInfoW1;
				}
			}
			if (wPatternInfoW2 != null) {
//				typeSB += (wPatternInfoW2.getType() + ",");
				if( wPatternInfoW2.getCriticalPointBtwn().getTxnDateInt()>tnxDateInt) {
					tnxDateInt = wPatternInfoW2.getCriticalPointBtwn().getTxnDateInt();
					actualShapInfo = wPatternInfoW2;
				}
			}
		} catch (Exception e) {
			log.error(orgstockList.get(orgstockList.size() - 1).getStockCode() + " ERROR occur", e);

		}
//		if (typeSB.isEmpty() == false)
//			typeSB = typeSB.substring(0, typeSB.length() - 1);
		return actualShapInfo;
	}

	public String funcLastMonthAndCurrentMonthRelationship(List<StockBean> stockList) {
		String msg =  Const.NA;
		StockBean nowStock = stockList.get(stockList.size() - 1);// 012345678
		try {
		
			String prevMonthDateString = DateHelper.plusMonths(nowStock.getTxnDate(), -1, DateHelper.YYYY_MM_DD);
	//		List<StockBean> prevMonthStockList = StreamTransformHelper.extractByStockCode(stockList, prevMonthDateString.substring(0,7));
	
			List<StockBean> prevMonthStockList = stockList.parallelStream()
					.filter(x -> x.getTxnDate().contains(prevMonthDateString.substring(0, 7))).collect(Collectors.toList());
	
	//		Double prevMonthH = prevMonthStockList.stream().mapToDouble(StockBean::getH).max().orElse(Double.NaN);
	//		Double prevMonthL = prevMonthStockList.stream().mapToDouble(StockBean::getL).min().orElse(Double.NaN);
	
			StockBean prevMonthHStockBean = prevMonthStockList.stream().max(Comparator.comparingDouble(StockBean::getH))
					.get();
			StockBean prevMonthLStockBean = prevMonthStockList.stream().min(Comparator.comparingDouble(StockBean::getL))
					.get();
			Double prevMonthH = prevMonthHStockBean.getH();
			Double prevMonthL = prevMonthLStockBean.getH();
	
			if (nowStock.getC() > prevMonthH) {
				msg = "C > 上月-H";
			} else if (nowStock.getH() > prevMonthH) {
				msg = "H > 上月-H";
			} else if (nowStock.getC() < prevMonthL) {
				msg = "C < 上月-L";
			} else if (nowStock.getL() < prevMonthL) {
				msg = "L < 上月-L";
			} else {
				msg = Const.NA;
			}

		}catch(Exception e) {
			log.warn(nowStock.getStockCode() + ", "+e.getMessage());
		}
		return msg;
	}

	public double getTopToDonwRate(List<StockBean> stockList) {
		StockBean nowStock = stockList.get(stockList.size() - 1);
		StockBean histH = stockList.parallelStream().max(Comparator.comparingDouble(StockBean::getH)).get();

//		double upSideRatioToHistoricalHigh = (histH.getH() - nowStock.getC() ) / nowStock.getC();
//		double upSideRatioToYearHigh = (histH.getH() - nowStock.getC() ) / nowStock.getC();
		///////////////

		List<StockBean> histHtoNowList = StreamTransformHelper.extractData(stockList, histH.getTxnDate(),nowStock.getTxnDate());
		StockBean subsequentPeriodLowest = histHtoNowList.parallelStream()
				.min(Comparator.comparingDouble(StockBean::getL)).get();

		// again
//		List<StockBean> periodLtoNowList = StreamTransformHelper.extractData(stockList , subsequentPeriodLowest.getTxnDate(), nowStock.getTxnDate());			
//		StockBean recoverMaxH = periodLtoNowList.parallelStream().max(Comparator.comparingDouble(StockBean::getH)).get();//getHighestStockBean(histHtoNowList);

		// days from Highest score date
//		int daysFromH2Current = DateHelper.dayBetween(histH.getTxnDate(), nowStock.getTxnDate());
//		
//		int daysFromH2L = DateHelper.dayBetween( histH.getTxnDate(),subsequentPeriodLowest.getTxnDate());

		double adjustRatioFromH2L = (subsequentPeriodLowest.getL() - histH.getH()) / histH.getH();

		return adjustRatioFromH2L;
	}
	
	//New high reached within the month, newHighReachedWithinTheMonth
	public String getNewHighReachedWithinTheMonth(List<StockBean> stockList, int numberOfDaysInPastMonth) {
		
//		int numberOfDaysInPastMonth = 30;
		StockBean last = stockList.get(stockList.size()-1);
		
		StockBean is250DHighest = this.getLongDayNewHigh(stockList, 250, numberOfDaysInPastMonth);
		StockBean is100DHighest = this.getLongDayNewHigh(stockList, 100, numberOfDaysInPastMonth);
		StockBean is50DHighest = this.getLongDayNewHigh(stockList, 50, numberOfDaysInPastMonth);
		StockBean is20DHighest = this.getLongDayNewHigh(stockList, 20, numberOfDaysInPastMonth);
		
		StockBean is250DLowest = this.getLongDayNewLow(stockList, 250, numberOfDaysInPastMonth);
		StockBean is100DLowest = this.getLongDayNewLow(stockList, 100, numberOfDaysInPastMonth);
		StockBean is50DLowest = this.getLongDayNewLow(stockList, 50, numberOfDaysInPastMonth);
		StockBean is20DLowest = this.getLongDayNewLow(stockList, 20, numberOfDaysInPastMonth);
		
		StockBean newHighBean = null; //default
		StockBean newLowBean = null; //default
				
		String newHighMsg = Const.SPACE;
		String newLowMsg = Const.SPACE;
		
		//check new high
		if(is250DHighest!=null) {
			newHighMsg = "創250新高";
			newHighBean = is250DHighest;
		}else if(is100DHighest!=null) {
			newHighMsg = "創100新高";
			newHighBean = is100DHighest;
		}else if(is50DHighest!=null) {
			newHighMsg = "創50新高";
			newHighBean = is50DHighest;
		}else if(is20DHighest!=null) {
			newHighMsg = "創"+numberOfDaysInPastMonth+"新高";
			newHighBean = is20DHighest;
		}
		
		if(newHighBean!=null ) {
			int txDays = StreamTransformHelper.txDaysBetween(stockList, newHighBean.getTxnDate(), last.getTxnDate());
			if(txDays<=2)
				newHighMsg += "(最近)";
		}
		
		
		//check new low
		if(is250DLowest!=null) {
			newLowMsg = "創250新低";
			newLowBean = is250DLowest;
		}else if(is100DLowest!=null) {
			newLowMsg = "創100新低";
			newLowBean = is100DLowest;
		}else if(is50DLowest!=null) {
			newLowMsg = "創50新低";
			newLowBean = is50DLowest;
		}else if(is20DLowest!=null) {
			newLowMsg = "創"+numberOfDaysInPastMonth+"新低";
			newLowBean = is20DLowest;
		}
		
		if(newLowBean!=null ) { 
			int txDays = StreamTransformHelper.txDaysBetween(stockList, newLowBean.getTxnDate(), last.getTxnDate());
//			newLowMsg += "("+(0-txDays)+"D)";
			if(txDays<=2)
				newLowMsg += "(最近)";
		}
		

	//// section 2
		double pct = 0;
		

		int flagHighOrLow = 0; // 0: non, 1: High, -1:Low
		///compare and generate the return result
		String resultMsg = "";
		if(newHighBean!=null && newLowBean!=null && newHighBean.getTxnDateInt() > newLowBean.getTxnDateInt()) {
			resultMsg = newLowMsg +" -> "+ newHighMsg;
			flagHighOrLow = 1;
		}else if(newHighBean!=null && newLowBean!=null && newLowBean.getTxnDateInt() > newHighBean.getTxnDateInt()) {
			resultMsg = newHighMsg +" -> "+ newLowMsg;
			flagHighOrLow = -1;
		}else if(newHighBean!=null && newLowBean!=null && newLowBean.getTxnDateInt() == newHighBean.getTxnDateInt() && newLowBean.isRiseToday()) {
			resultMsg = newHighMsg;
			flagHighOrLow = 1;
		}else if(newHighBean!=null && newLowBean!=null && newLowBean.getTxnDateInt() == newHighBean.getTxnDateInt() && newLowBean.isRiseToday()==false) {
			resultMsg = newLowMsg;
			flagHighOrLow = -1;
		}else if(newHighBean!=null && newLowBean==null) {
			resultMsg = newHighMsg;
			flagHighOrLow = 1;
		}
		else if(newHighBean==null && newLowBean!=null) {
			resultMsg = newLowMsg;
			flagHighOrLow = -1;
		}
		else if(newHighBean==null && newLowBean==null) {
			resultMsg = Const.SPACE;
			flagHighOrLow = 0;
		}
		
		
		//月內拉升/拉跌 pct
		if(flagHighOrLow >0 && newLowBean!=null) {
			pct = (newHighBean.getH() - newLowBean.getL()) /newLowBean.getL();
			
		}else if(flagHighOrLow < 0 && newHighBean!=null) {
			pct = (newLowBean.getL() - newHighBean.getH()) / newHighBean.getH();
			
		}else {
			int startIdx = stockList.size() - numberOfDaysInPastMonth;		
			
			if(startIdx > 0) 
			{
				List<StockBean> sublist = stockList.subList(startIdx, stockList.size());
				StockBean maxHStock = StreamTransformHelper.findMaxHighStock(sublist);
				StockBean minLStock = StreamTransformHelper.findMinLowStock(sublist);
					
				if(maxHStock.getTxnDateInt() > minLStock.getTxnDateInt()) {
					pct = ( maxHStock.getH() - minLStock.getL() ) / minLStock.getL();
				}
				
				if(maxHStock.getTxnDateInt() < minLStock.getTxnDateInt()) {
					pct = (minLStock.getL() - maxHStock.getH()  ) / maxHStock.getH();
				}
			}
		}
		
		resultMsg = resultMsg + "\t"+ GeneralHelper.toPct(pct);
		return resultMsg;

	}
	
	private StockBean getLongDayNewHigh(List<StockBean> stockList, int lenghOfDays, int withinTecentDays) {
		if(stockList.size() < lenghOfDays)
			return null;

		int maxDays = lenghOfDays + withinTecentDays;
		
		if(stockList.size() >= maxDays) {			
			
			int startIdx = stockList.size() - withinTecentDays;
			
			List<StockBean> passMontStockList = stockList.subList(startIdx, stockList.size());
			
			StockBean maxHStock = StreamTransformHelper.findMaxHighStock(passMontStockList);
			int targetMaxHIndex = StreamTransformHelper.findIndex(stockList, maxHStock.getTxnDate());
			int startIdxFor250D = targetMaxHIndex - lenghOfDays;
			if(startIdxFor250D <0)
					return null;
			List<StockBean> subList = stockList.subList(startIdxFor250D, targetMaxHIndex+1);
			
			StockBean maxHStock2 = StreamTransformHelper.findMaxHighStock(subList);
			
			if(maxHStock.getTxnDateInt() == maxHStock2.getTxnDateInt())
				return maxHStock;
//				return true;
			return null;
			
		}else {
			return null;
		}
	}
	
	private StockBean getLongDayNewLow(List<StockBean> stockList, int lenghOfDays, int withinTecentDays) {
		if(stockList.size() < lenghOfDays)
			return null;
		
//		int numOfDaysInRecentPeriod = 30;
		int maxDays = lenghOfDays + withinTecentDays;
		
		if(stockList.size() >= maxDays) {			
			
			int startIdx = stockList.size() - withinTecentDays;
			
			List<StockBean> passMontStockList = stockList.subList(startIdx, stockList.size());
			
			StockBean minLStock = StreamTransformHelper.findMinLowStock(passMontStockList);
			int targetMinLIndex = StreamTransformHelper.findIndex(stockList, minLStock.getTxnDate());
			int startIdxFor250D = targetMinLIndex - lenghOfDays;
			
			if(startIdxFor250D <0)
				return null;
			
			List<StockBean> subList = stockList.subList(startIdxFor250D, targetMinLIndex+1);
			
			StockBean minLStock2 = StreamTransformHelper.findMinLowStock(subList);
			
			if(minLStock.getTxnDateInt() == minLStock2.getTxnDateInt())
				return minLStock;
			return null;
			
		}else {
			return null;
		}
	}

	public String getPercentageDifferenceFromHighestPrice(List<StockBean> srcstockList, int days) {
		if(srcstockList.size() < days) {
			return Const.EMPTY;
		}


		List<StockBean> stockList = srcstockList.subList(srcstockList.size() - days, srcstockList.size());
		StockBean maxStock = null;
//		StockBean minStock = null;

		maxStock = stockList.stream().max(Comparator.comparingDouble(StockBean::getH)).orElse(null);
//		minStock = stockList.stream().min(Comparator.comparingDouble(StockBean::getL)).orElse(null);


		if(maxStock.getTxnDateInt() == srcstockList.getLast().getTxnDateInt() ){
			return "TODAY";
		}

		// 获取最后一天的 C 值
		double lastDayCValue = srcstockList.get(srcstockList.size() - 1).getC();

		// 计算与最高价和最低价的百分比差异
//		double maxPercentageDifference = ((lastDayHValue - maxStock.getH()) / maxStock.getH()) * 100;
//		double minPercentageDifference = ((lastDayHValue - minStock.getH()) / minStock.getH()) * 100;

		double maxDifference = ((lastDayCValue - maxStock.getH()) / maxStock.getH());
		//Percentage difference from highest price
//		System.out.println("与最高价的百分比差异: " + maxPercentageDifference + "%");
//		System.out.println("与最低价的百分比差异: " + minPercentageDifference + "%");

		return GeneralHelper.toPct(maxDifference);
	}

	//New high reached within the month, newHighReachedWithinTheMonth
		public String getMovingAvgLongArrangementWithinTheMonth(List<StockBean> srcstockList, int days) {
			
//			int numberOfDaysInPastMonth = 30;
			if(srcstockList.size() < days) {
				return Const.SPACE;
			}
			
			
			List<StockBean> stockList = srcstockList.subList(srcstockList.size() - days, srcstockList.size());
			StockBean last = stockList.get(stockList.size()-1);
			StockBean middle = stockList.get(15);
			StockBean first = stockList.get(0);
			
			boolean isFoundMAChangeToLong = false;
			boolean isLongArrange = false;
			boolean isShortArrange = false;
			
			int idx = stockList.size()-1;
			for(; idx >0; idx--) 
			{
				StockBean prev = stockList.get(idx-1);
				StockBean curr = stockList.get(idx);
				
				if(prev.getPriceSma()==null || curr.getPriceSma()==null) {
//					System.out.println("");
					continue;
				}
				
				boolean isPrevLongArrange = (prev.getPriceSma().getMa10() > prev.getPriceSma().getMa20() && prev.getPriceSma().getMa20() > prev.getPriceSma().getMa50() && prev.getPriceSma().getMa50() > prev.getPriceSma().getMa100());
				boolean isCurrLongArrange = (curr.getPriceSma().getMa10() > curr.getPriceSma().getMa20() && curr.getPriceSma().getMa20() > curr.getPriceSma().getMa50() && curr.getPriceSma().getMa50() > curr.getPriceSma().getMa100());
				
				if(!isPrevLongArrange && isCurrLongArrange) {
					isFoundMAChangeToLong = true;
					break;
				}
			}// end loop
			
			// Check ALL the TIME is a LONG MA arrangement 
			if( first.getPriceSma()!=null && last.getPriceSma()!=null 
				&& (first.getPriceSma().getMa10() > first.getPriceSma().getMa20() && first.getPriceSma().getMa20() > first.getPriceSma().getMa50() && first.getPriceSma().getMa50() > first.getPriceSma().getMa100())
				&&
				(last.getPriceSma().getMa10() > last.getPriceSma().getMa20() && last.getPriceSma().getMa20() > last.getPriceSma().getMa50() && last.getPriceSma().getMa50() > last.getPriceSma().getMa100())
				&&	
				(last.getPriceSma().getMa10()>=first.getPriceSma().getMa10() && last.getPriceSma().getMa20()>=first.getPriceSma().getMa20() && last.getPriceSma().getMa50()>=first.getPriceSma().getMa50() && last.getPriceSma().getMa100()>=first.getPriceSma().getMa100())
				) 
			{
				isLongArrange = true;
				
			}
			
			
			if( first.getPriceSma()!=null && last.getPriceSma()!=null 
					&& 
					(first.getPriceSma().getMa10() < first.getPriceSma().getMa20() && first.getPriceSma().getMa20() < first.getPriceSma().getMa50() && first.getPriceSma().getMa50() < first.getPriceSma().getMa100())
					&&
					(last.getPriceSma().getMa10() < last.getPriceSma().getMa20() && last.getPriceSma().getMa20() < last.getPriceSma().getMa50() && last.getPriceSma().getMa50() < last.getPriceSma().getMa100())
					&&	
					(last.getPriceSma().getMa10()<=first.getPriceSma().getMa10() && last.getPriceSma().getMa20()<=first.getPriceSma().getMa20() && last.getPriceSma().getMa50()<=first.getPriceSma().getMa50() && last.getPriceSma().getMa100()<=first.getPriceSma().getMa100())
					) 
				{
					isShortArrange = true;
				}
			
			
			// determine RESULT
			String rtn = Const.SPACE;
			
			if(isFoundMAChangeToLong) 
			{		
				StockBean target = stockList.get(idx);
				boolean b = last.getPriceSma().getMa10()>=target.getPriceSma().getMa10() && last.getPriceSma().getMa20()>=target.getPriceSma().getMa20() && last.getPriceSma().getMa50()>=target.getPriceSma().getMa50() && last.getPriceSma().getMa100()>=target.getPriceSma().getMa100();
				if(b)
					rtn = target.getTxnDate();
				
			}else if(isFoundMAChangeToLong == false  && isLongArrange ==true) {
				rtn = MAStatusEnum.LONG_ARRANGE.toString();
								
				double distanceMiddle = middle.getPriceSma().getMa10() - middle.getPriceSma().getMa50();
				double distanceLast = last.getPriceSma().getMa10() - last.getPriceSma().getMa50();				
				
				double expendRatio = distanceLast / distanceMiddle;
				
				if(expendRatio > 1.1) {
					rtn += "-張開";
				}
				
			}else if(isShortArrange ==true) {
				rtn = MAStatusEnum.SHORT_ARRANGE.toString();
			}
			
			return rtn;			
		}

	public String findTriplePregnancyInPassFewDays(List<StockBean> srcstockList, int days) {
		if (srcstockList.size() < days) {
			return Const.SPACE;
		}
		int pregnancyDays = 3;

		String returnDate = "";
		List<StockBean> stockBeans = srcstockList.subList(srcstockList.size() - days, srcstockList.size());

		for (int i = 0; i < stockBeans.size() - (pregnancyDays ); i++) {
			StockBean a = stockBeans.get(i);
			List<StockBean> subsequentBeans = IntStream.range(i + 1, i + pregnancyDays+1)
					.mapToObj(stockBeans::get)
					.collect(Collectors.toList());

			if (subsequentBeans.stream().allMatch(b -> isCovering(a, b))) {
				returnDate = subsequentBeans.get(subsequentBeans.size() - 1).getTxnDate();
			}
		}

		return returnDate;
	}

	private boolean isCovering(StockBean a, StockBean b) {
		// 实现 isCovering 方法的逻辑
		// 示例：假设 a 的最高价大于等于 b 的最高价且 a 的最低价小于等于 b 的最低价
		return a.getBodyTop() >= b.getBodyTop() && a.getBodyBottom() <= b.getBodyBottom();
	}
		
//		public String findTriplePregnancyInPassFewDays(List<StockBean> srcstockList, int days) {
//			if(srcstockList.size() < days) {
//				return Const.SPACE;
//			}
//
//			String returnDate = "";
//			List<StockBean> stockBeans = srcstockList.subList(srcstockList.size() - days, srcstockList.size());
//			for (int i = 0; i < stockBeans.size() - 3; i++) {
//				StockBean a = stockBeans.get(i);
//				StockBean b = stockBeans.get(i + 1);
//				StockBean c = stockBeans.get(i + 2);
//				StockBean d = stockBeans.get(i + 3);
//				if (isCovering(a, b) && isCovering(a, c) && isCovering(a, d)) {
//					returnDate = d.getTxnDate();
//				}
//			}
//
//
//			return returnDate;
//
//		}
//
//		private static boolean isCovering(StockBean a, StockBean b) {
//			return a.o <= b.o && a.o <= b.c && a.c >= b.o && a.c >= b.c;
//		}

//		public String getUpDownBreak3WavePoint(List<StockBean> srcstockList, WaveShape wavePointResult, int days) {
//
//			if(wavePointResult.getSortedTopList().isEmpty()|| wavePointResult.getSortedTopList().size() <3)
//				return Const.SPACE;
//
//			StockBean last1 = srcstockList.get(srcstockList.size()-1);
//			StockBean last2 = srcstockList.get(srcstockList.size()-2);
//
//			WavePoint lastTop1 = wavePointResult.getSortedTopList().get(wavePointResult.getSortedTopList().size()-1);
//			WavePoint lastTop2 = wavePointResult.getSortedTopList().get(wavePointResult.getSortedTopList().size()-2);
//			WavePoint lastTop3 = wavePointResult.getSortedTopList().get(wavePointResult.getSortedTopList().size()-3);
//
//			boolean bUpBCheck1 = (last1.getBodyBottom() <= lastTop1.getH() && last1.getBodyBottom() <= lastTop2.getH() && last1.getBodyBottom() <= lastTop3.getH());
//			boolean bUpBCheck2 = (last1.getC() >= lastTop1.getH()) && (last1.getC() >= lastTop2.getH()) && (last1.getC() >= lastTop3.getH());
//			boolean bNoUpBreakYesterday = last2.getBodyTop() < lastTop1.getH();
//			boolean bUpBCheck3 =  (Const.IS_INTRADAY==true)?true:last1.getDayVolumeChgPct() >1.2;
//			boolean bUpBreakOne = (last1.getC() >= lastTop1.getH()) && (last1.getC() < lastTop2.getH()) && (last1.getC() < lastTop3.getH()) ;
//
//
//			if(wavePointResult.getSortedBotList().isEmpty() || wavePointResult.getSortedBotList().size() <3)
//				return Const.SPACE;
//
//			WavePoint lastBot1 = wavePointResult.getSortedBotList().get(wavePointResult.getSortedBotList().size()-1);
//			WavePoint lastBot2 = wavePointResult.getSortedBotList().get(wavePointResult.getSortedBotList().size()-2);
//			WavePoint lastBot3 = wavePointResult.getSortedBotList().get(wavePointResult.getSortedBotList().size()-3);
//
//			boolean bDownBreakCheck1 = (last1.getBodyTop() >= lastBot1.getL() && last1.getBodyTop() >= lastBot2.getL() && last1.getBodyTop() >= lastBot3.getL());
//			boolean bDownBreakCheckt2 = (last1.getC() < lastBot1.getL()) && (last1.getC() < lastBot2.getL()) && (last1.getC() < lastBot3.getL());
//			boolean bNoDownBreakYesterday = (last1.getBodyBottom() > lastBot1.getL());
//			boolean bDownBreakOne = (last1.getC() < lastBot1.getL()) && (last1.getC() > lastBot2.getL()) && (last1.getC() > lastBot3.getL()) ;
//
//
//
//			Set<String> result = new HashSet<String>();
//			if(bUpBCheck1 && bUpBCheck2 && bUpBCheck3 && bNoUpBreakYesterday)
//				result.add("UP破小3頂(D0)");
//			if(bUpBCheck1 && bUpBCheck3 && bUpBreakOne && bNoUpBreakYesterday)
//				result.add("UP破小1頂(D0)");
//			if(bDownBreakCheck1 && bDownBreakCheckt2 && bNoDownBreakYesterday)
//				result.add("DOWN破小3底(D0)");
//			if(bDownBreakCheck1 && bDownBreakOne && bNoDownBreakYesterday)
//				result.add("DOWN破小1底(D0)");
//
//			return result.isEmpty()?"":result.toString();
//		}
		
		public String getLargeVolumeWithinTheMonth(List<StockBean> srcstockList, int days) {
			if(srcstockList.size() < days)
				return Const.SPACE;
			List<StockBean> stockList = srcstockList.subList(srcstockList.size() - days, srcstockList.size());
			StockBean last = stockList.get(stockList.size()-1);
			StockBean prev = stockList.get(stockList.size()-2);
//			StockBean first = stockList.get(0);
			
			StockBean maxVolStock = stockList.stream()
				      .max(Comparator.comparing(StockBean::getVolume))
				      .orElseThrow(NoSuchElementException::new);
			double avgVol = stockList.stream().mapToDouble(StockBean::getVolume).average().orElse(0.0);
			
			String rtnDate = Const.SPACE;
			if((maxVolStock.getVolume()/avgVol) >2.0) {
				rtnDate = maxVolStock.getTxnDate();
				
				if(last.getBodyTop()>maxVolStock.getBodyTop() && prev.getBodyTop()>maxVolStock.getBodyTop() && last.getL()>maxVolStock.getL() && prev.getL()>maxVolStock.getL()) 
				{
					double pct = (last.getC() - maxVolStock.getBodyTop()) / maxVolStock.getBodyTop();
					rtnDate += " UP ("+ GeneralHelper.toPct(pct)+")";
				}else if(last.getBodyBottom()< maxVolStock.getBodyBottom() && prev.getBodyBottom()>maxVolStock.getBodyBottom() && last.getH()<maxVolStock.getH() && prev.getH()<maxVolStock.getH() )
				{
					double pct = (last.getC() - maxVolStock.getBodyBottom()) / maxVolStock.getBodyBottom();
					rtnDate += " DOWN ("+ GeneralHelper.toPct(pct)+")";					
				}
			}
			return rtnDate;
//			if(maxVolStock.getDayVolumeChgPct() >2)
		}
		
		public String getBollingerUpBreakForATimeInRecentDays(List<StockBean> srcstockList, int days) {
			if(srcstockList.size() < days) {
				return Const.SPACE;
			}
			
			
			List<StockBean> stockList = srcstockList.subList(srcstockList.size() - days, srcstockList.size());
			
			String upBreakDateForATime = bollingerBandApi.isUpBreakBBATime(stockList);
			return upBreakDateForATime;
		}
		
	public String getMALongSideSupportSingalInRecentDays(List<StockBean> srcstockList, int days) {
				
	//			int numberOfDaysInPastMonth = 30;
				if(srcstockList.size() < days) {
					return Const.SPACE;
				}
				
				
				List<StockBean> stockList = srcstockList.subList(srcstockList.size() - days, srcstockList.size());
				StockBean last1 = stockList.get(stockList.size()-1);
				StockBean last2 = stockList.get(stockList.size()-2);
//				StockBean last3 = stockList.get(stockList.size()-3);
//				StockBean last4 = stockList.get(stockList.size()-4);
				
				if(last1.getPriceSma()==null || last2.getPriceSma()==null)
					return Const.SPACE;
				

				boolean bLongArrange = last1.getPriceSma().getMa10() >= last1.getPriceSma().getMa50() || last1.getPriceSma().getMa50() >= last1.getPriceSma().getMa200();
//				boolean bisDowning1 = (last4.getH() > last3.getH() || last4.getBodyTop() > last3.getBodyTop()) && (last3.getH() > last2.getH() || last3.getBodyTop() > last2.getBodyTop());
//				boolean bisDowning2 = (last4.getL() > last3.getL() || last4.getBodyBottom() > last3.getBodyBottom()) && (last3.getL() > last2.getL() || last3.getBodyBottom() > last2.getBodyBottom());
						

				boolean isValidMA10Support = last1.getC()>=last1.getPriceSma().getMa10() && (last1.getL()<last1.getPriceSma().getMa10() || last2.getL()<last2.getPriceSma().getMa10());
				boolean isValidMA20Support = last1.getC()>=last1.getPriceSma().getMa20() && (last1.getL()<last1.getPriceSma().getMa20() || last2.getL()<last2.getPriceSma().getMa20());
				boolean isValidMA50Support = last1.getC()>=last1.getPriceSma().getMa50() && (last1.getL()<last1.getPriceSma().getMa50() || last2.getL()<last2.getPriceSma().getMa50());

//				boolean isVolDecding = ((last4.getVolume() - last2.getVolume())/last2.getVolume() > 0.1 )
//									&& ((last4.getVolume() - last2.getVolume())/last2.getVolume() >0.1 ) 
//									&& last2.getDayVolumeChgPct()<0.9;

				boolean bResultUpTrend = last1.isRiseToday() && bLongArrange ;//&& ( bisDowning1 || bisDowning2 );
				
								
				boolean isValidMA10Rs = last1.getC()<=last1.getPriceSma().getMa10() && (last1.getH()>last1.getPriceSma().getMa10() || last2.getH()>last2.getPriceSma().getMa10());
				boolean isValidMA20Rs = last1.getC()<=last1.getPriceSma().getMa20() && (last1.getH()>last1.getPriceSma().getMa20() || last2.getH()>last2.getPriceSma().getMa20());
				boolean isValidMA50Rs = last1.getC()<=last1.getPriceSma().getMa50() && (last1.getH()>last1.getPriceSma().getMa50() || last2.getH()>last2.getPriceSma().getMa50());				
				boolean bShortArrange = last1.isRiseToday()==false &&  last1.getPriceSma().getMa10() <= last1.getPriceSma().getMa50() || last1.getPriceSma().getMa50() <= last1.getPriceSma().getMa200();
				
				Set<String> signalSet = new HashSet<String>();
				if(bResultUpTrend && isValidMA10Support)
					signalSet.add("10D支持");
				if(bResultUpTrend && isValidMA20Support)
					signalSet.add("20D支持");
				if(bResultUpTrend && isValidMA50Support)
					signalSet.add("50D支持");
				
				if(bShortArrange && isValidMA10Rs)
					signalSet.add("10D阻力");
				if(bShortArrange && isValidMA20Rs)
					signalSet.add("20D阻力");
				if(bShortArrange && isValidMA50Rs)
					signalSet.add("50D阻力");
				
				return  signalSet.isEmpty()?Const.SPACE:signalSet.toString().replace("[", "").replace("]", "");
				
	}

	public String getLastEngulfingInRecentDays(List<StockBean> srcstockList, List<WavePoint> sortedTopList, List<WavePoint> sortedBotList, int days) {
		if(srcstockList.size() < days) {
			return "";
		}

		List<WavePoint> topbotList = new ArrayList<WavePoint>();
		topbotList.addAll(sortedTopList);
		topbotList.addAll(sortedBotList);

		List<WavePoint> sortedTopBotList = topbotList.stream().sorted(Comparator.comparing(e -> e.getDateInt())).collect(Collectors.toList());
		if(sortedTopBotList.isEmpty())
			return "";

		WavePoint lastWP = sortedTopBotList.get(sortedTopBotList.size()-1);


		Set<String> hashSet = new HashSet<String>();
		List<StockBean> stockList = srcstockList.subList(srcstockList.size() - days, srcstockList.size());
		for( int i=1; i<stockList.size()-1; i++) {
			StockBean curr = stockList.get(i);
			StockBean prev = stockList.get(i - 1);
			if (lastWP.getStockBean().getTxnDateInt() > curr.getTxnDateInt()
					|| curr.getDayVolumeChgPct()<0.95 || curr.getL()< lastWP.getH()
			)
			{
				continue;
			}

			double topPrev = KHelper.getBodyTopValue(prev);
			double bottomPrev = KHelper.getBodyBottomValue(prev);

			double topCurr = KHelper.getBodyTopValue(curr);
			double bottomCurr = KHelper.getBodyBottomValue(curr);


			if (lastWP.getType() == WaveType.TOP && KHelper.isBearishCandle(curr) && topPrev < topCurr && bottomPrev > bottomCurr) {
				boolean isMatch = false;
				if(sortedTopList.size() >= 3) {
					WavePoint last2 = sortedTopList.get(sortedTopList.size() - 2);
					WavePoint last3 = sortedTopList.get(sortedTopList.size() - 3);

					if(last2.getH() > lastWP.getH() || last3.getH() > lastWP.getH()) {
						isMatch = true;
					}
				}else{
					isMatch = true;
				}

				if(isMatch)
					hashSet.add(CandlestickTradingPatternType.LAST_BEARISH_ENGULFING + "_" + curr.getTxnDate());

			}

			if (lastWP.getType() == WaveType.BOT && KHelper.isBullishCandle(curr) && topPrev < topCurr && bottomPrev > bottomCurr) {
				boolean isMatch = false;
				if(sortedBotList.size() >= 3) {
					WavePoint last2 = sortedBotList.get(sortedBotList.size() - 2);
					WavePoint last3 = sortedBotList.get(sortedBotList.size() - 3);

					if (last2.getL() < lastWP.getL() || last3.getL() < lastWP.getL()) {
						isMatch = true;
					}
				}else{
					isMatch = true;
				}

				if(isMatch)
					hashSet.add(CandlestickTradingPatternType.LAST_BULLISH_ENGULFING + "_" + curr.getTxnDate());
			}

		}

		return hashSet.toString();


	}
	
}
