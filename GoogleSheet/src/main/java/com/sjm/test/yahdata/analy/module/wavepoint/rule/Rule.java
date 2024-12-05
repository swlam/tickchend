package com.sjm.test.yahdata.analy.module.wavepoint.rule;

public interface Rule {

	boolean evaluate(Expression expression);
	Result getResult();

}
