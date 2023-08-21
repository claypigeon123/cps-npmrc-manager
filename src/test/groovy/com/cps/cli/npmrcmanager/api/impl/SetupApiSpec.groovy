package com.cps.cli.npmrcmanager.api.impl


import com.cps.cli.npmrcmanager.service.configuration.ConfigurationService
import com.cps.cli.npmrcmanager.service.input.UserInputService
import org.junit.Rule
import org.springframework.boot.test.system.OutputCaptureRule
import picocli.CommandLine
import picocli.CommandLine.ExitCode
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

import static java.lang.String.format

@RestoreSystemProperties
class SetupApiSpec extends Specification {

    @Rule
    OutputCaptureRule output = new OutputCaptureRule()

    UserInputService userInputService = Mock()
    ConfigurationService configurationService = Mock()

    CommandLine cmd

    void setup() {
        cmd = new CommandLine(new SetupApi(userInputService, configurationService))
    }

    def "setup configuration - config exists and user terminates setup"() {
        when:
        int exitCode = cmd.execute()

        then:
        1 * configurationService.exists() >> true
        1 * userInputService.promptForYesOrNo(_) >> false
        0 * configurationService.setup()

        noExceptionThrown()
        exitCode == ExitCode.SOFTWARE
        output.out == format("Initializing guided setup%n")
        output.err == format("Terminated by user%n")
    }

    def "setup configuration - config exists and user proceeds anyway"() {
        given:
        String expectedOutMessage = """\
        Initializing guided setup
        Starting guided setup
        Some answers have defaults in [square brackets] - just press enter to accept these as is.
        Configuration complete
        """.stripIndent().replaceAll("\n", System.lineSeparator())

        when:
        int exitCode = cmd.execute()

        then:
        1 * configurationService.exists() >> true
        1 * userInputService.promptForYesOrNo(_) >> true
        1 * configurationService.setup()

        noExceptionThrown()
        exitCode == ExitCode.OK
        output.out == expectedOutMessage
        output.err == ""
    }

    def "setup configuration - new config"() {
        given:
        String expectedOutMessage = """\
        Initializing guided setup
        Starting guided setup
        Some answers have defaults in [square brackets] - just press enter to accept these as is.
        Configuration complete
        """.stripIndent().replaceAll("\n", System.lineSeparator())

        when:
        int exitCode = cmd.execute()

        then:
        1 * configurationService.exists() >> false
        0 * userInputService.promptForYesOrNo(_)
        1 * configurationService.setup()

        noExceptionThrown()
        exitCode == ExitCode.OK
        output.out == expectedOutMessage
        output.err == ""
    }

    def "setup configuration - app error"() {
        given:
        String expectedOutMessage = """\
        Initializing guided setup
        Starting guided setup
        Some answers have defaults in [square brackets] - just press enter to accept these as is.
        """.stripIndent().replaceAll("\n", System.lineSeparator())

        when:
        int exitCode = cmd.execute()

        then:
        1 * configurationService.exists() >> false
        0 * userInputService.promptForYesOrNo(_)
        1 * configurationService.setup() >> { throw new IllegalStateException("Something went wrong") }

        noExceptionThrown()
        exitCode == ExitCode.SOFTWARE
        output.out == expectedOutMessage
        output.err == format("Something went wrong%n")
    }
}
