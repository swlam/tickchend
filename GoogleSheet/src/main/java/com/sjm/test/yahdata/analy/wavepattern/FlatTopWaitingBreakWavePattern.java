package com.sjm.test.yahdata.analy.wavepattern;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WavePoint;
import com.sjm.test.yahdata.analy.ta.KHelper;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

//Double Top waiting to be broken
public class FlatTopWaitingBreakWavePattern extends BaseWavePattern{
    private static final int WINDOW_SIZE = 3;
    @Override
    public Set<String> find(List<StockBean> stockList, List<WavePoint> sortedTopList, List<WavePoint> sortedBotList) {
//        return Set.of();

        return findFlatTopByTopQty(stockList,sortedTopList, sortedBotList,WINDOW_SIZE);
    }

    private Set<String> findFlatTopByTopQty(List<StockBean> stockList, List<WavePoint> sortedTopList, List<WavePoint> sortedBotList, int windowSize) {
        Set<String> attributes = new LinkedHashSet<String>();


//        List<WavePoint> sortedTopList = sortedTopBotList.stream().filter(x-> WaveType.TOP== x.getType()).toList();
//        List<WavePoint> sortedBotList = sortedTopBotList.stream().filter(x-> WaveType.BOT== x.getType()).toList();
        if(sortedTopList.size() <= windowSize || sortedBotList.size() <= windowSize)
            return attributes;

        StockBean last1 = stockList.get(stockList.size()-1);
        StockBean last2 = stockList.get(stockList.size()-2);

        WavePoint last1Top = sortedTopList.get(sortedTopList.size()-1);
        WavePoint last2Top = sortedTopList.get(sortedTopList.size()-2);
        WavePoint last3Top = sortedTopList.get(sortedTopList.size()-3);

        WavePoint last1Bot = sortedBotList.get(sortedBotList.size()-1);

        List<WavePoint> targetWpList = sortedTopList.subList(sortedTopList.size()-windowSize, sortedTopList.size());
        boolean isFlatTop = this.isFlatTop(targetWpList);
//			   for(WavePoint data: targetWpList) {
//				   List<WavePoint> othersList = targetWpList.stream().filter(x-> !x.getDate().equalsIgnoreCase(data.getDate())).toList();
//				   boolean b1 = this.isTopValueHigherThanOthersLowPrice(data.getStockBean().getBodyTop(), othersList);
//				   boolean b2 = this.isBottomValueLowerThanOthersHightPrice(data.getStockBean().getBodyBottom(), othersList);
////				   boolean b2 = isValueHigherThanAllStockDataBodyBottom(data.getH(), othersList);
//				   if(b1==false || b2==false) {
//					   return attributes;
//				   }
//			   }

        boolean isPatternMValid = checkFlatTopPattern(last1Top, last2Top);
        boolean isPattern3BottomValid = checkFlatTopPattern(last1Top, last2Top, last3Top);


        boolean b1 = last1.getC() < last1Top.getH() && last1.getC() >= last1Bot.getH();

        boolean bbD0 = last2.getH()< last1Top.getH() && last1.getH()  > last1Top.getH() &&
                last1.getC() > last1Top.getStockBean().getBodyTop() &&
                last1.isRiseToday();


//        boolean bFlatTop1 = last1Top.getStockBean().getBodyTop() > last2Top.getStockBean().getBodyBottom() && last1Top.getStockBean().getBodyTop() < last2Top.getH();
//        boolean bFlatTop2 = last2Top.getStockBean().getBodyTop() > last1Top.getStockBean().getBodyBottom() && last2Top.getStockBean().getBodyTop() < last1Top.getH();

        boolean b2 = KHelper.isBullishCandle(last1) && last1.getBodyTop() >= last2.getBodyTop() && last1.getH() > last2.getH() && last1.getVolume() >= last2.getVolume();
        boolean bEnterLastTopKBody = last1.getBodyTop() > last1Top.getStockBean().getBodyBottom() ;

//        if(b1 && (bFlatTop1 && bFlatTop2) && b2 && bEnterLastTopKBody) {
//            attributes.add(windowSize+"平頂"+Const.WAIT+Const.UP);
//        }

        if (b2 && bEnterLastTopKBody) {
            if (b1) {
                addPatternAttribute(attributes, isPattern3BottomValid, isPatternMValid,  Const.WAIT+Const.UP);
            } else if (bbD0) {
                addPatternAttribute(attributes, isPattern3BottomValid, isPatternMValid, Const.UP + Const.D0);
            }
        }

        return attributes;
    }

    public boolean isFlatTop(List<WavePoint> targetWpList) {
        for(WavePoint data: targetWpList) {
            List<WavePoint> othersList = targetWpList.stream().filter(x-> !x.getDate().equalsIgnoreCase(data.getDate())).toList();
            boolean b1 = this.isTopValueHigherThanOthersLowPrice(data.getStockBean().getBodyTop(), othersList);
            boolean b2 = this.isBottomValueLowerThanOthersHightPrice(data.getStockBean().getBodyBottom(), othersList);
//			   boolean b2 = isValueHigherThanAllStockDataBodyBottom(data.getH(), othersList);
            if(b1==false || b2==false) {
                return false;
            }
        }
        return true;
    }


    private void addPatternAttribute(Set<String> attributes, boolean isPattern3BottomValid, boolean isPatternMValid, String prefix) {
        if (isPattern3BottomValid) {
            attributes.add(prefix + "_3頂");
        } else if (isPatternMValid) {
            attributes.add(prefix + "_2頂");
        }
    }

    private boolean isFlatTop(WavePoint currentTop, WavePoint previousTop) {
        return currentTop.getStockBean().getBodyTop() > previousTop.getStockBean().getBodyBottom()  &&
                currentTop.getStockBean().getBodyTop() < previousTop.getH();
    }

    private boolean checkFlatTopPattern(WavePoint last1Bot, WavePoint... otherBots) {
        for (WavePoint otherBot : otherBots) {
            if (!isFlatTop(last1Bot, otherBot)) {
                return false;
            }
        }
        return true;
    }
}





