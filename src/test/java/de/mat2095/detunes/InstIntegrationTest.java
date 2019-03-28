package de.mat2095.detunes;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


class InstIntegrationTest {

    @Test
    void combinedRomTest() throws IOException {
        Path testFile = Paths.get("roms/instr_test-v5/official_only.nes");
        RunConfiguration runConfig = new RunConfiguration(1, 1000);
//        runConfig.debugPrintGeneralInfo = true;
//        runConfig.debugPrintMem = new int[]{0x6000, 0x6001, 0x6002, 0x6003};
        runConfig.debugPrintMemText = 0x6004;
//        Emulator emu = new Emulator(testFile, runConfig);
//        emu.power();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "01-basics.nes",
        "02-implied.nes",
        "03-immediate.nes",
        "04-zero_page.nes",
        "05-zp_xy.nes",
        "06-absolute.nes",
        "07-abs_xy.nes",
        "08-ind_x.nes",
        "09-ind_y.nes",
        "10-branches.nes",
        "11-stack.nes",
        "12-jmp_jsr.nes",
        "13-rts.nes",
        "14-rti.nes",
        "15-brk.nes",
        "16-special.nes",
    })
    void splitRomTest(String fileName) throws IOException {
        Path testFile = Paths.get("roms/instr_test-v5/rom_singles/" + fileName);
        RunConfiguration runConfig = new RunConfiguration(1, 1000);
//        runConfig.debugPrintGeneralInfo = true;
//        runConfig.debugPrintMem = new int[]{0x6000, 0x6001, 0x6002, 0x6003};
        runConfig.debugPrintMemText = 0x6004;
        Emulator emu = new Emulator(testFile, runConfig);
        emu.power();
    }
}
