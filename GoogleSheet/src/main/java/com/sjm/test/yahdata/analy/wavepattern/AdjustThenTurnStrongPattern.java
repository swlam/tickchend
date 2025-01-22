package com.sjm.test.yahdata.analy.wavepattern;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.type.WaveType;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WavePoint;
import com.sjm.test.yahdata.analy.ta.KHelper;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AdjustThenTurnStrongPattern {


	public Set<String> find(List<StockBean> stockList, List<WavePoint> sortedTopBotList) {
		Set<String> msg = new LinkedHashSet<String>();
		if(sortedTopBotList.size()<4 )
			return msg;
		
		StockBean last1 = stockList.getLast();
		StockBean last2 = stockList.get(stockList.size()-2);
		StockBean last3 = stockList.get(stockList.size()-3);

		WavePoint topBotLast1 = sortedTopBotList.getLast(); // BOT
		WavePoint topBotLast2 = sortedTopBotList.get(sortedTopBotList.size()-2); // TOP
		WavePoint topBotLast3 = sortedTopBotList.get(sortedTopBotList.size()-3); // BOT
		WavePoint topBotLast4 = sortedTopBotList.get(sortedTopBotList.size()-4); // TOP

		boolean isBotTopValid = false;
		if(WaveType.BOT == topBotLast1.getType() && WaveType.TOP == topBotLast2.getType() && WaveType.BOT == topBotLast3.getType() && WaveType.TOP == topBotLast4.getType()){
			if(topBotLast2.getStockBean().getBodyBottom()>=topBotLast4.getStockBean().getBodyTop() &&
				topBotLast1.getStockBean().getBodyBottom()>=topBotLast3.getStockBean().getBodyTop() &&
				last1.getH()<topBotLast2.getH() && last2.getH()<topBotLast2.getH() && last3.getH()<topBotLast2.getH()
			){
				isBotTopValid = true;
			}
		}

		if(isBotTopValid &&
			last1.getDayChgPct()>0 && last2.getDayChgPct()>0 &&
			last1.getL() > last3.getL()
		){
			msg.add(Const.WAIT+Const.UP+"ZZ強");
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
