package com.cps.cli.npmrcmanager.api.impl

import com.cps.cli.npmrcmanager.model.NpmrcProfile
import com.cps.cli.npmrcmanager.model.NpmrcmConfiguration
import com.cps.cli.npmrcmanager.service.configuration.ConfigurationService
import org.junit.Rule
import org.springframework.boot.test.system.OutputCaptureRule
import picocli.CommandLine
import picocli.CommandLine.ExitCode
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

import static java.lang.String.format

@RestoreSystemProperties
class ActiveApiSpec extends Specification {

    @Rule
    OutputCaptureRule output = new OutputCaptureRule()

    ConfigurationService configurationService = Mock()

    CommandLine cmd

    void setup() {
        cmd = new CommandLine(new ActiveApi(configurationService))
    }

    def "check active profile"() {
        given:
        NpmrcmConfiguration configuration = NpmrcmConfiguration.builder()
            .npmrcPath("/home/tester/.npmrc")
            .profiles(profiles)
            .build()

        when:
        int exitCode = cmd.execute()

        then:
        1 * configurationService.load() >> configuration

        noExceptionThrown()
        exitCode == ExitCode.OK
        output.out == format(expectedOutMessage)
        output.err == format(expectedErrMessage)

        where:
        profiles                                                                                      || expectedOutMessage              | expectedErrMessage
        [new NpmrcProfile("npm-central", "/", true)]                                                  || "npm-central%n"                 | ""
        [new NpmrcProfile("npm-central", "/", true), new NpmrcProfile("custom-profile", "/", false)]  || "npm-central%n"                 | ""
        [new NpmrcProfile("npm-central", "/", false), new NpmrcProfile("custom-profile", "/", true)]  || "custom-profile%n"              | ""
        [new NpmrcProfile("npm-central", "/", true), new NpmrcProfile("custom-profile", "/", true)]   || "npm-central, custom-profile%n" | ""
        [new NpmrcProfile("npm-central", "/", false)]                                                 || ""                              | "None of the configured profiles are active%n"
        [new NpmrcProfile("npm-central", "/", false), new NpmrcProfile("custom-profile", "/", false)] || ""                              | "None of the configured profiles are active%n"
    }

    def "check active profile - app error"() {
        when:
        int exitCode = cmd.execute()

        then:
        1 * configurationService.load() >> { throw new IllegalStateException("Something went wrong") }

        noExceptionThrown()
        exitCode == ExitCode.SOFTWARE
        output.out == ""
        output.err == format("Something went wrong%n")
    }
}
