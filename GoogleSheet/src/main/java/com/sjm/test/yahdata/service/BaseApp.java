package com.sjm.test.yahdata.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.maas.util.DateHelper;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.cfg.GlobalConfig;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.helper.CFGHelper;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class BaseApp {
	
	public static  List<String> CODE_POOL = Stream.of(GlobalConfig.POOL_HK, GlobalConfig.POOL_US, GlobalConfig.POOL_CN).flatMap(Collection::stream) .collect(Collectors.toList());		

	public BaseApp() {}
	
	
	public static List<StockBean> loadStockData(List<String> stockPool, String interval, String startDate, String endDate) {
		List<StockBean> ls = loadStockData( stockPool, interval);
		return StreamTransformHelper.extractData(ls, startDate, endDate);
	}
	
	public static List<StockBean> loadStockData(List<String> stockPool, String interval) {
		List<StockBean> fullTrunkList = new ArrayList<StockBean>();
		log.info("\t Data Interval: "+ interval+", original ticker size:"+stockPool.size()+ ", path = "+GlobalConfig.getDefaultDownloadPath(interval));
		int cnt = 0;
		for (String code : stockPool) {
//			log.info("Start to load Data: "+code);
			List<StockBean> list = CFGHelper.getStockRawData(GlobalConfig.getDefaultDownloadPath(interval), code);
			if(list==null ) {
				log.warn("Cannot load Data: "+code);
				return new ArrayList<StockBean>(0);
			}
			cnt++;
			if(!Const.INTERVAL_D.equalsIgnoreCase(interval) && list.size()>5) {						
				boolean isNeedRemoveLastItem = checkSameMonthOrWeek(list, interval);
				if(isNeedRemoveLastItem)
					list.remove(list.size()-1);
				
			}

			List<StockBean> sortedStocks  = list.stream().sorted((s1, s2) -> s1.getTxnDateInt()- s2.getTxnDateInt()).toList();

			fullTrunkList.addAll(sortedStocks);
		}
		log.info("\t Loaded All Data from CSV files. ticker size: "+ cnt);

				
		return fullTrunkList;
	}
	
	public static boolean checkSameMonthOrWeek(List<StockBean> list, String interval) {
		StockBean last = list.get(list.size()-1);
		StockBean last_1 = list.get(list.size()-2);
		
		
		if(Const.INTERVAL_W.equalsIgnoreCase(interval)) {
			return DateHelper.isInSameWeek(LocalDate.parse(last.getTxnDate()), LocalDate.parse(last_1.getTxnDate()));
		}
		
		if(Const.INTERVAL_M.equalsIgnoreCase(interval)) {
			return DateHelper.isInSameMonth(LocalDate.parse(last.getTxnDate()), LocalDate.parse(last_1.getTxnDate()));

		}
		
		return false;
	}
	

}
