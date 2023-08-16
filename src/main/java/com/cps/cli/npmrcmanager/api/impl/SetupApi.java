package com.cps.cli.npmrcmanager.api.impl;

import com.cps.cli.npmrcmanager.api.Api;
import com.cps.cli.npmrcmanager.model.Configuration;
import com.cps.cli.npmrcmanager.model.NpmrcProfile;
import com.cps.cli.npmrcmanager.service.configuration.ConfigurationService;
import com.cps.cli.npmrcmanager.service.input.UserInputService;
import com.cps.cli.npmrcmanager.service.npmrc.NpmrcService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Command(name = "setup", description = "-> Run guided setup of required configuration for this tool", mixinStandardHelpOptions = true)
public class SetupApi extends Api {

    private static final Path DEFAULT_NPMRC_PATH = Path.of(System.getProperty("user.home"), ".npmrc");

    @NonNull
    private final UserInputService userInputService;

    @NonNull
    private final ConfigurationService configurationService;

    @NonNull
    private final NpmrcService npmrcService;

    @Override
    protected void initialize() {
        boolean configExists = configurationService.exists();
        if (!configExists) return;

        boolean wantsToProceed = userInputService.promptForYesOrNo("Config file already exists. Are you sure you want to proceed?");
        if (!wantsToProceed) throw new RuntimeException("Terminated by user");
    }

    @Override
    protected void start() {
        System.out.println("Starting guided setup.");
        System.out.println("Some answers have defaults in [square brackets] - just press enter to accept these as is.");
        System.out.println();

        Path npmrcPath = userInputService.promptForPath("Enter path to active npmrc file", DEFAULT_NPMRC_PATH);

        File npmrcFile = new File(npmrcPath.toUri());

        NpmrcProfile profile = (npmrcFile.exists() && npmrcFile.isFile())
            ? npmrcService.recordExistingNpmrcIntoProfile(npmrcFile, configurationService.getProfilesLocation())
            : npmrcService.recordNewNpmrcForCentralRegistryIntoProfile(npmrcFile, configurationService.getProfilesLocation());

        Configuration configuration = Configuration.builder()
            .npmrcPath(npmrcPath.toAbsolutePath().toString())
            .profiles(new ArrayList<>(List.of(profile)))
            .activeProfile(profile.name())
            .build();

        configurationService.save(configuration);

        System.out.println("Configuration complete");
    }
}
