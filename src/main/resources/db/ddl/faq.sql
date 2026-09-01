CREATE TABLE `faq` (
    `faq_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'FAQ 내부 식별자',
    `large_category` varchar(100) NOT NULL COMMENT '대분류',
    `middle_category` varchar(100) NOT NULL COMMENT '중분류',
    `small_category` varchar(100) NOT NULL COMMENT '소분류',
    `title` varchar(500) NOT NULL COMMENT 'FAQ 제목',
    `body` longtext DEFAULT NULL COMMENT 'PC 본문 HTML',
    `processed_text` longtext DEFAULT NULL COMMENT 'PC 본문 HTML 태그 제거 텍스트',
    `mobile_body` longtext DEFAULT NULL COMMENT '모바일 본문 HTML',
    `use_yn` char(1) NOT NULL DEFAULT 'Y' COMMENT '사용 여부(Y/N)',
    `source_file_name` varchar(255) NOT NULL COMMENT '적재 원본 파일명',
    `source_row_no` int(11) NOT NULL COMMENT '원본 Excel 행 번호(헤더 포함)',
    `created_at` datetime NOT NULL DEFAULT current_timestamp() COMMENT '최초 적재 일시',
    `updated_at` datetime NOT NULL DEFAULT current_timestamp()
        ON UPDATE current_timestamp() COMMENT '최종 수정 일시',
    PRIMARY KEY (`faq_id`),
    UNIQUE KEY `uk_faq_source_row` (`source_file_name`, `source_row_no`),
    KEY `idx_faq_category` (`large_category`, `middle_category`, `small_category`),
    KEY `idx_faq_use_yn` (`use_yn`),
    KEY `idx_faq_title` (`title`),
    CONSTRAINT `chk_faq_use_yn` CHECK (`use_yn` IN ('Y', 'N'))
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='서비스센터 많이 하는 질문(FAQ)';
