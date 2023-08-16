package com.cps.cli.npmrcmanager.service.configuration;

import com.cps.cli.npmrcmanager.model.Configuration;

public interface ConfigurationService {
    String getConfigLocation();

    String getProfilesLocation();

    Configuration load();

    boolean exists();

    void save(Configuration configuration);

    void remove();
}
