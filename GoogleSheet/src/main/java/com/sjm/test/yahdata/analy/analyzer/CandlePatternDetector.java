package com.sjm.test.yahdata.analy.analyzer;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CandlePatternDetector {

    private static Map<Integer, List<String>> findNplets(List<StockBean> stockBeans) {
        Map<Integer, List<String>> npletDatesMap = new HashMap<>();
        int size = stockBeans.size();

        // 从 4 开始，因为三胞胎形态由 4 根蜡烛组成
        for (int n = 4; n <= size; n++) {
            List<String> npletDates = new ArrayList<>();
//            System.out.println("Checking for " + n + "-plet...");

            for (int i = 0; i <= size - n; i++) {
                StockBean A = stockBeans.get(i);
                boolean isNplet = true;

                for (int j = 1; j < n; j++) {
                    StockBean current = stockBeans.get(i + j);
                    if (!(A.getBodyTop() >= current.getBodyTop() &&
                            A.getBodyBottom() <= current.getBodyBottom())) {
                        isNplet = false;
                        break;
                    }
                }

                if (isNplet) {
                    npletDates.add(A.getTxnDate());
//                    System.out.println("Found " + n + "-plet starting at: " + A.getTxnDate());
                }
            }

            if (!npletDates.isEmpty()) {
                npletDatesMap.put(n, npletDates);
            }
        }

        return npletDatesMap;
    }

    public static String findNpletsResult(List<StockBean> stockBeans){
        Map<Integer, List<String>> npletDatesMap = findNplets(stockBeans);

        String result = "";
        if (npletDatesMap.isEmpty()) {
//            System.out.println("No nplet patterns found.");
            return result;
        } else {
            String endDate = "";
            int daysValue = 0;
            for (Map.Entry<Integer, List<String>> entry : npletDatesMap.entrySet()) {
                int n = entry.getKey();
                List<String> dates = entry.getValue();
                daysValue = n;

//                System.out.println(n + "-plet Dates:");
                for (String date : dates) {
//                    System.out.println(date);
                    endDate = date;
                }


                System.out.println();
            }

            result = endDate +"("+daysValue+")";
        }
        return result;
    }

    @Deprecated
    public static String findTriplePregnancyInPassFewDays(List<StockBean> srcstockList, int days) {
        if (srcstockList.size() < days) {
            return Const.SPACE;
        }
        int pregnancyDays = 3;

        String returnDate = "";
        List<StockBean> stockBeans = srcstockList.subList(srcstockList.size() - days, srcstockList.size());

        for (int i = 0; i < stockBeans.size() - (pregnancyDays ); i++) {
            StockBean a = stockBeans.get(i);
            List<StockBean> subsequentBeans = IntStream.range(i + 1, i + pregnancyDays+1)
                    .mapToObj(stockBeans::get)
                    .collect(Collectors.toList());

            if (subsequentBeans.stream().allMatch(b -> isCovering(a, b))) {
                returnDate = subsequentBeans.get(subsequentBeans.size() - 1).getTxnDate();
            }
        }

        return returnDate;
    }
    @Deprecated
    private static boolean isCovering(StockBean a, StockBean b) {
        // 实现 isCovering 方法的逻辑
        // 示例：假设 a 的最高价大于等于 b 的最高价且 a 的最低价小于等于 b 的最低价
        return a.getBodyTop() >= b.getBodyTop() && a.getBodyBottom() <= b.getBodyBottom();
	}
    public static void main(String[] args) {
        List<StockBean> stockBeans = new ArrayList<>();
        stockBeans.add(new StockBean("AAPL","2023-01-01", 100.0, 110.0, 90.0, 105.0));
        stockBeans.add(new StockBean("AAPL","2023-01-02", 105.0, 108.0, 102.0, 106.0));
        stockBeans.add(new StockBean("AAPL","2023-01-03", 116.0, 107.0, 104.0, 104.0));
        stockBeans.add(new StockBean("AAPL","2023-01-04", 105.0, 106.0, 103.0, 105.0));
        stockBeans.add(new StockBean("AAPL","2023-01-05", 104.0, 115.0, 100.0, 110.0));
        stockBeans.add(new StockBean("AAPL","2023-01-06", 110.0, 112.0, 108.0, 111.0));
        stockBeans.add(new StockBean("AAPL","2023-01-07", 111.0, 113.0, 109.0, 112.0));
        stockBeans.add(new StockBean("AAPL","2023-01-08", 112.0, 114.0, 110.0, 113.0));
        stockBeans.add(new StockBean("AAPL","2023-01-09", 115.9, 114.0, 110.0, 113.0));

        // 自动识别胞胎形态数量
        Map<Integer, List<String>> npletDatesMap = findNplets(stockBeans);

        if (npletDatesMap.isEmpty()) {
            System.out.println("No nplet patterns found.");
        } else {
            for (Map.Entry<Integer, List<String>> entry : npletDatesMap.entrySet()) {
                int n = entry.getKey();
                List<String> dates = entry.getValue();
                System.out.println(n + "-plet Dates:");
                for (String date : dates) {
                    System.out.println(date);
                }
                System.out.println();
            }
        }
    }

}
