package com.sjm.test.yahdata.analy.ta.pattern;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.ta.KHelper;

public class BollingerBandApi {

	public String isUpBreakBBATime(List<StockBean> stockList) {
		int size = stockList.size();
		if(size < 5)
			return "";
		
		int endIdx = stockList.size()-5;
		stockList.get(1);
		
		boolean isFoundUBbb = false;
		StockBean sb = stockList.get(size-1);
		for(int i=size-1; i>=endIdx; i--){
			sb = stockList.get(i);
			isFoundUBbb = this.isUpBreakBB(sb);
			if(isFoundUBbb) {
				break;
			}
		}
		
		if(isFoundUBbb==false)
			return "";
		return sb.getTxnDate();
	}
	
	public boolean isUpBreakBB(StockBean stock) {
		if(stock.getBollingerBand()==null)
			return false;
		
		
		double bbUpper = stock.getBollingerBand().getUpper();
		double bbMiddle = stock.getBollingerBand().getMiddle();
		double prevBbUpper = stock.getBollingerBand().getUpper();
		
		boolean condition1 = false;
		boolean condition2 = false;
		boolean condition3 = false;
		if(stock.getC() >= bbUpper * 0.99 && KHelper.isBullishCandle(stock)
			&& prevBbUpper>stock.getH()
		) {
			condition1 = true;
		}
		
		//up break
		double upBreakPct = (stock.getBodyTop() - bbUpper) / bbUpper;
		if( upBreakPct > 0.001 && upBreakPct < 0.02 && KHelper.isBearishCandle(stock)) 
		{
			condition2 = true;
		}
		
		double hUpBreakPct = (stock.getH() - bbUpper) / bbUpper;
		if( hUpBreakPct > 0.003 
			&& bbUpper >= stock.getBodyTop() && bbMiddle < stock.getBodyBottom()
			&& KHelper.isBearishCandle(stock)) 
		{
			condition3 = true;
		}
		
		if(condition1 || condition2 || condition3) {		
			return true;
		}
			
		return false;
	}
	
}
