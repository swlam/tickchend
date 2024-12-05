package com.sjm.test.yahdata.analy.vol;

import lombok.Data;

@Data
public class VolumePriceRangeModel {
	private int priceRangeSize;
	private String priceRange;
	private double volume;

	public VolumePriceRangeModel(int priceRangeSize, String priceRange, double volume) {
		this.priceRangeSize= priceRangeSize;
		this.priceRange= priceRange;
		this.volume= volume;
	}
}
