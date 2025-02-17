package com.sjm.test.yahdata.analy.main;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.util.*;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.bean.raw.StockData;
import com.sjm.test.yahdata.analy.bean.raw.StockDataCsvWriter;
import com.sjm.test.yahdata.analy.cfg.CNStockListConfig;
import com.sjm.test.yahdata.analy.cfg.GlobalConfig;
import com.sjm.test.yahdata.analy.cfg.HKStockListConfig;
import com.sjm.test.yahdata.analy.cfg.USStockListConfig;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class WeeklyAndMonthlyStockGeneratorApp extends BaseApp{

	public static void main(String[] args) {
        

		CODE_POOL = USStockListConfig.ALL;
		CODE_POOL = HKStockListConfig.ALL;
//		CODE_POOL = CNStockListConfig.ALL_AVAILABLE;

        WeeklyAndMonthlyStockGeneratorApp app = new WeeklyAndMonthlyStockGeneratorApp();
        List<StockBean> fullTrunkList = loadStockData(CODE_POOL, Const.INTERVAL_D);
        int cnt = 1;
		for (String code : CODE_POOL) {

			try {

				String symbol= code + ".csv";
				List<StockBean> dailyStocks = StreamTransformHelper.extractByStockCode(fullTrunkList, code);
				StockDataCsvWriter csvWriter = new StockDataCsvWriter();
				
//				// Generate the weekly stock data
		        List<StockData> stockDataList = convertDailyToWeekly(symbol, dailyStocks);
		        String path = GlobalConfig.getDefaultDownloadPath(Const.INTERVAL_W);
		        
		        csvWriter.writeStocksToCSV(stockDataList, path + symbol);
				
				// Generate the monthly stock data
		        stockDataList = convertDailyToMonthly(symbol, dailyStocks);
		        path = GlobalConfig.getDefaultDownloadPath(Const.INTERVAL_M);
				csvWriter.writeStocksToCSV(stockDataList, path + symbol);
				
				cnt++;
			}catch(Exception e) {
				e.printStackTrace();
			}
			
		}
		
		log.info("Export "+cnt+" csv files from D to W, Done.");
    }

    public static List<StockData> convertDailyToWeekly(String symbol, List<StockBean> dailyStockList) {
        List<StockData> weeklyStockList = new ArrayList<>();
        Map<String, StockData> weeklyMap = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (StockBean dailyStock : dailyStockList) {
            LocalDate date = LocalDate.parse(dailyStock.getTxnDate(), formatter);
            int weekOfYear = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            int year = date.get(IsoFields.WEEK_BASED_YEAR);

            String weekKey = year + "-" + weekOfYear;

            if (!weeklyMap.containsKey(weekKey)) {
//                tockData weeklyStock = new StockData(symbol, "W", weekStartDateString, weeklyOpenValue, weeklyCloseValue,
//                        weeklyHighValue, weeklyLowValue, weeklyVolume);
                        // 初始化每周的第一条记录作为开盘价
                weeklyMap.put(weekKey, new StockData(
                        dailyStock.getStockCode(), "W",
                        dailyStock.getTxnDate(),
                        dailyStock.getO(),
                        dailyStock.getC(),
                        dailyStock.getH(),
                        dailyStock.getL(),
                        dailyStock.getVolume()
                ));
            } else {
                StockData weeklyStock = weeklyMap.get(weekKey);
                // 更新收盘价
                weeklyStock.setC(dailyStock.getC());
                // 更新最高价
                weeklyStock.setH(Math.max(weeklyStock.getH(), dailyStock.getH()));
                // 更新最低价
                weeklyStock.setL(Math.min(weeklyStock.getL(), dailyStock.getL()));
                // 更新成交量
                weeklyStock.setVolume(weeklyStock.getVolume() + dailyStock.getVolume());
            }
        }

        weeklyStockList.addAll(weeklyMap.values());
        Collections.sort(weeklyStockList, Comparator.comparing(StockData::getDayOneDate));
        return weeklyStockList;
    }

    public static List<StockData> convertDailyToMonthly(String symbol, List<StockBean> dailyStockList) {
        List<StockData> monthlyStockList = new ArrayList<>();
        Map<String, StockData> monthlyMap = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (StockBean dailyStock : dailyStockList) {
            LocalDate date = LocalDate.parse(dailyStock.getTxnDate(), formatter);
            int year = date.getYear();
            int month = date.getMonthValue();

            String monthKey = year + "-" + month;

            if (!monthlyMap.containsKey(monthKey)) {
                // 初始化每月的第一条记录作为开盘价
                monthlyMap.put(monthKey, new StockData(
                        dailyStock.getStockCode(), "M",
                        dailyStock.getTxnDate(),
                        dailyStock.getO(),
                        dailyStock.getC(),
                        dailyStock.getH(),
                        dailyStock.getL(),
                        dailyStock.getVolume()
                ));
            } else {
                StockData monthlyStock = monthlyMap.get(monthKey);
                // 更新收盘价
                monthlyStock.setC(dailyStock.getC());
                // 更新最高价
                monthlyStock.setH(Math.max(monthlyStock.getH(), dailyStock.getH()));
                // 更新最低价
                monthlyStock.setL(Math.min(monthlyStock.getL(), dailyStock.getL()));
                // 更新成交量
                monthlyStock.setVolume(monthlyStock.getVolume() + dailyStock.getVolume());
            }
        }

        monthlyStockList.addAll(monthlyMap.values());
        Collections.sort(monthlyStockList, Comparator.comparing(StockData::getDayOneDate));
        return monthlyStockList;
    }

    @Deprecated
	public static List<StockData> generateWeeklyStocks(String symbol, List<StockBean> dailyStocks) {
        List<StockData> historicalStocks = new ArrayList<>();
        LocalDate weekStartDate = null;
        double weeklyOpenValue = 0;
        double weeklyCloseValue = 0;
        double weeklyHighValue = Double.MIN_VALUE;
        double weeklyLowValue = Double.MAX_VALUE;
        double weeklyVolume = 0;

        // Define the formatter for the date string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (StockBean dailyStock : dailyStocks) {
        	
            LocalDate currentDate = LocalDate.parse(dailyStock.getTxnDate(), formatter);//getTxDate();
            double currentOpenValue = dailyStock.getO();//.getOpenValue();
            double currentCloseValue = dailyStock.getC();//.getCloseValue();
            double currentHighValue = dailyStock.getH();//.getHighValue();
            double currentLowValue = dailyStock.getL();//).getLowValue();
            double currentVolume = dailyStock.getVolume();//;

            
            // Check if a new week has started
            if(weekStartDate==null || weekStartDate.get(ChronoField.ALIGNED_WEEK_OF_YEAR)!=currentDate.get(ChronoField.ALIGNED_WEEK_OF_YEAR)) {
//            if (currentDate.getDayOfWeek() == DayOfWeek.MONDAY) {
                // Add the previous week's data to the list
                if (weekStartDate != null) {
                	String weekStartDateString = weekStartDate.format(formatter);
                	StockData weeklyStock = new StockData(symbol, "W", weekStartDateString, weeklyOpenValue, weeklyCloseValue,
                            weeklyHighValue, weeklyLowValue, weeklyVolume);
                    historicalStocks.add(weeklyStock);
                }

                // Initialize values for the new week
                weekStartDate = currentDate;
                weeklyOpenValue = currentOpenValue;
                weeklyCloseValue = currentCloseValue;
                weeklyHighValue = currentHighValue;
                weeklyLowValue = currentLowValue;
                weeklyVolume = currentVolume;
            } else {
                // Update values for the current week
                weeklyCloseValue = currentCloseValue;
                weeklyHighValue = Math.max(weeklyHighValue, currentHighValue);
                weeklyLowValue = Math.min(weeklyLowValue, currentLowValue);
                weeklyVolume += currentVolume;
            }
        }

        // Add the last week's data to the list
        if (weekStartDate != null) {
        	String weekStartDateString = weekStartDate.format(formatter);
        	StockData weeklyStock = new StockData(symbol, "W", weekStartDateString, weeklyOpenValue, weeklyCloseValue,
                    weeklyHighValue, weeklyLowValue, weeklyVolume);
            historicalStocks.add(weeklyStock);
        }

        return historicalStocks;
    }


    @Deprecated
	public static List<StockData> generateMonthlyStocks(String symbol, List<StockBean> dailyStocks) {
        List<StockData> historicalStocks = new ArrayList<>();
        LocalDate monthlyStartDate = null;
        
        double mOpenValue = 0;
        double mCloseValue = 0;
        double mHighValue = Double.MIN_VALUE;
        double mLowValue = Double.MAX_VALUE;
        double mVolume = 0;

        // Define the formatter for the date string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (StockBean dailyStock : dailyStocks) {
        	
            LocalDate currentDate = LocalDate.parse(dailyStock.getTxnDate(), formatter);//getTxDate();
            double currentOpenValue = dailyStock.getO();//.getOpenValue();
            double currentCloseValue = dailyStock.getC();//.getCloseValue();
            double currentHighValue = dailyStock.getH();//.getHighValue();
            double currentLowValue = dailyStock.getL();//).getLowValue();
            double currentVolume = dailyStock.getVolume();//;

            
            if(monthlyStartDate==null || monthlyStartDate.getMonth()!=currentDate.getMonth()) {
            
            // Check if a new week has started
//            if (currentDate.getDayOfMonth() == 1) {
                // Add the previous week's data to the list
                if (monthlyStartDate != null) {
                	String monthlyStartDateString = monthlyStartDate.format(formatter);
                	StockData mStock = new StockData(symbol, "M", monthlyStartDateString, mOpenValue, mCloseValue,
                            mHighValue, mLowValue, mVolume);
                    historicalStocks.add(mStock);
                }

                // Initialize values for the new month
                monthlyStartDate = currentDate;
                mOpenValue = currentOpenValue;
                mCloseValue = currentCloseValue;
                mHighValue = currentHighValue;
                mLowValue = currentLowValue;
                mVolume = currentVolume;
            } else {
                // Update values for the current month
                mCloseValue = currentCloseValue;
                mHighValue = Math.max(mHighValue, currentHighValue);
                mLowValue = Math.min(mLowValue, currentLowValue);
                mVolume += currentVolume;
            }
        }

        // Add the last week's data to the list
        if (monthlyStartDate != null) {
        	String mStartDateString = monthlyStartDate.format(formatter);
        	StockData mStock = new StockData(symbol, "M", mStartDateString, mOpenValue, mCloseValue,
                    mHighValue, mLowValue, mVolume);
            historicalStocks.add(mStock);
        }

        return historicalStocks;
    }
}
