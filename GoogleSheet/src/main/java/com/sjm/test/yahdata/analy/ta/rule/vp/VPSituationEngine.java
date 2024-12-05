package com.sjm.test.yahdata.analy.ta.rule.vp;

import java.util.ArrayList;
import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.ta.ValidateHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VPSituationEngine {

	public String detect(List<StockBean> stockList) {
		
		if(stockList.size()<4)
			return null;
		
		StockBean curr = stockList.get(stockList.size()-1);
		StockBean prev = stockList.get(stockList.size()-2);
		StockBean prev_1 = stockList.get(stockList.size()-3);
		StockBean prev_2 = stockList.get(stockList.size()-4);
		
		List<StockBean> prevList = stockList.subList(0, stockList.size()-1);
		
		
		boolean b = this.validate(prev, curr);
		if(b==false)
			return null;
		
		
		double ma5 = getMAbyLength(prevList, curr, 5);
		double ma30 = getMAbyLength(prevList, curr, 30);
		double ratio =ma5/ma30; 
		String maVolMsg = "";
		if(ratio>1.25 && ratio<2.5)
			maVolMsg ="5天>30天近2倍量 ";
		if(ratio<0.75 && ratio>0.5)
			maVolMsg ="5天<30天近半量 ";
		
		
		String rtnMsg = "";
		if( curr.getVolume()> prev.getVolume() 
			&& (prev.getVolume()>prev_1.getVolume() || prev.getVolume()>prev_2.getVolume())
			
		  ) {
			rtnMsg += "量連日遞增 ";
		}
		else if( curr.getVolume()> prev.getVolume() 
			&& curr.getVolume()>prev_1.getVolume() 
			&& curr.getVolume()>prev_2.getVolume()				
		) {
			rtnMsg = "今日量最多 ";
		}
		
		else if( curr.getVolume()< prev.getVolume() 
				&& curr.getVolume()<prev_1.getVolume() 
				&& curr.getVolume()<prev_2.getVolume()				
			) {
			rtnMsg = "今日量最少 ";
			}
		
		maVolMsg += rtnMsg;
		
		
		
		maVolMsg +=func(curr.getVolume(), prev.getVolume(), prev_1.getVolume(), prev_2.getVolume());
		return  maVolMsg;
	}
	
	
	public String func(double cur, double prev, double prev_1,double prev_2) {
//		if(cur > prev && prev >prev_1 && prev_1>prev_2)
		if(prev_2 < prev_1 && prev_1 < prev && prev<cur)
			return "低高高高";		
		if(prev_2 < prev_1 && prev_1 < prev && prev>cur)
			return "低高高低";		
		if(prev_2 < prev_1 && prev_1 > prev && prev<cur)
			return "低高低高";
		if(prev_2 < prev_1 && prev_1 > prev && prev>cur)
			return "低高低低";
		
		
		
		if(prev_2 > prev_1 && prev_1 < prev && prev<cur)			
			return "高低高高";		
		if(prev_2 > prev_1 && prev_1 < prev && prev>cur)
			return "高低高低";
		if(prev_2 > prev_1 && prev_1 > prev && prev<cur)
			return "高低低高";		
		if(prev_2 > prev_1 && prev_1 > prev && prev>cur)
			return "高低低低";
		return "";
	}
	
	public boolean validate(StockBean prev, StockBean curr) {
		
		boolean b1 = ValidateHelper.isPriceData(prev);
		boolean b2 = ValidateHelper.isPriceData(curr);
		
//		boolean b3 = ValidateHelper.isVolumeData(prev);
//		boolean b4 = ValidateHelper.isVolumeData(curr);
		return b1&&b2;
//		return (b1 && b2 && b3 && b4);
		
	}
	
	protected double getAverageVolume(List<StockBean> orgList) {
		double avg = orgList.parallelStream().mapToDouble(a -> a.getVolume()).average().getAsDouble();
		return avg;
	}
	
	
	public double getMAbyLength(List<StockBean> prevList, StockBean curr, int maDays) {
		
		
		List<StockBean>  avgList = null;
		int maLength = maDays;
		try {			
			if(maDays>= prevList.size())
				maLength = prevList.size();
				
			int subListStartIdx = prevList.size()- (maLength-1);
			if(subListStartIdx<0)
				return 0.0;
			
			List<StockBean>  prevSubList = prevList.subList(prevList.size()- (maLength-1), prevList.size());
			

			avgList = new ArrayList<StockBean>(maLength);
			avgList.addAll(prevSubList);
			avgList.add(curr);
			
			if(avgList.size() < maDays) {
				log.warn("Incorrect occured. MA: "+maDays + ", element size:  "+avgList.size());
			}
//			avgList = new ArrayList<StockBean>(maLength);
//			avgList.addAll(prevSubList);
//			avgList.add(curr);
		}catch(Exception e) {
			log.error(curr.getStockCode()+", stockSize = "+prevList.size()+", MA-length="+maLength, e);
		}
		return getAverageVolume(avgList);
	}

}
