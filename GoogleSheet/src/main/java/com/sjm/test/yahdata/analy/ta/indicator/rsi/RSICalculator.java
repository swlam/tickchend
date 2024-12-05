package com.sjm.test.yahdata.analy.ta.indicator.rsi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.OptionalInt;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.KPatternConst;
import com.sjm.test.yahdata.analy.conts.type.RsiType;
//import com.sjm.test.yahdata.analy.helper.CFGHelper;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
//import com.sjm.test.yahdata.analy.main.BaseApp;
//import com.sjm.test.yahdata.analy.main.FindHistoricalHighApp;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WavePoint;

import lombok.extern.slf4j.Slf4j;

//RSI forumula
//https://school.stockcharts.com/doku.php?id=technical_indicators:relative_strength_index_rsi

@Slf4j
public class RSICalculator {

	public static RSICalculator instance = null;
	

	public static RSICalculator getInstance() {
		if(instance == null)
			instance = new RSICalculator();
		return instance;
	}
	
	public List<RSIMeta> calculateAll(List<StockBean> data, int periodLen) throws Exception {
		int firstBar = periodLen;
		int lastBar = data.size() - 1;
		
		if (firstBar < periodLen) {
			String msg = "Quote history length " + data.size() + " is insufficient to calculate the RSI indicator.";
			throw new Exception(msg);
		}
		
		
		List<RSIMeta> rsiMetaList = new ArrayList<RSIMeta>();
		
		for (int bar = 1; bar <= lastBar; bar++) {
			
			double change = data.get(bar).getC() - data.get(bar - 1).getC();
			
			RSIMeta meta = new RSIMeta(periodLen);
			meta.setStockCode(data.get(bar).getStockCode());
			meta.setTxnDate(data.get(bar).getTxnDate());
			meta.setChange(change);
			double avgGain = 0.0;
			double avgLoss =  0.0;
			if (change >= 0) {
				meta.setGain(change);	
			}else {
				meta.setLoss(change);	
			}
			
			if(bar == firstBar+1) {
				 avgGain = rsiMetaList.subList(0, periodLen).stream().mapToDouble(x->x.getGain()).sum() / periodLen;
				 avgLoss = rsiMetaList.subList(0, periodLen).stream().mapToDouble(x->x.getLoss()).sum() / periodLen;
				
			}else if(bar > firstBar+1) {
				RSIMeta lastMeta = rsiMetaList.get(rsiMetaList.size()-1);
				 avgGain = (meta.getGain() + lastMeta.getAvgGain()* (periodLen-1))/periodLen;
				 avgLoss = (meta.getLoss() + lastMeta.getAvgLoss()*(periodLen-1))/periodLen;
			}
			meta.setAvgGain(avgGain);
			meta.setAvgLoss(avgLoss);
			meta.setRsValue(avgGain / Math.abs(avgLoss));
			meta.setRsiValue( 100.0 - (100.0/(1.0 + meta.getRsValue())) );
			
			rsiMetaList.add(meta);
//			System.out.println(meta);
			
		}
		return rsiMetaList;
		
	}
	
	public DivergenceResult calcRsiDiverence(List<StockBean> stockList, List<WavePoint> topList, List<WavePoint> botList) {

		int SHIFT_DAYS = 3;
		int DAYS_DIFF_FROM_LAST1RSI_UNTIL_NOW = 10;
				
		StockBean nowStock = stockList.get(stockList.size()-1);
		
		DivergenceResult result = new DivergenceResult();
		
		if(topList!=null && topList.size()>1)
		{
			StockBean topLast1 = topList.get(topList.size()-1).getStockBean();
			StockBean topLast2 = topList.get(topList.size()-2).getStockBean();
			
			int idxLast1 = StreamTransformHelper.findIndex(stockList, topLast1.getTxnDate());
			int idxLast1End =stockList.size()-1;
			if(idxLast1+SHIFT_DAYS < stockList.size()) {
				idxLast1End = idxLast1+SHIFT_DAYS;
			}
			//e.g. SHIFT_DAYS elements before and after are calculated
			StockBean last1MaxRsi = stockList.subList(idxLast1-SHIFT_DAYS, idxLast1End).parallelStream()
		      .max(Comparator.comparing(StockBean::getRsi9))
		      .orElseThrow(NoSuchElementException::new);
			
			int idxLast2 = StreamTransformHelper.findIndex(stockList, topLast2.getTxnDate());
			int idxLast2End = idxLast2 + SHIFT_DAYS;
			if(idxLast2End >= idxLast1) {
				idxLast2End = idxLast1-1 ;
			}

			//e.g. SHIFT_DAYS elements before and after are calculated
			StockBean last2MaxRsi = stockList.subList(idxLast2-SHIFT_DAYS, idxLast2End).parallelStream()
		      .max(Comparator.comparing(StockBean::getRsi9))
		      .orElseThrow(NoSuchElementException::new);
			
			boolean bTopDivergence = last2MaxRsi.getRsi9()>0.7 && last1MaxRsi.getRsi9()<last2MaxRsi.getRsi9() &&  topLast1.getH()>topLast2.getH();
			
			
			//this inclued the wpRsiTopLast1.getDate()
			List<StockBean> subls = StreamTransformHelper.subListWithEndElement(stockList, last1MaxRsi.getTxnDate(), nowStock.getTxnDate());
			long countBrkRsiPrevTop = subls.stream()
		            .filter(s -> s.getRsi9() > last1MaxRsi.getRsi9())
		            .count();
			
			
			//if last-1-max-rsi, last-top-H is UP break, skip RSI_TOP_DIVERGENCE
			if(this.isSkipTopDivergence(stockList, last1MaxRsi, last2MaxRsi, topLast1, nowStock)){
				log.debug("Skip TOP_DIVERGENCE ");
			}else {
				if(bTopDivergence) {// && nowStock.getRsi9() > 0.6) {
					result.setDivergenceType(RsiType.TOP_DIVERGENCE);
					result.setDates(Arrays.asList(topLast2.getTxnDate(), topLast1.getTxnDate()));
				}
				if(bTopDivergence && countBrkRsiPrevTop >0 && nowStock.getDayChgPct()>0.0 && nowStock.getC()>=last1MaxRsi.getBodyTop()) { //&& nowStock.getRsi9() > 0.6 
					result.setDivergenceType(RsiType.TOP_DIVERGENCE_UP_BREAK);
					result.setRemark(this.obtainUpBreakDivergenceMessage(stockList, last1MaxRsi, nowStock));
	
				}
			}
		}
		//////
		
		if(botList!=null && botList.size()>1)
		{
			StockBean botLast1 = botList.get(botList.size()-1).getStockBean();
			StockBean botLast2 = botList.get(botList.size()-2).getStockBean();
			
			int idxLast1 = StreamTransformHelper.findIndex(stockList, botLast1.getTxnDate());		
			int idxLast1End =stockList.size()-1;
			if(idxLast1+SHIFT_DAYS < stockList.size()) {
				idxLast1End = idxLast1+SHIFT_DAYS ;
			}
			
			StockBean last1MinRsi = stockList.subList(idxLast1-SHIFT_DAYS, idxLast1End).parallelStream()
		      .min(Comparator.comparing(StockBean::getRsi9))
		      .orElseThrow(NoSuchElementException::new);
			
			int idxLast2 = StreamTransformHelper.findIndex(stockList, botLast2.getTxnDate());
			int idxLast2End = idxLast2+SHIFT_DAYS;
			if(idxLast2End >= idxLast1) {
				idxLast2End = idxLast1-1 ;
			}
			
			StockBean last2MinRsi = stockList.subList(idxLast2-SHIFT_DAYS, idxLast2End).parallelStream()
		      .min(Comparator.comparing(StockBean::getRsi9))
		      .orElseThrow(NoSuchElementException::new);

			boolean bBottomDivergence = last1MinRsi.getRsi9()>last2MinRsi.getRsi9() &&  botLast1.getL()<botLast2.getL();

			List<StockBean> subls = StreamTransformHelper.subListWithEndElement(stockList, botLast1.getTxnDate(), nowStock.getTxnDate());
			long countDownBrkRsiPrevTop = subls.stream()
		            .filter(s -> s.getRsi9() < last1MinRsi.getRsi9())
		            .count();
			

			if(this.isSkipBottomDivergence(stockList, last1MinRsi, last2MinRsi, botLast1, nowStock)){
				log.debug("Skip BOT_DIVERGENCE ");

			}else{
				if(bBottomDivergence){ //&& nowStock.getRsi9() >=0.5
					result.setDivergenceType(RsiType.BOTTOM_DIVERGENCE);
					result.setDates(Arrays.asList(botLast2.getTxnDate(), botLast1.getTxnDate()));
				}
				if(bBottomDivergence && countDownBrkRsiPrevTop >0 && nowStock.getDayChgPct()<0.0 && nowStock.getC()<=botLast1.getBodyBottom()) {// && nowStock.getRsi9() <=0.5
					result.setDivergenceType(RsiType.BOTTOM_DIVERGENCE_DOWN_BREAK);
					result.setRemark(this.obtainDownBreakDivergenceMessage(stockList, last1MinRsi, nowStock));

				}
			}
			

		}
		
		return result;
	}
	
	
	//if last-1-max-rsi, last-top-H is UP break, skip RSI_TOP_DIVERGENCE
	public boolean isSkipTopDivergence(List<StockBean> stockList, StockBean last1MaxRsi, StockBean last2MaxRsi, StockBean last1TopStock, StockBean last) {
		List<StockBean> subls = StreamTransformHelper.subListWithEndElement(stockList, last1MaxRsi.getTxnDate(), last.getTxnDate());
		long countBrkRsiPrevTop = subls.stream()
	            .filter(s -> s.getRsi9() > last1MaxRsi.getRsi9() && s.getRsi9() >= last2MaxRsi.getRsi9())
	            .count();
		long countBrkPricePrevTop = subls.stream()
	            .filter(s -> s.getH() > last1TopStock.getH())
	            .count();
		return countBrkRsiPrevTop>0 && countBrkPricePrevTop>0;
	}
	
	private StockBean getFirstUpBreakDivergenceDate(List<StockBean> stockList, StockBean last1MaxRsi, StockBean last) {
		int rsiTopLast1Index = StreamTransformHelper.findIndex(stockList, last1MaxRsi.getTxnDate());
		OptionalInt firstBreakIndex = stockList.stream()
                .skip(rsiTopLast1Index + 1) // 跳过前面的元素
                .filter(num -> num.getRsi9() > last1MaxRsi.getRsi9())
                .mapToInt(stockList::indexOf) // 获取元素在aList中的索引
                .findFirst(); // 获取第一个匹配元素的索引

      if (firstBreakIndex.isPresent()) {
    	  	StockBean firstBreakRsiStockBean = stockList.get(firstBreakIndex.getAsInt());

    	  	return firstBreakRsiStockBean;
      }
      return null;
	}
	
	
	public String obtainUpBreakDivergenceMessage(List<StockBean> stockList, StockBean last1MaxRsi, StockBean last) {
		String returnMsg = "";
		
		StockBean firstUpBreakRsiStock = getFirstUpBreakDivergenceDate(stockList, last1MaxRsi, last);
		
		if(firstUpBreakRsiStock==null)
			return returnMsg;

    		int diffDays = StreamTransformHelper.txDaysBetween(stockList, firstUpBreakRsiStock.getTxnDate(), last.getTxnDate());
            int actualUpBreakDays = diffDays+1;

            if(actualUpBreakDays<=3 && firstUpBreakRsiStock.getH()>last1MaxRsi.getH() && last.getC()>=last1MaxRsi.getH()) {
            	returnMsg ="頂背馳-上破第"+actualUpBreakDays+"日";
            }
        
      
      return returnMsg;
	}
	
	
	//if last-1-min-rsi, last-bot-L is DOWN break, skip RSI_BOT_DIVERGENCE
	public boolean isSkipBottomDivergence(List<StockBean> stockList, StockBean last1MinRsi, StockBean last2MinRsi, StockBean last1BotStock, StockBean last) {
		List<StockBean> subls = StreamTransformHelper.subListWithEndElement(stockList, last1MinRsi.getTxnDate(), last.getTxnDate());
		long countBrkRsiPrev = subls.stream()
				.filter(s -> s.getRsi9() < last1MinRsi.getRsi9() && s.getRsi9() <= last2MinRsi.getRsi9())
				.count();
		long countBrkPricePrev = subls.stream()
				.filter(s -> s.getL() < last1BotStock.getL())
				.count();
		return countBrkRsiPrev>0 && countBrkPricePrev>0;
	}			


	private StockBean getFirstDownBreakDivergenceDate(List<StockBean> stockList, StockBean last1MinRsi, StockBean last) {
		int rsiBotLast1Index = StreamTransformHelper.findIndex(stockList, last1MinRsi.getTxnDate());
		OptionalInt firstBreakIndex = stockList.stream()
				.skip(rsiBotLast1Index + 1) // 跳过前面的元素
				.filter(num -> num.getRsi9() < last1MinRsi.getRsi9())
				.mapToInt(stockList::indexOf) // 获取元素在aList中的索引
				.findFirst(); // 获取第一个匹配元素的索引

	  if (firstBreakIndex.isPresent()) {
			  StockBean firstBreakRsiStockBean = stockList.get(firstBreakIndex.getAsInt());

			  return firstBreakRsiStockBean;
	  }
	  return null;
	}

	public String obtainDownBreakDivergenceMessage(List<StockBean> stockList, StockBean last1MinRsi, StockBean last) {
		String returnMsg = "";
		
		StockBean firstDownBreakRsiStock = getFirstDownBreakDivergenceDate(stockList, last1MinRsi, last);
		
		if(firstDownBreakRsiStock==null)
			return returnMsg;

			int diffDays = StreamTransformHelper.txDaysBetween(stockList, firstDownBreakRsiStock.getTxnDate(), last.getTxnDate());
			int actualBreakDays = diffDays+1;

			if(actualBreakDays<=3 && firstDownBreakRsiStock.getL() < last1MinRsi.getL() && last.getC()<=last1MinRsi.getL()) {
				returnMsg ="底背馳-下破第"+actualBreakDays+"日";
			}
		
	  
	  return returnMsg;
	}
	

	
	
}
