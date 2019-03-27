package de.mat2095.detunes;

class Ppu {

    private final byte[] registers;

    Ppu() {
        this.registers = new byte[8];
    }

    void power() {
    }

    byte read(int addr) {
        if (addr < 0x2000 || addr > 0x3FFF) {
            throw new IllegalArgumentException("PPU addr out of range: " + Util.getHexString16bit(addr));
        }
        return registers[addr % 0x0008];
    }

    void write(int addr, byte value) {
        if (addr < 0x2000 || addr > 0x3FFF) {
            throw new IllegalArgumentException("PPU addr out of range: " + Util.getHexString16bit(addr));
        }
        registers[addr % 0x0008] = value;
    }
}
