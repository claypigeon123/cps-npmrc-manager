package com.cps.cli.npmrcmanager.service.npmrc.impl

import com.cps.cli.npmrcmanager.model.NpmrcProfile
import com.cps.cli.npmrcmanager.model.NpmrcmConfiguration
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

    def "switch to profile - non-existent"() {
        given:
        String targetProfileName = "I don't exist"
        Path npmrcPath = Path.of("user", "home", ".npmrc").toAbsolutePath()
        NpmrcmConfiguration configuration = NpmrcmConfiguration.builder()
            .npmrcPath(npmrcPath.toString())
            .profiles([
                NpmrcProfile.builder()
                    .name("test-profile-1")
                    .path(Path.of("user", "home", ".npmrcm", "profiles", "test-profile-1").toAbsolutePath().toString())
                    .active(true)
                    .build(),
                NpmrcProfile.builder()
                    .name("test-profile-2")
                    .path(Path.of("user", "home", ".npmrcm", "profiles", "test-profile-2").toAbsolutePath().toString())
                    .active(false)
                    .build()
            ])
            .build()

        when:
        npmrcService.switchToProfile(configuration, targetProfileName)

        then:
        0 * filesystemHelper.copy(_, _)

        thrown(IllegalStateException)
    }

    def "switch to profile - already active"() {
        given:
        String targetProfileName = "test-profile-2"
        Path npmrcPath = Path.of("user", "home", ".npmrc").toAbsolutePath()
        NpmrcmConfiguration configuration = NpmrcmConfiguration.builder()
            .npmrcPath(npmrcPath.toString())
            .profiles([
                NpmrcProfile.builder()
                    .name("test-profile-1")
                    .path(Path.of("user", "home", ".npmrcm", "profiles", "test-profile-1").toAbsolutePath().toString())
                    .active(false)
                    .build(),
                NpmrcProfile.builder()
                    .name("test-profile-2")
                    .path(Path.of("user", "home", ".npmrcm", "profiles", "test-profile-2").toAbsolutePath().toString())
                    .active(true)
                    .build()
            ])
            .build()

        when:
        npmrcService.switchToProfile(configuration, targetProfileName)

        then:
        0 * filesystemHelper.copy(_, _)

        thrown(IllegalStateException)
    }

    def "switch to profile"() {
        given:
        Path npmrcPath = Path.of("user", "home", ".npmrc").toAbsolutePath()
        NpmrcmConfiguration configuration = NpmrcmConfiguration.builder()
            .npmrcPath(npmrcPath.toString())
            .profiles([
                NpmrcProfile.builder()
                    .name("test-profile-1")
                    .path(Path.of("user", "home", ".npmrcm", "profiles", "test-profile-1").toAbsolutePath().toString())
                    .active(false)
                    .build(),
                NpmrcProfile.builder()
                    .name("test-profile-2")
                    .path(Path.of("user", "home", ".npmrcm", "profiles", "test-profile-2").toAbsolutePath().toString())
                    .active(false)
                    .build()
            ])
            .build()

        when:
        npmrcService.switchToProfile(configuration, targetProfileName)

        then:
        1 * filesystemHelper.copy(Path.of(expectedProfile.path()).toAbsolutePath(), Path.of(npmrcPath.toString()).toAbsolutePath())

        noExceptionThrown()

        where:
        targetProfileName || expectedProfile
        "test-profile-1"  || new NpmrcProfile("test-profile-1", Path.of("user", "home", ".npmrcm", "profiles", "test-profile-1").toAbsolutePath().toString(), false)
        "test-profile-2"  || new NpmrcProfile("test-profile-2", Path.of("user", "home", ".npmrcm", "profiles", "test-profile-2").toAbsolutePath().toString(), false)
    }
}
