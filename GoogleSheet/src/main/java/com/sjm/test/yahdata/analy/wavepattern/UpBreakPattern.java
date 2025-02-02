package com.sjm.test.yahdata.analy.wavepattern;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.type.WaveType;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WavePoint;

import java.util.*;
import java.util.stream.Collectors;

public class UpBreakPattern extends BaseWavePattern {

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
				

		
		

		return msg;
	}



	private void findPosition(List<WavePoint> stockList, double inputPrice) {
		int size = stockList.size();
		boolean found = false;
		WavePoint currentNumber = null;
		for (int i = size - 1; i >= 0; i--) {
			currentNumber = stockList.get(i);

			if (inputPrice < currentNumber.getH()) {
//				System.out.println("小于第" + (i + 1) + "个元素 (" + currentNumber + ")");
				found = true;
				break;
			}
		}

		String rtnMsg = "";
		if (!found) {
			int maxIdz = findMaxPosition(stockList);
			rtnMsg = "破過了近期頂";
			//System.out.println("已经是最大的数字，上一个最大的数字位置在第" + size + "个元素 (" + stockList.get(maxIdz) + ")");
		}else{
			rtnMsg = currentNumber.getStockBean().getTxnDate() + "在第" + (size - 1) + "个元素 (" + currentNumber + ")";
		}
	}
}
