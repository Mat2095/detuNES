## detuNES

### Import into IntelliJ IDEA

- Open the Gradle project
- Enable "Use auto-import"
- Select "Use local gradle distribution", using at least version 5.3

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

