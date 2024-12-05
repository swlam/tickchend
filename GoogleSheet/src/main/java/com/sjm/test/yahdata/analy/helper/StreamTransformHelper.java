package com.sjm.test.yahdata.analy.helper;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.cfg.GlobalConfig;
import com.sjm.test.yahdata.analy.conts.Const;

public class StreamTransformHelper {

	public StreamTransformHelper() {
	}

	public static List<StockBean> extractMonthData(List<StockBean> list, String txDatePattern) {
		
		List<StockBean> bList = list.parallelStream().filter(x->x.getTxnDate().contains(txDatePattern)).collect(Collectors.toList());
		if(Const.VERBOSE_ENABLE) {
			for (StockBean stockBean : bList) {
				System.out.println(stockBean);	
			}
		}
		return bList;
	}
	
	public static List<StockBean> extractMonthData(List<StockBean> list, String stockCode, String txDatePattern) {
		
		List<StockBean> bList = list.parallelStream().filter(x
				-> x.getTxnDate().contains(txDatePattern) && x.getStockCode().equalsIgnoreCase(stockCode)
				).collect(Collectors.toList());
		return bList;
	}
	
	public static List<StockBean> extractData(List<StockBean> list, String yyyyMMddStart, String yyyyMMddEnd) {
		
		int st = yyyyMMddtoInt(yyyyMMddStart);
		int ed = yyyyMMddtoInt(yyyyMMddEnd);
		
		List<StockBean> bList = list.parallelStream().filter(
					x->  st <= yyyyMMddtoInt(x.getTxnDate()) && yyyyMMddtoInt(x.getTxnDate()) <= ed 
				).collect(Collectors.toList());
//		if(Const.VERBOSE_ENABLE) {
//			for (StockBean stockBean : bList) {
//				System.out.println(stockBean);	
//			}
//		}
		return bList;
	}
	
public static List<StockBean> extractData(List<StockBean> list, String stockCode, String yyyyMMddStart, String yyyyMMddEnd) {
		
		int st = yyyyMMddtoInt(yyyyMMddStart);
		int ed = yyyyMMddtoInt(yyyyMMddEnd);
		
		List<StockBean> bList = list.parallelStream().filter(
					x-> x.getStockCode().equalsIgnoreCase(stockCode) && st <= yyyyMMddtoInt(x.getTxnDate()) && yyyyMMddtoInt(x.getTxnDate()) <= ed 
				).collect(Collectors.toList());

		return bList;
	}

	public static List<StockBean> subList(List<StockBean> list, String yyyyMMddStart, String yyyyMMddEnd) {
		
		int st = yyyyMMddtoInt(yyyyMMddStart);
		int ed = yyyyMMddtoInt(yyyyMMddEnd);
		
		List<StockBean> bList = list.parallelStream().filter(
					x->  st <= yyyyMMddtoInt(x.getTxnDate()) && yyyyMMddtoInt(x.getTxnDate()) < ed 
				).collect(Collectors.toList());

		return bList;
	}
	
	public static List<StockBean> subListWithEndElement(List<StockBean> list, String yyyyMMddStart, String yyyyMMddEnd) {
		
		int st = yyyyMMddtoInt(yyyyMMddStart);
		int ed = yyyyMMddtoInt(yyyyMMddEnd);
		
		List<StockBean> bList = list.parallelStream().filter(
					x->  st <= yyyyMMddtoInt(x.getTxnDate()) && yyyyMMddtoInt(x.getTxnDate()) <= ed 
				).collect(Collectors.toList());

		return bList;
	}
	
	public static List<StockBean> extractMonthData(List<StockBean> list, List<String> txDatePattern) {
		List<StockBean> bList =  new ArrayList<StockBean>();
		
		for (String string : txDatePattern) {
			bList.addAll( extractMonthData(list, string) );
		}
		
		return bList;
	}
	
	public static List<StockBean> trimThisMonth(List<StockBean> stockHistMonthList) {
		LocalDate now = LocalDate.now();
//		int dayOfMonth = now.get(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH);
		int dayOfMonth = now.getDayOfMonth();
		if (dayOfMonth < 23) {
			String nowYyyy_MM = now.getYear() +"-"+ String.format("%02d", now.getMonthValue());
			stockHistMonthList = stockHistMonthList.parallelStream().filter(x -> !x.getTxnDate().contains(nowYyyy_MM)).collect(Collectors.toList());
			
		}
		
		return stockHistMonthList;
	}
	
	


	public static int yyyyMMddtoInt(String yyyyMMdd) {
		int ymd = Integer.parseInt(yyyyMMdd.replaceAll("-", ""));
		return ymd;
	}
	
//This method returns new String object containing the substring of the given string from specified startIndex (inclusive).
	public static List<StockBean> subListByTxnYear(List<StockBean> list, int startYear) {
		 
		 return list.parallelStream().filter(x-> LocalDate.parse(x.getTxnDate()).getYear()>=startYear).collect(Collectors.toList());
	 }
	
	public static List<StockBean> subSingleStockList(List<StockBean> stockList, String fromYYYYmmdd, Integer days) {

		int lastIdx = stockList.size()-1;
		
		List<StockBean> subList = null;
		if(fromYYYYmmdd==null && days !=null) {
			int idx = lastIdx - days;
			subList = stockList.subList(idx, stockList.size());

		}else {
			subList = stockList.parallelStream().filter(x->  x.getTxnDateInt() >= StreamTransformHelper.yyyyMMddtoInt(fromYYYYmmdd)).collect(Collectors.toList());
		}
		
		
		return subList;
	}
	
	public static List<StockBean> trimByCustomStartYear(List<StockBean> fullTrunkList ){
		
		String initialTxnDate = fullTrunkList.get(0).getTxnDate();
		
		int startYear = Integer.parseInt(initialTxnDate.substring(0, 4));
		if(GlobalConfig.getStockDataStartYear() > startYear)
			startYear = GlobalConfig.getStockDataStartYear();
			
		
		List<StockBean> trunkList = StreamTransformHelper.subListByTxnYear(fullTrunkList , startYear);
		return trunkList;
	}
	
	
	public static List<StockBean> extractByStockCode(List<StockBean> list, String stockCode) {
		 
		 return list.parallelStream().filter(x-> x.getStockCode().equalsIgnoreCase(stockCode)).collect(Collectors.toList());
	}
	
public static List<StockBean> extractDataByStockCodeAndPeriod(List<StockBean> list, String stockCode, String yyyyMMddStart, String yyyyMMddEnd) {
		
		int st = yyyyMMddtoInt(yyyyMMddStart);
		int ed = yyyyMMddtoInt(yyyyMMddEnd);
		
		List<StockBean> bList = list.parallelStream().filter(
					x->  x.getStockCode().equalsIgnoreCase(stockCode) && st <= yyyyMMddtoInt(x.getTxnDate()) && yyyyMMddtoInt(x.getTxnDate()) <= ed 
				).collect(Collectors.toList());
//		if(Const.VERBOSE_ENABLE) {
//			for (StockBean stockBean : bList) {
//				System.out.println(stockBean);	
//			}
//		}
		return bList;
	}

	
	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) 
    {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
	
	
	public static int findIndex(List<StockBean> stockList, String yyyy_MM_dd) {
		
		 int index = IntStream.range(0, stockList.size())
	                .filter(i -> stockList.get(i).getTxnDate().equalsIgnoreCase(yyyy_MM_dd))
	                .findFirst()
	                .orElse(-1);
		return index;
	}
	
	public static int findMinIndex(List<StockBean> numbers) { 
	    if (numbers == null || numbers.size() == 0) return -1; // Saves time for empty array 
	     
	    double minVal = numbers.get(0).getL(); // Keeps a running count of the smallest value so far 
	    int minIdx = 0; // Will store the index of minVal 
	    for(int idx=1; idx<numbers.size(); idx++) { 
	        if(numbers.get(idx).getL() < minVal) { 
	            minVal = numbers.get(idx).getL(); 
	            minIdx = idx; 
	        } 
	    } 
	    return minIdx; 
	} 
	public static int findMinRsi9Index(List<StockBean> numbers) { 
	    if (numbers == null || numbers.size() == 0) return -1; // Saves time for empty array 
	     
	    double minVal = numbers.get(0).getRsi9(); // Keeps a running count of the smallest value so far 
	    int minIdx = 0; // Will store the index of minVal 
	    for(int idx=1; idx<numbers.size(); idx++) { 
	        if(numbers.get(idx).getRsi9() < minVal) { 
	            minVal = numbers.get(idx).getRsi9(); 
	            minIdx = idx; 
	        } 
	    } 
	    return minIdx; 
	} 

	public static int findMaxIndex(List<StockBean> arr) {
		if (arr == null || arr.size() == 0) return -1; // Saves time for empty array
		
		double max = arr.get(0).getH();  
	     int maxIdx = 0;  
	     for(int i = 1; i < arr.size(); i++) {  
	          if(arr.get(i).getH() > max) {  
	             max = arr.get(i).getH();  
	             maxIdx = i;  
	          }  
	     }  
	     return maxIdx;  
	}
	
	public static int findMaxRsi9Index(List<StockBean> arr) {
		if (arr == null || arr.size() == 0) return -1; // Saves time for empty array
		
		double max = arr.get(0).getRsi9();  
	     int maxIdx = 0;  
	     for(int i = 1; i < arr.size(); i++) {  
	          if(arr.get(i).getRsi9() > max) {  
	             max = arr.get(i).getRsi9();  
	             maxIdx = i;  
	          }  
	     }  
	     return maxIdx;  
	}
	
	public static StockBean findMaxHighStock(List<StockBean> arr) {
		if (arr == null || arr.size() == 0) return null; // Saves time for empty array
		
		//StockBean maxHStock = stockList.stream().max(Comparator.comparingDouble(StockBean::getH)).orElse(null);
		
		double max = arr.get(0).getH();  
	     int maxIdx = 0;  
	     for(int i = 1; i < arr.size(); i++) {  
	          if(arr.get(i).getH() > max) {  
	             max = arr.get(i).getH();  
	             maxIdx = i;  
	          }  
	     }  
	     StockBean maxBean = arr.get(maxIdx);
	     return maxBean;  
	}
	
	public static StockBean findMinLowStock(List<StockBean> numbers) { 
	    if (numbers == null || numbers.size() == 0) return null; // Saves time for empty array 
	     
	    double minVal = numbers.get(0).getL(); // Keeps a running count of the smallest value so far 
	    int minIdx = 0; // Will store the index of minVal 
	    for(int idx=1; idx<numbers.size(); idx++) { 
	        if(numbers.get(idx).getL() < minVal) { 
	            minVal = numbers.get(idx).getL(); 
	            minIdx = idx; 
	        } 
	    }
	    StockBean minBean = numbers.get(minIdx);
	    return minBean; 
	} 
	
	
	public static int txDaysBetween(List<StockBean> stockList, String yyyy_MM_dd_before, String yyyy_MM_dd_after) {
		int indexBefore = findIndex(stockList, yyyy_MM_dd_before);
		int indexAfter = findIndex(stockList, yyyy_MM_dd_after);
		
		int dayDiff = indexAfter - indexBefore;
		
		if(dayDiff<0)
			return -1;
		
		return dayDiff;
	}
	
	public static StockBean getPeriodHighest(List<StockBean> stockList, int noOfDays) throws Exception {
		if(stockList==null || stockList.size()< noOfDays)
			throw new Exception("Require list size >= "+noOfDays);
		
		List<StockBean>  subList = stockList.subList(stockList.size()- noOfDays, stockList.size());
		
		
		StockBean maxStock = subList
			      .parallelStream()
			      .max(Comparator.comparing(StockBean::getH))
			      .orElseThrow(NoSuchElementException::new);
		
		return maxStock;
	}
	
	public static StockBean getPeriodLowest(List<StockBean> stockList, int noOfDays) throws Exception {
		if(stockList==null || stockList.size()< noOfDays)
			throw new Exception("Require list size >= "+noOfDays);
		
		
		List<StockBean>  subList = stockList.subList(stockList.size()- noOfDays, stockList.size());
		
		StockBean minStock = subList
			      .parallelStream()
			      .min(Comparator.comparing(StockBean::getL))
			      .orElseThrow(NoSuchElementException::new);
		
		return minStock;
	}
	
}
