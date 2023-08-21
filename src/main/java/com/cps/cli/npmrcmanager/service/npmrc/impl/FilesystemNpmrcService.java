package com.cps.cli.npmrcmanager.service.npmrc.impl;

import com.cps.cli.npmrcmanager.model.NpmrcProfile;
import com.cps.cli.npmrcmanager.model.NpmrcmConfiguration;
import com.cps.cli.npmrcmanager.service.input.UserInputService;
import com.cps.cli.npmrcmanager.service.npmrc.NpmrcService;
import com.cps.cli.npmrcmanager.util.FilesystemHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Optional;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class FilesystemNpmrcService implements NpmrcService {

    @NonNull
    private final UserInputService userInputService;

    @NonNull
    private final FilesystemHelper filesystemHelper;

    @Override
    public NpmrcProfile recordExistingNpmrcIntoProfile(@NonNull String npmrcLocation, @NonNull String profilesLocation) {
        System.out.println("Existing .npmrc file detected!");

        String currentNpmrcName = userInputService.promptForString("Enter a name for the current .npmrc config", "npm-central");

        Path profileLocation = Path.of(profilesLocation, currentNpmrcName).toAbsolutePath();

        NpmrcProfile profile = NpmrcProfile.builder()
            .name(currentNpmrcName)
            .path(profileLocation.toString())
            .active(true)
            .build();

        filesystemHelper.copy(Path.of(npmrcLocation).toAbsolutePath(), Path.of(profile.path()).toAbsolutePath());

        return profile;
    }

    @Override
    public NpmrcProfile recordNewNpmrcForCentralRegistryIntoProfile(@NonNull String npmrcLocation, @NonNull String profilesLocation) {
        System.out.println("No existing .npmrc file has been detected!");
        System.out.println("Creating default .npmrc pointing to npm central registry...");

        Path npmrcPath = Path.of(npmrcLocation).toAbsolutePath();

        filesystemHelper.write(npmrcPath, "registry=https://registry.npmjs.org/");

        String profileName = "npm-central";
        Path profileLocation = Path.of(profilesLocation, profileName).toAbsolutePath();

        NpmrcProfile profile = NpmrcProfile.builder()
            .name(profileName)
            .path(profileLocation.toString())
            .active(true)
            .build();

        filesystemHelper.copy(npmrcPath, Path.of(profile.path()));

        return profile;
    }

    @Override
    public void switchToProfile(@NonNull NpmrcmConfiguration configuration, @NonNull String targetProfileName) {
        Optional<NpmrcProfile> targetProfileOpt = configuration.getProfiles().stream()
            .filter(p -> targetProfileName.equals(p.name()))
            .findAny();

        if (targetProfileOpt.isEmpty()) {
            throw new IllegalStateException(format(
                "Target profile [%s] does not exist.%nSUGGESTION: List available profiles with \"npmrcm list\" or \"npmrcm list --verbose\"",
                targetProfileName
            ));
        }

        NpmrcProfile targetProfile = targetProfileOpt.get();

        if (targetProfile.active()) {
            throw new IllegalStateException(format("Profile [%s] is already active", targetProfile.name()));
        }

        filesystemHelper.copy(Path.of(targetProfile.path()).toAbsolutePath(), Path.of(configuration.getNpmrcPath()).toAbsolutePath());
    }
}
