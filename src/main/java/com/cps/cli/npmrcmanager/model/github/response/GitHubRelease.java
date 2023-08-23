package com.cps.cli.npmrcmanager.model.github.response;

import lombok.Builder;
import org.springframework.aot.hint.annotation.Reflective;

import java.io.Serializable;
import java.util.List;

@Builder
@Reflective
public record GitHubRelease(
    String id,
    String url,
    String name,
    List<GitHubReleaseAsset> assets
) implements Serializable {
}
