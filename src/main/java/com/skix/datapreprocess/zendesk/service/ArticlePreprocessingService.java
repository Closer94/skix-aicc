package com.skix.datapreprocess.zendesk.service;

import com.skix.datapreprocess.zendesk.dto.ArticleBodyDto;
import com.skix.datapreprocess.zendesk.dto.ArticlePreprocessingResult;
import com.skix.datapreprocess.zendesk.dto.ProcessedArticleDto;
import com.skix.datapreprocess.zendesk.mapper.ArticleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticlePreprocessingService {
    private static final Logger log = LoggerFactory.getLogger(ArticlePreprocessingService.class);
    private static final int BATCH_SIZE = 100;

    private final ArticleMapper articleMapper;
    private final ArticleTextPreprocessor preprocessor;

    public ArticlePreprocessingService(ArticleMapper articleMapper, ArticleTextPreprocessor preprocessor) {
        this.articleMapper = articleMapper;
        this.preprocessor = preprocessor;
    }

    public ArticlePreprocessingResult processArticles(boolean reprocess) {
        long startedAt = System.nanoTime();
        int processedCount = 0;
        int emptyTextCount = 0;
        int batchNumber = 0;
        long lastArticleId = 0;
        log.info("Article text preprocessing started: batchSize={}, reprocess={}", BATCH_SIZE, reprocess);

        while (true) {
            List<ArticleBodyDto> articleBodies = articleMapper.selectArticleBodies(lastArticleId, BATCH_SIZE, reprocess);
            if (articleBodies.isEmpty()) {
                break;
            }
            lastArticleId = articleBodies.get(articleBodies.size() - 1).articleId();

            batchNumber++;
            List<ProcessedArticleDto> processedArticles = articleBodies.stream()
                    .map(article -> new ProcessedArticleDto(
                            article.articleId(), preprocessor.preprocess(article.body())))
                    .toList();
            articleMapper.updateProcessedTexts(processedArticles);

            int batchEmptyCount = (int) processedArticles.stream()
                    .filter(article -> ArticleTextPreprocessor.NOT_AVAILABLE.equals(article.processedText()))
                    .count();
            processedCount += processedArticles.size();
            emptyTextCount += batchEmptyCount;
            log.info("Article text preprocessing batch completed: batch={}, processed={}, emptyText={}, totalProcessed={}, lastArticleId={}",
                    batchNumber, processedArticles.size(), batchEmptyCount, processedCount, lastArticleId);
        }

        log.info("Article text preprocessing completed: batches={}, processed={}, emptyText={}, elapsedMs={}",
                batchNumber, processedCount, emptyTextCount, (System.nanoTime() - startedAt) / 1_000_000);
        return new ArticlePreprocessingResult(processedCount, emptyTextCount);
    }
}
