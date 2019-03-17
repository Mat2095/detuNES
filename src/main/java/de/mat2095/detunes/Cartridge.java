package de.mat2095.detunes;

import java.util.Arrays;


class Cartridge {

    int prgSize;
    int chrSize;
    byte[] prg;
    byte[] chr;
    int mapperNumber;
    boolean persistentMemory;
    NametableMirroring nametableMirroring;

    Cartridge(byte[] romData) {

        if (!(romData[0] == 'N' && romData[1] == 'E' && romData[2] == 'S' && romData[3] == 0x1A)) {
            throw new IllegalArgumentException("Invalid ROM-data");
        }

        prgSize = romData[4] * 16 * 1024;
        chrSize = romData[5] * 8 * 1024;
        prg = Arrays.copyOfRange(romData, 16, 16 + prgSize);
        chr = Arrays.copyOfRange(romData, 16 + prgSize, 16 + prgSize + chrSize);
        mapperNumber = ((romData[6] & 0xFF) >>> 4) | (romData[7] & 0b11110000);
        persistentMemory = (romData[6] & 0b00000010) == 0b00000010;
        nametableMirroring = ((romData[6] & 0b00000001) == 0b00000001) ? NametableMirroring.VERTICAL : NametableMirroring.HORIZONTAL;

        if (mapperNumber != 0) {
            throw new IllegalArgumentException("Only NROM mapper is supported, got " + mapperNumber);
        }
        if (prgSize != 16 * 1024 && prgSize != 32 * 1024) {
            throw new IllegalArgumentException("Invalid PRG-size: " + prgSize);
        }
        if (chrSize > 8 * 1024) {
            throw new IllegalArgumentException("Invalid CHR-size: " + chrSize);
        }
    }

    byte read(int addr) {
        if (addr < 0x6000 || addr > 0xFFFF) {
            throw new IllegalArgumentException("PRG addr out of range: 0x" + Integer.toHexString(addr));
        }
        if (addr < 0x8000) {
            throw new IllegalArgumentException("PRG-RAM not supported");
        }

        return prg[(addr & 0x7FFF) % prgSize];
    }

    byte readChr(int addr) {
        if (addr < 0x0000 || addr > 0x1FFF || addr >= chrSize) {
            throw new IllegalArgumentException("CHR addr out of range: 0x" + Integer.toHexString(addr) + " (size is 0x" + Integer.toHexString(chrSize) + ")");
        }

        return chr[addr];
    }
}
