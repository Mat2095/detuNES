package de.mat2095.detunes;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;


class InstrIntegrationTest {

    @Test
    void combinedRomTest() throws IOException {
        Path testFile = Paths.get("roms/instr_test-v5/official_only.nes");
        RunConfiguration runConfig = new RunConfiguration();
        runConfig.stopAddr = 0x6000;
        runConfig.stopValueUnequal = (byte) 0x80;

        Emulator emu = new Emulator(testFile, runConfig);
        emu.power();
        assertTimeoutPreemptively(Duration.ofSeconds(10), emu::run, "Emulator did not stop within given time.");

        byte testStatus = emu.readCpu(runConfig.stopAddr);
        String testMessage = emu.readText(0x6004);
        assertEquals(0, testStatus, testMessage);
        assertEquals("All 16 tests passed   ", testMessage, testMessage);
    }

    @ParameterizedTest
    @CsvSource({
        "01-basics.nes, true, 60146",
        "02-implied.nes, false, 929",
        "03-immediate.nes, false, 929",
        "04-zero_page.nes, false, 929",
        "05-zp_xy.nes, false, 929",
        "06-absolute.nes, false, 929",
        "07-abs_xy.nes, false, 929",
        "08-ind_x.nes, false, 929",
        "09-ind_y.nes, false, 929",
        "10-branches.nes, true, 60051",
        "11-stack.nes, true, 60307",
        "12-jmp_jsr.nes, true, 60051",
        "13-rts.nes, true, 60051",
        "14-rti.nes, true, 60051",
        "15-brk.nes, true, 60051",
        "16-special.nes, true, 59914",
    })
    void splitRomTest(String fileName, boolean allOpsImplemented, int expectedEndPc) throws IOException {
        Path testFile = Paths.get("roms/instr_test-v5/rom_singles/" + fileName);
        RunConfiguration runConfig = new RunConfiguration();
        runConfig.stopAddr = 0x6000;
        runConfig.stopValueUnequal = (byte) 0x80;

        Emulator emu = new Emulator(testFile, runConfig);
        emu.power();

        if (allOpsImplemented) {
            assertTimeoutPreemptively(Duration.ofSeconds(1), emu::run, "Emulator did not stop within given time.");

            byte testStatus = emu.readCpu(runConfig.stopAddr);
            String testMessage = emu.readText(0x6004);
            assertEquals(0, testStatus, testMessage);
            assertEquals(" " + fileName.substring(0, fileName.length() - 4) + "  Passed ", testMessage, testMessage);
        } else {
            assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
                try {
                    emu.run();
                    fail("Expected a non-clean run.");
                } catch (IllegalStateException e) {
                    if (!e.getMessage().startsWith("Invalid OP-code: 0x")) {
                        throw e;
                    }
                }
            }, "Emulator did not stop within given time.");

            byte testStatus = emu.readCpu(runConfig.stopAddr);
            String testMessage = emu.readText(0x6004);
            assertEquals(-128, testStatus, "Expected unset status-code: " + testStatus + " " + testMessage);
            assertEquals("", testMessage, "Expected unset message: " + testStatus + " " + testMessage);
        }

        assertEquals(expectedEndPc, emu.getCpu().pc, "PC after run does not match expectation.");
    }
}
