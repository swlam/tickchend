package com.sjm.test.yahdata.analy.module.wavepoint.rule;

import java.util.ArrayList;
import java.util.List;

public class RuleEngine {
	private static List<Rule> rules = new ArrayList<Rule>();
	static {
		rules.add(null);
	}
	
	public RuleEngine() {
		// TODO Auto-generated constructor stub
	}
	
	public Result process(Expression expression) {
		Rule rule = null;//rules.stream()
//				.filter( r -> r.evaluate(expression))
//				.findFirst();
//				.orElseThrow();
//				.orElseThrow()-> new IllegalArgumentException("Expression does not matches any Rule"));
		return rule.getResult();
	}

}
