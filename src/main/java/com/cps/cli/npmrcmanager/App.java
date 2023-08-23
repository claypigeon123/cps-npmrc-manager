package com.cps.cli.npmrcmanager;

import com.cps.cli.npmrcmanager.api.impl.*;
import com.cps.cli.npmrcmanager.util.InfoProvider;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(
    versionProvider = InfoProvider.class,
    mixinStandardHelpOptions = true,
    subcommands = {
        SetupApi.class,
        ListApi.class,
        ActiveApi.class,
        SwitchApi.class,
        UpdateApi.class
    }
)
public class App {
}
