package com.skix.datapreprocess.zendesk.mapper;

import com.skix.datapreprocess.zendesk.dto.ArticlePersistenceDto;
import com.skix.datapreprocess.zendesk.dto.CategorySection;
import com.skix.datapreprocess.zendesk.dto.ZendeskArticle;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
public class ArticlePersistenceConverter {
    private final ObjectMapper objectMapper;

    public ArticlePersistenceConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ArticlePersistenceDto convert(CategorySection section, ZendeskArticle article) {
        return new ArticlePersistenceDto(
                article.id(), section.categoryId(), section.categoryName(), article.sectionId(), section.sectionName(),
                article.title(), article.body(), article.url(), article.htmlUrl(), article.authorId(), article.locale(),
                article.sourceLocale(), article.draft(), article.promoted(), article.position(), json(article.labelNames()),
                json(article.contentTagIds()), article.userSegmentId(), json(article.userSegmentIds()),
                article.permissionGroupId(), article.commentsDisabled(), article.voteCount(), article.voteSum(),
                article.createdAt(), article.updatedAt(), article.editedAt(), article.outdated(), json(article.outdatedLocales()));
    }

    private String json(Object value) {
        try {
            return objectMapper.writeValueAsString(value == null ? List.of() : value);
        } catch (tools.jackson.core.JacksonException e) {
            throw new IllegalArgumentException("Article JSON column serialization failed", e);
        }
    }
}
