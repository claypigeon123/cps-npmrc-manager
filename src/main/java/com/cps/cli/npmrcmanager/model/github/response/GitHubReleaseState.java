package com.cps.cli.npmrcmanager.model.github.response;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GitHubReleaseState {
    UPLOADED("uploaded"),
    OPEN("open");

    @JsonValue
    private final String value;
}
