package com.sjm.test.yahdata.analy.wavepattern;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WavePoint;

import java.util.*;

public class DownBreakPattern extends BaseWavePattern {

	@Override
	public Set<String> find(List<StockBean> stockList, List<WavePoint> sortedTopList, List<WavePoint> sortedBotList) {
		List<WavePoint> topbotList = new ArrayList<>();
		topbotList.addAll(sortedTopList);
		topbotList.addAll(sortedBotList);

//		List<WavePoint> sortedTopBotList = topbotList.stream()
//				.sorted(Comparator.comparing(WavePoint::getDateInt))
//				.collect(Collectors.toList());

		List<WavePoint> sortedBotLowestList = sortedBotList.stream()
				.sorted(Comparator.comparing(WavePoint::getL).reversed())
				.toList();

		int firstElementDateInt = sortedBotLowestList.getFirst().getDateInt();

		List<WavePoint> sortedBotLList = sortedBotLowestList.stream()
				.filter(wp -> wp.getDateInt()>=firstElementDateInt)
				.toList();


		Set<String> msg = new LinkedHashSet<>();
		if (sortedBotLList.size() < 4) {
			return msg;
		}
		WavePoint last1Bot = sortedBotList.getLast();
		WavePoint last1Top = sortedTopList.getLast();

		StockBean last1 = stockList.getLast();
		StockBean last2 = stockList.get(stockList.size()-2);
		StockBean last3 = stockList.get(stockList.size()-3);

		if( last1.getH() > last1Top.getH() || last1.getL() > last1Bot.getStockBean().getBodyTop()){
			return msg;
		}

		// Example usage of findWavePointWithClosePriceGreaterThanH
		StockBean exampleStockBean = stockList.get(stockList.size() - 1); // Assuming you want to check the last stock bean
//		StockBean last1 = stockList.getLast();


		Optional<WavePoint> wavePoint = findWavePointWithClosePriceLowerThanL(exampleStockBean, sortedBotLList);

		if (wavePoint.isPresent()) {
			WavePoint wp = wavePoint.get();

			boolean isDwBreakD0 = last3.getL()> wp.getL() &&
					last2.getL() > wp.getL() &&
					last1.getL() < wp.getL() &&
					last1.getBodyBottom() < wp.getStockBean().getBodyBottom() &&
					!last1.isRiseToday();

			String txt = Const.DOWN+"破" + wp.getDate() + "底";
			if(isDwBreakD0){
				txt = Const.DOWN+Const.D0+"破" + wp.getDate() + "底";
			}
			msg.add(txt);

//			msg.add("大于第" + (sortedTopHList.indexOf(wp) + 1) + "个元素 (" + wp.getH() + ")");
		} else {
			int minIndex = findMinPosition(sortedBotLList);
			WavePoint minWavePoint = sortedBotLList.get(minIndex);

			boolean isDwBreakD0 = last3.getL()> minWavePoint.getL() &&
					last2.getL() > minWavePoint.getL() &&
					last1.getL() < minWavePoint.getL() &&
					last1.getBodyBottom() < minWavePoint.getStockBean().getBodyBottom() &&
					!last1.isRiseToday();

			String txt = "All"+Const.DOWN+"破多Bot，之前最小的底在" + minWavePoint.getDate();
			if(isDwBreakD0){
				txt = "All"+Const.DOWN+Const.D0+"破多Bot，之前最小的底在" + minWavePoint.getDate();
			}
			msg.add(txt);


		}

		return msg;
	}

	private Optional<WavePoint> findWavePointWithClosePriceLowerThanL(StockBean stockBean, List<WavePoint> sortedBotLList) {
		for (int i = 0; i < sortedBotLList.size(); i++) {
			WavePoint currentWavePoint = sortedBotLList.get(i);
			if (stockBean.getC() > currentWavePoint.getL()) {
				if(i>0) {
					WavePoint prevWavePoint = sortedBotLList.get(i - 1);
					return Optional.of(prevWavePoint);
				}else{
					return Optional.of(currentWavePoint);
				}

			}
		}
		return Optional.empty();
	}

//	public int findMinPosition(List<WavePoint> numbers) {
//		if (numbers == null || numbers.isEmpty()) {
//			throw new IllegalArgumentException("列表不能为空");
//		}
//
//		int minIndex = 0;
//		double minValue = numbers.get(0).getL();
//
//		for (int i = 1; i < numbers.size(); i++) {
//			if (numbers.get(i).getL() < minValue) {
//				minValue = numbers.get(i).getL();
//				minIndex = i;
//			}
//		}
//
//		return minIndex;
//	}
}

