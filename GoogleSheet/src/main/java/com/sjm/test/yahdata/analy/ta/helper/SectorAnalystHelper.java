package com.sjm.test.yahdata.analy.ta.helper;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Comparator;
import com.sjm.test.yahdata.analy.bean.SectorStatsSummary;
import com.sjm.test.yahdata.analy.cfg.CNStockListConfig;
import com.sjm.test.yahdata.analy.cfg.HKStockListConfig;
import com.sjm.test.yahdata.analy.cfg.USStockListConfig;
import com.sjm.test.yahdata.analy.recenthigh.RecentHighBean;
import com.sjm.test.yahdata.benchmarks.BenchmarkSummary;

public class SectorAnalystHelper {
	public final static String CODE_ETF_HK_TECH = "3033.HK";//"科技指數";
	public final static String CODE_ETF_HSCEI_ELEMENT = "2828.HK";//"國企指數";
	public final static String CODE_INDEX_HSI = "^HSI";
	
	public final static String LABEL_HK_DUAL_COUNTER_CNH = "雙櫃台";	
	public final static String LABEL_HK_RED_CHIP = "紅籌股";
	public final static String CODE_US_CHINA_CONCEPT = "PGJ";//""中概股";
	
	public final static String LABEL_CN_CSI_300 = "滬深三百";
	public final static String LABEL_CN_SZSE_ChiNext = "中國創業板指數";
	
	public final static String CODE_ETF_SPY = "SPY";
	public final static String CODE_ETF_QQQ = "QQQ";
	public final static String CODE_ETF_DIA = "DIA";
	
	public SectorAnalystHelper() {
	}

	
	public static List<SectorStatsSummary> getSectorStatsSummary(List<BenchmarkSummary> benchSumyList) {
		Map<String, Long> sectorCountMap =
				benchSumyList.stream().collect(Collectors.groupingBy(BenchmarkSummary::getSector, Collectors.counting()));
		
		Map<String, Double> sectorYTDMap = 
				benchSumyList.stream().collect(Collectors.groupingBy(BenchmarkSummary::getSector, Collectors.averagingDouble(BenchmarkSummary::getPercentageE2EC2C)));
		
		
		Map<String, BenchmarkSummary> sectorMaxValueMap = benchSumyList.stream()
		        .collect(Collectors.toMap(BenchmarkSummary::getSector, Function.identity(),
		            BinaryOperator.maxBy(Comparator.comparing(BenchmarkSummary::getPercentageE2EC2C))));
		
		Map<String, BenchmarkSummary> sectorMinValueMap = benchSumyList.stream()
		        .collect(Collectors.toMap(BenchmarkSummary::getSector, Function.identity(),
		            BinaryOperator.minBy(Comparator.comparing(BenchmarkSummary::getPercentageE2EC2C))));
		
		
		Map<String, Double> sectorUpDownRatioMap = 
				benchSumyList.stream()
							.collect(
								Collectors.groupingBy(BenchmarkSummary::getSector,
//										Collectors.collectingAndThen(
												Collectors.mapping(BenchmarkSummary::getPercentageE2EC2C, Collectors.averagingDouble(ytd ->{
													return ytd>0.0?1:0;
									}))
//							, avg -> String.format("%,.0f%%", avg * 100))
								));
						
		
		List<SectorStatsSummary> sumyList = new ArrayList<SectorStatsSummary>();
		
		for (Map.Entry<String, Long> entry : sectorCountMap.entrySet()) {
			Double avgPerformance = sectorYTDMap.get(entry.getKey());
			Double upDownRatio = sectorUpDownRatioMap.get(entry.getKey());
			BenchmarkSummary maxObj = sectorMaxValueMap.get(entry.getKey());
			BenchmarkSummary minObj = sectorMinValueMap.get(entry.getKey());
			
			SectorStatsSummary sectorStats = new SectorStatsSummary();
			sectorStats.setAvg(avgPerformance);
			sectorStats.setSector(entry.getKey());
			sectorStats.setStockCnt(entry.getValue());
			sectorStats.setUpDownRatio(upDownRatio);
			sectorStats.setRemark("");
			sectorStats.setMaxStockCode(maxObj.getStockCode());
			sectorStats.setMinStockCode(minObj.getStockCode());
			sectorStats.setMaxStockPencentage(maxObj.getPercentageE2EC2C());
			sectorStats.setMinStockPencentage(minObj.getPercentageE2EC2C());
			sumyList.add(sectorStats);
			
		}
		
		return sumyList;
		
		
	}
	
	public static List<String> getETFBelongsTo(String stockCode) {
		List<String> sb = new ArrayList<String> ();
		if(USStockListConfig.QQQ_COMPONENTS.contains(stockCode)) {
			sb.add(SectorAnalystHelper.CODE_ETF_QQQ);
		}
		
		if(USStockListConfig.SPX_COMPONENTS.contains(stockCode)) {
			sb.add(SectorAnalystHelper.CODE_ETF_SPY);
		}
		if(USStockListConfig.DOW_COMPONENTS.contains(stockCode)) {
			sb.add(SectorAnalystHelper.CODE_ETF_DIA);
		}
		
		if(USStockListConfig.CHINA_CONCEPT.contains(stockCode)) {
			sb.add(SectorAnalystHelper.CODE_US_CHINA_CONCEPT);
		}
		if(USStockListConfig.IWM_TOP_20_HOLDING.contains(stockCode)) {
			sb.add("羅素2千 ");
		}
		
		if(HKStockListConfig.HSI_ELEMENT.contains(stockCode)) {
			sb.add(SectorAnalystHelper.CODE_INDEX_HSI);
		}
		if(HKStockListConfig.HSCEI_ELEMENT.contains(stockCode)) {
			sb.add(CODE_ETF_HSCEI_ELEMENT);
		}
		if(HKStockListConfig.HSTECH.contains(stockCode)) {
			sb.add(SectorAnalystHelper.CODE_ETF_HK_TECH);
		}
		if(HKStockListConfig.HK_BIOTECH.contains(stockCode)) {
			sb.add("HK_BIOTECH");
		}
		if(HKStockListConfig.H_SHARES.contains(stockCode)) {
			sb.add("H股");
		}
		if(HKStockListConfig.RED_CHIPS.contains(stockCode)) {
			sb.add(SectorAnalystHelper.LABEL_HK_RED_CHIP);
		}
		if(HKStockListConfig.DUAL_COUNTER.contains(stockCode)) {
			sb.add(SectorAnalystHelper.LABEL_HK_DUAL_COUNTER_CNH);
		}
		
		
		if(CNStockListConfig.CSI_300_ETF.contains(stockCode)) {
			sb.add(LABEL_CN_CSI_300);
		}
		
		if(CNStockListConfig.SZSE_ChiNext_ETF.contains(stockCode)) {
			sb.add(LABEL_CN_SZSE_ChiNext);
		}
		
		
		return sb;
	}
	
	
	public static List<String> getSouthboundBelongsTo(String stockCode) {
		List<String> sb = new ArrayList<String> ();
		
		
		
		if(HKStockListConfig.SHANGHAI_LIST.contains(stockCode)) {
			sb.add("上海");
		}
		if(HKStockListConfig.SHENZHEN_LIST.contains(stockCode)) {
			sb.add("深圳");
		}
		if(HKStockListConfig.DUAL_COUNTER.contains(stockCode)) {
			sb.add(LABEL_HK_DUAL_COUNTER_CNH);
		}
		
		if(HKStockListConfig.DUAL_COUNTER_CNH.contains(stockCode)) {
			sb.add("雙櫃台(8)");
		}
		
		
		return sb;
	}
	
	
	public static List<SectorStatsSummary> getSectorStatsSummaryByRecentHighBean(List<RecentHighBean> benchSumyList) {
		Map<String, Long> sectorCountMap =
				benchSumyList.stream().collect(Collectors.groupingBy(RecentHighBean::getSector, Collectors.counting()));
		
		Map<String, Double> sectorYTDMap = 
				benchSumyList.stream().collect(Collectors.groupingBy(RecentHighBean::getSector, Collectors.averagingDouble(RecentHighBean::getYtd)));
		
		
//		Map<String, String> sectorUpDownRatioMap = 
//				benchSumyList.stream()
//							.collect(
//								Collectors.groupingBy(RecentHighBean::getSector,
//										Collectors.collectingAndThen(
//												Collectors.mapping(RecentHighBean::getYtd, Collectors.averagingDouble(ytd ->{
//													return ytd>0.0?1:0;
//									}))
//							, avg -> String.format("%,.0f%%", avg * 100))));
		
		Map<String, Double> sectorUpDownRatioMap = 
				benchSumyList.stream()
							.collect(
								Collectors.groupingBy(RecentHighBean::getSector,
//										Collectors.collectingAndThen(
												Collectors.mapping(RecentHighBean::getYtd, Collectors.averagingDouble(ytd ->{
													return ytd>0.0?1:0;
									}))
//							, avg -> String.format("%,.0f%%", avg * 100))
								));
		
		
		List<SectorStatsSummary> sumyList = new ArrayList<SectorStatsSummary>();
		for (Map.Entry<String, Long> entry : sectorCountMap.entrySet()) {
			Double avgPerformance = sectorYTDMap.get(entry.getKey());
			Double upDownRatio = sectorUpDownRatioMap.get(entry.getKey());
			
			SectorStatsSummary sectorStats = new SectorStatsSummary();
			sectorStats.setAvg(avgPerformance);
			sectorStats.setSector(entry.getKey());
			sectorStats.setStockCnt(entry.getValue());
			sectorStats.setUpDownRatio(upDownRatio);
			sectorStats.setRemark("");
			sumyList.add(sectorStats);
			
		}
		
		return sumyList;
		
		
	}
}
