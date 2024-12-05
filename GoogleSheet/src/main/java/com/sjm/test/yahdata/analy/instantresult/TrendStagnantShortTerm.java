package com.sjm.test.yahdata.analy.instantresult;

import java.util.stream.DoubleStream;

import com.sjm.test.yahdata.analy.conts.KPatternConst;
import com.sjm.test.yahdata.analy.model.InstantPerformanceResult;

public class TrendStagnantShortTerm extends TrendStagnantSMTerm{

	public static final Double DEFAULT_RATIO = 0.03;
	
	public TrendStagnantShortTerm() {
	}

	@Override
	public String goAnalyze(InstantPerformanceResult x) {
		boolean isHit = this.isCategoryHit(x);
		
		if(isHit)
			return StockPickTagger.STAGNANT_SHORT_TERM;
		
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

	//5, 10, 50
	@Override
	public boolean isMovingAverageMeet(InstantPerformanceResult x) {
		//
		boolean b1 = x.getMaSituation().getPriceSma().getMa5() >= x.getMaSituation().getPriceSma().getMa50() && x.getMaSituation().getPriceSma().getMa20() >= x.getMaSituation().getPriceSma().getMa200(); 
		
		double curLow = x.getCurrentStockBean().getL();
		boolean b2 = (curLow >= x.getMaSituation().getPriceSma().getMa5() || curLow >= x.getMaSituation().getPriceSma().getMa10() || curLow >= x.getMaSituation().getPriceSma().getMa20()); 

		boolean criteriaA = (b1 && b2 && x.getStagnantShortTermMARatio()< DEFAULT_RATIO);
		
		// criteria B
		DoubleStream doubleStream = DoubleStream.of(x.getMaSituation().getPriceSma().getMa5(), x.getMaSituation().getPriceSma().getMa10(), x.getMaSituation().getPriceSma().getMa20());
        double maLineAverage = doubleStream.average()
                               .orElse(-100);

        boolean criteriaB = false;
		if( maLineAverage>0 && x.getCurrentStockBean().getC()>= maLineAverage) 
		{
			Double ratio = (x.getCurrentStockBean().getC() - maLineAverage)/maLineAverage;
			if(ratio <= DEFAULT_RATIO)
				criteriaB = true;
			
		}
		return criteriaA && criteriaB;
	}
	
	
	@Override
	public String toDescription() {
		return "待變(短線), L>=5D, 10D, 50D And 20D>50D, 20D>200D";
	}
}
