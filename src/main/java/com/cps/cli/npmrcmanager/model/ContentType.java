package com.cps.cli.npmrcmanager.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContentType {
    APPLICATION_TAR_GZ("application/x-gzip"),
    APPLICATION_ZIP("application/x-zip"),
    APPLICATION_ZIP_COMPRESSED("application/x-zip-compressed");

    @JsonValue
    private final String value;
}
