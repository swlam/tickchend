package com.sjm.test.yahdata.analy.wavepattern;

import java.util.*;
import java.util.stream.Collectors;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.type.WaveType;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WavePoint;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.helper.VolumePriceStructureHelper;

public class DownBreakBotWavePattern extends BaseWavePattern {

	@Override
	public Set<String> find(List<StockBean> stockList, List<WavePoint> sortedTopList, List<WavePoint> sortedBotList) {

		List<WavePoint> topbotList = new ArrayList<WavePoint>();
		topbotList.addAll(sortedTopList);
		topbotList.addAll(sortedBotList);

		List<WavePoint> sortedTopBotList = topbotList.stream().sorted(Comparator.comparing(e -> e.getDateInt())).collect(Collectors.toList());
//		sortedTopBotList = this.distinctSortedTopBotList(sortedTopBotList);
		return findDownBreakBot(stockList, sortedTopBotList);
	}

	public Set<String> findDownBreakBot(List<StockBean> stockList, List<WavePoint> sortedTopBotList){
		Set<String> msg = new LinkedHashSet<String>();
		if(sortedTopBotList.size()<4 )
			return msg;

		StockBean last1 = stockList.get(stockList.size()-1);
		StockBean last2 = stockList.get(stockList.size()-2);

		WavePoint wpLast1 = sortedTopBotList.get(sortedTopBotList.size() - 1);
		WavePoint wpLast2 = sortedTopBotList.get(sortedTopBotList.size() - 2);
		WavePoint wpLast3 = sortedTopBotList.get(sortedTopBotList.size() - 3);
		WavePoint wpLast4 = sortedTopBotList.get(sortedTopBotList.size() - 4);

		WavePoint wpTopLast1 = null;
		WavePoint wpTopLast2 = null;

		WavePoint wpBotLast1 = null;
		WavePoint wpBotLast2 = null;

		if(WaveType.BOT.equals(wpLast2.getType()) && WaveType.TOP.equals(wpLast1.getType())){
			wpBotLast1 = wpLast2;
			wpTopLast1 = wpLast1;

			wpBotLast2 = wpLast4;
			wpTopLast2 = wpLast3;

		}else if(WaveType.TOP.equals(wpLast2.getType()) && WaveType.BOT.equals(wpLast1.getType())){
			wpBotLast1 = wpLast1;
			wpTopLast1 = wpLast2;

			wpBotLast2 = wpLast3;
			wpTopLast2 = wpLast4;
		}else{
			return msg;
		}




//		boolean bMa = last1.getBodyTop() < last1.getPriceSma().getMa20() && last1.getPriceSma().getMa20() < last1.getPriceSma().getMa50()
//				&& last1.getBodyTop() < last1.getPriceSma().getMa50()
//				&& last2.getPriceSma().getMa20() <= last2.getPriceSma().getMa50();


		//require true
		boolean b1 = last1.getBodyTop() < wpBotLast1.getH() && last1.getL() > wpBotLast1.getL() &&
		!last1.isRiseToday();
//		boolean b2 = last2.getC() < wpBotLast1.getStockBean().getBodyTop() && last2.getC() > wpBotLast1.getL();

		boolean isBreakBotLowOnce = this.isHitLowestPriceButRebounded(stockList, wpBotLast1);
		boolean isVolIncrease = last1.getDayVolumeChgPct()>1 && last2.getDayVolumeChgPct() > 1;

		if( b1)
		{
			String readyTxt = this.getDownReadyMessage(stockList);

			String txt = Const.WAIT+Const.DOWN+"前BOT" + readyTxt;
			if( isBreakBotLowOnce ){
				txt +="#";
			}
			if(!Const.IS_INTRADAY && isVolIncrease){
				txt +="+";
			}
			msg.add(txt);
		}


		boolean isBadSign = this.isBadSignForDownBreak(last1, last2);	//last1.getH() > last2.getH() && last1.getBodyTop() > last2.getBodyTop() && last1.getC() > last1.getO();


		//boolean about wave
		boolean bWaveDwnDw = wpTopLast1.getL()>wpBotLast1.getL() && wpTopLast1.getH()>wpBotLast1.getH();
		boolean bAnother = last1.getH()<wpTopLast1.getStockBean().getBodyBottom();

		boolean  bWave = bWaveDwnDw?(bWaveDwnDw && bAnother): true;


		boolean bbD0 = last2.getL()> wpBotLast1.getL() && last1.getC() <= wpBotLast1.getL() && last1.isRiseToday()==false;
		boolean bb = last2.getL()<= wpBotLast1.getL() && last1.getC() <= wpBotLast1.getL();

		boolean isVolEngouht = Const.IS_INTRADAY ?(last1.getDayVolumeChgPct() > 0.5):(last1.getDayVolumeChgPct() > 0.8);


		boolean isDwBreakD0 = bWave && bbD0 && isVolEngouht; //==> D0
		boolean isDwBreak = bWave  && bb  && !isBadSign; //==> Up前BOT
		boolean isDwBreakWithAlert = bWave  && bb && isBadSign; //==> Up前BOT(小心)


		if(isDwBreakD0) {
			String txt = Const.DOWN+Const.D0+ "前BOT";
			if(last1.getC() <= wpBotLast2.getL() && last2.getL() > wpBotLast2.getL()){
				txt = Const.DOWN+Const.D0+"前BOT-2";
				if(wpBotLast2.getL() < wpBotLast1.getL()){
					txt = Const.DOWN+Const.D0+"前BOT-2低高";
				}
			}
			msg.add(txt);
		}if(isDwBreak)
			msg.add(Const.DOWN+"前BOT");
		if(isDwBreakWithAlert)
			msg.add(Const.DOWN+"前BOT(小心反轉)");


		return msg;
	}
	
	
//	public Set<String> findDownBreakBot(List<StockBean> stockList, List<WavePoint> sortedTopBotList){
//		Set<String> msg = new LinkedHashSet<String>();
//		if(sortedTopBotList.size()<3 ||sortedTopBotList.size()<3)
//			return msg;
//
//		StockBean last1 = stockList.get(stockList.size()-1);
//		StockBean last2 = stockList.get(stockList.size()-2);
//
//		WavePoint wpLast1 = sortedTopBotList.get(sortedTopBotList.size() - 1);
//		WavePoint wpLast2 = sortedTopBotList.get(sortedTopBotList.size() - 2);
//
//
//		boolean b = WaveType.TOP.equals(wpLast1.getType());
//		if(b) return msg;
//
//
//
////		int wpLast1Idx= StreamTransformHelper.findIndex(stockList, wpLast1.getDate());
////		List<StockBean> tailSkList = stockList.subList(wpLast1Idx+1, stockList.size());
////		StockBean tailMaxH = StreamTransformHelper.findMaxHighStock(tailSkList);
////		StockBean tailMinL = StreamTransformHelper.findMinLowStock(tailSkList);
//
//		boolean bMa = last1.getBodyTop() < last1.getPriceSma().getMa20() && last1.getPriceSma().getMa20() < last1.getPriceSma().getMa50()
//				&& last1.getBodyTop() < last1.getPriceSma().getMa50()
//				&& last2.getPriceSma().getMa20() <= last2.getPriceSma().getMa50();
//
//
//		//require true
//		boolean b1 = last1.getC() < wpLast1.getStockBean().getBodyTop() && last1.getC() > wpLast1.getL();
////		boolean b2 = last2.getC() < wpLast1.getStockBean().getBodyTop() && last2.getC() > wpLast1.getL();
//
//		boolean isBreakBotLowOnce = this.isHitLowestPriceButRebounded(stockList, wpLast1);
//
//		if(bMa && b1){
//
//			String txt = "待Dw前BOT";
//			if( isBreakBotLowOnce ){
//				txt +="*";
//			}
//			msg.add(txt);
//		}
//
//
//		boolean isBadSign = this.isBadSignForDownBreak(last1, last2);	//last1.getH() > last2.getH() && last1.getBodyTop() > last2.getBodyTop() && last1.getC() > last1.getO();
//
//
//		//boolean about wave
//		boolean bWaveDwnDw = wpLast2.getL()>wpLast1.getL() && wpLast2.getH()>wpLast1.getH();
//		boolean bAnother = last1.getH()<wpLast2.getStockBean().getBodyBottom();
//
//		boolean  bWave = bWaveDwnDw?(bWaveDwnDw && bAnother): true;
//
//
//		boolean bbD0 = last2.getL()> wpLast1.getL() && last1.getC() <= wpLast1.getL() && last1.isRiseToday()==false;
//		boolean bb = last2.getL()<= wpLast1.getL() && last1.getC() <= wpLast1.getL();
//
//		boolean isVolEngouht = Const.IS_INTRADAY ?(last1.getDayVolumeChgPct() > 0.5):(last1.getDayVolumeChgPct() > 1.2);
//
//
//		boolean isDwBreakD0 = bWave && bMa && bbD0 && isVolEngouht; //==> D0
//		boolean isDwBreak = bWave && bMa && bb  && !isBadSign; //==> Up前BOT
//		boolean isDwBreakWithAlert = bWave && bMa && bb && isBadSign; //==> Up前BOT(小心)
//
//
//		if(isDwBreakD0)
//			msg.add("Dw前BOT(D0)");
//		if(isDwBreak)
//			msg.add("Dw前BOT");
//		if(isDwBreakWithAlert)
//			msg.add("Dw前BOT(小心反轉)");
//
//
//		return msg;
//	}
}
