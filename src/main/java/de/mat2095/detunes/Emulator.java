package de.mat2095.detunes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


class Emulator implements RenderingContext, InputProvider {

    private final Cpu cpu;
    private final Ppu ppu;
    private final Apu apu;
    private final Controller controller;
    private final Cartridge cartridge;
    private final RunConfiguration runConfig;
    private RenderingContext renderingContext;
    private InputProvider inputProvider;
    private long cpuInstrs;
    private boolean stopCheckRunning;
    private boolean running;

    Emulator(Path nesFile, RunConfiguration runConfig) throws IOException {
        byte[] nesBytes = Files.readAllBytes(nesFile);
        cartridge = Cartridge.createCartridge(nesBytes);
        cpu = new Cpu();
        ppu = new Ppu();
        apu = new Apu();
        controller = new Controller();
        this.runConfig = runConfig;
    }

    void setRenderingContext(RenderingContext renderingContext) {
        this.renderingContext = renderingContext;
    }

    @Override
    public void setBufferData(int addr, int value) {
        if (renderingContext != null) {
            renderingContext.setBufferData(addr, value);
        }
    }

    @Override
    public void sync() {
        if (renderingContext != null) {
            renderingContext.sync();
        }
    }

    void setInputProvider(InputProvider inputProvider) {
        this.inputProvider = inputProvider;
    }

    @Override
    public boolean isButtonPressed(int player, InputProvider.Button button) {
        return inputProvider != null && inputProvider.isButtonPressed(player, button);
    }

    void power() {
        cpu.power(this);
        if (runConfig.startPC != null) {
            cpu.pc = runConfig.startPC;
        }
        cpuInstrs = 0;

        ppu.power(this);

        apu.power();

        controller.power(this);

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

    RunConfiguration getRunConfig() {
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

    Ppu getPpu() {
        return ppu;
    }

    Cpu getCpu() {
        return cpu;
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
