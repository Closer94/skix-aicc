package com.skix.datapreprocess.zendesk.controller;

import com.skix.datapreprocess.zendesk.dto.ArticleCollectionResult;
import com.skix.datapreprocess.zendesk.service.ArticleCollectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/zendesk/articles")
public class ZendeskArticleController {
    private final ArticleCollectionService service;

    public ZendeskArticleController(ArticleCollectionService service) {
        this.service = service;
    }

    @PostMapping("/collect")
    public ResponseEntity<ArticleCollectionResult> collect() {
        return ResponseEntity.ok(service.collect());
    }
}
