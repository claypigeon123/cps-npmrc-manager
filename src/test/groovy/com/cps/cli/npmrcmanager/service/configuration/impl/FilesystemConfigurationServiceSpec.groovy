package com.cps.cli.npmrcmanager.service.configuration.impl

import com.cps.cli.npmrcmanager.model.NpmrcProfile
import com.cps.cli.npmrcmanager.model.NpmrcmConfiguration
import com.cps.cli.npmrcmanager.service.configuration.ConfigurationService
import com.cps.cli.npmrcmanager.service.input.UserInputService
import com.cps.cli.npmrcmanager.service.npmrc.NpmrcService
import com.cps.cli.npmrcmanager.util.FilesystemHelper
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import spock.lang.Specification

import java.nio.file.Path
import java.util.stream.Stream

import static java.lang.String.format

class FilesystemConfigurationServiceSpec extends Specification {

    private static final Path USER_HOME_PATH = Path.of("home", "tester")
    private static final Path DEFAULT_NPMRC_PATH = USER_HOME_PATH.resolve(".npmrc")
    private static final Path APP_HOME_PATH = USER_HOME_PATH.resolve(".npmrcm")
    private static final Path APP_PROFILES_FOLDER_PATH = APP_HOME_PATH.resolve("profiles")
    private static final Path APP_CONFIG_JSON_PATH = APP_HOME_PATH.resolve("config.json")

    // dependencies
    ObjectWriter prettyPrinter = Spy(new ObjectMapper().writerWithDefaultPrettyPrinter())
    ObjectMapper objectMapper = Spy() {
        writerWithDefaultPrettyPrinter() >> prettyPrinter
    }
    UserInputService userInputService = Mock()
    NpmrcService npmrcService = Mock()
    FilesystemHelper filesystemHelper = Mock() {
        getDefaultNpmrcPath() >> DEFAULT_NPMRC_PATH
        getProfilesFolder() >> APP_PROFILES_FOLDER_PATH
        getConfigJsonFilePath() >> APP_CONFIG_JSON_PATH
    }

    // tested class
    ConfigurationService configurationService

    void setup() {
        configurationService = new FilesystemConfigurationService(objectMapper, userInputService, npmrcService, filesystemHelper)
    }

    def "setup config with existing npmrc: [#existingNpmrc]"() {
        given:
        NpmrcProfile profile = new NpmrcProfile("npm-central", APP_PROFILES_FOLDER_PATH.resolve("npm-central").toAbsolutePath().toString(), true)
        NpmrcmConfiguration expectedConfig = NpmrcmConfiguration.builder()
            .npmrcPath(DEFAULT_NPMRC_PATH.toAbsolutePath().toString())
            .profiles([profile])
            .build()

        when:
        configurationService.setup()

        then:
        1 * userInputService.promptForPath(_, _) >> DEFAULT_NPMRC_PATH
        1 * filesystemHelper.exists(DEFAULT_NPMRC_PATH, false) >> existingNpmrc
        expectedCallsForNew * npmrcService.recordNewNpmrcForCentralRegistryIntoProfile(DEFAULT_NPMRC_PATH.toString(), APP_PROFILES_FOLDER_PATH.toAbsolutePath().toString()) >> profile
        expectedCallsForExisting * npmrcService.recordExistingNpmrcIntoProfile(DEFAULT_NPMRC_PATH.toString(), APP_PROFILES_FOLDER_PATH.toAbsolutePath().toString()) >> profile
        1 * prettyPrinter.writeValueAsString(expectedConfig) >> expectedConfig.toString()
        1 * filesystemHelper.write(APP_CONFIG_JSON_PATH, expectedConfig.toString())

        noExceptionThrown()

        where:
        existingNpmrc || expectedCallsForNew | expectedCallsForExisting
        true          || 0                   | 1
        false         || 1                   | 0
    }

    def "load config - no config file"() {
        when:
        configurationService.load()

        then:
        1 * filesystemHelper.read(APP_CONFIG_JSON_PATH) >> { throw new UncheckedIOException("io-err", new IOException("io-err")) }

        thrown(IllegalStateException)
    }

    def "load config - error reading config file"() {
        given:
        String malformedConfigFileContents = "{{{{{{\"}"

        when:
        configurationService.load()

        then:
        1 * filesystemHelper.read(APP_CONFIG_JSON_PATH) >> malformedConfigFileContents
        1 * objectMapper.readValue(malformedConfigFileContents, NpmrcmConfiguration)

        thrown(IllegalStateException)
    }

    def "load config - error reading npmrc"() {
        given:
        String configFileContents = "{ \"npmrcPath\": \"${DEFAULT_NPMRC_PATH.toAbsolutePath().toString().replace("\\", "\\\\")}\" }"
        String npmrcFileContents = "registry=https://registry.npmjs.org/"

        and:
        NpmrcProfile npmCentralProfile = NpmrcProfile.builder()
            .name("npm-central")
            .path(APP_PROFILES_FOLDER_PATH.resolve("npm-central").toAbsolutePath().toString())
            .active(false)
            .build()
        String npmCentralProfileContents = "$npmrcFileContents"
        NpmrcProfile customProfile = NpmrcProfile.builder()
            .name("custom-profile")
            .path(APP_PROFILES_FOLDER_PATH.resolve("custom-profile").toAbsolutePath().toString())
            .active(false)
            .build()
        String customProfileContents = format("registry=https://some-other-reg.localhost/%n//some-other-reg.localhost/:_auth=asdf")

        when:
        NpmrcmConfiguration result = configurationService.load()

        then:
        1 * filesystemHelper.read(APP_CONFIG_JSON_PATH) >> configFileContents
        1 * objectMapper.readValue(configFileContents, NpmrcmConfiguration)
        1 * filesystemHelper.read(DEFAULT_NPMRC_PATH.toAbsolutePath()) >> { throw new UncheckedIOException("io-err", new IOException("io-err")) }
        1 * filesystemHelper.list(APP_PROFILES_FOLDER_PATH) >> Stream.of(Path.of(npmCentralProfile.path()), Path.of(customProfile.path()))
        1 * filesystemHelper.exists(Path.of(npmCentralProfile.path()), false) >> true
        1 * filesystemHelper.read(Path.of(npmCentralProfile.path())) >> npmCentralProfileContents
        1 * filesystemHelper.exists(Path.of(customProfile.path()), false) >> true
        1 * filesystemHelper.read(Path.of(customProfile.path())) >> customProfileContents

        noExceptionThrown()
        result.npmrcPath == DEFAULT_NPMRC_PATH.toAbsolutePath().toString()
        result.profiles == [npmCentralProfile, customProfile]
    }

    def "load config - a profile is a directory"() {
        given:
        String configFileContents = "{ \"npmrcPath\": \"${DEFAULT_NPMRC_PATH.toAbsolutePath().toString().replace("\\", "\\\\")}\" }"
        String npmrcFileContents = "registry=https://registry.npmjs.org/"

        and:
        NpmrcProfile npmCentralProfile = NpmrcProfile.builder()
            .name("npm-central")
            .path(APP_PROFILES_FOLDER_PATH.resolve("npm-central").toAbsolutePath().toString())
            .active(true)
            .build()
        String npmCentralProfileContents = "$npmrcFileContents"
        NpmrcProfile customProfile = NpmrcProfile.builder()
            .name("custom-profile")
            .path(APP_PROFILES_FOLDER_PATH.resolve("custom-profile").toAbsolutePath().toString())
            .active(false)
            .build()

        when:
        NpmrcmConfiguration result = configurationService.load()

        then:
        1 * filesystemHelper.read(APP_CONFIG_JSON_PATH) >> configFileContents
        1 * objectMapper.readValue(configFileContents, NpmrcmConfiguration)
        1 * filesystemHelper.read(DEFAULT_NPMRC_PATH.toAbsolutePath()) >> npmrcFileContents
        1 * filesystemHelper.list(APP_PROFILES_FOLDER_PATH) >> Stream.of(Path.of(npmCentralProfile.path()), Path.of(customProfile.path()))
        1 * filesystemHelper.exists(Path.of(npmCentralProfile.path()), false) >> true
        1 * filesystemHelper.read(Path.of(npmCentralProfile.path())) >> npmCentralProfileContents
        1 * filesystemHelper.exists(Path.of(customProfile.path()), false) >> false

        noExceptionThrown()
        result.npmrcPath == DEFAULT_NPMRC_PATH.toAbsolutePath().toString()
        result.profiles == [npmCentralProfile]
    }

    def "load config"() {
        given:
        String configFileContents = "{ \"npmrcPath\": \"${DEFAULT_NPMRC_PATH.toAbsolutePath().toString().replace("\\", "\\\\")}\" }"
        String npmrcFileContents = "registry=https://registry.npmjs.org/"

        and:
        NpmrcProfile npmCentralProfile = NpmrcProfile.builder()
            .name("npm-central")
            .path(APP_PROFILES_FOLDER_PATH.resolve("npm-central").toAbsolutePath().toString())
            .active(true)
            .build()
        String npmCentralProfileContents = "$npmrcFileContents"
        NpmrcProfile customProfile = NpmrcProfile.builder()
            .name("custom-profile")
            .path(APP_PROFILES_FOLDER_PATH.resolve("custom-profile").toAbsolutePath().toString())
            .active(false)
            .build()
        String customProfileContents = format("registry=https://some-other-reg.localhost/%n//some-other-reg.localhost/:_auth=asdf")

        when:
        NpmrcmConfiguration result = configurationService.load()

        then:
        1 * filesystemHelper.read(APP_CONFIG_JSON_PATH) >> configFileContents
        1 * objectMapper.readValue(configFileContents, NpmrcmConfiguration)
        1 * filesystemHelper.read(DEFAULT_NPMRC_PATH.toAbsolutePath()) >> npmrcFileContents
        1 * filesystemHelper.list(APP_PROFILES_FOLDER_PATH) >> Stream.of(Path.of(npmCentralProfile.path()), Path.of(customProfile.path()))
        1 * filesystemHelper.exists(Path.of(npmCentralProfile.path()), false) >> true
        1 * filesystemHelper.read(Path.of(npmCentralProfile.path())) >> npmCentralProfileContents
        1 * filesystemHelper.exists(Path.of(customProfile.path()), false) >> true
        1 * filesystemHelper.read(Path.of(customProfile.path())) >> customProfileContents

        noExceptionThrown()
        result.npmrcPath == DEFAULT_NPMRC_PATH.toAbsolutePath().toString()
        result.profiles == [npmCentralProfile, customProfile]
    }

    def "exists check with exist check of [#exists]"() {
        when:
        boolean result = configurationService.exists()

        then:
        1 * filesystemHelper.exists(APP_CONFIG_JSON_PATH, false) >> exists

        noExceptionThrown()
        result == exists

        where:
        exists << [true, false]
    }

    def "save - error serializing config"() {
        given:
        NpmrcmConfiguration configuration = NpmrcmConfiguration.builder()
            .npmrcPath("/home/user/.npmrc")
            .profiles([
                NpmrcProfile.builder()
                    .name("npm-central")
                    .path("/home/user/.npmrcm/profiles/npm-central")
                    .active(true)
                    .build(),
                NpmrcProfile.builder()
                    .name("custom")
                    .path("/home/user/.npmrcm/profiles/custom")
                    .active(false)
                    .build()
            ])
            .build()

        when:
        configurationService.save(configuration)

        then:
        1 * prettyPrinter.writeValueAsString(configuration) >> { throw new IOException("io-err") }
        0 * filesystemHelper.write(_, _)

        thrown(UncheckedIOException)
    }

    def "save"() {
        given:
        NpmrcmConfiguration configuration = NpmrcmConfiguration.builder()
            .npmrcPath("/home/user/.npmrc")
            .profiles([
                NpmrcProfile.builder()
                    .name("npm-central")
                    .path("/home/user/.npmrcm/profiles/npm-central")
                    .active(true)
                    .build(),
                NpmrcProfile.builder()
                    .name("custom")
                    .path("/home/user/.npmrcm/profiles/custom")
                    .active(false)
                    .build()
            ])
            .build()
        String serializedContent = format("{%n  \"npmrcPath\" : \"/home/user/.npmrc\"%n}")

        when:
        configurationService.save(configuration)

        then:
        1 * prettyPrinter.writeValueAsString(configuration)
        1 * filesystemHelper.write(APP_CONFIG_JSON_PATH, serializedContent)

        noExceptionThrown()
    }

    def "remove"() {
        when:
        configurationService.remove()

        then:
        1 * filesystemHelper.remove(APP_CONFIG_JSON_PATH)

        noExceptionThrown()
    }
}
