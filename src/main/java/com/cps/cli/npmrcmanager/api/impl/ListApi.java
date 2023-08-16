package com.cps.cli.npmrcmanager.api.impl;

import com.cps.cli.npmrcmanager.api.Api;
import com.cps.cli.npmrcmanager.model.Configuration;
import com.cps.cli.npmrcmanager.service.configuration.ConfigurationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@RequiredArgsConstructor
@Command(name = "list", description = "-> List configured .npmrc profiles", mixinStandardHelpOptions = true)
public class ListApi extends Api {

    @NonNull
    private final ConfigurationService configurationService;

    private Configuration configuration;

    @Override
    protected void initialize() {
        configuration = configurationService.load();
    }

    @Override
    protected void start() {
        System.out.printf("Listing .npmrc profiles:%n");

        String activeProfile = configuration.getActiveProfile();

        for (var profile : configuration.getProfiles()) {
            System.out.printf("%n");
            System.out.printf("# profile name:              %s%n", profile.name());
            System.out.printf("# persistent path:           %s%n", profile.path());
            System.out.printf("# active?                    %s%n", activeProfile.equals(profile.name()) ? "yes" : "no");
        }
    }
}
