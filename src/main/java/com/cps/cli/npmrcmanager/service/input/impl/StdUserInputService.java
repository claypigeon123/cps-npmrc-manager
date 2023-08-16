package com.cps.cli.npmrcmanager.service.input.impl;

import com.cps.cli.npmrcmanager.service.input.UserInputService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedInputStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Scanner;

@Service
public class StdUserInputService implements UserInputService {

    private Scanner scanner;

    @Override
    public boolean promptForYesOrNo(@NonNull String prompt) {
        String answer;

        do {
            System.out.printf("%n%s (Y/N): ", prompt);
            answer = scanner.nextLine();
        } while (!answer.equalsIgnoreCase("Y") && !answer.equalsIgnoreCase("N"));

        System.out.println();
        return answer.equalsIgnoreCase("Y");
    }

    @Override
    public Path promptForPath(@NonNull String prompt, @NonNull Path defaultValue) {
        Path answer;

        do {
            System.out.printf("%n%s [%s]: ", prompt, defaultValue.toAbsolutePath());
            try {
                String strAnswer = scanner.nextLine();

                if (!StringUtils.hasText(strAnswer)) {
                    answer = defaultValue;
                } else {
                    answer = Path.of(strAnswer);
                }
            } catch (InvalidPathException e) {
                answer = null;
            }
        } while (answer == null);

        System.out.println();
        return answer;
    }

    @Override
    public String promptForString(@NonNull String prompt, @NonNull String defaultValue) {
        String answer;

        do {
            System.out.printf("%n%s [%s]: ", prompt, defaultValue);
            answer = scanner.nextLine();

            if (!StringUtils.hasText(answer)) {
                answer = defaultValue;
            }
        } while (!StringUtils.hasText(answer));

        System.out.println();
        return answer;
    }

    // --

    @PostConstruct
    private void postConstruct() {
        scanner = new Scanner(new BufferedInputStream(System.in));
    }

    @PreDestroy
    private void preDestroy() {
        scanner.close();
    }
}
