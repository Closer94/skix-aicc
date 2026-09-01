package com.skix.datapreprocess.faq.dto;

public record FaqImportRow(
        String largeCategory,
        String middleCategory,
        String smallCategory,
        String title,
        String body,
        String processedText,
        String mobileBody,
        String useYn,
        String sourceFileName,
        int sourceRowNo) {
}
