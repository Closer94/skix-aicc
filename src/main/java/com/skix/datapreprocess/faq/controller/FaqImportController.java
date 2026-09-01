package com.skix.datapreprocess.faq.controller;

import com.skix.datapreprocess.faq.dto.FaqImportResult;
import com.skix.datapreprocess.faq.service.FaqImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/faq")
public class FaqImportController {
    private final FaqImportService service;

    public FaqImportController(FaqImportService service) {
        this.service = service;
    }

    @PostMapping("/import")
    public ResponseEntity<FaqImportResult> importFaqs() {
        return ResponseEntity.ok(service.importFaqs());
    }
}
