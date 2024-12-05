package com.sjm.test.yahdata.analy.instantresult;

import java.util.stream.DoubleStream;

import com.sjm.test.yahdata.analy.conts.KPatternConst;
import com.sjm.test.yahdata.analy.model.InstantPerformanceResult;

public class TrendStagnantMidTerm extends TrendStagnantSMTerm{

	public static final Double DEFAULT_RATIO = 0.05;
	
	public TrendStagnantMidTerm() {
	}

	@Override
	public String goAnalyze(InstantPerformanceResult x) {
		boolean isHit = this.isCategoryHit(x);
		
		if(isHit)
			return StockPickTagger.STAGNANT_MID_TERM;
		
		return null;
	}
	
	
	@Override
	public boolean isCategoryHit(InstantPerformanceResult x) {
		
		if(	this.isMeetTradingVolume(x)
			&& this.isMovingAverageMeet(x)
			&& this.hasExtreamLowVol(x)	
			&& this.isMeetStrongSideRSI(x)
			&& 
			(KPatternConst.L_WHITE_K.equalsIgnoreCase(x.getDailyCandleStatus())==false 
			&& KPatternConst.L_DARK_K.equalsIgnoreCase(x.getDailyCandleStatus())==false
			&& KPatternConst.M_DARK_K.equalsIgnoreCase(x.getDailyCandleStatus())==false)
		)
		{
			return true;
		}
		return false;
	}

	//50,100,200
	@Override
	public boolean isMovingAverageMeet(InstantPerformanceResult x) {

		boolean b1 = x.getMaSituation().getPriceSma().getMa20() >= x.getMaSituation().getPriceSma().getMa50() 
					&& x.getMaSituation().getPriceSma().getMa20() >= x.getMaSituation().getPriceSma().getMa200(); 
		
		double curLow = x.getCurrentStockBean().getL();
		boolean b2 = (curLow >= x.getMaSituation().getPriceSma().getMa50() && curLow >= x.getMaSituation().getPriceSma().getMa100() && curLow >= x.getMaSituation().getPriceSma().getMa200());
		
		boolean criteriaA = (b1 && b2 && x.getStagnantMidTermMARatio()< DEFAULT_RATIO);
		
		DoubleStream doubleStream = DoubleStream.of(x.getMaSituation().getPriceSma().getMa50(), x.getMaSituation().getPriceSma().getMa100(), x.getMaSituation().getPriceSma().getMa200());
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
	
	@Override
	public String toDescription() {
		return "待變(中線), L>=50D, 100D, 200D And 20D>50D, 20D>200D";
	}
}
