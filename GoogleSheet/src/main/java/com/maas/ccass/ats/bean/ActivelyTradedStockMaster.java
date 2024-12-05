package com.maas.ccass.ats.bean;

import java.time.LocalDate;

import com.maas.util.GeneralHelper;
import com.maas.util.NumberUtils;

import lombok.Data;

@Data
public class ActivelyTradedStockMaster {

	private String txnDate;
	private String market;
	
	private Integer rank; //rank by turnover
	
	private String stockCode;
	private String stockName;
	private long buyTrades = 0l;
	private long sellTrades = 0l;
	private long turnover = 0l;
	
	//below is additional 
	private long netAmount = 0l;
	private Integer rankNetBuyAmount;
	private Integer rankNetSellAmount;
	
	private double percentageOfturnover = 0.0;
	
	
	private ActivelyTradedStock shenzhen;
	
	private ActivelyTradedStock shanghai;
	
	public ActivelyTradedStockMaster() {
	}
	
	public LocalDate getTxnDateObject() {
		return LocalDate.parse(txnDate);
	}
	public String toBuySellResult() {
		if(netAmount==0)
			return " _ ";
		if(netAmount > 0)
			return " 買("+NumberUtils.amountConversion(netAmount)+") ";
		
		return " 沽("+NumberUtils.amountConversion(netAmount)+") ";
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
