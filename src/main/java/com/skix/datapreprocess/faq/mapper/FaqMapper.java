package com.skix.datapreprocess.faq.mapper;

import com.skix.datapreprocess.faq.dto.FaqImportRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FaqMapper {
    int upsertFaqs(@Param("faqs") List<FaqImportRow> faqs);
}
