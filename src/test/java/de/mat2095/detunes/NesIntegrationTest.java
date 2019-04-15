package de.mat2095.detunes;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;


class NesIntegrationTest {

    @Test
    void nesTest() throws IOException {
        Path testFile = Paths.get("roms/nestest/nestest.nes");
        RunConfiguration runConfig = new RunConfiguration();
        runConfig.startPC = 0xC000; // pc to run all tests
//        runConfig.debugCpuPrintGeneralInfo = true;
//        runConfig.debugCpuPrintMem = new int[]{0x0002, 0x0003};
        Emulator emu = new Emulator(testFile, runConfig);
        emu.power();
    }
}
