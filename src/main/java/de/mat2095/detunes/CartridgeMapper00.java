package de.mat2095.detunes;

class CartridgeMapper00 extends Cartridge {

    CartridgeMapper00(byte[] romData) {
        super(romData);

        if (prgSize != 0x4000 && prgSize != 0x8000) {
            throw new IllegalArgumentException("Invalid PRG-size: " + prgSize);
        }
        if (prgRamSize != 0x2000) {
            throw new IllegalArgumentException("Invalid PRG-RAM-size: " + prgRamSize);
        }
        if (chrSize != 0x2000) {
            throw new IllegalArgumentException("Invalid CHR-size: " + chrSize);
        }
    }

    @Override
    int mapCpuAddr(int addr) {
        return addr - 0x8000;
    }

    @Override
    void writePrgRom(int addr, byte value) {
        throw new IllegalArgumentException("Writing to PRG-ROM not supported for NROM");
    }

    @Override
    int mapPpuAddr(int addr) {
        return addr;
    }
}
