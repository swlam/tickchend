package com.sjm.test.yahdata.service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

import com.sjm.test.yahdata.analy.repo.DownloadResult;
import com.sjm.test.yahdata.analy.repo.YahooDataSource;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class StockDownloadService {

	

	public void downloadStock(String market, String interval, List<String> stockSymbolsInput) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(()->{
				try {
					YahooDataSource yahooDataSource = new YahooDataSource();
					DownloadResult downloadResultUS = new DownloadResult();
					downloadResultUS = yahooDataSource.readFromYahoo(interval, stockSymbolsInput);
					log.info("\n Download "+market+" ("+interval+"): "+downloadResultUS);
				}catch(Exception e) {
					log.error(null,e);
				}finally {
					executor.shutdown();
				}
				log.info("\n Download "+market+" ("+interval+"): DONE");
			}
		);
	}
}
