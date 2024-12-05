package com.sjm.test.yahdata.analy.ta.rule.ma.crossing.winrate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;

public class RuleMACrossingHandler {

	public RuleMACrossingHandler() {}

	private static List<MACrossUpForWinRateRule> ruleRepository = new ArrayList<MACrossUpForWinRateRule>(10);
	
	public void addRuleRepository(MACrossUpForWinRateRule rule) {
		ruleRepository.add(rule);
	}
	
	public void addAllRules(List<MACrossUpForWinRateRule> rules) {
		
		ruleRepository.addAll(rules);
	}
	public List<MACrossUpForWinRateRule> getRuleRepository() {
		return ruleRepository;
	}
	
	
	public Set<String> fireRules(List<StockBean> prevList, StockBean curr) {
		
		Set<String> signSet = new HashSet<String>();
				
		for (MACrossUpForWinRateRule volRuleBase : getRuleRepository()) {
			boolean b = volRuleBase.detect(prevList, curr);
			
			if(b) {
				signSet.add(volRuleBase.getTag());				
			}
		}
		
		return signSet;
		
	}
}
