package com.sjm.test.yahdata.analy.helper;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.maas.util.DateHelper;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.probability.HistMonthlyPerformanceDataContainer;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class VSToolHelper {

	private static final int maxTry = 8;
	
	public VSToolHelper() {}
		
	public void testExport12MonthProbability(String stockCode, String initTxnDate, List<String> month) {

		//get init yyyy-mm
		LocalDate initDate = LocalDate.parse(initTxnDate);
		int yearIdxStart = initDate.getYear();
		int initYyyyMM = Integer.parseInt(yearIdxStart + String.format("%02d", initDate.getMonthValue()));

		// calc end yyyy-mm
		LocalDate now = LocalDate.now();
		int dayOfMonth = now.get(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH);
		int nowYyyyMM = 0;
		if (dayOfMonth > 23) {
			nowYyyyMM = Integer.parseInt(now.getYear() + String.format("%02d", now.getMonthValue()));
		} else {
			LocalDate prevMonth = now.plusMonths(-1);
			nowYyyyMM = Integer.parseInt(prevMonth.getYear() + String.format("%02d", prevMonth.getMonthValue()));
		}

		System.out.println("yearIdxStart= " + yearIdxStart + ", initDate.getMonthValue()=" + initDate.getMonthValue());

		HistMonthlyPerformanceDataContainer histMonthPerformanceContainer = new HistMonthlyPerformanceDataContainer();

		//start loop logic
		for (int y = yearIdxStart; y <= now.getYear(); y++) {
//			System.out.println();

			List<StockBean> ymList = new ArrayList<StockBean>(10);
//			List<MonthlyPerformance> histMonthlyPerformanceList = new ArrayList<MonthlyPerformance>(10);

			for (int m = 1; m <= 12; m++) {

				String curMM = String.format("%02d", m);
				int curYyyyMM = Integer.parseInt(y + curMM);
				String txDatePattern = y + "-" + String.format("%02d", m);// example: 2020-04

				if (!(initYyyyMM <= curYyyyMM && curYyyyMM <= nowYyyyMM)) {
					continue;
				} else {
					//core logic here
					System.out.println(" " + curYyyyMM);


				}

			}
		}
	}
	
	
	
	public static List<String> getMonthNumber(int startPos, int endPos) {

		List<String> rtn = new ArrayList<String>();
		for(int i=startPos; i<= endPos; i++) {
			rtn.add(String.format("%02d", i));
		}
		
		return rtn;
		
	}
	
	
	public static StockBean findCustomStartDateStockBean(List<StockBean> stockHistMonthList, String customDate ) {
		StockBean customStartDateBean = null;
		
		int tryCnt = 1;
		
		boolean isContinue = true;
		do {
			
			if(tryCnt >= maxTry) {
				System.out.println(customDate+ ", Max Try "+tryCnt);
				break;
			}
				
			customStartDateBean = extractSpecificDateData(stockHistMonthList, customDate);
			
			if(customStartDateBean == null) {
				customDate = DateHelper.getNextDate(customDate);
			}else {
				isContinue = false;
			}
			tryCnt ++;
			
		}while(isContinue);
		
		return customStartDateBean;
	}
	
	
	
	
	// customDate format: YYYY-MM-DD, MM-DD
	public static StockBean extractSpecificDateData(List<StockBean> stockHistMonthList, final String customDate) {
		StockBean customStartDateBean = null;
		Optional<StockBean> optional = stockHistMonthList.parallelStream().filter(x -> x.getTxnDate().endsWith(customDate)).findFirst();
		if(optional != null && optional.isPresent()) {
			customStartDateBean = optional.get();			
		}
		return customStartDateBean;
	}
	
	
	public static void main(String args[]) {
		System.out.println(Integer.parseInt("-02-".replaceAll("-", "")));
	}
	
	

}
