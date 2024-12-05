package com.sjm.test.yahdata.analy.pv.cntdoublevol;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;

import lombok.Data;

@Data
public class ShrinkageVolMarker {
	private StockBean now;

	private int listIndex;
	
	public ShrinkageVolMarker() {
		// TODO Auto-generated constructor stub
	}

}
