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
	

}
