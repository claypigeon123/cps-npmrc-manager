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

@Component
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Command(name = "active", description = "-> Describe the current active profile", mixinStandardHelpOptions = true)
public class ActiveApi extends Api {

    @NonNull
    private final ConfigurationService configurationService;

    @Option(names = {"-v", "--verbose"}, description = "Prints additional information about the active profile", defaultValue = "false")
    private boolean verbose;

    private NpmrcmConfiguration configuration;

    @Override
    protected void initialize() {
        configuration = configurationService.load();
    }

    @Override
    protected void start() {
        NpmrcProfile profile = configuration.getProfiles().stream()
            .filter(p -> p.name().equals(configuration.getActiveProfile()))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("Illegal state in config. Fix your config or re-run setup."));

        printProfile(profile, verbose);
    }

    // --

    private void printProfile(NpmrcProfile profile, boolean verbose) {
        if (!verbose) {
            System.out.printf("%s%n", profile.name());
            return;
        }

        System.out.printf("Active profile: %n%n");
        System.out.printf("# profile name:         %s%n", profile.name());
        System.out.printf("# persistent path:      %s%n", profile.path());
    }
}
