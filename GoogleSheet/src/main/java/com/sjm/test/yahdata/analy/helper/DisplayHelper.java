package com.sjm.test.yahdata.analy.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.plaf.synth.SynthSeparatorUI;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;

public class DisplayHelper {

	public DisplayHelper() {
		// TODO Auto-generated constructor stub
	}

	public static String toFileName(File file) {
		return file.getName().replace(".csv", "");
	}
	
	public static String toMonth(String monthpattern) {
		return monthpattern.replaceAll("-", "");
	}
	
	public static String toMonthPattern(String monthpattern) {
		return "-"+monthpattern.replaceAll("-", "")+"-";
	}
	
	
	
	public static List<String> toMonthPattern(List<String> monthpatternList) {
		List<String> bList =  new ArrayList<String>();
		for (String string : monthpatternList) {
			bList.add(DisplayHelper.toMonthPattern(string));
		}
		
		return bList;
		
	}
	
	
	public static List<String> toMonth(List<String> monthpatternList) {
		List<String> rtn = new ArrayList<String>(2);
		for (String string : monthpatternList) {
			rtn.add(string);
		}
		return rtn;
	}
}
	
	
