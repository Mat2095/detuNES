package de.mat2095.detunes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


class Emulator {

    private final Cpu cpu;
    private final Ppu ppu;
    private final Apu apu;
    private final Cartridge cartridge;
    private final RunConfiguration runConfig;
    private long cpuInstrs;

    Emulator(Path nesFile, RunConfiguration runConfig) throws IOException {
        byte[] nesBytes = Files.readAllBytes(nesFile);
        cartridge = new Cartridge(nesBytes);
        cpu = new Cpu();
        ppu = new Ppu();
        apu = new Apu();
        this.runConfig = runConfig;
    }

    void power() {
        cpu.power(this);
        if (runConfig.startPC != null) {
            cpu.pc = runConfig.startPC;
        }
        cpuInstrs = 0;

        ppu.power();

        apu.power();
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

    byte read(int addr) {
        if (addr < 0x0000 || addr > 0xFFFF) {
            throw new IllegalArgumentException("addr out of range: " + Util.getHexString16bit(addr));
        }

        if (addr < 0x2000) {
            return cpu.read(addr);
        } else if (addr < 0x4000) {
            return ppu.read(addr);
        } else if (addr < 0x4020) {
            if (addr == 0x4014) {
                throw new IllegalArgumentException("Can't read from OAMDMA: " + Util.getHexString16bit(addr));
            } else if (addr == 0x4016) {
                throw new IllegalArgumentException("Joypad-0 (read) not yet implemented: " + Util.getHexString16bit(addr));
            } else if (addr == 0x4017) {
                throw new IllegalArgumentException("Joypad-1 (read) not yet implemented: " + Util.getHexString16bit(addr));
            } else if (addr >= 0x4018) {
                throw new IllegalArgumentException("APU/IO test functionality not yet implemented: " + Util.getHexString16bit(addr));
            } else {
                return apu.read(addr);
            }
        } else {
            return cartridge.read(addr);
        }
    }

    void write(int addr, byte value) {
        if (addr < 0x0000 || addr > 0xFFFF) {
            throw new IllegalArgumentException("addr out of range: " + Util.getHexString16bit(addr));
        }

        if (addr < 0x2000) {
            cpu.write(addr, value);
        } else if (addr < 0x4000) {
            ppu.write(addr, value);
        } else if (addr < 0x4020) {
            if (addr == 0x4014) {
                throw new IllegalArgumentException("OAMDMA (write) not yet implemented: " + Util.getHexString16bit(addr));
            } else if (addr == 0x4016) {
                throw new IllegalArgumentException("Joypad write strobe not yet implemented: " + Util.getHexString16bit(addr));
            } else if (addr >= 0x4018) {
                throw new IllegalArgumentException("APU/IO test functionality not yet implemented: " + Util.getHexString16bit(addr));
            } else {
                apu.write(addr, value);
            }
        } else {
            cartridge.write(addr, value);
        }
    }
}
