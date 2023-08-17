package com.cps.cli.npmrcmanager.service.configuration;

import com.cps.cli.npmrcmanager.model.Configuration;
import lombok.NonNull;

public interface ConfigurationService {
    void setup();

    Configuration load();

    boolean exists();

    void save(@NonNull Configuration configuration);

    void remove();
}
