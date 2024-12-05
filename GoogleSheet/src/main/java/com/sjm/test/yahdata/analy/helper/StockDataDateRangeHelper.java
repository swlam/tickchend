package com.sjm.test.yahdata.analy.helper;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.maas.util.DateHelper;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;

public class StockDataDateRangeHelper {
	
	private static final int MAX_TRY = 12;
	
	public StockDataDateRangeHelper() {}
	
	public static StockBean findCustomStartDateStockBean(List<StockBean> stockList, String customDateTxt ) {
		
		
		if(stockList==null || stockList.isEmpty())
			return null;
		
		StockBean customStartDateBean = null;
		
		int tryCnt = 1;
		
		boolean isContinue = true;
		do {
			LocalDate stockLastTxDate = null; 
			try {
				stockLastTxDate = LocalDate.parse(stockList.get(stockList.size()-1).getTxnDate());
			}catch(Exception e) {
				System.out.println(e);
			}
			LocalDate customDate = LocalDate.parse(customDateTxt);
			
			if(stockLastTxDate.isBefore(customDate)) {
				return null;
			}
			
			if(tryCnt >= MAX_TRY) {
//				logger.warn(customDateTxt+ ", Max Try "+tryCnt);
				break;
			}
			
			customStartDateBean = StockDataDateRangeHelper.extractSpecificDateData(stockList, customDateTxt);
			
			if(customStartDateBean == null) {
				customDateTxt = DateHelper.getNextDate(customDateTxt);
			}else {
				isContinue = false;
			}
			tryCnt ++;
			
		}while(isContinue);
		
		return customStartDateBean;
	}
	
	// customDate format: YYYY-MM-DD, MM-DD
	public static StockBean extractSpecificDateData(List<StockBean> stockList, final String customDate) {
		StockBean customStartDateBean = null;
		Optional<StockBean> optional = stockList.parallelStream().filter(x -> x.getTxnDate().endsWith(customDate)).findFirst();
		if(optional != null && optional.isPresent()) {
			customStartDateBean = optional.get();			
		}
		return customStartDateBean;
	}
	
}
