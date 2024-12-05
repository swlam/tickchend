package com.sjm.test.yahdata.analy.ta.indicator.rsi;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.IntStream;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.KPatternConst;
import com.sjm.test.yahdata.analy.conts.type.RsiType;

public class DivergencePriceVol {
/*
	private final static int WINDOW_SIZE = 15;
	
	private static DivergencePriceVol instance = null;
	
	public static DivergencePriceVol getInstance() {
		if(instance == null)
			instance = new DivergencePriceVol();
		return instance;
	}
	
	public DivergenceResult calcPriceAndVolume(List<StockBean> stockList) {
		DivergenceResult result = this.calcVolumeDecrease(stockList, WINDOW_SIZE);
		if(result == null)			
			result = this.calcVolumeIncrease(stockList, WINDOW_SIZE);
		
		return result;
	}
	public DivergenceResult calcVolumeDecrease(List<StockBean> stockList, int windowSize) {
			int stockListSize = stockList.size();
			List<StockBean> subList = stockList.subList(stockListSize - WINDOW_SIZE, stockListSize);
			return calcVolumeDecrease(subList);
		}
	
	public DivergenceResult calcVolumeDecrease(List<StockBean> subList) {
//		int stockListSize = stockList.size();
//		
//		List<StockBean> subList = stockList.subList(stockListSize - WINDOW_SIZE, stockListSize);
		
		// find Index of highest volume element
		OptionalDouble maxVolume = subList.stream()
                .mapToDouble(StockBean::getVolume)
                .max();

        int indexMax = maxVolume.isPresent() ? IntStream.range(0, subList.size())
                .filter(i -> subList.get(i).getVolume() == maxVolume.getAsDouble())
                .findFirst()
                .orElse(-1)
                : -1;
        
		if(indexMax > (subList.size()/2))
			return null; // No P-V Divergence
		
        StockBean maxVolStock = subList.get(indexMax);
		List<StockBean> targetList = subList.subList(indexMax + 1, subList.size());

        if(targetList==null || targetList.isEmpty())
        	return null;
        
		double otherDaysAvgVol = targetList.parallelStream().mapToDouble(StockBean::getVolume).average().orElse(Double.NaN);

		double ratio = otherDaysAvgVol / maxVolume.getAsDouble();
		
		// Check if "volume" is decreasing.
		boolean isVolDecline = false;
		if(ratio < 0.65) {
			isVolDecline = true;
		}
				
		//Check if "price" is raising.
		double priceRisePct = 0.0;
		
		priceRisePct = this.isPriceRaise(targetList);
		double priceGoingDownPct = this.isPriceDowing(targetList);
		
		DivergenceResult diverResult = new DivergenceResult();
		
		if(isVolDecline && priceRisePct != 0.0) {
			diverResult.setDivergenceType(RsiType.DIVERGENCE_PRICE_INCREASE_VOL_DECREASE );//+ " " + GeneralHelper.to100Pct(priceRisePct));
			diverResult.setDates(Arrays.asList(maxVolStock.getTxnDate()));
			return diverResult;
		}else if(isVolDecline && priceGoingDownPct != 0.0) {
			diverResult.setDivergenceType(RsiType.DIVERGENCE_PRICE_DECREASE_VOL_DECREASE);// + " " + GeneralHelper.to100Pct(priceGoingDownPct));
			diverResult.setDates(Arrays.asList(maxVolStock.getTxnDate()));
			return diverResult;
		}
		else{
			return null;
		}
		
	}
	
	
	public DivergenceResult calcVolumeIncrease(List<StockBean> stockList, int windowSize) {
		int stockListSize = stockList.size();
		List<StockBean> subList = stockList.subList(stockListSize - WINDOW_SIZE, stockListSize);
		return calcVolumeIncrease(subList);
	}
	
	
	public DivergenceResult calcVolumeIncrease(List<StockBean> subList) {
//		int stockListSize = stockList.size();
//		List<StockBean> subList = stockList.subList(stockListSize - WINDOW_SIZE, stockListSize);
		
		// find Index of highest volume element
		OptionalDouble minVolume = subList.stream()
                .mapToDouble(StockBean::getVolume)
                .min();

        int indexMin = minVolume.isPresent() ? IntStream.range(0, subList.size())
                .filter(i -> subList.get(i).getVolume() == minVolume.getAsDouble())
                .findFirst()
                .orElse(-1)
                : -1;
        
		if(indexMin > (subList.size()/2))
			return null; // No P-V Divergence
		
        StockBean minVolStock = subList.get(indexMin);
		List<StockBean> targetList = subList.subList(indexMin + 1, subList.size());
		if(targetList==null || targetList.isEmpty())
			return null;
        
		double otherDaysAvgVol = targetList.parallelStream().mapToDouble(StockBean::getVolume).average().orElse(Double.NaN);

		double ratio = otherDaysAvgVol / minVolume.getAsDouble();
		
		// Check if "volume" is increasing.
		boolean isVolRaise = false;
		if(ratio >1.6) {
			isVolRaise = true;
		}
				
		//Check if "price" is raising.
		double priceRisePct = 0.0;
		priceRisePct = this.isPriceRaise(targetList);
		double priceGoingDownPct = this.isPriceDowing(targetList);
		
		DivergenceResult diverResult = new DivergenceResult();
		
		if(isVolRaise && priceRisePct!=0.0) {
			diverResult.setDivergenceType(RsiType.DIVERGENCE_PRICE_INCREASE_VOL_INCREASE);// + " " + GeneralHelper.to100Pct(priceRisePct));//價升量升
			diverResult.setDates(Arrays.asList(minVolStock.getTxnDate()));
			return diverResult;
		}else if(isVolRaise && priceGoingDownPct != 0.0) {
			diverResult.setDivergenceType(RsiType.DIVERGENCE_PRICE_DECREASE_VOL_INCREASE);// + " " + GeneralHelper.to100Pct(priceGoingDownPct)); //價跌量升
			diverResult.setDates(Arrays.asList(minVolStock.getTxnDate()));
			return diverResult;
		}
		else{
			return null;
		}
		
	}
	
	private double isPriceRaise(List<StockBean> skList) {
		int listSize = skList.size();
		double defaultRaisePct = 0.0;
		
		StockBean firstStock = skList.get(0);
		
		OptionalDouble highestPrice = skList.stream()
                .mapToDouble(StockBean::getH).max();

        int index = highestPrice.isPresent() ? IntStream.range(0, listSize)
                .filter(i -> skList.get(i).getH() == highestPrice.getAsDouble())
                .findFirst()
                .orElse(-1)
                : -1;
        
        boolean bHighestPriceInRightSide = false;
        boolean bMinBodyBottomInRishtSide = false;
        if((listSize / 2) <index)
        	bHighestPriceInRightSide = true;
        
        
        List<StockBean> leftSideList = skList.subList(0, index);
        
        OptionalDouble minBodyBottomLeftSide = leftSideList.stream().mapToDouble(StockBean::getBodyBottom).min();
        if(minBodyBottomLeftSide.isPresent()==true && (minBodyBottomLeftSide.getAsDouble() < firstStock.getL())) {
            	return defaultRaisePct;
        }
        
        
        List<StockBean> righSideList = skList.subList(index+1, listSize);
        OptionalDouble minBodyBottom = righSideList.stream().mapToDouble(StockBean::getBodyBottom).min();
        
        
        if(minBodyBottom.isPresent()==false)
        	return defaultRaisePct;
        	
        
        double rightSizeMinBodyBottom = minBodyBottom.getAsDouble();
        	
        if(rightSizeMinBodyBottom >= firstStock.getBodyTop())
        	bMinBodyBottomInRishtSide = true;
        	
        if( bHighestPriceInRightSide && bMinBodyBottomInRishtSide) {
        	double risePct = (highestPrice.getAsDouble() - firstStock.getL())/firstStock.getL();
        	return risePct;
        }
        
        return defaultRaisePct;
		
	}
	
	
	private double isPriceDowing(List<StockBean> skList) {
		double defaultDownPct = 0.0;
		
		int listSize = skList.size();
		StockBean firstStock = skList.get(0);
		
		OptionalDouble lowestPrice = skList.stream()
                .mapToDouble(StockBean::getL).min();

        int indexLowest = lowestPrice.isPresent() ? IntStream.range(0, listSize)
                .filter(i -> skList.get(i).getL() == lowestPrice.getAsDouble())
                .findFirst()
                .orElse(-1)
                : -1;
        
        boolean bLowestPriceInRightSide = false;
        boolean bMaxBodyTopInRishtSide = false;
        if((listSize / 2) <indexLowest)
        	bLowestPriceInRightSide = true;
        
        
        List<StockBean> leftSideList = skList.subList(0, indexLowest);
        
        OptionalDouble maxBodyTopLeftSide = leftSideList.stream().mapToDouble(StockBean::getBodyTop).max();
        if(maxBodyTopLeftSide.isPresent()==true && (maxBodyTopLeftSide.getAsDouble() > firstStock.getH())) {
            	return defaultDownPct;
        }
        
        
        List<StockBean> righSideList = skList.subList(indexLowest+1, listSize);
        
        
        OptionalDouble maxBodyTop = righSideList.stream().mapToDouble(StockBean::getBodyTop).max();
        if(maxBodyTop.isPresent()==false)
        	return defaultDownPct;
        
        double rightSizeMaxBodyTop = maxBodyTop.getAsDouble();
        	
        if(rightSizeMaxBodyTop <= firstStock.getBodyBottom())
        	bMaxBodyTopInRishtSide = true;
        	
        
        if( bLowestPriceInRightSide && bMaxBodyTopInRishtSide) {
        	double downPct = (lowestPrice.getAsDouble() - firstStock.getH()) /firstStock.getH(); 
        	return downPct;
        	
        }
        return defaultDownPct;
        
	}
	
	*/
}
