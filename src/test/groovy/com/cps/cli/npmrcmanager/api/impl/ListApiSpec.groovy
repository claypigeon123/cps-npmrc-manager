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
class ListApiSpec extends Specification {

    @Rule
    OutputCaptureRule output = new OutputCaptureRule()

    ConfigurationService configurationService = Mock()

    CommandLine cmd

    void setup() {
        cmd = new CommandLine(new ListApi(configurationService))
    }

    def "list profiles"() {
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
        profiles                                                                                      || expectedOutMessage                        | expectedErrMessage
        []                                                                                            || ""                                        | "No profiles are configured%n"
        [new NpmrcProfile("npm-central", "/", true)]                                                  || "---> npm-central%n"                      | ""
        [new NpmrcProfile("npm-central", "/", true), new NpmrcProfile("custom-profile", "/", false)]  || "---> npm-central%n     custom-profile%n" | ""
        [new NpmrcProfile("npm-central", "/", false), new NpmrcProfile("custom-profile", "/", true)]  || "     npm-central%n---> custom-profile%n" | ""
        [new NpmrcProfile("npm-central", "/", true), new NpmrcProfile("custom-profile", "/", true)]   || "---> npm-central%n---> custom-profile%n" | ""
        [new NpmrcProfile("npm-central", "/", false)]                                                 || "     npm-central%n"                      | ""
        [new NpmrcProfile("npm-central", "/", false), new NpmrcProfile("custom-profile", "/", false)] || "     npm-central%n     custom-profile%n" | ""
    }

    def "list profiles - verbose"() {
        given:
        NpmrcmConfiguration configuration = NpmrcmConfiguration.builder()
            .npmrcPath("/home/tester/.npmrc")
            .profiles([new NpmrcProfile("npm-central", "/", true), new NpmrcProfile("custom-profile", "/", false)])
            .build()
        String expectedMessage = """\
        Listing .npmrc profiles:
        
        # profile name:         npm-central
        # persistent path:      /
        # active?               yes
        
        # profile name:         custom-profile
        # persistent path:      /
        # active?               no
        """.stripIndent().replaceAll("\n", System.lineSeparator())

        when:
        int exitCode = cmd.execute(args)

        then:
        1 * configurationService.load() >> configuration

        noExceptionThrown()
        exitCode == ExitCode.OK
        output.out == format(expectedMessage)
        output.err == ""

        where:
        args                      | _
        ["--verbose"] as String[] | _
        ["-v"] as String[]        | _
    }

    def "list profiles - app error"() {
        when:
        int exitCode = cmd.execute()

        then:
        1 * configurationService.load() >> { throw new IllegalStateException("Something went wrong") }

        noExceptionThrown()
        exitCode == ExitCode.SOFTWARE
        output.out == ""
        output.err == format("Something went wrong%n")

        where:
        args                      | _
        ["--verbose"] as String[] | _
        ["-v"] as String[]        | _
    }
}
