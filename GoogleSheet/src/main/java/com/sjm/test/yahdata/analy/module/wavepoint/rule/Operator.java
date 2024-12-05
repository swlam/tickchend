package com.sjm.test.yahdata.analy.module.wavepoint.rule;

public enum Operator {

//	public Operator() {
//		// TODO Auto-generated constructor stub
//	}

	ADD{
		public int apply(int a, int b) {
			return a + b;
		}
	},
	SUBTRACT{
		public int apply(int a, int b) {
			return a -+ b;
		}
	}
}
