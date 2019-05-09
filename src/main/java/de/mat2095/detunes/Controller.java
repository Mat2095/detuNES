package de.mat2095.detunes;


class Controller {

    private final InputProvider[] ips;
    private boolean strobe;
    private final int[] states;

    Controller(InputProvider ip1, InputProvider ip2) {
        ips = new InputProvider[]{ip1, ip2};
        states = new int[2];
    }

    void power() {
        strobe = false;
        for (int player = 0; player < 2; player++) {
            states[player] = 0;
        }
    }

    byte read(int player) {
        boolean result;
        if (strobe) {
            result = ips[player].isButtonPressed(InputProvider.Button.BUTTON_A);
        } else {
            result = (states[player] & 0x01) != 0;
            states[player] >>>= 1;
        }
        byte lastReadValue = 0x40; // TODO: actually check value, but 0x40 should be correct in most cases
        return (byte) (lastReadValue | (result ? 0x01 : 0x00));
    }

    void updateStrobe(boolean newStrobe) {
        strobe = newStrobe;
        if (!strobe) { // Actually, states are continuously update during strobe, but the result is only observable AFTER strobing
            for (int player = 0; player < 2; player++) {
                states[player] = 0;
                for (InputProvider.Button button : InputProvider.Button.values()) {
                    states[player] |= ips[player].isButtonPressed(button) ? button.code : 0;
                }
            }
        }
    }
}
