package com.cps.cli.npmrcmanager.service.configuration.impl;

import com.cps.cli.npmrcmanager.model.Configuration;
import com.cps.cli.npmrcmanager.model.NpmrcProfile;
import com.cps.cli.npmrcmanager.service.configuration.ConfigurationService;
import com.cps.cli.npmrcmanager.service.input.UserInputService;
import com.cps.cli.npmrcmanager.service.npmrc.NpmrcService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilesystemConfigurationService implements ConfigurationService {

    private static final Path DEFAULT_NPMRC_PATH = Path.of(System.getProperty("user.home"), ".npmrc");

    private static final Path CONFIG_FOLDER = Path.of(System.getProperty("user.home"), ".npmrcm");
    private static final Path PROFILES_FOLDER = CONFIG_FOLDER.resolve("profiles");
    private static final Path CONFIG_FILE_PATH = CONFIG_FOLDER.resolve("config.json");

    @NonNull
    private final ObjectMapper objectMapper;

    @NonNull
    private final UserInputService userInputService;

    @NonNull
    private final NpmrcService npmrcService;

    @Override
    public void setup() {
        Path npmrcPath = userInputService.promptForPath("Enter path to active npmrc file", DEFAULT_NPMRC_PATH);

        File npmrcFile = new File(npmrcPath.toUri());

        NpmrcProfile profile = (npmrcFile.exists() && npmrcFile.isFile())
            ? npmrcService.recordExistingNpmrcIntoProfile(npmrcFile.getAbsolutePath(), PROFILES_FOLDER.toAbsolutePath().toString())
            : npmrcService.recordNewNpmrcForCentralRegistryIntoProfile(npmrcFile.getAbsolutePath(), PROFILES_FOLDER.toAbsolutePath().toString());

        Configuration configuration = Configuration.builder()
            .npmrcPath(npmrcPath.toAbsolutePath().toString())
            .profiles(new ArrayList<>(List.of(profile)))
            .activeProfile(profile.name())
            .build();

        save(configuration);
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
        Optional<String> npmrcFileContents;
        try {
            npmrcFileContents = Optional.of(Files.readString(npmrcPath));
        } catch (IOException e) {
            System.err.printf("Could not parse file contents of .npmrc file at [%s]. Does it exist?%n", npmrcPath);
            System.err.printf("SUGGESTION: Activate a configured profile with \"npmrcm switch <profile name>\" to have it created for you.%n%n");
            npmrcFileContents = Optional.empty();
        }

        File profilesFolder = new File(PROFILES_FOLDER.toUri());
        File[] profileFiles = profilesFolder.listFiles();

        if (profileFiles == null || profileFiles.length < 1) return configuration;

        for (File profileFile : profileFiles) {
            NpmrcProfile profile = NpmrcProfile.builder()
                .name(profileFile.getName())
                .path(profileFile.getAbsolutePath())
                .build();

            String fileContents;
            try {
                fileContents = Files.readString(profileFile.toPath());
            } catch (IOException e) {
                throw new RuntimeException("Could not parse file contents of [" + profileFile.getAbsolutePath() + "]: " + e.getMessage(), e);
            }

            if (npmrcFileContents.isPresent() && fileContents.equals(npmrcFileContents.get())) {
                configuration.setActiveProfile(profile.name());
            }

            configuration.getProfiles().add(profile);
        }

        return configuration;
    }

    @Override
    public boolean exists() {
        File configFile = new File(CONFIG_FILE_PATH.toUri());
        return configFile.exists() && configFile.isFile();
    }

    @Override
    public void save(@NonNull Configuration configuration) {
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
