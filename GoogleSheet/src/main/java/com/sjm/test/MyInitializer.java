package com.sjm.test;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MyInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        // Your initialization logic here
        System.out.println("Application initialization logic executed!");
    }
}