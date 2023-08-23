package com.cps.cli.npmrcmanager.config;

import com.cps.cli.npmrcmanager.model.ContentType;
import com.cps.cli.npmrcmanager.model.NpmrcProfile;
import com.cps.cli.npmrcmanager.model.NpmrcmConfiguration;
import com.cps.cli.npmrcmanager.model.github.response.GitHubRelease;
import com.cps.cli.npmrcmanager.model.github.response.GitHubReleaseAsset;
import com.cps.cli.npmrcmanager.model.github.response.GitHubReleaseState;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;

@Configuration
@RegisterReflectionForBinding({
    NpmrcmConfiguration.class, NpmrcProfile.class,
    GitHubRelease.class, GitHubReleaseAsset.class, GitHubReleaseState.class, ContentType.class
})
public class NativeConfiguration {
}
