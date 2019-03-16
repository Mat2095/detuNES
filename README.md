## detuNES

### Import into IntelliJ IDEA

- Open the Gradle project
- Enable "Use auto-import"
- Select "Use local gradle distribution", using at least version 5

### Usage

- Build JAR (executable):
`gradle clean shadowJar`
- Run from Gradle:
`gradle run -PappArgs="['world']"`
- Run from console:
`java -Xmx2g -jar build/libs/detuNES-0.1.0-all.jar world`
- Run test:
TODO

These tasks can be saved in IntelliJ IDEA for easy access.
