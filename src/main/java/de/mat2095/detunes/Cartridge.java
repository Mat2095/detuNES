package de.mat2095.detunes;

import java.util.Arrays;


class Cartridge {

    final int prgSize;
    final int chrSize;
    private final byte[] prg;
    private final byte[] chr;
    private final byte[] prgRam;
    final int mapperNumber;
    final boolean persistentMemory;
    final NametableMirroring nametableMirroring;

    Cartridge(byte[] romData) {

        if (!(romData[0] == 'N' && romData[1] == 'E' && romData[2] == 'S' && romData[3] == 0x1A)) {
            throw new IllegalArgumentException("Invalid ROM-data");
        }

        prgSize = romData[4] * 0x4000;
        chrSize = romData[5] * 0x2000;
        prg = Arrays.copyOfRange(romData, 16, 16 + prgSize);
        chr = Arrays.copyOfRange(romData, 16 + prgSize, 16 + prgSize + chrSize);
        prgRam = new byte[0x2000];
        mapperNumber = ((romData[6] & 0xFF) >>> 4) | (romData[7] & 0b11110000);
        persistentMemory = (romData[6] & 0b00000010) == 0b00000010;
        nametableMirroring = ((romData[6] & 0b00000001) == 0b00000001) ? NametableMirroring.VERTICAL : NametableMirroring.HORIZONTAL;

        if (mapperNumber != 0) {
            throw new IllegalArgumentException("Only NROM mapper is supported, got " + mapperNumber);
        }
        if (prgSize != 0x4000 && prgSize != 0x8000) {
            throw new IllegalArgumentException("Invalid PRG-size: " + prgSize);
        }
        if (chrSize > 0x2000) {
            throw new IllegalArgumentException("Invalid CHR-size: " + chrSize);
        }
    }

    byte read(int addr) {
        if (addr < 0x6000 || addr > 0xFFFF) {
            throw new IllegalArgumentException("PRG addr out of range: " + Util.getHexString16bit(addr));
        }
        if (addr < 0x8000) {
            return prgRam[addr - 0x6000];
        } else {
            return prg[(addr - 0x8000) % prgSize];
        }
    }

    void write(int addr, byte value) {
        if (addr < 0x6000 || addr > 0xFFFF) {
            throw new IllegalArgumentException("PRG addr out of range: " + Util.getHexString16bit(addr));
        }
        if (addr < 0x8000) {
            prgRam[addr - 0x6000] = value;
        } else {
            throw new IllegalArgumentException("Writing to PRG-ROM not yet supported");
        }
    }

    byte readChr(int addr) {
        if (addr < 0x0000 || addr > 0x1FFF || addr >= chrSize) {
            throw new IllegalArgumentException("CHR addr out of range: " + Util.getHexString16bit(addr)
                + " (size is " + Util.getHexString16bit(addr) + ")");
        }

        return chr[addr];
    }
}
