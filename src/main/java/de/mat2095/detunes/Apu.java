package de.mat2095.detunes;

class Apu {

    Apu() {
    }

    void power() {
        write(0x4017, (byte) 0x00);
        write(0x4015, (byte) 0x00);
        for (int addr = 0x4000; addr < 0x4010; addr++) {
            write(addr, (byte) 0x00);
        }
    }

    byte read(int addr) {
        if (addr >= 0x4000 && addr < 0x4014) {
            throw new IllegalArgumentException("Can't read from APU register: " + Util.getHexString16bit(addr));
        } else if (addr == 0x4015) {
            throw new IllegalArgumentException("APU-Status (read) not yet implemented: " + Util.getHexString16bit(addr));
        } else {
            throw new IllegalArgumentException("APU addr out of range: " + Util.getHexString16bit(addr));
        }
    }

    void write(int addr, byte value) {
        if (addr >= 0x4000 && addr < 0x4014) {
            // TODO: implement APU (write) as far as necessary
        } else if (addr == 0x4015) {
            // TODO: implement Apu-Status (write) as far as necessary
        } else if (addr == 0x4017) {
            // TODO: implement APU-Framecounter (write) as far as necessary
        } else {
            throw new IllegalArgumentException("APU addr out of range: " + Util.getHexString16bit(addr));
        }
    }
}
