package com.sjm.test.yahdata.analy.repo;

import java.util.ArrayList;
import java.util.List;

import com.maas.util.GeneralHelper;

import lombok.Data;

 @Data
 public class DownloadResult {
	private int passCount = 0;
	private List<String> failureList = null;
	private List<String> okList = null;
	
	public DownloadResult() {
		failureList = new ArrayList<String>(0);
	}
	public String toString() {
		double passRatio = (double) (passCount / (double)(passCount + failureList.size()));
		
		return String.format("Pass (%d), Fail (%d). Download-OK-ratio: %s \t FailureList: %s", passCount, failureList.size(), GeneralHelper.toPct(passRatio), failureList);

	}
}