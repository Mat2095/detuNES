package de.mat2095.detunes;

class Apu {

    Apu() {
    }

    void power() {
        writeCpu(0x4017, (byte) 0x00);
        writeCpu(0x4015, (byte) 0x00);
        for (int addr = 0x4000; addr < 0x4010; addr++) {
            writeCpu(addr, (byte) 0x00);
        }
    }

    byte readCpu(int addr) {
        if (addr >= 0x4000 && addr < 0x4014) {
            throw new IllegalArgumentException("Can't read from APU register: " + Util.getHexString16bit(addr));
        } else if (addr == 0x4015) {
            // TODO: implement Apu-Status (read) as far as necessary
            return 0;
        } else {
            throw new IllegalArgumentException("CPU addr out of range at APU: " + Util.getHexString16bit(addr));
        }
    }

    void writeCpu(int addr, byte value) {
        if (addr >= 0x4000 && addr < 0x4014) {
            // TODO: implement APU (write) as far as necessary
        } else if (addr == 0x4015) {
            // TODO: implement Apu-Status (write) as far as necessary
        } else if (addr == 0x4017) {
            // TODO: implement APU-Framecounter (write) as far as necessary
        } else {
            throw new IllegalArgumentException("CPU addr out of range at APU: " + Util.getHexString16bit(addr));
        }
    }
}
