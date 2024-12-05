//package com.maas.util;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//
//import org.jsoup.Jsoup;
//import org.jsoup.Connection.Method;
//import org.jsoup.Connection.Response;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import com.maas.ccass.conts.Conts;
//import com.maas.ccass.model.SouthboundShareholding;
//
//import yahoofinance.Stock;
//import yahoofinance.YahooFinance;
//
//public class YahooFinanceHelper {
////https://financequotes-api.com/
//	
//	public static String URL_PREFIX= "http://www.etnet.com.hk/www/tc/stocks/realtime/quote.php?code=0";
////	https://hk.finance.yahoo.com/quote/1810.HK?p=1810.HK&.tsrc=fin-srch
//		
//		
//	public static String URL_SUFFIX= "";
//	
//	
//	public static String selector= "#StkDetailMainBox > table > tbody > tr:nth-child(1) > td.styleA > span.Price.down2";
//	
//
//	public static String selectorMarketCap = "#StkDetailMainBox > table > tbody > tr:nth-child(1) > td:nth-child(6) > span";
//	
//	public  Stock getPrice(String ticker) {
//		try {
//		Stock stock = YahooFinance.get(ticker+".hk"); //3690.hk
////		BigDecimal price = stock.getQuote().getPrice();
////		return price.doubleValue();
//		return stock;
//		}catch(Exception e) {
//			return null;
//		}
//	    
//	  }
//	
//	public static void main(String arg[]) {
////		YahooFinanceHelper helper = new YahooFinanceHelper();
////		System.out.println(helper.getPrice("3690"));
//		
//		
//		Stock stock;
//		try {
//			stock = YahooFinance.get("3690.hk");
////			BigDecimal price = stock.getQuote().getPrice();
////			
//			BigDecimal change = stock.getQuote().getChangeInPercent();
//			BigDecimal peg = stock.getStats().getPeg();
//			BigDecimal dividend = stock.getDividend().getAnnualYieldPercent();
//			stock.getStats().getMarketCap();
////			stock.print();
//			
//			
//			
////			Stock tesla = YahooFinance.get("TSLA", true);
//			System.out.println(stock.getHistory());
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		 
//		
//	}
//	 
//}
