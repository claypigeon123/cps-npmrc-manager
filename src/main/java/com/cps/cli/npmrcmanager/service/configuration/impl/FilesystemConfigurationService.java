package com.cps.cli.npmrcmanager.service.configuration.impl;

import com.cps.cli.npmrcmanager.model.NpmrcProfile;
import com.cps.cli.npmrcmanager.model.NpmrcmConfiguration;
import com.cps.cli.npmrcmanager.service.configuration.ConfigurationService;
import com.cps.cli.npmrcmanager.service.input.UserInputService;
import com.cps.cli.npmrcmanager.service.npmrc.NpmrcService;
import com.cps.cli.npmrcmanager.util.FilesystemHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilesystemConfigurationService implements ConfigurationService {

    @NonNull
    private final ObjectMapper objectMapper;

    @NonNull
    private final UserInputService userInputService;

    @NonNull
    private final NpmrcService npmrcService;

    @NonNull
    private final FilesystemHelper filesystemHelper;

    @Override
    public void setup() {
        Path npmrcPath = userInputService.promptForPath("Enter path to active npmrc file", filesystemHelper.getDefaultNpmrcPath());

        boolean npmrcExists = filesystemHelper.exists(npmrcPath, false);
        String profilesFolderLocation = filesystemHelper.getProfilesFolder().toAbsolutePath().toString();

        NpmrcProfile profile = npmrcExists
            ? npmrcService.recordExistingNpmrcIntoProfile(npmrcPath.toString(), profilesFolderLocation)
            : npmrcService.recordNewNpmrcForCentralRegistryIntoProfile(npmrcPath.toString(), profilesFolderLocation);

        NpmrcmConfiguration configuration = NpmrcmConfiguration.builder()
            .npmrcPath(npmrcPath.toAbsolutePath().toString())
            .profiles(new ArrayList<>(List.of(profile)))
            .build();

        save(configuration);
    }

    @Override
    public NpmrcmConfiguration load() {
        Path configJsonFilePath = filesystemHelper.getConfigJsonFilePath();
        String configFileContents;
        try {
            configFileContents = filesystemHelper.read(configJsonFilePath);
        } catch (UncheckedIOException e) {
            throw new IllegalStateException(String.format(
                "Config file not found in the location it is supposed to be at: [%s]%nSet it up by running \"npmrcm setup\"",
                configJsonFilePath
            ));
        }

        NpmrcmConfiguration configuration;
        try {
            configuration = objectMapper.readValue(configFileContents, NpmrcmConfiguration.class);
        } catch (IOException e) {
            throw new IllegalStateException("Error while parsing configuration json file: " + e.getMessage(), e);
        }

        Path npmrcPath = Path.of(configuration.getNpmrcPath()).toAbsolutePath();
        Optional<String> npmrcFileContentsOpt;
        try {
            String npmrcFileContents = filesystemHelper.read(npmrcPath);
            npmrcFileContentsOpt = Optional.of(npmrcFileContents);
        } catch (UncheckedIOException e) {
            System.err.printf("Could not parse file contents of .npmrc file at [%s]. Does it exist?%n", npmrcPath);
            System.err.printf("SUGGESTION: Activate a configured profile with \"npmrcm switch <profile name>\" to have it created for you.%n%n");
            npmrcFileContentsOpt = Optional.empty();
        }

        List<Path> profilePaths = filesystemHelper.list(filesystemHelper.getProfilesFolder()).toList();

        if (profilePaths.isEmpty()) return configuration;

        for (Path profilePath : profilePaths) {
            if (!filesystemHelper.exists(profilePath, false)) {
                continue;
            }

            String fileContents = filesystemHelper.read(profilePath);
            boolean active = npmrcFileContentsOpt.isPresent() && fileContents.equals(npmrcFileContentsOpt.get());

            NpmrcProfile profile = NpmrcProfile.builder()
                .name(profilePath.getFileName().toString())
                .path(profilePath.toAbsolutePath().toString())
                .active(active)
                .build();

            configuration.getProfiles().add(profile);
        }

        return configuration;
    }

    @Override
    public boolean exists() {
        return filesystemHelper.exists(filesystemHelper.getConfigJsonFilePath(), false);
    }

    @Override
    public void save(@NonNull NpmrcmConfiguration configuration) {
        String content;

        try {
            content = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(configuration);
        } catch (IOException e) {
            throw new UncheckedIOException("Error while serializing app configuration to json: " + e.getMessage(), e);
        }

        filesystemHelper.write(filesystemHelper.getConfigJsonFilePath(), content);
    }

    @Override
    public void remove() {
        filesystemHelper.remove(filesystemHelper.getConfigJsonFilePath());
    }

    // --

    @PostConstruct
    private void postConstruct() {
        filesystemHelper.createDirs(filesystemHelper.getProfilesFolder());
    }
}
