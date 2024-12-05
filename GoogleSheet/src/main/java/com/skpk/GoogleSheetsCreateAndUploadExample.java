package com.skpk;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GoogleSheetsCreateAndUploadExample {

    private static final String APPLICATION_NAME = "Google Sheets Create and Upload Example";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String SHEET_NAME = "NewSheet";
    private static final String SPREADSHEET_ID = "1tRHVd0bGURbiLOVvUqDn1nA2kv7j0W0OIvo6bxn5c54";

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Check if the sheet exists
        boolean sheetExists = checkSheetExists(service, SPREADSHEET_ID, SHEET_NAME);

        if (!sheetExists) {
            // Create the new sheet
            createSheet(service, SPREADSHEET_ID, SHEET_NAME);
        }

        // Upload data to the sheet
        uploadDataToSheet(service, SPREADSHEET_ID, SHEET_NAME);
    }

    private static boolean checkSheetExists(Sheets service, String spreadsheetId, String sheetName) throws IOException {
        Spreadsheet spreadsheet = service.spreadsheets().get(spreadsheetId).execute();
        List<com.google.api.services.sheets.v4.model.Sheet> sheets = spreadsheet.getSheets();
        for (com.google.api.services.sheets.v4.model.Sheet sheet : sheets) {
            if (sheet.getProperties().getTitle().equals(sheetName)) {
                return true;
            }
        }
        return false;
    }

    private static void createSheet(Sheets service, String spreadsheetId, String sheetName) throws IOException {
        AddSheetRequest addSheetRequest = new AddSheetRequest()
                .setProperties(new SheetProperties()
                        .setTitle(sheetName));

        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                .setRequests(Collections.singletonList(new Request().setAddSheet(addSheetRequest)));

        service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
        System.out.println("Sheet '" + sheetName + "' created successfully.");
    }

    private static void uploadDataToSheet(Sheets service, String spreadsheetId, String sheetName) throws IOException {
        String range = sheetName + "!A1";
        String targetMonth = "12";
        String interval = "D";
        String YEARS_DATA_20 = "Y20";

        StringBuilder titles = new StringBuilder();
        titles.append("CODE ("+interval+")\t數據時間\tName\tSector\t所屬ETF\tDATE\t現價\tD%\t3D(o2c)%\t5D(o2c)%\tVol(-1D)%\tVol%\tEst.金額(B)");
        titles.append("\tB-type\tS-type\tFLAT-type");

        titles.append("\t小浪型\t小浪型state");
        titles.append("\t強弱(-1D)\t今天強弱");
        titles.append("\tK线Status(D-1)\tK线(Status)\tK线(Desc)");

        titles.append("\tVol(5D vs 20D)");
//		msg.append("\t"+WavePointAnalyticalResult.getColumnHeader());	//小浪方向\t突破Pct(小浪)\t小浪型狀\t上一個小浪頂底日
        titles.append("\t近日Last穿頭破腳/破腳穿頭");
        titles.append("\tPrice Status\tVolume Status");
        //msg.append("\t價量狀態\t價量狀態開始日期");
        titles.append("\t反轉型態\t反轉突破日\t反轉型態詳細");
        titles.append("\t近日上破BB\t月內變多頭排列\t近日出收集三胞胎形態\t近日大陰/大量陰日子\t昨今日平均線有支持/阻力");

        titles.append("\tRSI(9)\tRSI DIVER.\tRSI DIVER. DATES\tRSI DIVER.加劇");
        titles.append("\tMTD(O2C)%\tMTD(O2PH)%\tMTD(O2PL)%\tMTD 波幅");
        titles.append("\t近期GAP Type\t裂口大小%\t裂口Vol Pct\tGAP Type日期");
        titles.append("\t近期島型\t島型日期\t島型日數");
        titles.append("\t倍量數\t倍量日子[MA數]");
        titles.append("\t1D-Vol\t5D-Vol\t50D-Vol\t5X50D-Vol(UP)");
        titles.append("\t大於5D\t大於10D\t大於20D\t大於50D\t大於100D\t大於200D");
        titles.append("\tMA-CrossUP");

        titles.append("\t數據(10Y)\t"+targetMonth.replace("-", "")+"月上升機率");
        titles.append("\t歷史Avg(波幅)\tAVG(C2H)\t中位數(C2H)\t9成(C2-Peri-H)\tAVG(C2L)\t中位數(C2L)\t9成(C2-Peri-L)");
        titles.append("\t月低位日期\t月低位日數\t月低位日Prob.\t月高位日期\t月高位日數\t月高位日Prob");

        titles.append("\t數據(20Y)\t"+targetMonth.replace("-", "")+"月上升機率");

        titles.append("\t歷史Avg(波幅)\tAVG(C2H)\t中位數(C2H)\t9成(C2-Peri-H)\tAVG(C2L)\t中位數(C2L)\t9成(C2-Peri-L)");
        titles.append("\t月低位日期\t月低位日數\t月低位日Prob.\t月高位日期\t月高位日數\t月高位日Prob");
        titles.append("\t本月"+YEARS_DATA_20+"向好時段數目\t本月"+YEARS_DATA_20+"Y O2H最佳時段");
        titles.append("\t本月"+YEARS_DATA_20+"向淡時段數目\t本月"+YEARS_DATA_20+"Y O2L最佳時段");
        titles.append("\tIndustry");
        titles.append("\t業績公佈日");
//		msg.append("\t最近看好pattern\t最近看淡pattern");
        titles.append("\tYTD %\tQ1 %\tQ2 %\tQ3 %\tQ4 %");
        titles.append("\tMktCap.(B)\tPE\t所屬ETF");

        String message = titles.toString();
        // 准备数据，这里假设我们将字符串按行分割并插入到 Google Sheet 中
        List<List<Object>> values = new ArrayList<>();
        String[] lines = message.split("\t");
        values.add(Arrays.asList(lines));  // 将整个 lines 数组作为一行添加到 values 中

        ValueRange body = new ValueRange().setValues(values);

        // Data to write to the sheet
        service.spreadsheets().values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();
//        ValueRange body = new ValueRange()
//                .setValues(List.of(
//                        List.of("Name", "Age"),
//                        List.of("John Doe", "300")
//                ));

        // Write data to the sheet
        service.spreadsheets().values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();
        System.out.println("Data uploaded to sheet '" + sheetName + "' successfully.");
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = GoogleSheetsCreateAndUploadExample.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
}
