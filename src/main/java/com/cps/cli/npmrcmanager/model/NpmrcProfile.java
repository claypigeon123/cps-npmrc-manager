package com.cps.cli.npmrcmanager.model;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.aot.hint.annotation.Reflective;

import java.io.Serializable;

@Builder
@Reflective // for native usage
@Jacksonized
public record NpmrcProfile (String name, String path) implements Serializable {
}
