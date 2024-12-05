package com.sjm.test.yahdata.analy.pv.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sjm.test.yahdata.analy.conts.type.VolumePriceType;

import lombok.Data;

@Data
public class PriceVolResult {

	private Set<VolumePriceType> divergenceType = new HashSet<VolumePriceType>();
	private List<String> dates = new ArrayList<String>(5);
	private String remark = "";
}

