package com.sjm.test.yahdata.analy.repo;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

//import org.apache.log4j.log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sjm.test.yahdata.analy.bean.StockProfileBean;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.helper.StreamTransformHelper;
import com.sjm.test.yahdata.analy.main.cycle.MonthlyPerformanceApp;
import com.sjm.test.yahdata.analy.probability.bean.BenchmarkBeanResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BenchmarkProbabilityAnalysisReportDataSourceManager {

    public BenchmarkProbabilityAnalysisReportDataSourceManager() {
    }

    public static void saveDataToDisk(List<BenchmarkBeanResult> dataList, String filePath) {
        if (dataList == null) {
            log.info("NO Data saved to disk due to dataList=NULL.");

            return;
        }
        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().setPrettyPrinting().create();

        String jsonData = gson.toJson(dataList);

        try {
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(jsonData);
            fileWriter.close();
            log.info("Data saved to disk successfully.");
        } catch (IOException e) {
            log.warn("BenchmarkBeanResult Data saved to disk failed. File not found: " + filePath, e.getMessage());
        }
    }

    public static void saveStockProfileDataToDisk(List<StockProfileBean> dataList, String filePath) {

        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().setPrettyPrinting().create();

        String jsonData = gson.toJson(dataList);

        try {
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(jsonData);
            fileWriter.close();

        } catch (IOException e) {
            log.warn("StockProfileBean Data saved to disk failed. File not found: " + filePath, e.getMessage());
        }
    }

    public static List<BenchmarkBeanResult> loadDataFromDisk(String filePath) {
        List<BenchmarkBeanResult> dataList = null;
        Gson gson = new Gson();

        try {
            FileReader fileReader = new FileReader(filePath);
//             dataList = gson.fromJson(fileReader, ArrayList.class);
            Type type = new TypeToken<ArrayList<BenchmarkBeanResult>>() {
            }.getType();
            dataList = gson.fromJson(fileReader, type);
            fileReader.close();
        } catch (IOException e) {
            log.warn("BenchmarkBeanResult Data load failed. File not found: " + filePath, e.getMessage());
        }

        return dataList;
    }


    public static List<StockProfileBean> loadStockProfileDataFromDisk(String filePath) {
        List<StockProfileBean> dataList = null;
        Gson gson = new Gson();

        try {
            FileReader fileReader = new FileReader(filePath);
//            dataList = gson.fromJson(fileReader, ArrayList.class);
            Type type = new TypeToken<ArrayList<StockProfileBean>>() {
            }.getType();
            dataList = gson.fromJson(fileReader, type);
            fileReader.close();
        } catch (IOException e) {
            log.warn("StockProfileBean data load failed. File not found: " + filePath, e.getMessage());
        }

        return dataList;
    }

    public static List<BenchmarkBeanResult> generateBenchmarkResult(String targetWatchMonth, List<String> stockCodePool, List<StockBean> fullTrunkList, String o2cOrC2) {

        List<BenchmarkBeanResult> stockPerformanceResultList = new ArrayList<BenchmarkBeanResult>(10);

        for (String code : stockCodePool) {
            List<StockBean> list = StreamTransformHelper.extractByStockCode(fullTrunkList, code);
            if (list == null || list.isEmpty())
                continue;// SKIP

            try {
                String beginDate = list.get(0).getTxnDate();
                log.debug("\n" + code + " Load data, select data from " + beginDate + " \n");

                BenchmarkBeanResult stockResult = null;

//					if(o2cOrC2.equalsIgnoreCase("C2C")) {
//						stockResult = MonthlyPerformanceApp.generateMonthProbability(code, beginDate, targetWatchMonth);
//					}else {
                stockResult = MonthlyPerformanceApp.generateMonthProbability(code, beginDate, targetWatchMonth, o2cOrC2);
//					}

                if (stockResult != null && stockResult.getNumOfYears() >= MonthlyPerformanceApp.REQUIRE_MIN_YRS_DATA) {
                    stockPerformanceResultList.add(stockResult);
                } else {
                    log.debug(code + " don't have " + targetWatchMonth + " data");
                }
            } catch (Exception e) {
                log.error("SKIP, due to error on Stock: " + code + ", list size = " + list.size(), e);
            }
        }
        return stockPerformanceResultList;
    }

}
