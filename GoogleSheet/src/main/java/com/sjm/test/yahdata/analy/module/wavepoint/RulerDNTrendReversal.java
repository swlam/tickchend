package com.sjm.test.yahdata.analy.module.wavepoint;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.ta.helper.VolumePriceStructureHelper;

import lombok.extern.slf4j.Slf4j;
//連跌多日出現反轉
@Slf4j
public class RulerDNTrendReversal {
	public static int NUM_OF_ElEMENTS = 12;
	
	public RulerDNTrendReversal() {
	}

	public String doIt(List<StockBean> fullStockList) {
		int LEN = fullStockList.size();
		for(int i=LEN; i>=NUM_OF_ElEMENTS; i--) {
			List<StockBean> subList = fullStockList.subList(i-NUM_OF_ElEMENTS, i);
			String result = this.func(subList);
			if(result.isEmpty()==false)
				return result;
		}
		
		return Const.NA;
	}
	
	
	private String func(List<StockBean> skList) {
		
		long cntDark = skList.stream().filter(x -> x.isRiseToday()==false).count();

		if (cntDark < 7) {
			return "";
		}
		
		int cntContinueDownDays = 0;
		StockBean lastBaseBean = null;
		for(int i=1; i<skList.size() ; i++) {
			StockBean prev = skList.get(i-1);
			StockBean now = skList.get(i);
			
			if(lastBaseBean ==null && prev.getL() > now.getL() && prev.isRiseToday()==false && now.isRiseToday()==false) {
				lastBaseBean = now;	
				cntContinueDownDays++;
			}else if(lastBaseBean !=null && lastBaseBean.getTxnDateInt() == prev.getTxnDateInt() 
					&& prev.getL() > now.getL() && prev.isRiseToday()==false && now.isRiseToday()==false) 
			{
				lastBaseBean = now;	
				cntContinueDownDays++;
			}
		}
		
		if(cntContinueDownDays<2)
			return "";

		StockBean N = skList.get(NUM_OF_ElEMENTS - 1);
		StockBean N_1 = skList.get(NUM_OF_ElEMENTS - 2);
		if (N.isRiseToday() == true || N_1.isRiseToday() == true) {
			// is V(n days)++, reference :
			double volRatio = VolumePriceStructureHelper.getRecentDaysVolumeRatio(skList, 5);
			if(volRatio<1.25)
				return "";
			
			
			boolean b_1 = this.isRecentDayHigh(skList.subList(0, skList.size()-1));
			boolean b = this.isRecentDayHigh(skList.subList(0, skList.size()));

			if (b_1 == false && b == false)
				return "";
 
			if (N_1.isRiseToday() == false && N.isRiseToday() && N.getDayVolumeChgPct()>0.95) {
				return N.getTxnDate();
			}else if (b_1 ==false && b==true && N.isRiseToday() && N.getDayVolumeChgPct()>0.95) {
				return N.getTxnDate();
				 
			} else if (N_1.getDayVolumeChgPct()>0.95){
				return N_1.getTxnDate();
			}else {
				return "";
			}

		} else {
			return "";
		}
	}

	private boolean isRecentDayHigh(List<StockBean> skList) {

		boolean isHighCloseToday = false;
		StockBean curr = skList.get(skList.size()-1);
		try {
			
			List<StockBean> sksubList = skList.subList(0, skList.size()-1);
			StockBean wHBean = StreamTransformHelper.getPeriodHighest(sksubList, 2);
			if (curr.getBodyTop() > wHBean.getBodyTop() && curr.isRiseToday())
				isHighCloseToday = true;
		} catch (Exception e) {
			log.warn(curr.getStockCode()+" "+e.getMessage());
		}
		return isHighCloseToday;
	}

}
