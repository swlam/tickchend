package com.sjm.test.yahdata.analy.ta.indicator.macd;

import java.util.ArrayList;
import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;

public class MACDDataCaculator {

	public MACDDataCaculator() {
		// TODO Auto-generated constructor stub
	}

	
	public MACDData calculateIndicators(List<StockBean> stockList) {
        // Calculate MACD, EMA, and divergence for each stock transaction date
		double latestMacd = 0.0;
		double latestEma12 = 0.0;
		double latestDivergence = 0.0;
		double latestSignalLine = 0.0;
		String txnDate = "";
        for (StockBean stock : stockList) {
            double ema12 = calculateEMA(stockList, stock.getTxnDate(), 12);
            double ema26 = calculateEMA(stockList, stock.getTxnDate(), 26);
            double macd = ema12 - ema26;//DIF
            double signalLine = calculateEMA(stockList, stock.getTxnDate(), 9);
            double divergence = macd - signalLine;
            
            txnDate = stock.getTxnDate();
            latestMacd = macd;
            latestEma12 = ema12;
            latestDivergence = divergence;
            latestSignalLine = signalLine;
        }
        /*
        System.out.println("Date: " + txnDate);
        System.out.println("MACD(DIF): " + latestMacd);
        System.out.println("EMA: " + latestEma12);
        System.out.println("Divergence: " + latestDivergence);
        System.out.println("SignalLine: " + latestSignalLine);
        System.out.println("----------------------");*/
        MACDData macd = new MACDData(latestMacd, latestSignalLine, latestDivergence);
        return macd;
    }
    
    public double calculateEMA(List<StockBean> stockList, String txnDate, int period) {
    	double ema = 0.0;
        boolean found = false;
        
        // Find the stock with the given txnDate
        for (StockBean stock : stockList) {
            if (stock.getTxnDate().equals(txnDate)) {
                ema = stock.getC();
                found = true;
                break;
            }
        }
        
        // Calculate EMA using the previous EMA and current close price
        if (found) {
            double multiplier = 2.0 / (period + 1);
            for (int i = stockList.indexOf(stockList.get(0)); i < stockList.size(); i++) {
                StockBean stock = stockList.get(i);
                ema = (stock.getC() - ema) * multiplier + ema;
            }
        }
        
        return ema;
    }
    
    // referene: https://support.futunn.com/hant/topic165/
    @Deprecated
    public static void calculateMACD(List<StockBean> stockList) {
        int ema1Period = 12; // EMA1 parameter (12-day)
        int ema2Period = 26; // EMA2 parameter (26-day)
        int difPeriod = 9; // DIF parameter (9-day)
        
        double ema1 = 0.0;
        double ema2 = 0.0;
        double dif = 0.0;
        double dea = 0.0;
        double macd = 0.0;
        String txnDate = "";
        for (int i = 0; i < stockList.size(); i++) {
            StockBean stock = stockList.get(i);
            
            // Calculate Average Index (DI)
            double averageIndex = (stock.getH() + stock.getL() + (2 * stock.getC())) / 4.0;
            
            // Calculate EMA1 (12-day EMA)
            if (i == 0) {
                ema1 = averageIndex;
            } else {
                ema1 = ema1 + (2.0 / (1 + ema1Period)) * (averageIndex - ema1);
            }
            
            // Calculate EMA2 (26-day EMA)
            if (i == 0) {
                ema2 = averageIndex;
            } else {
                ema2 = ema2 + (2.0 / (1 + ema2Period)) * (averageIndex - ema2);
            }
            
            // Calculate DIF (Difference)
            dif = ema1 - ema2;
            
            // Calculate DEA (9-day EMA of DIF)
            if (i == 0) {
                dea = dif;
            } else {
                dea = dea + (2.0 / (1 + difPeriod)) * (dif - dea);
            }
            
            macd = dif - dea;
            
            txnDate = stock.getTxnDate();
        }
        
        System.out.println("Date: " + txnDate);
        System.out.println("MACD: " + macd);
        System.out.println("EMA1: " + ema1);
        System.out.println("EMA2: " + ema2);
        System.out.println("DIF: " + dif);
        System.out.println("----------------------");
    }
	
}
