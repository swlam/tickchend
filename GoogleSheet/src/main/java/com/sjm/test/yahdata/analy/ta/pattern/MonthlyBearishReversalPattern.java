package com.sjm.test.yahdata.analy.ta.pattern;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MonthlyBearishReversalPattern {

	public MonthlyBearishReversalPattern() {
	}
	
	public void doFunc(List<String> stockPool, List<StockBean> fullTrunkList,String START_DATE, String END_DATE) {
		List<String>  bearishresultStockCodeList = new ArrayList<String>(10);
		List<String>  bullishResultStockCodeList = new ArrayList<String>(10);
		
		stockPool.parallelStream().forEach(productCode -> {
//			logger.info("\t"+productCode );
			try {		
				List<StockBean> productStockList = StreamTransformHelper.extractByStockCode(fullTrunkList, productCode);
				log.info("\t "+productCode+", size = "+productStockList.size() );	
				if(productStockList.isEmpty() || productStockList.size()<12) {
					log.info( "SKIP "+ productCode);
				}
				
				List<StockBean> stockPeriodList = StreamTransformHelper.extractData(productStockList, START_DATE, END_DATE);
				
				int targetMonthValue = LocalDate.parse(END_DATE).getMonthValue();
				int actualDataMonthValue = LocalDate.parse(stockPeriodList.get(stockPeriodList.size()-1).getTxnDate()).getMonthValue();
				
				int N = stockPeriodList.size() - 3;
				boolean foundBearishReversal = this.foundBearishReversalPattern(productCode, stockPeriodList, N);
				boolean foundBullishReversal = this.foundBullishReversalPattern(productCode, stockPeriodList, N);
				if(foundBearishReversal && targetMonthValue==actualDataMonthValue) {
					bearishresultStockCodeList.add(productCode);
				}
				
				if(foundBullishReversal) {
					bullishResultStockCodeList.add(productCode);
				}
				

			}catch(Exception e) {
				log.error(null, e);
			}
		});
		
		
		System.out.println("========== RESULT ========== ");
		System.out.println("TYPE \t Count \t Total \t Ticker List");
		System.out.println("Bearish Reversal\t"+ bearishresultStockCodeList.size()+"\t"+stockPool.size()+ "\t"+bearishresultStockCodeList);
		System.out.println("Bullish Reversal\t"+ bullishResultStockCodeList.size()+"\t"+stockPool.size()+ "\t"+bullishResultStockCodeList);
	}
		
	public boolean foundBearishReversalPattern(String stockCode , List<StockBean> stkList, int N) {
		

		boolean r1 = false; //Avg close (N-1, N, N+1)  < Avg close (N-1-6, N-6, N+1-6) AND AvgBodyTop(N-1,N, N+1) < AvgBodyBottom(N-1-6, N-6, N+1-6)
//		boolean r2 = false;	//HighestPrice(N+1,N+2) > HighestPrice(N-1,N,N+1)
		boolean r3 = false;	//Close(N+2) > BodyTop(N+1) 
		boolean r4 = false;	//Lowest(N+1,N+2) > Lowest(N)
		
		double avgCloseA = Arrays.asList(stkList.get(N-1), stkList.get(N), stkList.get(N+1)).stream().mapToDouble(x->x.getC()).average().orElse(0);
		double avgCloseB = Arrays.asList(stkList.get(N-1-6),stkList.get(N-6), stkList.get(N+1-6)).stream().mapToDouble(x->x.getC()).average().orElse(0);
		
		double avgBodyTopA = Arrays.asList(stkList.get(N-1), stkList.get(N), stkList.get(N+1)).stream().mapToDouble(x->x.getBodyTop()).average().orElse(0);
		double avgBodyBtmB = Arrays.asList(stkList.get(N-1-6),stkList.get(N-6), stkList.get(N+1-6)).stream().mapToDouble(x->x.getBodyBottom()).average().orElse(0);
		
		double diff = (avgBodyTopA-avgBodyBtmB) /avgBodyBtmB;
		
		
		if(avgCloseA < avgCloseB && diff<-0.2) {
			r1 = true;
		}		
		
//		double a = Arrays.asList(stkList.get(N+1), stkList.get(N+2)).stream().mapToDouble(x->x.getC()).max().orElse(0);
//		double b = Arrays.asList(stkList.get(N-1),stkList.get(N), stkList.get(N+1)).stream().mapToDouble(x->x.getH()).max().orElse(0);
//		if(a> b) {
//			r2 = true;
//		}
		
//		a = Arrays.asList(stkList.get(N+1), stkList.get(N+2)).stream().mapToDouble(x->x.getBodyTop()).max().orElse(0);
//		b = stkList.get(N+3).getC();
		if(stkList.get(N+2).getC() > stkList.get(N+1).getBodyTop()) {
			r3 = true;
		}
		
		double a = Arrays.asList(stkList.get(N+1), stkList.get(N+2)).stream().mapToDouble(x->x.getL()).min().orElse(0);
		double b = stkList.get(N).getL();
		
		if( a > b ) 
		{
			r4 = true;
		}
		
		return r1&&r3&&r4;
	}
	
	
	public boolean foundBullishReversalPattern(String stockCode , List<StockBean> stkList, int N) {
//		boolean r1 = false; //Avg close (N-1, N, N+1)  < Avg close (N-1-6, N-6, N+1-6) AND AvgBodyTop(N-1,N, N+1) < AvgBodyBottom(N-1-6, N-6, N+1-6)
//		boolean r2 = false;	//HighestPrice(N+2,N+3) > HighestPrice(N-1,N,N+1)
//		boolean r3 = false;	//Close(N+3) > BodyTop(N+1,N+2)
//		boolean r4 = false;	//Lowest(N+1) < Lowest(N+2) < Lowest(N+3)
		
		boolean r1 = false; //AVGBodyBottom(N-1,N, N+1) > AvgBodyTop(N-1-6, N-6, N+1-6)
		boolean r2 = false;	//Lowest(N+1,N+2) < Lowest(N)
		boolean r3 = false;	//LowestClose(N+2) < LowestClose(N-1,N,N+1)
		boolean r4 = false;	//Highest(N)  > Highest(N+1,N+3)
		
//		double avgCloseA = Arrays.asList(stkList.get(N-1), stkList.get(N), stkList.get(N+1)).stream().mapToDouble(x->x.getC()).average().orElse(0);
//		double avgCloseB = Arrays.asList(stkList.get(N-1-6),stkList.get(N-6), stkList.get(N+1-6)).stream().mapToDouble(x->x.getC()).average().orElse(0);
		
		double avgBodyBtmA = Arrays.asList(stkList.get(N), stkList.get(N+1), stkList.get(N+2)).stream().mapToDouble(x->x.getBodyBottom()).average().orElse(0);
		double avgBodyBtmB = Arrays.asList(stkList.get(N-1-6),stkList.get(N-6), stkList.get(N+1-6)).stream().mapToDouble(x->x.getBodyBottom()).average().orElse(0);
		
		double diff = (avgBodyBtmA-avgBodyBtmB) /avgBodyBtmB;
		if(avgBodyBtmA < avgBodyBtmB && diff<0.2) {
			r1 = true;
		}
		
		double a = Arrays.asList(stkList.get(N+1), stkList.get(N+2)).stream().mapToDouble(x->x.getL()).min().orElse(0);
		double b = stkList.get(N).getL();
		if(a < b) {
			r2 = true;
		}
		
		a = stkList.get(N+2).getL();
		b = Arrays.asList(stkList.get(N-1), stkList.get(N), stkList.get(N+1)).stream().mapToDouble(x->x.getC()).min().orElse(0);
		
		if(a < b) {
			r3 = true;
		}
		
		if( stkList.get(N).getH() > stkList.get(N+1).getH() || stkList.get(N).getH() > stkList.get(N+2).getH() ) 
		{
			r4 = true;
		}
		
		return r1&&r2&&r3&&r4;
	}

}
