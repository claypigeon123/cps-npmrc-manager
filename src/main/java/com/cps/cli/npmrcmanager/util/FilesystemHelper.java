package com.cps.cli.npmrcmanager.util;

import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Component
public class FilesystemHelper {
    private static final Path USER_HOME_PATH = Path.of(System.getProperty("user.home"));
    private static final Path DEFAULT_NPMRC_PATH = USER_HOME_PATH.resolve(".npmrc");
    private static final Path CONFIG_FOLDER = USER_HOME_PATH.resolve(".npmrcm");
    private static final Path PROFILES_FOLDER = CONFIG_FOLDER.resolve("profiles");
    private static final Path CONFIG_JSON_FILE_PATH = CONFIG_FOLDER.resolve("config.json");

    public Path getDefaultNpmrcPath() {
        return DEFAULT_NPMRC_PATH;
    }

    public Path getProfilesFolder() {
        return PROFILES_FOLDER;
    }

    public Path getConfigJsonFilePath() {
        return CONFIG_JSON_FILE_PATH;
    }

    public void copy(Path source, Path target) {
        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("Error copying [" + source + "] to [" + target + "]: " + e.getMessage(), e);
        }
    }

    public void write(Path target, String content) {
        try (PrintWriter pw = new PrintWriter(new BufferedOutputStream(new FileOutputStream(target.toString())))) {
            pw.println(content);
        } catch (IOException e) {
            throw new UncheckedIOException("Error writing to file [" + target + "]: " + e.getMessage(), e);
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
            throw new UncheckedIOException("Error deleting file at location [" + path.toAbsolutePath() + "]: " + e.getMessage(), e);
        }
    }

    public String read(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new UncheckedIOException("Error reading file at location [" + path.toAbsolutePath() + "]: " + e.getMessage(), e);
        }
    }

    public Stream<Path> list(Path path) {
        try {
            return Files.list(path);
        } catch (IOException e) {
            throw new UncheckedIOException("Error listing files in location [" + path.toAbsolutePath() + "]: " + e.getMessage(), e);
        }
    }

    public void createDirs(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create dirs through location [" + path.toAbsolutePath() + "]: " + e.getMessage(), e);
        }
    }
}
