package com.cps.cli.npmrcmanager.model;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record NpmrcProfile(
    String name,
    String path
) {
}
