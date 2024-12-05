package com.sjm.test.yahdata.analy.repo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

//import org.apache.log4j.Logger;

import com.sjm.test.yahdata.downloader.YFPageDownloadDemo;

import com.sjm.test.yahdata.downloader.v2.YFApiDownloader;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class YahooDataSource {
//	private static final Logger logger = Logger.getLogger(YahooDataSource.class);

 	public YahooDataSource() {
	}
 	
	private List<String> stockSymbols = new ArrayList<String>(0);

	public DownloadResult readFromYahoo(String interval, List<String> stockSymbolsInput) {
		if(stockSymbolsInput!=null && stockSymbolsInput.isEmpty()==false) {
			stockSymbols.clear();
			stockSymbols.addAll(stockSymbolsInput);
		}
		
		
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		from.add(Calendar.YEAR, -30); // from 1 year ago

		YFApiDownloader stockDownLoader = new YFApiDownloader();
//		YFPageDownloadDemo stockDownLoader = new YFPageDownloadDemo();
//		JSOUPDownLoader stockDownLoader = new JSOUPDownLoader();
		Map<String, String> mapLoginPageCookies = null;
		List<String> reDownLoadStockList = new ArrayList<String>(10);
		List<String> downLoadStockFailureList = new ArrayList<String>(10);
		List<String> downLoadStockOKList = new ArrayList<String>(10);
		List<String> finalFailureList = new ArrayList<String>(10);
		
		int cntCookieUseTime = 0;
		int stockCnt = 0;
			for(String symbol : stockSymbols) {
//				logger.info("Reading yahoo stock data: "+symbol);
//				logger.info("Reading yahoo stock data: "+symbol + ",\tcookie: "+mapLoginPageCookies);
				try {
					if (mapLoginPageCookies== null || cntCookieUseTime>=20) {
//						mapLoginPageCookies = stockDownLoader.getCookie();
						cntCookieUseTime = 0;
					}
					stockCnt ++;
//					stockDownLoader.download(interval, symbol, mapLoginPageCookies);
					stockDownLoader.download(interval, symbol);
					if(stockCnt % 50 == 0)
						log.info("\t"+stockCnt+" of "+stockSymbols.size() +" downloaded.");
					
					cntCookieUseTime++;
					downLoadStockOKList.add(symbol);
				}catch(Exception e){
					log.error(symbol + "\t"+ e.getMessage());
					reDownLoadStockList.add(symbol);
				}
			}	
		
		
		//redownload the first failure stock code
			cntCookieUseTime = 0;
			for(String symbol : reDownLoadStockList) {
				
//				log.info("RE-Reading yahoo stock data: "+symbol);
				try {
					if (mapLoginPageCookies== null || cntCookieUseTime>=20) {
//						mapLoginPageCookies = stockDownLoader.getCookie();
						cntCookieUseTime = 0;
					}
//					stockDownLoader.download(interval, symbol, mapLoginPageCookies);
					stockDownLoader.download(interval, symbol);
					cntCookieUseTime++;
					downLoadStockOKList.add(symbol);
				}catch(Exception e){					
//					log.error("Download Failed: "+symbol+"\t"+ e.getMessage());
					downLoadStockFailureList.add(symbol);
				}
			}
			

			//redownload the 2nd round failure stock code
			cntCookieUseTime = 0;
			for(String symbol : downLoadStockFailureList) {
				
//				log.info("RE-Reading yahoo stock data: "+symbol);
				try {
					if (mapLoginPageCookies== null || cntCookieUseTime>=20) {
//						mapLoginPageCookies = stockDownLoader.getCookie();
						cntCookieUseTime = 0;
					}
//					stockDownLoader.download(interval, symbol, mapLoginPageCookies);
					stockDownLoader.download(interval,symbol);
					cntCookieUseTime++;
					downLoadStockOKList.add(symbol);
				}catch(Exception e){
//					log.error("Download Failed: "+symbol+"\t"+ e.getMessage());
					finalFailureList.add(symbol);
				}
			}
			
			
			
			DownloadResult downloadResult = new DownloadResult();
			downloadResult.setPassCount(stockSymbols.size() - finalFailureList.size());
			downloadResult.setFailureList(finalFailureList);
			downloadResult.setOkList(downLoadStockOKList);
			return downloadResult;
		
	}
	
	
	/*
	public static List<StockBean> readFromYahoo(List<String> stockSymbolsInput) {
		if(stockSymbolsInput!=null && stockSymbolsInput.isEmpty()==false) {
			stockSymbols.clear();
			stockSymbols.addAll(stockSymbolsInput);
		}
		
		
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		from.add(Calendar.YEAR, -30); // from 1 year ago
		 
		Stock yahooStock;
		try {
			
			for(String symbol : stockSymbols) {
				
				logger.info("Reading stock data: "+symbol);
				yahooStock = YahooFinance.get(symbol);
				if(yahooStock==null) {
					logger.warn("Read "+symbol + " Failed. SKIP");
					continue;
				}
				
				List<HistoricalQuote> googleHistQuotes = yahooStock.getHistory(from, to, Interval.DAILY);
				List<StockBean> sbList = historicalQuote2StockBean(googleHistQuotes);
				
				
				String initialTxnDate = sbList.get(0).getTxnDate();
				
				int startYear = Integer.parseInt(initialTxnDate.substring(0, 4));
				if(GlobalConfig.CUSTOM_START_YEAR>startYear)
					startYear = GlobalConfig.CUSTOM_START_YEAR;
					
				List<StockBean> trunkList = StreamTransformHelper.subListByTxnYear(sbList , startYear);
				StockMarketRepo.getTrunk().addAll(trunkList);
				
				
				
				
//				StockMarketRepo.getTrunk().addAll(sbList);
				logger.info("Reading stock data DONE: "+symbol);
				UserCsvWriter.writeToCSV(trunkList, symbol);
				return trunkList;
			}
			
		} catch (IOException e) {
			logger.error(null, e);
		}
		return null;
	}
	
	
	public static List<StockBean> historicalQuote2StockBean(List<HistoricalQuote> ls) {
		List<StockBean> sbList = new ArrayList<StockBean>();
		for (HistoricalQuote hQuote : ls) {
			
			if(hQuote.getOpen()==null)
				continue;
			StockBean sb = new StockBean(hQuote.getSymbol());
//			try {
//			sb.setAdjC(hQuote.getAdjClose().doubleValue());
			sb.setC(hQuote.getClose().doubleValue());
			sb.setH(hQuote.getHigh().doubleValue());
			sb.setL(hQuote.getLow().doubleValue());
			sb.setO(hQuote.getOpen().doubleValue());
			sb.setTxnDate(GeneralHelper.formatCalendar(hQuote.getDate()));
			sb.setTxnDateInt(GeneralHelper.txDateToIntNumber(sb.getTxnDate()));			
//			sb.setVolume(hQuote.getVolume().longValue());
			sb.setVolume(hQuote.getVolume().doubleValue());
			;
			sbList.add(sb);
			
//			}catch(Exception e) {
//				System.out.println("txn date = "+hQuote.getDate());
//				e.printStackTrace();
//			}
		}
		
		return sbList;
	}
*/
}
