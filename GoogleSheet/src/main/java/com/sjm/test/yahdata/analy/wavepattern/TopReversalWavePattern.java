package com.sjm.test.yahdata.analy.wavepattern;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.KPatternConst;
import com.sjm.test.yahdata.analy.conts.type.WaveType;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.module.wavepoint.bean.WavePoint;

import java.util.*;
import java.util.stream.Collectors;

public class TopReversalWavePattern extends BaseWavePattern{
    private static final int WINDOW_SIZE = 2;
    @Override
    public Set<String> find(List<StockBean> stockList, List<WavePoint> sortedTopList, List<WavePoint> sortedBotList) {
//        return Set.of();

        List<WavePoint> topbotList = new ArrayList<WavePoint>();
        topbotList.addAll(sortedTopList);
        topbotList.addAll(sortedBotList);

        List<WavePoint> sortedTopBotList = topbotList.stream().sorted(Comparator.comparing(e -> e.getDateInt())).collect(Collectors.toList());
//		sortedTopBotList = this.distinctSortedTopBotList(sortedTopBotList);
        return findTopReversal(stockList,sortedTopBotList);
    }

    public Set<String> findTopReversal(List<StockBean> stockList, List<WavePoint> sortedTopBotList){
		Set<String> msg = new LinkedHashSet<String>();
		if(sortedTopBotList.size()<3 )
			return msg;

		StockBean last1 = stockList.get(stockList.size()-1);

		WavePoint wpLast1 = sortedTopBotList.get(sortedTopBotList.size() - 1);
		WavePoint wpLast2 = sortedTopBotList.get(sortedTopBotList.size() - 2);
		WavePoint wpLast3 = sortedTopBotList.get(sortedTopBotList.size() - 3);

		WavePoint previouseTop = null;
		if( WaveType.TOP.equals(wpLast3.getType()) && WaveType.BOT.equals(wpLast2.getType()) && WaveType.TOP.equals(wpLast1.getType())){
			if(wpLast3.getStockBean().getBodyTop() > wpLast2.getH() && wpLast3.getH() < wpLast1.getStockBean().getBodyBottom()) {
				previouseTop = wpLast3;
			}
		}
		if( WaveType.TOP.equals(wpLast2.getType()) && WaveType.TOP.equals(wpLast1.getType())){
			if( wpLast2.getH() < wpLast1.getStockBean().getBodyBottom()) {
				previouseTop = wpLast2;
			}

		}

		boolean isMeet = previouseTop!=null ;



		WavePoint maxByH = sortedTopBotList
			      .stream()
			      .max(Comparator.comparing(WavePoint::getH))
			      .orElseThrow(NoSuchElementException::new);
		if( isMeet )
		{
			boolean isLastSkLowerPrevTop = last1.getC() < previouseTop.getH();

			if(isLastSkLowerPrevTop && maxByH.getDateInt() == wpLast1.getDateInt())
			{
				List<StockBean> subList = StreamTransformHelper.subListWithEndElement(stockList, wpLast1.getDate(), last1.getTxnDate());

				int confirmDateInt = 0;

				for(int i=1; i<subList.size(); i++)
				{
					StockBean elem = subList.get(i);

					if(elem.getC() < previouseTop.getStockBean().getBodyTop() && !elem.isRiseToday()) {
						confirmDateInt = elem.getTxnDateInt();
						break;
					}
				}

				if(confirmDateInt > 0 ) {
					String firstDateBreakMsg = "";
					if(last1.getTxnDateInt() == confirmDateInt)
						firstDateBreakMsg = Const.D0;

					msg.add(KPatternConst.KP_TOP_REVERSAL+firstDateBreakMsg);
				}

			}
		}

		return msg;
		/*
		 String confirmDate = "";
		 subList(wpLast3.getDate(), current.getTxnDate())

		 for loop {
		  if (elem.getC() < wpLast3.getBodyBottom() ){
		  	confirmDate = elem.getDate();
		  }
		 }
		 */
//		return Const.NA+"\t";
	}




}

