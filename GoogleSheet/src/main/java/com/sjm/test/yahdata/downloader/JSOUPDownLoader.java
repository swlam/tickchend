package com.sjm.test.yahdata.downloader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

//import org.apache.log4j.Logger;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import com.sjm.test.yahdata.analy.cfg.GlobalConfig;
import com.sjm.test.yahdata.analy.conts.Const;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class JSOUPDownLoader {
//	private static final Logger logger = Logger.getLogger(JSOUPDownLoader.class);
	private static String TREST_TICKER = "0700.HK";//"^IXIC";
	private final static String URL_DOWNLOAD_TEST = "https://query1.finance.yahoo.com/v7/finance/download/"+TREST_TICKER+"?period1=34560000&period2=16512768000&interval=1d&events=history&includeAdjustedClose=true";
	
	private final static String URL_DOWNLOAD_PART_1 = "https://query1.finance.yahoo.com/v7/finance/download/";
	private final static String URL_DOWNLOAD_PART_2 = "?period1=34560000&period2=16512768000&interval=";
	private final static String URL_DOWNLOAD_PART_3 = "&events=history&includeAdjustedClose=true";		
	   //https://query1.finance.yahoo.com/v7/finance/download/QQQ?period1=1621781243&period2=1653317243&interval=1mo&events=history&includeAdjustedClose=true
	   //https://query1.finance.yahoo.com/v7/finance/download/QQQ?period1=1621781243&period2=1653317243&interval=1wk&events=history&includeAdjustedClose=true
	public Map<String, String> getCookie() throws IOException  {
		Response loginPageResponse = 
//                Jsoup.connect("https://finance.yahoo.com/quote/MSFT?p=MSFT&.tsrc=fin-srch")
				Jsoup.connect("https://finance.yahoo.com/")
                .referrer("https://finance.yahoo.com/")
                .userAgent("Firefox/42.0")
                .timeout(2 * 1000)
                .followRedirects(true)
                .execute();
        
//        System.out.println("Fetched login page");
        
        //get the cookies from the response, which we will post to the action URL
        Map<String, String> mapLoginPageCookies = loginPageResponse.cookies();
        return mapLoginPageCookies;
	}
	
	
	    public void downloadXXX(String downloadInterval, String stockTicker, Map<String, String> mapLoginPageCookies)  throws Exception{
//	    	String ticker = URLEncoder.encode(stockTicker, StandardCharsets.UTF_8);
	    	String ticker = URLEncoder.encode(stockTicker, "UTF-8");
	    	String interval ="1d";

	    	if(Const.INTERVAL_W.equalsIgnoreCase(downloadInterval)) {	
	    		interval = "1wk";

	    	}else if(Const.INTERVAL_M.equalsIgnoreCase(downloadInterval)) {
	    		interval = "1mo";
	    	}
	    		
	    	String stockURL = URL_DOWNLOAD_PART_1 + ticker  +URL_DOWNLOAD_PART_2 +interval+URL_DOWNLOAD_PART_3;
	    	
	           
	                try {
	                    
	                	byte[] bytes = Jsoup.connect(stockURL).timeout(2000)
	        	                .header("Accept-Encoding", "gzip, deflate")
	        	                .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
	        	                .referrer(stockURL)
	        	                .ignoreContentType(true)
	        	                .maxBodySize(0)
	        	                .cookies(mapLoginPageCookies)
	        	                .execute()
	        	                .bodyAsBytes();
	                	
	                	
	                	
	                	
	                    String savedFileName = GlobalConfig.getDefaultDownloadPath(downloadInterval) + stockTicker;
	                    if (!savedFileName.endsWith(".csv")) 
	                    	savedFileName = savedFileName.concat(".csv");
	                    FileOutputStream fos = new FileOutputStream(savedFileName);
	                    fos.write(bytes);
	                    fos.close();
	                    
	                    log.debug(savedFileName + " downloaded.");
	                } catch (Exception e) {
//	                	log.error("Could not read the file at '" + stockURL + "'.\t"+ e.getMessage());
	                	throw e;
	                }
	    }

}
