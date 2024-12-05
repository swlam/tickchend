package com.sjm.test.yahdata.analy.bean.raw;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Accessors(chain = true)
@Setter
@Getter
public class BaseStockBean {
	@MongoId(FieldType.OBJECT_ID)
	protected String id;

	@Indexed(unique = true)
	@Field("stockCode")
	private String stockCode;

	@Field("txnDate")
	private String txnDate;

	@Field("stockName")
	private String stockName;

	@Field("o")
	public double o;

	@Field("l")
	public double l;

	@Field("h")
	public double h;

	@Field("c")
	public double c;

	@Field("volume")
	public double volume;


//	private MACDData macdData;
public BaseStockBean() {

}
	
	public BaseStockBean(String stockCode) {
		this.stockCode = stockCode;
	}

	public double getBodyTop() {
		return (this.o > this.c) ? this.o : this.c;
	}

	public double getBodyBottom() {
		return (this.o < this.c) ? this.o : this.c;
	}
	
	public String toString() {
		return this.getStockCode()+" "+this.getTxnDate()+" C:"+ this.getC();
	}
	
//	public String toString() {
////		return this.getStockCode()+" "+this.getTxnDate()+" O:"+ this.getO() +" C:"+ this.getC();
//		return this.getStockCode() + " " + this.getTxnDate() + " O:" + this.getO() + " C:" + this.getC() + " H:"
//				+ this.getH() + " L:" + this.getL()+ " riseToday:" + this.isRiseToday();
//	}

}
