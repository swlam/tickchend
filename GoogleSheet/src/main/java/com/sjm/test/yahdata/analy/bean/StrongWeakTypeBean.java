package com.sjm.test.yahdata.analy.bean;

import com.sjm.test.yahdata.analy.conts.Const;

public class StrongWeakTypeBean {

	String type = null;
	Double maxDistancePct = null;
	
	public StrongWeakTypeBean() {
		
		type = Const.NA;
		maxDistancePct = null;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Double getMaxDistancePct() {
		return maxDistancePct;
	}

	public void setMaxDistancePct(Double maxDistancePct) {
		this.maxDistancePct = maxDistancePct;
	}
	
	public String toString() {
		return type;
	}

}
