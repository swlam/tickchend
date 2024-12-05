package com.sjm.test.yahdata.analy.wavepattern;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WavePoint;
import com.sjm.test.yahdata.analy.ta.KHelper;

public class UpBreakTriangleWavePattern extends BaseWavePattern {

	@Override
	public Set<String> find(List<StockBean> stockList, List<WavePoint> sortedTopList, List<WavePoint> sortedBotList) {

		
		return findUpBreakTriangle(stockList, sortedTopList, sortedBotList);
	}

	
	private Set<String> findUpBreakTriangle(List<StockBean> stockList, List<WavePoint> sortedTopList, List<WavePoint> sortedBotList) {
		Set<String> msg = new LinkedHashSet<String>();
		if(sortedTopList.size()<3 ||sortedBotList.size()<3)
			return msg;
		
		StockBean last = stockList.get(stockList.size()-1);
		
		WavePoint topLast1 = sortedTopList.get(sortedTopList.size()-1);
		WavePoint topLast2 = sortedTopList.get(sortedTopList.size()-2);
		WavePoint topLast3 = sortedTopList.get(sortedTopList.size()-3);
		
		WavePoint botLast1 = sortedBotList.get(sortedBotList.size()-1);
		WavePoint botLast2 = sortedBotList.get(sortedBotList.size()-2);
		WavePoint botLast3 = sortedBotList.get(sortedBotList.size()-3);

		
		boolean ispass1 = false;
		boolean ispass2 = false;
		if(topLast3.getStockBean().getH()>=topLast2.getStockBean().getH() && topLast2.getStockBean().getH()>=topLast1.getStockBean().getH()) {
			ispass1 = true;
		}
		
//		if(topLast3.getStockBean().getBodyTop()>=topLast2.getStockBean().getBodyTop() && topLast2.getStockBean().getBodyTop()>=topLast1.getStockBean().getBodyTop()) {
//			ispass1 = true;
//		}
		List<StockBean>  topToEndList = StreamTransformHelper.subListWithEndElement(stockList, topLast1.getDate(), last.getTxnDate());;
		List<StockBean>  targetStockList1 = topToEndList.subList(1, topToEndList.size());
		StockBean relativeHighSk = StreamTransformHelper.findMaxHighStock(targetStockList1);
		if(relativeHighSk.getH() > topLast1.getH()){
			ispass1 = false;
		}

		if(botLast3.getStockBean().getL()<=botLast2.getStockBean().getL() && botLast2.getStockBean().getL()<=botLast1.getStockBean().getL()) {
			ispass2 = true;
		}
		
//		if(botLast3.getStockBean().getBodyBottom()<=botLast2.getStockBean().getBodyBottom() && botLast2.getStockBean().getBodyBottom()<=botLast1.getStockBean().getBodyBottom()) {
//			ispass2 = true;
//		}

		List<StockBean>  botToEndList = StreamTransformHelper.subListWithEndElement(stockList, botLast1.getDate(), last.getTxnDate());;
		List<StockBean>  targetStockList2 = botToEndList.subList(1, botToEndList.size());
		StockBean relativeLowSk = StreamTransformHelper.findMinLowStock(targetStockList2);
		if(relativeLowSk.getL()< botLast1.getL()){
			ispass2 = false;
		}



		
		
		double thresholdMinRequired = topLast1.getStockBean().getH() * 0.95;
		double thresholdMaxRequired = topLast1.getStockBean().getH() * 1.01;
		
		if(ispass1 && ispass2 && last.getC()>=thresholdMinRequired 
				&& last.getC()<=thresholdMaxRequired
				&& topLast1.getL() > botLast3.getL()
				&& KHelper.isBullishCandle(last)
			) 
		{
			msg.add(Const.WAIT+Const.UP+"破小三角");
			
			boolean isAnotherTriangleShape = this.isUpTrendTriangle( sortedTopList, sortedBotList);
			if(isAnotherTriangleShape)
				msg.add(Const.WAIT+Const.UP+"破向上三角");
		}
		
		return msg;
	}
	
	private boolean isUpTrendTriangle( List<WavePoint> sortedTopList, List<WavePoint> sortedBotList) {
		if(sortedTopList.size()<3 ||sortedBotList.size()<3)
			return false;
		
		
		WavePoint topLast1 = sortedTopList.get(sortedTopList.size()-1);
		WavePoint topLast2 = sortedTopList.get(sortedTopList.size()-2);
		WavePoint topLast3 = sortedTopList.get(sortedTopList.size()-3);
		
		WavePoint botLast1 = sortedBotList.get(sortedBotList.size()-1);
		WavePoint botLast2 = sortedBotList.get(sortedBotList.size()-2);
		WavePoint botLast3 = sortedBotList.get(sortedBotList.size()-3);
		
		
		double threshold = 0.03;
		boolean isExceedThreshold = isDifferenceExceedThreshold(topLast3.getH(), topLast2.getH(), topLast1.getH(), threshold);
		
		if(isExceedThreshold ==false) {
			
			boolean isGoodBottom = false;
			if(botLast3.getStockBean().getL()<botLast2.getStockBean().getL() && botLast2.getStockBean().getL()<botLast1.getStockBean().getL()) {
				isGoodBottom = true;
			}
			
			if(botLast3.getStockBean().getBodyBottom()<botLast2.getStockBean().getBodyBottom() && botLast2.getStockBean().getBodyBottom()<botLast1.getStockBean().getBodyBottom()) {
				isGoodBottom = true;
			}
			
			
			if(isGoodBottom)
				return true; //return "向上三角";
		}
		
		return false;
	}
	
	private  boolean isDifferenceExceedThreshold(double num1, double num2, double num3, double threshold) {
        double maxDifference = Math.max(Math.abs(num1 - num2), Math.max(Math.abs(num1 - num3), Math.abs(num2 - num3)));
        double maxPercentageDifference = (maxDifference / Math.max(Math.max(num1, num2), num3)) ;
        return maxPercentageDifference > threshold;
 }
}
