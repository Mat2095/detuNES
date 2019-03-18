package de.mat2095.detunes;

public class Cpu {

    private static final String[] OP_NAMES = new String[256];

    static {
        OP_NAMES[0x08] = "PHP";
        OP_NAMES[0x09] = "ORA<imm>";
        OP_NAMES[0x10] = "br<N,0>";
        OP_NAMES[0x18] = "flag<C,0>";
        OP_NAMES[0x20] = "JSR";
        OP_NAMES[0x24] = "BIT<zp>";
        OP_NAMES[0x28] = "PLP";
        OP_NAMES[0x29] = "AND<imm>";
        OP_NAMES[0x30] = "br<N,1>";
        OP_NAMES[0x38] = "flag<C,1>";
        OP_NAMES[0x48] = "PHA";
        OP_NAMES[0x4C] = "JMP";
        OP_NAMES[0x50] = "br<V,0>";
        OP_NAMES[0x60] = "RTS";
        OP_NAMES[0x68] = "PLA";
        OP_NAMES[0x70] = "br<V,1>";
        OP_NAMES[0x78] = "flag<I,1>";
        OP_NAMES[0x85] = "st<A,zp>";
        OP_NAMES[0x86] = "st<X,zp>";
        OP_NAMES[0x8D] = "st<A,abs>";
        OP_NAMES[0x90] = "br<C,0>";
        OP_NAMES[0x9A] = "tr<X,S>";
        OP_NAMES[0xA2] = "ld<X,imm>";
        OP_NAMES[0xA9] = "ld<A,imm>";
        OP_NAMES[0xAD] = "ld<A,abs>";
        OP_NAMES[0xB0] = "br<C,1>";
        OP_NAMES[0xC9] = "CMP<A,imm>";
        OP_NAMES[0xEA] = "NOP";
        OP_NAMES[0xD0] = "br<Z,0>";
        OP_NAMES[0xD8] = "flag<D,0>";
        OP_NAMES[0xF0] = "br<Z,1>";
        OP_NAMES[0xF8] = "flag<D,1>";
    }

    byte[] ram;
    Cartridge cartridge;

    // registers
    byte regAcc, regX, regY, regSP;
    int pc;
    boolean flagC, flagZ, flagI, flagD, flagV, flagN; // Carry, Zero, Interrupt, Decimal, oVerflow, Negative

    Cpu(Cartridge cartridge) {
        this.ram = new byte[0x0800]; // 2KiB
        this.cartridge = cartridge;
    }

    byte read(int addr) {
        if (addr < 0x0000 || addr > 0xFFFF) {
            throw new IllegalArgumentException("CPU addr out of range: 0x" + Integer.toHexString(addr));
        }

        if (addr < 0x2000) {
            return ram[addr % 0x0800];
        } else if (addr < 0x4000) {
            throw new IllegalArgumentException("ppu registers not yet implemented: 0x" + Integer.toHexString(addr));
            // addr % 0x0008
        } else if (addr < 0x4018) {
            throw new IllegalArgumentException("apu / io registers not yet implemented");
        } else if (addr < 0x4020) {
            throw new IllegalArgumentException("api / io test functionality not yet implemented");
        } else {
            return cartridge.read(addr);
        }
    }

    int readDouble(int addr) {
        return (read(addr) & 0xFF) | ((read(addr + 1) & 0xFF) << 8);
    }

    void write(int addr, byte value) {
        if (addr < 0x0000 || addr > 0xFFFF) {
            throw new IllegalArgumentException("CPU addr out of range: 0x" + Integer.toHexString(addr));
        }

        if (addr < 0x2000) {
            ram[addr % 0x0800] = value;
        } else if (addr < 0x4000) {
            throw new IllegalArgumentException("ppu registers not yet implemented: 0x" + Integer.toHexString(addr));
            // addr % 0x0008
        } else if (addr < 0x4018) {
            throw new IllegalArgumentException("apu / io registers not yet implemented");
        } else if (addr < 0x4020) {
            throw new IllegalArgumentException("api / io test functionality not yet implemented");
        } else {
            cartridge.write(addr, value);
        }
    }

    byte getP() {
        byte p = 0b00100000;

        p |= flagC ? 0b00000001 : 0;
        p |= flagZ ? 0b00000010 : 0;
        p |= flagI ? 0b00000100 : 0;
        p |= flagD ? 0b00001000 : 0;
        p |= flagV ? 0b01000000 : 0;
        p |= flagN ? 0b10000000 : 0;

        return p;
    }

    void setP(byte p) {
        flagC = (p & 0b00000001) != 0;
        flagZ = (p & 0b00000010) != 0;
        flagI = (p & 0b00000100) != 0;
        flagD = (p & 0b00001000) != 0;
        flagV = (p & 0b01000000) != 0;
        flagN = (p & 0b10000000) != 0;
    }

    void updateNZ(byte b) {
        flagN = b < 0;
        flagZ = b == 0;
    }

    void pushStack(byte b) {
        write(0x100 + (regSP--), b);
    }

    byte popStack() {
        return read(0x100 + (++regSP));
    }

    void power() {
        setP((byte) 0b00110100);
        regAcc = regX = regY = 0;
        regSP = (byte) 0xFD;

        pc = readDouble(0xFFFC);
    }

    void run() {
        while (true) {
            exec();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void exec() {

        byte op = read(pc);
        System.out.println("exec"
            + "\u00a0  SP: " + Util.getHexString(regSP)
            + "\u00a0 X: " + Util.getHexString(regX)
            + "\u00a0 Y: " + Util.getHexString(regY)
            + "\u00a0 Flags: " + Util.getBinString(getP())
            + "\u00a0 pc: 0x" + Integer.toHexString(pc)
            + "\u00a0 Acc: " + Util.getHexString(regAcc)
            + "\u00a0  op: " + Util.getHexString(op)
            + "\u00a0 " + (OP_NAMES[op & 0xFF] != null ? OP_NAMES[op & 0xFF] : ""));
        pc++;

        switch (op) {
            case 0x08: { // PHP
                byte p = (byte) (getP() | 0b00010000);
                pushStack(p);
                break;
            }
            case 0x09: { // ORA<imm>
                regAcc |= read(pc++);
                updateNZ(regAcc);
                break;
            }
            case 0x10: { // br<N,0>
                byte offset = read(pc++);
                if (!flagN) {
                    pc += offset;
                }
                break;
            }
            case 0x18: { // flag<C,0>
                flagC = false;
                break;
            }
            case 0x20: { // JSR
                int t = pc + 1;
                pushStack((byte) (t >>> 8));
                pushStack((byte) (t & 0xFF));
                pc = readDouble(pc);
                break;
            }
            case 0x24: { // BIT<zp>
                byte p = read(read(pc++));
                flagZ = (regAcc & p) == 0;
                flagN = (p & 0x80) != 0;
                flagV = (p & 0x40) != 0;
                break;
            }
            case 0x28: { // PLP
                setP(popStack());
                break;
            }
            case 0x29: { // AND<imm>
                regAcc &= read(pc++);
                updateNZ(regAcc);
                break;
            }
            case 0x30: { // br<N,1>
                byte offset = read(pc++);
                if (flagN) {
                    pc += offset;
                }
                break;
            }
            case 0x38: { // flag<C,1>
                flagC = true;
                break;
            }
            case 0x48: { // PHA
                pushStack(regAcc);
                break;
            }
            case 0x4C: { // JMP
                pc = readDouble(pc);
                break;
            }
            case 0x50: { // br<V,0>
                byte offset = read(pc++);
                if (!flagV) {
                    pc += offset;
                }
                break;
            }
            case 0x60: { // RTS
                byte t1 = popStack();
                byte t2 = popStack();
                pc = ((t2 & 0xFF) << 8 | (t1 & 0xFF)) + 1;
                break;
            }
            case 0x68: { // PLA
                regAcc = popStack();
                updateNZ(regAcc);
                break;
            }
            case 0x70: { // br<V,1>
                byte offset = read(pc++);
                if (flagV) {
                    pc += offset;
                }
                break;
            }
            case 0x78: { // flag<I,1>
                flagI = false;
                break;
            }
            case (byte) 0x85: { // st<A,zp>
                write(read(pc++) & 0xFF, regAcc);
                break;
            }
            case (byte) 0x86: { // st<X,zp>
                write(read(pc++) & 0xFF, regX);
                break;
            }
            case (byte) 0x8D: { // st<A,abs>
                int addr = readDouble(pc);
                pc += 2;
                write(addr, regAcc);
                break;
            }
            case (byte) 0x90: { // br<C,0>
                byte offset = read(pc++);
                if (!flagC) {
                    pc += offset;
                }
                break;
            }
            case (byte) 0x9A: { // tr<X,S>
                regSP = regX;
                updateNZ(regSP);
                break;
            }
            case (byte) 0xA2: { // ld<X,imm>
                regX = read(pc++);
                updateNZ(regX);
                break;
            }
            case (byte) 0xA9: { // ld<A,imm>
                regAcc = read(pc++);
                updateNZ(regAcc);
                break;
            }
            case (byte) 0xAD: { // ld<A,abs>
                regAcc = read(readDouble(pc));
                pc += 2;
                updateNZ(regAcc);
                break;
            }
            case (byte) 0xB0: { // br<C,1>
                byte offset = read(pc++);
                if (flagC) {
                    pc += offset;
                }
                break;
            }
            case (byte) 0xC9: { // CMP<A,imm>
                byte m = read(pc++);
                updateNZ((byte) (regAcc - m));
                flagC = (regAcc & 0xFF) >= (m & 0xFF);
                break;
            }
            case (byte) 0xEA: { // NOP
                break;
            }
            case (byte) 0xD0: { // br<Z,0>
                byte offset = read(pc++);
                if (!flagZ) {
                    pc += offset;
                }
                break;
            }
            case (byte) 0xD8: { // flag<D,0>
                flagD = false;
                break;
            }
            case (byte) 0xF0: { // br<Z,1>
                byte offset = read(pc++);
                if (flagZ) {
                    pc += offset;
                }
                break;
            }
            case (byte) 0xF8: { // flag<D,1>
                flagD = true;
                break;
            }
            default:
                throw new IllegalStateException("Invalid OP-code: " + Util.getHexString(op));
        }

    }
}
