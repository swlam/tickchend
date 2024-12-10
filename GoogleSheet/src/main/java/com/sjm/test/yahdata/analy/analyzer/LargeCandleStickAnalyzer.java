package com.sjm.test.yahdata.analy.analyzer;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LargeCandleStickAnalyzer {

    public static String findLargeCandlestickAndHigherClose(List<StockBean> stockData, int recentDaysCount) {
        if(stockData.size()< recentDaysCount)
            return Const.EMPTY;
        StockBean last = stockData.getLast();
        // e.g. 获取最近10天的股票数据
//        int recentDaysCount = 10;
        List<StockBean> recentStockData = stockData.subList(stockData.size() - recentDaysCount, stockData.size());

        // 找到最近10天内具有最大烛身和第二大烛身的股票数据
        List<StockBean> topTwoCandleBodies = recentStockData.stream()
                .sorted(Comparator.comparingDouble(LargeCandleStickAnalyzer::getCandleBodySize).reversed())
                .limit(2)
                .toList();

        // 找到最近10天内具有最大成交量和第二大成交量的股票数据
        List<StockBean> topTwoVolumes = recentStockData.stream()
                .sorted(Comparator.comparingDouble(StockBean::getVolume).reversed())
                .limit(2)
                .toList();

        // 合并两个列表
        List<StockBean> topCandidates = new ArrayList<>(topTwoCandleBodies);
        topCandidates.addAll(topTwoVolumes);

        // 去重
        topCandidates = topCandidates.stream()
                .distinct()
                .toList();

        // 找到收盘价低于开盘价的股票数据
        List<StockBean> validStocks = topCandidates.stream()
                .filter(stockBean -> stockBean.getC() < stockBean.getO())
                .toList();

        String returnData = Const.EMPTY;
        if (!validStocks.isEmpty()) {
            // 找到收盘价最高的股票数据
            StockBean highestClosePriceStock = validStocks.stream()
                    .max(Comparator.comparingDouble(StockBean::getC))
                    .orElse(null);

            if (highestClosePriceStock != null) {
//                System.out.println("收盘价低于开盘价且收盘价最高的交易日期: " + highestClosePriceStock.getTxnDate());
                // 从该日期的下一个日期开始查找
                int startIndex = stockData.indexOf(highestClosePriceStock) + 1;
                for (int i = startIndex; i < stockData.size(); i++) {
                    StockBean currentStock = stockData.get(i);
                    if (currentStock.getC() > highestClosePriceStock.getBodyTop() && last.getC()>=highestClosePriceStock.getBodyTop()) {
//                        System.out.println("找到收盘价高于该日最高价的交易日期: " + currentStock.getTxnDate());
                        returnData =  currentStock.getTxnDate();
                        break;
                    }
                }
            }
        } else {
//            System.out.println("没有找到符合条件的股票数据");
//            return Const.EMPTY;
        }
        return returnData;
    }


    public static String findLargeCandlestickAndLowerClose(List<StockBean> stockData, int recentDaysCount) {
        if(stockData.size()< recentDaysCount)
            return Const.EMPTY;
        StockBean last = stockData.getLast();
        // e.g. 获取最近10天的股票数据
//        int recentDaysCount = 10;
        List<StockBean> recentStockData = stockData.subList(stockData.size() - recentDaysCount, stockData.size());

        // 找到最近10天内具有最大烛身和第二大烛身的股票数据
        List<StockBean> topTwoCandleBodies = recentStockData.stream()
                .sorted(Comparator.comparingDouble(LargeCandleStickAnalyzer::getCandleBodySize).reversed())
                .limit(2)
                .toList();

        // 找到最近10天内具有最大成交量和第二大成交量的股票数据
        List<StockBean> topTwoVolumes = recentStockData.stream()
                .sorted(Comparator.comparingDouble(StockBean::getVolume).reversed())
                .limit(2)
                .toList();

        // 合并两个列表
        List<StockBean> topCandidates = new ArrayList<>(topTwoCandleBodies);
        topCandidates.addAll(topTwoVolumes);

        // 去重
        topCandidates = topCandidates.stream()
                .distinct()
                .toList();

        // 找到收盘价高于开盘价的股票数据
        List<StockBean> validStocks = topCandidates.stream()
                .filter(stockBean -> stockBean.getC() > stockBean.getO())
                .toList();

        String returnData = Const.EMPTY;
        if (!validStocks.isEmpty()) {
            // 找到收盘价最低的股票数据
            StockBean lowestClosePriceStock = validStocks.stream()
                    .min(Comparator.comparingDouble(StockBean::getC))
                    .orElse(null);

            if (lowestClosePriceStock != null) {
                // 从该日期的下一个日期开始查找
                int startIndex = stockData.indexOf(lowestClosePriceStock) + 1;
                for (int i = startIndex; i < stockData.size(); i++) {
                    StockBean currentStock = stockData.get(i);
                    if (currentStock.getC() < lowestClosePriceStock.getBodyBottom() && last.getC()<=lowestClosePriceStock.getBodyBottom()) {
//                        System.out.println("找到收盘价高于该日最高价的交易日期: " + currentStock.getTxnDate());
                        returnData =  currentStock.getTxnDate();
                        break;
                    }
                }
            }
        } else {
//            System.out.println("没有找到符合条件的股票数据");
//            return Const.EMPTY;
        }
        return returnData;
    }

    private static double getCandleBodySize(StockBean stockBean) {
        return Math.abs(stockBean.getC() - stockBean.getO());
    }


}
