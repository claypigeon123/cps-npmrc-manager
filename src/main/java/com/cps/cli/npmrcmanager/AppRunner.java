package com.cps.cli.npmrcmanager;

import com.cps.cli.npmrcmanager.util.InfoProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

@SpringBootApplication
@RequiredArgsConstructor
public class AppRunner implements CommandLineRunner, ExitCodeGenerator {

    @NonNull
    private final App app;

    @NonNull
    private final IFactory factory;

    @NonNull
    private final InfoProvider infoProvider;

    private int exitCode = 0;

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(AppRunner.class, args)));
    }

    @Override
    public void run(String... args) {
        exitCode = new CommandLine(app, factory)
            .setCommandName(infoProvider.getExecutableName())
            .execute(args);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
