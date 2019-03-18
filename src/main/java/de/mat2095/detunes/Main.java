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
//        File romFile = new File(args[0]).getCanonicalFile();
//        System.out.println("Reading ROM-file: " + romFile);
//        play(romFile.toPath());

//        Files.find(Paths.get("roms"),
//            Integer.MAX_VALUE,
//            (filePath, fileAttr) -> fileAttr.isRegularFile() && filePath.toString().toLowerCase().endsWith(".nes"))
//            .forEach(Main::analyseRom);

        test();
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

    private static void play(Path gameFile) throws IOException {
        byte[] testBytes = Files.readAllBytes(gameFile);
        Cartridge testCartridge = new Cartridge(testBytes);
        Cpu cpu = new Cpu(testCartridge);
        cpu.power();
        cpu.run();
    }

    private static void test() throws IOException {
        Path testFile = Paths.get("roms/tests/nestest.nes");
        byte[] testBytes = Files.readAllBytes(testFile);
        Cartridge testCartridge = new Cartridge(testBytes);
        Cpu cpu = new Cpu(testCartridge);
        cpu.power();
        cpu.pc = 0x0C000; // pc to run all tests
        cpu.run();
    }
}
