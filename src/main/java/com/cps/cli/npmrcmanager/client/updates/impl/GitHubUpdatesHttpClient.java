package com.cps.cli.npmrcmanager.client.updates.impl;

import com.cps.cli.npmrcmanager.client.updates.UpdatesClient;
import com.cps.cli.npmrcmanager.model.github.response.GitHubRelease;
import com.cps.cli.npmrcmanager.model.github.response.GitHubReleaseAsset;
import com.cps.cli.npmrcmanager.model.system.SupportedOperatingSystem;
import com.cps.cli.npmrcmanager.util.InfoProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class GitHubUpdatesHttpClient implements UpdatesClient {

    private static final URI GITHUB_RELEASES_URI = URI.create("https://api.github.com/repos/claypigeon123/cps-npmrc-manager/releases");

    @NonNull
    private final HttpClient httpClient;

    @NonNull
    private final ObjectMapper objectMapper;

    @NonNull
    private final InfoProvider infoProvider;

    private List<GitHubRelease> cachedResponse;

    @Override
    public boolean isUpdateAvailable() {
        String currentVersion = infoProvider.getRawVersion();
        GitHubRelease latestRelease = getReleases().get(0);

        return currentVersion.equalsIgnoreCase(latestRelease.name());
    }

    @Override
    public Path downloadLatestRelease(@NonNull Path to, @NonNull SupportedOperatingSystem os) {
        GitHubRelease latestRelease = getReleases().get(0);
        GitHubReleaseAsset compatibleAsset = latestRelease.assets().stream()
            .filter(asset -> asset.downloadUrl().contains(os.getFilenameIncludes()))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("No compatible asset found in latest GitHub release"));

        return downloadAsset(to, compatibleAsset);
    }

    // --

    private List<GitHubRelease> getReleases() {
        if (cachedResponse != null) return cachedResponse;

        HttpRequest request = HttpRequest.newBuilder()
            .uri(GITHUB_RELEASES_URI)
            .build();

        HttpResponse<String> rawResponse = call(request, HttpResponse.BodyHandlers.ofString());
        List<GitHubRelease> response = mapResponse(rawResponse, new TypeReference<>() {});
        cachedResponse = response;
        return response;
    }

    private Path downloadAsset(Path to, GitHubReleaseAsset asset) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(asset.downloadUrl()))
            .build();

        HttpResponse<Path> rawResponse = call(request, HttpResponse.BodyHandlers.ofFileDownload(to, StandardOpenOption.CREATE, StandardOpenOption.WRITE));
        return rawResponse.body();
    }

    private <T> HttpResponse<T> call(HttpRequest request, HttpResponse.BodyHandler<T> bodyHandler) {
        HttpResponse<T> response;

        try {
            response = httpClient.send(request, bodyHandler);
        } catch (IOException e) {
            throw new UncheckedIOException(format("I/O error occurred calling %s %s: %s", request.method(), request.uri(), e.getMessage()), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(format("Thread interrupted: %s", e.getMessage()), e);
        }

        if (response.statusCode() == 200 || response.statusCode() == 201) return response;
        throw new IllegalStateException(format("%s %s failed with status code %s", request.method(), request.uri(), response.statusCode()));
    }

    private <T> T mapResponse(HttpResponse<String> rawResponse, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(rawResponse.body(), typeReference);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(format("I/O error while parsing response from GET %s: %s", GITHUB_RELEASES_URI, e.getMessage()), e);
        }
    }
}
