package com.sjm.test.yahdata.analy.conts.type;

import lombok.Getter;

@Getter
public enum KType {
	
	END2END_C2C("CC"),    
	END2END_C2L("CL"),
	END2END_C2H("CH"),
	END2END_L2H("LH"),
	END2END_L2C("LC"),
	END2END_H2L("HL"),
	END2END_O2O("OO"),
	END2END_O2C("OC"),
	END2END_O2H("OH"),
	
	O2PH("O2PH"),
	O2PL("O2PL"),
	C2PH("C2PH"),
	C2PL("C2PL"),
	
	PCT_RANGE("PCT-RANGE")
	;
	
    private final String displayName;
    
    KType(String displayName) {
        this.displayName = displayName;
    }

}