package com.cps.cli.npmrcmanager.api;

import picocli.CommandLine.ExitCode;
import picocli.CommandLine.IExitCodeGenerator;

public abstract class Api implements Runnable, IExitCodeGenerator {

    protected int exitCode = ExitCode.OK;

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
            exitCode = ExitCode.SOFTWARE;
        }
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
