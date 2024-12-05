package com.sjm.test.yahdata.analy.wavepattern;

import java.util.*;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.type.WaveType;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WavePoint;
import com.sjm.test.yahdata.analy.ta.helper.VolumePriceStructureHelper;

public abstract class BaseWavePattern {
	public abstract Set<String> find(List<StockBean> stockList, List<WavePoint> sortedTopList, List<WavePoint> sortedBotList);

	//checkTopValueLessThanOthersLow
	public boolean isTopValueHigherThanOthersLowPrice(double bodyTopValue, List<WavePoint> stockDataList) {
		for (WavePoint stockData : stockDataList) {
			if (bodyTopValue <= stockData.getL()) {
				return false;
			}
		}
		return true;
	}

	public boolean isBottomValueLowerThanOthersHightPrice(double bodyBotValue, List<WavePoint> stockDataList) {
		for (WavePoint stockData : stockDataList) {
			if (bodyBotValue >= stockData.getH()) {
				return false;
			}
		}
		return true;
	}


	public boolean isBadSignForUpBreak(StockBean last1, StockBean last2){
		return last1.getL() < last2.getL() && last1.getBodyBottom() < last2.getBodyBottom() && last1.getC() < last1.getO();

	}

	public boolean isBadSignForDownBreak(StockBean last1, StockBean last2){
		return last1.getH() > last2.getH() && last1.getBodyTop() > last2.getBodyTop() && last1.getC() > last1.getO();

	}


	public boolean isHitHighestPriceButPulledBack(List<StockBean> stockList, WavePoint fromLastWp){
		if(WaveType.TOP != fromLastWp.getType())
			return false;

		StockBean tailMaxH = this.findHighest(stockList, fromLastWp);
        return tailMaxH.getH() > fromLastWp.getH();
    }

	public boolean isHitLowestPriceButRebounded(List<StockBean> stockList, WavePoint fromLastWp){
		if(WaveType.BOT != fromLastWp.getType())
			return false;

		StockBean tailMinL = this.findLowest(stockList, fromLastWp);
		return tailMinL.getL() < fromLastWp.getL();
	}


	public StockBean findHighest(List<StockBean> stockList, WavePoint fromLastWp){
		int wpLast1Idx= StreamTransformHelper.findIndex(stockList, fromLastWp.getDate());
		List<StockBean> tailSkList = stockList.subList(wpLast1Idx+1, stockList.size());
        //		StockBean tailMinL = StreamTransformHelper.findMinLowStock(tailSkList);
		return StreamTransformHelper.findMaxHighStock(tailSkList);
	}

	public StockBean findLowest(List<StockBean> stockList, WavePoint fromLastWp){
		int wpLast1Idx= StreamTransformHelper.findIndex(stockList, fromLastWp.getDate());
		List<StockBean> tailSkList = stockList.subList(wpLast1Idx+1, stockList.size());
//		StockBean tailMaxH = StreamTransformHelper.findMaxHighStock(tailSkList);
		StockBean tailMinL = StreamTransformHelper.findMinLowStock(tailSkList);
		return tailMinL;
	}


	public String getUpReadyMessage(List<StockBean> stockList){
		String priceStatus = VolumePriceStructureHelper.getPriceStatus(stockList);
		List<String> validValues = Arrays.asList(VolumePriceStructureHelper.WEEKLY_H, VolumePriceStructureHelper.WEEKLY_H_CLOSE, VolumePriceStructureHelper.MONTHLY_H, VolumePriceStructureHelper.MONTHLY_H_CLOSE, VolumePriceStructureHelper.DAY50_H, VolumePriceStructureHelper.DAY50_H_CLOSE);
		String readyTxt = validValues.stream().anyMatch(validValue -> validValue.equals(priceStatus)) ? "_(準備)" : "";

		List<StockBean> prevDayStockList = stockList.subList(0, stockList.size()-1);
		String prevDayPriceStatus = VolumePriceStructureHelper.getPriceStatus(prevDayStockList);
		List<String> prevDayValidValues = Arrays.asList(VolumePriceStructureHelper.WEEKLY_H, VolumePriceStructureHelper.WEEKLY_H_CLOSE, VolumePriceStructureHelper.MONTHLY_H, VolumePriceStructureHelper.MONTHLY_H_CLOSE, VolumePriceStructureHelper.DAY50_H, VolumePriceStructureHelper.DAY50_H_CLOSE);
		String prevDayReadyTxt = validValues.stream().anyMatch(validValue -> validValue.equals(prevDayPriceStatus)) ? "_(準備)" : "";

		if("".equalsIgnoreCase(prevDayReadyTxt) && !"".equalsIgnoreCase(readyTxt)){
			return readyTxt;
		}
		return "";

	}


	public String getDownReadyMessage(List<StockBean> stockList){
		String priceStatus = VolumePriceStructureHelper.getPriceStatus(stockList);
		List<String> validValues = Arrays.asList(VolumePriceStructureHelper.WEEKLY_L, VolumePriceStructureHelper.WEEKLY_L_CLOSE, VolumePriceStructureHelper.MONTHLY_L, VolumePriceStructureHelper.MONTHLY_L_CLOSE, VolumePriceStructureHelper.DAY50_L, VolumePriceStructureHelper.DAY50_L_CLOSE);
		String readyTxt = validValues.stream().anyMatch(validValue -> validValue.equals(priceStatus)) ? "_(準備)" : "";


		List<StockBean> prevDayStockList = stockList.subList(0, stockList.size()-1);
		String prevDayPriceStatus = VolumePriceStructureHelper.getPriceStatus(prevDayStockList);
		List<String> prevDayValidValues = Arrays.asList(VolumePriceStructureHelper.WEEKLY_L, VolumePriceStructureHelper.WEEKLY_L_CLOSE, VolumePriceStructureHelper.MONTHLY_L, VolumePriceStructureHelper.MONTHLY_L_CLOSE, VolumePriceStructureHelper.DAY50_L, VolumePriceStructureHelper.DAY50_L_CLOSE);
		String prevDayReadyTxt = validValues.stream().anyMatch(validValue -> validValue.equals(prevDayPriceStatus)) ? "_(準備)" : "";

		if("".equalsIgnoreCase(prevDayReadyTxt) && !"".equalsIgnoreCase(readyTxt)){
			return readyTxt;
		}
		return "";

	}


//	public static List<WavePoint> distinctSortedTopBotList(List<WavePoint> sortedTopBotList){
//		List<WavePoint> returnSortedTopBotList = new ArrayList<WavePoint>();
//		if(sortedTopBotList.isEmpty())
//			return returnSortedTopBotList;
//
//		WavePoint latestWavePoint = sortedTopBotList.get(sortedTopBotList.size()-1);
//		final boolean isTopBegin;
//		if(latestWavePoint.getType()==WaveType.TOP){
//			isTopBegin = true;
//		}else{
//			isTopBegin = false;
//		}
//
//
//		returnSortedTopBotList.add(latestWavePoint);
//
//
//
//		// Find the next TOP after the latest BOT
//		Optional<WavePoint> nextTB1 = sortedTopBotList.stream()
//				.filter(wavePoint -> isTopBegin?(WaveType.BOT == wavePoint.getType()):(WaveType.TOP == wavePoint.getType()) )
//				.filter(wavePoint -> wavePoint.getDateInt()<latestWavePoint.getDateInt())
//				.max(Comparator.comparing(WavePoint::getDateInt));
//
//
//
//
//		if (nextTB1.isPresent()) {
//			WavePoint nextTB1Wp = nextTB1.get();
//			returnSortedTopBotList.add(nextTB1Wp);
//
//
//			// Find the next BOT after the found TOP
//			Optional<WavePoint> nextTB2 = sortedTopBotList.stream()
////						.filter(wavePoint -> WaveType.BOT == wavePoint.getType())
//					.filter(wavePoint -> isTopBegin?(WaveType.TOP == wavePoint.getType()):(WaveType.BOT == wavePoint.getType()) )
//					.filter(wavePoint -> wavePoint.getDateInt()<nextTB1Wp.getDateInt())
//					//.filter(wavePoint -> wavePoint.getDate().isAfter(nextTopWavePoint.getDate()))
//					.max(Comparator.comparing(WavePoint::getDateInt));
//
//
//			if (nextTB2.isPresent()) {
//
//				WavePoint nextTB2Wp = nextTB2.get();
//				returnSortedTopBotList.add(nextTB2Wp);
//
//				Optional<WavePoint> nextTB3 = sortedTopBotList.stream()
////						.filter(wavePoint -> WaveType.BOT == wavePoint.getType())
//						.filter(wavePoint -> isTopBegin?(WaveType.BOT == wavePoint.getType()):(WaveType.TOP == wavePoint.getType()) )
//						.filter(wavePoint -> wavePoint.getDateInt()<nextTB2Wp.getDateInt())
//						//.filter(wavePoint -> wavePoint.getDate().isAfter(nextTopWavePoint.getDate()))
//						.max(Comparator.comparing(WavePoint::getDateInt));
//				if (nextTB3.isPresent()) {
//					returnSortedTopBotList.add(nextTB3.get());
//				}
//
//
//			} else {
////				System.out.println("No next BOT found after the next TOP.");
//			}
//		} else {
////			System.out.println("No next TOP found after the latest BOT.");
//		}
//		Collections.reverse(returnSortedTopBotList);
//
//	return returnSortedTopBotList;
//	}
}
