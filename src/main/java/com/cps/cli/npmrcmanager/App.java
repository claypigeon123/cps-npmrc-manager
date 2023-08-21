package com.cps.cli.npmrcmanager;

import com.cps.cli.npmrcmanager.api.impl.ActiveApi;
import com.cps.cli.npmrcmanager.api.impl.ListApi;
import com.cps.cli.npmrcmanager.api.impl.SetupApi;
import com.cps.cli.npmrcmanager.api.impl.SwitchApi;
import com.cps.cli.npmrcmanager.config.VersionProviderConfiguration;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(
    name = "npmrcm",
    versionProvider = VersionProviderConfiguration.class,
    mixinStandardHelpOptions = true,
    subcommands = {
        SetupApi.class,
        ListApi.class,
        ActiveApi.class,
        SwitchApi.class
    }
)
public class App {
}
