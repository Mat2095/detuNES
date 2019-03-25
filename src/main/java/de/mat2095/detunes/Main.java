package de.mat2095.detunes;

import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


class Main {

    public static void main(String[] args) throws IOException {
//        // play game
//        File romFile = new File(args[0]).getCanonicalFile();
//        System.out.println("Reading ROM-file: " + romFile);
//        // assuming 4 cycles per instruction on average, 1ms per 450 instructions should result in about the correct speed
//        RunConfiguration runConfig = new RunConfiguration(1, 450);
//        run(romFile.toPath(), runConfig);

//        // analyse roms
//        Files.find(Paths.get("roms"),
//            Integer.MAX_VALUE,
//            (filePath, fileAttr) -> fileAttr.isRegularFile() && filePath.toString().toLowerCase().endsWith(".nes"))
//            .forEach(Main::analyseRom);

//        // nestest
//        Path testFile = Paths.get("roms/tests/nestest.nes");
//        RunConfiguration runConfig = new RunConfiguration(1, 2);
//        runConfig.startPC = 0xC000; // pc to run all tests
//        runConfig.debugPrintGeneralInfo = true;
//        runConfig.debugPrintMem = new int[]{0x0002, 0x0003};
//        run(testFile, runConfig);

        //instr_test
//        Path testFile = Paths.get("roms/tests/instr_test-v5/rom_singles/01-basics.nes");
//        Path testFile = Paths.get("roms/tests/instr_test-v5/rom_singles/02-implied.nes");
//        Path testFile = Paths.get("roms/tests/instr_test-v5/rom_singles/03-immediate.nes");
//        Path testFile = Paths.get("roms/tests/instr_test-v5/rom_singles/04-zero_page.nes");
//        Path testFile = Paths.get("roms/tests/instr_test-v5/rom_singles/05-zp_xy.nes");
//        Path testFile = Paths.get("roms/tests/instr_test-v5/rom_singles/06-absolute.nes");
//        Path testFile = Paths.get("roms/tests/instr_test-v5/rom_singles/07-abs_xy.nes");
//        Path testFile = Paths.get("roms/tests/instr_test-v5/rom_singles/08-ind_x.nes");
//        Path testFile = Paths.get("roms/tests/instr_test-v5/rom_singles/09-ind_y.nes");
//        Path testFile = Paths.get("roms/tests/instr_test-v5/rom_singles/10-branches.nes");
        Path testFile = Paths.get("roms/tests/instr_test-v5/rom_singles/11-stack.nes");
        RunConfiguration runConfig = new RunConfiguration(1, 1000);
//        runConfig.debugPrintGeneralInfo = true;
//        runConfig.debugPrintMem = new int[]{0x6000, 0x6001, 0x6002, 0x6003};
        runConfig.debugPrintMemText = 0x6004;
        run(testFile, runConfig);
    }

    private static void analyseRom(Path path) {
        System.out.println();
        System.out.println(path);

        Cartridge cartridge;
        try {
            byte[] romContent = Files.readAllBytes(path);
            cartridge = new Cartridge(romContent);
        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("  PRG-ROM size: " + cartridge.prgSize
            + "\tCHR-ROM size: " + cartridge.chrSize);
        System.out.println("  mapper: " + cartridge.mapperNumber
            + " \tnametable-mirroring: " + cartridge.nametableMirroring
            + "\tpersistent memory: " + cartridge.persistentMemory);
    }

    private static void debugController() throws XInputNotLoadedException {
        XInputDevice controller = XInputDevice.getDeviceFor(0);
        while (true) {
            controller.poll();
            XInputComponents state = controller.getComponents();
            System.out.println(state.getButtons().b);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void run(Path nesFile, RunConfiguration runConfig) throws IOException {
        byte[] testBytes = Files.readAllBytes(nesFile);
        Cartridge testCartridge = new Cartridge(testBytes);

        Cpu cpu = new Cpu(testCartridge, runConfig);
        cpu.power();
        cpu.run();
    }
}
