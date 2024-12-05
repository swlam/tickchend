package com.sjm.test.yahdata.analy.ta.rule.ma.crossing.winrate;

import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.cfg.BacktestConfig;
import com.sjm.test.yahdata.analy.cfg.MAValidateCfgBean;
import com.sjm.test.yahdata.analy.ta.ValidateHelper;
import com.sjm.test.yahdata.analy.ta.helper.MovingAvgHelper;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class MACrossUpForWinRateRule{

	public static String MA_TAG = "MA";
	public static int DEFAULT_BASE_MA = 50;
	
	private int fasterMa;
	private int slowerMa;
	
	private int baseMa = DEFAULT_BASE_MA;
	
	public MACrossUpForWinRateRule(int fasterMa, int slowerMa) {
		this.fasterMa = fasterMa;
		this.slowerMa = slowerMa;				
	}
	
	
	public String getTag() {
		return MA_TAG + this.getFasterMa() +"X"+ this.getSlowerMa();
	}
	
	public boolean detect(List<StockBean> prevList, StockBean curr) {
		
		boolean b = this.validate(prevList, curr);
		if(b==false)
			return b;
		
		boolean bRtn = false;
		
		
		double curFastMa = MovingAvgHelper.getMAPricebyLength(prevList, curr, fasterMa);
		double curSlowMa = MovingAvgHelper.getMAPricebyLength(prevList, curr, slowerMa);
		
		
		double prevFastMa = MovingAvgHelper.getPrevMAbyLength(prevList, fasterMa);
		double prevSlowMa = MovingAvgHelper.getPrevMAbyLength(prevList, slowerMa);
		
		
		boolean currMa = ( curFastMa  > curSlowMa);
		boolean prevMa = ( prevFastMa  > prevSlowMa);
		try {
			boolean isCrossUp = false;
			if( currMa==true  && prevMa==false)
				isCrossUp = true;
			
			
		double baseMaValue = MovingAvgHelper.getMAPricebyLength(prevList, curr, baseMa);
		
		if((curr.getC() > baseMaValue) && isCrossUp)	
			bRtn = true;
		
		}catch(Exception e) {
			log.error(null,e);
		}
		
		return bRtn;

	}	
	
//	protected double getMAbyLength(List<StockBean> prevList, StockBean curr, int maLength) {
//		List<StockBean>  prevSubList = prevList.subList(prevList.size()- (maLength-1), prevList.size());
//		
//		List<StockBean>  avgList = new ArrayList<StockBean>(maLength);
//		avgList.addAll(prevSubList);
//		avgList.add(curr);
//		return getAverage(avgList);
//	}
//
//	
//	protected double getPrevMAbyLength(List<StockBean> prevList, int maLength) {
//		List<StockBean>  prevSubList = prevList.subList((prevList.size()- maLength), prevList.size());
//		
//		List<StockBean>  avgList = new ArrayList<StockBean>(maLength);
//		avgList.addAll(prevSubList);
//		
//		return getAverage(avgList);
//	}
//	
	
	public boolean validate(StockBean prev, StockBean curr) {
		
		boolean b1 = ValidateHelper.isPriceData(prev);
		boolean b2 = ValidateHelper.isPriceData(curr);
		
		boolean b3 = ValidateHelper.isVolumeData(prev);
		boolean b4 = ValidateHelper.isVolumeData(curr);
		
		return (b1 && b2 && b3 && b4);
		
	}
	
	public boolean validate(List<StockBean> prevList, StockBean curr) {
		StockBean prev = prevList.get(prevList.size()-1);
		
		boolean bValidate = this.validate(prev, curr);
		
		
		MAValidateCfgBean maCfg = BacktestConfig.MA_VALIDATE_CFG;
		if(maCfg==null || (maCfg.isAboveMA()==false && maCfg.isBelowMA()==false)) {
			return bValidate;
		}
		
		boolean bCheck = false;
		
		double shortMA = MovingAvgHelper.getMAPricebyLength(prevList, curr, maCfg.getMaShort());
		double longMa = MovingAvgHelper.getMAPricebyLength(prevList, curr, maCfg.getMaLong());
		
		if(maCfg.isAboveMA() ) {			
			if(curr.getC() >shortMA && shortMA > longMa)
				bCheck = true;	
		}
		
		if(maCfg.isBelowMA() ) {			
			if(curr.getC() <shortMA && shortMA < longMa)
				bCheck = true;
		}
		
		return bValidate && bCheck;
		
	}
	
	
	
	
//	protected double getAverage(List<StockBean> orgList) {
//		double avg = orgList.parallelStream().mapToDouble(a -> a.getC()).average().getAsDouble();
//		return avg;
//	}

	public int getFasterMa() {
		return fasterMa;
	}

	public void setFasterMa(int fasterMa) {
		this.fasterMa = fasterMa;
	}

	public int getSlowerMa() {
		return slowerMa;
	}

	public void setSlowerMa(int slowerMa) {
		this.slowerMa = slowerMa;
	}

	
	
	
	

}
