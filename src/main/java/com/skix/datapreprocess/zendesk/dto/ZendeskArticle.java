package com.skix.datapreprocess.zendesk.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ZendeskArticle(
        long id,
        String url,
        @JsonProperty("html_url") String htmlUrl,
        @JsonProperty("author_id") Long authorId,
        @JsonProperty("comments_disabled") boolean commentsDisabled,
        boolean draft,
        boolean promoted,
        Integer position,
        @JsonProperty("vote_sum") int voteSum,
        @JsonProperty("vote_count") int voteCount,
        @JsonProperty("section_id") long sectionId,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,
        String title,
        String body,
        @JsonProperty("source_locale") String sourceLocale,
        String locale,
        boolean outdated,
        @JsonProperty("outdated_locales") List<String> outdatedLocales,
        @JsonProperty("edited_at") Instant editedAt,
        @JsonProperty("user_segment_id") Long userSegmentId,
        @JsonProperty("user_segment_ids") List<Long> userSegmentIds,
        @JsonProperty("permission_group_id") Long permissionGroupId,
        @JsonProperty("content_tag_ids") List<String> contentTagIds,
        @JsonProperty("label_names") List<String> labelNames) {
}
