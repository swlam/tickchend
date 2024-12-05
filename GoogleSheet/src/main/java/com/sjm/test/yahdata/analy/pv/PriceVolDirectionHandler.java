package com.sjm.test.yahdata.analy.pv;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.IntStream;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.type.RsiType;
import com.sjm.test.yahdata.analy.conts.type.VolumePriceType;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.ta.indicator.rsi.DivergenceResult;

public class PriceVolDirectionHandler {

	public PriceVolDirectionHandler() {
		// TODO Auto-generated constructor stub
	}


	//價跌量縮	
	public VolumePriceType findUpTrendPriceDownVolDown(List<StockBean> stockList) {
		StockBean highestStock = stockList.get(0); //MUST be maxVolStock
		int len = stockList.size();
		int lowestIdx = StreamTransformHelper.findMinIndex(stockList);
		
		StockBean lowestStock = stockList.get(lowestIdx); ////MUST be lastStock
		StockBean lastStock = stockList.get(len-1); ////MUST be lastStock
		
		//1.price rule:
		int daysDiff = StreamTransformHelper.txDaysBetween(stockList, highestStock.getTxnDate(), lowestStock.getTxnDate());
		boolean b1 = lowestIdx >= stockList.size()-2; //lowestStock == Last-1-Stock OR Last-2-Stock
		boolean b2 = highestStock.getL() > lowestStock.getH();
		boolean b3 = daysDiff <=8;
		boolean b4 = lowestStock.getC() >= lowestStock.getPriceSma().getMa20() 
				&& lowestStock.getC() > lowestStock.getPriceSma().getMa50()
				&& lowestStock.getPriceSma().getMa20() > lowestStock.getPriceSma().getMa50();
		//b4 = first element MUST HighestStock		
		boolean bPriceRule = b1 && b2 && b3 && b4;

		//2.Volume rule:
		StockBean maxVolStock = stockList.stream()
                .max(Comparator.comparingDouble(StockBean::getVolume))
                .orElse(null);
		
		
		// find Index of highest volume element
		OptionalDouble minVolume = stockList.stream()
                .mapToDouble(StockBean::getVolume)
                .min();

        int indexMin = minVolume.isPresent() ? IntStream.range(0, stockList.size())
                .filter(i -> stockList.get(i).getVolume() == minVolume.getAsDouble())
                .findFirst()
                .orElse(-1)
                : -1;
        
        boolean bV1 = (indexMin > (stockList.size()/2));
			
		
		boolean bV2 = maxVolStock.getTxnDateInt() == highestStock.getTxnDateInt();
		boolean bV3 = lowestStock.getDayVolumeChgPct() < 1.05;
		boolean bV4 = lowestStock.getVolume() < maxVolStock.getVolume() * 0.8
				&&  minVolume.getAsDouble() < maxVolStock.getVolume() * 0.6
				;
		
		//
		boolean bVolueRule = bV1 && bV2 && bV3 && bV4;
		
		//lowestStock is Not lastStock
		boolean bLastSk1 = lowestIdx == len-2;
		boolean bLastSk2 = lowestIdx == len-1;
		boolean bLattSk3 = lastStock.getC() >= lowestStock.getC() && lastStock.getBodyTop() >= lowestStock.getBodyTop();
		boolean bIsLastStock = (bLastSk1 || bLastSk2) && bLattSk3;
		
		if(bPriceRule && bVolueRule && bIsLastStock) {
//			DivergenceResult diverResult = new DivergenceResult();
//			diverResult.setDivergenceType(RsiType.DIVERGENCE_PRICE_DECREASE_VOL_DECREASE);// + " " + GeneralHelper.to100Pct(priceGoingDownPct));
//			diverResult.setDates(Arrays.asList(maxVolStock.getTxnDate()));
			return VolumePriceType.DIVERGENCE_PRICE_DECREASE_VOL_DECREASE;

		}else{
			return null;
		}
	}

	
	//價升量縮	
		public VolumePriceType findUpTrendPriceUpVolDown(List<StockBean> stockList) {
			StockBean firstStock = stockList.get(0); //MUST be maxVolStock
			int len = stockList.size();
			int highestPriceIdx = StreamTransformHelper.findMaxIndex(stockList);
			
			StockBean highestPriceStock = stockList.get(highestPriceIdx); ////MUST be lastStock
			StockBean lastStock = stockList.get(len-1); ////MUST be lastStock
			
			//1.price rule:
			int daysDiff = StreamTransformHelper.txDaysBetween(stockList, firstStock.getTxnDate(), highestPriceStock.getTxnDate());
			boolean b1 = highestPriceIdx >= stockList.size()-2; //lowestStock == Last-1-Stock OR Last-2-Stock
			boolean b2 = firstStock.getH() < highestPriceStock.getBodyBottom();
			boolean b3 = daysDiff <=8;
			
			//b4 = first element MUST HighestStock		
			boolean bPriceRule = b1 && b2 && b3 ;

			//2.Volume rule:
			StockBean maxVolStock = stockList.stream()
	                .max(Comparator.comparingDouble(StockBean::getVolume))
	                .orElse(null);
			
			
			// find Index of highest volume element
			OptionalDouble minVolume = stockList.stream()
	                .mapToDouble(StockBean::getVolume)
	                .min();

	        int indexMin = minVolume.isPresent() ? IntStream.range(0, stockList.size())
	                .filter(i -> stockList.get(i).getVolume() == minVolume.getAsDouble())
	                .findFirst()
	                .orElse(-1)
	                : -1;
	        
	        boolean bV1 = (indexMin > (stockList.size()/2));
				
			
			boolean bV2 = maxVolStock.getTxnDateInt() == firstStock.getTxnDateInt();
			boolean bV3 = highestPriceStock.getDayVolumeChgPct() < 0.95;
			boolean bV4 = highestPriceStock.getVolume() < maxVolStock.getVolume() * 0.8
					&&  minVolume.getAsDouble() < maxVolStock.getVolume() * 0.7
					;
			
			//
			boolean bVolueRule = bV1 && bV2 && bV3 && bV4;
			
			
			
			if(bPriceRule && bVolueRule ) {
//				DivergenceResult diverResult = new DivergenceResult();
//				diverResult.setDivergenceType(RsiType.DIVERGENCE_PRICE_DECREASE_VOL_DECREASE);// + " " + GeneralHelper.to100Pct(priceGoingDownPct));
//				diverResult.setDates(Arrays.asList(maxVolStock.getTxnDate()));
				return VolumePriceType.DIVERGENCE_PRICE_INCREASE_VOL_DECREASE;

			}else{
				return null;
			}
		}


	//低位縮量,價反彈
	public VolumePriceType findUpTrendBottomPriceVolRebound(List<StockBean> stockList) {
		if(stockList.size()<3)
			return null;
		
		StockBean botStock = stockList.get(0); //MUST be recent botStock
		StockBean lastStock = stockList.get(stockList.size()-1); //MUST be recent botStock
		
		StockBean botNext1Stock = stockList.get(1);
		StockBean botNext2Stock = stockList.get(2);
		
		
		
		// find Index of highest volume element
		OptionalDouble minVolume = stockList.stream().mapToDouble(StockBean::getVolume).min();

        int indexMin = minVolume.isPresent() ? IntStream.range(0, stockList.size())
                .filter(i -> stockList.get(i).getVolume() == minVolume.getAsDouble())
                .findFirst().orElse(-1)
                : -1;
        
		boolean bLowestVol = indexMin<2;
		
		boolean b1 = botStock.getDayVolumeChgPct() < 0.7;
		boolean b2OR1 = botStock.getBodyTop() < botNext1Stock.getC() 
				&& botNext1Stock.getBodyBottom() < botNext2Stock.getBodyBottom()
				&& botNext1Stock.getDayVolumeChgPct()>1.0 && botNext2Stock.getDayVolumeChgPct()>0.9;
				
		boolean b2OR2 = botStock.getBodyTop() < botNext2Stock.getC() && botNext2Stock.getO() < botNext2Stock.getC()
				&& botNext2Stock.getDayVolumeChgPct()>1.1;

				
		boolean isLast2BotPctValid = (lastStock.getC() - botStock.getBodyTop())/botStock.getBodyTop() < 0.05;
				
		boolean isUpTrend = botStock.getC() >= botStock.getPriceSma().getMa50() 
				&& lastStock.getC() > lastStock.getPriceSma().getMa50()
				&& lastStock.getPriceSma().getMa20() > lastStock.getPriceSma().getMa50();
		
		boolean bFinal = bLowestVol && b1 && (b2OR1 || b2OR2) && isLast2BotPctValid && isUpTrend;
		if(bFinal) {
//			DivergenceResult diverResult = new DivergenceResult();
//			diverResult.setDivergenceType(RsiType.VOL_SHRINKS_AT_BOT_AND_PRICE_REBOUND);// + " " + GeneralHelper.to100Pct(priceGoingDownPct));
//			diverResult.setDates(Arrays.asList(botStock.getTxnDate()));
//			return diverResult;
			return VolumePriceType.VOL_SHRINKS_AT_BOT_AND_PRICE_REBOUND;
			
		}
		
		return null;
	}
	
	
	public VolumePriceType findExtremelyShrinkingVolume(List<StockBean> orgStockList) {
//		StockBean minVolStock = stockList.stream()
//                .min(Comparator.comparingDouble(StockBean::getVolume))
//                .orElse(null);
			
		int endIdx = orgStockList.size();
		if(Const.IS_INTRADAY) {
			endIdx = orgStockList.size()-1;
		}
		
		List<StockBean> stockList = orgStockList.subList(0, endIdx);
		
		// find Index of highest volume element
		OptionalDouble minVolume = stockList.stream()
                .mapToDouble(StockBean::getVolume)
                .min();

        int indexMin = minVolume.isPresent() ? IntStream.range(0, stockList.size())
                .filter(i -> stockList.get(i).getVolume() == minVolume.getAsDouble())
                .findFirst()
                .orElse(-1)
                : -1;
        StockBean minVolStock = stockList.get(indexMin);
        boolean isLessVol = minVolStock.getVolume() <= minVolStock.getVolumeSma().getMa20()*0.6;
        boolean isRecent5Days = indexMin >  stockList.size() - 8;
        if(isLessVol && isRecent5Days)
        	return VolumePriceType.VOL_SHRINKS_EXTREMELY;
        return null;
        
	}
}
