package com.sjm.test.yahdata.analy.helper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.type.KType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnalyGeneralHelper {

	public AnalyGeneralHelper() {
	}
	
	// mm: -04-,  04
	public static int getPreviousMonth(String mm) {
		String mmString = mm.replaceAll("-", "");
		int m = Integer.parseInt(mmString);
		if(m<1 || m>12)
			log.warn("Incorrect Monthvalue:"+mm);
		
		int prevM = 0;
		if(m==1) {
			prevM = 12;
		}else {
			prevM = m-1;
		}
		
		
		return prevM;
	}

	public static String getPreviousMonthMMFormat(String mm) {
		
		int m = getPreviousMonth(mm);
		return String.format("%02d", m);
	}
	
	public static String getPreviousYYYYMMFormat(String yyyyMMdd) {
		
		LocalDate ld = LocalDate.parse(yyyyMMdd);
		
		LocalDate yesterday = ld.plusMonths(-1);
		
		
		String formattedDate = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM"));
		return formattedDate;
	}
	
	
	public static List<Double> toReverseOrderList(List<Double> list){
		List<Double> ls = 
				list.stream()
				.sorted(Comparator.reverseOrder())
				.collect(Collectors.toList());
		return ls;
	}

	public static Double getMedian(List<Double> ls) {
		if(ls==null || ls.isEmpty())
			return -999.0;

		Double j;
		if(ls.size() %2==0) {
			j= (ls.get(ls.size()/2-1)+ ls.get(ls.size()/2))/2;

		}else {
			j= ls.get(ls.size()/2);
		}

		return j;
	}
	
	
	
//	public static double calculateSD(double numArray[]){
	public static Double calculateSD(List<Double> numArray){
		if(numArray==null || numArray.isEmpty())
			return 0.0;
		
        double sum = 0.0, standardDeviation = 0.0;
        int length = numArray.size();

        for(double num : numArray) {
            sum += num;
        }

        double mean = sum/length;

        for(double num: numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.sqrt(standardDeviation/length);
    }
	
	
	public static String getDirectionText(double percentage) {
		if(percentage>0.0)
			return Const.UP;
		else if(percentage==0.0)
			return Const.STEADY;
		else
			return Const.DOWN;
	}
	
	public static StockBean getMTDPeriodH(List<StockBean> stockList) {
		StockBean last = stockList.get(stockList.size()-1);
		LocalDate lastDate = LocalDate.parse(last.getTxnDate());
		
		String monthStart = lastDate.getYear()+"-"+String.format("%02d", lastDate.getMonthValue())+"-01";
		List<StockBean> mtdList = StreamTransformHelper.extractData(stockList, monthStart, last.getTxnDate());
		
		StockBean maxByHSkBean = mtdList
			      .stream()
			      .max(Comparator.comparing(StockBean::getH))
			      .orElseThrow(NoSuchElementException::new);
		return maxByHSkBean;
	}
	
	public static StockBean getMTDPeriodL(List<StockBean> stockList) {
		StockBean last = stockList.get(stockList.size()-1);
		LocalDate lastDate = LocalDate.parse(last.getTxnDate());
		
		String monthStart = lastDate.getYear()+"-"+String.format("%02d", lastDate.getMonthValue())+"-01";
		List<StockBean> mtdList = StreamTransformHelper.extractData(stockList, monthStart, last.getTxnDate());
		
		StockBean minByLSkBean = mtdList
			      .stream()
			      .min(Comparator.comparing(StockBean::getL))
			      .orElseThrow(NoSuchElementException::new);
		return minByLSkBean;
	}
	
	public static Double getMTDPct(List<StockBean> stockList, KType end2EndType) {
		StockBean last = stockList.get(stockList.size()-1);
		LocalDate lastDate = LocalDate.parse(last.getTxnDate());
		
		String monthStart = lastDate.getYear()+"-"+String.format("%02d", lastDate.getMonthValue())+"-01";
		List<StockBean> mtdList = StreamTransformHelper.extractData(stockList, monthStart, last.getTxnDate());
		Double mtdPct = getStockPct(mtdList, end2EndType);
		return mtdPct;
		
//		List<StockBean> threeDayList = stockList.subList(stockList.size()-3, stockList.size());
//		Double threeDaysPct = getPerformanceO2C(threeDayList);
	}
	
	public static Double getMTDPctRange(List<StockBean> stockList) {
		StockBean last = stockList.get(stockList.size()-1);
		LocalDate lastDate = LocalDate.parse(last.getTxnDate());
		
		String monthStart = lastDate.getYear()+"-"+String.format("%02d", lastDate.getMonthValue())+"-01";
		List<StockBean> mtdList = StreamTransformHelper.extractData(stockList, monthStart, last.getTxnDate());
		
		Double mtdPctRange = getStockPctRange(mtdList);
		return mtdPctRange;
	}
	
	//0,1,2,3,4,5,6,7,8,9
	//10-3 = 7
	public static Double getThreeDaysPct(List<StockBean> stockList, KType end2EndType) {
		return getDaysPct(stockList, end2EndType, 3);
	}
	public static Double getDaysPct(List<StockBean> stockList, KType end2EndType, int days) {
		List<StockBean> stockDayList = stockList.subList(stockList.size()-days, stockList.size());
		Double daysPct = getStockPct(stockDayList, end2EndType);
		return daysPct;
	}
	
	
	public static double getDailyChangePct(List<StockBean> stockList) {
		StockBean curr = stockList.get(stockList.size()-1);
		StockBean prev = stockList.get(stockList.size()-2);
		
		double pct = (curr.getC()/prev.getC())-1.0;
		
		return pct;
	}
	
	public static double getDailyChangeHighestPct(List<StockBean> stockList) {
		StockBean curr = stockList.get(stockList.size()-1);
		StockBean prev = stockList.get(stockList.size()-2);
		
		double pct = (curr.getH()/prev.getC())-1.0;
		
		return pct;
	}
	
	public static double getDailyChangeLowestPct(List<StockBean> stockList) {
		StockBean curr = stockList.get(stockList.size()-1);
		StockBean prev = stockList.get(stockList.size()-2);
		
		double pct = (curr.getL()/prev.getC())-1.0;
		
		return pct;
	}
	
	public static Double getStockPct(List<StockBean> subStockList, KType type) {

		double pct = 0.0d;

		double periodH = subStockList.stream().mapToDouble(StockBean::getH).max().orElse(Double.NaN);
		double periodL = subStockList.stream().mapToDouble(StockBean::getL).min().orElse(Double.NaN);
		

		switch (type) {

			case END2END_O2C: 
				pct = (subStockList.get(subStockList.size()-1).getC()  - subStockList.get(0).getO())/ subStockList.get(0).getO();
				break;

			case END2END_O2H:
				pct = (subStockList.get(subStockList.size()-1).getH()  - subStockList.get(0).getO())/ subStockList.get(0).getO();
				break;

			case O2PH:
				pct = (periodH  - subStockList.get(0).getO())/ subStockList.get(0).getO();
				break;

			case O2PL:
				pct = (periodL  - subStockList.get(0).getO())/ subStockList.get(0).getO();
				break;
				
			case C2PH:
				pct = (periodH  - subStockList.get(0).getC())/ subStockList.get(0).getC();
				break;
				
			case C2PL:
				pct = (periodL  - subStockList.get(0).getC())/ subStockList.get(0).getC();
				break;
			default:

				pct = (subStockList.get(subStockList.size()-1).getC()  - subStockList.get(0).getO())/ subStockList.get(0).getO();

		}

		return pct;

	}
	
	
	public static Double getStockPctRange(List<StockBean> subStockList) {
		double periodMax = subStockList.stream().mapToDouble(StockBean::getH).max().orElse(Double.NaN);
		double periodMin = subStockList.stream().mapToDouble(StockBean::getL).min().orElse(Double.NaN);
		
		double pctRange =  periodMax/periodMin -1; // OR  (periodMax-periodMin)/periodMin
		return pctRange;
	}
	
//	public static Double getPerformanceO2C(List<StockBean> subStockList) {
//		double percentageO2C = (subStockList.get(subStockList.size()-1).getC()  - subStockList.get(0).getO())/ subStockList.get(0).getO();		
//		return percentageO2C;
//	}
//	
//	public static Double getPerformanceO2H(List<StockBean> subStockList) {
//		double percentageO2C = (subStockList.get(subStockList.size()-1).getH()  - subStockList.get(0).getO())/ subStockList.get(0).getO();		
//		return percentageO2C;
//	}
}
