package com.sjm.test.yahdata.downloader;

import com.sjm.test.yahdata.analy.cfg.GlobalConfig;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
public class YFPageDownloadDemo {


//    private final static String URL_TEMPLATE = "https://hk.finance.yahoo.com/quote/%s/history/?period1=1277769600&period2=16512768000&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true";
    private final static String URL_TEMPLATE = "https://hk.finance.yahoo.com/quote/%s/history/";
    public static void main(String[] args) throws Exception {

        YFPageDownloadDemo demoapp = new YFPageDownloadDemo();
        demoapp.download("D","0700.HK");
        demoapp.download("D","NVDA");
        demoapp.download("D","BABA");
        demoapp.download("D","QQQ");
        demoapp.download("D","SPY");
        demoapp.download("D","TSLA");
    }
    public  void download(String downloadInterval, String stockTicker) throws Exception {
        String ticker = URLEncoder.encode(stockTicker, "UTF-8");
        //System.out.println(stockTicker);
        //String url = "https://hk.finance.yahoo.com/quote/" + ticker + "/history/";
        String url = String.format(URL_TEMPLATE, ticker);

        String savedFileName = GlobalConfig.getDefaultDownloadPath(downloadInterval) + stockTicker;
        if (!savedFileName.endsWith(".csv"))
            savedFileName = savedFileName.concat(".csv");

        String savedFileNameTmp = savedFileName+".tmp";
        try {
            Document doc = Jsoup.connect(url).timeout(2500).get();
            Elements table = doc.select("#Col1-1-HistoricalDataTable-Proxy > section > div:nth-child(2) > table");

            FileWriter csvWriter = new FileWriter(savedFileNameTmp);
//            csvWriter.append("Date,Open,High,Low,Close,Adj Close,Volume\n");

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy年MM月dd日");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

            for (Element row : table.select("tr")) {
                Elements columns = row.select("td");

                if(columns.size()==0) continue;
                try {
                    String dateStr = columns.get(0).text();
                    Date date = inputFormat.parse(dateStr);
                    String formattedDate = outputFormat.format(date);

                    csvWriter.append(formattedDate);
                    for (int i = 1; i < columns.size(); i++) {
                        csvWriter.append(",").append(columns.get(i).text().replace(",", ""));
                    }
                    csvWriter.append("\n");
                }catch (ParseException e){

                }

            }
            csvWriter.flush();
            csvWriter.close();
            log.debug(savedFileNameTmp + " downloaded.");

            List<String> contentList = readContentFromFile(savedFileNameTmp);
            contentList.add("Date,Open,High,Low,Close,Adj Close,Volume");

            if (contentList != null) {
                Collections.reverse(contentList);
                new File(savedFileName).delete();

                appendContentToFile(savedFileName, contentList);
                new File(savedFileNameTmp).delete();
            }

            // Read the existing content of the file
//            BufferedReader reader = new BufferedReader(new FileReader(savedFileNameTmp));
//            String originalContent = reader.readLine();
//            reader.close();
//
//            // Append new data to the first line
//            BufferedWriter writer = new BufferedWriter(new FileWriter(savedFileNameTmp));
//            writer.write("New data to be appended," + originalContent);
//            writer.close();
            /*
            for (Element row : table.select("tr")) {
                Elements columns = row.select("td");
                for (Element column : columns) {
                    System.out.print(column.text() + " ");
                }
                System.out.println();
            }
            */
        } catch (Exception e) {
            throw e;
        }
    }

    private List<String> readContentFromFile(String filename) {
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
}
