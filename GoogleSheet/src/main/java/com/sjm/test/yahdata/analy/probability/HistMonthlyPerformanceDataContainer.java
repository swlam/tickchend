package com.sjm.test.yahdata.analy.probability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sjm.test.yahdata.analy.probability.bean.MonthlyPerformance;

public class HistMonthlyPerformanceDataContainer {

	Map<String, List<MonthlyPerformance>> histMonthPerformanceMap = new HashMap<String, List<MonthlyPerformance>>();
	
	public HistMonthlyPerformanceDataContainer() {
		this.histMonthPerformanceMap = new HashMap<String, List<MonthlyPerformance>>(12);
	}
	
	
	public void put(String mm, MonthlyPerformance mPerformance) {
		if( this.histMonthPerformanceMap.get(mm)==null || this.histMonthPerformanceMap.get(mm).isEmpty()) {
			this.histMonthPerformanceMap.put(mm, new ArrayList<MonthlyPerformance>(10));	
		}
		
		this.histMonthPerformanceMap.get(mm).add(mPerformance);
	}

	public Map<String, List<MonthlyPerformance>> getMap(){
		return this.histMonthPerformanceMap;
	}
	public List<MonthlyPerformance> getValue(String month){
		return this.histMonthPerformanceMap.get(month);
	}
}
