package com.cps.cli.npmrcmanager.api;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import picocli.CommandLine.IExitCodeGenerator;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public abstract class Api implements Runnable, IExitCodeGenerator {

    protected int exitCode = 0;

    protected void initialize() {}

    protected abstract void start();

    protected void close() {}

    @Override
    public final void run() {
        try {
            initialize();
            start();
            close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            exitCode = 1;
        }
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
