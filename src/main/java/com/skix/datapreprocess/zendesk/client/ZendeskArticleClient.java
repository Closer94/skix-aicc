package com.skix.datapreprocess.zendesk.client;

import com.skix.datapreprocess.zendesk.config.ZendeskProperties;
import com.skix.datapreprocess.zendesk.dto.ZendeskArticlePage;
import org.springframework.web.client.RestClient;

public class ZendeskArticleClient {
    private final RestClient restClient;
    private final ZendeskProperties properties;

    public ZendeskArticleClient(RestClient restClient, ZendeskProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    public ZendeskArticlePage getArticles(long sectionId, int page) {
        ZendeskArticlePage response = restClient.get()
                .uri(uri -> uri.path("/api/v2/help_center/sections/{sectionId}/articles.json")
                        .queryParam("page", page)
                        .queryParam("per_page", properties.perPage())
                        .build(sectionId))
                .retrieve()
                .body(ZendeskArticlePage.class);
        if (response == null) {
            throw new IllegalStateException("Zendesk returned an empty response for section " + sectionId);
        }
        return response;
    }
}
