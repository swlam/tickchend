package com.sjm.test.yahdata.analy.helper;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.cfg.GlobalConfig;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class UserCsvWriter {
	
	
    public static void main(String[] args) throws IOException {

        // set correct directory as output
        File csvOutputFile = new File("C:\\csv_tests\\user_output.csv");

//        StockBean sb = new StockBean();
//        UserDto userDto1 = new UserDto(1L, "Ann", 30);
//        UserDto userDto2 = new UserDto(2L, "Seth", 28);
//        UserDto userDto3 = new UserDto(3L, "Emm", 24);
//
//        List<UserDto> list = new ArrayList<>(Arrays.asList(userDto1, userDto2, userDto3));
//
//        CsvMapper mapper = new CsvMapper();
//        mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
//
//        CsvSchema schema = CsvSchema.builder().setUseHeader(true)
//                .addColumn("id")
//                .addColumn("name")
//                .addColumn("age")
//                .build();
//
//        ObjectWriter writer = mapper.writerFor(UserDto.class).with(schema);
//
//        writer.writeValues(csvOutputFile).writeAll(list);
//
//        System.out.println("Users saved to csv file under path: ");
//        System.out.println(csvOutputFile);
    }
    
    
  //European countries use ";" as 
    //CSV separator because "," is their digit separator
    private static final String CSV_SEPARATOR = ",";
    public static void writeToCSV(List<StockBean> productList, String interval, String csvFileName)
    {
        try
        {
        	String fullPath = GlobalConfig.getDefaultDownloadPath(interval)+"/"+csvFileName+".csv";
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullPath), "UTF-8"));
            for (StockBean product : productList)
            {
            	if(product.getTxnDate()==null || product.getO() == 0.0)
            		continue;
            	
                StringBuffer oneLine = new StringBuffer();
                oneLine.append(product.getTxnDate());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(product.getO());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(product.getH());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(product.getL());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(product.getC());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(product.getC());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(product.getVolume());
                
                
                bw.write(oneLine.toString());
                bw.newLine();
            }
            bw.flush();
            bw.close();
            System.out.println(csvFileName+".csv DONE");
        }catch (Exception e){e.printStackTrace();}
    }
    
    
 public static void appendToCSV(String csvFileName, String result) throws IOException{
    	
		try(FileWriter pw = new FileWriter(csvFileName, true)) {
	        
			pw.append(result);	
			
	        pw.flush();
		}
		log.info("Export file: "+ csvFileName+" DONE");
		
 }
    public static void appendToCSV(String csvFilePath, List<String> results) throws IOException{
		try(FileWriter pw = new FileWriter(csvFilePath, true)) {
			for (String string : results) {
//				str.append("\r\n");
				pw.append(string);	
			}
			
			
	        pw.flush();
		}
		
	}
    
}