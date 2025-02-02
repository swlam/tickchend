package com.sjm.test.yahdata.analy.wavepattern;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.type.KBodyType;
import com.sjm.test.yahdata.analy.conts.type.WaveType;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WavePoint;
import com.sjm.test.yahdata.analy.ta.KHelper;

import java.util.*;
import java.util.stream.Collectors;

public class UpBreakWPattern extends BaseWavePattern {
	private static final double DEVIATION = 0.02;

	@Override
	public Set<String> find(List<StockBean> stockList, List<WavePoint> sortedTopList, List<WavePoint> sortedBotList) {

		List<WavePoint> topbotList = new ArrayList<WavePoint>();
		topbotList.addAll(sortedTopList);
		topbotList.addAll(sortedBotList);
		
		List<WavePoint> sortedTopBotList = topbotList.stream().sorted(Comparator.comparing(e -> e.getDateInt())).collect(Collectors.toList());
//		sortedTopBotList = this.distinctSortedTopBotList(sortedTopBotList);
		return findReadyUpBreakW(stockList, sortedTopBotList);
	}


	private Set<String> findReadyUpBreakW(List<StockBean> stockList, List<WavePoint> sortedTopBotList) {
		Set<String> attributes = new LinkedHashSet<String>();

		StockBean last1 = stockList.getLast();
		StockBean last2 = stockList.get(stockList.size()-2);

		if(sortedTopBotList.size()<4 )
			return attributes;

		WavePoint last1Bot = sortedTopBotList.get(sortedTopBotList.size()-1); //BOT
		WavePoint last2Top = sortedTopBotList.get(sortedTopBotList.size()-2); //TOP ,need to break this
		WavePoint last3Bot = sortedTopBotList.get(sortedTopBotList.size()-3); //BOT
		WavePoint last4Top = sortedTopBotList.get(sortedTopBotList.size()-4);//TOP

//			double topHighDiffPct = Math.abs( (last2Top.getH() - last4Top.getH()) / last4Top.getH() );
//			boolean isSameTopHigh = (topHighDiffPct <= DEVIATION);

//			double botLowDiff = Math.abs( (last1Bot.getL() - last3Bot.getL()) / last3Bot.getL() );
//			boolean isSameBottom = (botLowDiff <= DEVIATION);

		int daysDiffInTop = StreamTransformHelper.txDaysBetween(stockList, last4Top.getDate(), last2Top.getDate());
		int daysDiffInBottom = StreamTransformHelper.txDaysBetween(stockList, last3Bot.getDate(), last1Bot.getDate());


		boolean isValidKBody = true;

		if(KHelper.getBodySize(stockList)>= KBodyType.L.getValue() && !last1.isOpenLowCloseHigh())
			isValidKBody = false;

//			Set<String> attributes = new HashSet<String>();
		if( WaveType.TOP==last2Top.getType() && WaveType.BOT==last1Bot.getType() )
		{

			boolean isSameTop = (last2Top.getStockBean().getH() >= last4Top.getStockBean().getBodyBottom() && last2Top.getStockBean().getH() < last4Top.getStockBean().getH() ) || (last4Top.getStockBean().getH() >=last2Top.getStockBean().getBodyBottom() && last4Top.getStockBean().getH() < last2Top.getStockBean().getH() );
			boolean isSameBottom = (last1Bot.getStockBean().getL() > last3Bot.getStockBean().getL() && last1Bot.getStockBean().getL() < last3Bot.getStockBean().getBodyTop()) ||
					(last3Bot.getStockBean().getL() > last1Bot.getStockBean().getL() && last3Bot.getStockBean().getL() < last1Bot.getStockBean().getBodyTop());

			boolean b1 = last2Top.getStockBean().getC() > last1Bot.getStockBean().getH() && last2Top.getStockBean().getC() > last3Bot.getStockBean().getH();
			boolean b2 = last4Top.getStockBean().getC() > last1Bot.getStockBean().getH() && last4Top.getStockBean().getC() > last3Bot.getStockBean().getH();


			boolean b3 = last1.getDayChgPct()>0 &&
					last1.getC() > last1Bot.getStockBean().getBodyTop() &&
					last1.getC() > last3Bot.getStockBean().getBodyTop();


			boolean bActualBreak = last1.getBodyTop() >= last2Top.getStockBean().getBodyTop() && last1.getH() > last2Top.getH();

			boolean bReadyBreak = last1.getH() <= last2Top.getStockBean().getH() && last1.getC() > last2Top.getL();



			if(b1 && b2 && b3 && isSameBottom && bActualBreak) {

				if(isValidKBody && last2.getH() <= last2Top.getStockBean().getH()) {
					attributes.add(Const.UP+Const.D0+"破小W");

				}else{
					attributes.add(Const.UP+"破小W");

				}


			}

			if(b1 && b2 && b3 && isSameBottom && bReadyBreak) {
				attributes.add(Const.WAIT+Const.UP+"破小W");
			}


			//adding other attributes about the W pattern
			if(!attributes.isEmpty() && isSameTop && daysDiffInTop >10) {
				attributes.add("平頂");
			}

			if(!attributes.isEmpty() && isSameBottom && daysDiffInBottom >10) {
				attributes.add("平底");
			}

//				double toleranceRatio = 0.03;
			double diff = (last1Bot.getL() - last3Bot.getL())/last3Bot.getL();
			if(!attributes.isEmpty() && diff >= DEVIATION) {
				attributes.add("LH");//lh
			}else if(!attributes.isEmpty() && diff <= -DEVIATION) {
				attributes.add("HL");//hl
			}

		}

		return attributes;
		}


}
