package com.skix.datapreprocess.faq.dto;

import java.util.List;

public record FaqImportResult(int fileCount, int rowCount, List<FaqFileImportResult> files) {
}
