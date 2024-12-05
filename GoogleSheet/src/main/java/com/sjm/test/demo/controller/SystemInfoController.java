package com.sjm.test.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sjm.test.yahdata.analy.conts.Const;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/system-info")
public class SystemInfoController {


	@GetMapping
	public String getDefauleSystemInfo() {
		String systemInfo = "System Info: ..."; // Replace with actual system information
		System.out.println("getDefauleSystemInfo()");
		return systemInfo;
	}

	//    http://localhost:8080/system-info/report?market=US&reportName=MONTHLY_REPORT
	@GetMapping("/report")
	public String getCustomSystemInfo(@RequestParam(required = false) String market,
									  @RequestParam(required = false) String reportName) {
		// Your logic to get system information
		String systemInfo = "System Info: ..."; // Replace with actual system information

		String COUNTRY_MARKET = Const.MARKET_US;
		String DEFAULT_INTERVAL = Const.INTERVAL_D;

		if (market != null) {
			systemInfo += "\nReceived parameter: " + market;
		}


		switch (market) {
			case Const.MARKET_CN:
				System.out.println("ZONE_CN:" +market);
				COUNTRY_MARKET =market;
				break;
			case Const.MARKET_US:
				System.out.println("ZONE_US:" +market);
				COUNTRY_MARKET =market;
				break;
			default:
				COUNTRY_MARKET = Const.MARKET_HK;
				System.out.println("ZONE_HK:" +market);
		}




		return systemInfo;
	}
}