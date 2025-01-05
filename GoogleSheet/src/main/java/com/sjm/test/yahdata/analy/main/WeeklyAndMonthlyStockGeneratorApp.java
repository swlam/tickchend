package com.sjm.test.yahdata.analy.main;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

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
//		CODE_POOL = HKStockListConfig.ALL;
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
		        List<StockData> stockDataList = generateWeeklyStocks(symbol, dailyStocks);
		        String path = GlobalConfig.getDefaultDownloadPath(Const.INTERVAL_W);
		        
		        csvWriter.writeStocksToCSV(stockDataList, path + symbol);
				
				// Generate the monthly stock data
		        stockDataList = generateMonthlyStocks(symbol, dailyStocks);
		        path = GlobalConfig.getDefaultDownloadPath(Const.INTERVAL_M);
				csvWriter.writeStocksToCSV(stockDataList, path + symbol);
				
				cnt++;
			}catch(Exception e) {
				e.printStackTrace();
			}
			
		}
		
		log.info("Export "+cnt+" csv files from D to W, Done.");
    }
	
	
	
	
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
