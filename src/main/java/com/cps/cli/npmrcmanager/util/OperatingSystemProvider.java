package com.cps.cli.npmrcmanager.util;

import com.cps.cli.npmrcmanager.model.system.SupportedOperatingSystem;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
public class OperatingSystemProvider {

    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final String ARCH = System.getProperty("os.arch").toLowerCase();

    public SupportedOperatingSystem getOperatingSystem() {
        for (SupportedOperatingSystem supportedOs : SupportedOperatingSystem.values()) {
            for (String pattern : supportedOs.getOsContainsPatterns()) {
                if (OS.contains(pattern) && ARCH.contains(supportedOs.getArch())) {
                    return supportedOs;
                }
            }
        }

        throw new IllegalStateException(format("Running on an unsupported operating system / architecture: %s/%s", OS, ARCH));
    }

    // --
}
