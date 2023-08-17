package com.cps.cli.npmrcmanager.service.npmrc;

import com.cps.cli.npmrcmanager.model.NpmrcProfile;
import lombok.NonNull;

public interface NpmrcService {
    NpmrcProfile recordExistingNpmrcIntoProfile(@NonNull String npmrcLocation, @NonNull String profilesLocation);

    NpmrcProfile recordNewNpmrcForCentralRegistryIntoProfile(@NonNull String npmrcLocation, @NonNull String profilesLocation);

    void switchToProfile(@NonNull String npmrcLocation, @NonNull NpmrcProfile profile);
}
