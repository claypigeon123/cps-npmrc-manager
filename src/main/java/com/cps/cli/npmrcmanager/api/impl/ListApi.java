package com.cps.cli.npmrcmanager.api.impl;

import com.cps.cli.npmrcmanager.api.Api;
import com.cps.cli.npmrcmanager.model.NpmrcProfile;
import com.cps.cli.npmrcmanager.model.NpmrcmConfiguration;
import com.cps.cli.npmrcmanager.service.configuration.ConfigurationService;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.List;

@Component
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
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
        if (verbose) System.out.printf("Listing .npmrc profiles:%n");

        profiles.forEach(this::printProfile);
    }

    private void printProfile(NpmrcProfile profile) {
        if (!verbose) {
            System.out.printf("%s%s%n", profile.active() ? "---> " : "     ", profile.name());
            return;
        }

        System.out.printf("%n");
        System.out.printf("# profile name:         %s%n", profile.name());
        System.out.printf("# persistent path:      %s%n", profile.path());
        System.out.printf("# active?               %s%n", profile.active() ? "yes" : "no");
    }
}
