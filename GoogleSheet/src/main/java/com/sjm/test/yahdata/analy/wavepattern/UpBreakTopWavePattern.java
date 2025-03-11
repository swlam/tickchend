package com.sjm.test.yahdata.analy.wavepattern;

import java.util.*;
import java.util.stream.Collectors;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.type.WaveType;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WavePoint;
import com.sjm.test.yahdata.analy.ta.helper.VolumePriceStructureHelper;

public class UpBreakTopWavePattern extends BaseWavePattern {

	@Override
	public Set<String> find(List<StockBean> stockList, List<WavePoint> sortedTopList, List<WavePoint> sortedBotList) {

//		List<WavePoint> topbotList = new ArrayList<WavePoint>();
//		topbotList.addAll(sortedTopList);
//		topbotList.addAll(sortedBotList);
		
//		List<WavePoint> sortedTopBotList = topbotList.stream().sorted(Comparator.comparing(e -> e.getDateInt())).collect(Collectors.toList());
//		sortedTopBotList = this.distinctSortedTopBotList(sortedTopBotList);
		return findUpBreakTop(stockList, sortedTopList, sortedBotList);
	}

	
	
	public Set<String> findUpBreakTop(List<StockBean> stockList, List<WavePoint> sortedTopList, List<WavePoint> sortedBotList){
		List<WavePoint> topbotList = new ArrayList<WavePoint>();
		topbotList.addAll(sortedTopList);
		topbotList.addAll(sortedBotList);
		List<WavePoint> sortedTopBotList = topbotList.stream().sorted(Comparator.comparing(e -> e.getDateInt())).collect(Collectors.toList());

		Set<String> msg = new LinkedHashSet<String>();
		if(sortedTopBotList.size()<4 )
			return msg;
				
		StockBean last1 = stockList.get(stockList.size()-1);
		StockBean last2 = stockList.get(stockList.size()-2);
		StockBean last3 = stockList.get(stockList.size()-3);

		WavePoint wpLast1 = sortedTopBotList.get(sortedTopBotList.size() - 1);
		WavePoint wpLast2 = sortedTopBotList.get(sortedTopBotList.size() - 2);
		WavePoint wpLast3 = sortedTopBotList.get(sortedTopBotList.size() - 3);
		WavePoint wpLast4 = sortedTopBotList.get(sortedTopBotList.size() - 4);

		WavePoint wpTopLast1 = null;
		WavePoint wpTopLast2 = null;

		WavePoint wpBotLast1 = null;
		WavePoint wpBotLast2 = null;

		if(WaveType.BOT.equals(wpLast2.getType()) && WaveType.TOP.equals(wpLast1.getType())){
			wpTopLast1 = wpLast1;
			wpBotLast1 = wpLast2;

			wpTopLast2 = wpLast3;
			wpBotLast2 = wpLast4;

		}else if(WaveType.TOP.equals(wpLast2.getType()) && WaveType.BOT.equals(wpLast1.getType())){
			wpBotLast1 = wpLast1;
			wpTopLast1 = wpLast2;

			wpBotLast2 = wpLast3;
			wpTopLast2 = wpLast4;
		}else{
			return msg;
		}

//		boolean b = WaveType.BOT.equals(wpTopLast1.getType());
//		if(b) return msg;


		//require true
		boolean bReady1 = last1.getBodyBottom() > wpTopLast1.getL() &&
				last1.getC() <= wpTopLast1.getStockBean().getBodyTop() &&
				last1.getH() <= wpTopLast1.getH() &&
				last2.getH() < wpTopLast1.getH() &&
				last2.getL() < wpTopLast1.getL() &&
				last1.isRiseToday();

		boolean isBreakTopHighOnce = this.isHitHighestPriceButPulledBack(stockList, wpTopLast1);
		boolean isVolIncrease = last1.getDayVolumeChgPct()>1 && last2.getDayVolumeChgPct() > 1;

		if(bReady1){
			String readyTxt = this.getUpReadyMessage(stockList);
			String txt = Const.WAIT+Const.UP+"前TOP"+readyTxt;
			if( isBreakTopHighOnce ){
				txt +="#";
			}

			if(!Const.IS_INTRADAY &&isVolIncrease){
				txt +="_Vol+";
			}
			msg.add(txt);
		}
		
		
		boolean isBadSign = this.isBadSignForUpBreak(last1, last2);

		
		//boolean about wave
//		boolean bWaveUpnUp = wpBotLast1.getH()<wpTopLast1.getH() && wpBotLast1.getL()<wpTopLast1.getL();
//		boolean bAnother = last1.getL() >wpBotLast1.getStockBean().getBodyTop();
//
//		boolean  bWave = bWaveUpnUp?(bWaveUpnUp && bAnother): true;
		
		boolean bbD0 = last3.getH() < wpTopLast1.getH() &&
				last2.getH() < wpTopLast1.getH() &&
				last1.getH() > wpTopLast1.getH() &&
				last1.getBodyTop() >= wpTopLast1.getStockBean().getBodyTop() &&
				last1.isRiseToday();

		boolean isUpBreakD1 = last3.getH()< wpTopLast1.getH() &&
				last2.getH() > wpTopLast1.getH() &&
				last2.getC() > wpTopLast1.getStockBean().getBodyTop() &&
				last1.getC() > wpTopLast1.getStockBean().getBodyTop() &&
				last2.isRiseToday();

		boolean bb = last2.getH()>= wpTopLast1.getH() && last1.getC() >= wpTopLast1.getH() ;

		boolean isVolEnough = Const.IS_INTRADAY ?(last1.getDayVolumeChgPct() > 0.5):(last1.getDayVolumeChgPct() >= 1);

		boolean isUpBreakD0 = bbD0 && isVolEnough; //==> D0
//		boolean isUpBreakD1 = bWave && bMa && bbD1; //==> D1

		boolean isUpBreak = bb  && !isBadSign; //==> Up前TOP
		boolean isUpBreakWithAlert = bb && isBadSign; //==> Up前TOP(小心)


		if(isUpBreakD1){
			String txt = Const.UP+Const.D1+"前TOP";
			if(wpTopLast2.getH() > wpTopLast1.getH() &&
				(last1.getC() >= wpTopLast2.getH() || last2.getH() < wpTopLast2.getH()))
			{
				txt = Const.UP+Const.D1+"前TOP-2";
//				if(wpTopLast2.getH() > wpTopLast1.getH()){
//					txt = Const.UP+Const.D1+"前TOP-2高低";
//				}
			}
			msg.add(txt);
		}else if(isUpBreakD0){
			String txt = Const.UP+Const.D0+"前TOP";
			if( (last1.getC() >= wpTopLast2.getH() || last2.getH() >= wpTopLast2.getH()) && wpTopLast2.getH() > wpTopLast1.getH())
			{
				txt = Const.UP+Const.D0+"前TOP-2";
//				if(wpTopLast2.getH() > wpTopLast1.getH()){
//					txt = Const.UP+Const.D0+"前TOP-2高低";
//				}
			}
			if(last1.getDayVolumeChgPct()>2.0) {
				txt += Const.BIG_VOL;
			}
			msg.add(txt);
		}


		if(isUpBreak) {
			msg.add(Const.UP + "前TOP");
			msg.add(findUpBreakDatePosition(sortedTopList, last1.getC()));
		}
		if(isUpBreakWithAlert)
			msg.add(Const.UP+"前TOP(小心反轉)");
		
		

		return msg;
	}



	private String findUpBreakDatePosition(List<WavePoint> wavePoints, double inputPrice) {
		int size = wavePoints.size();
		boolean found = false;
		WavePoint currentNumber = null;
		for (int i = size - 1; i >= 0; i--) {
			currentNumber = wavePoints.get(i);

			if (inputPrice < currentNumber.getH()) {
//				System.out.println("小于第" + (i + 1) + "个元素 (" + currentNumber + ")");
				found = true;
				break;
			}
		}

		String rtnMsg = "";
		if (!found) {
			int maxIdz = findMaxPosition(wavePoints);
			WavePoint maxWp = wavePoints.get(maxIdz);
			rtnMsg = Const.UP+"前頂("+maxWp.getDate()+")";
			//System.out.println("已经是最大的数字，上一个最大的数字位置在第" + size + "个元素 (" + stockList.get(maxIdz) + ")");
		}else{
			rtnMsg = "小於頂("+currentNumber.getStockBean().getTxnDate()+")";
		}
		return rtnMsg;
	}
}
