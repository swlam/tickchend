package com.sjm.test.yahdata.analy.ta.rule.ma.crossing.winrate;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.BaseSummaryStatistics;


// Hit: match and up trend, Miss: match and down trend
public class MACrossWinRateResultSummary {

	String stockCode;
	String lastHappenedDate;
	String tag;
	long upCount;
	long total;
	double hitRatio;
	int days;
	
	String lastHitDate;
	String lastMissDate;
	
	WinRateResultDetail hitResult;
	WinRateResultDetail missResult;
	
	private BaseSummaryStatistics end2EndStat;
	private BaseSummaryStatistics c2cStat;
	private BaseSummaryStatistics c2lStat;
	private BaseSummaryStatistics c2hStat;
	private BaseSummaryStatistics l2hStat;
	
	public MACrossWinRateResultSummary(String stockCode, String tag, int days) {
			this.stockCode = stockCode;
			this.tag = tag;
			this.days = days;
	}

	
	public String getTag() {
		return tag;
	}


	public void setTag(String tag) {
		this.tag = tag;
	}


	public long getUpCount() {
		return upCount;
	}


	public void setUpCount(long hitCount) {
		this.upCount = hitCount;
	}


	public long getTotal() {
		return total;
	}


	public void setTotal(long total) {
		this.total = total;
	}


	public double getHitRatio() {
		return hitRatio;
	}


	public void setHitRatio(double hitRatio) {
		this.hitRatio = hitRatio;
	}


	public String getLastHitDate() {
		return lastHitDate;
	}


	public void setLastHitDate(String lastHitDate) {
		this.lastHitDate = lastHitDate;
	}


	public String getLastMissDate() {
		return lastMissDate;
	}


	public void setLastMissDate(String lastMissDate) {
		this.lastMissDate = lastMissDate;
	}


	public WinRateResultDetail getHitResult() {
		return hitResult;
	}


	public void setHitResult(WinRateResultDetail hitResult) {
		this.hitResult = hitResult;
	}


	public WinRateResultDetail getMissResult() {
		return missResult;
	}


	public void setMissResult(WinRateResultDetail missResult) {
		this.missResult = missResult;
	}
	
	


	public String getStockCode() {
		return stockCode;
	}


	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}


	public int getDays() {
		return days;
	}


	public void setDays(int days) {
		this.days = days;
	}


	public String getMessage() {
		
		StringBuilder rtn = new StringBuilder();
		
		rtn.append("\t"+this.getStockCode()+"\t"+this.getTag()+"\t" + this.getLastHappenedDate() + " \t" + this.getDays()+ "\t" + GeneralHelper.toPct(this.getHitRatio()));
		rtn.append("\t"+this.getUpCount() + "\t"+total);				
		rtn.append("\t"+ this.getLastHitDate() + "\t"+ this.getLastMissDate() );
		rtn.append("\tHIT"+"\t"+ this.getHitResult().getMessage());
		rtn.append("\tMISS"	+ "\t"+ this.getMissResult().getMessage() );
		
		
		
		return rtn.toString();
		
	}

	
	
	
	public BaseSummaryStatistics getEnd2EndStat() {
		return end2EndStat;
	}


	public void setEnd2EndStat(BaseSummaryStatistics end2EndStat) {
		this.end2EndStat = end2EndStat;
	}


	public BaseSummaryStatistics getC2cStat() {
		return c2cStat;
	}


	public void setC2cStat(BaseSummaryStatistics c2cStat) {
		this.c2cStat = c2cStat;
	}


	public BaseSummaryStatistics getC2lStat() {
		return c2lStat;
	}


	public void setC2lStat(BaseSummaryStatistics c2lStat) {
		this.c2lStat = c2lStat;
	}


	public BaseSummaryStatistics getC2hStat() {
		return c2hStat;
	}


	public void setC2hStat(BaseSummaryStatistics c2hStat) {
		this.c2hStat = c2hStat;
	}


	public BaseSummaryStatistics getL2hStat() {
		return l2hStat;
	}


	public void setL2hStat(BaseSummaryStatistics l2hStat) {
		this.l2hStat = l2hStat;
	}


	public String getLastHappenedDate() {
		return lastHappenedDate;
	}


	public void setLastHappenedDate(String happenedDate) {
		this.lastHappenedDate = happenedDate;
	}


	public String toString() {
		return this.getTag() +", Days: " + days +", HitRatio: "+ GeneralHelper.toPct(this.getHitRatio());
	}
	
	

}
