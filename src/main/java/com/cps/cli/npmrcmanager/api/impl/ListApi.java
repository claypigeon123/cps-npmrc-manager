package com.cps.cli.npmrcmanager.api.impl;

import com.cps.cli.npmrcmanager.api.Api;
import com.cps.cli.npmrcmanager.model.NpmrcProfile;
import com.cps.cli.npmrcmanager.model.NpmrcmConfiguration;
import com.cps.cli.npmrcmanager.service.configuration.ConfigurationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.List;

@Component
@RequiredArgsConstructor
@Command(name = "list", description = "-> List configured .npmrc profiles", mixinStandardHelpOptions = true)
public class ListApi extends Api {

    @NonNull
    private final ConfigurationService configurationService;

    @Option(names = {"-v", "--verbose"}, description = "Prints additional information about profiles", defaultValue = "false")
    private boolean verbose;

    private NpmrcmConfiguration configuration;

    @Override
    protected void initialize() {
        configuration = configurationService.load();
    }

    @Override
    protected void start() {
        printProfiles(configuration.getProfiles());
    }

    // --

    private void printProfiles(List<NpmrcProfile> profiles) {
        String activeProfileName = configuration.getActiveProfile();

        if (verbose) System.out.printf("Listing .npmrc profiles:%n");

        profiles.forEach(profile -> printProfile(profile, profile.name().equals(activeProfileName)));
    }

    private void printProfile(NpmrcProfile profile, boolean isActive) {
        if (!verbose) {
            System.out.printf("%s%s%n", isActive ? "---> " : "     ", profile.name());
            return;
        }

        System.out.printf("%n");
        System.out.printf("# profile name:         %s%n", profile.name());
        System.out.printf("# persistent path:      %s%n", profile.path());
        System.out.printf("# active?               %s%n", isActive ? "yes" : "no");
    }
}
