package com.sjm.test.yahdata.analy.bean.raw;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sjm.test.yahdata.analy.main.StockDownloadApp;

import lombok.extern.slf4j.Slf4j;

//import com.sjm.test.yahdata.analy.main.WeeklyAndMonthlyStockGeneratorApp;
@Slf4j
public class StockDataCsvWriter {
    public void writeStocksToCSV(List<StockData> stocks, String filePath) {
    	StockData currStockInfo = null; 
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write the CSV header
        	//Date,Open,High,Low,Close,Adj Close,Volume
            writer.write("Date,Open,High,Low,Close,Adj Close,Volume");
            writer.newLine();
            
            
            // Write each stock entry as a CSV line
            for (StockData stock : stocks) {
            	currStockInfo = stock;
                writer.write(String.format("%s,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f",
//                        stock.getSymbol(),
//                        stock.getDate().toString(),
                        stock.getDayOneDate(),
                        stock.getO(),
                        stock.getH(),
                        stock.getL(),
                        stock.getC(),
                        stock.getC(),
                        stock.getVolume()));
                writer.newLine();
            }
            
            log.debug("Written to CSV file: "+filePath);
            
        } catch (Exception e) {
        	log.error("Error writing stock data("+currStockInfo+") to CSV file: " + e.getMessage());
        }
    }
    
}