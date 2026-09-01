package com.skix.datapreprocess.zendesk.service;

import com.skix.datapreprocess.zendesk.client.ZendeskArticleClient;
import com.skix.datapreprocess.zendesk.dto.ArticleCollectionResult;
import com.skix.datapreprocess.zendesk.dto.CategorySection;
import com.skix.datapreprocess.zendesk.dto.ZendeskArticle;
import com.skix.datapreprocess.zendesk.dto.ZendeskArticlePage;
import com.skix.datapreprocess.zendesk.mapper.ArticleMapper;
import com.skix.datapreprocess.zendesk.mapper.ArticlePersistenceConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleCollectionService {
    private static final Logger log = LoggerFactory.getLogger(ArticleCollectionService.class);

    private final ArticleMapper articleMapper;
    private final ArticlePersistenceConverter converter;
    private final ZendeskArticleClient client;

    public ArticleCollectionService(ArticleMapper articleMapper, ArticlePersistenceConverter converter,
                                    ZendeskArticleClient client) {
        this.articleMapper = articleMapper;
        this.converter = converter;
        this.client = client;
    }

    public ArticleCollectionResult collect() {
        long collectionStartedAt = System.nanoTime();
        List<CategorySection> activeSections = articleMapper.selectActiveSections();
        Map<Long, List<CategorySection>> sectionsByCategory = activeSections.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        CategorySection::categoryId, LinkedHashMap::new, java.util.stream.Collectors.toList()));

        log.info("Zendesk article collection started: categories={}, sections={}",
                sectionsByCategory.size(), activeSections.size());

        int sections = 0;
        int pages = 0;
        int articles = 0;
        int categoryIndex = 0;
        for (List<CategorySection> categorySections : sectionsByCategory.values()) {
            categoryIndex++;
            CategorySection category = categorySections.get(0);
            int categoryArticles = 0;
            long categoryStartedAt = System.nanoTime();
            log.info("Category collection started: category={}/{}, categoryId={}, categoryName={}, sections={}",
                    categoryIndex, sectionsByCategory.size(), category.categoryId(), category.categoryName(),
                    categorySections.size());

            int sectionIndex = 0;
            for (CategorySection section : categorySections) {
                sectionIndex++;
                sections++;
                int sectionPages = 0;
                int sectionArticles = 0;
                int pageNumber = 1;
                long sectionStartedAt = System.nanoTime();
                log.info("Section collection started: categoryId={}, section={}/{}, sectionId={}, sectionName={}",
                        section.categoryId(), sectionIndex, categorySections.size(), section.sectionId(),
                        section.sectionName());

                while (true) {
                    try {
                        ZendeskArticlePage page = client.getArticles(section.sectionId(), pageNumber);
                        List<ZendeskArticle> pageArticles = page.articles() == null ? List.of() : page.articles();
                        if (!pageArticles.isEmpty()) {
                            articleMapper.upsertArticles(pageArticles.stream()
                                    .map(article -> converter.convert(section, article))
                                    .toList());
                        }
                        sectionPages++;
                        sectionArticles += pageArticles.size();
                        pages++;
                        articles += pageArticles.size();
                        log.info("Section page collected: categoryId={}, sectionId={}, page={}/{}, pageArticles={}, sectionArticles={}",
                                section.categoryId(), section.sectionId(), pageNumber, page.pageCount(),
                                pageArticles.size(), sectionArticles);
                        if (page.nextPage() == null || page.nextPage().isBlank()) {
                            break;
                        }
                        pageNumber++;
                    } catch (RuntimeException exception) {
                        log.error("Section collection failed: categoryId={}, categoryName={}, sectionId={}, sectionName={}, page={}",
                                section.categoryId(), section.categoryName(), section.sectionId(), section.sectionName(),
                                pageNumber, exception);
                        throw exception;
                    }
                }
                categoryArticles += sectionArticles;
                log.info("Section collection completed: categoryId={}, sectionId={}, pages={}, articles={}, elapsedMs={}",
                        section.categoryId(), section.sectionId(), sectionPages, sectionArticles,
                        elapsedMillis(sectionStartedAt));
            }
            log.info("Category collection completed: category={}/{}, categoryId={}, categoryName={}, sections={}, articles={}, elapsedMs={}",
                    categoryIndex, sectionsByCategory.size(), category.categoryId(), category.categoryName(),
                    categorySections.size(), categoryArticles, elapsedMillis(categoryStartedAt));
        }
        log.info("Zendesk article collection completed: categories={}, sections={}, pages={}, articles={}, elapsedMs={}",
                sectionsByCategory.size(), sections, pages, articles, elapsedMillis(collectionStartedAt));
        return new ArticleCollectionResult(sections, pages, articles);
    }

    private static long elapsedMillis(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }
}
