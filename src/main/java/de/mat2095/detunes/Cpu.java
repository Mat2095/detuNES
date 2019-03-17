package de.mat2095.detunes;

public class Cpu {

    byte[] ram;
    Cartridge cartridge;

    // registers
    byte a, x, y, s; // Accumulator, index X, index Y, Stack pointer
    int pc; // Program Counter
    boolean c, z, i, d, v, n; // Carry, Zero, Interrupt disable, Decimal, oVerflow, Negative

    public Cpu(Cartridge cartridge) {
        this.ram = new byte[0x0800]; // 2KiB
        this.cartridge = cartridge;
    }

    public byte read(int addr) {
        if (addr < 0x0000 || addr > 0xFFFF) {
            throw new IllegalArgumentException("CPU addr out of range: 0x" + Integer.toHexString(addr));
        }

        if (addr < 0x2000) {
            return ram[addr % 0x0800];
        }
        if (addr < 0x4000) {
            throw new IllegalArgumentException("ppu registers not yet implemented");
            // addr % 0x0008
        }
        if (addr < 0x4018) {
            throw new IllegalArgumentException("apu / io registers not yet implemented");
        }
        if (addr < 0x4020) {
            throw new IllegalArgumentException("api / io test functionality not yet implemented");
        }

        return cartridge.read(addr);
    }

    byte getP() {
        byte p = 0b00100000;

        p |= c ? 0b00000001 : 0;
        p |= z ? 0b00000010 : 0;
        p |= i ? 0b00000100 : 0;
        p |= d ? 0b00001000 : 0;
        p |= v ? 0b01000000 : 0;
        p |= n ? 0b10000000 : 0;

        return p;
    }

    void setP(byte p) {
        c = (p & 0b00000001) != 0;
        z = (p & 0b00000010) != 0;
        i = (p & 0b00000100) != 0;
        d = (p & 0b00001000) != 0;
        v = (p & 0b01000000) != 0;
        n = (p & 0b10000000) != 0;
    }

    void run() {

    }
}
