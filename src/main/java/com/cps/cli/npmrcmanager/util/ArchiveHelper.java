package com.cps.cli.npmrcmanager.util;

import com.cps.cli.npmrcmanager.util.extractor.ArchiveExtractor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class ArchiveHelper {

    @NonNull
    private final ArchiveExtractor zipArchiveExtractor;

    @NonNull
    private final ArchiveExtractor gzipArchiveExtractor;

    public void extract(@NonNull Path archivePath) {
        if (archivePath.toString().endsWith(".zip")) {
            zipArchiveExtractor.extract(archivePath);
        } else if (archivePath.toString().endsWith(".tar.gz")) {
            gzipArchiveExtractor.extract(archivePath);
        } else {
            throw new IllegalStateException("Specified path is neither .zip nor .tar.gz - cannot extract");
        }
    }
}
