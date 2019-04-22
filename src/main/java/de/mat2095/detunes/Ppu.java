package de.mat2095.detunes;

class Ppu {

    static final int[] PALETTE = new int[]{
        0x7C7C7C, 0x0000FC, 0x0000BC, 0x4428BC, 0x940084, 0xA80020, 0xA81000, 0x881400,
        0x503000, 0x007800, 0x006800, 0x005800, 0x004058, 0x000000, 0x000000, 0x000000,
        0xBCBCBC, 0x0078F8, 0x0058F8, 0x6844FC, 0xD800CC, 0xE40058, 0xF83800, 0xE45C10,
        0xAC7C00, 0x00B800, 0x00A800, 0x00A844, 0x008888, 0x000000, 0x000000, 0x000000,
        0xF8F8F8, 0x3CBCFC, 0x6888FC, 0x9878F8, 0xF878F8, 0xF85898, 0xF87858, 0xFCA044,
        0xF8B800, 0xB8F818, 0x58D854, 0x58F898, 0x00E8D8, 0x787878, 0x000000, 0x000000,
        0xFCFCFC, 0xA4E4FC, 0xB8B8F8, 0xD8B8F8, 0xF8B8F8, 0xF8A4C0, 0xF0D0B0, 0xFCE0A8,
        0xF8D878, 0xD8F878, 0xB8F8B8, 0xB8F8D8, 0x00FCFC, 0xF8D8F8, 0x000000, 0x000000
    };

    // registers

    private boolean regCtrlV, regCtrlP, regCtrlH; // v-blank, master-slave, sprite-height
    private boolean regCtrlB, regCtrlS, regCtrlI; // bg-pattern-table, sprite-pattern-table, vram-inc
    private int regCtrlN; // base-nametable-address

    private boolean regMaskB, regMaskG, regMaskR; // emphasize: blue, green, red
    private boolean regMaskSp, regMaskBg; // show: sprites, background
    private boolean regMaskSpL, regMaskBgL; // show in left 8px: sprites, background
    private boolean regMaskGr; // grayscale

    private boolean regStatusV, regStatusS, regStatusO; // v-blank started, sprite 0 hit, sprite overflow

    private byte regOamAddr;

    private int scrollX;
    private int scrollY;

    private int ppuAddr;

    private byte lastValueWritten;
    private byte readBuffer;

    private final byte[][] nametables;
    private final byte[] palette;
    private final byte[] oam;
    private int line, x;
    private Emulator emu;

    Ppu() {
        this.nametables = new byte[2][0x0400];
        this.palette = new byte[0x20];
        this.oam = new byte[0x0100];
    }

    void power(Emulator emu) {
        this.emu = emu;
        setRegStatus((byte) 0b10100000);
        line = x = 0; // TODO: verify
    }

    byte getRegCtrl() {
        byte regCtrl = 0;

        regCtrl |= regCtrlN & 0b00000011;
        regCtrl |= regCtrlI ? 0b00000100 : 0;
        regCtrl |= regCtrlS ? 0b00001000 : 0;
        regCtrl |= regCtrlB ? 0b00010000 : 0;
        regCtrl |= regCtrlH ? 0b00100000 : 0;
        regCtrl |= regCtrlP ? 0b01000000 : 0;
        regCtrl |= regCtrlV ? 0b10000000 : 0;

        return regCtrl;
    }

    private void setRegCtrl(byte regCtrl) {
        regCtrlN = regCtrl & 0b00000011;
        regCtrlI = (regCtrl & 0b00000100) != 0;
        regCtrlS = (regCtrl & 0b00001000) != 0;
        regCtrlB = (regCtrl & 0b00010000) != 0;
        regCtrlH = (regCtrl & 0b00100000) != 0;
        regCtrlP = (regCtrl & 0b01000000) != 0;
        regCtrlV = (regCtrl & 0b10000000) != 0;
    }

    byte getRegMask() {
        byte regMask = 0;

        regMask |= regMaskGr ? 0b00000001 : 0;
        regMask |= regMaskBgL ? 0b00000010 : 0;
        regMask |= regMaskSpL ? 0b00000100 : 0;
        regMask |= regMaskBg ? 0b00001000 : 0;
        regMask |= regMaskSp ? 0b00010000 : 0;
        regMask |= regMaskR ? 0b00100000 : 0;
        regMask |= regMaskG ? 0b01000000 : 0;
        regMask |= regMaskB ? 0b10000000 : 0;

        return regMask;
    }

    private void setRegMask(byte regMask) {
        regMaskGr = (regMask & 0b00000001) != 0;
        regMaskBgL = (regMask & 0b00000010) != 0;
        regMaskSpL = (regMask & 0b00000100) != 0;
        regMaskBg = (regMask & 0b00001000) != 0;
        regMaskSp = (regMask & 0b00010000) != 0;
        regMaskR = (regMask & 0b00100000) != 0;
        regMaskG = (regMask & 0b01000000) != 0;
        regMaskB = (regMask & 0b10000000) != 0;
    }

    byte getRegStatus() {
        byte regStatus = 0;

        regStatus |= regStatusO ? 0b00100000 : 0;
        regStatus |= regStatusS ? 0b01000000 : 0;
        regStatus |= regStatusV ? 0b10000000 : 0;

        return regStatus;
    }

    void setRegStatus(byte regStatus) {
        regStatusO = (regStatus & 0b00100000) != 0;
        regStatusS = (regStatus & 0b01000000) != 0;
        regStatusV = (regStatus & 0b10000000) != 0;
    }

    byte readCpu(int addr) {
        if (addr < 0x2000 || addr > 0x3FFF) {
            throw new IllegalArgumentException("CPU addr out of range at PPU: " + Util.getHexString16bit(addr));
        }
        addr %= 0x0008;
        if (emu.getRunConfig().debugPpuPrintAccesses) {
            System.out.println("PPU rd: " + Util.getHexString16bit(addr));
        }
        byte result;
        switch (addr) {
            case 0x0000:
                throw new IllegalArgumentException("Read from PPU-CTRL not allowed.");
            case 0x0001:
                throw new IllegalArgumentException("Read from PPU-MASK not allowed.");
            case 0x0002:
                result = (byte) (getRegStatus() | (lastValueWritten & 0b00011111));
                regStatusV = false;
                return result;
            case 0x0003:
                throw new IllegalArgumentException("Read from OAM-ADDR not allowed.");
            case 0x0004:
                return oam[regOamAddr & 0xFF];
            case 0x0005:
                throw new IllegalArgumentException("Read from PPU-SCROLL not allowed.");
            case 0x0006:
                throw new IllegalArgumentException("Read from PPU-ADDR not allowed.");
            case 0x0007:
                if (ppuAddr < 0x3F00) {
                    result = readBuffer;
                    readBuffer = emu.readPpu(ppuAddr % 0x4000);
                } else {
                    readBuffer = emu.readPpu(ppuAddr % 0x4000);
                    result = readBuffer;
                }
                ppuAddr += regCtrlI ? 32 : 1;
                return result;
            default:
                throw new Error("(addr % 8) has invalid value");
        }
    }

    void writeCpu(int addr, byte value) {
        if (addr < 0x2000 || addr > 0x3FFF) {
            throw new IllegalArgumentException("CPU addr out of range at PPU: " + Util.getHexString16bit(addr));
        }
        addr %= 0x0008;
        if (emu.getRunConfig().debugPpuPrintAccesses) {
            System.out.println("PPU wr: " + Util.getHexString16bit(addr) + " "
                + (addr <= 2 ? Util.getBinString(value) : Util.getHexString(value)));
        }
        switch (addr) {
            case 0x0000:
                boolean oldRegCtrlV = regCtrlV;
                setRegCtrl(value);
                if (regStatusV && !oldRegCtrlV && regCtrlV) {
                    emu.fireNmi();
                }
                break;
            case 0x0001:
                setRegMask(value);
                break;
            case 0x0002:
                throw new IllegalArgumentException("Write to PPU-STATUS not allowed.");
            case 0x0003:
                regOamAddr = value;
                break;
            case 0x0004:
                oam[regOamAddr & 0xFF] = value;
                regOamAddr++;
                break;
            case 0x0005:
                scrollY = scrollX;
                scrollX = value & 0xFF;
                break;
            case 0x0006:
                ppuAddr = (ppuAddr << 8) | (value & 0xFF);
                break;
            case 0x0007:
                emu.writePpu(ppuAddr % 0x4000, value);
                ppuAddr += regCtrlI ? 32 : 1;
                break;
            default:
                throw new Error("(addr % 8) has invalid value");
        }

        lastValueWritten = value;
    }

    byte readPpu(int addr) {
        if (addr >= 0x2000 && addr < 0x3F00) {
            int quadrant = (addr / 0x0400) % 4;
            addr %= 0x0400;
            switch (emu.getNametableMirroring()) {
                case HORIZONTAL:
                    return nametables[quadrant / 2][addr];
                case VERTICAL:
                    return nametables[quadrant % 2][addr];
                default:
                    throw new Error("Invalid Nametable-Mirroring");
            }
        } else if (addr >= 0x3F00 && addr < 0x4000) {
            addr %= 0x20;
            if ((addr & 0x13) == 0x10) {
                addr &= 0x0F;
            }
            return palette[addr];
        } else {
            throw new IllegalArgumentException("PPU addr out of range at PPU: " + Util.getHexString16bit(addr));
        }
    }

    void writePpu(int addr, byte value) {
        if (addr >= 0x2000 && addr < 0x3F00) {
            int quadrant = (addr / 0x0400) % 4;
            addr %= 0x0400;
            switch (emu.getNametableMirroring()) {
                case HORIZONTAL:
                    nametables[quadrant / 2][addr] = value;
                    break;
                case VERTICAL:
                    nametables[quadrant % 2][addr] = value;
                    break;
                default:
                    throw new Error("Invalid Nametable-Mirroring");
            }
        } else if (addr >= 0x3F00 && addr < 0x4000) {
            addr %= 0x20;
            if ((addr & 0x13) == 0x10) {
                addr &= 0x0F;
            }
            palette[addr] = value;
        } else {
            throw new IllegalArgumentException("PPU addr out of range at PPU: " + Util.getHexString16bit(addr));
        }
    }

    void render() {
        if (line == 241 && x == 1) {
            regStatusV = true;
            if (regCtrlV) {
                emu.fireNmi();
            }
        }
        if (line == 261 && x == 1) {
            regStatusV = false;
            regStatusS = false;
            regStatusO = false;
        }

        if (line < 240 && x < 256) {
            int screenAddr = line * 256 + x;
            byte chrValue = emu.readPpu((line < 128 && x < 128) ? line * 128 + x : 0x3F00);
            emu.getRenderingContext().setBufferData(screenAddr, PALETTE[chrValue & 0x3F]);
        } else if (x == 256) {
            emu.getRenderingContext().sync();
        }

        x++;
        if (x > 341) {
            x = 0;
            line++;
            if (line > 262) {
                line = 0;
            }
        }
    }
}
