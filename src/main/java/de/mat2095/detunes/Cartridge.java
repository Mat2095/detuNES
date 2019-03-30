package de.mat2095.detunes;

import java.util.Arrays;


abstract class Cartridge {

    final int prgSize;
    final byte[] prg;

    final boolean prgRamPersistent;
    final int prgRamSize;
    final byte[] prgRam;

    private final boolean chrRam;
    final int chrSize;
    private final byte[] chr;

    NametableMirroring nametableMirroring;

    Cartridge(byte[] romData) {

        if (!(romData[0] == 'N' && romData[1] == 'E' && romData[2] == 'S' && romData[3] == 0x1A)) {
            throw new IllegalArgumentException("Invalid ROM-data");
        }

        prgSize = romData[4] * 0x4000;
        prg = Arrays.copyOfRange(romData, 16, 16 + prgSize);

        prgRamPersistent = (romData[6] & 0b00000010) == 0b00000010;
        prgRamSize = romData[8] != 0 ? romData[8] * 0x2000 : 0x2000;
        prgRam = new byte[prgRamSize];

        chrRam = romData[5] == 0;
        chrSize = chrRam ? 0x2000 : romData[5] * 0x2000;
        chr = chrRam ? new byte[chrSize] : Arrays.copyOfRange(romData, 16 + prgSize, 16 + prgSize + chrSize);

        nametableMirroring = ((romData[6] & 0b00000001) == 0b00000001) ? NametableMirroring.VERTICAL : NametableMirroring.HORIZONTAL;
    }

    static Cartridge createCartridge(byte[] romData) {
        int mapperNumber = ((romData[6] & 0xFF) >>> 4) | (romData[7] & 0b11110000);
        switch (mapperNumber) {
            case 0:
                return new CartridgeMapper00(romData);
            case 1:
                return new CartridgeMapper01(romData);
            default:
                throw new IllegalArgumentException("Mapper not supported " + mapperNumber);
        }
    }

    abstract int mapAddr(int addr);

    final byte read(int addr) {
        if (addr < 0x6000 || addr > 0xFFFF) {
            throw new IllegalArgumentException("PRG addr out of range: " + Util.getHexString16bit(addr));
        }
        if (addr < 0x8000) {
            return prgRam[(addr - 0x6000) % prgRamSize];
        } else {
            return prg[mapAddr(addr) % prgSize];
        }
    }

    abstract void writePrgRom(int addr, byte value);

    void write(int addr, byte value) {
        if (addr < 0x6000 || addr > 0xFFFF) {
            throw new IllegalArgumentException("PRG addr out of range: " + Util.getHexString16bit(addr));
        }
        if (addr < 0x8000) {
            prgRam[(addr - 0x6000) % prgRamSize] = value;
        } else {
            writePrgRom(addr, value);
        }
    }

    abstract int mapChrAddr(int addr);

    final byte readChr(int addr) {
        if (addr < 0x0000 || addr > 0x1FFF || addr >= chrSize) {
            throw new IllegalArgumentException("CHR addr out of range: " + Util.getHexString16bit(addr)
                + " (size is " + Util.getHexString16bit(addr) + ")");
        }

        return chr[mapChrAddr(addr) % chrSize];
    }
}
