package com.sjm.test.yahdata.analy.bean;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;

public class VolumePriceBean extends StockBean {

	private Integer chainIdx = 0;
	
	private Set<CandleEventTagEnum> signSet;
	private boolean isBenchmarkEvent;
	private boolean isOccurenceEvent;
	
	public VolumePriceBean(String stockCode) {
		super(stockCode);
	}


	public Set<CandleEventTagEnum> getSignSet() {
		return signSet;
	}

	public void setSignSet(Set<CandleEventTagEnum> signSet) {
		this.signSet = signSet;
	}

	public void setChainIdx(Integer chainIdx) {
		this.chainIdx = chainIdx;
	}
	
	public Integer getChainIdx() {
		return this.chainIdx;
	}
	

	
	public boolean isBenchmarkEvent() {
		return isBenchmarkEvent;
	}


	public void setBenchmarkEvent(boolean isBenchmarkEvent) {
		this.isBenchmarkEvent = isBenchmarkEvent;
	}


	public boolean isOccurenceEvent() {
		return isOccurenceEvent;
	}


	public void setOccurenceEvent(boolean isOccurenceEvent) {
		this.isOccurenceEvent = isOccurenceEvent;
	}


	public void printResult() {
		System.out.println(this.toString());
	}
	
	
	public String toString() {
		
		List<String> signList = this.getSignSet().stream().map(CandleEventTagEnum::getDescription)
		        .collect(Collectors.toList());
		
		return this.getTxnDate()+ ": "+ signList;
		
	}
}
