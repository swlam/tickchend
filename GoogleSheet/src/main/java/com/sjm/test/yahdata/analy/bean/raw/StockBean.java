package com.sjm.test.yahdata.analy.bean.raw;

import java.util.Date;

//import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.GapBean;
import com.sjm.test.yahdata.analy.bollinger.BollingerBand;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
@EqualsAndHashCode(callSuper = true)
@Document(collection = "stocks")
@Accessors(chain = true)
@NoArgsConstructor
@Data
public class StockBean extends BaseStockBean{

	@Indexed(unique = true)
	@Field("txnDateInt")
	private int txnDateInt;

	@Field("lastModifiedDateTime")
	private Date lastModifiedDateTime;// CSV file lastModified

	@Field("dayNameOfWeek")
	private String dayNameOfWeek;

	@Field("bodyDailyChgPct")
	private double bodyDailyChgPct;

	@Field("highestLowestPct")
	private double highestLowestPct;

	@Field("riseToday")
	private boolean riseToday;

	@Field("isOpenLowCloseHigh")
	private boolean isOpenLowCloseHigh;

	@Field("dayChgPct")
	private double dayChgPct;

	@Field("day2HChgPct")
	private double day2HChgPct;

	@Field("day2LChgPct")
	private double day2LChgPct;

	@Field("dayVolumeChgPct")
	private double dayVolumeChgPct;

	@Field("estTradeAmount")
	private double estTradeAmount;

	//@DBRef
	@Field("gapBean")
	private GapBean gapBean;

	//extra field
	private BollingerBand bollingerBand;

	@Field("rsi9")
	private double rsi9;

	@Field("rsi14")
	private double rsi14;

	@Field("bodyTop")
	private double bodyTop;

	@Field("bodyBottom")
	private double bodyBottom;
	
//	private double ma2;
//	private double ma5;
//	private double ma19;
//	private double ma10;
//	private double ma20;
//	private double ma50;
//	private double ma100;
//	private double ma200;
//	private double ma250;

	private PriceSMABean priceSma = new PriceSMABean();
	private VolumeSMABean volumeSma = new VolumeSMABean();
	
//	private MACDData macdData;
	
	
	public StockBean(String stockCode) {
		super(stockCode);
	}

	public StockBean(String stockCode, String txnDate, double o, double h, double l, double c) {
		super(stockCode);
		setTxnDate(txnDate);
		this.setO(o);
		this.setH(h);
		this.setL(l);
		this.setC(c);
		this.setBodyTop(Math.max(o, c));
		this.setBodyBottom(Math.min(o, c));
	}

//	public double getBodyTop() {
////		return (this.getO() > this.getC()) ? this.getO() : this.getC();
//		return Math.max(o, c);
//	}
//
//	public double getBodyBottom() {
////		return (this.getO() < this.getC()) ? this.getO() : this.getC();
//		return Math.min(o, c);
//	}
	
	
	
	public String toString() {
//		return this.getStockCode()+" "+this.getTxnDate()+" O:"+ this.getO() +" C:"+ this.getC();
		return this.getStockCode() + " " + this.getTxnDate() + " O:" + this.getO() + " C:" + this.getC() + " H:"
				+ this.getH() + " L:" + this.getL()+ " riseToday:" + this.isRiseToday();
	}

}
