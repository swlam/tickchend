package com.sjm.test.yahdata.analy.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.primitives.Ints;
import com.sjm.test.yahdata.analy.bean.StockMetaBean;
import com.sjm.test.yahdata.analy.bean.StockProfileBean;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.cfg.GlobalConfig;
import com.sjm.test.yahdata.analy.probability.bean.BenchmarkBeanResult;
//import com.sjm.test.yahdata.analy.repo.MonthlyAnalysisReportDataSource;
import com.sjm.test.yahdata.analy.repo.BenchmarkProbabilityAnalysisReportDataSourceManager;
import com.sjm.test.yahdata.analy.repo.DataFileReader;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class CFGHelper {


	private static Map<String, StockMetaBean> stockMetaMap = null;
	static {
		stockMetaMap = new HashMap<String, StockMetaBean>(100);
	}
	@Deprecated
	private static Map<String, StockMetaBean>  getStockMetaMap(){
		return stockMetaMap;
	}


	private static Map<String, StockProfileBean> stockProfileMap = null;
	static {
		stockProfileMap = new HashMap<String, StockProfileBean>(100);
	}

	public static Map<String, StockProfileBean>  getStockProfileMap(){
		return stockProfileMap;
	}

	//For month data
	private static Map<String, BenchmarkBeanResult> monthBenchmarkBeanResultMap = null;
	static {
		monthBenchmarkBeanResultMap = new HashMap<String, BenchmarkBeanResult>(100);
	}

	public static Map<String, BenchmarkBeanResult>  getMonthBenchmarkBeanResultMap(){
		return monthBenchmarkBeanResultMap;
	}

	//individual stock benchmark data
	private static Map<String, List<BenchmarkBeanResult>> stockBenchmarkBeanResultMap = null;
	static {
		stockBenchmarkBeanResultMap = new HashMap<String, List<BenchmarkBeanResult>>(100);
	}

	public static Map<String, List<BenchmarkBeanResult>>  getIndividualStockBenchmarkBeanResultMap(){
		return stockBenchmarkBeanResultMap;
	}


	public CFGHelper() {
		loadMetaInfo(GlobalConfig.DEFAULT_DEFAULT_PATH);
	}


	public static List<StockBean> getStockRawData(String csvPath, String code){
		List<StockBean> customStartYearList = new ArrayList<StockBean>(100);

		try {
			DataFileReader reader = new DataFileReader();
			List<StockBean> list = reader.readCSVFileToMemory(csvPath+ code +".csv");

			customStartYearList = StreamTransformHelper.trimByCustomStartYear(list);
		}catch(Exception e) {
			log.error(code+": "+e.getMessage());
		}

		return customStartYearList;
	}


	public static void loadMetaInfo(String path) {

//		loadMetaInfo(path+"Tickers/", "america_2023-11-20.csv", false);
//		loadMetaInfo(path+"Tickers/", "hongkong_2021-12-28.csv", true);
//		loadCNMetaInfo(path+"Tickers/", "Other_SSE.csv");
//		loadCNMetaInfo(path+"Tickers/", "Other_SZSE.csv");
//		loadMetaInfo(path+"Tickers/", "Other.csv", false);

		loadStockProfile(path+"Tickers/","ticker_data.json");
		loadStockProfile(path+"Tickers/","ticker_additional.json");
//		System.out.println();
	}


	public static void loadStockProfile(String path, String fileName) {
		String csvFile = path + fileName;
//		DataFileReader reader = new DataFileReader();
		try {
			List<StockProfileBean> ls = BenchmarkProbabilityAnalysisReportDataSourceManager.loadStockProfileDataFromDisk(csvFile);
			for (StockProfileBean stockMetaBean : ls) {
				String code = stockMetaBean.getCode();

				getStockProfileMap().put(code, stockMetaBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		System.out.println();
	}

	@Deprecated
	public static void loadMetaInfo(String path, String fileName, boolean isHKCode) {
		String csvFile = path + fileName;
		DataFileReader reader = new DataFileReader();


		try {
			List<StockMetaBean> ls = reader.readStockMetaData(csvFile);
			for (StockMetaBean stockMetaBean : ls) {

				String code = stockMetaBean.getCode();

				if(isHKCode) {
					if(code.length()==5) {
						code =code+".HK";
					}else {
						code = String.format("%04d", Integer.parseInt(stockMetaBean.getCode()))+".HK";
					}
				}
				getStockMetaMap().put(code, stockMetaBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	@Deprecated
	public static void loadCNMetaInfo(String path, String fileName) {
		String csvFile = path + fileName;
		DataFileReader reader = new DataFileReader();
		try {
			List<StockMetaBean> ls = reader.readCNStockMetaData(csvFile);
			for (StockMetaBean stockMetaBean : ls) {
				String code = stockMetaBean.getCode();

				getStockMetaMap().put(code, stockMetaBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		System.out.println();
	}

	public static void validateConfig() throws Exception{
		List<String> monthLs = GlobalConfig.TARGET_MONTH;
		boolean isWrongInput = false;
		for (String string : monthLs) {
			String tmp = string.replaceAll("-", "");
			if(tmp.length()!=2 || string.length()!=4) {
				isWrongInput = true;
			}else {
				Integer intValue = Ints.tryParse(tmp);
				if(intValue==null) {
					isWrongInput = true;
				}
			}
		}
		if(isWrongInput) {
			String msg = "Wrong input, please check GlobalConfig: "+GlobalConfig.TARGET_MONTH;
			log.warn(msg);
			throw new Exception (msg);
		}

	}

	public static void loadMonthBenchmarkData(String countryMarket, int yearsOfData, String monthString) {
//		yearsOfData
//		String dataFile = GlobalConfig.getDefaultMonthlyProbPath() + GlobalConfig.getYearOfStockData()+"Y-"+countryMarket+monthString+"data.json";
		String dataFile = GlobalConfig.getDefaultMonthlyProbPath() + yearsOfData+"Y-"+countryMarket+monthString+"data.json";
//			String dataFile = GlobalConfig.getDefaultMonthlyProbPath() + countryMarket + monthString +"data.json"; //ADP-09-data.json

		List<BenchmarkBeanResult> resultList = BenchmarkProbabilityAnalysisReportDataSourceManager.loadDataFromDisk(dataFile);
		if(resultList==null)
			return;

		for (BenchmarkBeanResult benchmarkBeanResult : resultList) {
			getMonthBenchmarkBeanResultMap().put(benchmarkBeanResult.getStockCode() + monthString + yearsOfData, benchmarkBeanResult);
		}
//		}

	}


	public static void loadIndividualBenchmarkData(List<String> stockPool, int yearsOfData) {

		for (String stockCode : stockPool) {

			String dataFile = GlobalConfig.getDefaultIndividualStockProbPath() + stockCode + "-" + yearsOfData + "Y-" + "data.json";

			List<BenchmarkBeanResult> resultList = BenchmarkProbabilityAnalysisReportDataSourceManager.loadDataFromDisk(dataFile);

			if(resultList!=null)
				getIndividualStockBenchmarkBeanResultMap().put(stockCode + yearsOfData, resultList);
		}

	}

	public static BenchmarkBeanResult getMonthBenchmark(String codekey) {
		return getMonthBenchmarkBeanResultMap().get(codekey);
	}
	public static BenchmarkBeanResult getMonthBenchmark(String code, String monthString, int yearsOfData) {
		return getMonthBenchmarkBeanResultMap().get(code + monthString + yearsOfData);
	}


	public static List<BenchmarkBeanResult> getIndividualStockBenchmark(String code, int yearsOfData) {
		return getIndividualStockBenchmarkBeanResultMap().get(code + yearsOfData);
	}


	public static void main(String arg[]) {
		String txt = "700";
		String code = String.format("%05d", txt);
		System.out.println(code);
	}
}
