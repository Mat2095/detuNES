package de.mat2095.detunes;

class CartridgeMapper01 extends Cartridge {

    private int[] prgMap;
    private int[] chrMap;
    private int bitsWritten;
    private byte shiftReg;
    private byte[] regs;

    CartridgeMapper01(byte[] romData) {
        super(romData);

        if (prgSize == 0x80000) {
            throw new IllegalArgumentException("512KiB PRG-ROM not yet implemented");
        } else if (prgSize != 0x40000) {
            throw new IllegalArgumentException("Invalid PRG-size: " + prgSize);
        }
        if (prgRamSize > 0x8000) {
            throw new IllegalArgumentException("Invalid PRG-RAM-size: " + prgRamSize);
        }
        if (chrSize > 0x20000) {
            throw new IllegalArgumentException("Invalid CHR-size: " + chrSize);
        }

        prgMap = new int[2];
        chrMap = new int[2];
        bitsWritten = 0;
        shiftReg = 0;
        regs = new byte[]{0x0C, 0, 0, 0};
        apply();
    }

    private void mapPrg(int slot, int bank) {
        if (bank < 0) {
            bank += prgSize / 0x4000;
        }
        prgMap[slot] = 0x4000 * bank;
    }

    private void mapChr(int slot, int bank) {
        chrMap[slot] = 0x1000 * bank;
    }

    private void apply() {

        if ((regs[0] & 0b1000) != 0) { // 16KiB PRG
            if ((regs[0] & 0b100) != 0) { // 0x8000 swappable, 0xC000 fixed to last bank
                mapPrg(0, regs[3] & 0xF);
                mapPrg(1, -1);
            } else { // 0x8000 fixed to first bank, 0xC000 swappable
                mapPrg(0, 0);
                mapPrg(1, regs[3] & 0xF);
            }
        } else { // 32KiB PRG
            mapPrg(0, (regs[3] & 0xF) & 0xF8);
            mapPrg(1, (regs[3] & 0xF) | 0x01);
        }

        if ((regs[3] & 0b10000) != 0) {
            // TODO: enable PRG-RAM
        } else {
            // TODO: disable PRG-RAM
        }

        if ((regs[0] & 0b10000) != 0) { // 4KiB CHR
            mapChr(0, regs[1]);
            mapChr(1, regs[2]);
        } else { // 8KiB CHR
            mapChr(0, regs[1] & 0b11000);
            mapChr(1, regs[1] | 0b00001);
        }

        switch (regs[0] & 0b11) {
            case 2:
                nametableMirroring = NametableMirroring.VERTICAL;
                break;
            case 3:
                nametableMirroring = NametableMirroring.HORIZONTAL;
                break;
        }
    }


    @Override
    int mapAddr(int addr) {
        return prgMap[(addr - 0x8000) / 0x4000] + ((addr - 0x8000) % 0x4000);
    }

    @Override
    void writePrgRom(int addr, byte value) { // TODO: ignore if just written
        // Reset:
        if (value < 0) {
            bitsWritten = 0;
            shiftReg = 0;
            regs[0] |= 0x0C;
            apply();
        } else {
            // Write a bit into the temporary register:
            shiftReg = (byte) (((value & 0x01) << 4) | ((shiftReg & 0xFF) >> 1));
            // Finished writing all the bits:
            bitsWritten++;
            if (bitsWritten == 5) {
                regs[(addr >> 13) & 0b11] = shiftReg;
                bitsWritten = 0;
                shiftReg = 0;
                apply();
            }
        }
    }

    @Override
    int mapChrAddr(int addr) {
        return chrMap[addr / 0x1000] + (addr % 0x1000);
    }
}
