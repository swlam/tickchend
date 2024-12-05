package com.sjm.test.yahdata.analy.instantresult;

import java.util.Set;

import lombok.Data;

@Data
public class StrongWeakHitResult {

	private boolean isHit;
	private Set<String> hittedCategories;

}
