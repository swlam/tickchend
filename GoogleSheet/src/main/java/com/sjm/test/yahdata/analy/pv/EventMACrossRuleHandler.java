package com.sjm.test.yahdata.analy.pv;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;

import com.sjm.test.yahdata.analy.bean.VolumePriceBean;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.cfg.BacktestConfig;
import com.sjm.test.yahdata.analy.conts.ta.CandleEventTagEnum;
import com.sjm.test.yahdata.analy.conts.ta.RuleConst;
import com.sjm.test.yahdata.analy.ta.VolRuleBase;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossDown10D50DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossDown2D19DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossDown3D18DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossDown50D200DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossDown50D250DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossDown5D10DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossUp10D20DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossUp10D50DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossUp150D200DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossUp2D19DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossUp3D18DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossUp3MALinesRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossUp50D200DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossUp50D250DRule;
import com.sjm.test.yahdata.analy.ta.rule.ma.event.CrossUp5D10DRule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventMACrossRuleHandler {
	private Set<VolRuleBase> ruleBaseSet = null;
	
	public EventMACrossRuleHandler() {
		String msg = " ***   " +BacktestConfig.MA_VALIDATE_CFG +" ***";
		log.info("\n"+msg);
		
		ruleBaseSet = new HashSet<VolRuleBase>(50);
		
		
		ruleBaseSet.add(new CrossUp5D10DRule());
		ruleBaseSet.add(new CrossUp3D18DRule());
		ruleBaseSet.add(new CrossUp2D19DRule());
		ruleBaseSet.add(new CrossUp150D200DRule());
		ruleBaseSet.add(new CrossUp50D250DRule());
		ruleBaseSet.add(new CrossUp50D200DRule());
		ruleBaseSet.add(new CrossUp10D50DRule());
		ruleBaseSet.add(new CrossUp10D20DRule());
		ruleBaseSet.add(new CrossDown2D19DRule());
//		
		ruleBaseSet.add(new CrossDown5D10DRule());
		ruleBaseSet.add(new CrossDown2D19DRule());
		ruleBaseSet.add(new CrossDown3D18DRule());
		ruleBaseSet.add(new CrossDown10D50DRule());
		ruleBaseSet.add(new CrossDown50D250DRule());
		ruleBaseSet.add(new CrossDown50D200DRule());	
		ruleBaseSet.add(new CrossUp3MALinesRule());

		
	}
	

	private VolumePriceBean fireRules(List<StockBean> prevList, StockBean curr, int stockListIdx) {
		
		Set<CandleEventTagEnum> signSet = new HashSet<CandleEventTagEnum>();
		boolean isBenchmarkEvent = false;
		boolean isOccurenceEvent = false;
		for (VolRuleBase volRuleBase : ruleBaseSet) {
			
			boolean b = volRuleBase.detect(prevList, curr);
			
			if(b) {
				if(volRuleBase.getBenchmarkCandleTag()!=null) {
					signSet.add(volRuleBase.getBenchmarkCandleTag());
					isBenchmarkEvent = true;
				}
				
				if(volRuleBase.getOccurCandleTag()!=null) {
					signSet.add(volRuleBase.getOccurCandleTag());
					isOccurenceEvent = true;
				}
			}
		}
		
		if(signSet.isEmpty())
			return null;
		
		VolumePriceBean tmp = new VolumePriceBean(curr.getStockCode());
		tmp.setSignSet(signSet);
		tmp.setChainIdx(stockListIdx);
		tmp.setBenchmarkEvent(isBenchmarkEvent);
		tmp.setOccurenceEvent(isOccurenceEvent);
		try {
			BeanUtils.copyProperties(tmp ,curr);			
		} catch (Exception e) {
			log.warn(curr.getStockCode()+" "+e.getMessage());
		} 		
		
		return tmp;
		
	}

	
	
	
	public Set<VolRuleBase> getRuleBaseSet(){
		if(ruleBaseSet == null)
			ruleBaseSet = new HashSet<VolRuleBase>(50);
		
		return ruleBaseSet;
	}
	
	
	
	public  List<VolumePriceBean> goThruRules(List<StockBean> trunkList) {
		
		int startIdx = RuleConst.YEAR_NUM_OF_DAYS ;
//		if(trunkList.size()<MIN_DATA_SIZE) {
//			logger.warn("Return goThruRules() function , due to Hist data size < "+MIN_DATA_SIZE);
//			return new ArrayList<VolumePriceBean>(0);
//		}
		if(startIdx > trunkList.size())
			startIdx = 1;
		
		List<VolumePriceBean> resultList = new ArrayList<VolumePriceBean>(10);
		
		for (int i = startIdx; i < trunkList.size(); i++) {
			
			int mRangeStartId = i - RuleConst.YEAR_NUM_OF_DAYS; //MONTHLY_SAMPLE_SIZE;
			if(mRangeStartId<0)
				mRangeStartId=0;
			
			List<StockBean> prevMonthlyList = trunkList.subList(mRangeStartId, i);
			
			StockBean curr = trunkList.get(i);
			
			VolumePriceBean tmp = this.fireRules(prevMonthlyList, curr, i);
			if(tmp!=null)
				resultList.add(tmp);
			
			if(BacktestConfig.isPrintCandleTag)
				tmp.printResult();
			
		}
		return resultList;
		
	}
	

	public  List<VolumePriceBean> goThruRules(List<StockBean> trunkList, int MIN_DATA_SIZE) {
		
		
		if(trunkList==null || trunkList.size()<MIN_DATA_SIZE) {
			log.warn("Return goThruRules() function , due to Hist data size < "+MIN_DATA_SIZE);
			return new ArrayList<VolumePriceBean>(0);
		}
		
		
		List<VolumePriceBean> resultList = new ArrayList<VolumePriceBean>(10);
		int startIdxWindow = 0;
//		int startIdxLoop = 0;
		//case 1
		if(trunkList.size() >= RuleConst.YEAR_NUM_OF_DAYS) {
			startIdxWindow = trunkList.size() - MIN_DATA_SIZE;
//			startIdxLoop = startIdxWindow - RuleConst.DEFAULT_NUM_OF_DAYS;
		}else {
			startIdxWindow = trunkList.size() - MIN_DATA_SIZE ;
//			startIdxLoop = 0;
		}
//		if(startIdxLoop <0) {
//			logger.warn(" startIdx error : "+ trunkList.get(0).getStockCode() );
//			return new ArrayList<VolumePriceBean>(0);
//		}
			
		
		for (int i = 0; i < MIN_DATA_SIZE; i++) {		

			List<StockBean> prevList = trunkList.subList(0, startIdxWindow) ;
			
			StockBean curr = trunkList.get(startIdxWindow);
			
//			startIdxLoop++;
			startIdxWindow++;
			
			
			VolumePriceBean tmp = this.fireRules(prevList, curr, i);
			if(tmp!=null)
				resultList.add(tmp);
			
			if(BacktestConfig.isPrintCandleTag)
				tmp.printResult();
			
		}
		return resultList;
		
	}

}
