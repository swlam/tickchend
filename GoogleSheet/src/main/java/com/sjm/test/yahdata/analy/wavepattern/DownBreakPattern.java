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

		List<WavePoint> sortedBotLList = sortedBotList.stream()
				.sorted(Comparator.comparing(WavePoint::getL).reversed())
				.toList();

//		return findDownBreakBot(stockList, sortedBotLList);
//	}
//
//	public Set<String> findDownBreakBot(List<StockBean> stockList, List<WavePoint> sortedBotLList) {
		Set<String> msg = new LinkedHashSet<>();
		if (sortedBotLList.size() < 4) {
			return msg;
		}

		WavePoint last1Top = sortedTopList.getLast();
		StockBean last1 = stockList.getLast();

		if( last1.getH() > last1Top.getH()){
			return msg;
		}

		// Example usage of findWavePointWithClosePriceGreaterThanH
		StockBean exampleStockBean = stockList.get(stockList.size() - 1); // Assuming you want to check the last stock bean
		Optional<WavePoint> wavePoint = findWavePointWithClosePriceLowerThanL(exampleStockBean, sortedBotLList);

		if (wavePoint.isPresent()) {
			WavePoint wp = wavePoint.get();
//			System.out.println("找到的 WavePoint 对象数据: " + wp);
			msg.add(Const.DOWN+"破" + wp.getDate() + "底("+ GeneralHelper.to2DecimalPlaces(wp.getL())+")" );

//			msg.add("大于第" + (sortedTopHList.indexOf(wp) + 1) + "个元素 (" + wp.getH() + ")");
		} else {
			int minIndex = findMinPosition(sortedBotLList);
			WavePoint minWavePoint = sortedBotLList.get(minIndex);
//			System.out.println("已经是最大的数字，上一个最大的数字位置在第" + (minIndex + 1) + "个元素 (" + minWavePoint.getH() + ")");
			msg.add("已"+Const.DOWN+"破多Bot，之前最小的底在" + minWavePoint.getDate() );
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

