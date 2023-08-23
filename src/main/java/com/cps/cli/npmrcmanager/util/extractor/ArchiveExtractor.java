package com.cps.cli.npmrcmanager.util.extractor;

import com.cps.cli.npmrcmanager.util.FilesystemHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.String.format;

@RequiredArgsConstructor
public abstract class ArchiveExtractor {

    @NonNull
    protected final FilesystemHelper filesystemHelper;

    public void extract(@NonNull Path archivePath) {
        Path destinationDir = archivePath.getParent().normalize();

        try (
            BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(archivePath));
            ArchiveInputStream tais = getArchiveInputStream(bis)
        ) {
            ArchiveEntry entry;
            while ((entry = tais.getNextEntry()) != null) {
                Path extractTo = destinationDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    filesystemHelper.createDirs(extractTo);
                } else {
                    filesystemHelper.copy(tais, extractTo);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(format("Error while extracting archive at [%s]: %s", archivePath, e.getMessage()), e);
        }
    }

    protected abstract ArchiveInputStream getArchiveInputStream(@NonNull InputStream is) throws IOException;
}
