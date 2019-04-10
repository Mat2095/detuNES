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
    private RenderingContext renderingContext;
    private long cpuInstrs;
    private boolean stopCheckRunning;
    private boolean running;

    Emulator(Path nesFile, RunConfiguration runConfig) throws IOException {
        byte[] nesBytes = Files.readAllBytes(nesFile);
        cartridge = Cartridge.createCartridge(nesBytes);
        cpu = new Cpu();
        ppu = new Ppu();
        apu = new Apu();
        this.runConfig = runConfig;
    }

    RenderingContext getRenderingContext() {
        return renderingContext;
    }

    void setRenderingContext(RenderingContext renderingContext) {
        this.renderingContext = renderingContext;
    }

    void resetRenderingContext() {
        renderingContext = new RenderingContext() {
            int[] data = new int[240 * 256];

            @Override
            void setBufferData(int addr, int value) {
                this.data[addr] = value;
            }

            @Override
            void sync() {

            }
        };
    }

    void power() {
        if (renderingContext == null) {
            resetRenderingContext();
        }

        cpu.power(this);
        if (runConfig.startPC != null) {
            cpu.pc = runConfig.startPC;
        }
        cpuInstrs = 0;

        ppu.power(this);

        apu.power();

        stopCheckRunning = false;
        running = false;
    }

    void run() {
        running = true;
        while (running) {
            cpu.printDebug(runConfig);
            cpu.exec();
            ppu.render();
            ppu.render();
            ppu.render();

            if (runConfig.stopAddr != null) {
                if (!stopCheckRunning) {
                    if (read(runConfig.stopAddr) == runConfig.stopValueUnequal) {
                        stopCheckRunning = true;
                    }
                } else {
                    if (read(runConfig.stopAddr) != runConfig.stopValueUnequal) {
                        return;
                    }
                }
            }

            cpuInstrs++;
            if (runConfig.sleepPeriodicity != null && cpuInstrs % runConfig.sleepPeriodicity == 0) {
                try {
                    Thread.sleep(runConfig.sleepDuration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void halt() {
        running = false;
    }

    String readText(int addr) {
        StringBuilder memTextBuilder = new StringBuilder();
        for (; ; addr++) {
            byte b = read(addr);
            if (b == 0) {
                break;
            }
            memTextBuilder.append((char) b);
        }
        return memTextBuilder.toString().replace('\n', ' ');
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

    byte readChr(int addr) {
        if (addr < 0x0000 || addr > 0x1FFF) {
            throw new IllegalArgumentException("CHR addr out of range: " + Util.getHexString16bit(addr));
        }
        return cartridge.readChr(addr);
    }
}
