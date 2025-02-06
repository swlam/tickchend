package com.sjm.test.yahdata.analy.wavepattern;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.KPatternConst;
import com.sjm.test.yahdata.analy.conts.type.KBodyType;
import com.sjm.test.yahdata.analy.conts.type.WaveType;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WavePoint;
import com.sjm.test.yahdata.analy.ta.KHelper;

import java.util.*;
import java.util.stream.Collectors;

public class TopReversalDownwardPattern extends BaseWavePattern {

	@Override
	public Set<String> find(List<StockBean> stockList, List<WavePoint> sortedTopList, List<WavePoint> sortedBotList) {

		List<WavePoint> topbotList = new ArrayList<WavePoint>();
		topbotList.addAll(sortedTopList);
		topbotList.addAll(sortedBotList);
		
		List<WavePoint> sortedTopBotList = topbotList.stream().sorted(Comparator.comparing(e -> e.getDateInt())).collect(Collectors.toList());
//		sortedTopBotList = this.distinctSortedTopBotList(sortedTopBotList);
		return findTopReversal(stockList, sortedTopBotList);
	}

	
	
	private Set<String> findTopReversal(List<StockBean> stockList, List<WavePoint> sortedTopBotList){
		Set<String> msg = new LinkedHashSet<String>();
		if(sortedTopBotList.size()<3 )
			return msg;
		StockBean last = stockList.getLast();
		StockBean last2 = stockList.get(stockList.size()-2);
		StockBean last3 = stockList.get(stockList.size()-3);

		WavePoint wpLast1 = sortedTopBotList.getLast();
		WavePoint wpLast2 = sortedTopBotList.get(sortedTopBotList.size() - 2);
		WavePoint wpLast3 = sortedTopBotList.get(sortedTopBotList.size() - 3);
		
//		WavePoint minByL = sortedTopBotList.stream()
//			      .min(Comparator.comparing(WavePoint::getL))
//			      .orElseThrow(NoSuchElementException::new);
		boolean isBigBullishBody = (KHelper.getBodySize(stockList) >= KBodyType.GENERAL.getValue() && KHelper.isBullishCandle(last));
		
		boolean condition1 = WaveType.TOP.equals(wpLast3.getType()) &&
				WaveType.BOT.equals(wpLast2.getType()) &&
				WaveType.TOP.equals(wpLast1.getType()) &&
				wpLast1.getStockBean().getBodyBottom() > wpLast3.getH() &&
				last.getC() < wpLast3.getStockBean().getBodyTop()
				&& !isBigBullishBody;

		boolean condition2 =
				WaveType.BOT.equals(wpLast1.getType()) &&
						(last.getC() < wpLast2.getStockBean().getBodyTop() || last2.getC() < wpLast2.getStockBean().getBodyTop()) &&
						(last.getH() > wpLast2.getH() || last2.getH() > wpLast2.getH() || last3.getH() > wpLast2.getH());

		String confirmDate = last.getTxnDate(); //init the date
		
		boolean isHit = false;
		String extraMsg = "";
		
		WavePoint prevTop = null;

		if( condition1 )
		{
			prevTop = wpLast3;
			List<WavePoint> sortedBotList = sortedTopBotList.stream().filter(x->x.getType()==WaveType.BOT)
					.sorted(Comparator.comparing(e -> e.getDateInt())).collect(Collectors.toList());

			WavePoint lastBot1 = sortedBotList.getLast();

			if(last.getC() < prevTop.getStockBean().getBodyTop()) //&& minByL.getDateInt() == wpLast1.getDateInt())
			{
				List<StockBean> subList = StreamTransformHelper.subListWithEndElement(stockList, wpLast1.getDate(), last.getTxnDate());
				
				//e.g. 100 + (110-100)/2 
				double achieveBodyLevel =  prevTop.getStockBean().getBodyBottom() + Math.abs(prevTop.getStockBean().getBodyTop() - prevTop.getStockBean().getBodyBottom()) / 2.0;

				for(int i=1; i<subList.size(); i++) 
				{
					StockBean elem = subList.get(i);
					if(elem.getC() <prevTop.getStockBean().getBodyTop() && elem.getH() < prevTop.getH()) {
						confirmDate = elem.getTxnDate(); //first confirmDate

//						boolean isAchieveBodyLevelRatio = this.findAchieveBodyLevelRatio(subList, prevTop.getStockBean(), elem, last.getTxnDate());
						//find min Low
						StockBean lowestSk = StreamTransformHelper.findMinLowStock(subList);

						if(lowestSk.getC()>lastBot1.getStockBean().getBodyBottom())
						{
							isHit = true;
							if(last.getTxnDateInt() == elem.getTxnDateInt())
								extraMsg= "D0";

							if(last.getTxnDateInt() > elem.getTxnDateInt()+1 && last.getTxnDateInt() < elem.getTxnDateInt()+5)
								extraMsg= "D(1-5)";


//							if(lowestSk.getBodyBottom() < lastTop1.getStockBean().getH() &&
//									last.getH() < highestSk.getH() &&
//									last.getL() < highestSk.getL() ) {
//								extraMsg += "(回)";
//							}
							break;
						}else 
							continue;
					}
				}
			}
		}

		if(condition2){
			isHit = true;
			extraMsg= "D(0-3)";
		}

		if(isHit) {	
			msg.add(KPatternConst.KP_TOP_REVERSAL+extraMsg);
		}
		return msg;
	}
	
	private boolean findAchieveBodyLevelRatio(List<StockBean> stockList, StockBean botStock, StockBean firstConfirmedStock, String currentDate) {
		List<StockBean> subList = StreamTransformHelper.subListWithEndElement(stockList, firstConfirmedStock.getTxnDate(), currentDate);


		long count = subList.stream()
	            .filter(x -> x.getBodyBottom() < firstConfirmedStock.getBodyBottom())
	            .count();
		
		StockBean lastStock = subList.get(subList.size()-1);
		double ratio = (double) count / (double)subList.size();
		
		
//		boolean isEngouthVol = false;
//		if(Const.IS_INTRADAY) {
//			isEngouthVol=true;
//		}else {
//			isEngouthVol = ((double)firstConfirmedStock.getVolume() / (double)botStock.getVolume() >= 1)
//				|| firstConfirmedStock.getDayVolumeChgPct() > 1.2;
//		}
//
//		if(isEngouthVol && ratio >=0.7 && lastStock.getBodyBottom()< firstConfirmedStock.getBodyBottom())
//			return true;
		if(lastStock.getBodyBottom()< firstConfirmedStock.getBodyBottom())
			return true;
		return false;
	}
}
