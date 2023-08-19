package com.cps.cli.npmrcmanager.service.configuration;

import com.cps.cli.npmrcmanager.model.NpmrcmConfiguration;
import lombok.NonNull;

public interface ConfigurationService {
    void setup();

    NpmrcmConfiguration load();

    boolean exists();

    void save(@NonNull NpmrcmConfiguration configuration);

    void remove();
}
