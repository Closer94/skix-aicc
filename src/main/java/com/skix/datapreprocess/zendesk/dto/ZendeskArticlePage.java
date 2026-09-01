package com.skix.datapreprocess.zendesk.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ZendeskArticlePage(
        int count,
        @JsonProperty("next_page") String nextPage,
        int page,
        @JsonProperty("page_count") int pageCount,
        List<ZendeskArticle> articles) {
}
