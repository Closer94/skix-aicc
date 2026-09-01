package com.skix.datapreprocess.faq.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FaqExcelReaderTest {
    @TempDir
    Path tempDirectory;

    @Test
    void readsFaqRowsUsingExpectedColumns() throws Exception {
        Path file = tempDirectory.resolve("서비스센터_많이하는질문_데이터 추출_20260723.xlsx");
        try (var workbook = new XSSFWorkbook(); var outputStream = Files.newOutputStream(file)) {
            var sheet = workbook.createSheet("Sheet1");
            var header = sheet.createRow(0);
            String[] headers = {"대분류", "중분류", "소분류", "제목", "본문", "MOB 본문", "사용여부"};
            for (int i = 0; i < headers.length; i++) header.createCell(i).setCellValue(headers[i]);
            var row = sheet.createRow(1);
            String[] values = {"많이 하는 질문", "정수기", "정수기", "제목", "<p>본문</p>", "<p>모바일</p>", "Y"};
            for (int i = 0; i < values.length; i++) row.createCell(i).setCellValue(values[i]);
            workbook.write(outputStream);
        }

        var rows = new FaqExcelReader(new FaqBodyTextProcessor()).read(file);

        assertEquals(1, rows.size());
        assertEquals("정수기", rows.get(0).middleCategory());
        assertEquals("<p>본문</p>", rows.get(0).body());
        assertEquals("본문", rows.get(0).processedText());
        assertEquals(2, rows.get(0).sourceRowNo());
    }

    @Test
    void replacesEmptyCategoriesWithNotAvailable() throws Exception {
        Path file = tempDirectory.resolve("faq-empty-category.xlsx");
        try (var workbook = new XSSFWorkbook(); var outputStream = Files.newOutputStream(file)) {
            var sheet = workbook.createSheet("Sheet1");
            var header = sheet.createRow(0);
            String[] headers = {"대분류", "중분류", "소분류", "제목", "본문", "MOB 본문", "사용여부"};
            for (int i = 0; i < headers.length; i++) header.createCell(i).setCellValue(headers[i]);
            var row = sheet.createRow(1);
            row.createCell(3).setCellValue("제목");
            row.createCell(6).setCellValue("Y");
            workbook.write(outputStream);
        }

        var row = new FaqExcelReader(new FaqBodyTextProcessor()).read(file).get(0);

        assertEquals("N/A", row.largeCategory());
        assertEquals("N/A", row.middleCategory());
        assertEquals("N/A", row.smallCategory());
        assertEquals("N/A", row.processedText());
    }
}
