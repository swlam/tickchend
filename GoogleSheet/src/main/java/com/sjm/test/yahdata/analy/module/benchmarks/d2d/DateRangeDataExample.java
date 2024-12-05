package com.sjm.test.yahdata.analy.module.benchmarks.d2d;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DateRangeDataExample {

    public static void main(String[] args) {

        List<DateRangeData> dateRangeDataList = new ArrayList<>();
        dateRangeDataList.add(new DateRangeData(LocalDate.of(2020, 6, 2), LocalDate.of(2020, 6, 7)));
        dateRangeDataList.add(new DateRangeData(LocalDate.of(2020, 6, 3), LocalDate.of(2020, 6, 6)));
        dateRangeDataList.add(new DateRangeData(LocalDate.of(2020, 6, 4), LocalDate.of(2020, 6, 8)));
        dateRangeDataList.add(new DateRangeData(LocalDate.of(2020, 6, 2), LocalDate.of(2020, 6, 9)));
        dateRangeDataList.add(new DateRangeData(LocalDate.of(2020, 6, 6), LocalDate.of(2020, 6, 15)));
        dateRangeDataList.add(new DateRangeData(LocalDate.of(2020, 6, 6), LocalDate.of(2020, 6, 12)));

        // Sort the list by start date
        Collections.sort(dateRangeDataList, (d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));

        // Find overlapping date ranges
        List<DateRangeData> overlappingDateRanges = new ArrayList<>();
        DateRangeData currentRange = dateRangeDataList.get(0);
        for (int i = 1; i < dateRangeDataList.size(); i++) {
            DateRangeData nextRange = dateRangeDataList.get(i);
            if (currentRange.getEndDate().compareTo(nextRange.getStartDate()) >= 0) {
                // The date ranges overlap
                LocalDate newStartDate = currentRange.getStartDate().compareTo(nextRange.getStartDate()) >= 0 ?
                        currentRange.getStartDate() : nextRange.getStartDate();
                LocalDate newEndDate = currentRange.getEndDate().compareTo(nextRange.getEndDate()) >= 0 ?
                        currentRange.getEndDate() : nextRange.getEndDate();
                currentRange = new DateRangeData(newStartDate, newEndDate);
            } else {
                // The date ranges do not overlap
                if (currentRange.getStartDate().compareTo(currentRange.getEndDate()) != 0) {
                    overlappingDateRanges.add(currentRange);
                }
                currentRange = nextRange;
            }
        }
        if (currentRange.getStartDate().compareTo(currentRange.getEndDate()) != 0) {
            overlappingDateRanges.add(currentRange);
        }

        // Print the overlapping date ranges
        for (DateRangeData dateRange : overlappingDateRanges) {
            System.out.println(dateRange);
        }

    }

}

class DateRangeData {

    private LocalDate startDate;
    private LocalDate endDate;

    public DateRangeData(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return "DateRangeData(" + startDate + ", " + endDate + ")";
    }

}