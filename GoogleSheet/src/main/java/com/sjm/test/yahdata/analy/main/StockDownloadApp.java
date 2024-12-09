package com.sjm.test.yahdata.analy.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sjm.test.yahdata.analy.bean.StockProfileBean;
import com.sjm.test.yahdata.analy.cfg.CNStockListConfig;
import com.sjm.test.yahdata.analy.cfg.GlobalConfig;
import com.sjm.test.yahdata.analy.cfg.HKStockListConfig;
import com.sjm.test.yahdata.analy.cfg.USStockListConfig;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.repo.BenchmarkProbabilityAnalysisReportDataSourceManager;
import com.sjm.test.yahdata.analy.repo.DownloadResult;
import com.sjm.test.yahdata.analy.repo.YahooDataSource;
import com.sjm.test.yahdata.analy.repo.YahooStockProfileDownloader;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class StockDownloadApp extends BaseApp{
	static List<String> ALL_TICKER = Stream.of(USStockListConfig.ALL, CNStockListConfig.ALL_AVAILABLE, HKStockListConfig.ALL).flatMap(Collection::stream).distinct().collect(Collectors.toList());
//	static List<String> ALL_TICKER = Stream.of(USStockListConfig.CRYPTO, USStockListConfig.ETF, USStockListConfig.ETF_INVERSE, HKStockListConfig.ETF, USStockListConfig.CURRENCY).flatMap(Collection::stream).distinct().collect(Collectors.toList());
	public StockDownloadApp() {}

	/* 2024-09-12
	https://www.reddit.com/r/sheets/comments/1farvxr/broken_yahoo_finance_url/
	https://query2.finance.yahoo.com/v8/finance/chart/TSLA?period1=946684800&period2=1725667200&interval=1d&events=history
	* */

	public static void main(String[] args) {

		
		StockDownloadApp app = new StockDownloadApp();


//		ALL_TICKER = Arrays.asList("NVDA","AAPL","MSFT","GOOG","BABA", "TSLA");
//		ALL_TICKER = Arrays.asList("NVDA");
//		app.downloadStockProfile(ALL_TICKER);

//		app. doMain("CN", Const.INTERVAL_D,CNStockListConfig.ALL_AVAILABLE);
		app. doMain("HK", Const.INTERVAL_D,HKStockListConfig.ALL);
//		app. doMain("US", Const.INTERVAL_D,USStockListConfig.ALL);
//		app. doMain("US", Const.INTERVAL_D,USStockListConfig.ALL);

//		app. doMain("HK", Const.INTERVAL_D,HKStockListConfig.HK_TEST);
//		app. doMain("HK", Const.INTERVAL_D,HKStockListConfig.HSCEI_ELEMENT);
//		app. doMain("HK", Const.INTERVAL_D,HKStockListConfig.HSTECH);

//		app. doMain("HK", Const.INTERVAL_D,HKStockListConfig.STOCK_INDEX);
		
//		app. doMain("US", Const.INTERVAL_D,USStockListConfig.QQQ_COMPONENTS);
//		app. doMain("US", Const.INTERVAL_D,USStockListConfig.CHINA_CONCEPT);
//
//		app. doMain("US", Const.INTERVAL_D,USStockListConfig.DOW_COMPONENTS)
//
//
//		;
//		app. doMain("US", Const.INTERVAL_D,USStockListConfig.SPX_COMPONENTS);
//		app. doMain("US", Const.INTERVAL_D,USStockListConfig.IWM_TOP_20_HOLDING);
//		app. doMain("US", Const.INTERVAL_D,USStockListConfig.ALL_ETF);
//		app. doMain("US", Const.INTERVAL_D,USStockListConfig.magnificent);
//		app. doMain("US", Const.INTERVAL_D,USStockListConfig.INDEX);
//		app. doMain("US", Const.INTERVAL_D,USStockListConfig.CRYPTO);
		
		
//		app. downloadStock("US", Const.INTERVAL_D,USStockListConfig.CRYPTO);
//		app. downloadStock("US", Const.INTERVAL_D,USStockListConfig.CURRENCY);
		
//		app. downloadStock("HK", Const.INTERVAL_W,HKStockListConfig.ALL);
//		app. downloadStock("US", Const.INTERVAL_W,USStockListConfig.ALL);		
//		app. downloadStock("CN", Const.INTERVAL_W,CNStockListConfig.ALL);
//
//		app. downloadStock("HK", Const.INTERVAL_M,HKStockListConfig.ALL);
//		app. downloadStock("US", Const.INTERVAL_M,USStockListConfig.ALL);
//		app. downloadStock("CN", Const.INTERVAL_M,CNStockListConfig.ALL);
		
		
//		////////////////////////////////////////////////////////////////////////
		
//		downloadResultUS = yahooDataSource.readFromYahoo(USStockListConfig.ALL);
//		downloadResultHK = yahooDataSource.readFromYahoo(HKStockListConfig.ALL);				
//		downloadResultCN = yahooDataSource.readFromYahoo(CNStockListConfig.ALL);
		
//		GlobalConfig.DEFAULT_DOWNLOAD_INTEcaRVAL = "W";
//		wdownloadResultHK = yahooDataSource.readFromYahoo(HKStockListConfig.ALL);
//		wdownloadResultUS = yahooDataSource.readFromYahoo(USStockListConfig.ALL);
//		wdownloadResultCN = yahooDataSource.readFromYahoo(CNStockListConfig.ALL);
		
//		GlobalConfig.DEFAULT_DOWNLOAD_INTERVAL = "M";
//		mdownloadResultHK = yahooDataSource.readFromYahoo(HKStockListConfig.ALL);
//		mdownloadResultUS = yahooDataSource.readFromYahoo(USStockListConfig.ALL);
//		mdownloadResultCN = yahooDataSource.readFromYahoo(CNStockListConfig.ALL);
		
//		logger.info("\n Download HK (D): "+downloadResultHK);
//		logger.info("\n Download US (D): "+downloadResultUS);
//		logger.info("\n Download CN (D): "+downloadResultCN);
//		
//		logger.info("\n Download HK (W): "+wdownloadResultHK);
//		logger.info("\n Download US (W): "+wdownloadResultUS);
//		logger.info("\n Download CN (W): "+wdownloadResultCN);
//		
//		logger.info("\n Download HK (M): "+mdownloadResultHK);
//		logger.info("\n Download US (M): "+mdownloadResultUS);
//		logger.info("\n Download CN (M): "+mdownloadResultCN);
		
	}
	
	public void downloadStock(String market, String interval, List<String> stockSymbolsInput) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(()->{
				try {
					YahooDataSource yahooDataSource = new YahooDataSource();
					DownloadResult downloadResultUS = new DownloadResult();
					downloadResultUS = yahooDataSource.readFromYahoo(interval, stockSymbolsInput);
					log.info("\n Download "+market+" ("+interval+"): "+downloadResultUS);
				}catch(Exception e) {
					log.error(null, e);
				}finally {
					executor.shutdown();
				}
				log.info("\n Download "+market+" ("+interval+"): DONE");
			}
		);
	}
	
	
	
	public void downloadStockProfile(List<String> stockSymbolsInput) {
		
		YahooStockProfileDownloader stockProfileDownloader = new YahooStockProfileDownloader();
		
		String exportFileName =  "ticker_data.json";
		String filePath = GlobalConfig.getDefaultStockProfilePath() + exportFileName; 
		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(()->{
				try {
					List<StockProfileBean> resultList = stockProfileDownloader.doDownload(stockSymbolsInput);
					BenchmarkProbabilityAnalysisReportDataSourceManager.saveStockProfileDataToDisk(resultList, filePath);
					
				}catch(Exception e) {
					log.error(null, e);
				}finally {
					executor.shutdown();
				}
				log.info("\n Download stock symbols ("+stockSymbolsInput.size()+"): DONE");
			}
		);
	}
	
	private void doMain(String market, String interval, List<String> stockSymbolsInput) {

		// Split the student list into chunks for each thread

        List<List<String>> symbolChunks = splitListIntoChunks(stockSymbolsInput, 200);


        List<DownloadResult> downloadResutList = new ArrayList<DownloadResult>();
        // Create a thread pool with 10 threads

        ExecutorService executor = Executors.newFixedThreadPool(10);

        log.info("Ready to download "+market+" ("+interval+"), total size = "+stockSymbolsInput.size());

        // Submit each chunk of students to a separate thread

        for (List<String> chunk : symbolChunks) {

            executor.submit(() -> {

//				logger.info(" Downloading "+market+" ("+interval+"), size = "+chunk.size() +" >> "+chunk );

                // Process each student in the chunk

            	YahooDataSource yahooDataSource = new YahooDataSource();
            	
				DownloadResult downloadResultUS = new DownloadResult();
				downloadResultUS = yahooDataSource.readFromYahoo(interval, chunk);
				downloadResutList.add(downloadResultUS);
				
				log.info("\tDownloaded "+market+" ("+interval+"), size = "+chunk.size()+", "+downloadResultUS);
				System.out.println("Downloaded "+market+" ("+interval+"), size = "+chunk.size()+", "+downloadResultUS);
            });

        }

        // Shut down the executor when all tasks are completed
        executor.shutdown();

        try {
            if(!executor.awaitTermination(180, TimeUnit.SECONDS)){
                // 超时的时候向线程池中所有的线程发出中断(interrupted)。
//                executorService.shutdownNow();
            }
//            downloadResutList.stream().forEach(s -> log.info(s.toString()));

        } catch (InterruptedException ignore) {
//            executorService.shutdownNow();
        }
	}

	

    // Split a list into chunks of approximately equal size

    private  <T> List<List<T>> splitListIntoChunks(List<T> list, int chunkSize) {

        List<List<T>> chunks = new ArrayList<>();

        int listSize = list.size();

        int startIndex = 0;

        while (startIndex < listSize) {

            int endIndex = Math.min(startIndex + chunkSize, listSize);

            chunks.add(list.subList(startIndex, endIndex));

            startIndex = endIndex;

        }

        return chunks;

    }
	
	

}


