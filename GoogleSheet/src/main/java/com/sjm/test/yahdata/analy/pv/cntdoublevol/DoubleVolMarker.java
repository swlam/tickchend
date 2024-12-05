package com.sjm.test.yahdata.analy.pv.cntdoublevol;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;

import lombok.Data;

@Data
public class DoubleVolMarker {
	private StockBean now;

	private int listIndex;
	
	public DoubleVolMarker() {
		// TODO Auto-generated constructor stub
	}

}
