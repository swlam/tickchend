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
        String range = sheetName + "!A1:B2";

        // Data to write to the sheet
        ValueRange body = new ValueRange()
                .setValues(List.of(
                        List.of("Name", "Age"),
                        List.of("John Doe", "30")
                ));

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
