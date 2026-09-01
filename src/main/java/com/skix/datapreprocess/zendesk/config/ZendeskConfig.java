package com.skix.datapreprocess.zendesk.config;

import com.skix.datapreprocess.zendesk.client.ZendeskArticleClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(ZendeskProperties.class)
public class ZendeskConfig {

    @Bean
    ZendeskArticleClient zendeskArticleClient(ZendeskProperties properties) {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        if (StringUtils.hasText(properties.username()) && StringUtils.hasText(properties.apiToken())) {
            String basicAuthUsername = properties.username().endsWith("/token")
                    ? properties.username()
                    : properties.username() + "/token";
            builder.defaultHeaders(headers -> headers.setBasicAuth(
                    basicAuthUsername, properties.apiToken()));
        }
        return new ZendeskArticleClient(builder.build(), properties);
    }
}
