package com.sjm.test.yahdata.benchmarks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.SectorStatsSummary;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.cfg.BenchmarksConfig;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.helper.CFGHelper;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.ta.helper.SectorAnalystHelper;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class BenchmarksFunction {
//	private static final log log = log.getlog(BenchmarksFunction.class);
	
	private Map<String, List<BenchmarkSummary>> mapPresent = new LinkedHashMap<String, List<BenchmarkSummary>> ();
	private Map<String, List<BenchmarkSummary>> mapFewDayAgo = new LinkedHashMap<String, List<BenchmarkSummary>> ();
	private static final int DAY_OFFSET = 3;
	
	public void put2MapPresent(String keyDays, BenchmarkSummary bean) {

		if( mapPresent.get(keyDays)==null ) {
			mapPresent.put(keyDays, new ArrayList<BenchmarkSummary>(0));
		}
		mapPresent.get(keyDays).add(bean);
	}
	
	public void put2MapFewDaysAgo(String keyDays, BenchmarkSummary bean) {

		if( mapFewDayAgo.get(keyDays)==null ) {
			mapFewDayAgo.put(keyDays, new ArrayList<BenchmarkSummary>(0));
		}
		mapFewDayAgo.get(keyDays).add(bean);
	}
	
	public  Map<String, List<BenchmarkSummary>> getMapPresent(){
		return mapPresent;
	}
	
	public  Map<String, List<BenchmarkSummary>> getMapFewDayAgo(){
		return mapFewDayAgo;
	}
	
	
	public BenchmarksFunction() {
		// TODO Auto-generated constructor stub
	}
	
	public void doBenchmarks(List<String> stockPool, List<StockBean> fullTrunkList) {
		try {
			
			for (String code : stockPool) {
				try {
					List<StockBean> stockList = StreamTransformHelper.extractByStockCode(fullTrunkList, code);
					func(stockList, 0);
//					func(stockList, DAY_OFFSET);
				}catch(Exception e ) {
					log.error("Exception , stock code = "+code, e);
				}
			}
			
			this.printSummaryMap( getMapPresent() );
//			this.printSummaryMap( getMapFewDayAgo() );
			
		}catch(Exception e) {
			log.error(null, e);

		}
	}
	
	public void findBetterBenchmarkStockList(List<String> tickerPool, String targetTicker, List<StockBean> fullTrunkList, String yyyymmdd, String yyyymmddEnd) {
		try {
			
			
			List <BenchmarkSummary> benchSumyList = new ArrayList <BenchmarkSummary>(); 
			
			double targetTickerBenchPercentage = -99;
			for (String code : tickerPool) {
				try {
					List<StockBean> stockList = StreamTransformHelper.extractByStockCode(fullTrunkList, code);
					
					
					BenchmarkSummary sumy = this.buildBenchmarkSummary(stockList, yyyymmdd, yyyymmddEnd);
					
					if(targetTicker.equalsIgnoreCase(code)) {
						targetTickerBenchPercentage = sumy.getPercentageE2EC2C();	
					}
					
					benchSumyList.add(sumy);
				}catch(Exception e ) {
					log.error("Exception , stock code = "+code, e);
				}
			}
			
			
			
			final double percentage = targetTickerBenchPercentage;
			List<BenchmarkSummary> filterBenchmarkSumyList = benchSumyList.parallelStream().filter(x-> x.getPercentageE2EC2C()>=percentage).collect(Collectors.toList());
			
			
			Map<String, List<BenchmarkSummary>> map = new LinkedHashMap<String, List<BenchmarkSummary>>();
			map.put(yyyymmdd+"-"+yyyymmddEnd, filterBenchmarkSumyList);
			printSummaryMap_doing(map);
//			this.printSummaryMap( getMapPresent() );
//			this.printSummaryMap( getMapFewDayAgo() );
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void doBenchmarksDateRange(List<String> stockPool, List<StockBean> fullTrunkList, String yyyymmdd, String yyyymmddEnd) {
		try {
			List <BenchmarkSummary> benchSumyList = new ArrayList <BenchmarkSummary>(); 
			for (String code : stockPool) {
				try {
					List<StockBean> stockList = StreamTransformHelper.extractByStockCode(fullTrunkList, code);
					
					BenchmarkSummary sumy = this.buildBenchmarkSummary(stockList, yyyymmdd, yyyymmddEnd);
					benchSumyList.add(sumy);
				}catch(Exception e ) {
					log.error("Exception , stock code = "+code, e);
				}
			}
			
			Map<String, List<BenchmarkSummary>> map = new LinkedHashMap<String, List<BenchmarkSummary>>();
			map.put(yyyymmdd+"-"+yyyymmddEnd, benchSumyList);
			printSummaryMap_doing(map);
//			this.printSummaryMap( getMapPresent() );
//			this.printSummaryMap( getMapFewDayAgo() );
			
			
			
			
			List<SectorStatsSummary> sectorStatList =  SectorAnalystHelper.getSectorStatsSummary(benchSumyList);
			System.out.println(SectorStatsSummary.getHeader());
			for (SectorStatsSummary sectorStatsSummary : sectorStatList) {
				System.out.println(sectorStatsSummary.toString() );
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public BenchmarkSummary buildBenchmarkSummary(List<StockBean> stockList, String yyyymmdd, String yyyymmddEnd) {
		BenchmarkSummary summaryBean = new BenchmarkSummary();
		StockBean latestStock =null;
		StockBean fromStock = null;

		if(yyyymmdd!=null && yyyymmddEnd !=null) {

			List<StockBean> subList = stockList.parallelStream().filter(
					x->  x.getTxnDateInt() >= StreamTransformHelper.yyyyMMddtoInt(yyyymmdd) && x.getTxnDateInt() <= StreamTransformHelper.yyyyMMddtoInt(yyyymmddEnd)
			).collect(Collectors.toList());
	
			fromStock = subList.get(0);
			latestStock = subList.get(subList.size()-1);
	
			StockBean minByL = subList
				      .stream()
				      .min(Comparator.comparing(StockBean::getL))
				      .orElseThrow(NoSuchElementException::new);
			StockBean maxByH = subList
				      .stream()
				      .max(Comparator.comparing(StockBean::getH))
				      .orElseThrow(NoSuchElementException::new);
			double lowest = minByL.getL();
			double highest = maxByH.getH();
			
			
			summaryBean.setFromDate(fromStock.getTxnDate());
			summaryBean.setToDate(latestStock.getTxnDate());
			// summaryBean.setPastNumOfDays(days);
	
			summaryBean.setStockCode(fromStock.getStockCode());
			double percentageC2C = (latestStock.getC() - fromStock.getC())/fromStock.getC();
			double percentageE2EL2H = (latestStock.getH() - fromStock.getL())/fromStock.getL();
			double percentageE2EL2C = (latestStock.getC() - fromStock.getL())/fromStock.getL();
			
//			double percentageE2EH2L = (latestStock.getL() - fromStock.getH())/fromStock.getH();
			
			
			double percentageL2PeriodH = (highest - fromStock.getL())/fromStock.getL();
			double percentageE2EH2L = (latestStock.getL() - fromStock.getH())/fromStock.getH();
			double percentageE2EH2C = (latestStock.getC() - fromStock.getH())/fromStock.getH();
			
			double percentageC2Lowest = ( lowest-fromStock.getC() )/fromStock.getC();			
			double percentageC2Highest = ( highest-fromStock.getC() )/fromStock.getC();
			
			
			summaryBean.setDailyChangePct(this.getDailyChangePct(stockList));
			summaryBean.setDailyEstTradeAmount(this.getEstTradeAmount(stockList));
			summaryBean.setPercentageC2PeriodL(percentageC2Lowest);
			summaryBean.setPercentageC2PeriodH(percentageC2Highest);
			summaryBean.setPercentageE2EC2C(percentageC2C);
			summaryBean.setPercentageE2EL2C(percentageE2EL2C);
			summaryBean.setPercentageL2PeriodH(percentageL2PeriodH);
			summaryBean.setPercentageE2EL2H(percentageE2EL2H);
			summaryBean.setPercentageE2EH2L(percentageE2EH2L);
			summaryBean.setPercentageE2EH2C(percentageE2EH2C);
			
			summaryBean.setSector((CFGHelper.getStockProfileMap().get(summaryBean.getStockCode())==null?Const.NA:CFGHelper.getStockProfileMap().get(summaryBean.getStockCode()).getSector()));
			
			return summaryBean;
		}
		return null;
		
	}
	
	public void func(List<StockBean> stockList, int offset) {
		BenchmarkSummary sumy = this.buildBenchmarkSummary(stockList, BenchmarksConfig.START_DATE, null, offset);
		if(offset==0) {
			this.put2MapPresent(BenchmarksConfig.START_DATE, sumy);
		}else {
			this.put2MapFewDaysAgo(BenchmarksConfig.START_DATE, sumy);
		}
		
		//////////
		for (Integer days : BenchmarksConfig.PAST_NUM_OF_DAYS) {
			BenchmarkSummary summaryBean = this.buildBenchmarkSummary(stockList, null, days, offset);
			
			
			if(offset==0) {
				this.put2MapPresent(""+days, summaryBean);
			}else {
				this.put2MapFewDaysAgo(""+days, summaryBean);
			}
		}
						
		
	}
	

	
	public BenchmarkSummary buildBenchmarkSummary(List<StockBean> stockList, String yyyymmdd, Integer days, Integer offset) {
		BenchmarkSummary summaryBean = new BenchmarkSummary();
		int lastIdx = 0;
		StockBean latestStock =null;
		try {
			 lastIdx = stockList.size()-1-offset;
			 latestStock = stockList.get(lastIdx);
		}catch(Exception e) {
			log.error("lastIdx = "+lastIdx, e);
			return null;
			
		}
		StockBean fromStock = null;
		if(yyyymmdd==null && days !=null) {
			int idx = lastIdx - days;
			fromStock = stockList.get(idx);			
			
			summaryBean.setFromDate(fromStock.getTxnDate());
			summaryBean.setToDate(latestStock.getTxnDate());
			summaryBean.setPastNumOfDays(days);
			
			summaryBean.setStockCode(fromStock.getStockCode());
		}else {
			List<StockBean> subList = stockList.parallelStream().filter(x->  x.getTxnDateInt() >= StreamTransformHelper.yyyyMMddtoInt(yyyymmdd)).collect(Collectors.toList());
						
			fromStock = subList.get(0);
			summaryBean.setFromDate(fromStock.getTxnDate());
			summaryBean.setToDate(latestStock.getTxnDate());
//				summaryBean.setPastNumOfDays(days);
			
			summaryBean.setStockCode(fromStock.getStockCode());
		}
		
		double percentage = (latestStock.getC() - fromStock.getC())/fromStock.getC();
		summaryBean.setPercentageE2EC2C(percentage);
		return summaryBean;
	}
	
	
	
	
	public void printSummaryMap(Map<String, List<BenchmarkSummary>> benchMarkMap) {		
		
		String upToDate = benchMarkMap.get(BenchmarksConfig.PAST_NUM_OF_DAYS.get(0)+"").get(0).getToDate();
		
		StringBuilder sbmsg = new StringBuilder();
		sbmsg.append("Up to "+upToDate+"\n");
		
		
		sbmsg.append("\t");
		//Sorting, by percentage DESC
		for (String name : benchMarkMap.keySet()) {			
			List<BenchmarkSummary> descList = benchMarkMap.get(name).parallelStream()
	        	    .sorted(Comparator.comparingDouble(BenchmarkSummary::getPercentageE2EC2C).reversed()).collect(Collectors.toList());
			benchMarkMap.put(name, descList);			
			sbmsg.append(name+" Days\t\t");
			
		}
		
		sbmsg.append("\n");
		sbmsg.append("#\t");
		for (String name : benchMarkMap.keySet()) {
			sbmsg.append("CODE\t%\t");
			sbmsg.append("Name\tSector\t");
			
		}
		sbmsg.append("\n");
		
		
		
		int numOfStock = benchMarkMap.get(BenchmarksConfig.PAST_NUM_OF_DAYS.get(0)+"").size();
		for(int idx = 0; idx < numOfStock; idx++ ) {
			sbmsg.append((idx+1) + "\t");
			for (String name : benchMarkMap.keySet()) {	
				try {
					if( benchMarkMap.get(name).size()<=idx ) {
						//SKIP 
						continue;
					}
					BenchmarkSummary sumy = benchMarkMap.get(name).get(idx);
					sbmsg.append( sumy.getStockCode().replace(".HK", "")+"\t" +GeneralHelper.toPct(sumy.getPercentageE2EC2C()) + "\t");
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			sbmsg.append("\n");
		}
		log.info("\n"+sbmsg.toString());

		
	}

	

	
	public void printSummaryMap_doing(Map<String, List<BenchmarkSummary>> benchMarkMap) {
		StringBuilder sbmsg = new StringBuilder();
//		sbmsg.append("Up to "+upToDate+"\n");		
		sbmsg.append("\n");		
		
		int numOfStock = 0;
		if(benchMarkMap!=null) {
			for (String name : benchMarkMap.keySet()) {			
				List<BenchmarkSummary> descList = benchMarkMap.get(name).parallelStream()
		        	    .sorted(Comparator.comparingDouble(BenchmarkSummary::getPercentageE2EC2C).reversed()).collect(Collectors.toList());
				benchMarkMap.put(name, descList);			
				numOfStock = descList.size();
				sbmsg.append("#\t"+name+"\tName\tSector\tDaily Est. Amount\tDaily%\tC2C %\tC2H %\tC2L%"
						+ "\tL2C(E2E) %\tL2H(E2E) %\tH2L(E2E) %\tH2C(E2E)\tL2Period-H %\tDate Range");
			}
			

		}
		sbmsg.append("\n");
		
		
		for(int idx = 0; idx < numOfStock; idx++ ) {
			sbmsg.append((idx+1) + "\t");
			for (String name : benchMarkMap.keySet()) {	
				try {
					if( benchMarkMap.get(name).size()<=idx ) {
						//SKIP 
						continue;
					}
					
					BenchmarkSummary sumy = benchMarkMap.get(name).get(idx);
					sbmsg.append( sumy.getStockCode().replace(".HK", "") ); 					
					sbmsg.append("\t"+ (CFGHelper.getStockProfileMap().get(sumy.getStockCode())==null?Const.NA:CFGHelper.getStockProfileMap().get(sumy.getStockCode()).getName()));
//					sbmsg.append("\t"+ (CFGHelper.getStockMetaMap().get(sumy.getStockCode())==null?"NA":CFGHelper.getStockMetaMap().get(sumy.getStockCode()).getSector()));
					sbmsg.append("\t"+ sumy.getSector());
					sbmsg.append("\t"+ sumy.getDailyEstTradeAmount());
					sbmsg.append( "\t" +GeneralHelper.toPct(sumy.getDailyChangePct()));
					
					
					sbmsg.append( "\t" +GeneralHelper.toPct(sumy.getPercentageE2EC2C()));
					sbmsg.append( "\t" +GeneralHelper.toPct(sumy.getPercentageC2PeriodH()));
					sbmsg.append( "\t" +GeneralHelper.toPct(sumy.getPercentageC2PeriodL()));
					sbmsg.append( "\t" +GeneralHelper.toPct(sumy.getPercentageE2EL2C()));
					sbmsg.append( "\t" +GeneralHelper.toPct(sumy.getPercentageE2EL2H()));
					sbmsg.append( "\t" +GeneralHelper.toPct(sumy.getPercentageE2EH2L()));
					sbmsg.append( "\t" +GeneralHelper.toPct(sumy.getPercentageE2EH2C()));
					sbmsg.append( "\t" +GeneralHelper.toPct(sumy.getPercentageL2PeriodH()));
					
					sbmsg.append("\t[" +sumy.getFromDate() +" to " +sumy.getToDate()+"]");
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			sbmsg.append("\n");
		}
		log.info("\n"+sbmsg.toString());
		
	}
	
	public double getDailyChangePct(List<StockBean> stockList) {
		StockBean curr = stockList.get(stockList.size()-1);
//		StockBean prev = stockList.get(stockList.size()-2);
//		
//		double pct = (curr.getC()/prev.getC())-1.0;
//		
		return curr.getDayChgPct();
	}
	public double getEstTradeAmount(List<StockBean> stockList) {
		StockBean curr = stockList.get(stockList.size()-1);
		return curr.getEstTradeAmount();
	}

}
