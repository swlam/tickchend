package com.sjm.test.yahdata.analy.wavepattern;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.KPatternConst;
import com.sjm.test.yahdata.analy.conts.type.KBodyType;
import com.sjm.test.yahdata.analy.conts.type.WaveType;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WavePoint;
import com.sjm.test.yahdata.analy.ta.KHelper;

public class BottomReversalWavePattern extends BaseWavePattern {

	@Override
	public Set<String> find(List<StockBean> stockList, List<WavePoint> sortedTopList, List<WavePoint> sortedBotList) {

		List<WavePoint> topbotList = new ArrayList<WavePoint>();
		topbotList.addAll(sortedTopList);
		topbotList.addAll(sortedBotList);
		
		List<WavePoint> sortedTopBotList = topbotList.stream().sorted(Comparator.comparing(e -> e.getDateInt())).collect(Collectors.toList());
//		sortedTopBotList = this.distinctSortedTopBotList(sortedTopBotList);
		return findBottomReversal(stockList, sortedTopBotList);
	}

	
	
	public Set<String> findBottomReversal(List<StockBean> stockList, List<WavePoint> sortedTopBotList){
		Set<String> msg = new LinkedHashSet<String>();
		if(sortedTopBotList.size()<3 )
			return msg;
		StockBean last = stockList.get(stockList.size()-1);
		
		WavePoint wpLast1 = sortedTopBotList.get(sortedTopBotList.size() - 1);
		WavePoint wpLast2 = sortedTopBotList.get(sortedTopBotList.size() - 2);
		WavePoint wpLast3 = sortedTopBotList.get(sortedTopBotList.size() - 3);
		
//		WavePoint minByL = sortedTopBotList.stream()
//			      .min(Comparator.comparing(WavePoint::getL))
//			      .orElseThrow(NoSuchElementException::new);
		boolean isBigBodyDark = (KHelper.getBodySize(stockList) >= KBodyType.GENERAL.getValue() && KHelper.isBearishCandle(last));
		
		boolean condition1 = WaveType.BOT.equals(wpLast3.getType())
				&& WaveType.TOP.equals(wpLast2.getType()) && WaveType.BOT.equals(wpLast1.getType())
				&& wpLast1.getStockBean().getBodyTop() < wpLast3.getL()
				&& last.getC() >= wpLast3.getStockBean().getBodyTop()
				&& !isBigBodyDark;
		boolean condition2 = WaveType.TOP.equals(wpLast3.getType())
				&& WaveType.BOT.equals(wpLast2.getType())
				&& WaveType.BOT.equals(wpLast1.getType()) 
				&& wpLast1.getStockBean().getBodyTop() < wpLast2.getL()
				&& last.getC() >= wpLast2.getStockBean().getBodyTop()
				&& !isBigBodyDark;
		
		
		String confirmDate = last.getTxnDate(); //init the date
		
		boolean isHit = false;
		String extraMsg = "";
		
		WavePoint prevBot = null;
		if( condition1 ) {
			prevBot = wpLast3;
		}else if( condition2 ) {
			prevBot = wpLast2;
		}
		
		
		if( condition1 || condition2) 
		{
			
			List<WavePoint> sortedTopList = sortedTopBotList.stream().filter(x->x.getType()==WaveType.TOP)
					.sorted(Comparator.comparing(e -> e.getDateInt())).collect(Collectors.toList());

			WavePoint lastTop1 = sortedTopList.get(sortedTopList.size()-1);
			
			if(last.getC() > prevBot.getStockBean().getBodyBottom()) //&& minByL.getDateInt() == wpLast1.getDateInt())
			{
				List<StockBean> subList = StreamTransformHelper.subListWithEndElement(stockList, wpLast1.getDate(), last.getTxnDate());
				
				//e.g. 100 + (110-100)/2 
				double achieveBodyLevel = prevBot.getStockBean().getBodyBottom() + Math.abs(prevBot.getStockBean().getBodyTop() - prevBot.getStockBean().getBodyBottom()) / 2.0;

				for(int i=1; i<subList.size(); i++) 
				{
					StockBean elemt = subList.get(i);
					if(elemt.getC() >= achieveBodyLevel) {
						confirmDate = elemt.getTxnDate(); //first confirmDate
						
						boolean result = this.findAchiveBodyLevelRatio(subList, prevBot.getStockBean(), elemt, last.getTxnDate());
						if(result)
						{
							isHit = true;
							if(last.getTxnDateInt() == elemt.getTxnDateInt())
								extraMsg= "D0";

							if(last.getTxnDateInt() > elemt.getTxnDateInt()+1 && last.getTxnDateInt() < elemt.getTxnDateInt()+5)
								extraMsg= "D(1-5)";
						
							//find  max high
							StockBean highestSk = StreamTransformHelper.findMaxHighStock(subList);
							if(highestSk.getH() > lastTop1.getStockBean().getH())
								extraMsg += "(回)";
							
							break;
						}else 
							continue;
					}
				}
			}
		}
		
		if(isHit) {	
			msg.add(KPatternConst.KP_BOTTOM_REVERSAL+extraMsg);
		}
		return msg;
	}
	
	private boolean findAchiveBodyLevelRatio(List<StockBean> stockList, StockBean botStock, StockBean firstConfirmedStock, String currentDate) {
		List<StockBean> subList = StreamTransformHelper.subListWithEndElement(stockList, firstConfirmedStock.getTxnDate(), currentDate);
//		long count = subList.stream()
//		            .filter(x -> x.getBodyBottom() >= achiveBodyLevel)
//		            .count();

		long count = subList.stream()
	            .filter(x -> x.getBodyBottom() > firstConfirmedStock.getBodyBottom())
	            .count();
		
		StockBean lastStock = subList.get(subList.size()-1);
		double ratio = (double) count / (double)subList.size();
		
		
		boolean isEngouthVol = false;
		if(Const.IS_INTRADAY) {
			isEngouthVol=true;
		}else {		
			isEngouthVol = ((double)firstConfirmedStock.getVolume() / (double)botStock.getVolume() >= 1) 
				|| firstConfirmedStock.getDayVolumeChgPct() > 1.2;
		}
		
		if(isEngouthVol && ratio >=0.7 && lastStock.getBodyBottom()> firstConfirmedStock.getBodyBottom())
			return true;
		
		return false;
	}
}
