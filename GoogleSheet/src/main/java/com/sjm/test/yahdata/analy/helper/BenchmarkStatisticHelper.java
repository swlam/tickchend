package com.sjm.test.yahdata.analy.helper;

import java.util.List;
import java.util.stream.Collectors;

import com.sjm.test.yahdata.analy.bean.BaseSummaryStatistics;
import com.sjm.test.yahdata.analy.bean.RisesFallsRatio;
import com.sjm.test.yahdata.analy.conts.Const;
//import com.sjm.test.yahdata.analy.probability.bean.BenchmarkBean;
import com.sjm.test.yahdata.analy.probability.bean.BenchmarkBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BenchmarkStatisticHelper {
	
	public BenchmarkStatisticHelper() {
	}

	//Ratio of rises to falls
	public static RisesFallsRatio getRisesFallsRatioResult(List<BenchmarkBean> benchmarkBeanlist, String end2EndPattern) {
		long upCnt = 0;
		long downCnt = 0;
		if(Const.END2END_C2C.equalsIgnoreCase(end2EndPattern)) {
			upCnt = benchmarkBeanlist.parallelStream().filter(x -> x.getPctC2C()>0.0).count();	
			downCnt = benchmarkBeanlist.parallelStream().filter(x -> x.getPctC2C()<0.0).count();		
		}else if(Const.END2END_O2C.equalsIgnoreCase(end2EndPattern)) {
			upCnt = benchmarkBeanlist.parallelStream().filter(x -> x.getPctO2C()>0.0).count();	
			downCnt = benchmarkBeanlist.parallelStream().filter(x -> x.getPctO2C()<0.0).count();		
		}else if(Const.END2END_L2H.equalsIgnoreCase(end2EndPattern)) {
			upCnt = benchmarkBeanlist.parallelStream().filter(x -> x.getPctEnd2EndLH()>0.0).count();	
			downCnt = benchmarkBeanlist.parallelStream().filter(x -> x.getPctEnd2EndLH()<0.0).count();
		}else if(Const.END2END_H2L.equalsIgnoreCase(end2EndPattern)) {
			upCnt = benchmarkBeanlist.parallelStream().filter(x -> x.getPctEnd2EndHL()>0.0).count();	
			downCnt = benchmarkBeanlist.parallelStream().filter(x -> x.getPctEnd2EndHL()<0.0).count();
		}else if(Const.C2PH.equalsIgnoreCase(end2EndPattern)) {
			upCnt = benchmarkBeanlist.parallelStream().filter(x -> x.getPctC2Highest()>0.0).count();	
			downCnt = benchmarkBeanlist.parallelStream().filter(x -> x.getPctC2Highest()<0.0).count();
		}else if(Const.C2PL.equalsIgnoreCase(end2EndPattern)) {
			upCnt = benchmarkBeanlist.parallelStream().filter(x -> x.getPctC2Lowest()>0.0).count();	
			downCnt = benchmarkBeanlist.parallelStream().filter(x -> x.getPctC2Lowest()<0.0).count();
		}else if(Const.O2PH.equalsIgnoreCase(end2EndPattern)) {
			upCnt = benchmarkBeanlist.parallelStream().filter(x -> x.getPctO2Highest()>0.0).count();	
			downCnt = benchmarkBeanlist.parallelStream().filter(x -> x.getPctO2Highest()<0.0).count();
		}else if(Const.O2PL.equalsIgnoreCase(end2EndPattern)) {
			upCnt = benchmarkBeanlist.parallelStream().filter(x -> x.getPctO2Lowest()>0.0).count();	
			downCnt = benchmarkBeanlist.parallelStream().filter(x -> x.getPctO2Lowest()<0.0).count();
		}else {
			return new RisesFallsRatio(end2EndPattern);
		}
		
		double upRatio = (double) upCnt / (double) benchmarkBeanlist.size();
		double downRatio = (double) downCnt / (double) benchmarkBeanlist.size();
		RisesFallsRatio ratio = new RisesFallsRatio(end2EndPattern);
		ratio.setRisesRatio(upRatio);
		ratio.setFallsRatio(downRatio);
		
		return ratio;
	}
	
	
	public static BaseSummaryStatistics genearteEnd2EndLHStat(List<BenchmarkBean> list) {
		double avg = list.parallelStream().mapToDouble(BenchmarkBean::getPctEnd2EndLH).average().orElse(Double.NaN);
		
		List<Double> targetList = list.parallelStream().map(BenchmarkBean::getPctEnd2EndLH).collect(Collectors.toList());
		List<Double> descList = AnalyGeneralHelper.toReverseOrderList(targetList);
		if(descList==null || descList.isEmpty())
			return new BaseSummaryStatistics(Const.END2END_L2H); 
		
		double median = AnalyGeneralHelper.getMedian(descList);
		double max = descList.get(0);
		double min = descList.get(descList.size()-1);
//		double min2nd = Double.NaN;
//		if(min >= 0 && descList.size()>9) {
//			min2nd = descList.get(descList.size()-2);
//			if(descList.size()>=18)
//				min2nd = descList.get(descList.size()-3);
//			
//						
//		}
		
		double min2nd = getNinetyPercentAchieve(descList);
		
		
		double sd = AnalyGeneralHelper.calculateSD(descList);
		
		BaseSummaryStatistics stat = buildStatBean(Const.END2END_L2H, avg, max, min, min2nd, median, sd, list.size());
		return stat;
	}
	
	public static BaseSummaryStatistics genearteEnd2EndHLStat(List<BenchmarkBean> list) {
		double avg = list.parallelStream().mapToDouble(BenchmarkBean::getPctEnd2EndHL).average().orElse(Double.NaN);
		
		List<Double> targetList = list.parallelStream().map(BenchmarkBean::getPctEnd2EndHL).collect(Collectors.toList());
		List<Double> descList = AnalyGeneralHelper.toReverseOrderList(targetList);
		
		if(descList==null || descList.isEmpty())
			return new BaseSummaryStatistics(Const.END2END_H2L); 
		
		double median = AnalyGeneralHelper.getMedian(descList);
		double max = descList.get(0);
		double min = descList.get(descList.size()-1);
		double min2nd = getNinetyPercentAchieve(descList);
		double sd = AnalyGeneralHelper.calculateSD(descList);
		BaseSummaryStatistics stat = buildStatBean(Const.END2END_H2L, avg, max, min, min2nd, median ,sd, list.size());
		return stat;
	}
	
	public static BaseSummaryStatistics genearteC2CStat(List<BenchmarkBean> list) {
		double avg = list.parallelStream().mapToDouble(BenchmarkBean::getPctC2C).average().orElse(Double.NaN);
		
		List<Double> targetList = list.parallelStream().map(BenchmarkBean::getPctC2C).collect(Collectors.toList());
		List<Double> descList = AnalyGeneralHelper.toReverseOrderList(targetList);
		if(descList==null || descList.isEmpty())
			return new BaseSummaryStatistics(Const.END2END_C2C); 
		
		double median = AnalyGeneralHelper.getMedian(descList);
		double max = descList.get(0);
		double min = descList.get(descList.size()-1);
		double min2nd = getNinetyPercentAchieve(descList);
		
		double sd = AnalyGeneralHelper.calculateSD(descList);
		BaseSummaryStatistics stat = buildStatBean(Const.END2END_C2C, avg, max, min, min2nd, median ,sd, list.size());
		return stat;
	}
	
	
	public static BaseSummaryStatistics genearteO2CStat(List<BenchmarkBean> list) {
		double avg = list.parallelStream().mapToDouble(BenchmarkBean::getPctO2C).average().orElse(Double.NaN);
		
		List<Double> targetList = list.parallelStream().map(BenchmarkBean::getPctO2C).collect(Collectors.toList());
		List<Double> descList = AnalyGeneralHelper.toReverseOrderList(targetList);
		if(descList==null || descList.isEmpty())
			return new BaseSummaryStatistics(Const.END2END_O2C); 
		
		double median = AnalyGeneralHelper.getMedian(descList);
		double max = descList.get(0);
		double min = descList.get(descList.size()-1);
		double min2nd = getNinetyPercentAchieve(descList);
		
		double sd = AnalyGeneralHelper.calculateSD(descList);
		BaseSummaryStatistics stat = buildStatBean(Const.END2END_O2C, avg, max, min, min2nd, median ,sd, list.size());
		return stat;
	}
	
	
	
	public static BaseSummaryStatistics genearteC2PLStat(List<BenchmarkBean> list) {
		double avg = list.parallelStream().mapToDouble(BenchmarkBean::getPctC2Lowest).average().orElse(Double.NaN);
		
		List<Double> targetList = list.parallelStream().map(BenchmarkBean::getPctC2Lowest).collect(Collectors.toList());
		List<Double> descList = AnalyGeneralHelper.toReverseOrderList(targetList);
		if(descList==null || descList.isEmpty())
			return new BaseSummaryStatistics(Const.C2PL); 
		
		double median = AnalyGeneralHelper.getMedian(descList);
		
		//if [3,1,0,-1,-2,-3,-4], the max-negative value should be -1
//		double maxNegative = descList.parallelStream().filter(x -> x<0).max(Double::compare).orElse(Double.NaN);

		double max = descList.get(0);
		double min = descList.get(descList.size()-1);
		double min2nd = getNinetyPercentAchieve(descList);
		double sd = AnalyGeneralHelper.calculateSD(descList);
		BaseSummaryStatistics stat = buildStatBean(Const.C2PL, avg, max, min, min2nd, median ,sd, list.size());
		return stat;
	}
	
	public static BaseSummaryStatistics genearteC2PHStat(List<BenchmarkBean> list) {
		double avg = list.parallelStream().mapToDouble(BenchmarkBean::getPctC2Highest).average().orElse(Double.NaN);
		
		List<Double> targetList = list.parallelStream().map(BenchmarkBean::getPctC2Highest).collect(Collectors.toList());
		List<Double> descList = AnalyGeneralHelper.toReverseOrderList(targetList);
		if(descList==null || descList.isEmpty())
			return new BaseSummaryStatistics(Const.C2PH); 
		
		double median = AnalyGeneralHelper.getMedian(descList);
		double max = descList.get(0);
		double min = descList.get(descList.size()-1);
		double min2nd = getNinetyPercentAchieve(descList);
		double sd = AnalyGeneralHelper.calculateSD(descList);
		BaseSummaryStatistics stat = buildStatBean(Const.C2PH, avg, max, min, min2nd, median ,sd, list.size());
		return stat;
	}
	
	public static BaseSummaryStatistics genearteO2PHStat(List<BenchmarkBean> list) {
		double avg = list.parallelStream().mapToDouble(BenchmarkBean::getPctO2Highest).average().orElse(Double.NaN);
		
		List<Double> targetList = list.parallelStream().map(BenchmarkBean::getPctO2Highest).collect(Collectors.toList());
		List<Double> descList = AnalyGeneralHelper.toReverseOrderList(targetList);
		if(descList==null || descList.isEmpty())
			return new BaseSummaryStatistics(Const.O2PH); 
		
		double median = AnalyGeneralHelper.getMedian(descList);
		double max = descList.get(0);
		double min = descList.get(descList.size()-1);
		double min2nd = getNinetyPercentAchieve(descList);
		double sd = AnalyGeneralHelper.calculateSD(descList);
		BaseSummaryStatistics stat = buildStatBean(Const.O2PH, avg, max, min, min2nd, median ,sd, list.size());
		return stat;
	}
	
	public static BaseSummaryStatistics genearteO2PLStat(List<BenchmarkBean> list) {
		double avg = list.parallelStream().mapToDouble(BenchmarkBean::getPctO2Lowest).average().orElse(Double.NaN);
		
		List<Double> targetList = list.parallelStream().map(BenchmarkBean::getPctO2Lowest).collect(Collectors.toList());
		List<Double> descList = AnalyGeneralHelper.toReverseOrderList(targetList);
		if(descList==null || descList.isEmpty())
			return new BaseSummaryStatistics(Const.O2PL); 
		
		double median = AnalyGeneralHelper.getMedian(descList);
		
		//if [3,1,0,-1,-2,-3,-4], the max-negative value should be -1
//		double maxNegative = descList.parallelStream().filter(x -> x<0).max(Double::compare).orElse(Double.NaN);

		double max = descList.get(0);
		double min = descList.get(descList.size()-1);
		double min2nd = getNinetyPercentAchieve(descList);
		double sd = AnalyGeneralHelper.calculateSD(descList);
		BaseSummaryStatistics stat = buildStatBean(Const.O2PL, avg, max, min, min2nd, median ,sd, list.size());
		return stat;
	}
	

	public static BaseSummaryStatistics geneartePercentageRangeStat(List<BenchmarkBean> list) {
		double avg = list.parallelStream().mapToDouble(BenchmarkBean::getPercentageRange).average().orElse(Double.NaN);
		
		List<Double> targetList = list.parallelStream().map(BenchmarkBean::getPercentageRange).collect(Collectors.toList());
		
		List<Double> descList = AnalyGeneralHelper.toReverseOrderList(targetList);
		if(descList==null || descList.isEmpty())
			return new BaseSummaryStatistics(Const.PCT_RANGE); 
		
		double median = AnalyGeneralHelper.getMedian(descList);
		double max = descList.get(0);
		double min = descList.get(descList.size()-1);
		double min2nd = getNinetyPercentAchieve(descList);
		double sd = AnalyGeneralHelper.calculateSD(descList);
		BaseSummaryStatistics stat = buildStatBean(Const.PCT_RANGE, avg, max, min, min2nd, median ,sd, list.size());
		return stat;
	}
	
	
	
	private static BaseSummaryStatistics buildStatBean(
			String type, double avg, double max, double min, double min2nd, double median, double sd, 
			int size) {
		BaseSummaryStatistics stat = new BaseSummaryStatistics(type);
		stat.setAvg(avg);
		stat.setCount(size);
		stat.setMax(max);
		stat.setMin(min);
		stat.setMin2nd(min2nd);
		stat.setMedian(median);
		stat.setSd(sd);
		return stat;
	}
	
	
	public static double getNinetyPercentAchieve(List<Double>  descList) {
		
		//The proportion of positive returns.
		long c2periodHUpCnt = descList.parallelStream().filter(x -> x > 0.0).count();	
		double c2periodHPositiveRatio = (double) c2periodHUpCnt / (double) descList.size();		

		//The proportion of negative returns.
		long c2periodLDownCnt = descList.parallelStream().filter(x -> x < 0.0).count();	
		double c2periodLNegativeRatio = (double) c2periodLDownCnt / (double) descList.size();

		
		double rtn = Double.NaN;
		
		
		if(c2periodHPositiveRatio >= 0.9) {
			double d = descList.size()*0.9;			
			int idx =(int)d;// Integer.parseInt(String.format("%.0f", d));
			int targetIdx = idx-1;
			if(idx==0)
				targetIdx = 0;
			
			rtn = descList.get(targetIdx);	
		}else if(c2periodLNegativeRatio >= 0.9) {
			double d = descList.size()*0.1;			
			int idx =(int)d;// Integer.parseInt(String.format("%.0f", d));
			int targetIdx = idx-1;
			
			if(idx==0)
				targetIdx = 0;
			
			rtn = descList.get(targetIdx);	
		}else {
			return rtn;
		}
		
		
//		double min = descList.get(descList.size()-1);
//		double max = descList.get(0);
//		double rtn = Double.NaN;
//		
//		
//		double d = descList.size()*0.9;
//		
//		int idx =(int)d;// Integer.parseInt(String.format("%.0f", d));
//		
//		if((min > 0 && max>0) && descList.size()>10) {// ALL Positive return checking
//			rtn = descList.get(idx-1);
//		}
//		
//		if((min < 0 && max<0) && descList.size()>=10) {// ALL Positive return checking
//			idx = descList.size() - idx -1;
//			rtn = descList.get(idx);
//		}
		
		
		return rtn ;

	}
	
//	public static void main(String args[]) {
//		double d = 11 * 0.9;			
//		int idx =(int)d;// Integer.parseInt(String.format("%.0f", d));
////		rtn = descList.get(idx-1);	
//		System.out.println("idx = " + idx);
//	}
}
