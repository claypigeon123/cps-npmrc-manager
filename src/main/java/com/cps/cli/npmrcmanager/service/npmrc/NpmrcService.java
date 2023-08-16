package com.cps.cli.npmrcmanager.service.npmrc;

import com.cps.cli.npmrcmanager.model.NpmrcProfile;
import lombok.NonNull;

import java.io.File;

public interface NpmrcService {
    NpmrcProfile recordExistingNpmrcIntoProfile(@NonNull File npmrcFile, @NonNull String profilesLocation);

    NpmrcProfile recordNewNpmrcForCentralRegistryIntoProfile(@NonNull File npmrcFile, @NonNull String profilesLocation);

    void switchToProfile(@NonNull NpmrcProfile profile, @NonNull String npmrcLocation);
}
