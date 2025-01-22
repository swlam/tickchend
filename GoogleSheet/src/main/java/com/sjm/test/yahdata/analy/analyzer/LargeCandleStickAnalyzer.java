package com.sjm.test.yahdata.analy.analyzer;

import com.maas.util.DateHelper;
import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LargeCandleStickAnalyzer {

    public static String findRecentLargeDarkCandleWithHighVolumeAndBreakUp(List<StockBean> stockData, int recentDaysCount) {
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
//                        int days = DateHelper.dayBetween(highestClosePriceStock.getTxnDate(), currentStock.getTxnDate());
                        returnData =  currentStock.getTxnDate() +"(" +highestClosePriceStock.getTxnDate()+")";
                        break;
                    }
                }
            }
        }
        return returnData;
    }


    public static String findRecentLargeWhiteCandleWithHighVolumeAndBreakdown(List<StockBean> stockData, int recentDaysCount) {
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
//                        int days = DateHelper.dayBetween(lowestClosePriceStock.getTxnDate(), currentStock.getTxnDate());
                        returnData =  currentStock.getTxnDate() + "("+lowestClosePriceStock.getTxnDate()+")";
                        break;
                    }
                }
            }
        }
//        else {
//            System.out.println("没有找到符合条件的股票数据");
//            return Const.EMPTY;
//        }
        return returnData;
    }


    public static String findRecentHighVolume(List<StockBean> stockData, int recentDaysCount) {
        if(stockData.size()< recentDaysCount)
            return Const.EMPTY;
        StockBean last = stockData.getLast();
        // e.g. 获取最近天的股票数据

        List<StockBean> recentStockData = stockData.subList(stockData.size() - recentDaysCount, stockData.size());

        List<StockBean> recent2WeeksStockData = stockData.subList(stockData.size() - 10, stockData.size());

        // 找到最近10天内具有最大烛身和第二大烛身的股票数据
//        List<StockBean> topTwoCandleBodies = recentStockData.stream()
//                .sorted(Comparator.comparingDouble(LargeCandleStickAnalyzer::getCandleBodySize).reversed())
//                .limit(2)
//                .toList();

        // 找到最近10天内具有最大成交量和第二大成交量的股票数据
        List<StockBean> topTwoVolumes = recentStockData.stream()
                .sorted(Comparator.comparingDouble(StockBean::getVolume).reversed())
                .limit(2)
                .toList();


        List<StockBean> validVolumeStocks = new ArrayList<>();

        int nDays = 3;
//        // 获取最新的日期
//        int latestDateInt = recentStockData.stream()
//                .mapToInt(StockBean::getTxnDateInt)
//                .max()
//                .orElse(Integer.MAX_VALUE);

        // 检查这两个股票是否在最近 N 天内
        if (topTwoVolumes.size() == 2) {
            StockBean stock1 = topTwoVolumes.get(0);
            StockBean stock2 = topTwoVolumes.get(1);

            // 检查两个股票是否在最近 N 天内
            boolean isWithinNDays = Math.abs(stock1.getTxnDateInt() - stock2.getTxnDateInt()) <= nDays;

            if (!isWithinNDays) {
                // 如果不在 N 天内，将时间较早的 StockBean 对象加入 validStocks
                StockBean earlierStock = stock1.getTxnDateInt() < stock2.getTxnDateInt() ? stock1 : stock2;
                validVolumeStocks.add(earlierStock);
            }
        }else {
            validVolumeStocks.addAll(topTwoVolumes);
        }


        List<StockBean> validStocks = new ArrayList<>();

        for (StockBean sk : validVolumeStocks)
        {
            int startIndex = stockData.indexOf(sk);
            if (startIndex < 30) {
                // 如果 startIndex 小于 30，说明数据不足 30 天，跳过
                continue;
            }

            List<StockBean> substockList = stockData.subList(startIndex - 30, startIndex +1);
            double avgVol = substockList.stream().mapToDouble(StockBean::getVolume).average().orElse(Double.NaN);

            if( sk.getVolume() > avgVol * 2.0 ){
                validStocks.add(sk);
            }
        }

        String returnData = Const.EMPTY;
        if (!validStocks.isEmpty()) {
            // 找到收盘价最高的股票数据
            StockBean highestVolStock = validStocks.stream()
                    .max(Comparator.comparingDouble(StockBean::getVolume))
                    .orElse(null);

            boolean isFound = false;

            // 从该日期的下一个日期开始查找
            int startIndex = stockData.indexOf(highestVolStock) + 1;
            for (int i = startIndex; i < stockData.size(); i++) {
                StockBean currentStock = stockData.get(i);
                if (currentStock.getC() > highestVolStock.getBodyTop() && last.getC()>=highestVolStock.getBodyTop()) {
                    returnData =  currentStock.getTxnDate() +"(ori:" +highestVolStock.getTxnDate()+" v:" + GeneralHelper.toPct(highestVolStock.getDayVolumeChgPct()) +")" ;
                    isFound = true;
                    break;
                }
            }

            if(isFound){

                for (StockBean currentStock : recent2WeeksStockData) {
                    if (currentStock.getTxnDateInt() > highestVolStock.getTxnDateInt()
                            && currentStock.getC() > highestVolStock.getBodyTop() && currentStock.getH() > highestVolStock.getH()
//                            && last.getC() >= highestVolStock.getBodyTop()
                    )
                    {
                        returnData = returnData + " - (" + currentStock.getTxnDate() + ")";
                        break;
                    }
                }
            }




        }
        return returnData;
    }


    public static String findRecentHighVolumeGoDown(List<StockBean> stockData, int recentDaysCount) {
        if(stockData.size()< recentDaysCount)
            return Const.EMPTY;
        StockBean last = stockData.getLast();
        // e.g. 获取最近天的股票数据

        List<StockBean> recentStockData = stockData.subList(stockData.size() - recentDaysCount, stockData.size());

        List<StockBean> recent2WeeksStockData = stockData.subList(stockData.size() - 10, stockData.size());

        // 找到最近10天内具有最大烛身和第二大烛身的股票数据
//        List<StockBean> topTwoCandleBodies = recentStockData.stream()
//                .sorted(Comparator.comparingDouble(LargeCandleStickAnalyzer::getCandleBodySize).reversed())
//                .limit(2)
//                .toList();

        // 找到最近10天内具有最大成交量和第二大成交量的股票数据
        List<StockBean> topTwoVolumes = recentStockData.stream()
                .sorted(Comparator.comparingDouble(StockBean::getVolume).reversed())
                .limit(2)
                .toList();


        List<StockBean> validVolumeStocks = new ArrayList<>();

        int nDays = 3;
//        // 获取最新的日期
//        int latestDateInt = recentStockData.stream()
//                .mapToInt(StockBean::getTxnDateInt)
//                .max()
//                .orElse(Integer.MAX_VALUE);

        // 检查这两个股票是否在最近 N 天内
        if (topTwoVolumes.size() == 2) {
            StockBean stock1 = topTwoVolumes.get(0);
            StockBean stock2 = topTwoVolumes.get(1);

            // 检查两个股票是否在最近 N 天内
            boolean isWithinNDays = Math.abs(stock1.getTxnDateInt() - stock2.getTxnDateInt()) <= nDays;

            if (!isWithinNDays) {
                // 如果不在 N 天内，将时间较早的 StockBean 对象加入 validStocks
                StockBean earlierStock = stock1.getTxnDateInt() < stock2.getTxnDateInt() ? stock1 : stock2;
                validVolumeStocks.add(earlierStock);
            }
        }else {
            validVolumeStocks.addAll(topTwoVolumes);
        }


        List<StockBean> validStocks = new ArrayList<>();

        for (StockBean sk : validVolumeStocks)
        {
            int startIndex = stockData.indexOf(sk);
            if (startIndex < 30) {
                // 如果 startIndex 小于 30，说明数据不足 30 天，跳过
                continue;
            }

            List<StockBean> substockList = stockData.subList(startIndex - 30, startIndex +1);
            double avgVol = substockList.stream().mapToDouble(StockBean::getVolume).average().orElse(Double.NaN);

            if( sk.getVolume() > avgVol * 2.0 ){
                validStocks.add(sk);
            }
        }

        String returnData = Const.EMPTY;
        if (!validStocks.isEmpty()) {
            // 找到收盘价最高的股票数据
            StockBean highestVolStock = validStocks.stream()
                    .max(Comparator.comparingDouble(StockBean::getVolume))
                    .orElse(null);

            boolean isFound = false;

            // 从该日期的下一个日期开始查找
            int startIndex = stockData.indexOf(highestVolStock) + 1;
            for (int i = startIndex; i < stockData.size(); i++) {
                StockBean currentStock = stockData.get(i);
                if (currentStock.getC() < highestVolStock.getBodyBottom() && last.getC()<=highestVolStock.getBodyBottom()) {
                    returnData =  currentStock.getTxnDate() +"(ori:" +highestVolStock.getTxnDate()+" v:" + GeneralHelper.toPct(highestVolStock.getDayVolumeChgPct()) +")" ;
                    isFound = true;
                    break;
                }
            }

            if(isFound){

                for (StockBean currentStock : recent2WeeksStockData) {
                    if (currentStock.getTxnDateInt() > highestVolStock.getTxnDateInt()
                            && currentStock.getC() < highestVolStock.getBodyBottom() && currentStock.getL() < highestVolStock.getL()
//                            && last.getC() >= highestVolStock.getBodyTop()
                    )
                    {
                        returnData = returnData + " - (" + currentStock.getTxnDate() + ")";
                        break;
                    }
                }
            }




        }
        return returnData;
    }



    private static double getCandleBodySize(StockBean stockBean) {
        return Math.abs(stockBean.getC() - stockBean.getO());
    }


}
