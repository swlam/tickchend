package com.sjm.test.yahdata.analy.module;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;

public class WeekRhythm {

	public WeekRhythm() {
		// TODO Auto-generated constructor stub
	}
	
//	public static void main(String arg[]) {
//		
//	}
	
	
	public static String test(List<StockBean> list) {
		StringBuilder sb = new StringBuilder();
		
		List<WeekRhythmResult> resultList = new ArrayList<WeekRhythmResult>();
		for(int i=1; i< list.size(); i++) {
			StockBean elem = list.get(i);			
			String direction = "";
			
				StockBean elemPrev = list.get(i-1);			
				if(elem.getC()>elemPrev.getC())
					direction = "UP";
				else if(elem.getC()<elemPrev.getC())
					direction = "DOWN";
				else
					direction = "STEADY";
		
			
			
			LocalDate d = LocalDate.parse(elem.getTxnDate());
			
			sb.append(d.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault())+":"+direction);
			
			resultList.add(new WeekRhythmResult(d.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()), direction));
		}
		
		
		
		Map<String, Long> upCnt = resultList.stream().filter(book -> book.getDirection().equalsIgnoreCase("UP"))
			    .collect(Collectors.groupingBy(WeekRhythmResult::getWeekName, Collectors.counting()));
		Map<String, Long> downCnt = resultList.stream().filter(book -> book.getDirection().equalsIgnoreCase("DOWN"))
			    .collect(Collectors.groupingBy(WeekRhythmResult::getWeekName, Collectors.counting()));		
//		Map<String, Long> steadyCnt = resultList.stream().filter(book -> book.getDirection().equalsIgnoreCase("STEADY"))
//			    .collect(Collectors.groupingBy(WeekRhythmResult::getWeekName, Collectors.counting()));
		
		return "UP:"+upCnt.toString() + " DOWN:"+ downCnt.toString();
	}
	

}

class WeekRhythmResult{
	String weekName;
	String direction;
	
	public WeekRhythmResult(String weekName, String direction) {
		this.weekName = weekName;
		this.direction = direction;
	}
	
	
	public String getWeekName() {
		return weekName;
	}


	public void setWeekName(String weekName) {
		this.weekName = weekName;
	}


	public String getDirection() {
		return direction;
	}


	public void setDirection(String direction) {
		this.direction = direction;
	}


	public String toString(){
		return this.weekName +":"+ this.direction;
	}
}