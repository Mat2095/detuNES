## detuNES

[![Build Status](https://travis-ci.com/Mat2095/detuNES.svg?token=xy78xvtSts4sqNz8p7Dv&branch=master)](https://travis-ci.com/Mat2095/detuNES)

## Project-Goal

The goal of this project is not to create an emulator for actual use, but rather for me to have fun implementing it and learning something along the way.

Features that WILL be implemented:
- passing certain test-ROMs
- running some games and outputting the graphics
- common mappers: NROM, MMC1, MMC3
- input from XInput (Xbox 360 controller)

Features that MIGHT be implemented:
- accurate timing
- fancy upscaling (xBRZ?)
- other mappers: UNROM, CNROM, ?
- keyboard-input

Features that WON'T be implemented:
- audio output (APU will only be implemented as far as necessary, but no actual output)
- persistency (neither save-states nor memory-snapshots)

### Import into IntelliJ IDEA

- Open the Gradle project
- Enable "Use auto-import"
- Select "Use local gradle distribution", using at least version 5.3
- Note: make sure you're using Java 8, since Darcula causes problems with Java 9+ (https://github.com/bulenkov/Darcula/issues/41)

### Usage

These tasks can be saved in IntelliJ IDEA for easy access.

- Build JAR (executable):  
`gradle clean shadowJar`
- Run from Gradle:  
`gradle run -PappArgs="['path/to/rom.nes']"`
- Run from console:  
`java -Xmx2g -jar build/libs/detuNES-0.1.0-all.jar path/to/rom.nes`
- Run tests from Gradle:  
  `gradle cleanTest test`
  
  This is not shown correctly in IDEA, see [this issue](https://github.com/gradle/gradle/issues/5975).
  A workaround is to use a IDEA-JUnit task directly:
  - Create a new JUnit task
  - Set "Test kind" to "All in package" and "Search for tests" to "In whole project"
  - Add the Gradle task "prepareTest" to "Before launch"

