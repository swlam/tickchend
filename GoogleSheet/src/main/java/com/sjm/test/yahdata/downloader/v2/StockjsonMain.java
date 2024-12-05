package com.sjm.test.yahdata.downloader.v2;

import com.google.gson.Gson;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

//https://query2.finance.yahoo.com/v8/finance/chart/TSLA?period1=63072000&period2=3471292800&interval=1d&events=history
public class StockjsonMain {
    public static void main(String[] args) {

        printTimestamps();
        printYyyyMMddTimestamps();

        fetchDataFromUrl("TSLA");
        fetchDataFromUrl("AAPL");
    }

    public static void fetchDataFromUrl(String ticker) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://query2.finance.yahoo.com/v8/finance/chart/" + ticker + "?period1=631152000&period2=3471292800&interval=1d&events=history")
                .build();
        Gson gson = new Gson();


        try {
            Response response = client.newCall(request).execute();
            String jsonData = response.body().string();
            // Now you have the JSON data in the 'jsonData' string
            System.out.println(ticker + " download DONE");
//            System.out.println(jsonData);
            YahooStockBean chartData = gson.fromJson(jsonData, YahooStockBean.class);
            System.out.println("Result: " + chartData.getChart().getMResult().get(0).getMMeta().getMSymbol());

            int size = chartData.getChart().getMResult().get(0).getMTimestamp().size();
            for(int i = 0; i < size; i++) {
                StockBean stockBean = new StockBean(chartData.getChart().getMResult().get(0).getMMeta().getMSymbol());

                stockBean.setTxnDate( convertEpochSecondToYyyyMMdd(chartData.getChart().getMResult().get(0).getMTimestamp().get(i)));
                stockBean.setO(chartData.getChart().getMResult().get(0).getMIndicators().getMQuote().get(0).getMOpen().get(i));
                stockBean.setC(chartData.getChart().getMResult().get(0).getMIndicators().getMQuote().get(0).getMClose().get(i));
                stockBean.setH(chartData.getChart().getMResult().get(0).getMIndicators().getMQuote().get(0).getMHigh().get(i));
                stockBean.setL(chartData.getChart().getMResult().get(0).getMIndicators().getMQuote().get(0).getMLow().get(i));
                stockBean.setVolume(chartData.getChart().getMResult().get(0).getMIndicators().getMQuote().get(0).getMVolume().get(i));
                System.out.println(stockBean);
                /*chartData.getChart().getMResult().get(0).getMIndicators().getMQuote().get(0).getMOpen().get(i);
                chartData.getChart().getMResult().get(0).getMIndicators().getMQuote().get(0).getMClose().get(i);
                chartData.getChart().getMResult().get(0).getMIndicators().getMQuote().get(0).getMHigh().get(i);
                chartData.getChart().getMResult().get(0).getMIndicators().getMQuote().get(0).getMLow().get(i);
                chartData.getChart().getMResult().get(0).getMIndicators().getMQuote().get(0).getMVolume().get(i);*/
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static List<String> readContentFromFile(String filename) {
        List<String> contentList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentList;
    }

    public static void printTimestamps() {
        //period1=946684800&period2=1725667200
        long[] timestamps = {1277818200, 1277904600, 1726061400, 1726154629};

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (long timestamp : timestamps) {
            LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
            String formattedDate = date.format(formatter);
            System.out.println(formattedDate);

        }
    }

    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static String convertEpochSecondToYyyyMMdd(long epochSeconds) {
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneId.systemDefault());
        String formattedDate = date.format(formatter);
        return formattedDate;
    }
    public static void printYyyyMMddTimestamps() {
        String dateString = "1990-01-01"; // Example date in "yyyy-MM-dd" format

        // Parse the date string to LocalDate
        LocalDate date = LocalDate.parse(dateString);

        // Convert LocalDate to Epoch seconds
        long epochSeconds = date.atStartOfDay(ZoneOffset.UTC).toEpochSecond();

        System.out.println(">> Date String: " + dateString);
        System.out.println(">> Epoch Seconds: " + epochSeconds);
    }
}
