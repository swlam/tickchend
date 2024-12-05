//package com.maas.util;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.apache.log4j.Logger;
//
//import com.maas.app.ShareholdingMainApp;
//import com.maas.ccass.model.MasterStockInfo;
//
//import yahoofinance.Stock;
//
//public class YahooFinanceWorker extends Thread{
//	private static final Logger logger = Logger.getLogger(YahooFinanceWorker.class);
//
//	private Map<String, MasterStockInfo> masterMap;
//	private String ticker;
//	public YahooFinanceWorker(Map<String, MasterStockInfo> masterMap, String ticker) {
//		this.masterMap = masterMap;
//		this.ticker = ticker;
//	}
//
//	@Override
//	public void run() {
//		updateLatestClosePriceAndAmount();
//	}
//	
//	public void updateLatestClosePriceAndAmount() {
//		YahooFinanceHelper helper = new YahooFinanceHelper();
//		Stock stock = helper.getPrice(ticker);
//		
////		double stockPrice = 
//		System.out.println(Thread.currentThread().getName()+ "Getting "+ticker);
//		
//		try {
//			
//			double stockPrice = 100;//stock.getQuote().getPrice().doubleValue();
//			double stockPriceAvg50 = 100;//stock.getQuote().getPriceAvg50().doubleValue();
//			
//			long marketCap = 1000000;//stock.getStats().getMarketCap().longValue();
//			
//			stockPrice = stock.getQuote().getPrice().doubleValue();
//			stockPriceAvg50 = stock.getQuote().getPriceAvg50().doubleValue();
//			
//			marketCap = stock.getStats().getMarketCap().longValue();
//						
//			
//			long shareholding = this.masterMap.get(ticker).getShareholding();
//			long dailyShareholdingDelta = this.masterMap.get(ticker).getLatestDailyShareholdingDelta();
//			
//			this.masterMap.get(ticker).setTotalShareholdingAmount(shareholding * stockPrice);
//			this.masterMap.get(ticker).setLatestDailyShareholdingDeltaAmount(dailyShareholdingDelta * stockPrice);
//			this.masterMap.get(ticker).setMarketCap(marketCap);
//			this.masterMap.get(ticker).setLatestClosePrice(stockPrice);
//			this.masterMap.get(ticker).setClosePriceAvg50(stockPriceAvg50);
//			
//		}catch(Exception e) {
//			logger.error(null, e);
//		}
//		
//		
//	}
//	/*public void updateLatestClosePriceAndAmount() {
//		YahooFinanceHelper helper = new YahooFinanceHelper();
//		
//		
////		double stockPrice = 
//		System.out.println(Thread.currentThread().getName()+ "Getting "+ticker);
//		Stock stock = helper.getPrice(ticker);
//		try {
//			
//			double stockPrice = stock.getQuote().getPrice().doubleValue();
//			double stockPriceAvg50 = stock.getQuote().getPriceAvg50().doubleValue();
//			
//			long marketCap = stock.getStats().getMarketCap().longValue();
//			long shareholding = this.masterMap.get(ticker).getShareholding();
//			long dailyShareholdingDelta = this.masterMap.get(ticker).getLatestDailyShareholdingDelta();
//			
//			this.masterMap.get(ticker).setTotalShareholdingAmount(shareholding * stockPrice);
//			this.masterMap.get(ticker).setLatestDailyShareholdingDeltaAmount(dailyShareholdingDelta * stockPrice);
//			this.masterMap.get(ticker).setMarketCap(marketCap);
//			this.masterMap.get(ticker).setLatestClosePrice(stockPrice);
//			this.masterMap.get(ticker).setClosePriceAvg50(stockPriceAvg50);
//			
//		}catch(Exception e) {
//			logger.error(null, e);
//		}
//		
//		
//	}*/
//
//}
