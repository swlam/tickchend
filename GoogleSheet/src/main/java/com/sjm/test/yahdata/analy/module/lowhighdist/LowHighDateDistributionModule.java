package com.sjm.test.yahdata.analy.module.lowhighdist;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.maas.util.DateHelper;
import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.PeriodStatisBean;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.helper.StockDataDateRangeHelper;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.module.lowhighdist.bean.LowHighDateRangeBean;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class LowHighDateDistributionModule {
	
	public LowHighDateDistributionModule() {}
	

	public List<LowHighDateRangeBean> generateLowHighDateRangeList(List<StockBean> trunkList, String startMMdd, String endMMdd) {
		
		String stockCode = trunkList.get(0).getStockCode();
		
		log.debug(stockCode + ",  startMMdd = "+startMMdd+ ", endMMdd = "+endMMdd);
		
		String initTxnDate = trunkList.get(0).getTxnDate();		
		LocalDate localDate = LocalDate.parse(initTxnDate);		
		String yyyy = "" + localDate.getYear();	//init yyyy
		
		
		String endTxnDate = trunkList.get(trunkList.size()-1).getTxnDate();		
		int endTxnYearInt = GeneralHelper.getYearValue(endTxnDate);
		
		
		List<LowHighDateRangeBean> lowHighList  = new ArrayList<LowHighDateRangeBean>();
		int yyyyInt = 0;
		yyyyInt = Integer.parseInt(yyyy);
		
		boolean isBeforeInitDate = DateHelper.before(yyyy+"-"+startMMdd, initTxnDate);
		if(isBeforeInitDate) {		
			// junmp to next year
			yyyyInt++;
			yyyy = ""+yyyyInt;
			localDate = localDate.plusYears(1);
		}
		
		List<StockBean>  ymList = null;
		do {

			ymList = StreamTransformHelper.extractMonthData(trunkList, yyyy);
			
			
			StockBean customStartDateBean = StockDataDateRangeHelper.findCustomStartDateStockBean(ymList, yyyy+"-"+startMMdd);
			List<StockBean> stockPeriodMonthList = StreamTransformHelper.extractData(ymList, yyyy+"-"+startMMdd, yyyy+"-"+endMMdd);
			
			if(customStartDateBean ==null || stockPeriodMonthList.isEmpty()) {
//				log.warn("SKIP. ("+stockCode+": " +yyyy+"-"+startMMdd+" to "+endMMdd+ "), customStartDateBean: "+ customStartDateBean+ ", stockPeriodMonthList="+stockPeriodMonthList);
			}else {
				StockBean minDataPeriod = stockPeriodMonthList.stream().min(Comparator.comparingDouble(StockBean::getL)).get();
				StockBean maxDataPeriod = stockPeriodMonthList.stream().max(Comparator.comparingDouble(StockBean::getH)).get();						

				// example: percentage = (now - prev)/prev
				
				LowHighDateRangeBean lowHighBean = new LowHighDateRangeBean();
				
				lowHighBean.setPeriodHighestPriceDate(maxDataPeriod.getTxnDate());
				lowHighBean.setPeriodHighestPrice(maxDataPeriod.getH());
				lowHighBean.setPeriodLowestPriceDate(minDataPeriod.getTxnDate());
				lowHighBean.setPeriodLowestPrice(minDataPeriod.getL());
				
				if(customStartDateBean!=null) {
					//assign value
					lowHighBean.setCustomStartDate(customStartDateBean.getTxnDate());
					lowHighBean.setCustomStartDatePrice(customStartDateBean.getL());
					
					//calculate the percentage base on above assigned value
					double percentageCustomDateToHDate = (maxDataPeriod.getH() - customStartDateBean.getL()) / customStartDateBean.getL();
					double percentageL2H = (maxDataPeriod.getH() - lowHighBean.getPeriodLowestPrice()) / lowHighBean.getPeriodLowestPrice();								
					double custom2LPercentage = (lowHighBean.getPeriodLowestPrice() - lowHighBean.getCustomStartDatePrice())/lowHighBean.getCustomStartDatePrice();
									
					lowHighBean.setPercentageCustomDateToHDate(percentageCustomDateToHDate);// growth ratio of specified date to the highest price date
					lowHighBean.setPercentagePeriodLToH(percentageL2H);
					lowHighBean.setPercentageCustomDateToLDate(custom2LPercentage);
					
					//check NOT is current YEAR
					if(lowHighBean.getCustomStartDate().substring(0, 4).equalsIgnoreCase( LocalDate.now().getYear()+"") == false) {
						lowHighList.add(lowHighBean);
					}
					
					PeriodStatisBean periodStatBean = this.calcPeriodStat(stockPeriodMonthList);
					lowHighBean.setPeriodStatisBean(periodStatBean);
					
				}
			}

			
			
			
			
			
			
			
			
			// junmp to next year
			localDate = localDate.plusYears(1);
			yyyy = "" + localDate.getYear();
			yyyyInt = Integer.parseInt(yyyy);
			
			
		} while ( yyyyInt <= endTxnYearInt);
		
		return lowHighList;
	}
	
	public PeriodStatisBean calcPeriodStat(List<StockBean> stockPeriodMonthList) {
		StockBean first = stockPeriodMonthList.get(0);
		StockBean last = stockPeriodMonthList.get(stockPeriodMonthList.size()-1);
		
		double periodL = stockPeriodMonthList.stream().mapToDouble(StockBean::getL).min().orElse(Double.NaN);
		double periodH = stockPeriodMonthList.stream().mapToDouble(StockBean::getH).max().orElse(Double.NaN);
		
		double c2c = (last.getC() - first.getC())/first.getC();
		double c2h = (periodH - first.getC())/first.getC();
		double c2l = (periodL - first.getC())/first.getC();
		
		double l2h = (periodH - periodL)/periodL;
		
		PeriodStatisBean rtn = new PeriodStatisBean();
		rtn.setPeriodC2CRatio(c2c);
		rtn.setPeriodC2HRatio(c2h);
		rtn.setPeriodC2LRatio(c2l);
		rtn.setPeriodL2HRatio(l2h);
		
		rtn.setPeriodStart(first.getTxnDate());
		rtn.setPeriodEnd(last.getTxnDate());
		rtn.setStockCode(first.getStockCode());
		return rtn;
		
	}
	
	public void exportResult(List<StockBean> trunkList, String startMMdd, String endMMdd) throws Exception {
		String stockCode = trunkList.get(0).getStockCode();
		List<LowHighDateRangeBean> lowHighList = generateLowHighDateRangeList(trunkList, startMMdd, endMMdd);
		

		String title = "\n**** StockCode : "+stockCode+", startMMdd = "+startMMdd+ ", endMMdd = "+endMMdd+", (L to H),  Years = "+lowHighList.size()+" ****";
		title += "\nFROM-DATE\tPERIOD-L-DATE\tPERIOD-H-DATE\tFROM-2-H(%) \tL-2-H(%)\tFROM-2-L(%) \tPATTERN ";
		log.info(title);
		
		
		for (LowHighDateRangeBean elemt : lowHighList) {
			
			System.out.println(elemt.printCustomDate2MaxH());			
		}
				
//		lowHighList.stream().filter(d -> d.getPeriodHighestPriceDate())
		
//		double avgC2H = lowHighList.stream().mapToDouble(d -> d.getPercentageCustomDateToHDate()).average().orElse(0.0);
//		double avgC2L = lowHighList.stream().mapToDouble(d -> d.getPercentageCustomDateToLDate()).average().orElse(0.0);
//		double avgL2H = lowHighList.stream().mapToDouble(d -> d.getPercentagePeriodLToH()).average().orElse(0.0);
		
		
		
		
		
		
		List<PeriodStatisBean> periodStaticList = lowHighList.stream().map(LowHighDateRangeBean::getPeriodStatisBean).collect(Collectors.toCollection(ArrayList::new));
		double periodC2CAvg = periodStaticList.stream().mapToDouble(PeriodStatisBean::getPeriodC2CRatio).average().orElse(Double.NaN);
		double periodC2LAvg = periodStaticList.stream().mapToDouble(PeriodStatisBean::getPeriodC2LRatio).average().orElse(Double.NaN);
		double periodC2HAvg = periodStaticList.stream().mapToDouble(PeriodStatisBean::getPeriodC2HRatio).average().orElse(Double.NaN);
		double periodL2HAvg = periodStaticList.stream().mapToDouble(PeriodStatisBean::getPeriodL2HRatio).average().orElse(Double.NaN);
		
		String msg = "         \t             \tAverage :\t"+GeneralHelper.toPct(periodC2HAvg)+" \t"+GeneralHelper.toPct(periodL2HAvg)+"\t"+GeneralHelper.toPct(periodC2LAvg)+"";
		System.out.println(msg);
		
		/// next phase
		
//		List<String> periodLowDateList = lowHighList.stream()
//				.map(LowHighDateRangeBean::getPeriodLowestPriceDate).collect(Collectors.toList());
//		List<String> periodHighDateList = lowHighList.stream()
//				.map(LowHighDateRangeBean::getPeriodHighestPriceDate).collect(Collectors.toList());
//		
//		System.out.println("\t Distribution of the lowest price date: ");
//		LowHighDateDistrDetailResultSummary resultSummary = (LowHighDateDistrDetailResultSummary)LowHighDateDistributionHelper.exploreDateDistribution(stockCode, periodLowDateList, startMMdd, endMMdd );
//		resultSummary.setPeriodC2CAvg(periodC2CAvg);
//		resultSummary.setPeriodC2HAvg(periodC2HAvg);
//		resultSummary.setPeriodC2LAvg(periodC2LAvg);
//		resultSummary.setPeriodL2HAvg(periodL2HAvg);
//		System.out.println(resultSummary.toPrintDetailString());
//		
//		System.out.println("\n\t Distribution of the highest price date: ");
//		resultSummary = (LowHighDateDistrDetailResultSummary)LowHighDateDistributionHelper.exploreDateDistribution(stockCode, periodHighDateList, startMMdd, endMMdd );
//		resultSummary.setPeriodC2CAvg(periodC2CAvg);
//		resultSummary.setPeriodC2HAvg(periodC2HAvg);
//		resultSummary.setPeriodC2LAvg(periodC2LAvg);
//		resultSummary.setPeriodL2HAvg(periodL2HAvg);
//		System.out.println(resultSummary.toPrintDetailString());
		
	}
			
	

}
