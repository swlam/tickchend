package com.sjm.test.yahdata.analy.wavepattern;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WavePoint;

import java.util.List;
import java.util.Set;

public abstract class BaseTriangleWavePattern extends BaseWavePattern{

    public boolean isTrangleWave(List<StockBean> stockList, List<WavePoint> sortedTopList, List<WavePoint> sortedBotList){
        StockBean last1 = stockList.getLast();
        StockBean last2 = stockList.get(stockList.size()-2);

        WavePoint topLast1 = sortedTopList.getLast();
        WavePoint topLast2 = sortedTopList.get(sortedTopList.size()-2);
        WavePoint topLast3 = sortedTopList.get(sortedTopList.size()-3);

        WavePoint botLast1 = sortedBotList.getLast();
        WavePoint botLast2 = sortedBotList.get(sortedBotList.size()-2);
        WavePoint botLast3 = sortedBotList.get(sortedBotList.size()-3);


        boolean ispass1 = false;
        boolean ispass2 = false;
        boolean ispass3 = false;
        if(topLast3.getStockBean().getH()>topLast2.getStockBean().getH()
                && topLast2.getStockBean().getH()>topLast1.getStockBean().getH()) {
            ispass1 = true;
        }

//		if(topLast3.getStockBean().getBodyTop()>=topLast2.getStockBean().getBodyTop() && topLast2.getStockBean().getBodyTop()>=topLast1.getStockBean().getBodyTop()) {
//			ispass1 = true;
//		}
        List<StockBean>  topToEndList = StreamTransformHelper.subListWithEndElement(stockList, topLast1.getDate(), last1.getTxnDate());;
        List<StockBean>  targetStockList1 = topToEndList.subList(1, topToEndList.size());
        StockBean relativeHighSk = StreamTransformHelper.findMaxHighStock(targetStockList1);
        if(relativeHighSk.getH() > topLast1.getH()){
            ispass1 = false;
        }

        if(botLast3.getStockBean().getL()<botLast2.getStockBean().getL()
                && botLast2.getStockBean().getL()<botLast1.getStockBean().getL()) {
            ispass2 = true;
        }

//		if(botLast3.getStockBean().getBodyBottom()<=botLast2.getStockBean().getBodyBottom() && botLast2.getStockBean().getBodyBottom()<=botLast1.getStockBean().getBodyBottom()) {
//			ispass2 = true;
//		}

        List<StockBean>  botToEndList = StreamTransformHelper.subListWithEndElement(stockList, botLast1.getDate(), last1.getTxnDate());;
        List<StockBean>  targetStockList2 = botToEndList.subList(1, botToEndList.size());
        StockBean relativeLowSk = StreamTransformHelper.findMinLowStock(targetStockList2);
        if(relativeLowSk.getL()< botLast1.getL()){
            ispass2 = false;
        }

        if( topLast1.getL() > botLast3.getL()){
            ispass3 = true;
        }

        if(!(ispass1 && ispass2 && ispass3))
            return false;
        return true;
    }
}
