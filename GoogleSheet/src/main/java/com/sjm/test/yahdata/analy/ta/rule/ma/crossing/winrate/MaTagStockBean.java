package com.sjm.test.yahdata.analy.ta.rule.ma.crossing.winrate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;

public class MaTagStockBean extends StockBean {

	private Integer chainIdx = 0;
	
	private Set<String> tagSet;
	
	
	public MaTagStockBean(String stockCode) {
		super(stockCode);
	}

	public Set<String> getTagSet() {
		return tagSet;
	}

	public void setTagSet(Set<String> tagSet) {
		this.tagSet = tagSet;
	}

	public void setChainIdx(Integer chainIdx) {
		this.chainIdx = chainIdx;
	}
	
	public Integer getChainIdx() {
		return this.chainIdx;
	}

	
	public void printResult() {		
		List<String> signList = this.getTagSet().parallelStream()
        .collect(Collectors.toList());
		System.out.println(this.getTxnDate() +", c:"+ this.getC() + " " +signList);
	}
	
	public String toString() {
		return super.toString() + " "+getTagSet();
		
	}
}
