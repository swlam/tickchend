package com.sjm.test.demo.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sjm.test.yahdata.analy.cfg.HKStockListConfig;
import com.sjm.test.yahdata.analy.cfg.USStockListConfig;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.service.StockDownloadService;

import lombok.extern.slf4j.Slf4j;

@Component
@EnableScheduling
@Slf4j
public class CronJobScheduler {
//	private static final Logger log = LoggerFactory.getLogger(CronJobScheduler.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
//	public static final String CRON_EXPRESSION = "0 0/50 * * * ?";
	public static final String CRON_EXPRESSION_HK = "0 */23 11-16 * * *";
	public static final String CRON_EXPRESSION_US =  "0 */15 18-23 * * *";
	@Autowired
	private StockDownloadService stockDownloadService;
	
    @Scheduled(cron = CRON_EXPRESSION_HK) // Runs every 1 minutes
    public void runCronJobHK() {
//    	log.info("Cron job executed! "+CRON_EXPRESSION);    	
//    	stockDownloadService. downloadStock("HK", Const.INTERVAL_D,HKStockListConfig.ALL);
    	
//    	stockDownloadService. downloadStock("US", Const.INTERVAL_D,USStockListConfig.ALL);
    	log.info("Cron job executed! "+CRON_EXPRESSION_HK);    	
    	
    }
    
    
    @Scheduled(cron = "0 0/29 * * * ?")
    public void runCronJobUSData() {
//    	stockDownloadService. downloadStock("US", Const.INTERVAL_D,USStockListConfig.ALL);
        System.out.println("Cron job executed!");
    }
    

//	@Scheduled(fixedRate = 5000)
//	public void reportCurrentTime() {
//		log.info(" FixedRRate The time is now {}", dateFormat.format(new Date()));
//	}
}