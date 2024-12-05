package com.sjm.test.yahdata.analy.bollinger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
public class BollingerBandCalculator {
    
    public static void main(String[] args) {
        
        List<Double> prices = new ArrayList<>();
        prices.add(172.57);
        prices.add(172.07);
        prices.add(172.07);
        prices.add(172.69);
        prices.add(175.05);
        prices.add(175.160);
        prices.add(174.197);
        prices.add(171.559);
        prices.add(171.839);
        prices.add(172.990);
        prices.add(175.429);
        prices.add(177.30);
        prices.add(177.25);
        prices.add(180.08996);
        prices.add(180.94997);
        prices.add(179.58);
        prices.add(179.21);
        prices.add(177.82);
        prices.add(180.57);
        prices.add(180.96);
        // add more numbers as needed
        
        double[] bollingerBands = calculateBollingerBands(prices, 20, 2); // 20 is the period and 2 is the standard deviation multiplier
        
        double upperBand = bollingerBands[0];
        double middleBand = bollingerBands[1];
        double lowerBand = bollingerBands[2];
        
        System.out.println("Upper Bollinger Band: " + upperBand);
        System.out.println("Middle Bollinger Band: " + middleBand);
        System.out.println("Lower Bollinger Band: " + lowerBand);
    }
    
    
    public static List<BollingerBand> func(List<StockBean> stockList) {
    	//if size <20, then return null;
    	int len = 20;
    	
    	List<BollingerBand> rtn = new ArrayList<BollingerBand>(50);
    	
    	for(int idx=20; idx<=stockList.size(); idx++) {
    		
    		List<StockBean>  subList= stockList.subList(idx-len, idx);
    		StockBean subListLast = subList.get(subList.size()-1);
    		List<Double> closePriceList = subList.stream().map(StockBean::getC).collect(Collectors.toList());
            double[] bollingerBands = calculateBollingerBands(closePriceList, 20, 2); // 20 is the period and 2 is the standard deviation multiplier
            
            BollingerBand bb = new BollingerBand(subListLast.getTxnDate(), bollingerBands[0], bollingerBands[1], bollingerBands[2]);
            rtn.add(bb);
    	}
    	
    	return rtn;
    	
    }
    
    
    public static double[] calculateBollingerBands(List<Double> prices, int period, double stdDevMultiplier) {
        double[] bollingerBands = new double[3];
        
        double[] sma = calculateSMA(prices, period);
        double[] stdDev = calculateStdDev(prices, period);
        
        double upperBand = sma[period-1] + stdDevMultiplier * stdDev[period-1];
        double middleBand = sma[period-1];
        double lowerBand = sma[period-1] - stdDevMultiplier * stdDev[period-1];
        
        bollingerBands[0] = upperBand;
        bollingerBands[1] = middleBand;
        bollingerBands[2] = lowerBand;
        
        return bollingerBands;
    }
    
    public static double[] calculateSMA(List<Double> prices, int period) {
        double[] sma = new double[prices.size()];
        double sum = 0;
        
        int i;
        for (i = 0; i < period-1; i++) {
            sum += prices.get(i);
            sma[i] = Double.NaN;
        }
        
        for (; i < prices.size(); i++) {
            sum += prices.get(i);
            sma[i] = sum / period;
            sum -= prices.get(i-period+1);
        }
        
        return sma;
    }
    
    public static double[] calculateStdDev(List<Double> prices, int period) {
        double[] stdDev = new double[prices.size()];
        double sum = 0;
        double sumSq = 0;
        
        int i;
        for (i = 0; i < period-1; i++) {
            stdDev[i] = Double.NaN;
        }
        
        for (; i < prices.size(); i++) {
            for (int j = i-period+1; j <= i; j++) {
                sum += prices.get(j);
                sumSq += Math.pow(prices.get(j), 2);
            }
            double mean = sum / period;
            stdDev[i] = Math.sqrt((sumSq - period * Math.pow(mean, 2)) / (period - 1));
            sum -= prices.get(i-period+1);
            sumSq -= Math.pow(prices.get(i-period+1), 2);
        }
        
        return stdDev;
    }
}