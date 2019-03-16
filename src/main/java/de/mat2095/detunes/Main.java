package de.mat2095.detunes;

import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


class Main {

    public static void main(String[] args) throws IOException {
        File romFile = new File(args[0]).getCanonicalFile();
        System.out.println("Reading ROM-file: " + romFile);
        analyseRom(romFile.toPath());

        System.out.println("---");

        Files.find(Paths.get("roms"),
            Integer.MAX_VALUE,
            (filePath, fileAttr) -> fileAttr.isRegularFile() && filePath.toString().toLowerCase().endsWith(".nes"))
            .forEach(Main::analyseRom);
    }

    private static void analyseRom(Path path) {

        Rom rom;
        try {
            byte[] romContent = Files.readAllBytes(path);
            rom = new Rom(romContent);
        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println();
        System.out.println(path);
        System.out.println("  PRG-ROM size: " + rom.prgSize
            + "\tCHR-ROM size: " + rom.chrSize);
        System.out.println("  mapper: " + rom.mapperNumber
            + " \tnametable-mirroring: " + rom.nametableMirroring
            + "\tpersistent memory: " + rom.persistentMemory);
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
