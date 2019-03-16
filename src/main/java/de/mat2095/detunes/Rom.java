package de.mat2095.detunes;

import java.util.Arrays;


class Rom {

    int prgSize;
    int chrSize;
    byte[] prg;
    byte[] chr;
    int mapperNumber;
    boolean persistentMemory;
    NametableMirroring nametableMirroring;

    Rom(byte[] romData) {

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
    }
}
