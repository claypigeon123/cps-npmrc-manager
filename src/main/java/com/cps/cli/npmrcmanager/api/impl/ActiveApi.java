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

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Command(name = "active", description = "-> Describe the current active profile", mixinStandardHelpOptions = true)
public class ActiveApi extends Api {

    @NonNull
    private final ConfigurationService configurationService;

    private NpmrcmConfiguration configuration;

    @Override
    protected void initialize() {
        configuration = configurationService.load();
    }

    @Override
    protected void start() {
        String profiles = configuration.getProfiles().stream()
            .filter(NpmrcProfile::active)
            .map(NpmrcProfile::name)
            .collect(Collectors.joining(", "));

        if (profiles.isBlank()) {
            System.err.printf("None of the configured profiles are active%n");
            return;
        }

        System.out.printf("%s%n", profiles);
    }
}
