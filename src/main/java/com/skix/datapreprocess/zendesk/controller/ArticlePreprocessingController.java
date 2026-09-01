package com.skix.datapreprocess.zendesk.controller;

import com.skix.datapreprocess.zendesk.dto.ArticlePreprocessingResult;
import com.skix.datapreprocess.zendesk.service.ArticlePreprocessingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/zendesk/articles")
public class ArticlePreprocessingController {
    private final ArticlePreprocessingService service;

    public ArticlePreprocessingController(ArticlePreprocessingService service) {
        this.service = service;
    }

    @PostMapping("/process")
    public ResponseEntity<ArticlePreprocessingResult> process(
            @RequestParam(defaultValue = "false") boolean reprocess) {
        return ResponseEntity.ok(service.processArticles(reprocess));
    }
}
