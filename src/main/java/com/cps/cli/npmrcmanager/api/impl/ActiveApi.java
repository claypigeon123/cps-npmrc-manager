package com.cps.cli.npmrcmanager.api.impl;

import com.cps.cli.npmrcmanager.api.Api;
import com.cps.cli.npmrcmanager.model.Configuration;
import com.cps.cli.npmrcmanager.model.NpmrcProfile;
import com.cps.cli.npmrcmanager.service.configuration.ConfigurationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@RequiredArgsConstructor
@Command(name = "active", description = "-> Describe the current active profile", mixinStandardHelpOptions = true)
public class ActiveApi extends Api {

    @NonNull
    private final ConfigurationService configurationService;

    private Configuration configuration;

    @Override
    protected void initialize() {
        configuration = configurationService.load();
    }

    @Override
    protected void start() {
        System.out.printf("Active profile:%n");

        NpmrcProfile profile = configuration.getProfiles().stream()
            .filter(p -> configuration.getActiveProfile().equals(p.name()))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("Illegal state in config. Fix your config or re-run setup."));

        System.out.printf("%n");
        System.out.printf("# profile name:              %s%n", profile.name());
        System.out.printf("# persistent path:           %s%n", profile.path());
    }
}
