package com.cps.cli.npmrcmanager.util;

import com.cps.cli.npmrcmanager.config.props.InfoProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import picocli.CommandLine.IVersionProvider;

import static java.lang.String.format;

@Configuration
@RequiredArgsConstructor
public class PicocliProvider implements IVersionProvider {

    @NonNull
    private final InfoProperties infoProperties;

    public String getExecutableName() {
        return infoProperties.executableName();
    }

    @Override
    public String[] getVersion() {
        String name = infoProperties.name();
        String version = infoProperties.version().replaceFirst("-SNAPSHOT|-RC|-RELEASE", "");

        return new String[] {
            format("%s - version %s", name, version)
        };
    }
}
