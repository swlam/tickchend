package com.sjm.test.demo.controller;



import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sjm.test.demo.model.Staff;

@RestController
@RequestMapping("/staff")
public class StaffController {

    @GetMapping
    public List<Staff> getAllStaff() {
        // Your logic to fetch staff data
        List<Staff> staffList = new ArrayList<>();
        // Add staff records to the list
        staffList.add(new Staff("NameA",20,10000.05,"1980-01-01"));
        staffList.add(new Staff("NameB",25,20000.55,"1990-01-01"));
        staffList.add(new Staff("NameC",30,30000.05,"1991-01-01"));
        return staffList;
    }
}