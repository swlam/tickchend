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

public class BottomReversalUpwardPattern extends BaseWavePattern {

	@Override
	public Set<String> find(List<StockBean> stockList, List<WavePoint> sortedTopList, List<WavePoint> sortedBotList) {

		List<WavePoint> topbotList = new ArrayList<WavePoint>();
		topbotList.addAll(sortedTopList);
		topbotList.addAll(sortedBotList);
		
		List<WavePoint> sortedTopBotList = topbotList.stream().sorted(Comparator.comparing(e -> e.getDateInt())).collect(Collectors.toList());
//		sortedTopBotList = this.distinctSortedTopBotList(sortedTopBotList);
		return findBottomReversal(stockList, sortedTopBotList);
	}

	
	
	private Set<String> findBottomReversal(List<StockBean> stockList, List<WavePoint> sortedTopBotList){
		Set<String> msg = new LinkedHashSet<String>();
		if(sortedTopBotList.size()<3 )
			return msg;
		StockBean last1 = stockList.get(stockList.size()-1);
		StockBean last2 = stockList.get(stockList.size()-2);
		StockBean last3 = stockList.get(stockList.size()-3);

		WavePoint wpLast1 = sortedTopBotList.getLast();
		WavePoint wpLast2 = sortedTopBotList.get(sortedTopBotList.size() - 2);
		WavePoint wpLast3 = sortedTopBotList.get(sortedTopBotList.size() - 3);
		
//		WavePoint minByL = sortedTopBotList.stream()
//			      .min(Comparator.comparing(WavePoint::getL))
//			      .orElseThrow(NoSuchElementException::new);
		boolean isBigBearishBody = (KHelper.getBodySize(stockList) >= KBodyType.GENERAL.getValue() && KHelper.isBearishCandle(last1));
		
		boolean condition1 = WaveType.BOT.equals(wpLast3.getType())
				&& WaveType.TOP.equals(wpLast2.getType()) &&
				WaveType.BOT.equals(wpLast1.getType()) &&
				wpLast1.getStockBean().getBodyTop() < wpLast3.getL() &&
				last1.getC() > wpLast3.getStockBean().getBodyBottom() &&
				!isBigBearishBody;

		boolean condition2 =
				WaveType.TOP.equals(wpLast1.getType()) &&
				(last1.getC() > wpLast2.getStockBean().getBodyBottom() && last2.getC() > wpLast2.getStockBean().getBodyBottom()) &&
				(last1.getL() < wpLast2.getL() || last2.getL() < wpLast2.getL() || last3.getL() < wpLast2.getL());

		
		String confirmDate = last1.getTxnDate(); //init the date
		
		boolean isHit = false;
		String extraMsg = "";
		
		WavePoint prevBot = null;

		
		if( condition1 )
		{
			prevBot = wpLast3;
			
			List<WavePoint> sortedTopList = sortedTopBotList.stream().filter(x->x.getType()==WaveType.TOP)
					.sorted(Comparator.comparing(e -> e.getDateInt())).collect(Collectors.toList());

			WavePoint lastTop1 = sortedTopList.getLast();
			
			if(last1.getC() > prevBot.getStockBean().getBodyBottom()) //&& minByL.getDateInt() == wpLast1.getDateInt())
			{
				List<StockBean> subList = StreamTransformHelper.subListWithEndElement(stockList, wpLast1.getDate(), last1.getTxnDate());
				
				//e.g. 100 + (110-100)/2 
				double achieveBodyLevel = prevBot.getStockBean().getBodyBottom() + Math.abs(prevBot.getStockBean().getBodyTop() - prevBot.getStockBean().getBodyBottom()) / 2.0;

				for(int i=1; i<subList.size(); i++) 
				{
					StockBean elem = subList.get(i);
					if(elem.getC() >= achieveBodyLevel) {
						confirmDate = elem.getTxnDate(); //first confirmDate
						
//						boolean isAchieveBodyLevelRatio = this.findAchieveBodyLevelRatio(subList, prevBot.getStockBean(), elem, last1.getTxnDate());
						//find  max high
						StockBean highestSk = StreamTransformHelper.findMaxHighStock(subList);

						if(highestSk.getC()<lastTop1.getStockBean().getBodyTop())
						{
							isHit = true;
							if(last1.getTxnDateInt() == elem.getTxnDateInt())
								extraMsg= "D0";

							if(last1.getTxnDateInt() > elem.getTxnDateInt()+1 && last1.getTxnDateInt() < elem.getTxnDateInt()+5)
								extraMsg= "D(1-5)";


							if(highestSk.getBodyBottom() < lastTop1.getStockBean().getH() && last1.getH() < highestSk.getH() && last1.getL() < highestSk.getL() )
								extraMsg += "(回)";
							
							break;
						}else 
							continue;
					}
				}
			}
		}

		if(condition2){
			isHit = true;
			extraMsg= "D0-3";
		}
		
		if(isHit) {	
			msg.add(Const.UP+KPatternConst.KP_BOTTOM_REVERSAL+extraMsg);
		}
		return msg;
	}
	
	private boolean findAchieveBodyLevelRatio(List<StockBean> stockList, StockBean botStock, StockBean firstConfirmedStock, String currentDate) {
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
