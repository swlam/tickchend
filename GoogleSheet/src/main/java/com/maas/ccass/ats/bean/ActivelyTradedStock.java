package com.maas.ccass.ats.bean;

import com.maas.util.GeneralHelper;

import lombok.Data;
@Data
public class ActivelyTradedStock {

	private String txnDate;
	private String market;
	
	private Integer rank; //rank by turnover
	
	private String stockCode;
	private String stockName;
	private long buyTrades;
	private long sellTrades;
	private long turnover;
	
	//below is additional 
	private long netAmount;
	private Integer rankNetBuyAmount;
	private Integer rankNetSellAmount;
	
	private double percentageOfturnover;
	
	public ActivelyTradedStock() {
	}
	

//	public String getMarket() {
//		return market;
//	}
//
//	public void setMarket(String market) {
//		this.market = market;
//	}
//
//	public Integer getRank() {
//		return rank;
//	}
//
//	public void setRank(Integer rank) {
//		this.rank = rank;
//	}
//
//	public String getStockCode() {
//		return stockCode;
//	}
//
//	public void setStockCode(String stockCode) {
//		this.stockCode = stockCode;
//	}
//
//	public String getStockName() {
//		return stockName;
//	}
//
//	public void setStockName(String stockName) {
//		this.stockName = stockName;
//	}
//
//	public long getBuyTrades() {
//		return buyTrades;
//	}
//
//	public void setBuyTrades(long buyTrades) {
//		this.buyTrades = buyTrades;
//	}
//
//	public long getSellTrades() {
//		return sellTrades;
//	}
//
//	public void setSellTrades(long sellTrades) {
//		this.sellTrades = sellTrades;
//	}
//
//	public long getTurnover() {
//		return turnover;
//	}
//
//	public void setTurnover(long turnover) {
//		this.turnover = turnover;
//	}
//	
//	public String getTxnDate() {
//		return txnDate;
//	}
//
//
//
//	public void setTxnDate(String txnDate) {
//		this.txnDate = txnDate;
//	}
//
//
//
//	public long getNetAmount() {
//		return netAmount;
//	}
//
//
//
//	public void setNetAmount(long netAmount) {
//		this.netAmount = netAmount;
//	}
//
//
//
//
//	public Integer getRankNetBuyAmount() {
//		return rankNetBuyAmount;
//	}
//
//
//	public void setRankNetBuyAmount(Integer rankNetBuyAmount) {
//		this.rankNetBuyAmount = rankNetBuyAmount;
//	}
//
//
//	public Integer getRankNetSellAmount() {
//		return rankNetSellAmount;
//	}
//
//
//	public void setRankNetSellAmount(Integer rankNetSellAmount) {
//		this.rankNetSellAmount = rankNetSellAmount;
//	}
//
//
//	public double getPercentageOfturnover() {
//		return percentageOfturnover;
//	}
//
//
//	public void setPercentageOfturnover(double percentageOfturnover) {
//		this.percentageOfturnover = percentageOfturnover;
//	}


	public String toString() {
		return this.market +" "+this.txnDate + " " + this.getStockCode() + " "+this.getStockName().trim()
		+ " Rank-Turnover# "+this.getRank()
		+ " Rank-Buy# "+ (this.getRankNetBuyAmount()==null?"":this.getRankNetBuyAmount())
		+ " Rank-Sell# "+ (this.getRankNetSellAmount()==null?"":this.getRankNetSellAmount())
		+ " buy $"+GeneralHelper.formatToCurrencyText(this.getBuyTrades()) 
		+ " sell $"+GeneralHelper.formatToCurrencyText(this.getSellTrades())
		+ " net: $"+GeneralHelper.formatToCurrencyText(this.getNetAmount())
		+ " net/turnover: "+GeneralHelper.format(this.getPercentageOfturnover());
	}

}
