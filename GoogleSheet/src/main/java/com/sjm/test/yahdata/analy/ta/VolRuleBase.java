package com.sjm.test.yahdata.analy.ta;

import java.util.List;


import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.cfg.BacktestConfig;
import com.sjm.test.yahdata.analy.cfg.MAValidateCfgBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.ta.helper.MovingAvgHelper;

public abstract class VolRuleBase {
//	private static final Logger logger = Logger.getLogger(VolRuleBase.class);


	private boolean enable = true;
	private CandleEventTagEnum benchmarkCandleTag = null;
	private CandleEventTagEnum occurCandleTag = null;
	
	private boolean isOccurCandleTag = false;
	
	private boolean validate(StockBean prev, StockBean curr) {
		
		if(enable == false )
			return false;
		
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
		if(maCfg==null || (maCfg.isAboveMA()==false && maCfg.isBelowMA()==false) || (maCfg.isAboveMA()==true && maCfg.isBelowMA()==true)) {
			return bValidate;
		}
		
//		boolean bCheck = false;
		
		double shortMA = MovingAvgHelper.getMAPricebyLength(prevList, curr, maCfg.getMaShort());
		double longMa = MovingAvgHelper.getMAPricebyLength(prevList, curr, maCfg.getMaLong());
		
		if(maCfg.isAboveMA() ) {			
			if(curr.getC() >shortMA && shortMA > longMa)
				return true;	
		}
		
		if(maCfg.isBelowMA() ) {			
			if(curr.getC() <shortMA && shortMA < longMa)
				return true;
		}
		
		return false;
		
	}
	
	public abstract boolean detect(List<StockBean> prevList, StockBean curr) ;
	


	public CandleEventTagEnum getBenchmarkCandleTag() {
		return benchmarkCandleTag;
	}


	public void setBenchmarkCandleTag(CandleEventTagEnum tag) {
		this.benchmarkCandleTag = tag;
	}


	public CandleEventTagEnum getOccurCandleTag() {
		return occurCandleTag;
	}


	public void setOccurCandleTag(CandleEventTagEnum occurCandleTag) {
		this.occurCandleTag = occurCandleTag;
	}


	public boolean isEnable() {
		return enable;
	}


	public boolean isOccurCandleTag() {
		return isOccurCandleTag;
	}


	public void setOccurCandleTag(boolean isOccurCandleTag) {
		this.isOccurCandleTag = isOccurCandleTag;
	}


	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	

}
