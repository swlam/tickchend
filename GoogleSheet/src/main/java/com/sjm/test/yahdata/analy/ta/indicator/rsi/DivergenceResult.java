package com.sjm.test.yahdata.analy.ta.indicator.rsi;

import java.util.ArrayList;
import java.util.List;

import com.sjm.test.yahdata.analy.conts.type.RsiType;

import lombok.Data;

@Data
public class DivergenceResult {

//	private Set<VolumePriceType> divergenceType = new HashSet<VolumePriceType>();
	private RsiType divergenceType;
	private List<String> dates = new ArrayList<String>(5);
	private String remark = "";
}
