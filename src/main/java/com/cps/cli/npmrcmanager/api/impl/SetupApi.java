package com.cps.cli.npmrcmanager.api.impl;

import com.cps.cli.npmrcmanager.api.Api;
import com.cps.cli.npmrcmanager.service.configuration.ConfigurationService;
import com.cps.cli.npmrcmanager.service.input.UserInputService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@RequiredArgsConstructor
@Command(name = "setup", description = "-> Run guided setup of required configuration for this tool", mixinStandardHelpOptions = true)
public class SetupApi extends Api {

    @NonNull
    private final UserInputService userInputService;

    @NonNull
    private final ConfigurationService configurationService;

    @Override
    protected void initialize() {
        System.out.printf("Initializing guided setup%n");

        boolean configExists = configurationService.exists();
        if (!configExists) return;

        boolean wantsToProceed = userInputService.promptForYesOrNo("Config file already exists. Are you sure you want to proceed?");
        if (!wantsToProceed) throw new RuntimeException("Terminated by user");
    }

    @Override
    protected void start() {
        System.out.printf("Starting guided setup%n");
        System.out.printf("Some answers have defaults in [square brackets] - just press enter to accept these as is.%n");

        configurationService.setup();

        System.out.println("Configuration complete");
    }
}
