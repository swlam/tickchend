package com.sjm.test.yahdata.analy.module.benchmarks.d2d;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sjm.test.yahdata.analy.probability.bean.BenchmarkBeanResult;

public class DateRangeOverlapAnalyst {

    public static void doAnalysis(List <BenchmarkBeanResult> dateRangeDataList) {
    	if(dateRangeDataList==null || dateRangeDataList.isEmpty()) {
    		System.out.println("EMPTY, SKIP");
    		return;
    	}
    		
        // Sort the list by start date
        Collections.sort(dateRangeDataList, (d1, d2) -> d1.getFakeRangeStartDate().compareTo(d2.getFakeRangeStartDate()));

        // Find overlapping date ranges
        List<BenchmarkDateRangeData> overlappingDateRanges = new ArrayList<>();
        BenchmarkBeanResult currentBenchmarkBeanResult = dateRangeDataList.get(0);
        
        BenchmarkDateRangeData currentRange = new BenchmarkDateRangeData(currentBenchmarkBeanResult.getFakeRangeStartDate(), currentBenchmarkBeanResult.getFakeRangeEndDate());
        
        for (int i = 1; i < dateRangeDataList.size(); i++) 
        {
        	BenchmarkDateRangeData nextRange = new BenchmarkDateRangeData(dateRangeDataList.get(i).getFakeRangeStartDate(),dateRangeDataList.get(i).getFakeRangeEndDate());
        	
            if (currentRange.getFakeRangeEndDate().compareTo(nextRange.getFakeRangeStartDate()) >= 0) {
                // The date ranges overlap
                LocalDate newStartDate = currentRange.getFakeRangeStartDate().compareTo(nextRange.getFakeRangeStartDate()) >= 0 ?
                        currentRange.getFakeRangeStartDate() : nextRange.getFakeRangeStartDate();
                LocalDate newEndDate = currentRange.getFakeRangeEndDate().compareTo(nextRange.getFakeRangeEndDate()) >= 0 ?
                        currentRange.getFakeRangeEndDate() : nextRange.getFakeRangeEndDate();
                currentRange = new BenchmarkDateRangeData(newStartDate, newEndDate);
            } else {
                // The date ranges do not overlap
                if (currentRange.getFakeRangeStartDate().compareTo(currentRange.getFakeRangeEndDate()) != 0) {
                    overlappingDateRanges.add(currentRange);
                }
                currentRange = nextRange;
            }
        }
        if (currentRange.getFakeRangeStartDate().compareTo(currentRange.getFakeRangeEndDate()) != 0) {
            overlappingDateRanges.add(currentRange);
        }

        // Print the overlapping date ranges
        for (BenchmarkDateRangeData dateRange : overlappingDateRanges) {
            System.out.println(dateRange);
        }

    }

}

class BenchmarkDateRangeData {

    private LocalDate startDate;
    private LocalDate endDate;

    public BenchmarkDateRangeData(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getFakeRangeStartDate() {
        return startDate;
    }

    public LocalDate getFakeRangeEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return "DateRange(" + startDate + ", " + endDate + ")";
    }

}