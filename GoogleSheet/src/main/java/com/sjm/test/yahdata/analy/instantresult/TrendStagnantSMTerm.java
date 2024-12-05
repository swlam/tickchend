package com.sjm.test.yahdata.analy.instantresult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.DoubleStream;

import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.KPatternConst;
import com.sjm.test.yahdata.analy.model.InstantPerformanceResult;

public class TrendStagnantSMTerm extends BaseTrendGettingDirection{

	public static final Double DEFAULT_RATIO = 0.03;
	
	public TrendStagnantSMTerm() {
	}

	@Override
	public String goAnalyze(InstantPerformanceResult x) {
		boolean isHit = this.isCategoryHit(x);
		
		if(isHit)
			return StockPickTagger.STAGNANT_SHORT_MID_TERM;
		
		return null;
	}
	
	
	@Override
	public boolean isCategoryHit(InstantPerformanceResult x) {
		
		if(	this.isMeetTradingVolume(x)
//			&& this.isValidDailyVolume(x.getDailyVolDescription())
			&& this.isMovingAverageMeet(x)
			&& this.hasExtreamLowVol(x)	
			&& this.isMeetStrongSideRSI(x)
			&& 
			(KPatternConst.L_WHITE_K.equalsIgnoreCase(x.getDailyCandleStatus())==false 
			&& KPatternConst.L_DARK_K.equalsIgnoreCase(x.getDailyCandleStatus())==false
			&& KPatternConst.M_DARK_K.equalsIgnoreCase(x.getDailyCandleStatus())==false
			)
		)
		{
			return true;
		}
		return false;
	}

	//10, 20, 50
	public boolean isMovingAverageMeet(InstantPerformanceResult x) {

		boolean b1 = x.getMaSituation().getPriceSma().getMa20() >= x.getMaSituation().getPriceSma().getMa50() && x.getMaSituation().getPriceSma().getMa20() >= x.getMaSituation().getPriceSma().getMa200(); 
		
		double bottomValue = x.getCurrentStockBean().getBodyBottom();
		boolean b2 = (bottomValue >= x.getMaSituation().getPriceSma().getMa20()) && (bottomValue >= x.getMaSituation().getPriceSma().getMa50()) && (bottomValue >= x.getMaSituation().getPriceSma().getMa100()); 
		
		boolean criteriaA = (b1 && b2 && x.getStagnantMARatio()< DEFAULT_RATIO);
		
		DoubleStream doubleStream = DoubleStream.of(x.getMaSituation().getPriceSma().getMa10(), x.getMaSituation().getPriceSma().getMa20(), x.getMaSituation().getPriceSma().getMa50());
        double maLineAverage = doubleStream.average()
                               .orElse(-100);

        boolean criteriaB = false;
		if( maLineAverage>0 && x.getCurrentStockBean().getC()>= maLineAverage ) 
		{
			Double ratio = (x.getCurrentStockBean().getC() - maLineAverage)/maLineAverage;
			if(ratio <= DEFAULT_RATIO)
				criteriaB = true;
			
		}
		return criteriaA && criteriaB;
	}
	
	
//	public boolean isMovingAverageMeet(InstantPerformanceResult x) {
//		double cur = x.getStagnantMARatio();
//		double prev1 = x.getStagnantPrev1MARatio();
//		double prev2 = x.getStagnantPrev2MARatio();
//		double prev3 = x.getStagnantPrev3MARatio();
//		
//		
//	}
	
	
	
	@Override
	public String toDescription() {
		return "待變(短中線), Bottom> 10D,20D,50D And 20D>50D, 20D>200D";
	}
}
