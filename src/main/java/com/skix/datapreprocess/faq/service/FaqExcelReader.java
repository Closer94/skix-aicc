package com.skix.datapreprocess.faq.service;

import com.skix.datapreprocess.faq.dto.FaqImportRow;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class FaqExcelReader {
    private static final Logger log = LoggerFactory.getLogger(FaqExcelReader.class);
    private static final String NOT_AVAILABLE = "N/A";
    private static final List<String> EXPECTED_HEADERS =
            List.of("대분류", "중분류", "소분류", "제목", "본문", "MOB 본문", "사용여부");

    private final FaqBodyTextProcessor bodyTextProcessor;

    public FaqExcelReader(FaqBodyTextProcessor bodyTextProcessor) {
        this.bodyTextProcessor = bodyTextProcessor;
    }

    public List<FaqImportRow> read(Path excelFile) {
        try (InputStream inputStream = Files.newInputStream(excelFile);
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            if (workbook.getNumberOfSheets() == 0) {
                throw new IllegalArgumentException("Excel workbook has no sheets: " + excelFile);
            }
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter(Locale.KOREA);
            validateHeaders(sheet.getRow(0), formatter, excelFile);

            List<FaqImportRow> result = new ArrayList<>();
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null || isEmpty(row, formatter)) {
                    continue;
                }
                int sourceRowNo = rowIndex + 1;
                String useYn = required(cell(row, 6, formatter), "사용여부", sourceRowNo)
                        .toUpperCase(Locale.ROOT);
                if (!useYn.equals("Y") && !useYn.equals("N")) {
                    throw new IllegalArgumentException("사용여부 must be Y or N at Excel row " + sourceRowNo);
                }
                String body = nullable(cell(row, 4, formatter));
                result.add(new FaqImportRow(
                        categoryValue(cell(row, 0, formatter), 0, sourceRowNo),
                        categoryValue(cell(row, 1, formatter), 1, sourceRowNo),
                        categoryValue(cell(row, 2, formatter), 2, sourceRowNo),
                        required(cell(row, 3, formatter), "제목", sourceRowNo),
                        body,
                        bodyTextProcessor.process(body),
                        nullable(cell(row, 5, formatter)),
                        useYn,
                        excelFile.getFileName().toString(),
                        sourceRowNo));
            }
            return result;
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read FAQ Excel file: " + excelFile, exception);
        }
    }

    private void validateHeaders(Row headerRow, DataFormatter formatter, Path excelFile) {
        if (headerRow == null) {
            throw new IllegalArgumentException("Excel header row is missing: " + excelFile);
        }
        for (int column = 0; column < EXPECTED_HEADERS.size(); column++) {
            String actual = cell(headerRow, column, formatter);
            if (!EXPECTED_HEADERS.get(column).equals(actual)) {
                throw new IllegalArgumentException("Invalid Excel header at column " + (column + 1)
                        + ": expected=" + EXPECTED_HEADERS.get(column) + ", actual=" + actual);
            }
        }
    }

    private boolean isEmpty(Row row, DataFormatter formatter) {
        for (int column = 0; column < EXPECTED_HEADERS.size(); column++) {
            if (!cell(row, column, formatter).isBlank()) {
                return false;
            }
        }
        return true;
    }

    private String cell(Row row, int column, DataFormatter formatter) {
        return formatter.formatCellValue(row.getCell(column, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)).trim();
    }

    private String required(String value, String columnName, int rowNumber) {
        if (value.isBlank()) {
            throw new IllegalArgumentException(columnName + " is empty at Excel row " + rowNumber);
        }
        return value;
    }

    private String categoryValue(String value, int columnIndex, int rowNumber) {
        if (!value.isBlank()) {
            return value;
        }
        log.warn("Empty FAQ category replaced with N/A: row={}, column={}",
                rowNumber, EXPECTED_HEADERS.get(columnIndex));
        return NOT_AVAILABLE;
    }

    private String nullable(String value) {
        return value.isBlank() ? null : value;
    }
}
