package com.cps.cli.npmrcmanager.config;

import com.cps.cli.npmrcmanager.config.props.InfoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
@EnableConfigurationProperties(InfoProperties.class)
public class GeneralConfiguration {

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10L))
            .build();
    }
}
