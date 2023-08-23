package com.cps.cli.npmrcmanager.api.impl;

import com.cps.cli.npmrcmanager.api.Api;
import com.cps.cli.npmrcmanager.service.update.UpdateService;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Command(name = "update", description = "-> Check for updates and update if necessary", mixinStandardHelpOptions = true)
public class UpdateApi extends Api {

    @NonNull
    private final UpdateService updateService;

    @Option(names = {"-f", "--force"}, description = "Update npmrcm even if the latest version is already present", defaultValue = "false")
    private boolean force;

    @Override
    protected void start() {
        updateService.tryUpdate(force);
    }
}
