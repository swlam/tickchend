package com.sjm.test.demo.model;

import lombok.Data;

@Data
public class Staff {
    private String name;
    private int age;

    private double salary;
    private String dateOfBirth;
    
    public Staff(){}
    
    public Staff(String name, int age) {
    	this.name = name;
    	this.age = age;
    }
    
    public Staff(String name, int age, double salary, String dateOfBirth) {
    	this.name = name;
    	this.age = age;
    	this.salary = salary;
    	this.dateOfBirth = dateOfBirth;
    }
    // Getters and setters
}