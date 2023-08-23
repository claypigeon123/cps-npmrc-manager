package com.cps.cli.npmrcmanager.model.system;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum SupportedOperatingSystem {
    LINUX_X86_64(Set.of("nix", "nux", "aix"), "amd64", "linux_x86-64"),
    LINUX_ARM64(Set.of("nix", "nux", "aix"), "aarch64", "linux_arm64"),
    WIN_X86_64(Set.of("win"), "amd64", "win_x86-64"),
    MAC_OS_ARM64(Set.of("mac"), "aarch64", "macos_arm64");

    private final Set<String> osContainsPatterns;
    private final String arch;
    private final String filenameIncludes;
}
