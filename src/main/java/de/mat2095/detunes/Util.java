package de.mat2095.detunes;

public class Util {

    private Util() {
    }

    public static String getBinString(byte b) {
        return String.format("0b%8s", Integer.toBinaryString(b & 0xFF).toUpperCase()).replace(' ', '0');
    }

    public static String getHexString(byte b) {
        return String.format("0x%2s", Integer.toHexString(b & 0xFF).toUpperCase()).replace(' ', '0');
    }

    public static String getHexString16bit(int i) {
        return String.format("0x%4s", Integer.toHexString(i).toUpperCase()).replace(' ', '0');
    }

    public static int shiftRightNegArg(int i, int s) {
        if (s > 0) {
            return i >>> s;
        } else if (s < 0) {
            return i << -s;
        } else {
            return i;
        }
    }
}
