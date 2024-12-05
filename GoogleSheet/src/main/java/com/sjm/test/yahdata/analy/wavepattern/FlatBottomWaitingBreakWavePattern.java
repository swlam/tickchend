package com.sjm.test.yahdata.analy.wavepattern;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WavePoint;
import com.sjm.test.yahdata.analy.ta.KHelper;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

//Double bottom waiting to be broken
public class FlatBottomWaitingBreakWavePattern extends BaseWavePattern{
    private static final int WINDOW_SIZE = 2;
    @Override
    public Set<String> find(List<StockBean> stockList, List<WavePoint> sortedTopList, List<WavePoint> sortedBotList) {
//        return Set.of();

        return findFlatBottomByBotQty(stockList,sortedTopList, sortedBotList,WINDOW_SIZE);
    }


    private Set<String> findFlatBottomByBotQty(List<StockBean> stockList, List<WavePoint> sortedTopList, List<WavePoint> sortedBotList, int windowSize) {
        Set<String> attributes = new LinkedHashSet<String>();

        if(sortedTopList.size() <= windowSize || sortedBotList.size() <= windowSize)
            return attributes;


        StockBean last1 = stockList.get(stockList.size()-1);
        StockBean last2 = stockList.get(stockList.size()-2);

        WavePoint last1Top = sortedTopList.get(sortedTopList.size()-1);
        WavePoint last1Bot = sortedBotList.get(sortedBotList.size()-1);
        WavePoint last2Bot = sortedBotList.get(sortedBotList.size()-2);

        List<WavePoint> targetWpList = sortedBotList.subList(sortedBotList.size()-windowSize, sortedBotList.size());

//			boolean isFound = true;
        for(WavePoint data: targetWpList)
        {
            List<WavePoint> othersList = targetWpList.stream().filter(x-> !x.getDate().equalsIgnoreCase(data.getDate())).toList();
            boolean isF = this.isValueLowerThanAllStockDataHigh(data.getStockBean().getBodyBottom(), othersList);
            if(isF == false) {
                return attributes;
            }
        }

        boolean b1 = last1.getC() > last1Bot.getL() && last1.getC() > last2Bot.getL() ;
        boolean bFlatBot1 = last1Bot.getStockBean().getBodyBottom() < last2Bot.getStockBean().getBodyTop() && last1Bot.getStockBean().getBodyBottom() > last2Bot.getL();
        boolean bFlatBot2 = last2Bot.getStockBean().getBodyBottom() < last1Bot.getStockBean().getBodyTop() && last2Bot.getStockBean().getBodyBottom() > last1Bot.getL();

        boolean b2 = KHelper.isBearishCandle(last1) && last1.getBodyBottom() <= last2.getBodyBottom() && last1.getL() < last2.getL() ;
        boolean bEnterBotKBody = last1.getBodyBottom() < last1Bot.getStockBean().getBodyTop() ;
        if( b1 && (bFlatBot1 && bFlatBot2) && b2 && bEnterBotKBody )
        {
            attributes.add(windowSize+"平底待"+ Const.DOWN);
        }

        return attributes;
    }

    private boolean isValueLowerThanAllStockDataHigh(double bodyTopValue, List<WavePoint> stockDataList) {
        for (WavePoint stockData : stockDataList) {
            if (bodyTopValue >= stockData.getH()) {
                return false;
            }
        }
        return true;
    }
}

