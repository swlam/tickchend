package com.sjm.test.yahdata.analy.instantresult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.DoubleStream;

import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.KPatternConst;
import com.sjm.test.yahdata.analy.model.InstantPerformanceResult;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.PatternTrendHelper;

public class TrendStagnant extends BaseTrendGettingDirection{

	public static final Double DEFAULT_RATIO = 0.05;
	
	public TrendStagnant() {
	}

	@Override
	public String goAnalyze(InstantPerformanceResult x) {
		boolean isHit = this.isCategoryHit(x);
		
		if(isHit)
			return StockPickTagger.STAGNANT;
		
		return null;
	}
	
	
	@Override
	public boolean isCategoryHit(InstantPerformanceResult x) {
		
		if(	this.isMeetTradingVolume(x)
			&& this.hasExtreamLowVol(x)	
			&& 
			(	x.getStrongWeakTypeToday().getType().contains(Const.NA)
				&& x.getStrongWeakTypePrevDay().getType().contains(Const.NA)
				&& x.getStrongWeakTypePrev2Days().getType().contains(Const.NA)
				&& x.getAbv50D()>0
				&& x.getAbv200D()>0
				&& this.isMeetStrongSideRSI(x)
				&&
					(
						x.getDailyCandleStatus().contains(KPatternConst.G_WHITE_K)==false
						&& x.getDailyCandleStatus().contains(KPatternConst.M_WHITE_K)==false
						&& x.getDailyCandleStatus().contains(KPatternConst.L_WHITE_K)==false
						&& x.getDailyCandleStatus().contains(KPatternConst.G_DARK_K)==false
						&& x.getDailyCandleStatus().contains(KPatternConst.M_DARK_K)==false
						&& x.getDailyCandleStatus().contains(KPatternConst.L_DARK_K)==false
					)							
			)
			&&( KHelper.isDoji(x.getCurrentStockBean()) || KHelper.isDoji(x.getPrevStockBean()) || KHelper.isDoji(x.getPrev2StockBean()) )
		){
			return true;
		}
		return false;
	}

	
	
	
	
	public String toDescription() {
		return "待變, 3天強弱=NA, >50D, >200D, RSI(9)>0.5, RSI(14)>0.5";
	}
}
