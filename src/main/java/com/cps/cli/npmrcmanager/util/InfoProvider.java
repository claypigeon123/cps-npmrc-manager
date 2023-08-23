package com.cps.cli.npmrcmanager.util;

import com.cps.cli.npmrcmanager.config.props.InfoProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import picocli.CommandLine.IVersionProvider;

import static java.lang.String.format;

@Configuration
@RequiredArgsConstructor
public class InfoProvider implements IVersionProvider {

    @NonNull
    private final InfoProperties infoProperties;

    public String getExecutableName() {
        return infoProperties.executableName();
    }

    public String getRawVersion() {
        return infoProperties.version().replaceFirst("-SNAPSHOT|-RC|-RELEASE", "");
    }

    @Override
    public String[] getVersion() {
        String name = infoProperties.name();
        String version = getRawVersion();

        return new String[] {
            format("%s - version %s", name, version)
        };
    }
}
