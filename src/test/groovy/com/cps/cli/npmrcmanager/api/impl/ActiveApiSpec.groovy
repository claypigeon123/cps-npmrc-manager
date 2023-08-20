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
        output.out.trim() == expectedOutMessage
        output.err.trim() == expectedErrMessage

        where:
        profiles                                                                                      || expectedOutMessage            | expectedErrMessage
        [new NpmrcProfile("npm-central", "/", true)]                                                  || "npm-central"                 | ""
        [new NpmrcProfile("npm-central", "/", true), new NpmrcProfile("custom-profile", "/", false)]  || "npm-central"                 | ""
        [new NpmrcProfile("npm-central", "/", false), new NpmrcProfile("custom-profile", "/", true)]  || "custom-profile"              | ""
        [new NpmrcProfile("npm-central", "/", true), new NpmrcProfile("custom-profile", "/", true)]   || "npm-central, custom-profile" | ""
        [new NpmrcProfile("npm-central", "/", false)]                                                 || ""                            | "None of the configured profiles are active"
        [new NpmrcProfile("npm-central", "/", false), new NpmrcProfile("custom-profile", "/", false)] || ""                            | "None of the configured profiles are active"
    }
}
