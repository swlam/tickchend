package com.sjm.test.yahdata.analy.wavepattern;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.type.WaveType;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WavePoint;

import java.util.*;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.LinkedHashSet;

public class UpBreakPattern extends BaseWavePattern {

	@Override
	public Set<String> find(List<StockBean> stockList, List<WavePoint> sortedTopList, List<WavePoint> sortedBotList) {
		List<WavePoint> topbotList = new ArrayList<>();
		topbotList.addAll(sortedTopList);
		topbotList.addAll(sortedBotList);

		List<WavePoint> sortedTopBotList = topbotList.stream()
				.sorted(Comparator.comparing(WavePoint::getDateInt))
				.collect(Collectors.toList());

		List<WavePoint> sortedTopHighestList = sortedTopList.stream()
				.sorted(Comparator.comparing(WavePoint::getH).reversed())
				.toList();

		int firstElementDateInt = sortedTopHighestList.getFirst().getDateInt();

		List<WavePoint> sortedTopHList = sortedTopHighestList.stream()
				.filter(wp -> wp.getDateInt()>=firstElementDateInt)
				.toList();

//		return findUpBreakTop(stockList, sortedTopHList);
//	}
//
//	public Set<String> findUpBreakTop(List<StockBean> stockList, List<WavePoint> sortedTopHList) {
		Set<String> msg = new LinkedHashSet<>();
		if (sortedTopHList.size() < 4) {
			return msg;
		}
		WavePoint last1Bot = sortedBotList.getLast();
		WavePoint last1Top = sortedTopList.getLast();

		StockBean last1 = stockList.getLast();
		StockBean last2 = stockList.get(stockList.size()-2);
		StockBean last3 = stockList.get(stockList.size()-3);


		if( last1.getL() < last1Bot.getL() || last1.getH() < last1Top.getStockBean().getBodyBottom()){
			return msg;
		}

		// Example usage of findWavePointWithClosePriceGreaterThanH
		StockBean exampleStockBean = stockList.get(stockList.size() - 1); // Assuming you want to check the last stock bean
		Optional<WavePoint> wavePoint = findWavePointWithClosePriceGreaterThanH(exampleStockBean, sortedTopHList);



		if (wavePoint.isPresent()) {
			WavePoint wp = wavePoint.get();
//			System.out.println("找到的 WavePoint 对象数据: " + wp);

			String txt = Const.UP+"破" + wp.getDate() + "頂";
			boolean isUpBreakD0 = last3.getH() < wp.getH() &&
					last2.getH() < wp.getH() &&
					last1.getH() > wp.getH() &&
					last1.getBodyTop() > wp.getStockBean().getBodyTop() &&
					last1.isRiseToday();
			if(isUpBreakD0) {
				txt = Const.UP + Const.D0 + "破" + wp.getDate() + "頂";
			}
			msg.add(txt);
		} else {
			int maxIndex = findMaxPosition(sortedTopHList);
			WavePoint maxWavePoint = sortedTopHList.get(maxIndex);
//			System.out.println("已经是最大的数字，上一个最大的数字位置在第" + (maxIndex + 1) + "个元素 (" + maxWavePoint.getH() + ")");
			msg.add("All"+Const.UP+"破多Top，之前最大的頂在" + maxWavePoint.getDate() );
		}

		return msg;
	}

	private Optional<WavePoint> findWavePointWithClosePriceGreaterThanH(StockBean stockBean, List<WavePoint> sortedTopHList) {
		for (int i = 0; i < sortedTopHList.size(); i++) {
			WavePoint currentWavePoint = sortedTopHList.get(i);
			if (stockBean.getC() < currentWavePoint.getH()) {
				return Optional.of(currentWavePoint);
			}
		}
		return Optional.empty();
	}

	public int findMaxPosition(List<WavePoint> numbers) {
		if (numbers == null || numbers.isEmpty()) {
			throw new IllegalArgumentException("列表不能为空");
		}

		int maxIndex = 0;
		double maxValue = numbers.get(0).getH();

		for (int i = 1; i < numbers.size(); i++) {
			if (numbers.get(i).getH() > maxValue) {
				maxValue = numbers.get(i).getH();
				maxIndex = i;
			}
		}

		return maxIndex;
	}
}

