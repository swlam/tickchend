package com.sjm.test.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sjm.test.demo.config.AppProperties;

@RestController
@RequestMapping("/app-properties")
public class AppPropertiesController {

    private final AppProperties appProperties;

    @Autowired
    public AppPropertiesController(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @GetMapping
    public AppProperties getAppProperties() {
        return appProperties;
    }
}