package com.skix.datapreprocess.faq.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FaqImportProperties.class)
public class FaqImportConfig {
}
