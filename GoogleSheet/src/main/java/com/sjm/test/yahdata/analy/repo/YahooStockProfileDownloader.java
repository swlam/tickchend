package com.sjm.test.yahdata.analy.repo;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.StockProfileBean;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class YahooStockProfileDownloader {

	public YahooStockProfileDownloader() {
	}
	
	public List<StockProfileBean> doDownload(List<String> stockCodeList) {
		List<StockProfileBean> resultList = new ArrayList<StockProfileBean>(100); 
		
		for(int i=0; i<stockCodeList.size(); i++) {
			String code = stockCodeList.get(i);
			StockProfileBean profile = this.retriveProfile(code);
			resultList.add(profile);
		}
		
		return resultList;
	}
	
	
	public StockProfileBean retriveProfile(String stockCode) {
		StockProfileBean rtn = new StockProfileBean();
		rtn.setCode(stockCode);
		
    	String url = "https://hk.finance.yahoo.com/quote/" + stockCode;

		try {
            Document doc = Jsoup.connect(url).timeout(2000).get();
            Element nameElement = doc.selectFirst("h1:contains("+stockCode+")");

            
            Element marketCapElement = doc.selectFirst("td[data-test='MARKET_CAP-value']");
            Element earningsDateElement = doc.selectFirst("td[data-test='EARNINGS_DATE-value']");
            Element peRatioElement = doc.selectFirst("td[data-test='PE_RATIO-value']");
            Element exDivDateElement = doc.selectFirst("td[data-test='EX_DIVIDEND_DATE-value']");

            String nameValue = nameElement.text();
            rtn.setName(GeneralHelper.extractStockName(nameValue));
            if(nameValue.contains("指數")|| nameValue.contains("Index"))
            	rtn.setSector("Index");
            
            if(nameValue.contains("=X)"))
            	rtn.setSector("Currency");
            if(nameValue.contains("Index Fund"))
            	rtn.setSector("ETF");
            
            String marketCapValue = marketCapElement.text();
            rtn.setMarketCap(marketCapValue);
            
           String peRatioValue = peRatioElement.text();
           rtn.setPeRatio(peRatioValue);

           String earningsDateValue = earningsDateElement.text();
           rtn.setEarningsDate(earningsDateValue);
           
           String exDivDateValue = exDivDateElement.text();
           rtn.setExDivDate(exDivDateValue);
               
        } catch (Exception e) {
        	log.warn("Cannot download basic infor ("+stockCode+") from "+url +"\t"+ e.getMessage());
        }
		

		
		//handle Sector
		
		if(rtn.getSector().isEmpty() == false)
			return rtn;
		
		url = "https://hk.finance.yahoo.com/quote/"+stockCode+"/profile";
		
		// for normal stock
		try {
            
            Document doc = Jsoup.connect(url).timeout(2000).get();

            Element sectorElement = doc.selectFirst("span:contains(版塊) + span");
            Element industryElement = doc.selectFirst("span:contains(行業) + span");
            // Extract the sector value
            String sector = sectorElement.text();
            String industry = industryElement.text();
            
            rtn.setSector(sector);
            rtn.setIndustry(industry);
        } catch (Exception e) {
        	log.warn("Cannot download sector/industry ("+stockCode+") from "+url +"\t"+ e.getMessage());
        }
		
		
		if(rtn.getSector().isEmpty() == false)
			return rtn;
		// for ETF
		try {
            
            Document doc = Jsoup.connect(url).timeout(2000).get();

            Element categoryElement = doc.selectFirst("span:contains(類別) + span");
            // Extract the sector value
            String category = categoryElement.text();
            if(category.contains("Leveraged")) // e.g. SOXL : Trading--Leveraged Equity
            	rtn.setSector("ETF-Leveraged");
            else if(category.contains("Inverse")) 	//Trading--Inverse Equity
            	rtn.setSector("ETF-Inverse");
            else {
            	rtn.setSector("ETF");
            	rtn.setIndustry(category);
            }
        } catch (Exception e) {
        	log.warn("Cannot download sector/industry ("+stockCode+") from "+url +"\t"+ e.getMessage());
        }
		
		if(rtn.getSector().isEmpty() == false)
			return rtn;
		// for COIN
		try {
            
            Document doc = Jsoup.connect(url).timeout(2000).get();

            Element sectorElement = doc.selectFirst("span:contains(CoinMarketCap)");
            if(sectorElement!=null)
            	rtn.setSector("Crypto");
        } catch (Exception e) {
        	log.warn("Cannot download sector/industry ("+stockCode+") from "+url +"\t"+ e.getMessage());
        }
		
		return rtn;
	}
	
	
	

	/*
	public String getMarketCap(String stockCode) {
        try {
            // 指定要爬取的網頁 URL
//            String url = "https://finance.yahoo.com/quote/AAPL";
        	String url = "https://hk.finance.yahoo.com/quote/" + stockCode;
            // 使用 JSoup 連接到網頁並獲取 HTML 內容
            Document doc = Jsoup.connect(url).get();

            Element companyNameElement = doc.selectFirst("h1:contains(AAPL)");

            
            Element marketCapElement = doc.selectFirst("td[data-test='MARKET_CAP-value']");
            Element earningsDateElement = doc.selectFirst("td[data-test='EARNINGS_DATE-value']");
            Element peRatioElement = doc.selectFirst("td[data-test='PE_RATIO-value']");
            Element exDivDateElement = doc.selectFirst("td[data-test='EX_DIVIDEND_DATE-value']");

            // 獲取 "Market Cap" 的值
            String marketCapValue = marketCapElement.text();
            // Retrieve the value of "PE Ratio (TTM)"
               String peRatioValue = peRatioElement.text();
               
            // Retrieve the value of "Earnings Date"
               String earningsDateValue = earningsDateElement.text();
               String exDivDateValue = earningsDateElement.text();
            // 輸出 "Market Cap" 的值
            System.out.println("Market Cap: " + marketCapValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
	
	
	public String[] getSectorAndIndustory(String stockCode) {
		String rtn[] = new String[2];
		try {
            // Fetch the HTML content from the URL
            String url = "https://hk.finance.yahoo.com/quote/"+stockCode+"/profile";
            Document doc = Jsoup.connect(url).get();

            Element sectorElement = doc.selectFirst("span:contains(Sector) + span");
            Element industryElement = doc.selectFirst("span:contains(Industry) + span");
            // Extract the sector value
            String sector = sectorElement.text();
            String industry = industryElement.text();
            
            // Print the sector value
            System.out.println("Sector(s): " + sector);
            System.out.println("Industry: " + industry);
            
            rtn[0] = sector;
            rtn[1] = industry;
        } catch (Exception e) {
            e.printStackTrace();
        }
		return rtn;
	}*/
}
