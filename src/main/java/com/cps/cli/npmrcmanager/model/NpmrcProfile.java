package com.cps.cli.npmrcmanager.model;

import lombok.Builder;
import org.springframework.aot.hint.annotation.Reflective;

import java.io.Serializable;

@Builder
@Reflective // for native usage
public record NpmrcProfile (String name, String path, boolean active) implements Serializable {
}
