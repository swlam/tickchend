package com.sjm.test.yahdata.analy.repo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;

public class StockMarketRepo {

	private static List<StockBean> trunk = null;
	private static Set<String> productCodeSet = null;
	
	static {
		trunk = new ArrayList<StockBean> (1000);
	}
	
	static {
		productCodeSet = new HashSet<String> (100);
	}
	
	
	public static void append(StockBean bean){
		trunk.add(bean);
	}
	
	public static List<StockBean> getTrunk(){
		return trunk;
	}
	
	public static List<StockBean> getTrunk(String stockCode){
		return StreamTransformHelper.extractByStockCode(trunk, stockCode);
	}

	
	public static Set<String> getProductCodeSet(){
		return productCodeSet;
	}
	
}
