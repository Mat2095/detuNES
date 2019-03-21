package de.mat2095.detunes;

public class Cpu {

    private static final String[] OP_NAMES = new String[256];

    static {
        OP_NAMES[0x01] = "ORA<izx>";
        OP_NAMES[0x05] = "ORA<zp>";
        OP_NAMES[0x06] = "ASL<zp>";
        OP_NAMES[0x08] = "PHP";
        OP_NAMES[0x09] = "ORA<imm>";
        OP_NAMES[0x0A] = "ASL_A";
        OP_NAMES[0x0D] = "ORA<abs>";
        OP_NAMES[0x0E] = "ASL<abs>";
        OP_NAMES[0x10] = "br<N,0>";
        OP_NAMES[0x11] = "ORA<izy>";
        OP_NAMES[0x18] = "flag<C,0>";
        OP_NAMES[0x20] = "JSR";
        OP_NAMES[0x21] = "AND<izx>";
        OP_NAMES[0x24] = "BIT<zp>";
        OP_NAMES[0x25] = "AND<zp>";
        OP_NAMES[0x26] = "ROL<zp>";
        OP_NAMES[0x28] = "PLP";
        OP_NAMES[0x29] = "AND<imm>";
        OP_NAMES[0x2A] = "ROL_A";
        OP_NAMES[0x2C] = "BIT<abs>";
        OP_NAMES[0x2D] = "AND<abs>";
        OP_NAMES[0x2E] = "ROL<abs>";
        OP_NAMES[0x30] = "br<N,1>";
        OP_NAMES[0x31] = "AND<izy>";
//        OP_NAMES[0x35] = "AND<zpx>";
        OP_NAMES[0x38] = "flag<C,1>";
        OP_NAMES[0x40] = "RTI";
        OP_NAMES[0x41] = "EOR<izx>";
        OP_NAMES[0x45] = "EOR<zp>";
        OP_NAMES[0x46] = "LSR<zp>";
        OP_NAMES[0x48] = "PHA";
        OP_NAMES[0x49] = "EOR<imm>";
        OP_NAMES[0x4A] = "LSR_A";
        OP_NAMES[0x4C] = "JMP";
        OP_NAMES[0x4D] = "EOR<abs>";
        OP_NAMES[0x4E] = "LSR<abs>";
        OP_NAMES[0x50] = "br<V,0>";
        OP_NAMES[0x51] = "EOR<izy>";
        OP_NAMES[0x60] = "RTS";
        OP_NAMES[0x61] = "ADC<izx>";
        OP_NAMES[0x65] = "ADC<zp>";
        OP_NAMES[0x66] = "ROR<zp>";
        OP_NAMES[0x68] = "PLA";
        OP_NAMES[0x69] = "ADC<imm>";
        OP_NAMES[0x6A] = "ROR_A";
        OP_NAMES[0x6D] = "ADC<abs>";
        OP_NAMES[0x6E] = "ROR<abs>";
        OP_NAMES[0x70] = "br<V,1>";
        OP_NAMES[0x71] = "ADC<izy>";
        OP_NAMES[0x78] = "flag<I,1>";
        OP_NAMES[0x81] = "st<A,izx>";
        OP_NAMES[0x84] = "st<Y,zp>";
        OP_NAMES[0x85] = "st<A,zp>";
        OP_NAMES[0x86] = "st<X,zp>";
        OP_NAMES[0x88] = "dec<Y>";
        OP_NAMES[0x8A] = "tr<X,A>";
        OP_NAMES[0x8C] = "st<Y,abs>";
        OP_NAMES[0x8D] = "st<A,abs>";
        OP_NAMES[0x8E] = "st<X,abs>";
        OP_NAMES[0x90] = "br<C,0>";
        OP_NAMES[0x91] = "st<A,izy>";
        OP_NAMES[0x98] = "tr<Y,A>";
        OP_NAMES[0x9A] = "tr<X,S>";
        OP_NAMES[0xA0] = "ld<Y,imm>";
        OP_NAMES[0xA1] = "ld<A,izx>";
        OP_NAMES[0xA2] = "ld<X,imm>";
        OP_NAMES[0xA4] = "ld<Y,zp>";
        OP_NAMES[0xA5] = "ld<A,zp>";
        OP_NAMES[0xA6] = "ld<X,zp>";
        OP_NAMES[0xA8] = "tr<A,Y>";
        OP_NAMES[0xA9] = "ld<A,imm>";
        OP_NAMES[0xAA] = "tr<A,X>";
        OP_NAMES[0xAC] = "ld<Y,abs>";
        OP_NAMES[0xAD] = "ld<A,abs>";
        OP_NAMES[0xAE] = "ld<X,abs>";
        OP_NAMES[0xB0] = "br<C,1>";
        OP_NAMES[0xB1] = "ld<A,izy>";
        OP_NAMES[0xB8] = "flag<V,0>";
        OP_NAMES[0xBA] = "tr<S,X>";
        OP_NAMES[0xC0] = "cmp<Y,imm>";
        OP_NAMES[0xC1] = "cmp<A,izx>";
        OP_NAMES[0xC4] = "cmp<Y,zp>";
        OP_NAMES[0xC5] = "cmp<A,zp>";
        OP_NAMES[0xC6] = "DEC<zp>";
        OP_NAMES[0xC8] = "inc<Y>";
        OP_NAMES[0xC9] = "cmp<A,imm>";
        OP_NAMES[0xCA] = "dec<X>";
        OP_NAMES[0xCC] = "cmp<Y,abs>";
        OP_NAMES[0xCD] = "cmp<A,abs>";
        OP_NAMES[0xCE] = "DEC<abs>";
        OP_NAMES[0xD0] = "br<Z,0>";
        OP_NAMES[0xD1] = "cmp<A,izy>";
        OP_NAMES[0xD8] = "flag<D,0>";
        OP_NAMES[0xE0] = "cmp<X,imm>";
        OP_NAMES[0xE1] = "SBC<izx>";
        OP_NAMES[0xE4] = "cmp<X,zp>";
        OP_NAMES[0xE5] = "SBC<zp>";
        OP_NAMES[0xE6] = "INC<zp>";
        OP_NAMES[0xE8] = "inc<X>";
        OP_NAMES[0xE9] = "SBC<imm>";
        OP_NAMES[0xEA] = "NOP";
        OP_NAMES[0xEC] = "cmp<X,abs>";
        OP_NAMES[0xED] = "SBC<abs>";
        OP_NAMES[0xEE] = "INC<abs>";
        OP_NAMES[0xF0] = "br<Z,1>";
        OP_NAMES[0xF1] = "SBC<izy>";
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

    int readIzxAddr() {
        int ai1 = ((read(pc++) & 0xFF) + regX) & 0xFF;
        int ai2 = (ai1 + 1) & 0xFF;
        return (read(ai1) & 0xFF) | ((read(ai2) & 0xFF) << 8);
    }

    int readIzyAddr() {
        int ai1 = read(pc++) & 0xFF;
        int ai2 = (ai1 + 1) & 0xFF;
        return (((read(ai1) & 0xFF) | ((read(ai2) & 0xFF) << 8)) + (regY & 0xFF)) & 0xFFFF;
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
                Thread.sleep(1);
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
            case 0x01: { // ORA<izx>
                regAcc |= read(readIzxAddr());
                updateNZ(regAcc);
                break;
            }
            case 0x05: { // ORA<zp>
                regAcc |= read(read(pc++) & 0xFF);
                updateNZ(regAcc);
                break;
            }
            case 0x08: { // PHP
                byte p = (byte) (getP() | 0b00010000);
                pushStack(p);
                break;
            }
            case 0x06: { // ASL<zp>
                int addr = read(pc++) & 0xFF;
                byte p = read(addr);
                flagC = (p & 0x80) == 0x80;
                p = (byte) ((p & 0xFF) << 1);
                write(addr, p);
                updateNZ(p);
                break;
            }
            case 0x09: { // ORA<imm>
                regAcc |= read(pc++);
                updateNZ(regAcc);
                break;
            }
            case 0x0A: { // ASL_A
                flagC = (regAcc & 0x80) == 0x80;
                regAcc = (byte) ((regAcc & 0xFF) << 1);
                updateNZ(regAcc);
                break;
            }
            case 0x0D: { // ORA<abs>
                regAcc |= read(readDouble(pc));
                pc += 2;
                updateNZ(regAcc);
                break;
            }
            case 0x0E: { // ASL<abs>
                int addr = readDouble(pc);
                pc += 2;
                byte p = read(addr);
                flagC = (p & 0x80) == 0x80;
                p = (byte) ((p & 0xFF) << 1);
                write(addr, p);
                updateNZ(p);
                break;
            }
            case 0x10: { // br<N,0>
                byte offset = read(pc++);
                if (!flagN) {
                    pc += offset;
                }
                break;
            }
            case 0x11: { // ORA<izy>
                regAcc |= read(readIzyAddr());
                updateNZ(regAcc);
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
            case 0x21: { // AND<izx>
                regAcc &= read(readIzxAddr());
                updateNZ(regAcc);
                break;
            }
            case 0x24: { // BIT<zp>
                byte p = read(read(pc++) & 0xFF);
                flagZ = (regAcc & p) == 0;
                flagN = (p & 0x80) != 0;
                flagV = (p & 0x40) != 0;
                break;
            }
            case 0x25: { // AND<zp>
                regAcc &= read(read(pc++) & 0xFF);
                updateNZ(regAcc);
                break;
            }
            case 0x26: { // ROL<zp>
                int addr = read(pc++) & 0xFF;
                byte p = read(addr);
                byte newP = (byte) ((flagC ? 0x01 : 0x00) | ((p & 0xFF) << 1));
                flagC = (p & 0x80) == 0x80;
                write(addr, newP);
                updateNZ(newP);
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
            case 0x2A: { // ROL_A
                byte newAcc = (byte) ((flagC ? 0x01 : 0x00) | ((regAcc & 0xFF) << 1));
                flagC = (regAcc & 0x80) == 0x80;
                regAcc = newAcc;
                updateNZ(regAcc);
                break;
            }
            case 0x2C: { // BIT<abs>
                byte p = read(readDouble(pc));
                pc += 2;
                flagZ = (regAcc & p) == 0;
                flagN = (p & 0x80) != 0;
                flagV = (p & 0x40) != 0;
                break;
            }
            case 0x2D: { // AND<abs>
                regAcc &= read(readDouble(pc));
                pc += 2;
                updateNZ(regAcc);
                break;
            }
            case 0x2E: { // ROL<abs>
                int addr = readDouble(pc);
                pc += 2;
                byte p = read(addr);
                byte newP = (byte) ((flagC ? 0x01 : 0x00) | ((p & 0xFF) << 1));
                flagC = (p & 0x80) == 0x80;
                write(addr, newP);
                updateNZ(newP);
                break;
            }
            case 0x30: { // br<N,1>
                byte offset = read(pc++);
                if (flagN) {
                    pc += offset;
                }
                break;
            }
            case 0x31: { // AND<izy>
                regAcc &= read(readIzyAddr());
                updateNZ(regAcc);
                break;
            }
//            case 0x35: { // AND<zpx>
//                byte p = read((read(pc++) & 0xFF + regX & 0xFF) & 0xFF);
//                regAcc &= p;
//                updateNZ(regAcc);
//                break;
//            }
            case 0x38: { // flag<C,1>
                flagC = true;
                break;
            }
            case 0x40: { // RTI
                setP(popStack());
                byte t1 = popStack();
                byte t2 = popStack();
                pc = (t2 & 0xFF) << 8 | (t1 & 0xFF);
                break;
            }
            case 0x41: { // EOR<izx>
                regAcc ^= read(readIzxAddr());
                updateNZ(regAcc);
                break;
            }
            case 0x45: { // EOR<zp>
                regAcc ^= read(read(pc++) & 0xFF);
                updateNZ(regAcc);
                break;
            }
            case 0x46: { // LSR<zp>
                int addr = read(pc++) & 0xFF;
                byte p = read(addr);
                flagC = (p & 0x01) == 0x01;
                p = (byte) ((p & 0xFF) >>> 1);
                write(addr, p);
                updateNZ(p);
                break;
            }
            case 0x48: { // PHA
                pushStack(regAcc);
                break;
            }
            case 0x49: { // EOR<imm>
                regAcc ^= read(pc++);
                updateNZ(regAcc);
                break;
            }
            case 0x4A: { // LSR_A
                flagC = (regAcc & 0x01) == 0x01;
                regAcc = (byte) ((regAcc & 0xFF) >>> 1);
                updateNZ(regAcc);
                break;
            }
            case 0x4C: { // JMP
                pc = readDouble(pc);
                break;
            }
            case 0x4D: { // EOR<abs>
                regAcc ^= read(readDouble(pc));
                pc += 2;
                updateNZ(regAcc);
                break;
            }
            case 0x4E: { // LSR<abs>
                int addr = readDouble(pc);
                pc += 2;
                byte p = read(addr);
                flagC = (p & 0x01) == 0x01;
                p = (byte) ((p & 0xFF) >>> 1);
                write(addr, p);
                updateNZ(p);
                break;
            }
            case 0x50: { // br<V,0>
                byte offset = read(pc++);
                if (!flagV) {
                    pc += offset;
                }
                break;
            }
            case 0x51: { // EOR<izy>
                regAcc ^= read(readIzyAddr());
                updateNZ(regAcc);
                break;
            }
            case 0x60: { // RTS
                byte t1 = popStack();
                byte t2 = popStack();
                pc = ((t2 & 0xFF) << 8 | (t1 & 0xFF)) + 1;
                break;
            }
            case 0x61: { // ADC<izx>
                byte p = read(readIzxAddr());
                int result = (regAcc & 0xFF) + (p & 0xFF) + (flagC ? 1 : 0);
                flagC = result > 0xFF;

                // P[V] = ~(x^y) & (x^r) & 0x80;
                flagV = ((regAcc < 0) != ((byte) result < 0)) && ((regAcc < 0) == (p < 0));
                // TODO: write better

                regAcc = (byte) result;
                updateNZ(regAcc);
                break;
            }
            case 0x65: { // ADC<zp>
                byte p = read(read(pc++) & 0xFF);
                int result = (regAcc & 0xFF) + (p & 0xFF) + (flagC ? 1 : 0);
                flagC = result > 0xFF;

                // P[V] = ~(x^y) & (x^r) & 0x80;
                flagV = ((regAcc < 0) != ((byte) result < 0)) && ((regAcc < 0) == (p < 0));
                // TODO: write better

                regAcc = (byte) result;
                updateNZ(regAcc);
                break;
            }
            case 0x66: { // ROR<zp>
                int addr = read(pc++) & 0xFF;
                byte p = read(addr);
                byte newP = (byte) ((flagC ? 0x80 : 0x00) | ((p & 0xFF) >>> 1));
                flagC = (p & 0x01) == 0x01;
                write(addr, newP);
                updateNZ(newP);
                break;
            }
            case 0x68: { // PLA
                regAcc = popStack();
                updateNZ(regAcc);
                break;
            }
            case 0x69: { // ADC<imm>
                byte p = read(pc++);
                int result = (regAcc & 0xFF) + (p & 0xFF) + (flagC ? 1 : 0);
                flagC = result > 0xFF;

                // P[V] = ~(x^y) & (x^r) & 0x80;
                flagV = ((regAcc < 0) != ((byte) result < 0)) && ((regAcc < 0) == (p < 0));
                // TODO: write better

                regAcc = (byte) result;
                updateNZ(regAcc);
                break;
            }
            case 0x6A: { // ROR_A
                byte newAcc = (byte) ((flagC ? 0x80 : 0x00) | ((regAcc & 0xFF) >>> 1));
                flagC = (regAcc & 0x01) == 0x01;
                regAcc = newAcc;
                updateNZ(regAcc);
                break;
            }
            case 0x6D: { // ADC<abs>
                byte p = read(readDouble(pc));
                pc += 2;
                int result = (regAcc & 0xFF) + (p & 0xFF) + (flagC ? 1 : 0);
                flagC = result > 0xFF;

                // P[V] = ~(x^y) & (x^r) & 0x80;
                flagV = ((regAcc < 0) != ((byte) result < 0)) && ((regAcc < 0) == (p < 0));
                // TODO: write better

                regAcc = (byte) result;
                updateNZ(regAcc);
                break;
            }
            case 0x6E: { // ROR<abs>
                int addr = readDouble(pc);
                pc += 2;
                byte p = read(addr);
                byte newP = (byte) ((flagC ? 0x80 : 0x00) | ((p & 0xFF) >>> 1));
                flagC = (p & 0x01) == 0x01;
                write(addr, newP);
                updateNZ(newP);
                break;
            }
            case 0x70: { // br<V,1>
                byte offset = read(pc++);
                if (flagV) {
                    pc += offset;
                }
                break;
            }
            case 0x71: { // ADC<izy>
                byte p = read(readIzyAddr());
                int result = (regAcc & 0xFF) + (p & 0xFF) + (flagC ? 1 : 0);
                flagC = result > 0xFF;

                // P[V] = ~(x^y) & (x^r) & 0x80;
                flagV = ((regAcc < 0) != ((byte) result < 0)) && ((regAcc < 0) == (p < 0));
                // TODO: write better

                regAcc = (byte) result;
                updateNZ(regAcc);
                break;
            }
            case 0x78: { // flag<I,1>
                flagI = false;
                break;
            }
            case (byte) 0x81: { // st<A,izx>
                write(readIzxAddr(), regAcc);
                break;
            }
            case (byte) 0x84: { // st<Y,zp>
                write(read(pc++) & 0xFF, regY);
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
            case (byte) 0x88: { // dec<Y>
                regY--;
                updateNZ(regY);
                break;
            }
            case (byte) 0x8A: { // tr<X,A>
                regAcc = regX;
                updateNZ(regAcc);
                break;
            }
            case (byte) 0x8C: { // st<Y,abs>
                int addr = readDouble(pc);
                pc += 2;
                write(addr, regY);
                break;
            }
            case (byte) 0x8D: { // st<A,abs>
                int addr = readDouble(pc);
                pc += 2;
                write(addr, regAcc);
                break;
            }
            case (byte) 0x8E: { // st<X,abs>
                int addr = readDouble(pc);
                pc += 2;
                write(addr, regX);
                break;
            }
            case (byte) 0x90: { // br<C,0>
                byte offset = read(pc++);
                if (!flagC) {
                    pc += offset;
                }
                break;
            }
            case (byte) 0x91: { // st<A,izy>
                write(readIzyAddr(), regAcc);
                break;
            }
            case (byte) 0x98: { // tr<Y,A>
                regAcc = regY;
                updateNZ(regAcc);
                break;
            }
            case (byte) 0x9A: { // tr<X,S>
                regSP = regX;
                updateNZ(regSP);
                break;
            }
            case (byte) 0xA0: { // ld<Y,imm>
                regY = read(pc++);
                updateNZ(regY);
                break;
            }
            case (byte) 0xA1: { // ld<A,izx>
                regAcc = read(readIzxAddr());
                updateNZ(regAcc);
                break;
            }
            case (byte) 0xA2: { // ld<X,imm>
                regX = read(pc++);
                updateNZ(regX);
                break;
            }
            case (byte) 0xA4: { // ld<Y,zp>
                regY = read(read(pc++) & 0xFF);
                updateNZ(regY);
                break;
            }
            case (byte) 0xA5: { // ld<A,zp>
                regAcc = read(read(pc++) & 0xFF);
                updateNZ(regAcc);
                break;
            }
            case (byte) 0xA6: { // ld<X,zp>
                regX = read(read(pc++) & 0xFF);
                updateNZ(regX);
                break;
            }
            case (byte) 0xA8: { // tr<A,Y>
                regY = regAcc;
                updateNZ(regY);
                break;
            }
            case (byte) 0xA9: { // ld<A,imm>
                regAcc = read(pc++);
                updateNZ(regAcc);
                break;
            }
            case (byte) 0xAA: { // tr<A,X>
                regX = regAcc;
                updateNZ(regX);
                break;
            }
            case (byte) 0xAC: { // ld<Y,abs>
                regY = read(readDouble(pc));
                pc += 2;
                updateNZ(regY);
                break;
            }
            case (byte) 0xAD: { // ld<A,abs>
                regAcc = read(readDouble(pc));
                pc += 2;
                updateNZ(regAcc);
                break;
            }
            case (byte) 0xAE: { // ld<X,abs>
                regX = read(readDouble(pc));
                pc += 2;
                updateNZ(regX);
                break;
            }
            case (byte) 0xB0: { // br<C,1>
                byte offset = read(pc++);
                if (flagC) {
                    pc += offset;
                }
                break;
            }
            case (byte) 0xB1: { // ld<A,izy>,
                regAcc = read(readIzyAddr());
                updateNZ(regAcc);
                break;
            }
            case (byte) 0xB8: { // flag<V,0>
                flagV = false;
                break;
            }
            case (byte) 0xBA: { // tr<S,X>
                regX = regSP;
                updateNZ(regX);
                break;
            }
            case (byte) 0xC0: { // cmp<Y,imm>
                byte m = read(pc++);
                updateNZ((byte) (regY - m));
                flagC = (regY & 0xFF) >= (m & 0xFF);
                break;
            }
            case (byte) 0xC1: { // cmp<A,izx>
                byte m = read(readIzxAddr());
                updateNZ((byte) (regY - m));
                flagC = (regY & 0xFF) >= (m & 0xFF);
                break;
            }
            case (byte) 0xC4: { // cmp<Y,zp>
                byte m = read(read(pc++) & 0xFF);
                updateNZ((byte) (regY - m));
                flagC = (regY & 0xFF) >= (m & 0xFF);
                break;
            }
            case (byte) 0xC5: { // cmp<A,zp>
                byte m = read(read(pc++) & 0xFF);
                updateNZ((byte) (regY - m));
                flagC = (regY & 0xFF) >= (m & 0xFF);
                break;
            }
            case (byte) 0xC6: { // DEC<zp>
                int addr = read(pc++) & 0xFF;
                byte p = read(addr);
                p--;
                write(addr, p);
                updateNZ(p);
                break;
            }
            case (byte) 0xC8: { // inc<Y>
                regY++;
                updateNZ(regY);
                break;
            }
            case (byte) 0xC9: { // cmp<A,imm>
                byte m = read(pc++);
                updateNZ((byte) (regAcc - m));
                flagC = (regAcc & 0xFF) >= (m & 0xFF);
                break;
            }
            case (byte) 0xCA: { // dec<X>
                regX--;
                updateNZ(regX);
                break;
            }
            case (byte) 0xCC: { // cmp<Y,abs>
                byte m = read(readDouble(pc));
                pc += 2;
                updateNZ((byte) (regY - m));
                flagC = (regY & 0xFF) >= (m & 0xFF);
                break;
            }
            case (byte) 0xCD: { // cmp<A,abs>
                byte m = read(readDouble(pc));
                pc += 2;
                updateNZ((byte) (regY - m));
                flagC = (regY & 0xFF) >= (m & 0xFF);
                break;
            }
            case (byte) 0xCE: { // DEC<abs>
                int addr = readDouble(pc);
                pc += 2;
                byte p = read(addr);
                p--;
                write(addr, p);
                updateNZ(p);
                break;
            }
            case (byte) 0xD0: { // br<Z,0>
                byte offset = read(pc++);
                if (!flagZ) {
                    pc += offset;
                }
                break;
            }
            case (byte) 0xD1: { // cmp<A,izy>
                byte m = read(readIzyAddr());
                updateNZ((byte) (regY - m));
                flagC = (regY & 0xFF) >= (m & 0xFF);
                break;
            }
            case (byte) 0xD8: { // flag<D,0>
                flagD = false;
                break;
            }
            case (byte) 0xE0: { // cmp<X,imm>
                byte m = read(pc++);
                updateNZ((byte) (regX - m));
                flagC = (regX & 0xFF) >= (m & 0xFF);
                break;
            }
            case (byte) 0xE1: { // SBC<izx>
                byte p = (byte) (read(readIzxAddr()) ^ 0xFF); // TODO: test?
                int result = (regAcc & 0xFF) + (p & 0xFF) + (flagC ? 1 : 0);
                flagC = result > 0xFF;

                // P[V] = ~(x^y) & (x^r) & 0x80;
                flagV = ((regAcc < 0) != ((byte) result < 0)) && ((regAcc < 0) == (p < 0));
                // TODO: write better

                regAcc = (byte) result;
                updateNZ(regAcc);
                break;
            }
            case (byte) 0xE4: { // cmp<X,zp>
                byte m = read(read(pc++) & 0xFF);
                updateNZ((byte) (regX - m));
                flagC = (regX & 0xFF) >= (m & 0xFF);
                break;
            }
            case (byte) 0xE5: { // SBC<zp>
                byte p = (byte) (read(read(pc++) & 0xFF) ^ 0xFF); // TODO: test?
                int result = (regAcc & 0xFF) + (p & 0xFF) + (flagC ? 1 : 0);
                flagC = result > 0xFF;

                // P[V] = ~(x^y) & (x^r) & 0x80;
                flagV = ((regAcc < 0) != ((byte) result < 0)) && ((regAcc < 0) == (p < 0));
                // TODO: write better

                regAcc = (byte) result;
                updateNZ(regAcc);
                break;
            }
            case (byte) 0xE6: { // INC<zp>
                int addr = read(pc++) & 0xFF;
                byte p = read(addr);
                p++;
                write(addr, p);
                updateNZ(p);
                break;
            }
            case (byte) 0xE8: { // inc<X>
                regX++;
                updateNZ(regX);
                break;
            }
            case (byte) 0xE9: { // SBC<imm>
                byte p = (byte) (read(pc++) ^ 0xFF); // TODO: test?
                int result = (regAcc & 0xFF) + (p & 0xFF) + (flagC ? 1 : 0);
                flagC = result > 0xFF;

                // P[V] = ~(x^y) & (x^r) & 0x80;
                flagV = ((regAcc < 0) != ((byte) result < 0)) && ((regAcc < 0) == (p < 0));
                // TODO: write better

                regAcc = (byte) result;
                updateNZ(regAcc);
                break;
            }
            case (byte) 0xEA: { // NOP
                break;
            }
            case (byte) 0xEC: { // cmp<X,abs>
                byte m = read(readDouble(pc));
                pc += 2;
                updateNZ((byte) (regX - m));
                flagC = (regX & 0xFF) >= (m & 0xFF);
                break;
            }
            case (byte) 0xED: { // SBC<abs>
                byte p = (byte) (read(readDouble(pc)) ^ 0xFF); // TODO: test?
                pc += 2;
                int result = (regAcc & 0xFF) + (p & 0xFF) + (flagC ? 1 : 0);
                flagC = result > 0xFF;

                // P[V] = ~(x^y) & (x^r) & 0x80;
                flagV = ((regAcc < 0) != ((byte) result < 0)) && ((regAcc < 0) == (p < 0));
                // TODO: write better

                regAcc = (byte) result;
                updateNZ(regAcc);
                break;
            }
            case (byte) 0xEE: { // INC<abs>
                int addr = readDouble(pc);
                pc += 2;
                byte p = read(addr);
                p++;
                write(addr, p);
                updateNZ(p);
                break;
            }
            case (byte) 0xF0: { // br<Z,1>
                byte offset = read(pc++);
                if (flagZ) {
                    pc += offset;
                }
                break;
            }
            case (byte) 0xF1: { // SBC<izy>
                byte p = (byte) (read(readIzyAddr()) ^ 0xFF); // TODO: test?
                int result = (regAcc & 0xFF) + (p & 0xFF) + (flagC ? 1 : 0);
                flagC = result > 0xFF;

                // P[V] = ~(x^y) & (x^r) & 0x80;
                flagV = ((regAcc < 0) != ((byte) result < 0)) && ((regAcc < 0) == (p < 0));
                // TODO: write better

                regAcc = (byte) result;
                updateNZ(regAcc);
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
