package com.skix.datapreprocess.zendesk.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ArticleTextPreprocessorTest {
    private final ArticleTextPreprocessor preprocessor = new ArticleTextPreprocessor();

    @Test
    void removesHtmlAndConfidentialBlock() {
        String body = "<p>공개 문장</p><strong>**대외비**</strong><p>삭제할 문장</p>"
                + "<strong>**대외비**</strong><p>마지막 문장&nbsp;입니다.</p>";

        assertEquals("공개 문장 마지막 문장 입니다.", preprocessor.preprocess(body));
    }

    @Test
    void returnsNotAvailableWhenWholeBodyIsConfidential() {
        String body = "<p><strong>**대외비**</strong></p><p>전체 비공개 내용</p>"
                + "<p><strong>**대외비**</strong></p>";

        assertEquals("N/A", preprocessor.preprocess(body));
    }

    @Test
    void returnsNotAvailableWhenBodyIsEmpty() {
        assertEquals("N/A", preprocessor.preprocess(null));
        assertEquals("N/A", preprocessor.preprocess("<p>&nbsp;</p>"));
    }
}
