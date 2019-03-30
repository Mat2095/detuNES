package de.mat2095.detunes;

class Cpu {

    private static final String[] OP_NAMES = new String[256];

    static {
        OP_NAMES[0x00] = "INT<BRK>";
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
        OP_NAMES[0x15] = "ORA<zpx>";
        OP_NAMES[0x16] = "ASL<zpx>";
        OP_NAMES[0x18] = "flag<C,0>";
        OP_NAMES[0x19] = "ORA<aby>";
        OP_NAMES[0x1D] = "ORA<abx>";
        OP_NAMES[0x1E] = "ASL<abx>";
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
        OP_NAMES[0x35] = "AND<zpx>";
        OP_NAMES[0x36] = "ROL<zpx>";
        OP_NAMES[0x38] = "flag<C,1>";
        OP_NAMES[0x39] = "AND<aby>";
        OP_NAMES[0x3D] = "AND<abx>";
        OP_NAMES[0x3E] = "ROL<abx>";
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
        OP_NAMES[0x55] = "EOR<zpx>";
        OP_NAMES[0x56] = "LSR<zpx>";
        OP_NAMES[0x58] = "flag<I,0>";
        OP_NAMES[0x59] = "EOR<aby>";
        OP_NAMES[0x5D] = "EOR<abx>";
        OP_NAMES[0x5E] = "LSR<abx>";
        OP_NAMES[0x60] = "RTS";
        OP_NAMES[0x61] = "ADC<izx>";
        OP_NAMES[0x65] = "ADC<zp>";
        OP_NAMES[0x66] = "ROR<zp>";
        OP_NAMES[0x68] = "PLA";
        OP_NAMES[0x69] = "ADC<imm>";
        OP_NAMES[0x6A] = "ROR_A";
        OP_NAMES[0x6C] = "JMP_IND";
        OP_NAMES[0x6D] = "ADC<abs>";
        OP_NAMES[0x6E] = "ROR<abs>";
        OP_NAMES[0x70] = "br<V,1>";
        OP_NAMES[0x71] = "ADC<izy>";
        OP_NAMES[0x75] = "ADC<zpx>";
        OP_NAMES[0x76] = "ROR<zpx>";
        OP_NAMES[0x78] = "flag<I,1>";
        OP_NAMES[0x79] = "ADC<aby>";
        OP_NAMES[0x7D] = "ADC<abx>";
        OP_NAMES[0x7E] = "ROR<abx>";
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
        OP_NAMES[0x94] = "st<Y,zpx>";
        OP_NAMES[0x95] = "st<A,zpx>";
        OP_NAMES[0x96] = "st<X,zpy>";
        OP_NAMES[0x98] = "tr<Y,A>";
        OP_NAMES[0x99] = "st<A,aby>";
        OP_NAMES[0x9A] = "tr<X,S>";
        OP_NAMES[0x9D] = "st<A,abx>";
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
        OP_NAMES[0xB4] = "ld<Y,zpx>";
        OP_NAMES[0xB5] = "ld<A,zpx>";
        OP_NAMES[0xB6] = "ld<X,zpy>";
        OP_NAMES[0xB8] = "flag<V,0>";
        OP_NAMES[0xB9] = "ld<A,aby>";
        OP_NAMES[0xBA] = "tr<S,X>";
        OP_NAMES[0xBC] = "ld<Y,abx>";
        OP_NAMES[0xBD] = "ld<A,abx>";
        OP_NAMES[0xBE] = "ld<X,aby>";
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
        OP_NAMES[0xD5] = "cmp<A,zpx>";
        OP_NAMES[0xD6] = "DEC<zpx>";
        OP_NAMES[0xD8] = "flag<D,0>";
        OP_NAMES[0xD9] = "cmp<A,aby>";
        OP_NAMES[0xDD] = "cmp<A,abx>";
        OP_NAMES[0xDE] = "DEC<abx>";
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
        OP_NAMES[0xF5] = "SBC<zpx>";
        OP_NAMES[0xF6] = "INC<zpx>";
        OP_NAMES[0xF8] = "flag<D,1>";
        OP_NAMES[0xF9] = "SBC<aby>";
        OP_NAMES[0xFD] = "SBC<abx>";
        OP_NAMES[0xFE] = "INC<abx>";
    }

    private String lastMemText;

    private Emulator emu;
    private final byte[] ram;

    // registers
    private byte regAcc, regX, regY, regSP;
    int pc;
    private boolean flagC, flagZ, flagI, flagD, flagV, flagN; // Carry, Zero, Interrupt, Decimal, oVerflow, Negative

    Cpu() {
        this.ram = new byte[0x0800]; // 2KiB
    }

    void power(Emulator emu) {
        this.emu = emu;
        setP((byte) 0b00110100);
        regAcc = regX = regY = 0;
        regSP = (byte) 0xFD;

        pc = readDouble(0xFFFC);
    }

    byte read(int addr) {
        if (addr < 0 || addr > 0x1FFF) {
            throw new IllegalArgumentException("CPU addr out of range: " + Util.getHexString16bit(addr));
        }

        return ram[addr % 0x0800];
    }

    void write(int addr, byte value) {
        if (addr < 0 || addr > 0x1FFF) {
            throw new IllegalArgumentException("CPU addr out of range: " + Util.getHexString16bit(addr));
        }

        ram[addr % 0x0800] = value;
    }

    private byte getP() {
        byte p = 0b00100000;

        p |= flagC ? 0b00000001 : 0;
        p |= flagZ ? 0b00000010 : 0;
        p |= flagI ? 0b00000100 : 0;
        p |= flagD ? 0b00001000 : 0;
        p |= flagV ? 0b01000000 : 0;
        p |= flagN ? 0b10000000 : 0;

        return p;
    }

    private void setP(byte p) {
        flagC = (p & 0b00000001) != 0;
        flagZ = (p & 0b00000010) != 0;
        flagI = (p & 0b00000100) != 0;
        flagD = (p & 0b00001000) != 0;
        flagV = (p & 0b01000000) != 0;
        flagN = (p & 0b10000000) != 0;
    }

    private void updateNZ(byte b) {
        flagN = b < 0;
        flagZ = b == 0;
    }

    private void pushStack(byte b) {
        emu.write(0x100 | (regSP & 0xFF), b);
        regSP--;
    }

    private byte popStack() {
        regSP++;
        return emu.read(0x100 | (regSP & 0xFF));
    }

    private int readDouble(int addr) {
        return (emu.read(addr) & 0xFF) | ((emu.read(addr + 1) & 0xFF) << 8);
    }

    // <address-readers>

    private int readImmAddr() {
        return pc++;
    }

    private int readZpAddr() {
        return emu.read(readImmAddr()) & 0xFF;
    }

    private int readZpxAddr() {
        return (readZpAddr() + (regX & 0xFF)) & 0xFF;
    }

    private int readZpyAddr() {
        return (readZpAddr() + (regY & 0xFF)) & 0xFF;
    }

    private int readAbsAddr() {
        int ai1 = readZpAddr();
        int ai2 = readZpAddr();
        return ai1 | (ai2 << 8);
    }

    private int readAbxAddr() {
        return (readAbsAddr() + (regX & 0xFF)) & 0xFFFF;
    }

    private int readAbyAddr() {
        return (readAbsAddr() + (regY & 0xFF)) & 0xFFFF;
    }

    private int readIzxAddr() {
        int ai1 = (readZpAddr() + regX) & 0xFF;
        int ai2 = (ai1 + 1) & 0xFF;
        return (emu.read(ai1) & 0xFF) | ((emu.read(ai2) & 0xFF) << 8);
    }

    private int readIzyAddr() {
        int ai1 = readZpAddr();
        int ai2 = (ai1 + 1) & 0xFF;
        return (((emu.read(ai1) & 0xFF) | ((emu.read(ai2) & 0xFF) << 8)) + (regY & 0xFF)) & 0xFFFF;
    }

    // </address-readers>

    // <operations>

    private void execAnd(int addr) {
        regAcc &= emu.read(addr);
        updateNZ(regAcc);
    }

    private void execOra(int addr) {
        regAcc |= emu.read(addr);
        updateNZ(regAcc);
    }

    private void execEor(int addr) {
        regAcc ^= emu.read(addr);
        updateNZ(regAcc);
    }

    private void execAsl(int addr) {
        byte p = emu.read(addr);
        flagC = (p & 0x80) == 0x80;
        p = (byte) ((p & 0xFF) << 1);
        emu.write(addr, p);
        updateNZ(p);
    }

    private void execLsr(int addr) {
        byte p = emu.read(addr);
        flagC = (p & 0x01) == 0x01;
        p = (byte) ((p & 0xFF) >>> 1);
        emu.write(addr, p);
        updateNZ(p);
    }

    private void execRol(int addr) {
        byte p = emu.read(addr);
        byte newP = (byte) ((flagC ? 0x01 : 0x00) | ((p & 0xFF) << 1));
        flagC = (p & 0x80) == 0x80;
        emu.write(addr, newP);
        updateNZ(newP);
    }

    private void execRor(int addr) {
        byte p = emu.read(addr);
        byte newP = (byte) ((flagC ? 0x80 : 0x00) | ((p & 0xFF) >>> 1));
        flagC = (p & 0x01) == 0x01;
        emu.write(addr, newP);
        updateNZ(newP);
    }

    private void execBit(int addr) {
        byte p = emu.read(addr);
        flagZ = (regAcc & p) == 0;
        flagN = (p & 0x80) != 0;
        flagV = (p & 0x40) != 0;
    }

    private void execCmp(byte val, int addr) {
        byte m = emu.read(addr);
        updateNZ((byte) (val - m));
        flagC = (val & 0xFF) >= (m & 0xFF);
    }

    private void execBr(boolean cond) {
        byte offset = emu.read(readImmAddr());
        if (cond) {
            pc += offset;
        }
    }

    private void execInc(int addr) {
        byte p = emu.read(addr);
        p++;
        emu.write(addr, p);
        updateNZ(p);
    }

    private void execDec(int addr) {
        byte p = emu.read(addr);
        p--;
        emu.write(addr, p);
        updateNZ(p);
    }

    private void addToAcc(byte p) {
        int result = (regAcc & 0xFF) + (p & 0xFF) + (flagC ? 1 : 0);
        flagC = result > 0xFF;
        flagV = ((regAcc < 0) != ((byte) result < 0)) && ((regAcc < 0) == (p < 0));
        regAcc = (byte) result;
        updateNZ(regAcc);
    }

    private void execAdc(int addr) {
        addToAcc(emu.read(addr));
    }

    private void execSbc(int addr) {
        addToAcc((byte) (emu.read(addr) ^ 0xFF));
    }

    // </operations>

    void printDebug(RunConfiguration runConfig) {
        boolean printGeneralInfoLine = runConfig.debugPrintGeneralInfo || runConfig.debugPrintMem != null;
        if (printGeneralInfoLine) {
            StringBuilder outputLine = new StringBuilder("exec");
            if (runConfig.debugPrintMem != null) {
                outputLine.append("\u00a0 ");
                for (int memAddr : runConfig.debugPrintMem) {
                    outputLine.append(" ").append(Util.getHexString(emu.read(memAddr)));
                }
            }
            if (runConfig.debugPrintGeneralInfo) {
                byte op = emu.read(pc);
                outputLine
                    .append("\u00a0  SP: ").append(Util.getHexString(regSP))
                    .append("\u00a0 X: ").append(Util.getHexString(regX))
                    .append("\u00a0 Y: ").append(Util.getHexString(regY))
                    .append("\u00a0 Flags: ").append(Util.getBinString(getP()))
                    .append("\u00a0 pc: ").append(Util.getHexString16bit(pc))
                    .append("\u00a0 Acc: ").append(Util.getHexString(regAcc))
                    .append("\u00a0  op: ").append(Util.getHexString(op))
                    .append("\u00a0 ").append(OP_NAMES[op & 0xFF] != null ? OP_NAMES[op & 0xFF] : "");
            }
            System.out.println(outputLine);
        }

        if (runConfig.debugPrintMemText != null) {
            String memText = emu.readText(runConfig.debugPrintMemText);
            if (printGeneralInfoLine) {
                System.out.println("       " + memText);
            } else {
                if (!memText.equals(lastMemText)) {
                    System.out.println(memText);
                    lastMemText = memText;
                }
            }
        }
    }

    void exec() {

        if (pc < 0 || pc > 0xFFFF) {
            throw new IllegalStateException("pc is in illegal state: 0x" + Util.getHexString16bit(pc));
        }

        byte op = emu.read(pc++);

        switch (op) {
            case 0x00: { // INT<BRK>
                pc++; // padding-byte
                pushStack((byte) (pc >>> 8));
                pushStack((byte) (pc & 0xFF));
                pushStack((byte) (getP() | 0b00010000));
                flagI = true;
                pc = readDouble(0xFFFE);
                break;
            }
            case 0x01: { // ORA<izx>
                execOra(readIzxAddr());
                break;
            }
            case 0x05: { // ORA<zp>
                execOra(readZpAddr());
                break;
            }
            case 0x06: { // ASL<zp>
                execAsl(readZpAddr());
                break;
            }
            case 0x08: { // PHP
                pushStack((byte) (getP() | 0b00010000));
                break;
            }
            case 0x09: { // ORA<imm>
                execOra(readImmAddr());
                break;
            }
            case 0x0A: { // ASL_A
                flagC = (regAcc & 0x80) == 0x80;
                regAcc = (byte) ((regAcc & 0xFF) << 1);
                updateNZ(regAcc);
                break;
            }
            case 0x0D: { // ORA<abs>
                execOra(readAbsAddr());
                break;
            }
            case 0x0E: { // ASL<abs>
                execAsl(readAbsAddr());
                break;
            }
            case 0x10: { // br<N,0>
                execBr(!flagN);
                break;
            }
            case 0x11: { // ORA<izy>
                execOra(readIzyAddr());
                break;
            }
            case 0x15: { // ORA<zpx>
                execOra(readZpxAddr());
                break;
            }
            case 0x16: { // ASL<zpx>
                execAsl(readZpxAddr());
                break;
            }
            case 0x18: { // flag<C,0>
                flagC = false;
                break;
            }
            case 0x19: { // ORA<aby>
                execOra(readAbyAddr());
                break;
            }
            case 0x1D: { // ORA<abx>
                execOra(readAbxAddr());
                break;
            }
            case 0x1E: { // ASL<abx>
                execAsl(readAbxAddr());
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
                execAnd(readIzxAddr());
                break;
            }
            case 0x24: { // BIT<zp>
                execBit(readZpAddr());
                break;
            }
            case 0x25: { // AND<zp>
                execAnd(readZpAddr());
                break;
            }
            case 0x26: { // ROL<zp>
                execRol(readZpAddr());
                break;
            }
            case 0x28: { // PLP
                setP(popStack());
                break;
            }
            case 0x29: { // AND<imm>
                execAnd(readImmAddr());
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
                execBit(readAbsAddr());
                break;
            }
            case 0x2D: { // AND<abs>
                execAnd(readAbsAddr());
                break;
            }
            case 0x2E: { // ROL<abs>
                execRol(readAbsAddr());
                break;
            }
            case 0x30: { // br<N,1>
                execBr(flagN);
                break;
            }
            case 0x31: { // AND<izy>
                execAnd(readIzyAddr());
                break;
            }
            case 0x35: { // AND<zpx>
                execAnd(readZpxAddr());
                break;
            }
            case 0x36: { // ROL<zpx>
                execRol(readZpxAddr());
                break;
            }
            case 0x38: { // flag<C,1>
                flagC = true;
                break;
            }
            case 0x39: { // AND<aby>
                execAnd(readAbyAddr());
                break;
            }
            case 0x3D: { // AND<abx>
                execAnd(readAbxAddr());
                break;
            }
            case 0x3E: { // ROL<abx>
                execRol(readAbxAddr());
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
                execEor(readIzxAddr());
                break;
            }
            case 0x45: { // EOR<zp>
                execEor(readZpAddr());
                break;
            }
            case 0x46: { // LSR<zp>
                execLsr(readZpAddr());
                break;
            }
            case 0x48: { // PHA
                pushStack(regAcc);
                break;
            }
            case 0x49: { // EOR<imm>
                execEor(readImmAddr());
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
                execEor(readAbsAddr());
                break;
            }
            case 0x4E: { // LSR<abs>
                execLsr(readAbsAddr());
                break;
            }
            case 0x50: { // br<V,0>
                execBr(!flagV);
                break;
            }
            case 0x51: { // EOR<izy>
                execEor(readIzyAddr());
                break;
            }
            case 0x55: { // EOR<zpx>
                execEor(readZpxAddr());
                break;
            }
            case 0x56: { // LSR<zpx>
                execLsr(readZpxAddr());
                break;
            }
            case 0x58: { // flag<I,0>
                flagI = false;
                break;
            }
            case 0x59: { // EOR<aby>
                execEor(readAbyAddr());
                break;
            }
            case 0x5D: { // EOR<abx>
                execEor(readAbxAddr());
                break;
            }
            case 0x5E: { // LSR<abx>
                execLsr(readAbxAddr());
                break;
            }
            case 0x60: { // RTS
                byte t1 = popStack();
                byte t2 = popStack();
                pc = ((t2 & 0xFF) << 8 | (t1 & 0xFF)) + 1;
                break;
            }
            case 0x61: { // ADC<izx>
                execAdc(readIzxAddr());
                break;
            }
            case 0x65: { // ADC<zp>
                execAdc(readZpAddr());
                break;
            }
            case 0x66: { // ROR<zp>
                execRor(readZpAddr());
                break;
            }
            case 0x68: { // PLA
                regAcc = popStack();
                updateNZ(regAcc);
                break;
            }
            case 0x69: { // ADC<imm>
                execAdc(readImmAddr());
                break;
            }
            case 0x6A: { // ROR_A
                byte newAcc = (byte) ((flagC ? 0x80 : 0x00) | ((regAcc & 0xFF) >>> 1));
                flagC = (regAcc & 0x01) == 0x01;
                regAcc = newAcc;
                updateNZ(regAcc);
                break;
            }
            case 0x6C: { // JMP_IND
                int ai1 = readDouble(pc);
                int ai2 = (ai1 & 0xFF00) | ((ai1 + 1) & 0xFF);
                pc = (emu.read(ai1) & 0xFF) | ((emu.read(ai2) & 0xFF) << 8);
                break;
            }
            case 0x6D: { // ADC<abs>
                execAdc(readAbsAddr());
                break;
            }
            case 0x6E: { // ROR<abs>
                execRor(readAbsAddr());
                break;
            }
            case 0x70: { // br<V,1>
                execBr(flagV);
                break;
            }
            case 0x71: { // ADC<izy>
                execAdc(readIzyAddr());
                break;
            }
            case 0x75: { // ADC<zpx>
                execAdc(readZpxAddr());
                break;
            }
            case 0x76: { // ROR<zpx>
                execRor(readZpxAddr());
                break;
            }
            case 0x78: { // flag<I,1>
                flagI = true;
                break;
            }
            case 0x79: { // ADC<aby>
                execAdc(readAbyAddr());
                break;
            }
            case 0x7D: { // ADC<abx>
                execAdc(readAbxAddr());
                break;
            }
            case 0x7E: { // ROR<abx>
                execRor(readAbxAddr());
                break;
            }
            case (byte) 0x81: { // st<A,izx>
                emu.write(readIzxAddr(), regAcc);
                break;
            }
            case (byte) 0x84: { // st<Y,zp>
                emu.write(readZpAddr(), regY);
                break;
            }
            case (byte) 0x85: { // st<A,zp>
                emu.write(readZpAddr(), regAcc);
                break;
            }
            case (byte) 0x86: { // st<X,zp>
                emu.write(readZpAddr(), regX);
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
                emu.write(readAbsAddr(), regY);
                break;
            }
            case (byte) 0x8D: { // st<A,abs>
                emu.write(readAbsAddr(), regAcc);
                break;
            }
            case (byte) 0x8E: { // st<X,abs>
                emu.write(readAbsAddr(), regX);
                break;
            }
            case (byte) 0x90: { // br<C,0>
                execBr(!flagC);
                break;
            }
            case (byte) 0x91: { // st<A,izy>
                emu.write(readIzyAddr(), regAcc);
                break;
            }
            case (byte) 0x94: { // st<Y,zpx>
                emu.write(readZpxAddr(), regY);
                break;
            }
            case (byte) 0x95: { // st<A,zpx>
                emu.write(readZpxAddr(), regAcc);
                break;
            }
            case (byte) 0x96: { // st<X,zpy>
                emu.write(readZpyAddr(), regX);
                break;
            }
            case (byte) 0x98: { // tr<Y,A>
                regAcc = regY;
                updateNZ(regAcc);
                break;
            }
            case (byte) 0x99: { // st<A,aby>
                emu.write(readAbyAddr(), regAcc);
                break;
            }
            case (byte) 0x9A: { // tr<X,S>
                regSP = regX; // N and Z flags are not updated in this special case!
                break;
            }
            case (byte) 0x9D: { // st<A,abx>
                emu.write(readAbxAddr(), regAcc);
                break;
            }
            case (byte) 0xA0: { // ld<Y,imm>
                regY = emu.read(readImmAddr());
                updateNZ(regY);
                break;
            }
            case (byte) 0xA1: { // ld<A,izx>
                regAcc = emu.read(readIzxAddr());
                updateNZ(regAcc);
                break;
            }
            case (byte) 0xA2: { // ld<X,imm>
                regX = emu.read(readImmAddr());
                updateNZ(regX);
                break;
            }
            case (byte) 0xA4: { // ld<Y,zp>
                regY = emu.read(readZpAddr());
                updateNZ(regY);
                break;
            }
            case (byte) 0xA5: { // ld<A,zp>
                regAcc = emu.read(readZpAddr());
                updateNZ(regAcc);
                break;
            }
            case (byte) 0xA6: { // ld<X,zp>
                regX = emu.read(readZpAddr());
                updateNZ(regX);
                break;
            }
            case (byte) 0xA8: { // tr<A,Y>
                regY = regAcc;
                updateNZ(regY);
                break;
            }
            case (byte) 0xA9: { // ld<A,imm>
                regAcc = emu.read(readImmAddr());
                updateNZ(regAcc);
                break;
            }
            case (byte) 0xAA: { // tr<A,X>
                regX = regAcc;
                updateNZ(regX);
                break;
            }
            case (byte) 0xAC: { // ld<Y,abs>
                regY = emu.read(readAbsAddr());
                updateNZ(regY);
                break;
            }
            case (byte) 0xAD: { // ld<A,abs>
                regAcc = emu.read(readAbsAddr());
                updateNZ(regAcc);
                break;
            }
            case (byte) 0xAE: { // ld<X,abs>
                regX = emu.read(readAbsAddr());
                updateNZ(regX);
                break;
            }
            case (byte) 0xB0: { // br<C,1>
                execBr(flagC);
                break;
            }
            case (byte) 0xB1: { // ld<A,izy>
                regAcc = emu.read(readIzyAddr());
                updateNZ(regAcc);
                break;
            }
            case (byte) 0xB4: { // ld<Y,zpx>
                regY = emu.read(readZpxAddr());
                updateNZ(regY);
                break;
            }
            case (byte) 0xB5: { // ld<A,zpx>
                regAcc = emu.read(readZpxAddr());
                updateNZ(regAcc);
                break;
            }
            case (byte) 0xB6: { // ld<X,zpy>
                regX = emu.read(readZpyAddr());
                updateNZ(regX);
                break;
            }
            case (byte) 0xB8: { // flag<V,0>
                flagV = false;
                break;
            }
            case (byte) 0xB9: { // ld<A,aby>
                regAcc = emu.read(readAbyAddr());
                updateNZ(regAcc);
                break;
            }
            case (byte) 0xBA: { // tr<S,X>
                regX = regSP;
                updateNZ(regX);
                break;
            }
            case (byte) 0xBC: { // ld<Y,abx>
                regY = emu.read(readAbxAddr());
                updateNZ(regY);
                break;
            }
            case (byte) 0xBD: { // ld<A,abx>
                regAcc = emu.read(readAbxAddr());
                updateNZ(regAcc);
                break;
            }
            case (byte) 0xBE: { // ld<X,aby>
                regX = emu.read(readAbyAddr());
                updateNZ(regX);
                break;
            }
            case (byte) 0xC0: { // cmp<Y,imm>
                execCmp(regY, readImmAddr());
                break;
            }
            case (byte) 0xC1: { // cmp<A,izx>
                execCmp(regAcc, readIzxAddr());
                break;
            }
            case (byte) 0xC4: { // cmp<Y,zp>
                execCmp(regY, readZpAddr());
                break;
            }
            case (byte) 0xC5: { // cmp<A,zp>
                execCmp(regAcc, readZpAddr());
                break;
            }
            case (byte) 0xC6: { // DEC<zp>
                execDec(readZpAddr());
                break;
            }
            case (byte) 0xC8: { // inc<Y>
                regY++;
                updateNZ(regY);
                break;
            }
            case (byte) 0xC9: { // cmp<A,imm>
                execCmp(regAcc, readImmAddr());
                break;
            }
            case (byte) 0xCA: { // dec<X>
                regX--;
                updateNZ(regX);
                break;
            }
            case (byte) 0xCC: { // cmp<Y,abs>
                execCmp(regY, readAbsAddr());
                break;
            }
            case (byte) 0xCD: { // cmp<A,abs>
                execCmp(regAcc, readAbsAddr());
                break;
            }
            case (byte) 0xCE: { // DEC<abs>
                execDec(readAbsAddr());
                break;
            }
            case (byte) 0xD0: { // br<Z,0>
                execBr(!flagZ);
                break;
            }
            case (byte) 0xD1: { // cmp<A,izy>
                execCmp(regAcc, readIzyAddr());
                break;
            }
            case (byte) 0xD5: { // cmp<A,zpx>
                execCmp(regAcc, readZpxAddr());
                break;
            }
            case (byte) 0xD6: { // DEC<zpx>
                execDec(readZpxAddr());
                break;
            }
            case (byte) 0xD8: { // flag<D,0>
                flagD = false;
                break;
            }
            case (byte) 0xD9: { // cmp<A,aby>
                execCmp(regAcc, readAbyAddr());
                break;
            }
            case (byte) 0xDD: { // cmp<A,abx>
                execCmp(regAcc, readAbxAddr());
                break;
            }
            case (byte) 0xDE: { // DEC<abx>
                execDec(readAbxAddr());
                break;
            }
            case (byte) 0xE0: { // cmp<X,imm>
                execCmp(regX, readImmAddr());
                break;
            }
            case (byte) 0xE1: { // SBC<izx>
                execSbc(readIzxAddr());
                break;
            }
            case (byte) 0xE4: { // cmp<X,zp>
                execCmp(regX, readZpAddr());
                break;
            }
            case (byte) 0xE5: { // SBC<zp>
                execSbc(readZpAddr());
                break;
            }
            case (byte) 0xE6: { // INC<zp>
                execInc(readZpAddr());
                break;
            }
            case (byte) 0xE8: { // inc<X>
                regX++;
                updateNZ(regX);
                break;
            }
            case (byte) 0xE9: { // SBC<imm>
                execSbc(readImmAddr());
                break;
            }
            case (byte) 0xEA: { // NOP
                break;
            }
            case (byte) 0xEC: { // cmp<X,abs>
                execCmp(regX, readAbsAddr());
                break;
            }
            case (byte) 0xED: { // SBC<abs>
                execSbc(readAbsAddr());
                break;
            }
            case (byte) 0xEE: { // INC<abs>
                execInc(readAbsAddr());
                break;
            }
            case (byte) 0xF0: { // br<Z,1>
                execBr(flagZ);
                break;
            }
            case (byte) 0xF1: { // SBC<izy>
                execSbc(readIzyAddr());
                break;
            }
            case (byte) 0xF5: { // SBC<zpx>
                execSbc(readZpxAddr());
                break;
            }
            case (byte) 0xF6: { // INC<zpx>
                execInc(readZpxAddr());
                break;
            }
            case (byte) 0xF8: { // flag<D,1>
                flagD = true;
                break;
            }
            case (byte) 0xF9: { // SBC<aby>
                execSbc(readAbyAddr());
                break;
            }
            case (byte) 0xFD: { // SBC<abx>
                execSbc(readAbxAddr());
                break;
            }
            case (byte) 0xFE: { // INC<abx>
                execInc(readAbxAddr());
                break;
            }
            default:
                throw new IllegalStateException("Invalid OP-code: " + Util.getHexString(op));
        }

    }
}
