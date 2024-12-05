package com.sjm.test.yahdata.analy.conts.type;

public enum VolumePriceType {   

	DIVERGENCE_PRICE_INCREASE_VOL_DECREASE("價升量縮"),
	DIVERGENCE_PRICE_DECREASE_VOL_DECREASE("價跌量縮(好)"),
	DIVERGENCE_PRICE_INCREASE_VOL_INCREASE("價升量升"),	
	DIVERGENCE_PRICE_DECREASE_VOL_INCREASE("價跌量升"),
	
	//price shrinks at a low point, and the stock price rebounds
	//低位縮量,價反彈
	//Volume shrinks at low level, stock price rebounds
	VOL_SHRINKS_AT_BOT_AND_PRICE_REBOUND("低位縮量,價反彈"),
	VOL_SHRINKS_EXTREMELY("極縮量"),	
	//Extreme shrinkage
	;
	
    private final String displayName;
    
    VolumePriceType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String toString() {
    	return this.displayName;
    }
}