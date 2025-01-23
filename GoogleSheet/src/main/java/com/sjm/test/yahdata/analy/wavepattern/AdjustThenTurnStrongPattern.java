package com.sjm.test.yahdata.analy.wavepattern;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.type.WaveType;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WavePoint;
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


}
