package com.sjm.test.yahdata.analy.ta.helper;

import java.util.ArrayList;
import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.type.MovingAvgType;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class MovingAvgHelper {
//	private static final Logger logger = Logger.getLogger(MovingAvgHelper.class);
	public MovingAvgHelper() {
	}

	public static double getMAPricebyLength(List<StockBean> prevList, StockBean curr, int maDays) {
		return getMAbyLength(prevList, curr, maDays, MovingAvgType.PRICE);
	}
	
	public static double getMAbyLength(List<StockBean> prevList, StockBean curr, int maDays, MovingAvgType maType) {
		
		
		List<StockBean>  avgList = null;
		int maLength = maDays;
		try {			
			if(maDays>= prevList.size())
				maLength = prevList.size();
				
			int subListStartIdx = prevList.size()- (maLength-1);
			if(subListStartIdx<0)
				return 0.0;
			
			
			int leftIdx = prevList.size()- (maLength-1);
			int rightIdx = prevList.size();
			if(leftIdx>rightIdx)
				return 0.0;
			
			List<StockBean>  prevSubList = prevList.subList(leftIdx, rightIdx);
			

			avgList = new ArrayList<StockBean>(maLength);
			avgList.addAll(prevSubList);
			avgList.add(curr);
			
//			if(avgList.size() < maDays) {
//				logger.warn(curr.getStockCode()+", incorrect occured. MA: "+maDays + ", element size:  "+avgList.size());
//			}
		}catch(Exception e) {
			log.error(curr.getStockCode()+", stockSize = "+prevList.size()+", MA-length="+maLength, e);
		}
		return getAverage(avgList, maType);
	}
	
	
	public static List<StockBean>  getNumOfDaysData(List<StockBean> prevList, int maDays) {
//		double maPrev = MovingAvgHelper.getMAbyLength(prevList, prevList.get(prevList.size()-1), maDays);
		int maLength = maDays;
		if(maDays>= prevList.size())
			maLength = prevList.size();
		List<StockBean>  prevSubList = prevList.subList(prevList.size()- (maLength-1), prevList.size());
		return prevSubList;
	}
	
	public static double getPrevMAbyLength(List<StockBean> prevList, int maDays) {
		return getPrevMAbyLength(prevList, maDays, MovingAvgType.PRICE);
	}
	protected static double getPrevMAbyLength(List<StockBean> prevList, int maDays, MovingAvgType maType) {
		double maPrev = MovingAvgHelper.getMAbyLength(prevList.subList(0,  prevList.size()-1), prevList.get(prevList.size()-1), maDays, maType);
		return maPrev;
	}
	
	
	public static double calculateSMA(List<StockBean> stockList, int days, MovingAvgType maType) {
		int size = stockList.size();
    	int maStartIdx = size - days;
    	if(maStartIdx	< 0)
    		return 0.0;
    	
    	List<StockBean> subList = stockList.subList(maStartIdx, size);
    	return MovingAvgHelper.getAverage(subList,maType);
	}
	
	protected static double getAverage(List<StockBean> orgList, MovingAvgType maType) {
		double avg = -999.9;
		if(MovingAvgType.VOLUME == maType) {
			avg = orgList.parallelStream().mapToDouble(a -> a.getVolume()).average().getAsDouble();	
		}else {
			avg = orgList.parallelStream().mapToDouble(a -> a.getC()).average().getAsDouble();	
		}
		
		return avg;
	}
}
