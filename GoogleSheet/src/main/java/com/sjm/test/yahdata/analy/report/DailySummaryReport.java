package com.sjm.test.yahdata.analy.report;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.helper.CFGHelper;
import com.sjm.test.yahdata.analy.model.InstantPerformanceResult;
import com.sjm.test.yahdata.analy.model.StatisticsResult;
import com.sjm.test.yahdata.analy.ta.PatternTrendHelper;
import com.sjm.test.yahdata.analy.ta.helper.SectorAnalystHelper;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class DailySummaryReport {
	private static final String MKT_US = "USD";
	private static final String MKT_HK = "HKD";

	public DailySummaryReport() {
	}
	
	public static void printSimpleStatisticsResult(List<InstantPerformanceResult> performanceResultList, String market, String ICONIC_CODE) {
		
		Optional<InstantPerformanceResult> optional = performanceResultList.stream().filter( x->x.getCurrentStockBean().getStockCode().equalsIgnoreCase(ICONIC_CODE)).findFirst();
		//ICONIC_CODE
		if(optional != null && optional.isPresent()) {
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
	
	protected static void printSimpleHKStatisticsResult(List<InstantPerformanceResult> performanceResultList) {
		printSimpleStatisticsResult(performanceResultList, SectorAnalystHelper.CODE_INDEX_HSI, SectorAnalystHelper.CODE_ETF_HK_TECH, SectorAnalystHelper.CODE_ETF_HSCEI_ELEMENT, SectorAnalystHelper.LABEL_HK_DUAL_COUNTER_CNH);
	}
	
	protected static void printSimpleUSStatisticsResult(List<InstantPerformanceResult> performanceResultList) {
		printSimpleStatisticsResult(performanceResultList, SectorAnalystHelper.CODE_ETF_DIA, SectorAnalystHelper.CODE_ETF_SPY, SectorAnalystHelper.CODE_ETF_QQQ, SectorAnalystHelper.CODE_US_CHINA_CONCEPT);
	}
	
	protected static void printSimpleStatisticsResult(List<InstantPerformanceResult> performanceResultList, String filterName1, String filterName2, String filterName3, String filterName4) {
		StringBuilder sb =  new StringBuilder();
		sb.append("\n\n" +toFunc(performanceResultList, filterName1));
		sb.append("\n\n" +toFunc(performanceResultList, filterName2));
		sb.append("\n\n" +toFunc(performanceResultList, filterName3));
		sb.append("\n\n" +toFunc(performanceResultList, filterName4));
		System.out.println(sb.toString());
		
		System.out.println();
	}
	
	public static String toFunc(List<InstantPerformanceResult> perfromanceResultList, String filterName1) {
		
		List<InstantPerformanceResult> targetResultList = perfromanceResultList.stream()
				.filter(x-> x.getEstTradeAmount() >1.0 && x.getSector() !=null	&& !(x.getSector().contains("ETF") || x.getSector().contains("index") || x.getSector().contains("currency") || x.getSector().contains("指數") || x.getSector().contains("貨幣") || x.getSector().contains("Crypto") || x.getSector().contains("債券"))
				).collect(Collectors.toList());
			double highVolRatio = 1.2;
			
		Optional<InstantPerformanceResult> optional = perfromanceResultList.stream()
				.filter(x -> x.getCurrentStockBean().getStockCode().contains(filterName1))
				.findFirst();
		String resultMsg = "";

		
		if(optional != null && optional.isPresent()) 
		{
			StringBuilder sb = new StringBuilder();
			//posiReturn with Portion ---- START
			long cnt = targetResultList.stream().filter(x -> x.getBelongETF().contains(filterName1)).count();
			long upCnt = targetResultList.stream().filter( x-> x.getCurrentStockBean().getDayChgPct()>0.01 && x.getBelongETF().contains(filterName1)).count();
			long upCntWithMoreVol = targetResultList.stream().filter( x-> x.getCurrentStockBean().getDayChgPct()>0.01 && x.getCurrentStockBean().getDayVolumeChgPct()>highVolRatio && x.getBelongETF().contains(filterName1)).count();
			
			double ratioUp = (double)upCnt / (double)cnt;
			double ratioUpVol = (double)upCntWithMoreVol / (double)upCnt;
			
			sb.append("Name\t上升比例\t上升總數\tNo.of Stock\t上升數目中的More vol比例\n");
			sb.append(filterName1+ "\t"+GeneralHelper.toPct(ratioUp)+"\t"+upCnt+"\t"+cnt+"\t"+GeneralHelper.toPct(ratioUpVol));
			sb.append("\n\n");
			
			
			InstantPerformanceResult nameResult = optional.get();		    	    
			List<InstantPerformanceResult> goodPerformanceList = perfromanceResultList.stream()
					.filter(x -> x.getBelongETF().contains(filterName1) 
							&& x.getThreeDaysChangeO2HPct() > nameResult.getThreeDaysChangeO2HPct() 
							&& x.getWeeksChangeO2PHPct() > nameResult.getWeeksChangeO2PHPct()
							&& x.getCurrentStockBean().getDayVolumeChgPct() > nameResult.getCurrentStockBean().getDayVolumeChgPct() 
							)
					.sorted(Comparator.comparingDouble(InstantPerformanceResult::getThreeDaysChangeO2HPct).reversed())
					.toList();
			
			List<InstantPerformanceResult> badPerformanceList = perfromanceResultList.stream()
					.filter(x -> x.getBelongETF().contains(filterName1) 
							&& x.getThreeDaysChangeO2LPct() < nameResult.getThreeDaysChangeO2LPct() 
							&& x.getWeeksChangeO2PLPct() < nameResult.getWeeksChangeO2PLPct())
					.sorted(Comparator.comparingDouble(InstantPerformanceResult::getThreeDaysChangeO2HPct))
					.toList();
			
			double basicTradeAmount = 80.0;
			
			List<String> goodCodeList = goodPerformanceList.stream().filter(stock -> stock.getCurrentStockBean().getEstTradeAmount()>basicTradeAmount)
//	                .map(StockBean::getStockCode)
			        .map(stock -> stock.getCurrentStockBean().getStockCode() + " " + GeneralHelper.toPct(stock.getThreeDaysChangeO2HPct()))
	                .toList();
			List<String> badCodeList = badPerformanceList.stream().filter(stock -> stock.getCurrentStockBean().getEstTradeAmount()>basicTradeAmount)
//	                .map(StockBean::getStockCode)
			        .map(stock -> stock.getCurrentStockBean().getStockCode() + " " + GeneralHelper.toPct(stock.getThreeDaysChangeO2LPct()))
	                .toList();
			
			resultMsg = nameResult.getCurrentStockBean().getStockCode()  + ", Today Chg%: " + GeneralHelper.toPct(nameResult.getCurrentStockBean().getDayChgPct())
					+ ", 3-Days O2PH: "+GeneralHelper.toPct(nameResult.getThreeDaysChangeO2HPct()) + ", 1-Week O2PH: "+GeneralHelper.toPct(nameResult.getWeeksChangeO2PHPct())	
					+"\n\t 3D pct O2PH: "+ goodCodeList;
			
//			resultMsg2 = "\t 3-Days O2PL: "+GeneralHelper.to100Pct(nameResult.getThreeDaysChangeO2LPct()) + ", 1-Week O2PL: "+GeneralHelper.to100Pct(nameResult.getWeeksChangeO2PLPct())	
//					+"\n\t 3D pct O2PL : "+ badCodeList;
			
			String title ="Name\tDay Chg%\tDay2H Chg%\t3D Chg(O2PH)\t5D Chg(O2PH)\tO2PH-3天Pct順次序(數目:"+goodCodeList.size()+")";
			StringBuffer data = new StringBuffer();
			data.append(title+"\n");
			data.append(nameResult.getCurrentStockBean().getStockCode()+"\t");
			data.append(GeneralHelper.toPct(nameResult.getCurrentStockBean().getDayChgPct())+"\t");
			data.append(GeneralHelper.toPct(nameResult.getCurrentStockBean().getDay2HChgPct())+"\t");
			data.append(GeneralHelper.toPct(nameResult.getThreeDaysChangeO2HPct())+"\t");			
			data.append(GeneralHelper.toPct(nameResult.getWeeksChangeO2PHPct())+"\t");
			data.append(goodCodeList);
			data.append("\n\n");
			
			title ="Name\tDay Chg%\tDay2L Chg%\t3D Chg(O2PL)\t5D Chg(O2PL)\tO2PL-3天Pct倒次序(數目:"+ badCodeList.size()+")";
//			StringBuffer data = new StringBuffer();
			data.append(title+"\n"); 
			data.append(nameResult.getCurrentStockBean().getStockCode()+"\t");
			data.append(GeneralHelper.toPct(nameResult.getCurrentStockBean().getDayChgPct())+"\t");
			data.append(GeneralHelper.toPct(nameResult.getCurrentStockBean().getDay2LChgPct())+"\t");
			data.append(GeneralHelper.toPct(nameResult.getThreeDaysChangeO2LPct())+"\t");			
			data.append(GeneralHelper.toPct(nameResult.getWeeksChangeO2PLPct())+"\t");
			data.append(badCodeList);
			data.append("\n");
			resultMsg = sb.toString() + data.toString();
			
		}
		return resultMsg;
//		return resultMsg + "\n"+resultMsg2;
	}
	
	public static void exportStat(List<StatisticsResult> resultList) {
		StringBuilder msgMain = new StringBuilder();
		msgMain.append("Date\tNoOfStock\t轉強 Cnt\t轉弱 Cnt\t轉強比例\t轉弱比例\t上升比例\t下跌比例\tRSI(9)大於50比例\t大於20天線比例\t大於50天線比例\t大於100天線比例\t大於200天線比例\t2天>19天線比例\t50天>200天線比例\t轉強的(TOP 10 交易量) \t轉弱的(TOP 10 交易量)\t ");
//		msgMain.append(ICONIC_CODE+" close\tPct.\tRSI(9)\tRSI(14)\t "+BASE_STOCK_B+" close\tPct.\tRSI(9)\tRSI(14)\t "+BASE_STOCK_C+" close\tPct.\tRSI(9)\tRSI(14)\t "+BASE_STOCK_D+" close\tPct.\tRSI(9)\tRSI(14)");//
		msgMain.append("\t價升量升比例(HSI)\t價升量升比例(HS Tech)\t價升量升比例(雙櫃台)\t價升量升比例(RedChips)\t上升比例(SPY)\t上升比例(QQQ)\t上升比例(DIA)\t上升比例(China Concept)");
		msgMain.append("\n");
		
		for (StatisticsResult elemt : resultList) 
		{
			try {
				StringBuilder msg = new StringBuilder();
				msg.append(elemt.getTxnDate());
				msg.append("\t"+elemt.getNumOfStock());
				msg.append("\t"+elemt.getStrongCnt());
				msg.append("\t"+elemt.getWeakCnt());
	//			msg.append("\t"+elemt.getNumOfStock());
				msg.append("\t"+GeneralHelper.toPct((double)elemt.getStrongCnt() / (double)(elemt.getNumOfStock())));
				msg.append("\t"+GeneralHelper.toPct((double)elemt.getWeakCnt() / (double)(elemt.getNumOfStock())));
				msg.append("\t"+GeneralHelper.toPct( elemt.getRatioDailyPositiveReturn()));
				msg.append("\t"+GeneralHelper.toPct( elemt.getRatioDailyNegativeReturn()));
				
				msg.append("\t"+GeneralHelper.toPct((double)elemt.getRsi9Abv50Cnt()/ (double)(elemt.getNumOfStock())));
				msg.append("\t"+GeneralHelper.toPct((double)elemt.getAbv20DCnt() / (double)(elemt.getNumOfStock())));
				msg.append("\t"+GeneralHelper.toPct((double)elemt.getAbv50DCnt() / (double)(elemt.getNumOfStock())));
				msg.append("\t"+GeneralHelper.toPct((double)elemt.getAbv100DCnt() / (double)(elemt.getNumOfStock())));
				msg.append("\t"+GeneralHelper.toPct((double)elemt.getAbv200DCnt() / (double)(elemt.getNumOfStock())));
				msg.append("\t"+GeneralHelper.toPct((double)elemt.getMa2AbvMA19Cnt()/ (double)(elemt.getNumOfStock())));
				msg.append("\t"+GeneralHelper.toPct((double)elemt.getMa50AbvMA200Cnt()/ (double)(elemt.getNumOfStock())));
				
				msg.append("\t"+elemt.getLeadingStock());
				msg.append("\t"+elemt.getTailStock());
				msg.append("\t"+elemt.getIconicAClose());
				msg.append("\t"+GeneralHelper.toPct(elemt.getIconicAPct()));
				msg.append("\t"+ GeneralHelper.to2DecimalPlaces(elemt.getIconicARsi9()));
				msg.append("\t"+ GeneralHelper.to2DecimalPlaces(elemt.getIconicARsi14()));
				
				msg.append("\t"+elemt.getIconicBClose());
				msg.append("\t"+GeneralHelper.toPct(elemt.getIconicBPct()));
				msg.append("\t"+ ((elemt.getIconicBRsi9()!=null)?GeneralHelper.toPct(elemt.getIconicBRsi9()):Const.NA));
				msg.append("\t"+ ((elemt.getIconicBRsi14()!=null)?GeneralHelper.toPct(elemt.getIconicBRsi14()):Const.NA));
				
				msg.append("\t"+elemt.getIconicCClose());
				msg.append("\t"+GeneralHelper.toPct(elemt.getIconicCPct()));				
				msg.append("\t"+ ((elemt.getIconicCRsi9()!=null)?GeneralHelper.toPct(elemt.getIconicCRsi9()):Const.NA));
				msg.append("\t"+ ((elemt.getIconicCRsi14()!=null)?GeneralHelper.toPct(elemt.getIconicCRsi14()):Const.NA));
				
				msg.append("\t"+ elemt.getIconicDClose());
				msg.append("\t"+ GeneralHelper.toPct(elemt.getIconicDPct()));
				msg.append("\t"+ ((elemt.getIconicDRsi9()!=null)?GeneralHelper.toPct(elemt.getIconicDRsi9()):Const.NA));
				msg.append("\t"+ ((elemt.getIconicDRsi14()!=null)?GeneralHelper.toPct(elemt.getIconicDRsi14()):Const.NA));
				
				msg.append("\t"+GeneralHelper.toPct( elemt.getRatioUpHSI()));
				msg.append("\t"+GeneralHelper.toPct( elemt.getRatioUpHSTech()));
				msg.append("\t"+GeneralHelper.toPct( elemt.getRatioUpDualCounter()));
				msg.append("\t"+GeneralHelper.toPct( elemt.getRatioUpRedChip()));
				msg.append("\t"+GeneralHelper.toPct( elemt.getRatioUpSPY()));
				msg.append("\t"+GeneralHelper.toPct( elemt.getRatioUpQQQ()));
				msg.append("\t"+GeneralHelper.toPct( elemt.getRatioUpDIA()));
				msg.append("\t"+GeneralHelper.toPct( elemt.getRatioUpChinaConcept()));
				msg.append("\n");
				msgMain.append(msg);
			}catch(Exception e) {
				log.error(""+elemt.getTxnDate() , e);
			}
		}
		
		log.info("\n"+msgMain);
	}
	
	
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

	
	
	private static void doAnaly(String mkt, String type, List<InstantPerformanceResult> perfromanceResultList, String sectorOrSymbol) {
		
				double REQUIRED_TRADE_AMOUNT = (Const.IS_INTRADAY)?30.0:100.0;
				int LIMIT_RESULTS = 10;
				StringBuilder sb = new StringBuilder();


				boolean hasDisplayData = false;
				String txnDate = perfromanceResultList.get(0).getCurrentStockBean().getTxnDate();

					
					Optional<InstantPerformanceResult> optional = perfromanceResultList.stream()
							.filter(x -> x.getCurrentStockBean().getStockCode().equalsIgnoreCase(sectorOrSymbol)).findFirst();
					InstantPerformanceResult sectorOrSymbolBean;
					if(optional.isPresent()) {
						sectorOrSymbolBean = optional.get();			
					} else {
                        sectorOrSymbolBean = null;
						System.out.println("Not found "+sectorOrSymbol + ", sectorOrSymbolBean = null");
						return ;
                    }


                    List<InstantPerformanceResult> tmpList = extract(type, sectorOrSymbol, txnDate, perfromanceResultList);
					if(tmpList.isEmpty())
						return ;
					
					
					hasDisplayData = true;



					long cnt = tmpList.size();
					long strongerThanIndexCnt = tmpList.stream().filter( x-> sectorOrSymbolBean!=null && x.getCurrentStockBean().getDayChgPct()>sectorOrSymbolBean.getDailyChangePct() ).count();
					long positiveDayChgPctCnt = tmpList.stream().filter( x-> sectorOrSymbolBean!=null && x.getCurrentStockBean().getDayChgPct() > 0.0 ).count();

					long up20DCnt = tmpList.stream().filter( x-> x.getCurrentStockBean().getC() > x.getCurrentStockBean().getPriceSma().getMa20() ).count();
					long up50DCnt = tmpList.stream().filter( x-> x.getCurrentStockBean().getC() > x.getCurrentStockBean().getPriceSma().getMa50() ).count();
					long up20DAbv50DCnt = tmpList.stream()
							.filter( x-> x.getCurrentStockBean().getC() > x.getCurrentStockBean().getPriceSma().getMa20()
					 		&& x.getCurrentStockBean().getPriceSma().getMa10() > x.getCurrentStockBean().getPriceSma().getMa50()
					 		&& x.getCurrentStockBean().getPriceSma().getMa50() > x.getCurrentStockBean().getPriceSma().getMa200()
							).count();

					double ratioPositiveDayChgPctCnt = (double)positiveDayChgPctCnt / (double)cnt;
					double ratioStrongerThanIndexCnt = (double)strongerThanIndexCnt / (double)cnt;
					
					double ratioAbv20D = (double)up20DCnt / (double)cnt;
					double ratioAbv50D = (double)up50DCnt / (double)cnt;
					double ratioUp20DAbv50D = (double)up20DAbv50DCnt / (double)cnt;
					
					 List<String> top10List = tmpList.stream()
							 .filter(x->x.getCurrentStockBean().getEstTradeAmount() > REQUIRED_TRADE_AMOUNT && sectorOrSymbolBean!=null
									 && !"Index".equalsIgnoreCase(x.getSector())
									 && x.getCurrentStockBean().getDayChgPct()>sectorOrSymbolBean.getDailyChangePct())
							 .sorted(Comparator.comparingDouble(InstantPerformanceResult::getEstTradeAmount).reversed())
							 .limit(LIMIT_RESULTS*3)
							 .sorted(Comparator.comparingDouble(InstantPerformanceResult::getDailyChangePct).reversed())
				                .limit(LIMIT_RESULTS)
//				                .map(InstantPerformanceResult::getCurrentStockBean)
				                .map(
				                		stock -> stock.getCurrentStockBean().getStockCode() + ": " + GeneralHelper.toPct(stock.getDailyChangePct())
				                		+" V:"+GeneralHelper.toPct(stock.getCurrentStockBean().getDayVolumeChgPct())
		                				+" "+ (stock.getSysPickLongCategory()+","+stock.getSysPickStagnantCategory()+","+stock.getSysPickShortCategory()).replaceAll("\\[|\\]", "").replaceAll(",,", ",")+" "
		                				+ stock.getWaveShape().getShapeResult().replaceAll("NA", "")
				                )
				                .toList();
					 
					 List<String> top10O2HFor3DaysList = tmpList.stream()
							 .filter(x->x.getCurrentStockBean().getEstTradeAmount() > REQUIRED_TRADE_AMOUNT
									 && !"Index".equalsIgnoreCase(x.getSector())
									 && x.getThreeDaysChangeO2HPct()>sectorOrSymbolBean.getDailyChangePct())
				                .sorted((doc1, doc2) -> Double.compare(doc2.getThreeDaysChangeO2HPct(), doc1.getThreeDaysChangeO2HPct()))
				                .limit(LIMIT_RESULTS)
				                .map(
				                		stock -> stock.getCurrentStockBean().getStockCode() + ": " + GeneralHelper.toPct(stock.getThreeDaysChangeO2HPct())
				                )
				                .toList();
					
					 List<String> top10O2LFor3DaysList = tmpList.stream()
							 .filter(x->x.getCurrentStockBean().getEstTradeAmount() > REQUIRED_TRADE_AMOUNT
									 && !"Index".equalsIgnoreCase(x.getSector())
									 && x.getThreeDaysChangeO2LPct()<sectorOrSymbolBean.getDailyChangePct())
				                .sorted((doc1, doc2) -> Double.compare(doc1.getThreeDaysChangeO2LPct(), doc2.getThreeDaysChangeO2LPct()))
				                .limit(LIMIT_RESULTS)
				                .map(
				                		stock -> stock.getCurrentStockBean().getStockCode() + ": " + GeneralHelper.toPct(stock.getThreeDaysChangeO2LPct())
				                )
				                .toList();
					 
					 List<String> top10DownList = tmpList.stream()
							 .filter(x->x.getCurrentStockBean().getEstTradeAmount() > REQUIRED_TRADE_AMOUNT
									 && !"Index".equalsIgnoreCase(x.getSector())
									 && x.getCurrentStockBean().getDayChgPct()< sectorOrSymbolBean.getDailyChangePct())
							 .sorted(Comparator.comparingDouble(InstantPerformanceResult::getEstTradeAmount).reversed())
							 .limit(LIMIT_RESULTS*3)
							 .sorted(Comparator.comparingDouble(InstantPerformanceResult::getDailyChangePct))
				                .limit(LIMIT_RESULTS)
				                .map(
				                		stock -> stock.getCurrentStockBean().getStockCode() + ": " + GeneralHelper.toPct(stock.getDailyChangePct())+" V:"+GeneralHelper.toPct(stock.getCurrentStockBean().getDayVolumeChgPct())
				                )
				                .toList();
					 
					 
					 List<String> top10WithGoodMAList = tmpList.stream()
							 	.filter(x->x.getCurrentStockBean().getEstTradeAmount() > REQUIRED_TRADE_AMOUNT
										&& !"Index".equalsIgnoreCase(x.getSector())
										&& x.getCurrentStockBean().getDayChgPct()>=0.01
							 			&& x.getCurrentStockBean().getC() > x.getCurrentStockBean().getPriceSma().getMa20()
							 			&& x.getCurrentStockBean().getPriceSma().getMa10() > x.getCurrentStockBean().getPriceSma().getMa50()
							 			&& x.getCurrentStockBean().getPriceSma().getMa50() > x.getCurrentStockBean().getPriceSma().getMa200()
							 		
							 	)
							 .sorted(Comparator.comparingDouble(InstantPerformanceResult::getEstTradeAmount).reversed())
							 .limit(LIMIT_RESULTS*3)
							 .sorted(Comparator.comparingDouble(InstantPerformanceResult::getDailyChangePct).reversed())
				                .limit(LIMIT_RESULTS)
				                .map(
				                		stock -> stock.getCurrentStockBean().getStockCode() + ": " + GeneralHelper.toPct(stock.getDailyChangePct())
				                ).toList();
					 
					 
					 List<String> top10VolumnList = tmpList.stream()
							 .filter(x->x.getCurrentStockBean().getEstTradeAmount() > REQUIRED_TRADE_AMOUNT && !"Index".equalsIgnoreCase(x.getSector()))
				                .sorted((doc1, doc2) -> Double.compare(doc2.getCurrentStockBean().getDayVolumeChgPct(), doc1.getCurrentStockBean().getDayVolumeChgPct()))
				                .limit(LIMIT_RESULTS)
				                .map(
				                		stock -> stock.getCurrentStockBean().getStockCode() + ": " + GeneralHelper.toPct(stock.getDailyChangePct())
				                		+" V:"+GeneralHelper.toPct(stock.getCurrentStockBean().getDayVolumeChgPct())
				                		+" "+ (stock.getSysPickLongCategory()+","+stock.getSysPickStagnantCategory()+","+stock.getSysPickShortCategory()).replaceAll("\\[|\\]", "").replaceAll(",,", ",")+" "
				                		+ stock.getWaveShape().getShapeResult().replaceAll("NA", "")
				                )
				                .toList();

					 
					 List<String> top10TodayStrongList = tmpList.stream()
							 .filter(x->x.getCurrentStockBean().getEstTradeAmount() > REQUIRED_TRADE_AMOUNT
									 && x.getStrongWeakTypeToday().getType().contains(PatternTrendHelper.STRONG)
									 && !"Index".equalsIgnoreCase(x.getSector()))
							 .sorted(Comparator.comparingDouble(InstantPerformanceResult::getEstTradeAmount).reversed())
							 .limit(LIMIT_RESULTS*3)
							 .sorted(Comparator.comparingDouble(InstantPerformanceResult::getDailyChangePct).reversed())
				                .map(
				                		stock -> stock.getCurrentStockBean().getStockCode() + ": " + GeneralHelper.toPct(stock.getDailyChangePct())
				                		+" V:"+GeneralHelper.toPct(stock.getCurrentStockBean().getDayVolumeChgPct())
				                		+" " + stock.getStrongWeakTypeToday().getType()
				                )
				                .toList();
					 List<String> top10TodayWeakList = tmpList.stream()
							 .filter(x->x.getCurrentStockBean().getEstTradeAmount() > REQUIRED_TRADE_AMOUNT
									 && x.getStrongWeakTypeToday().getType().contains(PatternTrendHelper.WEAK)
									 && !"Index".equalsIgnoreCase(x.getSector()))
							 .sorted(Comparator.comparingDouble(InstantPerformanceResult::getEstTradeAmount).reversed())
							 .limit(LIMIT_RESULTS*3)
							 .sorted(Comparator.comparingDouble(InstantPerformanceResult::getDailyChangePct))
				             .limit(LIMIT_RESULTS)
				              .map(
				                		stock -> stock.getCurrentStockBean().getStockCode() + ": " + GeneralHelper.toPct(stock.getDailyChangePct())
				                		+" V:"+GeneralHelper.toPct(stock.getCurrentStockBean().getDayVolumeChgPct())
				                		+" " + stock.getStrongWeakTypeToday().getType()
				                )
				                .toList();


					long cnt2Strong = tmpList.stream().filter( x-> x.getStrongWeakTypeToday().getType().contains(PatternTrendHelper.STRONG) ).count();
					long cnt2Weak = tmpList.stream().filter( x-> x.getStrongWeakTypeToday().getType().contains(PatternTrendHelper.WEAK) ).count();

					//elemt.getRecentMonthDaysVolumeStatus() //小浪型狀
					long cntUpBreakSign = tmpList.stream()
							.filter( x-> x.getWaveShape()!=null && x.getWaveShape().getShapeResult().contains(Const.UP)
									&& !x.getWaveShape().getShapeResult().contains(Const.D0) && !x.getWaveShape().getShapeResult().contains(Const.WAIT)
									&& !"Index".equalsIgnoreCase(x.getSector())).count();
					long cntDwBreakSign = tmpList.stream()
							.filter( x-> x.getWaveShape()!=null && x.getWaveShape().getShapeResult().contains(Const.DOWN)
									&& !x.getWaveShape().getShapeResult().contains(Const.D0) && !x.getWaveShape().getShapeResult().contains(Const.WAIT)
									&& !"Index".equalsIgnoreCase(x.getSector())).count();


//					List<String> upBreakD0SignList = tmpList.stream()
//							.filter( x-> x.getWaveShape()!=null
//									&& x.getWaveShape().getShapeResult().contains(Const.UP+Const.D0)
//									&& !"Index".equalsIgnoreCase(x.getSector()))
//							.sorted((doc1, doc2) -> Double.compare(doc2.getDailyChangePct(), doc1.getDailyChangePct()))
//							.map( stock -> stock.getCurrentStockBean().getStockCode()
//									+ " :" + GeneralHelper.toPct(stock.getDailyChangePct())
//									+" V:"+GeneralHelper.toPct(stock.getCurrentStockBean().getDayVolumeChgPct()))
//							.toList();

					List<String> upBreakD0SignList = tmpList.stream()
							.filter(x -> x.getWaveShape() != null
									&& x.getWaveShape().getShapeResult().contains(Const.UP + Const.D0)
									&& !"Index".equalsIgnoreCase(x.getSector()))
							.sorted(Comparator.comparingDouble(InstantPerformanceResult::getEstTradeAmount).reversed())
							.limit(LIMIT_RESULTS*3)
							.sorted(Comparator.comparingDouble(InstantPerformanceResult::getDailyChangePct).reversed())
							.limit(LIMIT_RESULTS)
							.map(stock -> stock.getCurrentStockBean().getStockCode()
									+ " :" + GeneralHelper.toPct(stock.getDailyChangePct())
									+ " V:" + GeneralHelper.toPct(stock.getCurrentStockBean().getDayVolumeChgPct()))
							.collect(Collectors.toList());

					List<String> upBreakReadyList = tmpList.stream()
							.filter( x-> x.getWaveShape()!=null
									&& x.getWaveShape().getShapeResult().contains(Const.WAIT+Const.UP)
									&& !"Index".equalsIgnoreCase(x.getSector()))
							.sorted(Comparator.comparingDouble(InstantPerformanceResult::getEstTradeAmount).reversed())
							.limit(LIMIT_RESULTS*3)
							.sorted(Comparator.comparingDouble(InstantPerformanceResult::getDailyChangePct).reversed())
							.limit(LIMIT_RESULTS)
							.map( stock -> stock.getCurrentStockBean().getStockCode()
									+ " :" + GeneralHelper.toPct(stock.getDailyChangePct())
									+" V:"+GeneralHelper.toPct(stock.getCurrentStockBean().getDayVolumeChgPct()))
							.toList();

					List<String>  downBreakReadyList = tmpList.stream()
							.filter( x-> x.getWaveShape()!=null
									&& x.getWaveShape().getShapeResult().contains(Const.WAIT+Const.DOWN)
									&& !"Index".equalsIgnoreCase(x.getSector()))
							.sorted(Comparator.comparingDouble(InstantPerformanceResult::getEstTradeAmount).reversed())
							.limit(LIMIT_RESULTS*3)
							.sorted(Comparator.comparingDouble(InstantPerformanceResult::getDailyChangePct))
							.limit(LIMIT_RESULTS)
							.map( stock -> stock.getCurrentStockBean().getStockCode()
									+ " :" + GeneralHelper.toPct(stock.getDailyChangePct())
									+" V:"+GeneralHelper.toPct(stock.getCurrentStockBean().getDayVolumeChgPct()))
							.toList();
					List<String>  downBreakD0SignList = tmpList.stream()
							.filter( x-> x.getWaveShape()!=null
									&& x.getWaveShape().getShapeResult().contains(Const.DOWN+Const.D0)
									&& !"Index".equalsIgnoreCase(x.getSector()))
							.sorted(Comparator.comparingDouble(InstantPerformanceResult::getEstTradeAmount).reversed())
							.limit(LIMIT_RESULTS*3)
							.sorted(Comparator.comparingDouble(InstantPerformanceResult::getDailyChangePct))
							.limit(LIMIT_RESULTS)
							.map( stock -> stock.getCurrentStockBean().getStockCode()
									+ " :" + GeneralHelper.toPct(stock.getDailyChangePct())
									+" V:"+GeneralHelper.toPct(stock.getCurrentStockBean().getDayVolumeChgPct()))
							.toList();



					String [] titles = new String[]
							{"["+type+"]"
							, "上升比例"
							, "強於 "+sectorOrSymbol+" 比例 (cnt / records)"
							, "大於20D比例"
							, "大於50D比例"
							, "小多頭比例(>20,10>50,50>250)"
							, "Top"+top10List.size()+"_(強於 "+sectorOrSymbol+")"
							, "Top"+top10VolumnList.size()+"_(Vol)"
							, "Top"+top10O2HFor3DaysList.size()+"_(強於 "+sectorOrSymbol+")_3天O2H"
							, "Top"+top10WithGoodMAList.size()+"_(UP) 小多頭"
							, "Top"+top10DownList.size()+"_(弱於 "+sectorOrSymbol+")"
							, "Top"+top10O2LFor3DaysList.size()+"_(弱於 "+sectorOrSymbol+")_3天O2L"
							, "Top"+top10TodayStrongList.size()+"_(轉"+PatternTrendHelper.STRONG+")"
							, "Top"+top10TodayWeakList.size()+"_(轉"+PatternTrendHelper.WEAK+")"
							, "強/弱/總數 "
							, "Break (Up vs Down)"
							, "Up_Break_(D0)"
							, "Down_Break_(D0)"
							, "Up_Break_(待)"
							, "Down_Break_(待)"
							};

					sb.append("\t--------\t--------\t--------\n");
					sb.append(sectorOrSymbol +"\t"+titles[0].toUpperCase() +"  "+ txnDate+"  " + sectorOrSymbol +"  "+LocalDateTime.now());


					sb.append("\n"); sb.append(sectorOrSymbol);

					if(sectorOrSymbolBean!=null) {
						sb.append("\t 1D%: " + GeneralHelper.toPct(sectorOrSymbolBean.getDailyChangePct())
								+ "\t 3D%(O2H): "+  GeneralHelper.toPct(sectorOrSymbolBean.getThreeDaysChangeO2HPct())
								+ "\t V: "+ GeneralHelper.toPct(sectorOrSymbolBean.getCurrentStockBean().getDayVolumeChgPct())
								+ "\t"+ (sectorOrSymbolBean.getSysPickLongCategory()+" " + sectorOrSymbolBean.getSysPickStagnantCategory()+" " + sectorOrSymbolBean.getSysPickShortCategory()).replaceAll("\\[|\\]", ""));
					}

					double cntUpBreakSignRatio = (double)cntUpBreakSign / (double)cnt;
					double cntDwBreakSignRatio = (double)cntDwBreakSign / (double)cnt;



					sb.append("\n"); sb.append(sectorOrSymbol +"  "+titles[1] +"  " + GeneralHelper.toPct(ratioPositiveDayChgPctCnt));

					sb.append("\n"); sb.append(sectorOrSymbol +"\t"+titles[2] +"\t" + GeneralHelper.toPct(ratioStrongerThanIndexCnt)+ " ("+ strongerThanIndexCnt + " / " +cnt+")");
					sb.append("\n"); sb.append(sectorOrSymbol +"\t"+titles[3] +"\t" + GeneralHelper.toPct(ratioAbv20D));
					sb.append("\n"); sb.append(sectorOrSymbol +"\t"+titles[4] +"\t" + GeneralHelper.toPct(ratioAbv50D));
					sb.append("\n"); sb.append(sectorOrSymbol +"\t"+titles[5] +"\t" + GeneralHelper.toPct(ratioUp20DAbv50D));
					sb.append("\n"); sb.append(sectorOrSymbol +"\t"+titles[6] +"\t" + String.join(", ", top10List));
					sb.append("\n"); sb.append(sectorOrSymbol +"\t"+titles[7] +"\t" + String.join(", ", top10VolumnList));
					sb.append("\n"); sb.append(sectorOrSymbol +"\t"+titles[8] +"\t" + String.join(", ", top10O2HFor3DaysList));
					sb.append("\n"); sb.append(sectorOrSymbol +"\t"+titles[9] +"\t" + String.join(", ", top10WithGoodMAList));
					sb.append("\n"); sb.append(sectorOrSymbol +"\t"+titles[10] +"\t" + String.join(", ", top10DownList));
					sb.append("\n"); sb.append(sectorOrSymbol +"\t"+titles[11] +"\t" + String.join(", ", top10O2LFor3DaysList));
					sb.append("\n"); sb.append(sectorOrSymbol +"\t"+titles[12] +"\t" + String.join(", ", top10TodayStrongList));
					sb.append("\n"); sb.append(sectorOrSymbol +"\t"+titles[13] +"\t" + String.join(", ", top10TodayWeakList));
					sb.append("\n"); sb.append(sectorOrSymbol +"\t"+titles[14] +"\t" + cnt2Strong + " / "+ cnt2Weak + " / "+ cnt);
					sb.append("\n"); sb.append(sectorOrSymbol +"\t"+titles[15] +"\t" + "UpBreak % : "+GeneralHelper.toPct(cntUpBreakSignRatio) + "\t DwBreak %"+ GeneralHelper.toPct(cntDwBreakSignRatio) + "\t Up/Dw/總 : "+cntUpBreakSign + " / "+ cntDwBreakSign + " / "+ cnt);
					sb.append("\n"); sb.append(sectorOrSymbol +"\t"+titles[16] +"\t" + upBreakD0SignList.size() + ": "+ upBreakD0SignList);
					sb.append("\n"); sb.append(sectorOrSymbol +"\t"+titles[17] +"\t" + downBreakD0SignList.size() + ": "+ downBreakD0SignList);

					sb.append("\n"); sb.append(sectorOrSymbol +"\t"+titles[18] +"  " + upBreakReadyList.size() + ":\t "+ upBreakReadyList);
					sb.append("\n"); sb.append(sectorOrSymbol +"\t"+titles[19] +"  " + downBreakReadyList.size() + ":\t "+ downBreakReadyList);


				if(hasDisplayData)
					System.out.println(sb.toString());
		}


}

