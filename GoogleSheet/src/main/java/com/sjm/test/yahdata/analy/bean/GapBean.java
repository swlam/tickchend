package com.sjm.test.yahdata.analy.bean;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

//@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@AllArgsConstructor
@Setter
@Getter
public class GapBean {

	private String stockCode;

	private String date;


	private double gapTop;


	private double gapBottom;


	private double gapPct;


	private boolean isUP;


	private boolean isEnoughtVolume=true;


	private double volumeChgPct;

	public GapBean() {

	}
	public GapBean(String stockCode, String date) {
		this.stockCode = stockCode;
		this.date = date;		
	}

//	public String getStockCode() {
//		return stockCode;
//	}
//
//	public void setStockCode(String stockCode) {
//		this.stockCode = stockCode;
//	}
//
//	public String getDate() {
//		return date;
//	}
//
//	public void setDate(String date) {
//		this.date = date;
//	}
//
//	public double getGapTop() {
//		return gapTop;
//	}
//
//	public void setGapTop(double gapTop) {
//		this.gapTop = gapTop;
//	}
//
//	public double getGapBottom() {
//		return gapBottom;
//	}
//
//	public void setGapBottom(double gapBottom) {
//		this.gapBottom = gapBottom;
//	}

//	public boolean isUP() {
//		return isUP;
//	}
//
//	public void setUP(boolean isUP) {
//		this.isUP = isUP;
//	}

//	public String toString() {
//		return stockCode + ":"
//	}
}
