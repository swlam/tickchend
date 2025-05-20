package com.sjm.test.yahdata.analy.report;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.helper.CFGHelper;
import com.sjm.test.yahdata.analy.model.InstantPerformanceResult;
import com.sjm.test.yahdata.analy.model.StatisticsResult;
import com.sjm.test.yahdata.analy.ta.PatternTrendHelper;
import com.sjm.test.yahdata.analy.ta.helper.SectorAnalystHelper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class DailySummaryReportDeepSeek {
	private static final String MKT_US = "USD";
	private static final String MKT_HK = "HKD";

	public DailySummaryReportDeepSeek() {
	}
	
	public static void printSimpleStatisticsResult(List<InstantPerformanceResult> performanceResultList, String market, String ICONIC_CODE) {
		
		Optional<InstantPerformanceResult> optional = performanceResultList.stream().filter( x->x.getCurrentStockBean().getStockCode().equalsIgnoreCase(ICONIC_CODE)).findFirst();
		//ICONIC_CODE
		if(optional.isPresent()) {
			int iconicTxnDateInt = optional.get().getCurrentStockBean().getTxnDateInt();
			
			performanceResultList.stream().filter( x->x.getCurrentStockBean().getTxnDateInt() >= iconicTxnDateInt).toList();
			analyseBySectorAndIndustry(performanceResultList);
		}
		
		/*
		if(Const.MARKET_HK.equalsIgnoreCase(market))
			printSimpleHKStatisticsResult(performanceResultList);
		if(Const.MARKET_US.equalsIgnoreCase(market))
			printSimpleUSStatisticsResult(performanceResultList);
		
		*/
	}
	
//	protected static void printSimpleHKStatisticsResult(List<InstantPerformanceResult> performanceResultList) {
//		printSimpleStatisticsResult(performanceResultList, SectorAnalystHelper.CODE_INDEX_HSI, SectorAnalystHelper.CODE_ETF_HK_TECH, SectorAnalystHelper.CODE_ETF_HSCEI_ELEMENT, SectorAnalystHelper.LABEL_HK_DUAL_COUNTER_CNH);
//	}
//
//	protected static void printSimpleUSStatisticsResult(List<InstantPerformanceResult> performanceResultList) {
//		printSimpleStatisticsResult(performanceResultList, SectorAnalystHelper.CODE_ETF_DIA, SectorAnalystHelper.CODE_ETF_SPY, SectorAnalystHelper.CODE_ETF_QQQ, SectorAnalystHelper.CODE_US_CHINA_CONCEPT);
//	}
//
//	protected static void printSimpleStatisticsResult(List<InstantPerformanceResult> performanceResultList, String filterName1, String filterName2, String filterName3, String filterName4) {
//		StringBuilder sb =  new StringBuilder();
//		sb.append("\n\n" +toFunc(performanceResultList, filterName1));
//		sb.append("\n\n" +toFunc(performanceResultList, filterName2));
//		sb.append("\n\n" +toFunc(performanceResultList, filterName3));
//		sb.append("\n\n" +toFunc(performanceResultList, filterName4));
//		System.out.println(sb.toString());
//
//		System.out.println();
//	}
//
//	public static String toFunc(List<InstantPerformanceResult> perfromanceResultList, String filterName1) {
//
//		List<InstantPerformanceResult> targetResultList = perfromanceResultList.stream()
//				.filter(x-> x.getEstTradeAmount() >1.0 && x.getSector() !=null	&& !(x.getSector().contains("ETF") || x.getSector().contains("index") || x.getSector().contains("currency") || x.getSector().contains("指數") || x.getSector().contains("貨幣") || x.getSector().contains("Crypto") || x.getSector().contains("債券"))
//				).collect(Collectors.toList());
//			double highVolRatio = 1.2;
//
//		Optional<InstantPerformanceResult> optional = perfromanceResultList.stream()
//				.filter(x -> x.getCurrentStockBean().getStockCode().contains(filterName1))
//				.findFirst();
//		String resultMsg = "";
//
//
//		if(optional != null && optional.isPresent())
//		{
//			StringBuilder sb = new StringBuilder();
//			//posiReturn with Portion ---- START
//			long cnt = targetResultList.stream().filter(x -> x.getBelongETF().contains(filterName1)).count();
//			long upCnt = targetResultList.stream().filter( x-> x.getCurrentStockBean().getDayChgPct()>0.01 && x.getBelongETF().contains(filterName1)).count();
//			long upCntWithMoreVol = targetResultList.stream().filter( x-> x.getCurrentStockBean().getDayChgPct()>0.01 && x.getCurrentStockBean().getDayVolumeChgPct()>highVolRatio && x.getBelongETF().contains(filterName1)).count();
//
//			double ratioUp = (double)upCnt / (double)cnt;
//			double ratioUpVol = (double)upCntWithMoreVol / (double)upCnt;
//
//			sb.append("Name\t上升比例\t上升總數\tNo.of Stock\t上升數目中的More vol比例\n");
//			sb.append(filterName1+ "\t"+GeneralHelper.toPct(ratioUp)+"\t"+upCnt+"\t"+cnt+"\t"+GeneralHelper.toPct(ratioUpVol));
//			sb.append("\n\n");
//
//
//			InstantPerformanceResult nameResult = optional.get();
//			List<InstantPerformanceResult> goodPerformanceList = perfromanceResultList.stream()
//					.filter(x -> x.getBelongETF().contains(filterName1)
//							&& x.getThreeDaysChangeO2HPct() > nameResult.getThreeDaysChangeO2HPct()
//							&& x.getWeeksChangeO2PHPct() > nameResult.getWeeksChangeO2PHPct()
//							&& x.getCurrentStockBean().getDayVolumeChgPct() > nameResult.getCurrentStockBean().getDayVolumeChgPct()
//							)
//					.sorted(Comparator.comparingDouble(InstantPerformanceResult::getThreeDaysChangeO2HPct).reversed())
//					.toList();
//
//			List<InstantPerformanceResult> badPerformanceList = perfromanceResultList.stream()
//					.filter(x -> x.getBelongETF().contains(filterName1)
//							&& x.getThreeDaysChangeO2LPct() < nameResult.getThreeDaysChangeO2LPct()
//							&& x.getWeeksChangeO2PLPct() < nameResult.getWeeksChangeO2PLPct())
//					.sorted(Comparator.comparingDouble(InstantPerformanceResult::getThreeDaysChangeO2HPct))
//					.toList();
//
//			double basicTradeAmount = 80.0;
//
//			List<String> goodCodeList = goodPerformanceList.stream().filter(stock -> stock.getCurrentStockBean().getEstTradeAmount()>basicTradeAmount)
////	                .map(StockBean::getStockCode)
//			        .map(stock -> stock.getCurrentStockBean().getStockCode() + " " + GeneralHelper.toPct(stock.getThreeDaysChangeO2HPct()))
//	                .toList();
//			List<String> badCodeList = badPerformanceList.stream().filter(stock -> stock.getCurrentStockBean().getEstTradeAmount()>basicTradeAmount)
////	                .map(StockBean::getStockCode)
//			        .map(stock -> stock.getCurrentStockBean().getStockCode() + " " + GeneralHelper.toPct(stock.getThreeDaysChangeO2LPct()))
//	                .toList();
//
//			resultMsg = nameResult.getCurrentStockBean().getStockCode()  + ", Today Chg%: " + GeneralHelper.toPct(nameResult.getCurrentStockBean().getDayChgPct())
//					+ ", 3-Days O2PH: "+GeneralHelper.toPct(nameResult.getThreeDaysChangeO2HPct()) + ", 1-Week O2PH: "+GeneralHelper.toPct(nameResult.getWeeksChangeO2PHPct())
//					+"\n\t 3D pct O2PH: "+ goodCodeList;
//
////			resultMsg2 = "\t 3-Days O2PL: "+GeneralHelper.to100Pct(nameResult.getThreeDaysChangeO2LPct()) + ", 1-Week O2PL: "+GeneralHelper.to100Pct(nameResult.getWeeksChangeO2PLPct())
////					+"\n\t 3D pct O2PL : "+ badCodeList;
//
//			String title ="Name\tDay Chg%\tDay2H Chg%\t3D Chg(O2PH)\t5D Chg(O2PH)\tO2PH-3天Pct順次序(數目:"+goodCodeList.size()+")";
//			StringBuffer data = new StringBuffer();
//			data.append(title+"\n");
//			data.append(nameResult.getCurrentStockBean().getStockCode()+"\t");
//			data.append(GeneralHelper.toPct(nameResult.getCurrentStockBean().getDayChgPct())+"\t");
//			data.append(GeneralHelper.toPct(nameResult.getCurrentStockBean().getDay2HChgPct())+"\t");
//			data.append(GeneralHelper.toPct(nameResult.getThreeDaysChangeO2HPct())+"\t");
//			data.append(GeneralHelper.toPct(nameResult.getWeeksChangeO2PHPct())+"\t");
//			data.append(goodCodeList);
//			data.append("\n\n");
//
//			title ="Name\tDay Chg%\tDay2L Chg%\t3D Chg(O2PL)\t5D Chg(O2PL)\tO2PL-3天Pct倒次序(數目:"+ badCodeList.size()+")";
////			StringBuffer data = new StringBuffer();
//			data.append(title+"\n");
//			data.append(nameResult.getCurrentStockBean().getStockCode()+"\t");
//			data.append(GeneralHelper.toPct(nameResult.getCurrentStockBean().getDayChgPct())+"\t");
//			data.append(GeneralHelper.toPct(nameResult.getCurrentStockBean().getDay2LChgPct())+"\t");
//			data.append(GeneralHelper.toPct(nameResult.getThreeDaysChangeO2LPct())+"\t");
//			data.append(GeneralHelper.toPct(nameResult.getWeeksChangeO2PLPct())+"\t");
//			data.append(badCodeList);
//			data.append("\n");
//			resultMsg = sb.toString() + data.toString();
//
//		}
//		return resultMsg;
////		return resultMsg + "\n"+resultMsg2;
//	}
//
//	public static void exportStat(List<StatisticsResult> resultList) {
//		StringBuilder msgMain = new StringBuilder();
//		msgMain.append("Date\tNoOfStock\t轉強 Cnt\t轉弱 Cnt\t轉強比例\t轉弱比例\t上升比例\t下跌比例\tRSI(9)大於50比例\t大於20天線比例\t大於50天線比例\t大於100天線比例\t大於200天線比例\t2天>19天線比例\t50天>200天線比例\t轉強的(TOP 10 交易量) \t轉弱的(TOP 10 交易量)\t ");
////		msgMain.append(ICONIC_CODE+" close\tPct.\tRSI(9)\tRSI(14)\t "+BASE_STOCK_B+" close\tPct.\tRSI(9)\tRSI(14)\t "+BASE_STOCK_C+" close\tPct.\tRSI(9)\tRSI(14)\t "+BASE_STOCK_D+" close\tPct.\tRSI(9)\tRSI(14)");//
//		msgMain.append("\t價升量升比例(HSI)\t價升量升比例(HS Tech)\t價升量升比例(雙櫃台)\t價升量升比例(RedChips)\t上升比例(SPY)\t上升比例(QQQ)\t上升比例(DIA)\t上升比例(China Concept)");
//		msgMain.append("\n");
//
//		for (StatisticsResult elemt : resultList)
//		{
//			try {
//				StringBuilder msg = new StringBuilder();
//				msg.append(elemt.getTxnDate());
//				msg.append("\t"+elemt.getNumOfStock());
//				msg.append("\t"+elemt.getStrongCnt());
//				msg.append("\t"+elemt.getWeakCnt());
//	//			msg.append("\t"+elemt.getNumOfStock());
//				msg.append("\t"+GeneralHelper.toPct((double)elemt.getStrongCnt() / (double)(elemt.getNumOfStock())));
//				msg.append("\t"+GeneralHelper.toPct((double)elemt.getWeakCnt() / (double)(elemt.getNumOfStock())));
//				msg.append("\t"+GeneralHelper.toPct( elemt.getRatioDailyPositiveReturn()));
//				msg.append("\t"+GeneralHelper.toPct( elemt.getRatioDailyNegativeReturn()));
//
//				msg.append("\t"+GeneralHelper.toPct((double)elemt.getRsi9Abv50Cnt()/ (double)(elemt.getNumOfStock())));
//				msg.append("\t"+GeneralHelper.toPct((double)elemt.getAbv20DCnt() / (double)(elemt.getNumOfStock())));
//				msg.append("\t"+GeneralHelper.toPct((double)elemt.getAbv50DCnt() / (double)(elemt.getNumOfStock())));
//				msg.append("\t"+GeneralHelper.toPct((double)elemt.getAbv100DCnt() / (double)(elemt.getNumOfStock())));
//				msg.append("\t"+GeneralHelper.toPct((double)elemt.getAbv200DCnt() / (double)(elemt.getNumOfStock())));
//				msg.append("\t"+GeneralHelper.toPct((double)elemt.getMa2AbvMA19Cnt()/ (double)(elemt.getNumOfStock())));
//				msg.append("\t"+GeneralHelper.toPct((double)elemt.getMa50AbvMA200Cnt()/ (double)(elemt.getNumOfStock())));
//
//				msg.append("\t"+elemt.getLeadingStock());
//				msg.append("\t"+elemt.getTailStock());
//				msg.append("\t"+elemt.getIconicAClose());
//				msg.append("\t"+GeneralHelper.toPct(elemt.getIconicAPct()));
//				msg.append("\t"+ GeneralHelper.to2DecimalPlaces(elemt.getIconicARsi9()));
//				msg.append("\t"+ GeneralHelper.to2DecimalPlaces(elemt.getIconicARsi14()));
//
//				msg.append("\t"+elemt.getIconicBClose());
//				msg.append("\t"+GeneralHelper.toPct(elemt.getIconicBPct()));
//				msg.append("\t"+ ((elemt.getIconicBRsi9()!=null)?GeneralHelper.toPct(elemt.getIconicBRsi9()):Const.NA));
//				msg.append("\t"+ ((elemt.getIconicBRsi14()!=null)?GeneralHelper.toPct(elemt.getIconicBRsi14()):Const.NA));
//
//				msg.append("\t"+elemt.getIconicCClose());
//				msg.append("\t"+GeneralHelper.toPct(elemt.getIconicCPct()));
//				msg.append("\t"+ ((elemt.getIconicCRsi9()!=null)?GeneralHelper.toPct(elemt.getIconicCRsi9()):Const.NA));
//				msg.append("\t"+ ((elemt.getIconicCRsi14()!=null)?GeneralHelper.toPct(elemt.getIconicCRsi14()):Const.NA));
//
//				msg.append("\t"+ elemt.getIconicDClose());
//				msg.append("\t"+ GeneralHelper.toPct(elemt.getIconicDPct()));
//				msg.append("\t"+ ((elemt.getIconicDRsi9()!=null)?GeneralHelper.toPct(elemt.getIconicDRsi9()):Const.NA));
//				msg.append("\t"+ ((elemt.getIconicDRsi14()!=null)?GeneralHelper.toPct(elemt.getIconicDRsi14()):Const.NA));
//
//				msg.append("\t"+GeneralHelper.toPct( elemt.getRatioUpHSI()));
//				msg.append("\t"+GeneralHelper.toPct( elemt.getRatioUpHSTech()));
//				msg.append("\t"+GeneralHelper.toPct( elemt.getRatioUpDualCounter()));
//				msg.append("\t"+GeneralHelper.toPct( elemt.getRatioUpRedChip()));
//				msg.append("\t"+GeneralHelper.toPct( elemt.getRatioUpSPY()));
//				msg.append("\t"+GeneralHelper.toPct( elemt.getRatioUpQQQ()));
//				msg.append("\t"+GeneralHelper.toPct( elemt.getRatioUpDIA()));
//				msg.append("\t"+GeneralHelper.toPct( elemt.getRatioUpChinaConcept()));
//				msg.append("\n");
//				msgMain.append(msg);
//			}catch(Exception e) {
//				log.error(""+elemt.getTxnDate() , e);
//			}
//		}
//
//		log.info("\n"+msgMain);
//	}
	
	
	public static void analyseBySectorAndIndustry(List<InstantPerformanceResult> perfromanceResultList) {
//		 .map(doc -> doc.getDocumentNo().toLowerCase())
		List<String> sectorList = CFGHelper.getStockProfileMap().values().stream().map(x->x.getSector().toLowerCase()).distinct().toList();
		List<String> industryList = CFGHelper.getStockProfileMap().values().stream().map(x->x.getIndustry().toLowerCase()).distinct().toList();



//		Map<String, List<InstantPerformanceResult>> industryResultMap = new HashMap<String, List<InstantPerformanceResult>>(); 
//		for(String industry : industryList) {
//			List<InstantPerformanceResult> tmpList = perfromanceResultList.stream().filter(x-> x.getIndustry().equalsIgnoreCase(industry)).toList();
//			industryResultMap.put(industry, tmpList);
//		}

		doAnaly(MKT_US, "allSymbols", perfromanceResultList, SectorAnalystHelper.CODE_ETF_SPY);
		doAnaly(MKT_US, "etf", perfromanceResultList, SectorAnalystHelper.CODE_ETF_SPY);
		doAnaly(MKT_US,"etf", perfromanceResultList, SectorAnalystHelper.CODE_ETF_QQQ);
		doAnaly(MKT_US,"etf", perfromanceResultList, SectorAnalystHelper.CODE_US_CHINA_CONCEPT);

		doAnaly(MKT_HK, "allSymbols", perfromanceResultList, SectorAnalystHelper.CODE_INDEX_HSI);

		doAnaly(MKT_HK,"etf", perfromanceResultList, SectorAnalystHelper.CODE_INDEX_HSI);
		doAnaly(MKT_HK,"etf", perfromanceResultList, SectorAnalystHelper.CODE_ETF_HK_TECH);
		doAnaly(MKT_HK,"etf", perfromanceResultList, SectorAnalystHelper.CODE_ETF_HSCEI_ELEMENT);
		
		doAnaly("CN","etf", perfromanceResultList, SectorAnalystHelper.LABEL_CN_CSI_300);
		doAnaly("CN","etf", perfromanceResultList, SectorAnalystHelper.LABEL_CN_SZSE_ChiNext);
	}
	
	private static List<InstantPerformanceResult> extract(String type, String matchingText, String transactionDate, List<InstantPerformanceResult> perfromanceResultList){
		List<InstantPerformanceResult> tmpList = null;
		
		
		if("Sector".equalsIgnoreCase(type)) {
			tmpList = perfromanceResultList.stream()
					.filter(x-> x.getSector().equalsIgnoreCase(matchingText) && x.getCurrentStockBean().getTxnDate().equalsIgnoreCase(transactionDate))
					.toList();
		}else if("industry".equalsIgnoreCase(type)) {
			tmpList = perfromanceResultList.stream()
					.filter(x-> x.getIndustry().equalsIgnoreCase(matchingText) && x.getCurrentStockBean().getTxnDate().equalsIgnoreCase(transactionDate))
					.toList();
		}else if("etf".equalsIgnoreCase(type) || "index".equalsIgnoreCase(type)) {
			tmpList = perfromanceResultList.stream().filter(x -> x.getBelongETF().contains(matchingText) && x.getCurrentStockBean().getTxnDate().equalsIgnoreCase(transactionDate))
					.toList();
//			long upCnt = targetResultList.stream().filter( x-> x.getCurrentStockBean().getDayChgPct()>0.01 && x.getBelongETF().contains(filterName1)).count();
//			long upCntWithMoreVol = targetResultList.stream().filter( x-> x.getCurrentStockBean().getDayChgPct()>0.01 && x.getCurrentStockBean().getDayVolumeChgPct()>highVolRatio && x.getBelongETF().contains(filterName1)).count();
			
		}else if("allSymbols".equalsIgnoreCase(type) ) {
		tmpList = perfromanceResultList.stream().filter(x -> !x.getSector().contains("ETF") )
				.toList();
//			long upCnt = targetResultList.stream().filter( x-> x.getCurrentStockBean().getDayChgPct()>0.01 && x.getBelongETF().contains(filterName1)).count();
//			long upCntWithMoreVol = targetResultList.stream().filter( x-> x.getCurrentStockBean().getDayChgPct()>0.01 && x.getCurrentStockBean().getDayVolumeChgPct()>highVolRatio && x.getBelongETF().contains(filterName1)).count();

	}
		
		return (tmpList==null)?perfromanceResultList:tmpList;
		
	}



	private static void doAnaly(String mkt, String type, List<InstantPerformanceResult> performanceResultList, String sectorOrSymbol) {
		double REQUIRED_TRADE_AMOUNT = Const.IS_INTRADAY ? 30.0 : 100.0;
		int LIMIT_RESULTS = 10;

		if (performanceResultList.isEmpty()) {
			System.out.println("Performance result list is empty.");
			return;
		}

		String txnDate = performanceResultList.get(0).getCurrentStockBean().getTxnDate();
		InstantPerformanceResult sectorOrSymbolBean = findSectorOrSymbolBean(performanceResultList, sectorOrSymbol);

		if (sectorOrSymbolBean == null) {
			System.out.println("Not found " + sectorOrSymbol + ", sectorOrSymbolBean = null");
			return;
		}

		List<InstantPerformanceResult> filteredList = filterAndExtractList(type, sectorOrSymbol, txnDate, performanceResultList, REQUIRED_TRADE_AMOUNT, sectorOrSymbolBean);

		if (filteredList.isEmpty()) {
			System.out.println("No data to display after filtering.");
			return;
		}

		AnalysisResults results = analyzeData(filteredList, sectorOrSymbolBean, LIMIT_RESULTS);
		displayResults(results, type, sectorOrSymbol, txnDate, sectorOrSymbolBean);
	}

	private static InstantPerformanceResult findSectorOrSymbolBean(List<InstantPerformanceResult> performanceResultList, String sectorOrSymbol) {
		return performanceResultList.stream()
				.filter(x -> x.getCurrentStockBean().getStockCode().equalsIgnoreCase(sectorOrSymbol))
				.findFirst()
				.orElse(null);
	}

	private static List<InstantPerformanceResult> filterAndExtractList(String type, String sectorOrSymbol, String txnDate,
																	   List<InstantPerformanceResult> performanceResultList,
																	   double requiredTradeAmount, InstantPerformanceResult sectorOrSymbolBean) {
		List<InstantPerformanceResult> orgtmpList = extract(type, sectorOrSymbol, txnDate, performanceResultList);

		return orgtmpList.stream()
				.filter(x -> x.getCurrentStockBean().getEstTradeAmount() > requiredTradeAmount
						&& sectorOrSymbolBean != null
						&& !"Index".equalsIgnoreCase(x.getSector()))
				.toList();
	}

	private static AnalysisResults analyzeData(List<InstantPerformanceResult> filteredList, InstantPerformanceResult sectorOrSymbolBean, int limitResults) {
		long cnt = filteredList.size();
		long strongerThanIndexCnt = filteredList.stream().filter(x -> x.getCurrentStockBean().getDayChgPct() > sectorOrSymbolBean.getDailyChangePct()).count();
		long positiveDayChgPctCnt = filteredList.stream().filter(x -> x.getCurrentStockBean().getDayChgPct() > 0.0).count();

		long up20DCnt = filteredList.stream().filter(x -> x.getCurrentStockBean().getC() > x.getCurrentStockBean().getPriceSma().getMa20()).count();
		long up50DCnt = filteredList.stream().filter(x -> x.getCurrentStockBean().getC() > x.getCurrentStockBean().getPriceSma().getMa50()).count();
		long up20DAbv50DCnt = filteredList.stream()
				.filter(x -> x.getCurrentStockBean().getC() > x.getCurrentStockBean().getPriceSma().getMa20()
						&& x.getCurrentStockBean().getPriceSma().getMa10() > x.getCurrentStockBean().getPriceSma().getMa50()
						&& x.getCurrentStockBean().getPriceSma().getMa50() > x.getCurrentStockBean().getPriceSma().getMa200())
				.count();
		long upRsi9Cnt = filteredList.stream().filter(x -> x.getCurrentStockBean().getRsi9()>=0.6).count();
		long downRsi9Cnt = filteredList.stream().filter(x -> x.getCurrentStockBean().getRsi9()<0.5).count();

		double ratioPositiveDayChgPctCnt = (double) positiveDayChgPctCnt / cnt;
		double ratioStrongerThanIndexCnt = (double) strongerThanIndexCnt / cnt;
		double ratioAbv20D = (double) up20DCnt / cnt;
		double ratioAbv50D = (double) up50DCnt / cnt;
		double ratioUp20DAbv50D = (double) up20DAbv50DCnt / cnt;

		double ratioUpRsi9 = (double) upRsi9Cnt / cnt;
		double ratioDownRsi9 = (double) downRsi9Cnt / cnt;

		List<String> up20DList = generateStockList(filteredList, limitResults, x -> x.getCurrentStockBean().getC() > x.getCurrentStockBean().getPriceSma().getMa20());
		List<String> up50DList = generateStockList(filteredList, limitResults, x -> x.getCurrentStockBean().getC() > x.getCurrentStockBean().getPriceSma().getMa50());
		List<String> up20DAbv50DList = generateStockList(filteredList, limitResults, x -> x.getCurrentStockBean().getC() > x.getCurrentStockBean().getPriceSma().getMa20()
				&& x.getCurrentStockBean().getPriceSma().getMa10() > x.getCurrentStockBean().getPriceSma().getMa50()
				&& x.getCurrentStockBean().getPriceSma().getMa50() > x.getCurrentStockBean().getPriceSma().getMa200());

		List<String> upRsi9List = generateStockList(filteredList, limitResults, x -> x.getCurrentStockBean().getRsi9()>=0.6);
		List<String> downRsi9List = generateStockList(filteredList, limitResults, x -> x.getCurrentStockBean().getRsi9()<0.5);

		List<String> top10List = generateTopList(filteredList, limitResults, x -> x.getCurrentStockBean().getDayChgPct() > sectorOrSymbolBean.getDailyChangePct());
		List<String> top10Rsi9List = generateTopList(filteredList, limitResults, x -> x.getCurrentStockBean().getRsi9() > sectorOrSymbolBean.getRsi9());
		List<String> top10DownList = generateTopList(filteredList, limitResults, x -> x.getCurrentStockBean().getDayChgPct() < sectorOrSymbolBean.getDailyChangePct());
		List<String> top10Rsi9DownList = generateTopList(filteredList, limitResults, x -> x.getCurrentStockBean().getRsi9() < sectorOrSymbolBean.getRsi9());
		List<String> top10WithGoodMAList = generateTopList(filteredList, limitResults, x -> x.getCurrentStockBean().getDayChgPct() > 0.0
				&& x.getCurrentStockBean().getC() > x.getCurrentStockBean().getPriceSma().getMa20()
				&& x.getCurrentStockBean().getPriceSma().getMa10() > x.getCurrentStockBean().getPriceSma().getMa50()
				&& x.getCurrentStockBean().getPriceSma().getMa50() > x.getCurrentStockBean().getPriceSma().getMa200());

		List<String> top10VolumnList = generateTopList(filteredList, limitResults, Comparator.comparingDouble(x -> x.getCurrentStockBean().getDayVolumeChgPct()));
		List<String> top10TodayStrongList = generateTopList(filteredList, limitResults, x -> x.getStrongWeakTypeToday().getType().contains(PatternTrendHelper.STRONG));
		List<String> top10TodayWeakList = generateTopList(filteredList, limitResults, x -> x.getStrongWeakTypeToday().getType().contains(PatternTrendHelper.WEAK));

		long cnt2Strong = filteredList.stream().filter(x -> x.getStrongWeakTypeToday().getType().contains(PatternTrendHelper.STRONG)).count();
		long cnt2Weak = filteredList.stream().filter(x -> x.getStrongWeakTypeToday().getType().contains(PatternTrendHelper.WEAK)).count();

		long cntUpBreakSign = filteredList.stream().filter(x -> x.getWaveShape() != null && x.getWaveShape().getShapeResult().contains(Const.UP)).count();
		long cntDwBreakSign = filteredList.stream().filter(x -> x.getWaveShape() != null && x.getWaveShape().getShapeResult().contains(Const.DOWN)).count();

		List<String> upBreakD0SignList = generateBreakList(filteredList, limitResults, x -> x.getWaveShape() != null && (x.getWaveShape().getShapeResult().contains(Const.UP + Const.D0) || x.getWaveShape().getShapeResult().contains(Const.UP + Const.D1)));
		List<String> upBreakReadyList = generateBreakList(filteredList, limitResults, x -> x.getWaveShape() != null && x.getWaveShape().getShapeResult().contains(Const.WAIT + Const.UP));
		List<String> downBreakReadyList = generateBreakList(filteredList, limitResults, x -> x.getWaveShape() != null && x.getWaveShape().getShapeResult().contains(Const.WAIT + Const.DOWN));
		List<String> downBreakD0SignList = generateBreakList(filteredList, limitResults, x -> x.getWaveShape() != null && (x.getWaveShape().getShapeResult().contains(Const.DOWN + Const.D0) || x.getWaveShape().getShapeResult().contains(Const.DOWN + Const.D1)));

		return new AnalysisResults(
				ratioPositiveDayChgPctCnt, ratioStrongerThanIndexCnt, ratioAbv20D, ratioAbv50D, ratioUp20DAbv50D, ratioUpRsi9, ratioDownRsi9,
				up20DList, up50DList,up20DAbv50DList, upRsi9List, downRsi9List,
				top10List, top10Rsi9List, top10DownList,top10Rsi9DownList, top10WithGoodMAList, top10VolumnList, top10TodayStrongList, top10TodayWeakList,
				cnt2Strong, cnt2Weak, cntUpBreakSign, cntDwBreakSign, upBreakD0SignList, upBreakReadyList, downBreakReadyList, downBreakD0SignList,
				strongerThanIndexCnt, cnt // Pass the new fields
		);
	}

	private static List<String> generateStockList(List<InstantPerformanceResult> list, int limit, Predicate<InstantPerformanceResult> filter) {
		return list.stream()
				.filter(filter)
				.sorted(Comparator.comparingDouble(InstantPerformanceResult::getEstTradeAmount).reversed())
				.limit(limit)
				.map(stock -> stock.getCurrentStockBean().getStockCode() + ": " + GeneralHelper.toPct(stock.getDailyChangePct())
						+ "_V:" + GeneralHelper.toPct(stock.getCurrentStockBean().getDayVolumeChgPct())
						+ "_rsi(9):" + GeneralHelper.to100(stock.getCurrentStockBean().getRsi9())
//						+ "_" + (stock.getSysPickLongCategory() + " " + stock.getSysPickStagnantCategory() + " " + stock.getSysPickShortCategory()).replaceAll("\\[|\\]", "").replaceAll(",,", ",") + " "
//						+ stock.getWaveShape().getShapeResult().replaceAll("NA", "")
				)
				.toList();
	}

	private static List<String> generateTopList(List<InstantPerformanceResult> list, int limit, Predicate<InstantPerformanceResult> filter) {
		return list.stream()
				.filter(filter)
				.sorted(Comparator.comparingDouble(InstantPerformanceResult::getEstTradeAmount).reversed())
				.limit(limit * 3)
				.sorted(Comparator.comparingDouble(InstantPerformanceResult::getDailyChangePct).reversed())
				.limit(limit)
				.map(stock -> stock.getCurrentStockBean().getStockCode() + ":" + GeneralHelper.toPct(stock.getDailyChangePct())
						+ "_V:" + GeneralHelper.toPct(stock.getCurrentStockBean().getDayVolumeChgPct())
						+ "_rsi(9):" + GeneralHelper.to100(stock.getCurrentStockBean().getRsi9())
				)
				.toList();
	}

	private static List<String> generateTopList(List<InstantPerformanceResult> list, int limit, Comparator<InstantPerformanceResult> comparator) {
		return list.stream()
				.sorted(comparator) // Sort using the provided comparator
				.limit(limit) // Limit the results
				.map(stock -> stock.getCurrentStockBean().getStockCode() + ":" + GeneralHelper.toPct(stock.getDailyChangePct())
						+ "_V:" + GeneralHelper.toPct(stock.getCurrentStockBean().getDayVolumeChgPct())
						+ "_rsi(9):" + GeneralHelper.to100(stock.getCurrentStockBean().getRsi9())
				)
				.toList();
	}

	private static List<String> generateBreakList(List<InstantPerformanceResult> list, int limit, Predicate<InstantPerformanceResult> filter) {
		return list.stream()
				.filter(filter)
				.sorted(Comparator.comparingDouble(InstantPerformanceResult::getEstTradeAmount).reversed())
				.limit(limit * 3)
				.sorted(Comparator.comparingDouble(InstantPerformanceResult::getDailyChangePct).reversed())
				.limit(limit)
				.map(stock -> stock.getCurrentStockBean().getStockCode() + ":" + GeneralHelper.toPct(stock.getDailyChangePct())
						+ "_V:" + GeneralHelper.toPct(stock.getCurrentStockBean().getDayVolumeChgPct())
						+ "_rsi(9):" + GeneralHelper.to100(stock.getCurrentStockBean().getRsi9())
				)
				.toList();
	}

	private static void displayResults(AnalysisResults results, String type, String sectorOrSymbol, String txnDate, InstantPerformanceResult sectorOrSymbolBean) {
		StringBuilder sb = new StringBuilder();
		sb.append("\t--------\t--------\t--------\n");
		sb.append(sectorOrSymbol).append("\t").append("[").append(type).append("]").append("  ").append(txnDate).append("  ").append(sectorOrSymbol).append("  ").append(LocalDateTime.now());

		if (sectorOrSymbolBean != null) {
			sb.append("\n").append(sectorOrSymbol).append("\t 1D%: ").append(GeneralHelper.toPct(sectorOrSymbolBean.getDailyChangePct()))
					.append("\t 3D%(O2C): ").append(GeneralHelper.toPct(sectorOrSymbolBean.getThreeDaysChangeO2CPct()))
					.append("\t 5D%(O2C): ").append(GeneralHelper.toPct(sectorOrSymbolBean.getWeeksChangeO2CPct()))
					.append("\t MTD%(O2C): ").append(GeneralHelper.toPct(sectorOrSymbolBean.getMtdChangeO2CPct()))
					.append("\t RSI9(5D): ").append(sectorOrSymbolBean.getRsi9TrendIn5Days())
					.append("\t V: ").append(GeneralHelper.toPct(sectorOrSymbolBean.getCurrentStockBean().getDayVolumeChgPct()))
					.append("\t").append((sectorOrSymbolBean.getSysPickLongCategory() + " " + sectorOrSymbolBean.getSysPickStagnantCategory() + " " + sectorOrSymbolBean.getSysPickShortCategory()).replaceAll("\\[|\\]", ""));
		}

		sb.append("\n").append(sectorOrSymbol).append("  ").append("上升比例").append("  ").append(GeneralHelper.toPct(results.ratioPositiveDayChgPctCnt));
		sb.append("\n").append(sectorOrSymbol).append("\t").append("強於 ").append(sectorOrSymbol).append(" 比例 (cnt / records)").append("\t").append(GeneralHelper.toPct(results.ratioStrongerThanIndexCnt)).append(" (").append(results.strongerThanIndexCnt).append(" / ").append(results.cnt).append(")");
		sb.append("\n").append(sectorOrSymbol).append("\t").append("RSI9(強/弱)").append("\t").append("強:").append(GeneralHelper.toPct(results.ratioUpRsi9)).append("\t弱:").append(GeneralHelper.toPct(results.ratioDownRsi9));
		sb.append("\n").append(sectorOrSymbol).append("\t").append("RSI9強").append("\t").append(results.upRsi9List);
		sb.append("\n").append(sectorOrSymbol).append("\t").append("RSI9弱").append("\t").append(results.downRsi9List);
		sb.append("\n").append(sectorOrSymbol).append("\t").append("大於20D比例").append("\t").append(GeneralHelper.toPct(results.ratioAbv20D)).append("\t").append(results.up20DList);
		sb.append("\n").append(sectorOrSymbol).append("\t").append("大於50D比例").append("\t").append(GeneralHelper.toPct(results.ratioAbv50D)).append("\t").append(results.up50DList);

		sb.append("\n").append(sectorOrSymbol).append("\t").append("20D大於50D的比例").append("\t").append(GeneralHelper.toPct(results.ratioUp20DAbv50D)).append("\t").append(results.up20DAbv50DList);
		sb.append("\n").append(sectorOrSymbol).append("\t").append("Top").append(results.top10List.size()).append("_(強於 ").append(sectorOrSymbol).append(")").append("\t").append(String.join(", ", results.top10List));
		sb.append("\n").append(sectorOrSymbol).append("\t").append("Top").append(results.top10Rsi9List.size()).append("_(RSI9強於 ").append(sectorOrSymbol).append(")").append("\t").append(String.join(", ", results.top10Rsi9List));
		sb.append("\n").append(sectorOrSymbol).append("\t").append("Top").append(results.top10VolumnList.size()).append("_(Vol)").append("\t").append(String.join(", ", results.top10VolumnList));
		sb.append("\n").append(sectorOrSymbol).append("\t").append("Top").append(results.top10WithGoodMAList.size()).append("_(UP) 小多頭").append("\t").append(String.join(", ", results.top10WithGoodMAList));
		sb.append("\n").append(sectorOrSymbol).append("\t").append("Top").append(results.top10DownList.size()).append("_(弱於 ").append(sectorOrSymbol).append(")").append("\t").append(String.join(", ", results.top10DownList));
		sb.append("\n").append(sectorOrSymbol).append("\t").append("Top").append(results.top10Rsi9DownList.size()).append("_(RSI9弱於 ").append(sectorOrSymbol).append(")").append("\t").append(String.join(", ", results.top10Rsi9DownList));
		sb.append("\n").append(sectorOrSymbol).append("\t").append("Top").append(results.top10TodayStrongList.size()).append("_(轉").append(PatternTrendHelper.STRONG).append(")").append("\t").append(String.join(", ", results.top10TodayStrongList));
		sb.append("\n").append(sectorOrSymbol).append("\t").append("Top").append(results.top10TodayWeakList.size()).append("_(轉").append(PatternTrendHelper.WEAK).append(")").append("\t").append(String.join(", ", results.top10TodayWeakList));
		sb.append("\n").append(sectorOrSymbol).append("\t").append("強/弱/總數 ").append("\t").append(results.cnt2Strong).append(" / ").append(results.cnt2Weak).append(" / ").append(results.cnt);
		sb.append("\n").append(sectorOrSymbol).append("\t").append("Break (Up vs Down)").append("\t").append("UpBreak % : ").append(GeneralHelper.toPct((double) results.cntUpBreakSign / results.cnt)).append("\t DwBreak %").append(GeneralHelper.toPct((double) results.cntDwBreakSign / results.cnt)).append("\t Up/Dw/總 : ").append(results.cntUpBreakSign).append(" / ").append(results.cntDwBreakSign).append(" / ").append(results.cnt);
		sb.append("\n").append(sectorOrSymbol).append("\t").append("Up_Break_(D0)").append("\t").append(results.upBreakD0SignList.size()).append(": ").append(results.upBreakD0SignList);
		sb.append("\n").append(sectorOrSymbol).append("\t").append("Down_Break_(D0)").append("\t").append(results.downBreakD0SignList.size()).append(": ").append(results.downBreakD0SignList);
		sb.append("\n").append(sectorOrSymbol).append("\t").append("Up_Break_(待)").append("  ").append(results.upBreakReadyList.size()).append(":\t ").append(results.upBreakReadyList);
		sb.append("\n").append(sectorOrSymbol).append("\t").append("Down_Break_(待)").append("  ").append(results.downBreakReadyList.size()).append(":\t ").append(results.downBreakReadyList);

		System.out.println(sb.toString());
	}

	// Helper class to store analysis results
	private static class AnalysisResults {
		double ratioPositiveDayChgPctCnt;
		double ratioStrongerThanIndexCnt;
		double ratioAbv20D;
		double ratioAbv50D;
		double ratioUp20DAbv50D;
		double ratioUpRsi9;
		double ratioDownRsi9;
		List<String> up20DList;
		List<String> up50DList;
		List<String> up20DAbv50DList;
		List<String> upRsi9List;
		List<String> downRsi9List;

		List<String> top10List;
		List<String> top10Rsi9List;
		List<String> top10DownList;
		List<String> top10Rsi9DownList;
		List<String> top10WithGoodMAList;
		List<String> top10VolumnList;
		List<String> top10TodayStrongList;
		List<String> top10TodayWeakList;
		long cnt2Strong;
		long cnt2Weak;
		long cntUpBreakSign;
		long cntDwBreakSign;
		List<String> upBreakD0SignList;
		List<String> upBreakReadyList;
		List<String> downBreakReadyList;
		List<String> downBreakD0SignList;
		long strongerThanIndexCnt; // Add this field
		long cnt; // Add this field

		public AnalysisResults(double ratioPositiveDayChgPctCnt, double ratioStrongerThanIndexCnt, double ratioAbv20D, double ratioAbv50D, double ratioUp20DAbv50D,
							   double ratioUpRsi9, double ratioDownRsi9,
							   List<String> up20DList, List<String> up50DList, List<String> up20DAbv50DList,
							   List<String> upRsi9List, List<String> downRsi9List,
							   List<String> top10List, List<String> top10Rsi9List, List<String> top10DownList,List<String> top10Rsi9DownList, List<String> top10WithGoodMAList,
							   List<String> top10VolumnList, List<String> top10TodayStrongList, List<String> top10TodayWeakList, long cnt2Strong, long cnt2Weak,
							   long cntUpBreakSign, long cntDwBreakSign, List<String> upBreakD0SignList, List<String> upBreakReadyList, List<String> downBreakReadyList,
							   List<String> downBreakD0SignList, long strongerThanIndexCnt, long cnt) { // Update constructor
			this.ratioPositiveDayChgPctCnt = ratioPositiveDayChgPctCnt;
			this.ratioStrongerThanIndexCnt = ratioStrongerThanIndexCnt;
			this.ratioAbv20D = ratioAbv20D;
			this.ratioAbv50D = ratioAbv50D;
			this.ratioUp20DAbv50D = ratioUp20DAbv50D;
			this.ratioDownRsi9 = ratioDownRsi9;
			this.ratioUpRsi9 = ratioUpRsi9;
			this.up20DList = up20DList;
			this.up50DList = up50DList;
			this.up20DAbv50DList = up20DAbv50DList;
			this.downRsi9List = downRsi9List;
			this.upRsi9List = upRsi9List;
			this.top10List = top10List;
			this.top10Rsi9List = top10Rsi9List;
			this.top10DownList = top10DownList;
			this.top10Rsi9DownList = top10Rsi9DownList;
			this.top10WithGoodMAList = top10WithGoodMAList;
			this.top10VolumnList = top10VolumnList;
			this.top10TodayStrongList = top10TodayStrongList;
			this.top10TodayWeakList = top10TodayWeakList;
			this.cnt2Strong = cnt2Strong;
			this.cnt2Weak = cnt2Weak;
			this.cntUpBreakSign = cntUpBreakSign;
			this.cntDwBreakSign = cntDwBreakSign;
			this.upBreakD0SignList = upBreakD0SignList;
			this.upBreakReadyList = upBreakReadyList;
			this.downBreakReadyList = downBreakReadyList;
			this.downBreakD0SignList = downBreakD0SignList;
			this.strongerThanIndexCnt = strongerThanIndexCnt; // Initialize new field
			this.cnt = cnt; // Initialize new field
		}
	}

}

