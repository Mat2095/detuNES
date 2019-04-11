package de.mat2095.detunes;

class Ppu {

    private static final int[] PALETTE = new int[]{
        0x7C7C7C, 0x0000FC, 0x0000BC, 0x4428BC, 0x940084, 0xA80020, 0xA81000, 0x881400,
        0x503000, 0x007800, 0x006800, 0x005800, 0x004058, 0x000000, 0x000000, 0x000000,
        0xBCBCBC, 0x0078F8, 0x0058F8, 0x6844FC, 0xD800CC, 0xE40058, 0xF83800, 0xE45C10,
        0xAC7C00, 0x00B800, 0x00A800, 0x00A844, 0x008888, 0x000000, 0x000000, 0x000000,
        0xF8F8F8, 0x3CBCFC, 0x6888FC, 0x9878F8, 0xF878F8, 0xF85898, 0xF87858, 0xFCA044,
        0xF8B800, 0xB8F818, 0x58D854, 0x58F898, 0x00E8D8, 0x787878, 0x000000, 0x000000,
        0xFCFCFC, 0xA4E4FC, 0xB8B8F8, 0xD8B8F8, 0xF8B8F8, 0xF8A4C0, 0xF0D0B0, 0xFCE0A8,
        0xF8D878, 0xD8F878, 0xB8F8B8, 0xB8F8D8, 0x00FCFC, 0xF8D8F8, 0x000000, 0x000000
    };

    private final byte[] registers;
    private int line, x;
    private Emulator emu;

    Ppu() {
        this.registers = new byte[8];
    }

    void power(Emulator emu) {
        this.emu = emu;
        registers[2] = (byte) 0b10100000;
        line = x = 0; // TODO: verify
    }

    byte read(int addr) {
        if (addr < 0x2000 || addr > 0x3FFF) {
            throw new IllegalArgumentException("PPU addr out of range: " + Util.getHexString16bit(addr));
        }
        addr %= 0x0008;
        switch (addr) {
            case 0x0000:
                throw new IllegalArgumentException("Read from PPU-CTRL not allowed.");
            case 0x0001:
                throw new IllegalArgumentException("Read from PPU-MASK not allowed.");
            case 0x0003:
                throw new IllegalArgumentException("Read from OAM-ADDR not allowed.");
            case 0x0005:
                throw new IllegalArgumentException("Read from PPU-SCROLL not allowed.");
            case 0x0006:
                throw new IllegalArgumentException("Read from PPU-ADDR not allowed.");
            case 0x0002:
                registers[0x0002] = (byte) ((registers[0x0002] & 0xFF) & 0x7F);
            default:
                return registers[addr];
        }
    }

    void write(int addr, byte value) {
        if (addr < 0x2000 || addr > 0x3FFF) {
            throw new IllegalArgumentException("PPU addr out of range: " + Util.getHexString16bit(addr));
        }
        addr %= 0x0008;
        switch (addr) {
            case 0x0002:
                throw new IllegalArgumentException("Write to PPU-STATUS not allowed.");
            default:
                registers[addr] = value;
        }
    }

    void render() {
        if (line == 241 && x == 1) {
            registers[0x0002] = (byte) ((registers[0x0002] & 0xFF) | 0b10000000);
        }
        if (line == 261 && x == 1) {
            registers[0x0002] = (byte) ((registers[0x0002] & 0xFF) & 0b00011111);
        }

        if (line < 240 && x < 256) {
            if (line < 128 && x < 64) {
                int screenAddr = line * 256 + x;
                byte chrValue = emu.readChr(line * 64 + x);
                int screenValue = PALETTE[(chrValue & 0xFF) % PALETTE.length];
                emu.getRenderingContext().setBufferData(screenAddr, screenValue);
            }
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
