package com.sjm.test.yahdata.analy.main;

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
//	private static final Logger logger = log.getLogger(BaseApp.class);
	
	public static  List<String> CODE_POOL = Stream.of(GlobalConfig.POOL_HK, GlobalConfig.POOL_US, GlobalConfig.POOL_CN).flatMap(Collection::stream) .collect(Collectors.toList());		

	public BaseApp() {}
	
	
	public static List<StockBean> loadStockData(List<String> stockPool, String interval, String startDate, String endDate) {
		List<StockBean> ls = loadStockData( stockPool, interval);
		return StreamTransformHelper.extractData(ls, startDate, endDate);
	}
	
	public static List<StockBean> loadStockData(List<String> stockPool, String interval) {
		List<StockBean> fullTrunkList = new ArrayList<StockBean>();
		log.info("\t Data Interval: "+ interval+ ", path = "+GlobalConfig.getDefaultDownloadPath(interval));
		for (String code : stockPool) {	
			List<StockBean> list = CFGHelper.getStockRawData(GlobalConfig.getDefaultDownloadPath(interval), code);
			if(list==null )
				return new ArrayList<StockBean>(0);
			
			if(!Const.INTERVAL_D.equalsIgnoreCase(interval) && list.size()>5) {						
				boolean isNeedRemoveLastItem = checkSameMonthOrWeek(list, interval);
				if(isNeedRemoveLastItem)
					list.remove(list.size()-1);
				
			}
			
			fullTrunkList.addAll(list);
		}
				
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
