package com.cps.cli.npmrcmanager.util;

import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import static java.lang.String.format;

@Component
public class FilesystemHelper {
    private static final Path USER_HOME_PATH = Path.of(System.getProperty("user.home"));
    private static final Path DEFAULT_NPMRC_PATH = USER_HOME_PATH.resolve(".npmrc");
    private static final Path CONFIG_DIR = USER_HOME_PATH.resolve(".npmrcm");
    private static final Path PROFILES_DIR = CONFIG_DIR.resolve("profiles");
    private static final Path CONFIG_JSON_PATH = CONFIG_DIR.resolve("config.json");

    public Path getDefaultNpmrcPath() {
        return DEFAULT_NPMRC_PATH;
    }

    public Path getProfilesDirPath() {
        return PROFILES_DIR;
    }

    public Path getConfigJsonPath() {
        return CONFIG_JSON_PATH;
    }

    public void copy(Path source, Path target) {
        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException(format("Error copying [%s] to [%s]: %s", source, target, e.getMessage()), e);
        }
    }

    public void write(Path target, String content) {
        try (PrintWriter pw = new PrintWriter(new BufferedOutputStream(new FileOutputStream(target.toString())))) {
            pw.println(content);
        } catch (IOException e) {
            throw new UncheckedIOException(format("Error writing to file [%s]: %s", target, e.getMessage()), e);
        }
    }

    public boolean exists(Path path, boolean shouldBeDirectory) {
        return shouldBeDirectory
            ? Files.isDirectory(path)
            : Files.exists(path);
    }

    public void remove(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new UncheckedIOException(format("Error deleting file at location [%s]: %s", path.toAbsolutePath(), e.getMessage()), e);
        }
    }

    public String read(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new UncheckedIOException(format("Error reading file at location [%s]: %s", path.toAbsolutePath(), e.getMessage()), e);
        }
    }

    public Stream<Path> list(Path path) {
        try {
            return Files.list(path);
        } catch (IOException e) {
            throw new UncheckedIOException(format("Error listing files in location [%s]: %s", path.toAbsolutePath(), e.getMessage()), e);
        }
    }

    public void createDirs(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new UncheckedIOException(format("Failed to create dirs through location [%s]: %s", path.toAbsolutePath(), e.getMessage()), e);
        }
    }
}
