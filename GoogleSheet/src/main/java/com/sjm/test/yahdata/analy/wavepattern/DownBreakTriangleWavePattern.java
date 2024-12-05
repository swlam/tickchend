package com.sjm.test.yahdata.analy.wavepattern;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.type.WaveType;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WavePoint;
import com.sjm.test.yahdata.analy.ta.KHelper;

public class DownBreakTriangleWavePattern extends BaseWavePattern {

	@Override
	public Set<String> find(List<StockBean> stockList, List<WavePoint> sortedTopList, List<WavePoint> sortedBotList) {

		
		return findDownBreakTriangle(stockList, sortedTopList, sortedBotList);
	}

	
	private Set<String> findDownBreakTriangle(List<StockBean> stockList, List<WavePoint> sortedTopList, List<WavePoint> sortedBotList) {
		Set<String> msg = new LinkedHashSet<String>();
		
		if(sortedTopList.size()<3 || sortedBotList.size()<3)
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


		
		if(botLast3.getStockBean().getL()<=botLast2.getStockBean().getL() && botLast2.getStockBean().getL()<=botLast1.getStockBean().getL()) {
			ispass2 = true;
		}
		
//		if(botLast3.getStockBean().getBodyBottom()<=botLast2.getStockBean().getBodyBottom() && botLast2.getStockBean().getBodyBottom()<=botLast1.getStockBean().getBodyBottom()) {
//			ispass2 = true;
//		}

		List<StockBean>  botToEndList = StreamTransformHelper.subListWithEndElement(stockList, botLast1.getDate(), last.getTxnDate());;
		List<StockBean>  targetStockList1 = botToEndList.subList(1, botToEndList.size());
		StockBean relativeLowSk = StreamTransformHelper.findMinLowStock(targetStockList1);
		if(relativeLowSk.getH() < botLast1.getL()){
			ispass2 = false;
		}


//		double thresholdMinRequired = topLast1.getStockBean().getL() * 0.95;
		double thresholdRequired = botLast1.getStockBean().getL() * 1.03;
		
		if(ispass1 && ispass2
			&& topLast1.getL() > botLast3.getL()
			&& last.getC()<=thresholdRequired && KHelper.isBearishCandle(last))
		{
			msg.add(Const.WAIT+Const.DOWN+"破小三角");
			
		}
		
		return msg;
	}
}
