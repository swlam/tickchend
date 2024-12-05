package com.sjm.test.yahdata.analy.vol;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.maas.util.GeneralHelper;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.conts.Const;

public class ConcentratedVolumeAnalyzer {
    /*// 找出成交量集中的时段
    public List<String> findConcentratedVolumePeriods(List<StockBean> stockDataList) {
        List<String> periods = new ArrayList<>();

        double maxVolume = 0;
        boolean inPeriod = false;

        // 遍历股票数据，找出最大成交量
        for (StockBean stockData : stockDataList) {
        	double volume = stockData.getVolume();

            if (volume > maxVolume) {
                maxVolume = volume;
            }
        }

        double threshold = maxVolume / 2;  // 根据最大成交量自动调节阈值

        int startIdx = 0;
        int endIdx = 0;

        for (int i = 0; i < stockDataList.size(); i++) {
        	StockBean stockData = stockDataList.get(i);
            double volume = stockData.getVolume();

            // 如果成交量超过阈值并且当前不在时段中，则开始一个新的时段
            if (volume >= threshold && !inPeriod) {
                startIdx = i;
                inPeriod = true;
            }
            // 如果成交量超过阈值并且当前在时段中，则更新结束索引
            else if (volume >= threshold && inPeriod) {
                endIdx = i;
            }
            // 如果成交量低于阈值并且当前在时段中，则结束时段，并将时段添加到结果列表中
            else if (volume < threshold && inPeriod) {
                endIdx = i - 1;
                inPeriod = false;
                periods.add(stockDataList.get(startIdx).getTxnDate().toString() +
                        " to " + stockDataList.get(endIdx).getTxnDate().toString());
            }
        }

        // 如果当前仍在时段中，则结束时段，并将时段添加到结果列表中
        if (inPeriod) {
            endIdx = stockDataList.size() - 1;
            periods.add(stockDataList.get(startIdx).getTxnDate().toString() +
                    " to " + stockDataList.get(endIdx).getTxnDate().toString());
        }

        return periods;
    }
    
    
 // 找出指定时段内的价格范围
    public void findPriceRangeForPeriod(List<StockBean> stockDataList, String period) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(period.split(" to ")[0], formatter);  // 解析时段的开始日期
        LocalDate endDate = LocalDate.parse(period.split(" to ")[1], formatter);  // 解析时段的结束日期

        double minPrice = Double.MAX_VALUE;
        double maxPrice = Double.MIN_VALUE;

        // 遍历股票数据，找出指定时段内的最低价和最高价
        for (StockBean stockData : stockDataList) {
            LocalDate transactionDay = LocalDate.parse(stockData.getTxnDate(), formatter);

            // 如果交易日期在指定时段内，则更新最低价和最高价
            if (!transactionDay.isBefore(startDate) && !transactionDay.isAfter(endDate)) {
                double low = stockData.getL();
                double high = stockData.getH();

                if (low < minPrice) {
                    minPrice = low;
                }

                if (high > maxPrice) {
                    maxPrice = high;
                }
            }
        }

        // 打印价格范围
        System.out.println("Price Range for Period " + period + ": " +
                "Min Price = " + minPrice + ", Max Price = " + maxPrice);
    }
    */
	
	
	// 找出最大的3个成交量集中的价格区间
    public void findTop3ConcentratedVolumePriceRanges_v1(List<StockBean> stockDataList, int numDays) {
        // 根据指定的天数计算开始日期
        int startIndex = Math.max(stockDataList.size() - numDays, 0);

        // 保存每个价格区间的总成交量
        Map<String, Double> priceRangeVolumeMap = new HashMap<>();

        // 遍历股票数据，计算每个价格区间的总成交量
        for (int i = startIndex; i < stockDataList.size(); i++) {
        	StockBean stockData = stockDataList.get(i);
            String priceRange = getPriceRange(stockData); // 获取价格区间
            double volume = stockData.getVolume(); // 获取成交量

            // 更新每个价格区间的总成交量
            priceRangeVolumeMap.put(priceRange, priceRangeVolumeMap.getOrDefault(priceRange, 0.0) + volume);
        }

        // 创建一个根据成交量排序的列表
        List<Map.Entry<String, Double>> sortedVolumeList = new ArrayList<>(priceRangeVolumeMap.entrySet());
        sortedVolumeList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

     // 创建一个格式化成交量的对象
        DecimalFormat decimalFormat = new DecimalFormat("#,##0");
        
        // 打印最大的3个成交量集中的价格区间
        int count = 0;
        for (Map.Entry<String, Double> entry : sortedVolumeList) {
            if (count >= 3) {
                break; // 已经找到了最大的3个成交量集中的价格区间，退出循环
            }
            String priceRange = entry.getKey();
            double volume = entry.getValue();
            String formattedVolume = decimalFormat.format(volume); // 格式化成交量
            System.out.println("Price Range: " + priceRange + ", Volume: " + formattedVolume);
            
            count++;
        }
    }
    
    
    public List<String> findTop3ConcentratedVolumePriceRanges(List<StockBean> stockDataList, int numDays) {
        // 以价格区间为键，累计交易量为值，构建一个价格区间到交易量的映射
        Map<String, Double> volumeByPriceRange = new HashMap<>();
     // 根据指定的天数计算开始日期
        int startIndex = Math.max(stockDataList.size() - numDays, 0);
        // 遍历股票数据，计算每个价格区间的总成交量
        for (int i = startIndex; i < stockDataList.size(); i++) {
        	StockBean stockData = stockDataList.get(i);
            String priceRange = getPriceRange(stockData); // 获取价格区间
            double volume = stockData.getVolume(); // 获取成交量

            // 更新每个价格区间的总成交量
            volumeByPriceRange.put(priceRange, volumeByPriceRange.getOrDefault(priceRange, 0.0) + volume);
        }
        
        

        
     // 创建一个根据成交量排序的列表
        List<Map.Entry<String, Double>> sortedVolumeList = new ArrayList<>(volumeByPriceRange.entrySet());
        sortedVolumeList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

     // 创建一个格式化成交量的对象
//        DecimalFormat decimalFormat = new DecimalFormat("#,##0");
        
        List<VolumePriceRangeModel> rtnList = new ArrayList<>();
        String rtnString = "";
        // 打印最大的3个成交量集中的价格区间
        int count = 0;
        for (Map.Entry<String, Double> entry : sortedVolumeList) {
            if (count >= 3) {
                break; // 已经找到了最大的3个成交量集中的价格区间，退出循环
            }
            String priceRange = entry.getKey();
            double volume = entry.getValue();
//            String formattedVolume = decimalFormat.format(volume); // 格式化成交量
//            System.out.println("Price Range: " + priceRange + ", Volume: " + formattedVolume);
            
            rtnList.add(new VolumePriceRangeModel(numDays, priceRange, volume));
            rtnString +=priceRange+", ";
            count++;
        }
        List<String> priceRanges = rtnList.stream()
                .map(VolumePriceRangeModel::getPriceRange)
                .collect(Collectors.toList());
        
//        findClosestPriceRange(priceRanges);
//        return rtnString;
        return priceRanges;
    }

    
 // 检查新的价格区间是否与已选择的价格区间重叠
    private boolean isOverlapping(String priceRange, List<String> selectedPriceRanges) {
        for (String selectedPriceRange : selectedPriceRanges) {
            String[] selectedRange = selectedPriceRange.split(" - ");
            String[] newRange = priceRange.split(" - ");
            int selectedLow = Integer.parseInt(selectedRange[0]);
            int selectedHigh = Integer.parseInt(selectedRange[1]);
            int newLow = Integer.parseInt(newRange[0]);
            int newHigh = Integer.parseInt(newRange[1]);
            // 如果新的价格区间与已选择的价格区间有重叠，则返回 true
            if (newLow <= selectedHigh && newHigh >= selectedLow) {
                return true;
            }
        }
        return false;
    }
    
//    public String findClosestPriceRange(StockBean stockData, List<String> priceRanges) {
//        double price = stockData.getC();
//        double minDistance = Double.MAX_VALUE;
//        String closestPriceRange = "";
//
//        for (String priceRange : priceRanges) {
//            String[] rangeValues = priceRange.split(" - ");
//            double low = Double.valueOf(Integer.parseInt(rangeValues[0]));
//            double high = Double.valueOf(Integer.parseInt(rangeValues[1]));
//
//            // 计算价格与价格区间的距离
//            double distance;
//            if (price < low) {
//                distance = low - price;
//            } else if (price > high) {
//                distance = price - high;
//            } else {
//                distance = 0;
//            }
//
//            // 更新最小距离和对应的价格区间
//            if (distance < minDistance) {
//                minDistance = distance;
//                closestPriceRange = priceRange;
//            }
//        }
//
//        return closestPriceRange;
//    }
    
    public String findClosestPriceRange(StockBean stockData, List<String> priceRanges) {
    	double price = stockData.getC();
    	double minPercentageDistance = Double.MAX_VALUE;
        String closestPriceRange = "";

        for (String priceRange : priceRanges) {
            String[] rangeValues = priceRange.split(" - ");
            double low = Double.valueOf(Integer.parseInt(rangeValues[0]));
            double high = Double.valueOf(Integer.parseInt(rangeValues[1]));

            // 计算价格与价格区间的百分比距离
            double percentageDistance = 0.0;
            if (price < low) {
                percentageDistance = (price - low) * 100.0 / low;
            } else if (price > high) {
                percentageDistance = (price - high) * 100.0 / high;
            }

            // 更新最小百分比距离和对应的价格区间
            if (Math.abs(percentageDistance) < Math.abs(minPercentageDistance)) {
                minPercentageDistance = percentageDistance;
                closestPriceRange = priceRange;
                
            }
        }
        
//        if (minPercentageDistance >= 0) {
//            return closestPriceRange +" " + GeneralHelper.to100Pct(minPercentageDistance);
//        } else {
//            return "负百分比距离：" + closestPriceRange;
//        }
        if(minPercentageDistance== Double.MAX_VALUE)
        	return Const.SPACE;
        return closestPriceRange +" (" + GeneralHelper.to2DecimalPlaces(minPercentageDistance)+"%)";
    }
//    // 获取股票数据的价格区间
//    private String getPriceRange(StockBean stockData) {
//        double low = stockData.getL();
//        double high = stockData.getH();
//        return String.format("%.2f - %.2f", low, high);
//    }
    
 // 获取股票数据的价格区间（向下取整）
    private String getPriceRange(StockBean stockData) {
        double low = stockData.getL();
        double high = stockData.getH();
        long roundedLow = (long) Math.floor(low); // 向下取整
        long roundedHigh = (long) Math.floor(high); // 向下取整
        return roundedLow + " - " + roundedHigh;
    }
    
    
// // 获取股票数据的价格区间
//    private String getPriceRange(StockBean stockData) {
//        double low = stockData.getL();
//        double high = stockData.getH();
//        long roundedLow = Math.round(low); // 四舍五入到个位数
//        long roundedHigh = Math.round(high); // 四舍五入到个位数
//        return roundedLow + " - " + roundedHigh;
//    }
//    
    
}