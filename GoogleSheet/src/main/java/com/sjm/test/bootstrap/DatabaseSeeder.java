package com.sjm.test.bootstrap;


import com.sjm.test.repositories.StockRepository;
import com.sjm.test.repositories.StockTargetListRepository;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.cfg.GlobalConfig;
import com.sjm.test.yahdata.analy.cfg.USStockListConfig;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.helper.CFGHelper;


import java.util.*;
import java.util.stream.Collectors;

import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.model.InstantPerformanceResult;
import com.sjm.test.yahdata.analy.module.benchmarks.BenchmarksPrograms;
import com.sjm.test.yahdata.service.LiteWatchListRecentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements ApplicationListener<ContextRefreshedEvent> {
  private static String DEFAULT_INTERVAL = Const.INTERVAL_D;
  @Autowired
  private StockRepository stockRepository;
  @Autowired
  private LiteWatchListRecentService liteWatchListRecentService;

  @Autowired
  private StockTargetListRepository stockTargetListRepository;
//  private final Faker faker;

  public static final String START_DATE = "2016-01-01";
  public static String END_DATE = "2028-12-03";
  //public static  List<String> CODE_POOL = Arrays.asList("TSLA","QQQ","DIA","SPY");//USStockListConfig.ETF;
  public static  List<String> CODE_POOL = USStockListConfig.ALL;
//  public DatabaseSeeder(StockRepository stockRepository) {
//    this.stockRepository = stockRepository;
//    this.faker = new Faker(Locale.ENGLISH);
//  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
//    stockRepository.deleteAll();
    stockTargetListRepository.deleteAll();
    this.importStockAnalysResult();
  }


  public void loadStockData(){
    CFGHelper.loadMetaInfo(GlobalConfig.DEFAULT_DEFAULT_PATH);
    List<StockBean> fullTrunkList = liteWatchListRecentService.loadStockData(CODE_POOL, DEFAULT_INTERVAL);
    for (int i = 0; i <fullTrunkList.size() ; i++) {
      StockBean sk = fullTrunkList.get(i);
      this.stockRepository.save(sk);
    }
  }

  private void importStockAnalysResult() {
    CFGHelper.loadMetaInfo(GlobalConfig.DEFAULT_DEFAULT_PATH);
    List<StockBean> fullTrunkList = liteWatchListRecentService.loadStockData(CODE_POOL, DEFAULT_INTERVAL);
    List<StockBean> stockPeriodList = StreamTransformHelper.extractData(fullTrunkList, START_DATE, END_DATE);

    String ICONIC_CODE = "SPY";
    List<StockBean> spyStockList = StreamTransformHelper.extractDataByStockCodeAndPeriod(fullTrunkList, ICONIC_CODE, START_DATE, END_DATE);

    StockBean lastBean = spyStockList.get(spyStockList.size()-1);

    BenchmarksPrograms bchkProgram = new BenchmarksPrograms();
    List<InstantPerformanceResult> instantPerformanceResultList = bchkProgram.doBenchmarks(CODE_POOL, stockPeriodList, DEFAULT_INTERVAL, lastBean.getTxnDateInt());
//    List<InstantPerformanceResult> results = instantPerformanceResultList.stream()
//            .filter(x-> x.getCurrentStockBean().getTxnDateInt() == lastBean.getTxnDateInt()).toList();

    List<InstantPerformanceResult> results = instantPerformanceResultList.stream()
            .filter(x -> x.getCurrentStockBean().getTxnDateInt() == lastBean.getTxnDateInt())
            .collect(Collectors.toList()); // 使用 Collectors.toList()
      stockTargetListRepository.saveAll(results);
      System.out.println("EXPORT to MongoDB");

  }
}



