package com.cps.cli.npmrcmanager.model.github.response;

import com.cps.cli.npmrcmanager.model.ContentType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.springframework.aot.hint.annotation.Reflective;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Builder
@Reflective
public record GitHubReleaseAsset(
    String id,
    String name,
    @JsonProperty("content_type") ContentType contentType,
    GitHubReleaseState state,
    Long size,
    @JsonProperty("created_at") OffsetDateTime createdAt,
    @JsonProperty("updated_at") OffsetDateTime updatedAt,
    @JsonProperty("browser_download_url") String downloadUrl
) implements Serializable {
}
