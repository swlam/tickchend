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

		List<WavePoint> topbotList = new ArrayList<WavePoint>();
		topbotList.addAll(sortedTopList);
		topbotList.addAll(sortedBotList);
		
		List<WavePoint> sortedTopBotList = topbotList.stream().sorted(Comparator.comparing(e -> e.getDateInt())).collect(Collectors.toList());
//		sortedTopBotList = this.distinctSortedTopBotList(sortedTopBotList);
		return findUpBreakTop(stockList, sortedTopBotList);
	}

	
	
	public Set<String> findUpBreakTop(List<StockBean> stockList, List<WavePoint> sortedTopBotList){
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

//		boolean b = WaveType.BOT.equals(wpTopLast1.getType());
//		if(b) return msg;




//		boolean bMa = last1.getC() > last1.getPriceSma().getMa20() && last1.getPriceSma().getMa20() > last1.getPriceSma().getMa50()
//				&& last1.getC() > last1.getPriceSma().getMa50()
//				&& last2.getPriceSma().getMa20() >= last2.getPriceSma().getMa50();
		boolean bMa = true;
		//require true
		boolean b1 = last1.getBodyBottom() > wpTopLast1.getL() && last1.getH() < wpTopLast1.getH()
				&& last2.getH() < wpTopLast1.getH() && last1.isRiseToday();

		boolean isVolIncrease = last1.getDayVolumeChgPct()>1 && last2.getDayVolumeChgPct() > 1;
		
		boolean isBreakTopHighOnce = this.isHitHighestPriceButPulledBack(stockList, wpTopLast1);

		if(b1){
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
		boolean bWaveUpnUp = wpBotLast1.getH()<wpTopLast1.getH() && wpBotLast1.getL()<wpTopLast1.getL();
		boolean bAnother = last1.getL() >wpBotLast1.getStockBean().getBodyTop();
		
		boolean  bWave = bWaveUpnUp?(bWaveUpnUp && bAnother): true;
		
		boolean bbD0 = last2.getH()< wpTopLast1.getH() && last1.getC() >= wpTopLast1.getH() && last1.isRiseToday();
		boolean bb = last2.getH()>= wpTopLast1.getH() && last1.getC() >= wpTopLast1.getH() ;


		boolean isVolEnough = Const.IS_INTRADAY ?(last1.getDayVolumeChgPct() > 0.5):(last1.getDayVolumeChgPct() >= 1);
		boolean isUpBreakD0 = bWave && bMa && bbD0 && isVolEnough; //==> D0
		boolean isUpBreak = bWave && bMa && bb  && !isBadSign; //==> Up前TOP
		boolean isUpBreakWithAlert = bWave && bMa && bb && isBadSign; //==> Up前TOP(小心)
		
		if(isUpBreakD0){
			String txt = Const.UP+Const.D0+"前TOP";
			if(last1.getC() >= wpTopLast2.getH() && last2.getH() < wpTopLast2.getH()){
				txt = Const.UP+Const.D0+"前TOP-2";
				if(wpTopLast2.getH() > wpTopLast1.getH()){
					txt = Const.UP+Const.D0+"前TOP-2高低";
				}
			}
			msg.add(txt);
		}
		if(isUpBreak)
			msg.add(Const.UP+"前TOP");
		if(isUpBreakWithAlert)
			msg.add(Const.UP+"前TOP(小心反轉)");
		
		

		return msg;
	}
}
