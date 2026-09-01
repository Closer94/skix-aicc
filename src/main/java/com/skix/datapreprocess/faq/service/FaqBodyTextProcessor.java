package com.skix.datapreprocess.faq.service;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class FaqBodyTextProcessor {
    private static final String NOT_AVAILABLE = "N/A";
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    public String process(String body) {
        if (body == null || body.isBlank()) {
            return NOT_AVAILABLE;
        }

        String plainText = Jsoup.parseBodyFragment(body).text().replace('\u00A0', ' ');
        String normalizedText = WHITESPACE.matcher(plainText).replaceAll(" ").trim();
        return normalizedText.isEmpty() ? NOT_AVAILABLE : normalizedText;
    }
}
