package com.sjm.test.yahdata.analy.conts.type;

public enum MovingAvgType {   
	PRICE("PRICE"),    
	VOLUME("VOLUME");
	
    private final String displayName;
    
    MovingAvgType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String toString() {
    	return getDisplayName();
    }
}