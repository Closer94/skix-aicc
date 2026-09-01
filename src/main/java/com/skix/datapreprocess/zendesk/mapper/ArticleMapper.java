package com.skix.datapreprocess.zendesk.mapper;

import com.skix.datapreprocess.zendesk.dto.ArticlePersistenceDto;
import com.skix.datapreprocess.zendesk.dto.ArticleBodyDto;
import com.skix.datapreprocess.zendesk.dto.CategorySection;
import com.skix.datapreprocess.zendesk.dto.ProcessedArticleDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper {
    List<CategorySection> selectActiveSections();

    int upsertArticles(@Param("articles") List<ArticlePersistenceDto> articles);

    List<ArticleBodyDto> selectArticleBodies(@Param("afterArticleId") long afterArticleId,
                                             @Param("limit") int limit,
                                             @Param("reprocess") boolean reprocess);

    int updateProcessedTexts(@Param("articles") List<ProcessedArticleDto> articles);
}
