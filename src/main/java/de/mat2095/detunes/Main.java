package de.mat2095.detunes;

import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


class Main {

    public static void main(String[] args) throws IOException {
        // play game
        File romFile = new File(args[0]).getCanonicalFile();
        System.out.println("Reading ROM-file: " + romFile);

        RunConfiguration runConfig = new RunConfiguration();
        runConfig.sleepDuration = 1;
        runConfig.sleepPeriodicity = 200;
        runConfig.debugPpuPrintAccesses = true;

        Emulator emu = new Emulator(romFile.toPath(), runConfig);
        Gui gui = new Gui(emu);
        emu.power();
        emu.run();

//        // analyse roms
//        Files.find(Paths.get("roms"),
//            Integer.MAX_VALUE,
//            (filePath, fileAttr) -> fileAttr.isRegularFile() && filePath.toString().toLowerCase().endsWith(".nes"))
//            .forEach(Main::analyseRom);
    }

    private static void analyseRom(Path path) {
        System.out.println();
        System.out.println(path);

        Cartridge cartridge;
        try {
            byte[] romContent = Files.readAllBytes(path);
            cartridge = Cartridge.createCartridge(romContent);
        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("  PRG-ROM size: " + cartridge.prgSize
            + "\tCHR-ROM size: " + cartridge.chrSize);
        System.out.println("  mapper: " + cartridge.getClass().getSimpleName()
            + " \tnametable-mirroring: " + cartridge.nametableMirroring
            + "\tpersistent memory: " + cartridge.prgRamPersistent);
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
}
