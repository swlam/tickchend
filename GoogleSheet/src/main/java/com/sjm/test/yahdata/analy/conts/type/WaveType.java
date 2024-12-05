package com.sjm.test.yahdata.analy.conts.type;

public enum WaveType {   
    
	TOP("WaveTop"),    
	BOT("WaveBottom");
	
	
    private final String displayName;
    
    WaveType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}