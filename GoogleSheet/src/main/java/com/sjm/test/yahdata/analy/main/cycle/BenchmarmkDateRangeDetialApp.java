package com.sjm.test.yahdata.analy.main.cycle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.cfg.GlobalConfig;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.helper.AnalyGeneralHelper;
import com.sjm.test.yahdata.analy.helper.CFGHelper;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.main.BaseApp;
import com.sjm.test.yahdata.analy.module.benchmarks.d2d.BenchmarkModule;
import com.sjm.test.yahdata.analy.module.lowhighdist.LowHighDateHelper;
import com.sjm.test.yahdata.analy.module.lowhighdist.bean.LowHighDateSimplifyResult;
import com.sjm.test.yahdata.analy.probability.bean.BenchmarkBean;
import com.sjm.test.yahdata.analy.probability.bean.BenchmarkBeanResult;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class BenchmarmkDateRangeDetialApp extends BaseApp{
	
	public static int REQUIRED_MIN_YEAR = 3;
	
	private static final String INTERAL = Const.INTERVAL_D;
	
	private static final String MMDD_START = "01-24";
	private static final String MMDD_END = "02-10";
	
	private static final int REQUIRE_MIN_DAYS = 4;
	private static final double REQUIRE_MIN_WIN_RATE = 0.6;
	private static final String END2END_PATTERN = Const.END2END_C2C;
	
//	private static  List<String> CODE_POOL = Arrays.asList("CNY=X");
//	public static List<String> CODE_POOL= HKStockListConfig.MAIN;
	private static  List<String> CODE_POOL = Arrays.asList("AMD");
//	private static  List<String> CODE_POOL = USStockListConfig.ALL;
//	private static  List<String> CODE_POOL = HKStockListConfig.ALL;
	private static boolean isShowDetail = false;
	
	public static void main(String[] args) throws Exception{
		CFGHelper.validateConfig();
		GlobalConfig.setYearOfStockData(20);
		List<StockBean> fullTrunkList = loadStockData(CODE_POOL, INTERAL);	
			
		exportResult( CODE_POOL, fullTrunkList);
	}
	
	public static void exportDetail(List<BenchmarkBean> benchBeanList) {
		if(isShowDetail == false)
			return;
		
		StringBuilder sb = new StringBuilder();
		sb.append("Detail\n");
		sb.append("Ticker\tYear\tRange\tC2C\tC2Highest(升波幅)\tC2Lowest(跌波幅)\tEnd-End LH\tEnd-End HL \n");
		for (BenchmarkBean bean : benchBeanList) {
			sb.append(bean.getStockCode());
			sb.append(bean.getYyyy());
			sb.append("\t"+bean.getStartMMdd()+"-"+bean.getEndMMdd());
			sb.append("\t"+GeneralHelper.toPct(bean.getPctC2C()));
			sb.append("\t"+GeneralHelper.toPct(bean.getPctC2Highest()));
			sb.append("\t"+GeneralHelper.toPct(bean.getPctC2Lowest()));
			
			sb.append("\t"+GeneralHelper.toPct(bean.getPctEnd2EndLH()));
			sb.append("\t"+GeneralHelper.toPct(bean.getPctEnd2EndHL()));
			sb.append("\n");
		}
		
		System.out.println(sb.toString());
	}
	
	public static void exportResult( List<String> stockPool, List<StockBean> fullTrunkList) {
		
			List<BenchmarkBeanResult> stockPerformanceResultList = new ArrayList<BenchmarkBeanResult>(10);
			List<BenchmarkBean> bencharmkBeanList = null;
			BenchmarkModule module = new BenchmarkModule();
			for (String code : stockPool) {
				log.info("\nprocessing "+code);
				List<StockBean> stocklist = StreamTransformHelper.extractByStockCode(fullTrunkList, code);				
				try{
					String beginDate = stocklist.get(0).getTxnDate();
					log.debug(code+" Load data, select data from "+beginDate +" \n");
				
				
					bencharmkBeanList = module.doDataRangeLogic(stocklist, MMDD_START, MMDD_END);
					BenchmarkBean recentMonthlyPerfromance = module.getRecentBenchmarkBean(stocklist, MMDD_START, MMDD_END);
					
					BenchmarkBeanResult stockResult = module.generateBenchmarkResult(bencharmkBeanList, recentMonthlyPerfromance, REQUIRE_MIN_WIN_RATE, REQUIRE_MIN_DAYS, END2END_PATTERN);
					exportDetail( bencharmkBeanList);
					
					if(stockResult!=null && stockResult.getNumOfYears()>= REQUIRED_MIN_YEAR) {
						LowHighDateSimplifyResult lowHighResult = LowHighDateHelper.generate(stocklist, MMDD_START, MMDD_END);
						stockResult.setLowHighDateSimplifyResult(lowHighResult);
						stockPerformanceResultList.add(stockResult);
					}else {
						log.debug(code+" don't have engouth data, Require  year: "+REQUIRED_MIN_YEAR);		
					}
				}catch(Exception e) {
					log.error("SKIP, due to error on : "+code+", list size = "+stocklist.size(), e);
				}
			}
				
		
        
        
		//ordering
        List<BenchmarkBeanResult> descList = stockPerformanceResultList.parallelStream()
        	    .sorted(Comparator.comparingDouble(BenchmarkBeanResult::getRisesRatioC2C).reversed()).collect(Collectors.toList());
        
        
//        log.info("\n\t# Watch Month:"+targetWatchMonth.replace("-", "")+", Win Rate: [BEST: "+best.getStockCode()+" "+GeneralHelper.to100Percentage(best.getUpRatio())+", WORST: "+worst.getStockCode()+" " + GeneralHelper.to100Percentage(worst.getUpRatio())+"]");
        	        
        String header = "\n\t# Range:"+MMDD_START +" To "+ MMDD_END;
        
        header +="\n\tRow\tCODE"
        		+ "\tY年數"
        		+ "\t升(CC)\t跌(CC)\t升(End2End-LH)\t跌(End2End-LH)\t升(End2End-HL)\t跌(End2End-HL)"
				+"\t下跌年份"
        		+ "\t低位日子\t低位日子Prob.\t高位日子\t高位日子Prob."        		
        		+ "\t跌回開始日期比例\t沒跌回起點的日期"
        		+ "\t中位(C-C)\tAvg(C-C)\tMax(C-C)\tMin(C-C)"
//        		+ "\t中位(E2E_LH)\tAvg(E2E_LH)\tMax(E2E_LH)\tMin(E2E_LH)"
//        		+ "\t中位(E2E_HL)\tAvg(E2E_HL)\tMax(E2E_HL)\tMin(E2E_HL)"
        		+ "\t中位(C-H)\tAvg(C-H)\tMax(C-H)\tMin(C-H)"
        		+ "\t中位(C-L)\tAvg(C-L)\tMax(C-L)\tMin(C-L)"
        		+ "\t中位(波幅)\tAvg(波幅)\tMax(波幅)\tMin(波幅)"
        		+ "\t最後一年同期"
        		+ "\tC-C "
        		+ "\t波幅"
        		+ "\tC-L "
        		+ "\tC-H "
        		+ "\tPATTERN (CC)";
//        log.info( header);
        
        StringBuilder sb = new StringBuilder();
        sb.append(header+"\n");
        for(int i=0; i< descList.size(); i++) {
        	BenchmarkBeanResult sumy = descList.get(i);       
			sb.append("\t "+(i+1)+"\t"+sumy.getStockCode().replace(".HK", "") ); 
			sb.append("\t"+ sumy.getNumOfYears() );
			sb.append("\t" + GeneralHelper.toPct(sumy.getRisesRatioC2C()));
			sb.append("\t" + GeneralHelper.toPct(sumy.getFallsRatioC2C()));
			sb.append("\t" + GeneralHelper.toPct(sumy.getRisesRatioEnd2EndL2H()));
			sb.append("\t" + GeneralHelper.toPct(sumy.getFallsRatioEnd2EndL2H()));
			sb.append("\t" + GeneralHelper.toPct(sumy.getRisesRatioEnd2EndH2L()));
			sb.append("\t" + GeneralHelper.toPct(sumy.getFallsRatioEnd2EndH2L()));
			
			sb.append("\t" + sumy.getO2cDownYearList() );
			sb.append("\t"+ ((sumy.getLowHighDateSimplifyResult()==null)?" \t \t \t ":sumy.getLowHighDateSimplifyResult().toPrintResult())) ;			
			sb.append("\t" + GeneralHelper.toPct(sumy.getFallBackToNegativeReturnRatio()));
			sb.append("\t" + sumy.getNonNegativeRatioDates());
			sb.append("\t" + sumy.getC2cStat().getDescription());
//			sb.append("\t" + sumy.getEnd2EndLHStat().getDescription());
//			sb.append("\t" + sumy.getEnd2EndHLStat().getDescription());
			sb.append("\t" + sumy.getC2phStat().getDescription());
			sb.append("\t" + sumy.getC2plStat().getDescription());
			sb.append("\t" + sumy.getPercentageRangeStat().getDescription());

			sb.append("\t" + sumy.getRecentBenchmarkBean().getStartTxnDate()  +"_"+ sumy.getRecentBenchmarkBean().getEndTxnDate() );
			sb.append("\t" + GeneralHelper.toPct(sumy.getRecentBenchmarkBean().getPctC2C()) );
			sb.append("\t" + GeneralHelper.toPct(sumy.getRecentBenchmarkBean().getPercentageRange()) );
//			sb.append("\t" + GeneralHelper.to100Pct(sumy.getRecentBenchmarkBean().getPctEnd2EndHL()) );			
			sb.append("\t" + GeneralHelper.toPct(sumy.getRecentBenchmarkBean().getPctC2Lowest()) );
			sb.append("\t" + GeneralHelper.toPct(sumy.getRecentBenchmarkBean().getPctC2Highest()) );
			sb.append("\t" + AnalyGeneralHelper.getDirectionText(sumy.getRecentBenchmarkBean().getPctC2C()) );
//			sb.append("\t" + AnalyGeneralHelper.getDirectionText(sumy.getRecentBenchmarkBean().getPctEnd2EndLH()) );
//			sb.append("\t" + AnalyGeneralHelper.getDirectionText(sumy.getRecentBenchmarkBean().getPctEnd2EndHL()) );
			
			if(sumy.getCurrentBenchmarkBean()!=null) {
				sb.append("\t" + sumy.getCurrentBenchmarkBean().getStartTxnDate() +"_"+ sumy.getCurrentBenchmarkBean().getEndTxnDate() );
				sb.append("\t" + GeneralHelper.toPct(sumy.getCurrentBenchmarkBean().getPctC2C()) );
				sb.append("\t" + GeneralHelper.toPct(sumy.getCurrentBenchmarkBean().getPercentageRange()) );
//				sb.append("\t" + GeneralHelper.to100Pct(sumy.getCurrentBenchmarkBean().getPctEnd2EndHL()) );
				sb.append("\t" + GeneralHelper.toPct(sumy.getCurrentBenchmarkBean().getPctC2Lowest()) );
				sb.append("\t" + GeneralHelper.toPct(sumy.getCurrentBenchmarkBean().getPctC2Highest()) );
				sb.append("\t" + AnalyGeneralHelper.getDirectionText(sumy.getCurrentBenchmarkBean().getPctC2C()) );
//				sb.append("\t" + AnalyGeneralHelper.getDirectionText(sumy.getCurrentBenchmarkBean().getPctEnd2EndLH()) );
//				sb.append("\t" + AnalyGeneralHelper.getDirectionText(sumy.getCurrentBenchmarkBean().getPctEnd2EndHL()) );
			}
			sb.append("\n");
		}
        log.info( sb.toString());
		
        
	}

}
