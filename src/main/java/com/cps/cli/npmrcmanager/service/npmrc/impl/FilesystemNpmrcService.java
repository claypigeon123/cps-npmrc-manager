package com.cps.cli.npmrcmanager.service.npmrc.impl;

import com.cps.cli.npmrcmanager.model.NpmrcProfile;
import com.cps.cli.npmrcmanager.service.configuration.ConfigurationService;
import com.cps.cli.npmrcmanager.service.input.UserInputService;
import com.cps.cli.npmrcmanager.service.npmrc.NpmrcService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class FilesystemNpmrcService implements NpmrcService {

    @NonNull
    private final UserInputService userInputService;

    @Override
    public NpmrcProfile recordExistingNpmrcIntoProfile(@NonNull File npmrcFile, @NonNull String profilesLocation) {
        System.out.println("Existing .npmrc file detected!");

        String currentNpmrcName = userInputService.promptForString("Enter a name for the current .npmrc config", "npm-central");

        Path profileLocation = Path.of(profilesLocation, currentNpmrcName).toAbsolutePath();

        NpmrcProfile profile = NpmrcProfile.builder()
            .name(currentNpmrcName)
            .path(profileLocation.toString())
            .build();

        copy(npmrcFile.toPath(), Path.of(profile.path()));

        return profile;
    }

    @Override
    public NpmrcProfile recordNewNpmrcForCentralRegistryIntoProfile(@NonNull File npmrcFile, @NonNull String profilesLocation) {
        System.out.println("No existing .npmrc file has been detected!");
        System.out.println("Creating default .npmrc pointing to npm central registry...");

        try (PrintWriter pw = new PrintWriter(new BufferedOutputStream(new FileOutputStream(npmrcFile)))) {
            pw.println("registry=https://registry.npmjs.org/");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error writing to .npmrc file: " + e.getMessage(), e);
        }

        String profileName = "npm-central";
        Path profileLocation = Path.of(profilesLocation, profileName).toAbsolutePath();

        NpmrcProfile profile = NpmrcProfile.builder()
            .name(profileName)
            .path(profileLocation.toString())
            .build();

        copy(npmrcFile.toPath(), Path.of(profile.path()));

        return profile;
    }

    @Override
    public void switchToProfile(@NonNull NpmrcProfile profile, @NonNull String npmrcLocation) {
        copy(Path.of(profile.path()), Path.of(npmrcLocation));
    }

    // --

    private void copy(Path source, Path target) {
        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error copying [" + source + "] to [" + target + "]: " + e.getMessage(), e);
        }
    }
}
