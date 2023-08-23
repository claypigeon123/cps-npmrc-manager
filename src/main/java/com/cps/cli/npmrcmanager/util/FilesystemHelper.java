package com.cps.cli.npmrcmanager.util;

import com.cps.cli.npmrcmanager.AppRunner;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.String.format;

@Component
public class FilesystemHelper {
    private static final String TEMP_DIR_PREFIX = "npmrcm";

    private static final Path USER_HOME_PATH = Path.of(System.getProperty("user.home"));
    private static final Path DEFAULT_NPMRC_PATH = USER_HOME_PATH.resolve(".npmrc");
    private static final Path CONFIG_DIR = USER_HOME_PATH.resolve(".npmrcm");
    private static final Path PROFILES_DIR = CONFIG_DIR.resolve("profiles");
    private static final Path CONFIG_JSON_PATH = CONFIG_DIR.resolve("config.json");

    private final List<Path> tempDirs = new ArrayList<>();

    public Path getDefaultNpmrcPath() {
        return DEFAULT_NPMRC_PATH;
    }

    public Path getProfilesDirPath() {
        return PROFILES_DIR;
    }

    public Path getConfigJsonPath() {
        return CONFIG_JSON_PATH;
    }

    public Path getCurrentExecutingPath() {
        URI uri;

        try {
            uri = AppRunner.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        } catch (Exception e) {
            throw new IllegalStateException(format("Error getting URI for executable location: %s", e.getMessage()));
        }

        return Path.of(uri.toString().replaceAll("jar:file:/|file:/", "")
            .replace("!/BOOT-INF/classes!/", "")
            .replace("/", File.separator)
            .replace("%20", " "));
    }

    public void copy(@NonNull Path source, @NonNull Path target) {
        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException(format("Error copying [%s] to [%s]: %s", source, target, e.getMessage()), e);
        }
    }

    public void copy(@NonNull InputStream source, @NonNull Path target) {
        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException(format("Error copying [%s] to [%s]: %s", source, target, e.getMessage()), e);
        }
    }

    public void move(@NonNull Path source, @NonNull Path target) {
        try {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException(format("Error moving [%s] to [%s]: %s", source, target, e.getMessage()), e);
        }
    }

    public void write(@NonNull Path target, @NonNull String content) {
        try (PrintWriter pw = new PrintWriter(new BufferedOutputStream(new FileOutputStream(target.toString())))) {
            pw.println(content);
        } catch (IOException e) {
            throw new UncheckedIOException(format("Error writing to file [%s]: %s", target, e.getMessage()), e);
        }
    }

    public boolean exists(@NonNull Path path, boolean shouldBeDirectory) {
        return shouldBeDirectory
            ? Files.isDirectory(path)
            : Files.exists(path);
    }

    public boolean remove(@NonNull Path path) {
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new UncheckedIOException(format("Error deleting file at location [%s]: %s", path.toAbsolutePath(), e.getMessage()), e);
        }
    }

    public String read(@NonNull Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new UncheckedIOException(format("Error reading file at location [%s]: %s", path.toAbsolutePath(), e.getMessage()), e);
        }
    }

    public Stream<Path> list(@NonNull Path path) {
        try {
            return Files.list(path);
        } catch (IOException e) {
            throw new UncheckedIOException(format("Error listing files in location [%s]: %s", path.toAbsolutePath(), e.getMessage()), e);
        }
    }

    public void createDirs(@NonNull Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new UncheckedIOException(format("Failed to create dirs through location [%s]: %s", path.toAbsolutePath(), e.getMessage()), e);
        }
    }

    public Path createTempDir() {
        Path path;
        try {
            path = Files.createTempDirectory(TEMP_DIR_PREFIX);
        } catch (IOException e) {
            throw new UncheckedIOException(format("Failed to create new temporary directory: %s", e.getMessage()), e);
        }
        tempDirs.add(path.toAbsolutePath());
        return path;
    }

    public int cleanTempDirs() {
        if (tempDirs.isEmpty()) return 0;

        int count = 0;
        for (Path dir : tempDirs) {
            if (remove(dir)) count++;
        }

        return count;
    }
}
