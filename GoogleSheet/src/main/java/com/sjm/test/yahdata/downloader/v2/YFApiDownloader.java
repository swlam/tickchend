package com.sjm.test.yahdata.downloader.v2;

import com.google.gson.Gson;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.cfg.GlobalConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.net.URLEncoder;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

//https://query2.finance.yahoo.com/v8/finance/chart/TSLA?period1=63072000&period2=3471292800&interval=1d&events=history
@Slf4j
public class YFApiDownloader {
//    public static void main(String[] args) {
//
//        printTimestamps();
//        printYyyyMMddTimestamps();
//
//        fetchDataFromUrl("TSLA");
//        fetchDataFromUrl("AAPL");
//    }

    public void download(String downloadInterval, String stockTicker) throws Exception {
        String ticker = URLEncoder.encode(stockTicker, "UTF-8");

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://query2.finance.yahoo.com/v8/finance/chart/" + ticker + "?period1=631152000&period2=3471292800&interval=1d&events=history")
                .build();
        Gson gson = new Gson();

        String savedFileName = GlobalConfig.getDefaultDownloadPath(downloadInterval) + stockTicker;
        if (!savedFileName.endsWith(".csv"))
            savedFileName = savedFileName.concat(".csv");

        String savedFileNameTmp = savedFileName+".tmp";

        try {

            Response response = client.newCall(request).execute();
            String jsonData = response.body().string();
            // Now you have the JSON data in the 'jsonData' string
//            System.out.println(ticker + " download DONE");

            YahooStockBean chartData = gson.fromJson(jsonData, YahooStockBean.class);
//            System.out.println("download DONE:  " + chartData.getChart().getMResult().get(0).getMMeta().getMSymbol());

            new File(savedFileName).delete();

            int size = chartData.getChart().getMResult().get(0).getMTimestamp().size();

            StringBuilder contentLine = new StringBuilder();
            List<String> contentList = new ArrayList<String>();
            contentList.add("Date,Open,High,Low,Close,Adj Close,Volume");

            contentLine.setLength(0);

            for(int i = 0; i < size; i++) {
//                StockBean stockBean = new StockBean(chartData.getChart().getMResult().get(0).getMMeta().getMSymbol());
//
//                stockBean.setTxnDate( this.convertEpochSecondToYyyyMMdd(chartData.getChart().getMResult().get(0).getMTimestamp().get(i)));
//                stockBean.setO(chartData.getChart().getMResult().get(0).getMIndicators().getMQuote().get(0).getMOpen().get(i));
//                stockBean.setC(chartData.getChart().getMResult().get(0).getMIndicators().getMQuote().get(0).getMClose().get(i));
//                stockBean.setH(chartData.getChart().getMResult().get(0).getMIndicators().getMQuote().get(0).getMHigh().get(i));
//                stockBean.setL(chartData.getChart().getMResult().get(0).getMIndicators().getMQuote().get(0).getMLow().get(i));
//                stockBean.setVolume(chartData.getChart().getMResult().get(0).getMIndicators().getMQuote().get(0).getMVolume().get(i));
//                System.out.println(stockBean);


                contentLine.append(this.convertEpochSecondToYyyyMMdd(chartData.getChart().getMResult().get(0).getMTimestamp().get(i)));
                contentLine.append(","); contentLine.append(chartData.getChart().getMResult().get(0).getMIndicators().getMQuote().get(0).getMOpen().get(i));
                contentLine.append(","); contentLine.append(chartData.getChart().getMResult().get(0).getMIndicators().getMQuote().get(0).getMHigh().get(i));
                contentLine.append(","); contentLine.append(chartData.getChart().getMResult().get(0).getMIndicators().getMQuote().get(0).getMLow().get(i));
                contentLine.append(","); contentLine.append(chartData.getChart().getMResult().get(0).getMIndicators().getMQuote().get(0).getMClose().get(i));
                contentLine.append(","); contentLine.append(chartData.getChart().getMResult().get(0).getMIndicators().getMQuote().get(0).getMClose().get(i));
                contentLine.append(","); contentLine.append(chartData.getChart().getMResult().get(0).getMIndicators().getMQuote().get(0).getMVolume().get(i));


                contentList.add(contentLine.toString());

                contentLine.setLength(0);
            }

            appendContentToFile(savedFileName, contentList);
            new File(savedFileNameTmp).delete();


        } catch (Exception e) {
            throw e;
        }
    }

    private void appendContentToFile(String filename, List<String> contentList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            for (String line : contentList) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private  List<String> readContentFromFile(String filename) {
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

    public void printTimestamps() {
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
    private String convertEpochSecondToYyyyMMdd(long epochSeconds) {
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneId.systemDefault());
        String formattedDate = date.format(formatter);
        return formattedDate;
    }
    private void printYyyyMMddTimestamps() {
        String dateString = "1990-01-01"; // Example date in "yyyy-MM-dd" format

        // Parse the date string to LocalDate
        LocalDate date = LocalDate.parse(dateString);

        // Convert LocalDate to Epoch seconds
        long epochSeconds = date.atStartOfDay(ZoneOffset.UTC).toEpochSecond();

        System.out.println(">> Date String: " + dateString);
        System.out.println(">> Epoch Seconds: " + epochSeconds);
    }
}
