package com.skix.datapreprocess.faq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "faq")
public record FaqImportProperties(String importDirectory, String filePattern, int batchSize) {
    public FaqImportProperties {
        if (importDirectory == null || importDirectory.isBlank()) {
            importDirectory = "./data/faq";
        }
        if (filePattern == null || filePattern.isBlank()) {
            filePattern = "서비스센터*많이하는질문*데이터 추출_20260723.xlsx";
        }
        if (batchSize <= 0 || batchSize > 200) {
            batchSize = 50;
        }
    }
}
