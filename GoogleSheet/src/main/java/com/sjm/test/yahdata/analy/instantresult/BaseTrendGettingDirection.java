package com.sjm.test.yahdata.analy.instantresult;

import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.model.InstantPerformanceResult;
import com.sjm.test.yahdata.analy.ta.PatternTrendHelper;

public abstract class BaseTrendGettingDirection {
	public static int DEFAULT_YEARS_OF_DATA = 10;
	
	public BaseTrendGettingDirection() {
	}

	
	abstract public boolean isCategoryHit(InstantPerformanceResult x) ;
	abstract public String toDescription();	
	abstract public String goAnalyze(InstantPerformanceResult x); 
	
	
	
	
//	@Deprecated
	public boolean isMeetTradingVolume(InstantPerformanceResult x) {
		if( Const.IS_INTRADAY)
			return true;
		
		int estTradAmt = 30; //default is 'D'
		
//		if( Const.INTERVAL_D.equalsIgnoreCase(x.getInterval())){
//			if(GlobalConfig.IS_INTRADAY == true) 
//				estTradAmt = 50;
//			else
//				estTradAmt = 100;			
//		}else
			
		if( Const.INTERVAL_W.equalsIgnoreCase(x.getInterval())){
			estTradAmt = 100*5;
		}else if( Const.INTERVAL_M.equalsIgnoreCase(x.getInterval())){
			estTradAmt = 100*22;
		}
		
		if(x.getSector()!=null && x.getSector().contains("ETF"))
			return (x.getEstTradeAmount() > 5);
		return (x.getEstTradeAmount() > estTradAmt); 
	}
//	
//	protected boolean isValidDailyVolume(String x) {
//		boolean b = false;
//		
//		if(GlobalConfig.IS_INTRADAY == true) {
//			b = !x.contains(VolumePriceStructureHelper.VOL_EXTREMELY_SAMLL);
//		}else {
//			b = !(x.equalsIgnoreCase(VolumePriceStructureHelper.VOL_FLAT_MINUS) || 
//					x.contains(VolumePriceStructureHelper.VOL_DECREASE_HALF) || 
//					x.contains(VolumePriceStructureHelper.VOL_EXTREMELY_SAMLL) || 
//					x.contains("NA")
//				);
//		}
//				
//		return b;
//	}
	
	public boolean hasExtreamLowVol(InstantPerformanceResult x) {
		//One or more 極縮量日子
		//倍量數目 >0
		if(x.getPriceVolStockBean()==null) {
			return false;
		}
		
		boolean isNA = Const.NA.equalsIgnoreCase(x.getPriceVolStockBean().getNumOfDoubleVolumeDate()) || x.getPriceVolStockBean().getNumOfDoubleVolumeDate()==null;
		boolean isMeetDoubleVolRequirement = false;
		if(isNA==false) {
			Integer n = 0;
			if(x.getPriceVolStockBean().getNumOfDoubleVolumeDate().isEmpty())
			{
				n = 0;
			}else {
				n = Integer.parseInt(x.getPriceVolStockBean().getNumOfDoubleVolumeDate());
			}
			
			if(n>0)
				isMeetDoubleVolRequirement = true;
		}
		
		boolean isVolExtreameNA = Const.NA.equalsIgnoreCase(x.getPriceVolStockBean().getNumOfShrinkageVolumeDate()) || x.getPriceVolStockBean().getNumOfShrinkageVolumeDate()==null;
		boolean isMeetShrinkageVolRequirement = false;
		if(isVolExtreameNA==false) {
			Integer n = 0;
			if(x.getPriceVolStockBean().getNumOfDoubleVolumeDate().isEmpty())
			{
				n = 0;
			}else {
				n = Integer.parseInt(x.getPriceVolStockBean().getNumOfDoubleVolumeDate());
			}			if(n>0)
				isMeetShrinkageVolRequirement = true;
		}
		
		
		if(isMeetShrinkageVolRequirement==true && isMeetDoubleVolRequirement==true)
			return true;
		
		return false;
			
		}
	
	public boolean isMeetStrongSideRSI(InstantPerformanceResult x) {
		
		boolean b1 = (x.getCurrentStockBean().getRsi9()>=0.55 && x.getCurrentStockBean().getRsi9()<= 0.75) || (x.getCurrentStockBean().getRsi14()>=0.55 && x.getCurrentStockBean().getRsi14()<=0.75);
		boolean b2 = (x.getPrevStockBean().getRsi9()>=0.55 && x.getPrevStockBean().getRsi9()<= 0.75) || (x.getPrevStockBean().getRsi14()>=0.55 && x.getPrevStockBean().getRsi14()<=0.75);
		boolean b3 = x.getCurrentStockBean().getRsi9()>= x.getPrevStockBean().getRsi9() || x.getCurrentStockBean().getRsi14()>= x.getPrevStockBean().getRsi14();
		
		return (b1 && b2 && b3) || ( x.getPrevStockBean().getRsi9() <= 0.55 && x.getCurrentStockBean().getRsi9() > 0.7);
	}
	
	public boolean isMeetWeakSideRSI(InstantPerformanceResult x) {
		double rsiValue = 0.51;
		boolean b1 = (x.getCurrentStockBean().getRsi9()<=rsiValue && x.getCurrentStockBean().getRsi9()<= rsiValue) || (x.getCurrentStockBean().getRsi14()<=rsiValue && x.getCurrentStockBean().getRsi14()<=rsiValue);
		boolean b2 = (x.getPrevStockBean().getRsi9()<=rsiValue && x.getPrevStockBean().getRsi9()<= rsiValue) || (x.getPrevStockBean().getRsi14()<=rsiValue && x.getPrevStockBean().getRsi14()<=rsiValue);
		boolean b3 = x.getCurrentStockBean().getRsi9()<= x.getPrevStockBean().getRsi9() || x.getCurrentStockBean().getRsi14()<= x.getPrevStockBean().getRsi14(); 
		
		return (b1 && b2 && b3) || ( x.getPrevStockBean().getRsi9() > 0.5 && x.getCurrentStockBean().getRsi9() < 0.3);
	}
	
	public boolean isTodayStrong(InstantPerformanceResult x) {
		boolean b = (x.getStrongWeakTypeToday().getType().contentEquals(PatternTrendHelper.WEAK_2_STRONG_LV1)
				|| x.getStrongWeakTypeToday().getType().contentEquals(PatternTrendHelper.WEAK_2_STRONG_LV2)
				|| x.getStrongWeakTypeToday().getType().contentEquals(PatternTrendHelper.KEEP_STRONG)
				|| x.getStrongWeakTypeToday().getType().contentEquals(PatternTrendHelper.WEAK_2_STRONG_LV3_1)
				|| x.getStrongWeakTypeToday().getType().contentEquals(PatternTrendHelper.WEAK_2_STRONG_LV3_2)
				|| x.getStrongWeakTypeToday().getType().contentEquals(PatternTrendHelper.STRONG_UPPER_SHADOW)
				|| x.getStrongWeakTypeToday().getType().contentEquals(PatternTrendHelper.STRONG_2_STRONG_AGAIN)
			);
		return b;
	}
	
	public boolean isPrevDayStrong(InstantPerformanceResult x) {
		boolean b = (x.getStrongWeakTypePrevDay().getType().contentEquals(PatternTrendHelper.WEAK_2_STRONG_LV1)
				|| x.getStrongWeakTypePrevDay().getType().contentEquals(PatternTrendHelper.WEAK_2_STRONG_LV2)
				|| x.getStrongWeakTypePrevDay().getType().contentEquals(PatternTrendHelper.KEEP_STRONG)
				|| x.getStrongWeakTypePrevDay().getType().contentEquals(PatternTrendHelper.WEAK_2_STRONG_LV3_1)
				|| x.getStrongWeakTypePrevDay().getType().contentEquals(PatternTrendHelper.WEAK_2_STRONG_LV3_2)
				|| x.getStrongWeakTypePrevDay().getType().contentEquals(PatternTrendHelper.STRONG_UPPER_SHADOW)
				|| x.getStrongWeakTypePrevDay().getType().contentEquals(PatternTrendHelper.STRONG_2_STRONG_AGAIN)
			);
		return b;
	}
	
	public boolean isPrev2DayStrong(InstantPerformanceResult x) {
		boolean b = (x.getStrongWeakTypePrev2Days().getType().contentEquals(PatternTrendHelper.WEAK_2_STRONG_LV1)
				|| x.getStrongWeakTypePrev2Days().getType().contentEquals(PatternTrendHelper.WEAK_2_STRONG_LV2)
				|| x.getStrongWeakTypePrev2Days().getType().contentEquals(PatternTrendHelper.KEEP_STRONG)
				|| x.getStrongWeakTypePrev2Days().getType().contentEquals(PatternTrendHelper.WEAK_2_STRONG_LV3_1)
				|| x.getStrongWeakTypePrev2Days().getType().contentEquals(PatternTrendHelper.WEAK_2_STRONG_LV3_2)
				|| x.getStrongWeakTypePrev2Days().getType().contentEquals(PatternTrendHelper.STRONG_UPPER_SHADOW)
				|| x.getStrongWeakTypePrev2Days().getType().contentEquals(PatternTrendHelper.STRONG_2_STRONG_AGAIN)
			);
		return b;
	}
	
	
	public boolean isTodayWeak(InstantPerformanceResult x) {
		
		return (x.getStrongWeakTypeToday().getType().contentEquals(PatternTrendHelper.STRONG_2_WEAK_LV1)
				|| x.getStrongWeakTypeToday().getType().contentEquals(PatternTrendHelper.STRONG_2_WEAK_LV2)
				|| x.getStrongWeakTypeToday().getType().contentEquals(PatternTrendHelper.KEEP_WEAK)
				|| x.getStrongWeakTypeToday().getType().contentEquals(PatternTrendHelper.WEAK_LOWER_SHADOW)
				|| x.getStrongWeakTypeToday().getType().contentEquals(PatternTrendHelper.WEAK_2_WEAK_AGAIN)
			);
	}
	public boolean isPrevDayWeak(InstantPerformanceResult x) {
			
			return (x.getStrongWeakTypePrevDay().getType().contentEquals(PatternTrendHelper.STRONG_2_WEAK_LV1)
					|| x.getStrongWeakTypePrevDay().getType().contentEquals(PatternTrendHelper.STRONG_2_WEAK_LV2)
					|| x.getStrongWeakTypePrevDay().getType().contentEquals(PatternTrendHelper.KEEP_WEAK)
					|| x.getStrongWeakTypePrevDay().getType().contentEquals(PatternTrendHelper.WEAK_LOWER_SHADOW)
					|| x.getStrongWeakTypePrevDay().getType().contentEquals(PatternTrendHelper.WEAK_2_WEAK_AGAIN)
				);
		}
	
	public boolean isPrev2DayWeak(InstantPerformanceResult x) {
		
		return (x.getStrongWeakTypePrev2Days().getType().contentEquals(PatternTrendHelper.STRONG_2_WEAK_LV1)
				|| x.getStrongWeakTypePrev2Days().getType().contentEquals(PatternTrendHelper.STRONG_2_WEAK_LV2)
				|| x.getStrongWeakTypePrev2Days().getType().contentEquals(PatternTrendHelper.KEEP_WEAK)
				|| x.getStrongWeakTypePrev2Days().getType().contentEquals(PatternTrendHelper.WEAK_LOWER_SHADOW)
				|| x.getStrongWeakTypePrev2Days().getType().contentEquals(PatternTrendHelper.WEAK_2_WEAK_AGAIN)
			);
	}
	
	public boolean isRsiOverbought(InstantPerformanceResult x) {
		return (x.getCurrentStockBean().getRsi9()>0.77 && x.getPrevStockBean().getRsi9()>0.77 ) 
				|| (x.getCurrentStockBean().getRsi14()>0.75 && x.getPrevStockBean().getRsi14()>0.75);
	}
	
	public boolean isRsiOversold(InstantPerformanceResult x) {
		return (x.getCurrentStockBean().getRsi9()<0.3 && x.getPrevStockBean().getRsi9()<0.3) || (x.getCurrentStockBean().getRsi14()<0.3 && x.getPrevStockBean().getRsi14()<0.3);
	}
	
	
	public String checkRsiStatus(InstantPerformanceResult x) {
		String msg = "";
		double rsiMiddle = 0.5;
		double rsiObLv1 = 0.7;
		double rsiObLv2 = 0.8;
		double rsiObLv3 = 0.9;
		
		double rsiOsLv1 = 0.3;
		double rsiOsLv2 = 0.2;
		double rsiOsLv3 = 0.1;
		
		if( (x.getCurrentStockBean().getRsi9()>=rsiObLv3 && x.getPrevStockBean().getRsi9()>=rsiObLv3 ) || (x.getCurrentStockBean().getRsi14()>=rsiObLv3 && x.getPrevStockBean().getRsi14()>=rsiObLv3)) {
			msg = "超買x3";
		}else if( (x.getCurrentStockBean().getRsi9()>=rsiObLv3 && x.getPrevStockBean().getRsi9()<rsiMiddle ) || (x.getCurrentStockBean().getRsi14()>=rsiObLv3 && x.getPrevStockBean().getRsi14()<rsiMiddle)) {
			msg = "超買x3";
		}else if( (x.getCurrentStockBean().getRsi9()>rsiObLv2 && x.getPrevStockBean().getRsi9()>rsiObLv2 ) || (x.getCurrentStockBean().getRsi14()>=rsiObLv2 && x.getPrevStockBean().getRsi14()>=rsiObLv2)) {
			msg = "超買x2";
		}else if( (x.getCurrentStockBean().getRsi9()>rsiObLv2 && x.getPrevStockBean().getRsi9()<rsiMiddle ) || (x.getCurrentStockBean().getRsi14()>=rsiObLv2 && x.getPrevStockBean().getRsi14()<rsiMiddle)) {
			msg = "超買x2";
		}else if( (x.getCurrentStockBean().getRsi9()>rsiObLv1 && x.getPrevStockBean().getRsi9()>rsiObLv1 ) || (x.getCurrentStockBean().getRsi14()>=rsiObLv1 && x.getPrevStockBean().getRsi14()>=rsiObLv1)) {
			msg = "超買";
		}else if( (x.getCurrentStockBean().getRsi9()>rsiObLv1 && x.getPrevStockBean().getRsi9()<rsiMiddle ) || (x.getCurrentStockBean().getRsi14()>=rsiObLv1 && x.getPrevStockBean().getRsi14()<rsiMiddle)) {
			msg = "超買";
		}else if ((x.getCurrentStockBean().getRsi9()<=rsiOsLv3 && x.getPrevStockBean().getRsi9()<=rsiOsLv3) || (x.getCurrentStockBean().getRsi14()<=rsiOsLv2 && x.getPrevStockBean().getRsi14()<=rsiOsLv3)) {
			msg = "超賣x3";
		}else if ((x.getCurrentStockBean().getRsi9()<rsiOsLv3 && x.getPrevStockBean().getRsi9()>rsiMiddle) || (x.getCurrentStockBean().getRsi14()<=rsiOsLv2 && x.getPrevStockBean().getRsi14()>rsiMiddle)) {
			msg = "超賣x3";
		}else if ((x.getCurrentStockBean().getRsi9()<rsiOsLv2 && x.getPrevStockBean().getRsi9()<rsiOsLv2) || (x.getCurrentStockBean().getRsi14()<=rsiOsLv2 && x.getPrevStockBean().getRsi14()<=rsiOsLv2)) {
			msg = "超賣x2";
		}else if ((x.getCurrentStockBean().getRsi9()<rsiOsLv2 && x.getPrevStockBean().getRsi9()>rsiMiddle) || (x.getCurrentStockBean().getRsi14()<=rsiOsLv2 && x.getPrevStockBean().getRsi14()>rsiMiddle)) {
			msg = "超賣x2";
		}else if ((x.getCurrentStockBean().getRsi9()<rsiOsLv1 && x.getPrevStockBean().getRsi9()<rsiOsLv1) || (x.getCurrentStockBean().getRsi14()<=rsiOsLv1 && x.getPrevStockBean().getRsi14()<=rsiOsLv1)) {
			msg = "超賣";
		}else if ((x.getCurrentStockBean().getRsi9()<rsiOsLv1 && x.getPrevStockBean().getRsi9()>rsiMiddle) || (x.getCurrentStockBean().getRsi14()<=rsiOsLv1 && x.getPrevStockBean().getRsi14()>rsiMiddle)) {
			msg = "超賣";
		}else {			
			msg = "";
		}
	
		return msg;
	}
}

