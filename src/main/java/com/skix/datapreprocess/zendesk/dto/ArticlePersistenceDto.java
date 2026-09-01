package com.skix.datapreprocess.zendesk.dto;

import java.time.Instant;

public record ArticlePersistenceDto(
        long articleId,
        long categoryId,
        String categoryName,
        long sectionId,
        String sectionName,
        String title,
        String body,
        String url,
        String htmlUrl,
        Long authorId,
        String locale,
        String sourceLocale,
        boolean draft,
        boolean promoted,
        Integer position,
        String labelNames,
        String contentTagIds,
        Long userSegmentId,
        String userSegmentIds,
        Long permissionGroupId,
        boolean commentsDisabled,
        int voteCount,
        int voteSum,
        Instant createdAt,
        Instant updatedAt,
        Instant editedAt,
        boolean outdated,
        String outdatedLocales) {
}
