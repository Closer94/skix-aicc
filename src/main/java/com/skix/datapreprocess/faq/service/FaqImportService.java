package com.skix.datapreprocess.faq.service;

import com.skix.datapreprocess.faq.config.FaqImportProperties;
import com.skix.datapreprocess.faq.dto.FaqFileImportResult;
import com.skix.datapreprocess.faq.dto.FaqImportResult;
import com.skix.datapreprocess.faq.dto.FaqImportRow;
import com.skix.datapreprocess.faq.mapper.FaqMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class FaqImportService {
    private static final Logger log = LoggerFactory.getLogger(FaqImportService.class);

    private final FaqImportProperties properties;
    private final FaqExcelReader excelReader;
    private final FaqMapper faqMapper;

    public FaqImportService(FaqImportProperties properties, FaqExcelReader excelReader, FaqMapper faqMapper) {
        this.properties = properties;
        this.excelReader = excelReader;
        this.faqMapper = faqMapper;
    }

    public FaqImportResult importFaqs() {
        Path directory = Path.of(properties.importDirectory()).toAbsolutePath().normalize();
        List<Path> files = findMatchingFiles(directory);
        if (files.isEmpty()) {
            throw new IllegalStateException("No FAQ Excel file found: directory=" + directory
                    + ", pattern=" + properties.filePattern());
        }

        long startedAt = System.nanoTime();
        int totalRows = 0;
        List<FaqFileImportResult> fileResults = new ArrayList<>();
        log.info("FAQ import started: directory={}, pattern={}, files={}",
                directory, properties.filePattern(), files.size());

        for (Path file : files) {
            List<FaqImportRow> rows = excelReader.read(file);
            int batchCount = 0;
            for (int from = 0; from < rows.size(); from += properties.batchSize()) {
                int to = Math.min(from + properties.batchSize(), rows.size());
                faqMapper.upsertFaqs(rows.subList(from, to));
                batchCount++;
                log.info("FAQ import batch completed: file={}, batch={}, rows={}, processed={}/{}",
                        file.getFileName(), batchCount, to - from, to, rows.size());
            }
            totalRows += rows.size();
            fileResults.add(new FaqFileImportResult(file.getFileName().toString(), rows.size()));
            log.info("FAQ file import completed: file={}, rows={}, batches={}",
                    file.getFileName(), rows.size(), batchCount);
        }

        log.info("FAQ import completed: files={}, rows={}, elapsedMs={}",
                files.size(), totalRows, (System.nanoTime() - startedAt) / 1_000_000);
        return new FaqImportResult(files.size(), totalRows, List.copyOf(fileResults));
    }

    private List<Path> findMatchingFiles(Path directory) {
        if (!Files.isDirectory(directory)) {
            throw new IllegalStateException("FAQ import directory does not exist: " + directory);
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, properties.filePattern())) {
            List<Path> result = new ArrayList<>();
            for (Path path : stream) {
                if (Files.isRegularFile(path)) {
                    result.add(path.toAbsolutePath().normalize());
                }
            }
            result.sort(Comparator.comparing(path -> path.getFileName().toString()));
            return result;
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to scan FAQ import directory: " + directory, exception);
        }
    }
}
