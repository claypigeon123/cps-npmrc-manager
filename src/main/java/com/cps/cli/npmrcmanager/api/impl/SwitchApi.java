package com.cps.cli.npmrcmanager.api.impl;

import com.cps.cli.npmrcmanager.api.Api;
import com.cps.cli.npmrcmanager.model.Configuration;
import com.cps.cli.npmrcmanager.model.NpmrcProfile;
import com.cps.cli.npmrcmanager.service.configuration.ConfigurationService;
import com.cps.cli.npmrcmanager.service.npmrc.NpmrcService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Command(name = "switch", description = "-> Switch to the specified .npmrc profile", mixinStandardHelpOptions = true)
public class SwitchApi extends Api {

    @NonNull
    private final ConfigurationService configurationService;

    @NonNull
    private final NpmrcService npmrcService;

    @Parameters(index = "0", paramLabel = "<target profile>", description = "The configured .npmrc config to switch to")
    private String targetProfileName;

    private Configuration configuration;

    @Override
    protected void initialize() {
        configuration = configurationService.load();
    }

    @Override
    protected void start() {
        Optional<NpmrcProfile> targetProfileOpt = configuration.getProfiles().stream()
            .filter(p -> targetProfileName.equals(p.name()))
            .findAny();

        if (targetProfileOpt.isEmpty()) {
            System.err.printf("Target profile [%s] does not exist.%n", targetProfileName);
            System.err.printf("SUGGESTION: List available profiles with \"npmrcm list\" or \"npmrcm list --verbose\"%n");
            return;
        }

        NpmrcProfile targetProfile = targetProfileOpt.get();

        if (configuration.getActiveProfile().equals(targetProfile.name())) {
            throw new IllegalStateException("Profile [" + targetProfile.name() + "] is already active");
        }

        npmrcService.switchToProfile(configuration.getNpmrcPath(), targetProfile);

        System.out.printf("Switched to profile [%s]", targetProfile.name());
    }
}
