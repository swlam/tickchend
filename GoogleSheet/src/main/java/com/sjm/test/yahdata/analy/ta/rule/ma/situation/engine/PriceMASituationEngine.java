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
import com.sjm.test.yahdata.analy.ta.rule.ma.situation.bean.PriceMASituationSummaryBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PriceMASituationEngine{
	public PriceMASituationEngine() {}

	public PriceMASituationSummaryBean detect(List<StockBean> stockList) {
		if(stockList==null || stockList.size()<=5) {
//			return null;
			return new PriceMASituationSummaryBean();
		}
		
		StockBean curr = stockList.get(stockList.size()-1);
		List<StockBean> prevList = stockList.subList(0, stockList.size()-1);
				

		StockBean prev1 = prevList.get(prevList.size()-1);
		StockBean prev2 = prevList.get(prevList.size()-2);
		StockBean prev3 = prevList.get(prevList.size()-3);
		StockBean prev4 = prevList.get(prevList.size()-4);
		
		boolean b = this.validate(prev1, curr);
		if(b==false || curr.getPriceSma()==null) {
//			return null;
			return new PriceMASituationSummaryBean();
		}
		
//		double ma2 = curr.getPriceSma().getMa2();//MovingAvgHelper.getPriceSma().getMabyLength(prevList, curr, 2);
		double ma5 = curr.getPriceSma().getMa5();//MovingAvgHelper.getPriceSma().getMabyLength(prevList, curr, 5);
		double ma10 = curr.getPriceSma().getMa10();//MovingAvgHelper.getPriceSma().getMabyLength(prevList, curr, 10);
//		double ma19 = curr.getPriceSma().getMa19();//MovingAvgHelper.getPriceSma().getMabyLength(prevList, curr, 19);
		double ma20 = curr.getPriceSma().getMa20();//MovingAvgHelper.getPriceSma().getMabyLength(prevList, curr, 20);
		double ma50 = curr.getPriceSma().getMa50();//MovingAvgHelper.getPriceSma().getMabyLength(prevList, curr, 50);
		double ma100 = curr.getPriceSma().getMa100();//MovingAvgHelper.getPriceSma().getMabyLength(prevList, curr, 100);
		double ma200 = curr.getPriceSma().getMa200();//MovingAvgHelper.getPriceSma().getMabyLength(prevList, curr, 200);
//		double ma250 = curr.getPriceSma().getMa250();//MovingAvgHelper.getPriceSma().getMabyLength(prevList, curr, 250);

		double percentageToMA5 = 0-(1-curr.getPriceSma().getMa5()/curr.getC());//(curr.getC() - ma5) / ma5;
		double percentageToMA10 = 0-( 1-curr.getPriceSma().getMa10()/curr.getC());//(curr.getC() - ma10) / ma10;
		double percentageToMA20 = 0-(1-curr.getPriceSma().getMa20()/curr.getC());//(curr.getC() - ma20) / ma20;
		double percentageToMA50 = 0-(1-curr.getPriceSma().getMa50()/curr.getC());//(curr.getC() - ma50) / ma50;
		double percentageToMA200 = 0-(1-curr.getPriceSma().getMa200()/curr.getC());//(curr.getC() - ma200) / ma200;
		double percentageToMA250 = 0-(1-curr.getPriceSma().getMa250()/curr.getC());//(curr.getC() - ma250) / ma250;
		
		
		PriceMASituationSummaryBean sumy = new PriceMASituationSummaryBean();
//		sumy.setMa5(curr.getPriceSma().getMa5());
//		sumy.setMa10(curr.getPriceSma().getMa10());
//		sumy.setMa20(curr.getPriceSma().getMa20());
//		sumy.setMa50(curr.getPriceSma().getMa50());
//		sumy.setMa100(curr.getPriceSma().getMa100());
//		sumy.setMa200(curr.getPriceSma().getMa200());
//		sumy.setMa250(curr.getPriceSma().getMa250());
//		sumy.setMa2(curr.getPriceSma().getMa2());
//		sumy.setMa19(curr.getPriceSma().getMa19());
		
		sumy.setPriceSma(curr.getPriceSma());
		
		sumy.setPercentageCurrent2MA5(percentageToMA5);
		sumy.setPercentageCurrent2MA10(percentageToMA10);
		sumy.setPercentageCurrent2MA20(percentageToMA20);
		sumy.setPercentageCurrent2MA200(percentageToMA200);
		sumy.setPercentageCurrent2MA250(percentageToMA250);
		sumy.setPercentageCurrent2MA50(percentageToMA50);
		sumy.setStockCode(curr.getStockCode());
		sumy.setTxnDate(curr.getTxnDate());
		sumy.setPrice(curr.getC());
		
		
		double ma5DiffPct = (curr.getC() - curr.getPriceSma().getMa5()) /curr.getPriceSma().getMa5();
		double ma10DiffPct = (curr.getC() - curr.getPriceSma().getMa10()) /curr.getPriceSma().getMa10();
		double ma20DiffPct = (curr.getC() - curr.getPriceSma().getMa20()) /curr.getPriceSma().getMa20();
		double ma50DiffPct = (curr.getC() - curr.getPriceSma().getMa50()) /curr.getPriceSma().getMa50();
		double ma100DiffPct = (curr.getC() - curr.getPriceSma().getMa100()) /curr.getPriceSma().getMa100();
		double ma200DiffPct = (curr.getC() - curr.getPriceSma().getMa200()) /curr.getPriceSma().getMa200();
		double ma250DiffPct = (curr.getC() - curr.getPriceSma().getMa250()) /curr.getPriceSma().getMa250();
		
		sumy.setAbv2D((curr.getC()>curr.getPriceSma().getMa2())?true:false);	
		sumy.setAbv19D((curr.getC()>curr.getPriceSma().getMa19())?true:false);
		
		sumy.setAbv5D(ma5DiffPct);
		sumy.setAbv10D(ma10DiffPct);
		sumy.setAbv20D(ma20DiffPct);
		sumy.setAbv50D(ma50DiffPct);
		sumy.setAbv100D(ma100DiffPct);
		sumy.setAbv200D(ma200DiffPct);
		sumy.setAbv250D(ma250DiffPct);
		
		//////////////

		//find the ma 50 and ma200 cross up/down 
//		List<StockBean> prevList = stockList.subList(0, stockList.size()-1);
		int noOfDay = 2;
		
		double prev1ma2 = prev1.getPriceSma().getMa2();//MovingAvgHelper.getPriceSma().getMabyLength(prevList.subList(0, stockList.size()-2), prev, noOfDay);
		double prev2ma2 = prev2.getPriceSma().getMa2();//MovingAvgHelper.getPriceSma().getMabyLength(prevList.subList(0, stockList.size()-3), prevList.get(prevList.size()-2), noOfDay);
		double prev3ma2 = prev3.getPriceSma().getMa2();//MovingAvgHelper.getPriceSma().getMabyLength(prevList.subList(0, stockList.size()-4), prevList.get(prevList.size()-3), noOfDay);
		
		noOfDay = 10;
		double prev1ma10 = prev1.getPriceSma().getMa10();//MovingAvgHelper.getPriceSma().getMabyLength(prevList.subList(0, stockList.size()-2), prev, noOfDay);
		double prev2ma10 = prev2.getPriceSma().getMa10();//MovingAvgHelper.getPriceSma().getMabyLength(prevList.subList(0, stockList.size()-3), prevList.get(prevList.size()-2), noOfDay);
		double prev3ma10 = prev3.getPriceSma().getMa10();//MovingAvgHelper.getPriceSma().getMabyLength(prevList.subList(0, stockList.size()-4), prevList.get(prevList.size()-3), noOfDay);
		
		noOfDay = 19;
		double prev1ma19 = prev1.getPriceSma().getMa19();//MovingAvgHelper.getPriceSma().getMabyLength(prevList.subList(0, stockList.size()-2), prev, noOfDay);
		double prev2ma19 = prev2.getPriceSma().getMa19();//MovingAvgHelper.getPriceSma().getMabyLength(prevList.subList(0, stockList.size()-3), prevList.get(prevList.size()-2), noOfDay);
		double prev3ma19 = prev3.getPriceSma().getMa19();//MovingAvgHelper.getPriceSma().getMabyLength(prevList.subList(0, stockList.size()-4), prevList.get(prevList.size()-3), noOfDay);
		
		noOfDay = 20;
		double prev1ma20 = prev1.getPriceSma().getMa20(); //MovingAvgHelper.getPriceSma().getMabyLength(prevList.subList(0, stockList.size()-2), prev, noOfDay);
		double prev2ma20 = prev2.getPriceSma().getMa20(); //MovingAvgHelper.getPriceSma().getMabyLength(prevList.subList(0, stockList.size()-3), prevList.get(prevList.size()-2), noOfDay);
		double prev3ma20 = prev3.getPriceSma().getMa20(); //MovingAvgHelper.getPriceSma().getMabyLength(prevList.subList(0, stockList.size()-4), prevList.get(prevList.size()-3), noOfDay);
		
		noOfDay = 50;
		double prev1ma50 = prev1.getPriceSma().getMa50();//MovingAvgHelper.getPriceSma().getMabyLength(prevList.subList(0, stockList.size()-2), prev, noOfDay);
		double prev2ma50 = prev2.getPriceSma().getMa50();//MovingAvgHelper.getPriceSma().getMabyLength(prevList.subList(0, stockList.size()-3), prevList.get(prevList.size()-2), noOfDay);
		double prev3ma50 = prev3.getPriceSma().getMa50();//MovingAvgHelper.getPriceSma().getMabyLength(prevList.subList(0, stockList.size()-4), prevList.get(prevList.size()-3), noOfDay);
		
		noOfDay = 200;
		double prev1ma200 = prev1.getPriceSma().getMa200();//MovingAvgHelper.getPriceSma().getMabyLength(prevList.subList(0, stockList.size()-2), prev, noOfDay);
		double prev2ma200 = prev2.getPriceSma().getMa200();//MovingAvgHelper.getPriceSma().getMabyLength(prevList.subList(0, stockList.size()-3), prevList.get(prevList.size()-2), noOfDay);
		double prev3ma200 = prev3.getPriceSma().getMa200();//MovingAvgHelper.getPriceSma().getMabyLength(prevList.subList(0, stockList.size()-4), prevList.get(prevList.size()-3), noOfDay);
		
		
		noOfDay = 250;
		double prev1ma250 = prev1.getPriceSma().getMa250();//MovingAvgHelper.getPriceSma().getMabyLength(prevList.subList(0, stockList.size()-2), prev, noOfDay);
		double prev2ma250 = prev2.getPriceSma().getMa250();//MovingAvgHelper.getPriceSma().getMabyLength(prevList.subList(0, stockList.size()-3), prevList.get(prevList.size()-2), noOfDay);
		double prev3ma250 = prev3.getPriceSma().getMa250();//MovingAvgHelper.getPriceSma().getMabyLength(prevList.subList(0, stockList.size()-4), prevList.get(prevList.size()-3), noOfDay);
		
		
		sumy.setMa2Prev1(prev1ma2);
		sumy.setMa2Prev2(prev2ma2);
		sumy.setMa2Prev3(prev3ma2);
		
		sumy.setMa19Prev1(prev1ma19);
		sumy.setMa19Prev2(prev2ma19);
		sumy.setMa19Prev3(prev3ma19);
		
		sumy.setMa50Prev1(prev1ma50);
		sumy.setMa50Prev2(prev2ma50);
		sumy.setMa50Prev3(prev3ma50);
		
		sumy.setMa50Prev1(prev1ma50);
		sumy.setMa50Prev2(prev2ma50);
		sumy.setMa50Prev3(prev3ma50);
		
		sumy.setMa200Prev1(prev1ma200);
		sumy.setMa200Prev2(prev2ma200);
		sumy.setMa200Prev3(prev3ma200);
		
		sumy.setMa250Prev1(prev1ma250);
		sumy.setMa250Prev2(prev2ma250);
		sumy.setMa250Prev3(prev3ma250);
		
		//////////////
//		StockBean prev1 = stockList.get(stockList.size()-3);
//		StockBean prev2 = stockList.get(stockList.size()-4);
//		StockBean prev3 = stockList.get(stockList.size()-5);
		
		if(curr.getC()>ma50 && prev1.getC()>prev1.getPriceSma().getMa50() && prev2.getC()>prev2.getPriceSma().getMa50() && prev3.getC()>prev3.getPriceSma().getMa50() && prev4.getC()>prev4.getPriceSma().getMa50())
			sumy.setConsecutive5daysAbvCurrent50D(true);
		if(curr.getC()>ma200 && prev1.getC()>prev1.getPriceSma().getMa200() && prev2.getC()>prev2.getPriceSma().getMa200() && prev3.getC()>prev3.getPriceSma().getMa200() && prev4.getC()>prev4.getPriceSma().getMa200())
			sumy.setConsecutive5daysAbvCurrent200D(true);
		
		if(curr.getC()<ma50 && prev1.getC()<prev1.getPriceSma().getMa50() && prev2.getC()<prev2.getPriceSma().getMa50() && prev3.getC()<prev3.getPriceSma().getMa50() && prev4.getC()<prev4.getPriceSma().getMa50())
			sumy.setConsecutive5daysBlwCurrent50D(true);
		if(curr.getC()<ma200 && prev1.getC()<prev1.getPriceSma().getMa200() && prev2.getC()<prev2.getPriceSma().getMa200() && prev3.getC()<prev3.getPriceSma().getMa200() && prev4.getC()<prev4.getPriceSma().getMa200())
			sumy.setConsecutive5daysBlwCurrent200D(true);
		
		
		Set<MAStatusEnum> maSet = new LinkedHashSet<MAStatusEnum>();
		
		if(ma5>ma10 && ma10>ma20 && ma20>ma50 && ma50>ma200 ) {
			maSet.add(MAStatusEnum.FULL_LONG_ARRANGE);		
		}else if(curr.getC()>ma100 && ma10>ma20 && ma20>ma50) {
			maSet.add(MAStatusEnum.MID_TERM_LONG_ARRANGE);
		}else if(curr.getC()>ma50 && ma5>ma10 && ma10>ma20 ) {
			maSet.add(MAStatusEnum.SMALL_LONG_ARRANGE);
		}else if(ma10>ma50 && curr.getC()>ma5 && curr.getC()>ma10 && curr.getC()>ma20 && curr.getC()>ma50 && curr.getC()>ma100 && curr.getC()>ma200) {
			maSet.add(MAStatusEnum.LONG_READY_ARRANGE);
		}
		
		if(ma5<ma10 && ma10<ma20 && ma20<ma50 && ma50<ma200) {
			maSet.add(MAStatusEnum.FULL_SHORT_ARRANGE);
		}else if(curr.getC()<ma100 && ma10<ma20 && ma20<ma50 ) {
			maSet.add(MAStatusEnum.MID_TERM_SHORT_ARRANGE);
		}else if(curr.getC()<ma50 && ma5<ma10 && ma10<ma20) {
			maSet.add(MAStatusEnum.SMALL_SHORT_ARRANGE);
		}else if(ma10<ma50 && curr.getC()<ma5 && curr.getC()<ma10 && curr.getC()<ma20 && curr.getC()<ma50 && curr.getC()<ma100 && curr.getC()<ma200) {
			maSet.add(MAStatusEnum.SHORT_READY_ARRANGE);
		}
		
		if(maSet.isEmpty()==false) {
			sumy.setMovingAvgStatusSet(maSet);
//			return sumy; 
		}
			
		/*
		if(ma10<ma20 && ma10<ma50 && ma20<ma50 && (ma50<ma200 || ma50<ma250) && curr.getC()<=ma10) {
			maSet.add(MAStatusEnum.FULL_SHORT_ARRANGE);			
		}else if(ma10<ma20 && ma10<ma50 && ma20<ma50 && (ma50<ma200 || ma50<ma250) && curr.getC()>ma10) {
			maSet.add(MAStatusEnum.SHORT_ARRANGE_ABV_10D);
		}else if(ma10<ma50 && ma20<ma50 && (ma50<ma200 || ma50<ma250)) {			
			maSet.add(MAStatusEnum.HALF_SHORT_ARRANGE);
		}
		*/
		if(curr.getC()>ma5) {
			maSet.add(MAStatusEnum.ABV_5D);
		}else
		if(curr.getC()<ma5) {
			maSet.add(MAStatusEnum.BLW_5D);
		}
		
		if(curr.getC()>ma10) {
			maSet.add(MAStatusEnum.ABV_10D);
		}else
		if(curr.getC()<ma10) {
			maSet.add(MAStatusEnum.BLW_10D);
		}
		
		if(curr.getC()>ma20) {
			maSet.add(MAStatusEnum.ABV_20D);
		}else if(curr.getC()<ma20) {
			maSet.add(MAStatusEnum.BLW_20D);
		}
		
		if(curr.getC()>ma50) {
			maSet.add(MAStatusEnum.ABV_50D);
		}else if(curr.getC()<ma50) {
			maSet.add(MAStatusEnum.BLW_50D);
		}
		
		if(curr.getC()>ma100) {
			maSet.add(MAStatusEnum.ABV_100D);
		}else if(curr.getC()<ma100) {
			maSet.add(MAStatusEnum.BLW_100D);
		}
		
		if(curr.getC()>ma200) {
			maSet.add(MAStatusEnum.ABV_200D);
		}else if(curr.getC()<ma200) {
			maSet.add(MAStatusEnum.BLW_200D);
		}
		
		sumy.setMovingAvgStatusSet(maSet);
		
		MovingAvgCrossResultBean ma50250CrossUpDate = this.findRecentMACrossUpDate(stockList,50,250);
		MovingAvgCrossResultBean ma50200CrossUpDate = this.findRecentMACrossUpDate(stockList,50,200);
		MovingAvgCrossResultBean ma50200CrossDownDate = this.findRecentMACrossDownDate(stockList, 50, 200);

		sumy.setMa50200CrossUpDate(ma50200CrossUpDate);
		sumy.setMa50250CrossUpDate(ma50250CrossUpDate);
		sumy.setMa50200CrossDownDate(ma50200CrossDownDate);
		
		Double stagnantMaRatio = this.getStagnantMARatio(Arrays.asList(ma10, ma20, ma50));//STAGNANT_SHORT_MID_TERM
		Double stagnantPrev1MaRatio = this.getStagnantMARatio(Arrays.asList(prev1ma10, prev1ma20, prev1ma50));
		Double stagnantPrev2MaRatio = this.getStagnantMARatio(Arrays.asList(prev2ma10, prev2ma20, prev2ma50));
		Double stagnantPrev3MaRatio = this.getStagnantMARatio(Arrays.asList(prev3ma10, prev3ma20, prev3ma50));
		
		sumy.setStagnantMARatio(stagnantMaRatio);
		sumy.setStagnantPrev1MARatio(stagnantPrev1MaRatio);
		sumy.setStagnantPrev2MARatio(stagnantPrev2MaRatio);
		sumy.setStagnantPrev3MARatio(stagnantPrev3MaRatio);
		
		sumy.setStagnantMidTermMARatio(this.getStagnantMARatio(Arrays.asList(ma50, ma100, ma200)));//STAGNANT_MID_TERM
		sumy.setStagnantShortTermMARatio(this.getStagnantMARatio(Arrays.asList(ma5, ma10, ma20)));//STAGNANT_SHORT_TERM
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
			double ma50 =  curr.getPriceSma().getMa50();
			double ma250 = curr.getPriceSma().getMa250();
			
			double prev1ma50 = prev.getPriceSma().getMa50();
			double prev1ma250 = prev.getPriceSma().getMa250();
		
		if(ma50>ma250 && prev1ma50<prev1ma250 && "UP".equalsIgnoreCase(crossType))
			return new MovingAvgCrossResultBean(curr.getTxnDate(), crossType, ma50, ma250, daysShort, daysLong);
		
		if(ma50<ma250 && prev1ma50>prev1ma250 && "DOWN".equalsIgnoreCase(crossType))
			return new MovingAvgCrossResultBean(curr.getTxnDate(), crossType, ma50, ma250, daysShort, daysLong);
		
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
		
		double ma5 = curr.getPriceSma().getMa5();
		double ma10 = curr.getPriceSma().getMa10();
		double ma20 = curr.getPriceSma().getMa20();
		double ma50 = curr.getPriceSma().getMa50();
//		double ma100 = curr.getPriceSma().getMa100();
//		double ma200 = curr.getPriceSma().getMa200();
		double ma250 = curr.getPriceSma().getMa250();
		
		
		double prevMa5 = prev.getPriceSma().getMa5();
		double prevMa10 = prev.getPriceSma().getMa10();
		
		
		boolean b1 = false;
		boolean b2 = false;
		boolean b3 = false;
		boolean b4 = false;
		boolean b5 = false;
		boolean b6 = false;
		boolean b7 = false;
		
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
		if( ma250>curr.getC())
			b6 = true;
		if( ma250>ma50 && ma250>ma10)
			b7 = true;
		
		boolean btnResult = b1 && b2 && b3 && b4 && b5 && b6 && b7;
		
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
