package com.skix.datapreprocess.zendesk.service;

import com.skix.datapreprocess.zendesk.client.ZendeskArticleClient;
import com.skix.datapreprocess.zendesk.dto.ArticlePersistenceDto;
import com.skix.datapreprocess.zendesk.dto.CategorySection;
import com.skix.datapreprocess.zendesk.dto.ZendeskArticle;
import com.skix.datapreprocess.zendesk.dto.ZendeskArticlePage;
import com.skix.datapreprocess.zendesk.mapper.ArticleMapper;
import com.skix.datapreprocess.zendesk.mapper.ArticlePersistenceConverter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ArticleCollectionServiceTest {
    @Test
    void collectsEveryPageForEveryActiveSection() {
        ArticleMapper articleMapper = mock(ArticleMapper.class);
        ArticlePersistenceConverter converter = mock(ArticlePersistenceConverter.class);
        ZendeskArticleClient client = mock(ZendeskArticleClient.class);
        CategorySection section = new CategorySection(10L, "category", 20L, "section");
        ZendeskArticle article = mock(ZendeskArticle.class);
        ArticlePersistenceDto persistenceDto = mock(ArticlePersistenceDto.class);
        when(articleMapper.selectActiveSections()).thenReturn(List.of(section));
        when(converter.convert(section, article)).thenReturn(persistenceDto);
        when(client.getArticles(20L, 1))
                .thenReturn(new ZendeskArticlePage(2, "next", 1, 2, List.of(article)));
        when(client.getArticles(20L, 2))
                .thenReturn(new ZendeskArticlePage(2, null, 2, 2, List.of(article)));

        var result = new ArticleCollectionService(articleMapper, converter, client).collect();

        assertEquals(1, result.sectionCount());
        assertEquals(2, result.pageCount());
        assertEquals(2, result.articleCount());
        verify(articleMapper, times(2)).upsertArticles(List.of(persistenceDto));
    }
}
