package com.sjm.test.yahdata.analy.conts.type;

public enum RsiType {   

	TOP_DIVERGENCE("RSI頂背馳"),  //top divergence  
	BOTTOM_DIVERGENCE("RSI底背馳"),
	TOP_DIVERGENCE_UP_BREAK("RSI頂背馳上破"),
	BOTTOM_DIVERGENCE_DOWN_BREAK("RSI頂背馳下破"),
	;
	
    private final String displayName;
    
    RsiType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String toString() {
    	return this.displayName;
    }
}