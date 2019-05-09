package de.mat2095.detunes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


class Emulator {

    private final Cpu cpu;
    private final Ppu ppu;
    private final Apu apu;
    private final Controller controller;
    private final Cartridge cartridge;
    private final RunConfiguration runConfig;
    private RenderingContext renderingContext;
    private long cpuInstrs;
    private boolean stopCheckRunning;
    private boolean running;

    Emulator(Path nesFile, InputProvider ip1, InputProvider ip2, RunConfiguration runConfig) throws IOException {
        byte[] nesBytes = Files.readAllBytes(nesFile);
        cartridge = Cartridge.createCartridge(nesBytes);
        cpu = new Cpu();
        ppu = new Ppu();
        apu = new Apu();
        controller = new Controller(ip1, ip2);
        this.runConfig = runConfig;
    }

    Emulator(Path nesFile, RunConfiguration runConfig) throws IOException {
        this(nesFile, new InputProviderDummy(), new InputProviderDummy(), runConfig);
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

        controller.power();

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
                    if (readCpu(runConfig.stopAddr) == runConfig.stopValueUnequal) {
                        stopCheckRunning = true;
                    }
                } else {
                    if (readCpu(runConfig.stopAddr) != runConfig.stopValueUnequal) {
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

    public RunConfiguration getRunConfig() {
        return runConfig;
    }

    String readText(int addr) {
        StringBuilder memTextBuilder = new StringBuilder();
        for (; ; addr++) {
            byte b = readCpu(addr);
            if (b == 0) {
                break;
            }
            memTextBuilder.append((char) b);
        }
        return memTextBuilder.toString().replace('\n', ' ');
    }

    void fireNmi() {
        cpu.intNmi();
    }

    public Ppu getPpu() {
        return ppu;
    }

    byte readCpu(int addr) {
        if (addr < 0x0000 || addr > 0xFFFF) {
            throw new IllegalArgumentException("CPU addr out of range: " + Util.getHexString16bit(addr));
        }

        if (addr < 0x2000) {
            return cpu.readCpu(addr);
        } else if (addr < 0x4000) {
            return ppu.readCpu(addr);
        } else if (addr < 0x4020) {
            if (addr == 0x4014) {
                throw new IllegalArgumentException("Can't read from OAMDMA: " + Util.getHexString16bit(addr));
            } else if (addr == 0x4016) {
                return controller.read(0);
            } else if (addr == 0x4017) {
                return controller.read(1);
            } else if (addr >= 0x4018) {
                throw new IllegalArgumentException("APU/IO test functionality not yet implemented: " + Util.getHexString16bit(addr));
            } else {
                return apu.readCpu(addr);
            }
        } else {
            return cartridge.readCpu(addr);
        }
    }

    void writeCpu(int addr, byte value) {
        if (addr < 0x0000 || addr > 0xFFFF) {
            throw new IllegalArgumentException("CPU addr out of range: " + Util.getHexString16bit(addr));
        }

        if (addr < 0x2000) {
            cpu.writeCpu(addr, value);
        } else if (addr < 0x4000) {
            ppu.writeCpu(addr, value);
        } else if (addr < 0x4020) {
            if (addr == 0x4014) {
                ppu.doOamDma(value);
            } else if (addr == 0x4016) {
                controller.updateStrobe((value & 0x01) != 0);
            } else if (addr >= 0x4018) {
                throw new IllegalArgumentException("APU/IO test functionality not yet implemented: " + Util.getHexString16bit(addr));
            } else {
                apu.writeCpu(addr, value);
            }
        } else {
            cartridge.writeCpu(addr, value);
        }
    }

    NametableMirroring getNametableMirroring() {
        return cartridge.nametableMirroring;
    }

    byte readPpu(int addr) {
        if (addr >= 0 && addr < 0x2000) {
            return cartridge.readPpu(addr);
        } else if (addr >= 0x2000 && addr < 0x4000) {
            return ppu.readPpu(addr);
        } else {
            throw new IllegalArgumentException("PPU addr out of range: " + Util.getHexString16bit(addr));
        }
    }

    void writePpu(int addr, byte value) {
        if (addr >= 0 && addr < 0x2000) {
            cartridge.writePpu(addr, value);
        } else if (addr >= 0x2000 && addr < 0x4000) {
            ppu.writePpu(addr, value);
        } else {
            throw new IllegalArgumentException("PPU addr out of range: " + Util.getHexString16bit(addr));
        }
    }
}
