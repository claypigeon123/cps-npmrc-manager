package com.cps.cli.npmrcmanager.service.configuration.impl;

import com.cps.cli.npmrcmanager.model.Configuration;
import com.cps.cli.npmrcmanager.model.NpmrcProfile;
import com.cps.cli.npmrcmanager.service.configuration.ConfigurationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class FilesystemConfigurationService implements ConfigurationService {

    private static final Path CONFIG_FOLDER = Path.of(System.getProperty("user.home"), ".npmrcm");
    private static final Path PROFILES_FOLDER = CONFIG_FOLDER.resolve("profiles");
    private static final Path CONFIG_FILE_PATH = CONFIG_FOLDER.resolve("config.json");

    private final ObjectMapper objectMapper;

    @Override
    public String getConfigLocation() {
        return CONFIG_FOLDER.toAbsolutePath().toString();
    }

    @Override
    public String getProfilesLocation() {
        return PROFILES_FOLDER.toAbsolutePath().toString();
    }

    @Override
    public Configuration load() {
        File configFile = new File(CONFIG_FILE_PATH.toUri());

        if (!configFile.exists() || configFile.isDirectory()) {
            throw new IllegalStateException(String.format(
                "Config file not found in the location it is supposed to be at: \"%s\"\nSet it up by running \"npmrcm setup\"",
                configFile.getAbsolutePath()
            ));
        }

        Configuration configuration;

        try {
            configuration = objectMapper.readValue(configFile, Configuration.class);
        } catch (IOException e) {
            throw new IllegalStateException("Error while parsing configuration json file: " + e.getMessage(), e);
        }

        Path npmrcPath = Path.of(configuration.getNpmrcPath()).toAbsolutePath();
        String npmrcFileContents;
        try {
            npmrcFileContents = Files.readString(npmrcPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not parse file contents of [" + npmrcPath + "]: " + e.getMessage(), e);
        }

        File profilesFolder = new File(PROFILES_FOLDER.toUri());
        File[] profileFiles = profilesFolder.listFiles();

        if (profileFiles == null || profileFiles.length < 1) return configuration;

        Arrays.stream(profileFiles).forEach(file -> {
            NpmrcProfile profile = NpmrcProfile.builder()
                .name(file.getName())
                .path(file.getAbsolutePath())
                .build();

            String fileContents;
            try {
                fileContents = Files.readString(file.toPath());
            } catch (IOException e) {
                throw new RuntimeException("Could not parse file contents of [" + file.getAbsolutePath() + "]: " + e.getMessage(), e);
            }

            if (npmrcFileContents.equals(fileContents)) {
                configuration.setActiveProfile(profile.name());
            }

            configuration.getProfiles().add(profile);
        });

        return configuration;
    }

    @Override
    public boolean exists() {
        File configFile = new File(CONFIG_FILE_PATH.toUri());
        return configFile.exists() && configFile.isFile();
    }

    @Override
    public void save(Configuration configuration) {
        File configFile = new File(CONFIG_FILE_PATH.toUri());

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(configFile, configuration);
        } catch (IOException e) {
            throw new IllegalStateException("Error while writing configuration json file: " + e.getMessage(), e);
        }
    }

    @Override
    public void remove() {
        File configFile = new File(CONFIG_FILE_PATH.toUri());

        if (!configFile.exists() || configFile.isDirectory()) return;

        boolean deleteWasSuccess = configFile.delete();

        if (!deleteWasSuccess) {
            throw new IllegalStateException("Deletion of config file failed");
        }
    }

    // --

    @PostConstruct
    private void postConstruct() throws IOException {
        Files.createDirectories(CONFIG_FOLDER);
        Files.createDirectories(PROFILES_FOLDER);
    }
}
