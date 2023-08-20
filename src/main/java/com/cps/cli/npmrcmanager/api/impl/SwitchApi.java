package com.cps.cli.npmrcmanager.api.impl;

import com.cps.cli.npmrcmanager.api.Api;
import com.cps.cli.npmrcmanager.model.NpmrcmConfiguration;
import com.cps.cli.npmrcmanager.service.configuration.ConfigurationService;
import com.cps.cli.npmrcmanager.service.npmrc.NpmrcService;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Component
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Command(name = "switch", description = "-> Switch to the specified .npmrc profile", mixinStandardHelpOptions = true)
public class SwitchApi extends Api {

    @NonNull
    private final ConfigurationService configurationService;

    @NonNull
    private final NpmrcService npmrcService;

    @Parameters(index = "0", paramLabel = "<target profile>", description = "The configured .npmrc config to switch to")
    private String targetProfileName;

    private NpmrcmConfiguration configuration;

    @Override
    protected void initialize() {
        configuration = configurationService.load();
    }

    @Override
    protected void start() {
        npmrcService.switchToProfile(configuration, targetProfileName);

        System.out.printf("Switched to profile [%s]%n", targetProfileName);
    }
}
