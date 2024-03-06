package com.cps.cli.npmrcmanager.mapper.impl

import com.cps.cli.npmrcmanager.mapper.Mapper
import com.cps.cli.npmrcmanager.model.NpmrcProfile
import com.cps.cli.npmrcmanager.model.NpmrcmConfiguration
import spock.lang.Specification

class YmlMapperSpec extends Specification {

    Mapper mapper

    void setup() {
        mapper = new YmlMapper()
    }

    def "writes configuration correctly"() {
        given:
        def config = NpmrcmConfiguration.builder()
            .npmrcPath("/home/user/.npmrc")
            .profiles([
                NpmrcProfile.builder()
                    .name("central")
                    .active(true)
                    .path("/home/user/.npmrcm/profiles/central")
                    .build()
            ])
            .build()

        when:
        def result = mapper.writeValueAsString(config)

        then:
        result == """\
        npmrcPath: /home/user/.npmrc
        """.stripIndent()
    }

    def "reads configuration correctly"() {
        given:
        def configYml = """\
        npmrcPath: /home/user/.npmrc
        """.stripIndent()

        when:
        def result = mapper.readValue(configYml, NpmrcmConfiguration)

        then:
        result.npmrcPath == "/home/user/.npmrc"
        result.profiles.size() == 0
    }
}
