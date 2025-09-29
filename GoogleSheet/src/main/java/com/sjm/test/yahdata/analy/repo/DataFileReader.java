package com.sjm.test.yahdata.analy.repo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.maas.util.DateHelper;
import com.maas.util.GeneralHelper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.sjm.test.yahdata.analy.bean.GapBean;
import com.sjm.test.yahdata.analy.bean.StockMetaBean;
import com.sjm.test.yahdata.analy.bean.raw.BaseStockBean;
import com.sjm.test.yahdata.analy.bean.raw.PriceSMABean;
import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.bean.raw.VolumeSMABean;
import com.sjm.test.yahdata.analy.bollinger.BollingerBand;
import com.sjm.test.yahdata.analy.bollinger.BollingerBandCalculator;
import com.sjm.test.yahdata.analy.conts.Const;
import com.sjm.test.yahdata.analy.conts.type.MovingAvgType;
import com.sjm.test.yahdata.analy.helper.CFGHelper;
import com.sjm.test.yahdata.analy.helper.DisplayHelper;
import com.sjm.test.yahdata.analy.ta.KHelper;
import com.sjm.test.yahdata.analy.ta.helper.MovingAvgHelper;
import com.sjm.test.yahdata.analy.ta.indicator.macd.MACDDataCaculator;
import com.sjm.test.yahdata.analy.ta.indicator.rsi.RSICalculator;
import com.sjm.test.yahdata.analy.ta.indicator.rsi.RSIMeta;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataFileReader {

	public static final String COMMA_DELIMITER = ",";

	private static BollingerBandCalculator bbCalc = new BollingerBandCalculator();
	private static MACDDataCaculator macdCalc = new MACDDataCaculator();

	public static final int NUM_OF_DAYS_IN_YEAR = 250;
	public static final int REQUIRED_NUM_OF_TX_DATA_IN_YEAR = 30;


	public DataFileReader() { }
	/*
	public void bulkCSVRead(String interval) throws FileNotFoundException, IOException{
		String PATH_NAME = GlobalConfig.getDefaultDownloadPath(interval);
		Collection<File> files = FileUtils.listFiles(new File(PATH_NAME), new String[]{"csv"}, false);

		List<StockBean> tmpList;
		for (File file : files) {
			List<StockBean> sList = this.readCSVFileToMemory(file);
			/////////////////////////
			String initialTxnDate = sList.get(0).getTxnDate();
			int startYear = Integer.parseInt(initialTxnDate.substring(0, 4));
			if(GlobalConfig.STOCK_START_YEAR>startYear)
				startYear = GlobalConfig.STOCK_START_YEAR;

			tmpList = StreamTransformHelper.subListByTxnYear(sList , startYear);

			////////////////////////
			String productCode = getFileNameWithoutExtension(file);
			StockMarketRepo.getProductCodeSet().add(productCode);
			StockMarketRepo.getTrunk().addAll(tmpList);

		}


		StringBuilder cmd = new StringBuilder("");
		StockMarketRepo.getProductCodeSet().forEach(productCode -> {
	        cmd.append(productCode).append(",");
	    });
	    cmd.deleteCharAt(cmd.length()-1);

	    System.out.println("================= Product Code list ===================");
	    System.out.println(cmd.toString());
	    System.out.println("=====================================================");


	}

	*/
	public List<StockBean> readCSVFileToMemory(String csvFilePath) throws FileNotFoundException, IOException {
		File csvFile = new File(csvFilePath);
		return readCSVFileToMemory(csvFile);
	}

	public List<StockBean> readCSVFileToMemory(File csvFile) throws FileNotFoundException, IOException {
		String stockCode = DisplayHelper.toFileName(csvFile);
//		String csvLastModifiedDateTime = csvFileFormatter.format(csvFile.lastModified());

		List<List<String>> records = new ArrayList<>();

		List<StockBean> trunkList = new ArrayList<StockBean>(500);
		StockBean prevStock = null;

		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
			String line;

			boolean isFirstLine = true;

			while ((line = br.readLine()) != null) {
				if(isFirstLine) {
					isFirstLine = false;
					continue;
				}

				String[] values = line.split(COMMA_DELIMITER);
				if("null".equalsIgnoreCase(values[1]))
					continue;

				records.add(Arrays.asList(values));
				StockBean curStockBean = converAryToStockObj(Arrays.asList(values), stockCode, prevStock);



				if(curStockBean!=null)
				{
					curStockBean.setLastModifiedDateTime(new Date(csvFile.lastModified()));

					String name = (CFGHelper.getStockProfileMap().get(stockCode)==null?Const.NA:CFGHelper.getStockProfileMap().get(stockCode).getName());
					curStockBean.setStockName(GeneralHelper.extractStockName(name));

					if(curStockBean.getVolume()<=0 && prevStock!=null)
						curStockBean.setVolume(prevStock.getVolume());

					double estTradeAmount = (curStockBean.getC()*curStockBean.getVolume())/1000000;
					curStockBean.setEstTradeAmount(estTradeAmount);



					//macd.calculateMACD(stockList);


					trunkList.add(curStockBean);


					int daysDiff = DateHelper.dayBetween(LocalDate.parse(curStockBean.getTxnDate()), LocalDate.now());
					if(daysDiff<NUM_OF_DAYS_IN_YEAR && trunkList.size()>=REQUIRED_NUM_OF_TX_DATA_IN_YEAR)
						this.calculateAndPutMovingAverageData(trunkList);

					//NOT Suggest, Take more more time to calculate the MACD,
//		        	if(daysDiff<=1 && trunkList.size()>=10)
//		        		this.calculateAndMacdData(trunkList);
				}

				prevStock = curStockBean;
			}
		}

		try {
			this.appendRSI(trunkList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			this.appendBB(trunkList);
		} catch (Exception e) {
			e.printStackTrace();
		}


		return trunkList;
	}




	protected StockBean converAryToStockObj(List<String> ls, String stockCode, StockBean prevStock) {
		StockBean bean = new StockBean(stockCode);
		try {
			bean.setTxnDate(ls.get(0));
			bean.setO(Double.parseDouble(ls.get(1)));
			bean.setH(Double.parseDouble(ls.get(2)));
			bean.setL(Double.parseDouble(ls.get(3)));
			bean.setC(Double.parseDouble(ls.get(4)));
//			bean.setAdjC(Double.parseDouble(ls.get(5)));
			bean.setBodyTop( Math.max(bean.getO(), bean.getC()));
			bean.setBodyBottom( Math.min(bean.getO(), bean.getC()));

			bean.setVolume(Double.parseDouble(ls.get(6)));

//			bean.setVolume(Double.parseDouble(ls.get(6)));
			bean.setTxnDateInt(GeneralHelper.txDateToIntNumber(bean.getTxnDate()));
			bean.setDayNameOfWeek( DateHelper.getDayNameOfWeek(bean.getTxnDate()));


			double dailyBodyChgPct = (bean.getBodyTop() - bean.getBodyBottom())/bean.getBodyBottom();
			bean.setBodyDailyChgPct(dailyBodyChgPct);


			double dailyHighestLowestPct = (bean.getH() - bean.getL())/bean.getL();
			bean.setHighestLowestPct(dailyHighestLowestPct);

			double dayPct = 0.0;
			double day2HPct = 0.0;
			double day2LPct = 0.0;

			boolean isUp = false;

			double dayVolChgPct = 0.0;

			if(prevStock!=null) {

				if(isSameStock(bean, prevStock))
					return null;

				isUp = bean.getC() > prevStock.getC();
				dayPct = (bean.getC()-prevStock.getC())/prevStock.getC();
				day2HPct = (bean.getH()-prevStock.getC())/prevStock.getC();
				day2LPct = (bean.getL()-prevStock.getC())/prevStock.getC();

				try {
					if(prevStock.getVolume()> 0) {
						dayVolChgPct = (double)bean.getVolume()/(double)prevStock.getVolume();
//						dayVolChgPct = (double)(bean.getVolume() -prevStock.getVolume())/(double)prevStock.getVolume();
					}
				}catch(Exception e) {
					log.error(null, e);
				}
			}else {
				isUp = bean.getC() > bean.getO();
				dayPct = (bean.getC()-bean.getO())/bean.getO();
				day2HPct = (bean.getH()-bean.getO())/bean.getO();
				day2LPct = (bean.getL()-bean.getO())/bean.getO();
			}



			boolean isOpenLowCloseHigh = bean.getC()> bean.getO();

//			bean.setUp(isUp);
			bean.setRiseToday(isUp);
			bean.setOpenLowCloseHigh(isOpenLowCloseHigh);
			bean.setDayChgPct(dayPct);
			bean.setDay2HChgPct(day2HPct);
			bean.setDay2LChgPct(day2LPct);
			bean.setDayVolumeChgPct(dayVolChgPct);

			double estTradeAmount = (bean.getC()*bean.getVolume())/1000000;
			bean.setEstTradeAmount(estTradeAmount);


			GapBean gapBean = KHelper.getGapBean(prevStock, bean);
			bean.setGapBean(gapBean);
		}catch(Exception e) {
//			System.err.println("ERROR : "+stockCode + ""+ls);
//			log.error("ERROR : "+stockCode + " "+e.getMessage());
			return null;
		}

		return bean;
	}


	private static boolean isSameStock(BaseStockBean sb1 , BaseStockBean sb2) {
		boolean b = sb1.getH() == sb2.getH() && sb1.getL() ==sb2.getL() && sb1.getO() == sb2.getO() && sb1.getC() ==sb2.getC() && sb1.getVolume() == sb2.getVolume();
		return b ;

	}
	private static String getFileNameWithoutExtension(File file) {
		String fileName = "";

		try {
			if (file != null && file.exists()) {
				String name = file.getName();
				fileName = name.replaceFirst("[.][^.]+$", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			fileName = "";
		}

		return fileName;

	}

	@Deprecated
	public List<StockMetaBean> readStockMetaData(String csvFilePath) throws FileNotFoundException, IOException, CsvValidationException {
//		File csvFile = new File(csvFilePath);
		List<StockMetaBean> trunkList = new ArrayList<StockMetaBean>(500);
		FileInputStream fis = new FileInputStream(csvFilePath);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		CSVReader reader = new CSVReader(isr);
		boolean isSkipRow = true;
		for (String[] row; (row = reader.readNext()) != null;) {
			if(isSkipRow) {
				isSkipRow = false;
				continue;
			}
			StockMetaBean b = converAryToStockMetaObj(Arrays.asList(row));

			if(b!=null)
				trunkList.add(b);
		}

		reader.close();
		isr.close();
		fis.close();

		return trunkList;
	}

	@Deprecated
	public List<StockMetaBean> readCNStockMetaData(String csvFilePath) throws FileNotFoundException, IOException, CsvValidationException, Exception {
//		File csvFile = new File(csvFilePath);

//    	File file = new File(csvFilePath);
//        byte[] bytes = Files.readAllBytes(file.toPath());
//
//        // Detect the file encoding
//        EncodingDetector encodingDetector = new DefaultEncodingDetector();
//        Metadata metadata = new Metadata();
//        metadata.add(Metadata.RESOURCE_NAME_KEY, file.getName());
//        Charset encoding = encodingDetector.detect(new ByteArrayInputStream(bytes), metadata);
//
//        String charsetName = encoding.name();
//
//        System.out.println("Detected file encoding: " + charsetName);



		List<StockMetaBean> trunkList = new ArrayList<StockMetaBean>(500);
		FileInputStream fis = new FileInputStream(csvFilePath);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		CSVReader reader = new CSVReader(isr);
		boolean isSkipRow = true;
		for (String[] row; (row = reader.readNext()) != null;) {
			if(isSkipRow) {
				isSkipRow = false;
				continue;
			}
			StockMetaBean bSS = converAryToStockMetaSSEAndSZSE(Arrays.asList(row));
			StockMetaBean bSZ = converAryToStockMetaSSEAndSZSE(Arrays.asList(row));
			if(bSS!=null) {
				bSS.setCode(bSS.getCode()+".SS");
				bSZ.setCode(bSS.getCode()+".SZ");
				trunkList.add(bSS);
				trunkList.add(bSZ);
			}
		}

		reader.close();
		isr.close();
		fis.close();

		return trunkList;
	}

	public void appendRSI(List<StockBean> stockList) throws Exception {
		List<RSIMeta> rsi9MetaList = RSICalculator.getInstance().calculateAll(stockList, 9);
		List<RSIMeta> rsi14MetaList = RSICalculator.getInstance().calculateAll(stockList, 14);

		int skIdx = stockList.size()-1;
		int rsi9MetaIdx = rsi9MetaList.size()-1;
		int rsi14MetaIdx = rsi14MetaList.size()-1;

		if(rsi14MetaIdx<1)
			return;

		do {
			double rsi9 = rsi9MetaList.get(rsi9MetaIdx).getRsiValue();

			double rsi14 = rsi14MetaList.get(rsi14MetaIdx).getRsiValue();
			stockList.get(skIdx).setRsi9(rsi9/100.0); // example, the original rsi9 value is 20.0, now convert to 0.2
			stockList.get(skIdx).setRsi14(rsi14/100.0);	// example, the original rsi9 value is 70.0, now convert to 0.7

			skIdx--;
			rsi9MetaIdx --;
			rsi14MetaIdx --;
		}while(rsi14MetaIdx>0);

	}

	public void appendBB(List<StockBean> stockList) throws Exception {
		List<BollingerBand> bollingerLists = bbCalc.func(stockList);
		int skIdx = stockList.size()-1;
		int bbIdx = bollingerLists.size()-1;
		if(bbIdx==-1) {
//    		log.debug("BollingerBand list is ZERO: "+ stockList.get(0).getStockCode());
			return;
		}
		do {
			BollingerBand bb = bollingerLists.get(bbIdx);

			stockList.get(skIdx).setBollingerBand(bb);

			skIdx--;
			bbIdx --;
		}while(bbIdx>0);
	}



	public void calculateAndPutMovingAverageData(List<StockBean> stockList)  {

		int size = stockList.size();

		StockBean last = stockList.get(size-1);

		double ma2Value = MovingAvgHelper.calculateSMA(stockList, 2, MovingAvgType.PRICE);
		double ma5Value = MovingAvgHelper.calculateSMA(stockList, 5, MovingAvgType.PRICE);
		double ma10Value = MovingAvgHelper.calculateSMA(stockList, 10, MovingAvgType.PRICE);
		double ma19Value = MovingAvgHelper.calculateSMA(stockList, 19, MovingAvgType.PRICE);
		double ma20Value = MovingAvgHelper.calculateSMA(stockList, 20, MovingAvgType.PRICE);
		double ma50Value = MovingAvgHelper.calculateSMA(stockList, 50, MovingAvgType.PRICE);
		double ma100Value = MovingAvgHelper.calculateSMA(stockList, 100, MovingAvgType.PRICE);
		double ma200Value = MovingAvgHelper.calculateSMA(stockList, 200, MovingAvgType.PRICE);
		double ma250Value = MovingAvgHelper.calculateSMA(stockList, 250, MovingAvgType.PRICE);

//    	last.setMa2(ma2Value);
//    	last.setMa5(ma5Value);
//    	last.setMa10(ma10Value);
//    	last.setMa19(ma19Value);
//    	last.setMa20(ma20Value);
//    	last.setMa50(ma50Value);
//    	last.setMa100(ma100Value);
//    	last.setMa200(ma200Value);
//    	last.setMa250(ma250Value);

		PriceSMABean priceMa = new PriceSMABean();
		priceMa.setMa2(ma2Value);
		priceMa.setMa5(ma5Value);
		priceMa.setMa10(ma10Value);
		priceMa.setMa19(ma19Value);
		priceMa.setMa20(ma20Value);
		priceMa.setMa50(ma50Value);
		priceMa.setMa100(ma100Value);
		priceMa.setMa200(ma200Value);
		priceMa.setMa250(ma250Value);
		last.setPriceSma(priceMa);

		double ma5Vol = MovingAvgHelper.calculateSMA(stockList, 5, MovingAvgType.VOLUME);
		double ma10Vol = MovingAvgHelper.calculateSMA(stockList, 10, MovingAvgType.VOLUME);
		double ma20Vol = MovingAvgHelper.calculateSMA(stockList, 20, MovingAvgType.VOLUME);
		double ma50Vol = MovingAvgHelper.calculateSMA(stockList, 50, MovingAvgType.VOLUME);
		double ma100Vol = MovingAvgHelper.calculateSMA(stockList, 100, MovingAvgType.VOLUME);

		VolumeSMABean volumeMa = new VolumeSMABean();
		volumeMa.setMa5(ma5Vol);
		volumeMa.setMa10(ma10Vol);
		volumeMa.setMa20(ma20Vol);
		volumeMa.setMa50(ma50Vol);
		volumeMa.setMa100(ma100Vol);
		last.setVolumeSma(volumeMa);

	}



	@Deprecated
	public void calculateAndMacdData(List<StockBean> stockList)  {

		int size = stockList.size();

		StockBean last = stockList.get(size-1);
//    	MACDData macd = new MACDData(-99,-99,-99);
//    	last.setMacdData(macd);
//    	last.setMacdData(macdCalc.calculateIndicators(stockList));
	}

	@Deprecated
	public StockMetaBean converAryToStockMetaSSEAndSZSE(List<String> ls) {
		StockMetaBean bean = new StockMetaBean();
		try {
			//數目	上交所股份編號	CCASS股份編號	名稱	面值 (人民幣)	類型
			bean.setCode(ls.get(1));
			bean.setName(ls.get(3));
//			bean.setMktCap(Double.parseDouble(ls.get(7)));
//			if("".equalsIgnoreCase(ls.get(8)))
			bean.setPe(0.0);
//			else
//				bean.setPe(Double.parseDouble(ls.get(8)));
//			bean.setSector(ls.get(10));


		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}

		return bean;
	}
	@Deprecated
	public StockMetaBean converAryToStockMetaObj(List<String> ls) {
		StockMetaBean bean = new StockMetaBean();
		try {
			// 商品代碼,描述,最新價,漲跌幅%,漲跌,評級,成交量,市值,市盈率(TTM),基本每股盈餘 (TTM),員工人數,部門
			// AAPL,Apple Inc.,146.06,-1.83480073,-2.73,賣出,129868346,2414396007885,29.13965649,5.1589,147000,電子科技
			bean.setCode(ls.get(0));
			bean.setName(ls.get(1));
			String mktCap = ls.get(7);
			if(mktCap==null || mktCap.isEmpty()) {
				bean.setMktCap(0.0);
			}else {
				bean.setMktCap(Double.parseDouble(mktCap));
			}

			if("".equalsIgnoreCase(ls.get(8)))
				bean.setPe(0.0);
			else
				bean.setPe(Double.parseDouble(ls.get(8)));
			bean.setSector(ls.get(10));


		}catch(Exception e) {
//			e.printStackTrace();
			log.error("load StockMeta data error: "+bean.getCode(), e);
			return null;
		}

		return bean;
	}

}
