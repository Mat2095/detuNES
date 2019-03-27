package de.mat2095.detunes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


class Emulator {

    private final Cpu cpu;
    final Ppu ppu;
    Cartridge cartridge;
    private final RunConfiguration runConfig;
    private long cpuInstrs;

    Emulator(Path nesFile, RunConfiguration runConfig) throws IOException {
        byte[] nesBytes = Files.readAllBytes(nesFile);
        cartridge = new Cartridge(nesBytes);
        cpu = new Cpu();
        ppu = new Ppu();
        this.runConfig = runConfig;
    }

    void power() {
        cpu.power(this);
        if (runConfig.startPC != null) {
            cpu.pc = runConfig.startPC;
        }
        cpuInstrs = 0;
    }

    void run() {
        while (true) {
            cpu.printDebug(runConfig);
            cpu.exec();
            cpuInstrs++;
            if (cpuInstrs % runConfig.sleepPeriodicity == 0) {
                try {
                    Thread.sleep(runConfig.sleepDuration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
