package com.cps.cli.npmrcmanager.api.impl

import com.cps.cli.npmrcmanager.model.NpmrcProfile
import com.cps.cli.npmrcmanager.model.NpmrcmConfiguration
import com.cps.cli.npmrcmanager.service.configuration.ConfigurationService
import com.cps.cli.npmrcmanager.service.npmrc.NpmrcService
import org.junit.Rule
import org.springframework.boot.test.system.OutputCaptureRule
import picocli.CommandLine
import picocli.CommandLine.ExitCode
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

import static java.lang.String.format

@RestoreSystemProperties
class SwitchApiSpec extends Specification {

    @Rule
    OutputCaptureRule output = new OutputCaptureRule()

    ConfigurationService configurationService = Mock()
    NpmrcService npmrcService = Mock()

    CommandLine cmd

    void setup() {
        cmd = new CommandLine(new SwitchApi(configurationService, npmrcService))
    }

    def "switch profile"() {
        given:
        String targetProfileName = "custom-profile"
        NpmrcmConfiguration configuration = NpmrcmConfiguration.builder()
            .npmrcPath("/home/tester/.npmrc")
            .profiles([new NpmrcProfile("npm-central", "/", true), new NpmrcProfile("custom-profile", "/", false)])
            .build()

        when:
        int exitCode = cmd.execute(targetProfileName)

        then:
        1 * configurationService.load() >> configuration
        1 * npmrcService.switchToProfile(configuration, targetProfileName)

        noExceptionThrown()
        exitCode == ExitCode.OK
        output.out == format("Switched to profile [$targetProfileName]%n")
        output.err == ""
    }

    def "switch profile - no target specified"() {
        when:
        int exitCode = cmd.execute()

        then:
        0 * configurationService.load()
        0 * npmrcService.switchToProfile(_, _)

        noExceptionThrown()
        exitCode == ExitCode.USAGE
        output.out == ""
        output.err.readLines().get(0) == "Missing required parameter: '<target profile>'"
    }

    def "switch profile - app error"() {
        when:
        int exitCode = cmd.execute("profile")

        then:
        1 * configurationService.load() >> { throw new IllegalArgumentException("Something went wrong") }
        0 * npmrcService.switchToProfile(_, _)

        noExceptionThrown()
        exitCode == ExitCode.SOFTWARE
        output.out == ""
        output.err == format("Something went wrong%n")
    }
}
