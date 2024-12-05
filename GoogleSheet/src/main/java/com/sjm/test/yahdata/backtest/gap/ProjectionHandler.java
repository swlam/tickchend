package com.sjm.test.yahdata.backtest.gap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.VolumePriceBean;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProjectionHandler {
	
	public ProjectionHandler() { }

	public List<DayBenchmark> handle( List<StockBean> trunkList, List<VolumePriceBean> vpStockList) {

		//key is the orderOfDays, value is this StockList of this rule event 
		Map<Integer, List<StockBean>> vpVerticalMap = new HashMap<Integer, List<StockBean>>();
		
		//key is the first date when the event triggered
		Map<String, List<StockBean>> vpHorizontalMap = new LinkedHashMap<String, List<StockBean>>(); 
		/////////////////

		for (VolumePriceBean vpinfo : vpStockList) {
			int idx = StreamTransformHelper.findIndex(trunkList, vpinfo.getTxnDate());
			
			if(trunkList.size()< (idx+10))
				break;
			
			List<StockBean> subStockList = trunkList.subList(idx, idx +11);
			
			vpHorizontalMap.put(vpinfo.getTxnDate(), subStockList);
			
				for( int i=1; i<subStockList.size(); i++) {
					StockBean sb = subStockList.get(i);
					
					if(vpVerticalMap.get(i) == null) {
						vpVerticalMap.put(i, new ArrayList<StockBean>());
					}else {
						vpVerticalMap.get(i).add(sb);
					}
				}
			
			}
		/////
		List<DayBenchmark> resultList = new ArrayList<DayBenchmark>();
		for (Map.Entry<Integer, List<StockBean>> entry : vpVerticalMap.entrySet()) {
			Integer keyOrderOfDays = entry.getKey();
			List<StockBean> orderOfDaysStockList = entry.getValue();
			
			DayBenchmark dbenmk = this.calcDayList(keyOrderOfDays, orderOfDaysStockList);
			resultList.add(dbenmk);
		}
		
		

		
		//print raw data
		String msg =  "\n\tDay 0\tUP\tPct\tDay 1\tUP\tPct\tDay 2\tUP\tPct\tDay 3\tUP\tPct\tDay 4\tUP\tPct\tDay 5\tUP\tPct\tDay 6\tUP\tPct\tDay 7\tUP\tPct\tDay 8\tUP\tPct\tDay 9\tUP\tPct\tDay 10\tUP\tPct\n";
		for (Map.Entry<String, List<StockBean>> entry : vpHorizontalMap.entrySet()) {
//			String firstDate = entry.getKey();
			List<StockBean> stokList = entry.getValue();
			for(StockBean stock: stokList) {
				msg += "\t"+stock.getTxnDate()+"\t"+(stock.isRiseToday()?"Y":"N")+"\t"+GeneralHelper.toPct(stock.getDayChgPct());
			}
			msg +="\n";
		}
		log.info(msg);
		
		return resultList;
	}
	
	
	public DayBenchmark calcDayList(Integer dayNum, List<StockBean> aDayList) {
		long cntUp = aDayList.stream().filter(x -> x.isRiseToday() == true).count();
//		long cntDown = aDayList.stream().filter(x -> x.isUp() == false).count();
		Double avgOfUpDay = aDayList.stream().filter(x->x.isRiseToday() == true)
				.mapToDouble(StockBean::getDayChgPct).average().orElse(Double.NaN);
		Double avgOfDownDay = aDayList.stream().filter(x->x.isRiseToday() == false)
				.mapToDouble(StockBean::getDayChgPct).average().orElse(Double.NaN);
		
		double upRatio = (double) cntUp / (double) aDayList.size();
		
//		double upRatio = (double) cntUp / (double) aDayList.size();
		
		DayBenchmark dbmk = new DayBenchmark(dayNum, upRatio, avgOfUpDay, avgOfDownDay);
		return dbmk;
	}
	
	
	
}
