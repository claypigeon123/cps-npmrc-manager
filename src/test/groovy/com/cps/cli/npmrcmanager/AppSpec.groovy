package com.cps.cli.npmrcmanager

import com.cps.cli.npmrcmanager.util.InfoProvider
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.system.OutputCaptureRule
import org.springframework.test.context.ContextConfiguration
import picocli.CommandLine
import picocli.CommandLine.ExitCode
import picocli.CommandLine.IFactory
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

import static java.lang.String.format

@ContextConfiguration
@RestoreSystemProperties
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = [
    "info.name=cps-npmrc-manager", "info.version=127.0.0.1-SNAPSHOT", "info.executable-name=npmrcm"
])
class AppSpec extends Specification {

    private static final String USAGE_MESSAGE = """\
    Usage: npmrcm [-hV] [COMMAND]
      -h, --help      Show this help message and exit.
      -V, --version   Print version information and exit.
    Commands:
      setup   -> Run guided setup of required configuration for this tool
      list    -> List configured .npmrc profiles
      active  -> Describe the current active profile
      switch  -> Switch to the specified .npmrc profile
      update  -> Check for updates and update if necessary
    """.stripIndent().replaceAll("\n", System.lineSeparator())

    @Rule
    OutputCaptureRule output = new OutputCaptureRule()

    @Autowired
    IFactory factory

    @Autowired
    InfoProvider picocliProvider

    CommandLine cmd

    void setup() {
        cmd = new CommandLine(new App(), factory).setCommandName(picocliProvider.getExecutableName())
    }

    def "general usage sanity test: #testCase"() {
        when:
        int exitCode = cmd.execute(args as String[])

        then:
        noExceptionThrown()
        exitCode == expectedCode
        output.out == format(expectedOutMessage)
        output.err == format(expectedErrMessage)

        where:
        testCase                | args          || expectedCode   | expectedOutMessage                        | expectedErrMessage
        "no subcommand"         | []            || ExitCode.USAGE | ""                                        | "Missing required subcommand%n$USAGE_MESSAGE"
        "help message short"    | ["-h"]        || ExitCode.OK    | USAGE_MESSAGE                             | ""
        "help message long"     | ["--help"]    || ExitCode.OK    | USAGE_MESSAGE                             | ""
        "version message short" | ["-V"]        || ExitCode.OK    | "cps-npmrc-manager - version 127.0.0.1%n" | ""
        "version message long"  | ["--version"] || ExitCode.OK    | "cps-npmrc-manager - version 127.0.0.1%n" | ""
    }
}
