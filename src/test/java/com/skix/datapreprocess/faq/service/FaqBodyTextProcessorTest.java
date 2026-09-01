package com.skix.datapreprocess.faq.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FaqBodyTextProcessorTest {
    private final FaqBodyTextProcessor processor = new FaqBodyTextProcessor();

    @Test
    void removesHtmlTagsAndDecodesEntities() {
        String body = "<p><strong>▶연속취수호스란?</strong><br>&nbsp;&bull; 호스입니다.</p>";

        assertEquals("▶연속취수호스란? • 호스입니다.", processor.process(body));
    }

    @Test
    void returnsNotAvailableWhenExtractedTextIsEmpty() {
        assertEquals("N/A", processor.process("<p>&nbsp;</p><img src=\"image.png\">"));
        assertEquals("N/A", processor.process(null));
    }
}
