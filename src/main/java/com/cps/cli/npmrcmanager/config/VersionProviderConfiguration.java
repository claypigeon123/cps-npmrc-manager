package com.cps.cli.npmrcmanager.config;

import com.cps.cli.npmrcmanager.config.props.InfoProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import picocli.CommandLine.IVersionProvider;

import static java.lang.String.format;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(InfoProperties.class)
public class VersionProviderConfiguration implements IVersionProvider {

    @NonNull
    private final InfoProperties infoProperties;

    @Override
    public String[] getVersion() {
        String name = infoProperties.name();
        String version = infoProperties.version().split("-SNAPSHOT")[0];

        return new String[] {
            format("%s - version %s", name, version)
        };
    }
}
