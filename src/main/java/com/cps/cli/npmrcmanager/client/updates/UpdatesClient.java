package com.cps.cli.npmrcmanager.client.updates;

import com.cps.cli.npmrcmanager.model.system.SupportedOperatingSystem;
import lombok.NonNull;

import java.nio.file.Path;

public interface UpdatesClient {
    boolean isUpdateAvailable();

    Path downloadLatestRelease(@NonNull Path to, @NonNull SupportedOperatingSystem os);
}
