package com.sjm.test.yahdata.analy.analyzer;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;

import java.util.ArrayList;
import java.util.List;
public class MACDCalculator {

    private static final int SHORT_EMA_PERIOD = 12;
    private static final int LONG_EMA_PERIOD = 26;
    private static final int SIGNAL_LINE_PERIOD = 9;

    public static List<MACDResult> calculateMACD(List<StockBean> stockBeans) {
        List<Double> shortEMAs = calculateEMA(stockBeans, SHORT_EMA_PERIOD);
        List<Double> longEMAs = calculateEMA(stockBeans, LONG_EMA_PERIOD);
        List<Double> macdLines = new ArrayList<>();
        List<Double> signalLines = new ArrayList<>();
        List<Double> macdHistograms = new ArrayList<>();

        for (int i = 0; i < stockBeans.size(); i++) {
            if (i >= LONG_EMA_PERIOD - 1) {
                double macdLine = shortEMAs.get(i) - longEMAs.get(i);
                macdLines.add(macdLine);
            } else {
                macdLines.add(null);
            }
        }

//        int firstInxHasMACDValue = LONG_EMA_PERIOD - 1;
        for (int i = 0; i < macdLines.size(); i++) {
            if (i >= SIGNAL_LINE_PERIOD - 1) {
                double signalLine = calculateEMAForValues(macdLines.subList(i - SIGNAL_LINE_PERIOD + 1, i + 1), SIGNAL_LINE_PERIOD).get(SIGNAL_LINE_PERIOD - 1);
                signalLines.add(signalLine);
            } else {
                signalLines.add(null);
            }
        }

        for (int i = 0; i < macdLines.size(); i++) {
            if (macdLines.get(i) != null && signalLines.get(i) != null) {
                double macdHistogram = macdLines.get(i) - signalLines.get(i);
                macdHistograms.add(macdHistogram);
            } else {
                macdHistograms.add(null);
            }
        }

        List<MACDResult> results = new ArrayList<>();
        for (int i = 0; i < stockBeans.size(); i++) {
            results.add(new MACDResult(
                    stockBeans.get(i).getTxnDate(),
                    stockBeans.get(i).getC(),
                    shortEMAs.get(i),
                    longEMAs.get(i),
                    macdLines.get(i),
                    signalLines.get(i),
                    macdHistograms.get(i)
            ));
        }

        return results;
    }

    private static List<Double> calculateEMA(List<StockBean> stockBeans, int period) {
        List<Double> emas = new ArrayList<>();
        double multiplier = 2.0 / (period + 1);
        double sum = 0.0;
        for (int i = 0; i < stockBeans.size(); i++) {
            double price = stockBeans.get(i).getC();
            if (i < period - 1) {
                sum += price;
                emas.add(null);
            } else if (i == period - 1) {
                sum += price;
                double sma = sum / period;
                emas.add(sma);
            } else {
                double prevEMA = emas.get(i - 1);
                double ema = (price - prevEMA) * multiplier + prevEMA;
                emas.add(ema);
            }
        }
        return emas;
    }

    private static List<Double> calculateEMAForValues(List<Double> values, int period) {
        List<Double> emas = new ArrayList<>();
        double multiplier = 2.0 / (period + 1);
        double sum = 0.0;
        for (int i = 0; i < values.size(); i++) {
            Double value = values.get(i);
            if (value == null) {
                value = 0.0; // 处理 null 值，可以选择其他默认值
            }
            if (i < period - 1) {
                sum += value;
                emas.add(null);
            } else if (i == period - 1) {
                sum += value;
                double sma = sum / period;
                emas.add(sma);
            } else {
                double prevEMA = emas.get(i - 1);
                double ema = (value - prevEMA) * multiplier + prevEMA;
                emas.add(ema);
            }
        }
        return emas;
    }

    public static class MACDResult {
        private String txnDate;
        private double closePrice;
        private Double shortEMA;
        private Double longEMA;
        private Double macdLine;
        private Double signalLine;
        private Double macdHistogram;

        public MACDResult(String txnDate, double closePrice, Double shortEMA, Double longEMA, Double macdLine, Double signalLine, Double macdHistogram) {
            this.txnDate = txnDate;
            this.closePrice = closePrice;
            this.shortEMA = shortEMA;
            this.longEMA = longEMA;
            this.macdLine = macdLine;
            this.signalLine = signalLine;
            this.macdHistogram = macdHistogram;
        }

        // Getters and setters
        public String getTxnDate() {
            return txnDate;
        }

        public double getClosePrice() {
            return closePrice;
        }

        public Double getShortEMA() {
            return shortEMA;
        }

        public Double getLongEMA() {
            return longEMA;
        }

        public Double getMacdLine() {
            return macdLine;
        }

        public Double getSignalLine() {
            return signalLine;
        }

        public Double getMacdHistogram() {
            return macdHistogram;
        }

        @Override
        public String toString() {
            return "MACDResult{" +
                    "txnDate='" + txnDate + '\'' +
                    ", closePrice=" + closePrice +
                    ", shortEMA=" + shortEMA +
                    ", longEMA=" + longEMA +
                    ", macdLine=" + macdLine +
                    ", signalLine=" + signalLine +
                    ", macdHistogram=" + macdHistogram +
                    '}';
        }
    }

//    public static void main(String[] args) {
//        // 示例数据
//        List<StockBean> stockBeans = new ArrayList<>();
//        stockBeans.add(new StockBean("2023-01-01", 100.0));
//        stockBeans.add(new StockBean("2023-01-02", 101.0));
//        stockBeans.add(new StockBean("2023-01-03", 102.0));
//        stockBeans.add(new StockBean("2023-01-04", 103.0));
//        stockBeans.add(new StockBean("2023-01-05", 104.0));
//        stockBeans.add(new StockBean("2023-01-06", 105.0));
//        stockBeans.add(new StockBean("2023-01-07", 106.0));
//        stockBeans.add(new StockBean("2023-01-08", 107.0));
//        stockBeans.add(new StockBean("2023-01-09", 108.0));
//        stockBeans.add(new StockBean("2023-01-10", 109.0));
//        stockBeans.add(new StockBean("2023-01-11", 110.0));
//        stockBeans.add(new StockBean("2023-01-12", 111.0));
//        stockBeans.add(new StockBean("2023-01-13", 112.0));
//        stockBeans.add(new StockBean("2023-01-14", 113.0));
//        stockBeans.add(new StockBean("2023-01-15", 114.0));
//        stockBeans.add(new StockBean("2023-01-16", 115.0));
//        stockBeans.add(new StockBean("2023-01-17", 116.0));
//        stockBeans.add(new StockBean("2023-01-18", 117.0));
//        stockBeans.add(new StockBean("2023-01-19", 118.0));
//        stockBeans.add(new StockBean("2023-01-20", 119.0));
//
//        List<MACDResult> macdResults = calculateMACD(stockBeans);
//
//        for (MACDResult result : macdResults) {
//            System.out.println(result);
//        }
//    }
}

