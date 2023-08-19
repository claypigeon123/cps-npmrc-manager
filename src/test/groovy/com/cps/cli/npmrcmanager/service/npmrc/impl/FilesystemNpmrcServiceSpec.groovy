package com.cps.cli.npmrcmanager.service.npmrc.impl

import com.cps.cli.npmrcmanager.model.NpmrcProfile
import com.cps.cli.npmrcmanager.service.input.UserInputService
import com.cps.cli.npmrcmanager.service.npmrc.NpmrcService
import com.cps.cli.npmrcmanager.util.FilesystemHelper
import spock.lang.Specification

import java.nio.file.Path

class FilesystemNpmrcServiceSpec extends Specification {

    // dependencies
    UserInputService userInputService = Mock()
    FilesystemHelper filesystemHelper = Mock()

    // tested class
    NpmrcService npmrcService

    void setup() {
        npmrcService = new FilesystemNpmrcService(userInputService, filesystemHelper)
    }

    def "record existing npmrc into profile"() {
        given:
        String npmrcLocation = ".npmrc"
        String profilesLocation = "profiles"
        String profileName = "test-config"

        when:
        NpmrcProfile result = npmrcService.recordExistingNpmrcIntoProfile(npmrcLocation, profilesLocation)

        then:
        1 * userInputService.promptForString(_, _) >> profileName
        1 * filesystemHelper.copy(Path.of(npmrcLocation).toAbsolutePath(), Path.of(profilesLocation, profileName).toAbsolutePath())

        noExceptionThrown()
        result.name() == profileName
        result.path() == Path.of(profilesLocation, profileName).toAbsolutePath().toString()
    }

    def "record new npmrc for central registry into profile"() {
        given:
        String npmrcLocation = ".npmrc"
        String profilesLocation = "profiles"
        String expectedProfileName = "npm-central"

        when:
        NpmrcProfile result = npmrcService.recordNewNpmrcForCentralRegistryIntoProfile(npmrcLocation, profilesLocation)

        then:
        1 * filesystemHelper.write(Path.of(npmrcLocation).toAbsolutePath(), "registry=https://registry.npmjs.org/")
        1 * filesystemHelper.copy(Path.of(npmrcLocation).toAbsolutePath(), Path.of(profilesLocation, expectedProfileName).toAbsolutePath())

        noExceptionThrown()
        result.name() == expectedProfileName
        result.path() == Path.of(profilesLocation, expectedProfileName).toAbsolutePath().toString()
    }

    def "switch to profile"() {
        given:
        String npmrcLocation = ".npmrc"
        NpmrcProfile targetProfile = new NpmrcProfile("test-profile", ".npmrcm/profiles/test-profile")

        when:
        npmrcService.switchToProfile(npmrcLocation, targetProfile)

        then:
        1 * filesystemHelper.copy(Path.of(targetProfile.path()).toAbsolutePath(), Path.of(npmrcLocation).toAbsolutePath())

        noExceptionThrown()
    }
}
