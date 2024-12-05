package com.sjm.test.yahdata.analy.ta.rule.ma.event;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;

/**
 * 
 *  
 *
 */
public class MABullishCorssingRule  extends VolRuleBase{

	public CandleEventTagEnum SIGN = CandleEventTagEnum.EVNT_MA_BULISSH_CROSSING;
	
//	public static int SHORT = 10;
//	public static int LONG = 20;
	
	public MABullishCorssingRule() {
	}
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {	
		boolean b = validate(prevList, curr);
//		if("2022-04-09".equalsIgnoreCase(curr.getTxnDate()))
//			System.out.println("pause");
		
		if(b==false)
			return b;
		
//		if(stockList==null || stockList.size()==0)
//			return false;
//		
//		StockBean curr = stockList.get(stockList.size()-1);
//		List<StockBean> prevList = stockList.subList(0, stockList.size()-1);
//				
//		StockBean prev = prevList.get(prevList.size()-1);
//		
//		boolean b = this.validate(prev, curr);
//		if(b==false)
//			return false;
		StockBean prev = prevList.get(prevList.size()-1);

		double ma5 = curr.getPriceSma().getMa5();
		double ma10 = curr.getPriceSma().getMa10();
		double ma20 = curr.getPriceSma().getMa20();
		double ma50 = curr.getPriceSma().getMa50();
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
			if(prevList.size()< len-1) {
				len = prevList.size()-1;
			}
			List<StockBean> subListTmp = prevList.subList(prevList.size()-len, prevList.size());
			ArrayList<StockBean> substockList = new ArrayList<StockBean>(10);
			substockList.addAll(subListTmp);
			substockList.add(curr);
			

			StockBean maxVolStock = substockList.stream().max(Comparator.comparingDouble(StockBean::getVolume)).get();
			double avgVol = substockList.stream().mapToDouble(StockBean::getVolume).average().orElse(Double.NaN);
			double ratio = (double)maxVolStock.getVolume()/avgVol;
		
			if(ratio < 2) {
				btnResult = false;
			}
		}
		return btnResult;
	}
	
	
	@Override
	public CandleEventTagEnum getBenchmarkCandleTag() {
		return SIGN;
	}
	

}
