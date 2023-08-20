package com.cps.cli.npmrcmanager.service.input.impl

import com.cps.cli.npmrcmanager.service.input.UserInputService
import spock.lang.Specification

import java.nio.file.Path

import static java.lang.String.format

class StdUserInputServiceSpec extends Specification {

    // save std in & out
    InputStream stdIn
    PrintStream stdOut

    // tested class
    UserInputService userInputService

    void setup() {
        stdIn = System.in
        stdOut = System.out
    }

    void cleanup() {
        System.setIn(stdIn)
        System.setOut(stdOut)
    }

    def "prompt for yes or no with answer of #input, expecting #expected"() {
        given:
        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes())
        System.setIn(bais)

        and:
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        PrintStream ps = new PrintStream(baos)
        System.setOut(ps)

        and:
        userInputService = new StdUserInputService()
        String prompt = "Do you like tea?"

        when:
        boolean result = userInputService.promptForYesOrNo(prompt)

        then:
        noExceptionThrown()
        new String(baos.toByteArray()) == format("%nDo you like tea? (Y/N): %n")
        result == expected

        where:
        input || expected
        "y"   || true
        "Y"   || true
        "n"   || false
        "N"   || false
    }

    def "prompt for path with answer of \"#input\", expecting \"#expected\""() {
        given:
        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes())
        System.setIn(bais)

        and:
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        PrintStream ps = new PrintStream(baos)
        System.setOut(ps)

        and:
        userInputService = new StdUserInputService()
        String prompt = "Where is your favourite beverage?"
        Path defaultValue = Path.of("home", "tester").toAbsolutePath()

        when:
        Path result = userInputService.promptForPath(prompt, defaultValue)

        then:
        noExceptionThrown()
        new String(baos.toByteArray()) == format("%nWhere is your favourite beverage? [%s]: %n", defaultValue)
        result == expected

        where:
        input                                                                    || expected
        "${System.lineSeparator()}"                                              || Path.of("home", "tester").toAbsolutePath()
        "${Path.of("home", "tester").toAbsolutePath()}${System.lineSeparator()}" || Path.of("home", "tester").toAbsolutePath()
        "${Path.of("usr", "var").toAbsolutePath()}${System.lineSeparator()}"     || Path.of("usr", "var").toAbsolutePath()
        "${Path.of("usr", "var")}${System.lineSeparator()}"                      || Path.of("usr", "var")
    }

    def "prompt for path with answer of \"#wrongInput\" and then \"#correctInput\", expecting an invalid path, and then \"#expected\""() {
        given:
        ByteArrayInputStream bais = new ByteArrayInputStream(String.join(System.lineSeparator(), wrongInput, correctInput).getBytes())
        System.setIn(bais)

        and:
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        PrintStream ps = new PrintStream(baos)
        System.setOut(ps)

        and:
        userInputService = new StdUserInputService()
        String prompt = "Where is your favourite beverage?"
        Path defaultValue = Path.of("home", "tester").toAbsolutePath()

        when:
        Path result = userInputService.promptForPath(prompt, defaultValue)

        then:
        noExceptionThrown()
        def oneExpectedLine = format("Where is your favourite beverage? [%s]: %n", defaultValue)
        new String(baos.toByteArray()) == format("%n%s%s", oneExpectedLine, oneExpectedLine)
        result == expected

        where:
        wrongInput                    | correctInput                                                         || expected
        "\"/////\\//\\\0--...fd\"***\"" | "${System.lineSeparator()}"                                          || Path.of("home", "tester").toAbsolutePath()
        "\"/////\\//\\\0--...fd\"***\"" | "${Path.of("usr", "var").toAbsolutePath()}${System.lineSeparator()}" || Path.of("usr", "var").toAbsolutePath()
        "\"/////\\//\\\0--...fd\"***\"" | "${Path.of("usr", "var")}${System.lineSeparator()}"                  || Path.of("usr", "var")
    }

    def "prompt for string with answer of \"#input\", expecting \"#expected\""() {
        given:
        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes())
        System.setIn(bais)

        and:
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        PrintStream ps = new PrintStream(baos)
        System.setOut(ps)

        and:
        userInputService = new StdUserInputService()
        String prompt = "What is your favourite beverage?"
        String defaultValue = "tea"

        when:
        String result = userInputService.promptForString(prompt, defaultValue)

        then:
        noExceptionThrown()
        new String(baos.toByteArray()) == format("%nWhat is your favourite beverage? [%s]: %n", defaultValue)
        result == expected

        where:
        input                                                                    || expected
        "${System.lineSeparator()}"                                              || "tea"
        "tea${System.lineSeparator()}"                                           || "tea"
        "coffee${System.lineSeparator()}"                                        || "coffee"
        "milk${System.lineSeparator()}"                                          || "milk"
    }
}
