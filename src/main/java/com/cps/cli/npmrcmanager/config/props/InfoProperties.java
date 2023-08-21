package com.cps.cli.npmrcmanager.config.props;

import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Builder
@ConfigurationProperties("info")
public record InfoProperties(
    String name,
    String version
) {
}
