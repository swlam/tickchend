package com.sjm.test.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {

    @Value("${version_number}")
    private String versionNumber;

    @Value("${build_date}")
    private String buildDate;

    public String getVersionNumber() {
        return versionNumber;
    }

    public String getBuildDate() {
        return buildDate;
    }
}