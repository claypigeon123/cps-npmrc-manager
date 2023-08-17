package com.cps.cli.npmrcmanager.service.input;

import lombok.NonNull;

import java.nio.file.Path;

public interface UserInputService {
    boolean promptForYesOrNo(@NonNull String prompt);

    Path promptForPath(@NonNull String prompt, @NonNull Path defaultValue);

    String promptForString(@NonNull String prompt, @NonNull String defaultValue);
}
