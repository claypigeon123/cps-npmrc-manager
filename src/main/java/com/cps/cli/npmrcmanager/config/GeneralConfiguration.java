package com.cps.cli.npmrcmanager.config;

import com.cps.cli.npmrcmanager.config.props.InfoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.Yaml;

@Configuration
@EnableConfigurationProperties(InfoProperties.class)
public class GeneralConfiguration {

    @Bean
    public Yaml yaml() {
        return new Yaml();
    }
}
