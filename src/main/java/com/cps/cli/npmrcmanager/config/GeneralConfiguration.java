package com.cps.cli.npmrcmanager.config;

import com.cps.cli.npmrcmanager.config.props.InfoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(InfoProperties.class)
public class GeneralConfiguration {
}
