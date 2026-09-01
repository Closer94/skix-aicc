package com.skix.datapreprocess.zendesk.service;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ArticleTextPreprocessor {
    public static final String NOT_AVAILABLE = "N/A";
    private static final String CONFIDENTIAL_MARKER = "**대외비**";
    private static final Pattern CONFIDENTIAL_BLOCK = Pattern.compile(
            Pattern.quote(CONFIDENTIAL_MARKER) + ".*?" + Pattern.quote(CONFIDENTIAL_MARKER),
            Pattern.DOTALL);
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    public String preprocess(String body) {
        if (body == null || body.isBlank()) {
            return NOT_AVAILABLE;
        }

        String plainText = Jsoup.parseBodyFragment(body).text();
        String withoutConfidentialBlocks = CONFIDENTIAL_BLOCK.matcher(plainText).replaceAll(" ");
        String processedText = WHITESPACE.matcher(withoutConfidentialBlocks.replace('\u00A0', ' '))
                .replaceAll(" ")
                .trim();
        return processedText.isEmpty() ? NOT_AVAILABLE : processedText;
    }
}
