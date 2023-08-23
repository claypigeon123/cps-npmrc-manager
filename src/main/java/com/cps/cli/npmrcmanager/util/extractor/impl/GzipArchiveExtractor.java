package com.cps.cli.npmrcmanager.util.extractor.impl;

import com.cps.cli.npmrcmanager.util.FilesystemHelper;
import com.cps.cli.npmrcmanager.util.extractor.ArchiveExtractor;
import lombok.NonNull;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class GzipArchiveExtractor extends ArchiveExtractor {

    public GzipArchiveExtractor(@NonNull FilesystemHelper filesystemHelper) {
        super(filesystemHelper);
    }

    @Override
    protected ArchiveInputStream getArchiveInputStream(@NonNull InputStream is) throws IOException {
        return new TarArchiveInputStream(new GzipCompressorInputStream(is));
    }
}
