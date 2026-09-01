package com.skix.datapreprocess.zendesk.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zendesk")
public record ZendeskProperties(String baseUrl, String username, String apiToken, int perPage) {
    public ZendeskProperties {
        if (perPage <= 0 || perPage > 100) {
            perPage = 50;
        }
    }
}
