package com.sjm.test.yahdata.analy.ta.rule.ma.situation.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.MAStatusEnum;
import com.sjm.test.yahdata.analy.ta.ValidateHelper;
import com.sjm.test.yahdata.analy.ta.rule.ma.situation.bean.MovingAvgCrossResultBean;
import com.sjm.test.yahdata.analy.ta.rule.ma.situation.bean.VolumeMASituationSummaryBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VolumeMASituationEngine{
	public VolumeMASituationEngine() {}

	public VolumeMASituationSummaryBean detect(List<StockBean> stockList) {
		if(stockList==null || stockList.size()<=5)
			return null;
		
		StockBean curr = stockList.get(stockList.size()-1);
		List<StockBean> prevList = stockList.subList(0, stockList.size()-1);
				
//		StockBean prev = prevList.get(prevList.size()-1);
		StockBean prev1 = prevList.get(prevList.size()-1);
		StockBean prev2 = prevList.get(prevList.size()-2);
		StockBean prev3 = prevList.get(prevList.size()-3);
		StockBean prev4 = prevList.get(prevList.size()-4);
		
		boolean b = this.validate(prev1, curr);
		if(b==false || curr.getVolumeSma()==null)
			return null;
		
//		double ma2 = curr.getVolumeSma().getMa2();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList, curr, 2);
//		double ma5 = curr.getVolumeSma().getMa5();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList, curr, 5);
//		double ma10 = curr.getVolumeSma().getMa10();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList, curr, 10);
//		double ma19 = curr.getVolumeSma().getMa19();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList, curr, 19);
//		double ma20 = curr.getVolumeSma().getMa20();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList, curr, 20);
//		double ma50 = curr.getVolumeSma().getMa50();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList, curr, 50);
//		double ma100 = curr.getVolumeSma().getMa100();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList, curr, 100);
//		double ma200 = curr.getVolumeSma().getMa200();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList, curr, 200);
//		double ma250 = curr.getVolumeSma().getMa250();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList, curr, 250);

//		double percentageToMA5 = 0-(1-curr.getVolumeSma().getMa5()/curr.getC());//(curr.getC() - ma5) / ma5;
//		double percentageToMA10 = 0-( 1-curr.getVolumeSma().getMa10()/curr.getC());//(curr.getC() - ma10) / ma10;
//		double percentageToMA20 = 0-(1-curr.getVolumeSma().getMa20()/curr.getC());//(curr.getC() - ma20) / ma20;
//		double percentageToMA50 = 0-(1-curr.getVolumeSma().getMa50()/curr.getC());//(curr.getC() - ma50) / ma50;
//		double percentageToMA200 = 0-(1-curr.getVolumeSma().getMa200()/curr.getC());//(curr.getC() - ma200) / ma200;
//		double percentageToMA250 = 0-(1-curr.getVolumeSma().getMa250()/curr.getC());//(curr.getC() - ma250) / ma250;
		
		
		VolumeMASituationSummaryBean sumy = new VolumeMASituationSummaryBean();
//		sumy.setMa5(curr.getVolumeSma().getMa5());
//		sumy.setMa10(curr.getVolumeSma().getMa10());
//		sumy.setMa20(curr.getVolumeSma().getMa20());
//		sumy.setMa50(curr.getVolumeSma().getMa50());
//		sumy.setMa100(curr.getVolumeSma().getMa100());
//		sumy.setMa200(curr.getVolumeSma().getMa200());
//		sumy.setMa250(curr.getVolumeSma().getMa250());
//		sumy.setMa2(curr.getVolumeSma().getMa2());
//		sumy.setMa19(curr.getVolumeSma().getMa19());
		
		sumy.setSma(curr.getVolumeSma());
		sumy.setSmaPrev1(prev1.getVolumeSma());
		sumy.setSmaPrev2(prev2.getVolumeSma());
		
//		sumy.setPercentageCurrent2MA5(percentageToMA5);
//		sumy.setPercentageCurrent2MA10(percentageToMA10);
//		sumy.setPercentageCurrent2MA20(percentageToMA20);
//		sumy.setPercentageCurrent2MA200(percentageToMA200);
//		sumy.setPercentageCurrent2MA250(percentageToMA250);
//		sumy.setPercentageCurrent2MA50(percentageToMA50);
		sumy.setStockCode(curr.getStockCode());
		sumy.setTxnDate(curr.getTxnDate());
		sumy.setPrice(curr.getC());
		
		
		double ma5DiffPct = (curr.getC() - curr.getVolumeSma().getMa5()) /curr.getVolumeSma().getMa5();
		double ma10DiffPct = (curr.getC() - curr.getVolumeSma().getMa10()) /curr.getVolumeSma().getMa10();
		double ma20DiffPct = (curr.getC() - curr.getVolumeSma().getMa20()) /curr.getVolumeSma().getMa20();
		double ma50DiffPct = (curr.getC() - curr.getVolumeSma().getMa50()) /curr.getVolumeSma().getMa50();
		double ma100DiffPct = (curr.getC() - curr.getVolumeSma().getMa100()) /curr.getVolumeSma().getMa100();
//		double ma200DiffPct = (curr.getC() - curr.getVolumeSma().getMa200()) /curr.getVolumeSma().getMa200();
//		double ma250DiffPct = (curr.getC() - curr.getVolumeSma().getMa250()) /curr.getVolumeSma().getMa250();
		
//		sumy.setAbv2D((curr.getC()>curr.getVolumeSma().getMa2())?true:false);	
//		sumy.setAbv19D((curr.getC()>curr.getVolumeSma().getMa19())?true:false);
		
		sumy.setAbv5D(ma5DiffPct);
		sumy.setAbv10D(ma10DiffPct);
		sumy.setAbv20D(ma20DiffPct);
		sumy.setAbv50D(ma50DiffPct);
		sumy.setAbv100D(ma100DiffPct);
//		sumy.setAbv200D(ma200DiffPct);
//		sumy.setAbv250D(ma250DiffPct);
		
		//////////////

		//find the ma 50 and ma200 cross up/down 
//		List<StockBean> prevList = stockList.subList(0, stockList.size()-1);
		int noOfDay = 2;
		
//		double prev1ma2 = prev1.getVolumeSma().getMa2();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList.subList(0, stockList.size()-2), prev, noOfDay);
//		double prev2ma2 = prev2.getVolumeSma().getMa2();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList.subList(0, stockList.size()-3), prevList.get(prevList.size()-2), noOfDay);
//		double prev3ma2 = prev3.getVolumeSma().getMa2();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList.subList(0, stockList.size()-4), prevList.get(prevList.size()-3), noOfDay);
		
		noOfDay = 10;
		double prev1ma10 = prev1.getVolumeSma().getMa10();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList.subList(0, stockList.size()-2), prev, noOfDay);
		double prev2ma10 = prev2.getVolumeSma().getMa10();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList.subList(0, stockList.size()-3), prevList.get(prevList.size()-2), noOfDay);
		double prev3ma10 = prev3.getVolumeSma().getMa10();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList.subList(0, stockList.size()-4), prevList.get(prevList.size()-3), noOfDay);
		
//		noOfDay = 19;
//		double prev1ma19 = prev1.getVolumeSma().getMa19();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList.subList(0, stockList.size()-2), prev, noOfDay);
//		double prev2ma19 = prev2.getVolumeSma().getMa19();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList.subList(0, stockList.size()-3), prevList.get(prevList.size()-2), noOfDay);
//		double prev3ma19 = prev3.getVolumeSma().getMa19();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList.subList(0, stockList.size()-4), prevList.get(prevList.size()-3), noOfDay);
		
		noOfDay = 20;
		double prev1ma20 = prev1.getVolumeSma().getMa20(); //MovingAvgHelper.getVolumeSma().getMabyLength(prevList.subList(0, stockList.size()-2), prev, noOfDay);
		double prev2ma20 = prev2.getVolumeSma().getMa20(); //MovingAvgHelper.getVolumeSma().getMabyLength(prevList.subList(0, stockList.size()-3), prevList.get(prevList.size()-2), noOfDay);
		double prev3ma20 = prev3.getVolumeSma().getMa20(); //MovingAvgHelper.getVolumeSma().getMabyLength(prevList.subList(0, stockList.size()-4), prevList.get(prevList.size()-3), noOfDay);
		
		noOfDay = 50;
		double prev1ma50 = prev1.getVolumeSma().getMa50();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList.subList(0, stockList.size()-2), prev, noOfDay);
		double prev2ma50 = prev2.getVolumeSma().getMa50();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList.subList(0, stockList.size()-3), prevList.get(prevList.size()-2), noOfDay);
		double prev3ma50 = prev3.getVolumeSma().getMa50();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList.subList(0, stockList.size()-4), prevList.get(prevList.size()-3), noOfDay);
		
//		noOfDay = 200;
//		double prev1ma200 = prev1.getVolumeSma().getMa200();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList.subList(0, stockList.size()-2), prev, noOfDay);
//		double prev2ma200 = prev2.getVolumeSma().getMa200();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList.subList(0, stockList.size()-3), prevList.get(prevList.size()-2), noOfDay);
//		double prev3ma200 = prev3.getVolumeSma().getMa200();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList.subList(0, stockList.size()-4), prevList.get(prevList.size()-3), noOfDay);
//		
//		
//		noOfDay = 250;
//		double prev1ma250 = prev1.getVolumeSma().getMa250();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList.subList(0, stockList.size()-2), prev, noOfDay);
//		double prev2ma250 = prev2.getVolumeSma().getMa250();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList.subList(0, stockList.size()-3), prevList.get(prevList.size()-2), noOfDay);
//		double prev3ma250 = prev3.getVolumeSma().getMa250();//MovingAvgHelper.getVolumeSma().getMabyLength(prevList.subList(0, stockList.size()-4), prevList.get(prevList.size()-3), noOfDay);
//		
//		
//		sumy.setMa2Prev1(prev1ma2);
//		sumy.setMa2Prev2(prev2ma2);
//		sumy.setMa2Prev3(prev3ma2);
//		
//		sumy.setMa19Prev1(prev1ma19);
//		sumy.setMa19Prev2(prev2ma19);
//		sumy.setMa19Prev3(prev3ma19);
		
		sumy.setMa50Prev1(prev1ma50);
		sumy.setMa50Prev2(prev2ma50);
		sumy.setMa50Prev3(prev3ma50);
		
		sumy.setMa50Prev1(prev1ma50);
		sumy.setMa50Prev2(prev2ma50);
		sumy.setMa50Prev3(prev3ma50);
		
//		sumy.setMa200Prev1(prev1ma200);
//		sumy.setMa200Prev2(prev2ma200);
//		sumy.setMa200Prev3(prev3ma200);
//		
//		sumy.setMa250Prev1(prev1ma250);
//		sumy.setMa250Prev2(prev2ma250);
//		sumy.setMa250Prev3(prev3ma250);
		
		//////////////
//		StockBean prev1 = stockList.get(stockList.size()-3);
//		StockBean prev2 = stockList.get(stockList.size()-4);
//		StockBean prev3 = stockList.get(stockList.size()-5);
		
//		if(curr.getVolume()>ma50 && prev1.getC()>prev1.getVolumeSma().getMa50() && prev2.getC()>prev2.getVolumeSma().getMa50() && prev3.getC()>prev3.getVolumeSma().getMa50() && prev4.getC()>prev4.getVolumeSma().getMa50())
//			sumy.setConsecutive5daysAbvCurrent50D(true);
//		if(curr.getC()>ma200 && prev1.getC()>prev1.getVolumeSma().getMa200() && prev2.getC()>prev2.getVolumeSma().getMa200() && prev3.getC()>prev3.getVolumeSma().getMa200() && prev4.getC()>prev4.getVolumeSma().getMa200())
//			sumy.setConsecutive5daysAbvCurrent200D(true);
		
//		if(curr.getVolume()<ma50 && prev1.getC()<prev1.getVolumeSma().getMa50() && prev2.getC()<prev2.getVolumeSma().getMa50() && prev3.getC()<prev3.getVolumeSma().getMa50() && prev4.getC()<prev4.getVolumeSma().getMa50())
//			sumy.setConsecutive5daysBlwCurrent50D(true);
//		if(curr.getC()<ma200 && prev1.getC()<prev1.getVolumeSma().getMa200() && prev2.getC()<prev2.getVolumeSma().getMa200() && prev3.getC()<prev3.getVolumeSma().getMa200() && prev4.getC()<prev4.getVolumeSma().getMa200())
//			sumy.setConsecutive5daysBlwCurrent200D(true);
		
		
		MovingAvgCrossResultBean ma5x50CrossUpDate = this.findRecentMACrossUpDate(stockList,5,50);
		MovingAvgCrossResultBean ma5x50CrossDownDate = this.findRecentMACrossDownDate(stockList, 5, 50);

		sumy.setMa5x50CrossUpDate(ma5x50CrossUpDate);
		sumy.setMa5x50CrossDownDate(ma5x50CrossDownDate);
		
		return sumy;

	}	
	
	public Double getStagnantMARatio(List<Double> maList)//STAGNANT
	{
		Double minValue = Collections.min(maList);
		Double maxValue = Collections.max(maList);
		Double ratio = (maxValue - minValue)/minValue;
		return ratio;
	}
	
	protected double getPrevMAbyLength(List<StockBean> prevList, int maLength) {
		List<StockBean>  prevSubList = prevList.subList((prevList.size()- maLength), prevList.size());
		
		List<StockBean>  avgList = new ArrayList<StockBean>(maLength);
		avgList.addAll(prevSubList);
		
		return getAverage(avgList);
	}
	
	
	public boolean validate(StockBean prev, StockBean curr) {
		
		boolean b1 = ValidateHelper.isPriceData(prev);
		boolean b2 = ValidateHelper.isPriceData(curr);
		
//		boolean b3 = ValidateHelper.isVolumeData(prev);
//		boolean b4 = ValidateHelper.isVolumeData(curr);
//		
//		return (b1 && b2 && b3 && b4);
		return (b1&&b2);
		
	}
	
	protected double getAverage(List<StockBean> orgList) {
		double avg = orgList.parallelStream().mapToDouble(a -> a.getC()).average().getAsDouble();
		return avg;
	}

	
	public MovingAvgCrossResultBean findRecentMACrossUpDate(List<StockBean> stockList, int daysShort, int daysLong)
	{
		if(stockList.size() < daysLong)
	    	return null;
		
//	    String ma50250Date = null;
	    int cntFindDay = 0;
	    
	    MovingAvgCrossResultBean crossUpResult = new MovingAvgCrossResultBean();
	    for(int i=stockList.size()-1; i>=daysLong; i--)
	    {
	    	if(cntFindDay>daysLong)
	    		return crossUpResult; // return empty
            List<StockBean> subList = stockList.subList(0, i);
            
            //check cur-loop date: 50D > 250D and PREV-50D <250D
            //is cross up occured
            crossUpResult = this.isMACrossTrigger(subList, daysShort, daysLong, "UP");

//            if(isCrossUpOccured == true){
//                ma50250Date = stockList.get(i-1).getTxnDate();
//            }

            
            if(crossUpResult.getCrossDate()!=null)
            	break;	    
            cntFindDay++;
            
	    }
	    return crossUpResult;
	}
	
	public MovingAvgCrossResultBean findRecentMACrossDownDate(List<StockBean> stockList, int daysShort, int daysLong)
	{
		if(stockList.size() < daysLong)
	    	return null;
		
//	    String ma50250Date = null;
		 MovingAvgCrossResultBean crossDownResult = new MovingAvgCrossResultBean();
	    int cntFindDay = 0;
	    
	    for(int i=stockList.size()-1; i>=daysLong; i--)
	    {
	    	if(cntFindDay>daysLong)
	    		return crossDownResult; // return empty
	    	
            List<StockBean> subList = stockList.subList(0, i);
            
            //check cur-loop date: 50D > 250D and PREV-50D <250D
            crossDownResult = this.isMACrossTrigger(subList, daysShort, daysLong, "DOWN");

//            if(isCrossUpOccured == true){
//            	 ma50250Date = stockList.get(i-1).getTxnDate();
//            }

            
            if(crossDownResult.getCrossDate()!=null)
            	break;	       
            
            cntFindDay++;
	    }
	    return crossDownResult;
	}
	
	protected MovingAvgCrossResultBean isMACrossTrigger(List<StockBean> stockList, int daysShort, int daysLong, String crossType) {
		StockBean curr = stockList.get(stockList.size()-1);
		List<StockBean> prevList = stockList.subList(0, stockList.size()-1);
		StockBean prev = prevList.get(prevList.size()-1);
		
//		if("2022-12-23".equalsIgnoreCase(curr.getTxnDate())) {
//			System.out.println("PAUSE");
//		}
		try {
			double ma5 =  curr.getVolumeSma().getMa5();
			double ma50 =  curr.getVolumeSma().getMa50();
			double prev1ma5 = prev.getVolumeSma().getMa5();
			double prev1ma50 = prev.getVolumeSma().getMa50();

		if(ma5>ma50 && prev1ma5<prev1ma50 && "UP".equalsIgnoreCase(crossType))
			return new MovingAvgCrossResultBean(curr.getTxnDate(), crossType, ma5, ma50, daysShort, daysLong);
		
		if(ma5<ma50 && prev1ma5>prev1ma50 && "DOWN".equalsIgnoreCase(crossType))
			return new MovingAvgCrossResultBean(curr.getTxnDate(), crossType, ma5, ma50, daysShort, daysLong);
		
		}catch(Exception e) {
			log.error(null, e);
			return new MovingAvgCrossResultBean();
		}
		return new MovingAvgCrossResultBean();
	}
	
	public boolean findMABullishPattern(List<StockBean> stockList) {
		if(stockList==null || stockList.size()<2)
			return false;
		
		StockBean curr = stockList.get(stockList.size()-1);
		List<StockBean> prevList = stockList.subList(0, stockList.size()-1);
				
		StockBean prev = prevList.get(prevList.size()-1);
		
		boolean b = this.validate(prev, curr);
		if(b==false)
			return false;
		
		double ma5 = curr.getVolumeSma().getMa5();
		double ma10 = curr.getVolumeSma().getMa10();
		double ma20 = curr.getVolumeSma().getMa20();
		double ma50 = curr.getVolumeSma().getMa50();
		double ma100 = curr.getVolumeSma().getMa100();
//		double ma200 = curr.getVolumeSma().getMa200();
//		double ma250 = curr.getVolumeSma().getMa250();
		
		
		double prevMa5 = prev.getVolumeSma().getMa5();
		double prevMa10 = prev.getVolumeSma().getMa10();
		
		
		boolean b1 = false;
		boolean b2 = false;
		boolean b3 = false;
		boolean b4 = false;
		boolean b5 = false;
		
		if(curr.getC() > ma50)
			b1 = true;
		
		if(curr.getC() > prev.getH())
			b2 = true;
		
		if(ma5>ma10 && prevMa5<prevMa10)
			b3 = true;
		
		if(ma20 > ma5 && ma20 > ma10)
			b4 = true;
		if(ma5>ma50 && ma10>ma50 && ma20>ma50)
			b5 = true;
		
		
		boolean btnResult = b1 && b2 && b3 && b4 && b5;
		
		if(btnResult == true) {
			int len = 50;
			if(stockList.size()< len) {
				len = stockList.size()-1;
			}
			List<StockBean> substockList = stockList.subList(stockList.size()-len, stockList.size());
			StockBean maxVolStock = substockList.stream().max(Comparator.comparingDouble(StockBean::getVolume)).get();
			double avgVol = substockList.stream().mapToDouble(StockBean::getVolume).average().orElse(Double.NaN);
			double ratio = (double)maxVolStock.getVolume()/avgVol;
		
			if(ratio < 2) {
				btnResult = false;
			}
		}
		return btnResult;
	}
}
