package de.mat2095.detunes;

class Ppu {

    private final byte[] registers;
    private int line, x;
    private Emulator emu;

    Ppu() {
        this.registers = new byte[8];
    }

    void power(Emulator emu) {
        this.emu = emu;
        registers[2] = (byte) 0b10100000;
        line = x = 0; // TODO: verify
    }

    byte read(int addr) {
        if (addr < 0x2000 || addr > 0x3FFF) {
            throw new IllegalArgumentException("PPU addr out of range: " + Util.getHexString16bit(addr));
        }
        addr %= 0x0008;
        switch (addr) {
            case 0x0000:
                throw new IllegalArgumentException("Read from PPU-CTRL not allowed.");
            case 0x0001:
                throw new IllegalArgumentException("Read from PPU-MASK not allowed.");
            case 0x0003:
                throw new IllegalArgumentException("Read from OAM-ADDR not allowed.");
            case 0x0005:
                throw new IllegalArgumentException("Read from PPU-SCROLL not allowed.");
            case 0x0006:
                throw new IllegalArgumentException("Read from PPU-ADDR not allowed.");
            case 0x0002:
                registers[0x0002] = (byte) ((registers[0x0002] & 0xFF) & 0x7F);
            default:
                return registers[addr];
        }
    }

    void write(int addr, byte value) {
        if (addr < 0x2000 || addr > 0x3FFF) {
            throw new IllegalArgumentException("PPU addr out of range: " + Util.getHexString16bit(addr));
        }
        addr %= 0x0008;
        switch (addr) {
            case 0x0002:
                throw new IllegalArgumentException("Write to PPU-STATUS not allowed.");
            default:
                registers[addr] = value;
        }
    }

    void render() {
        if (line == 241 && x == 1) {
            registers[0x0002] = (byte) ((registers[0x0002] & 0xFF) | 0b10000000);
        }
        if (line == 261 && x == 1) {
            registers[0x0002] = (byte) ((registers[0x0002] & 0xFF) & 0b00011111);
        }

        if (line < 240 && x < 256) {
            emu.getRenderingContext().getBufferData()[line * 256 + x] = (int) (Math.random() * 0x1000000);
        } else if (x == 256) {
            emu.getRenderingContext().sync();
        }

        x++;
        if (x > 341) {
            x = 0;
            line++;
            if (line > 262) {
                line = 0;
            }
        }
    }
}
