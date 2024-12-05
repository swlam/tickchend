package com.sjm.test.yahdata.analy.conts.type;

public enum KBodyType {   
	
	XL(6),    
	L(5),    
	M(4),
	GENERAL(3),
	S(2),
	XS(1),
	NONE(0)
	;
	
	private final int value;

	KBodyType(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}