package com.cps.cli.npmrcmanager.util.extractor.impl;

import com.cps.cli.npmrcmanager.util.FilesystemHelper;
import com.cps.cli.npmrcmanager.util.extractor.ArchiveExtractor;
import lombok.NonNull;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class ZipArchiveExtractor extends ArchiveExtractor {

    public ZipArchiveExtractor(@NonNull FilesystemHelper filesystemHelper) {
        super(filesystemHelper);
    }

    @Override
    protected ArchiveInputStream getArchiveInputStream(@NonNull InputStream is) {
        return new ZipArchiveInputStream(is);
    }
}
