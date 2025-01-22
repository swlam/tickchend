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



	/*
	使用 stream().distinct().toList() 方法对 topCandidates 列表进行去重操作。
	distinct() 方法内部会使用 equals 和 hashCode 方法来判断两个对象是否相等。
	如果两个对象的 hashCode 值相同，才会调用 equals 方法进行进一步的比较。
	如果 equals 方法返回 true，则认为这两个对象是相同的，其中一个会被去重。
	*/
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		BaseStockBean that = (BaseStockBean) obj;
		return Double.compare(this.o, that.o) == 0 &&
				Double.compare(this.h, that.h) == 0 &&
				Double.compare(this.l, that.l) == 0 &&
				Double.compare(this.c, that.c) == 0 &&
				Double.compare(this.volume, that.volume) == 0 &&
				txnDate.equals(that.txnDate);
	}


	/*
	如果 stockCode 不为 null，则使用 stockCode 的 hashCode 方法生成哈希码。
	如果 stockCode 为 null，则返回 0。
	txnDate 的哈希码：
	使用 31 作为乘数，这是一个常见的做法，可以减少哈希冲突。
	如果 txnDate 不为 null，则使用 txnDate 的 hashCode 方法生成哈希码。
	如果 txnDate 为 null，则返回 0。
	组合哈希码：
	将 stockCode 和 txnDate 的哈希码组合起来，生成最终的哈希码。
	 */
	@Override
	public int hashCode() {
		int result = stockCode != null ? stockCode.hashCode() : 0;
		result = 31 * result + (txnDate != null ? txnDate.hashCode() : 0);
		return result;
	}

}
