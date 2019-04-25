package de.mat2095.detunes;

import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;


class Controller {

    private boolean strobe;
    private final XInputDevice[] controllers;
    private final int[] states;

    Controller() {
        controllers = new XInputDevice[2];
        states = new int[2];
    }

    void power() {
        strobe = false;
        for (int player = 0; player < 2; player++) {
            try {
                controllers[player] = XInputDevice.getDeviceFor(player);
            } catch (XInputNotLoadedException | UnsatisfiedLinkError | NoClassDefFoundError e) {
                e.printStackTrace();
            }
            states[player] = 0;
        }
    }

    byte read(int player) {
        boolean result;
        if (strobe) {
            if (controllers[player] != null) {
                controllers[player].poll();
                result = controllers[player].getComponents().getButtons().b;
            } else {
                result = false;
            }
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
                if (controllers[player] == null) {
                    continue;
                }
                controllers[player].poll();
                XInputComponents controlerState = controllers[player].getComponents();
                states[player] = 0;
                states[player] |= controlerState.getButtons().b ? 0b00000001 : 0;
                states[player] |= controlerState.getButtons().a ? 0b00000010 : 0;
                states[player] |= controlerState.getButtons().back ? 0b00000100 : 0;
                states[player] |= controlerState.getButtons().start ? 0b00001000 : 0;
                // TODO: also use analog-stick instead of d-pad
                states[player] |= controlerState.getButtons().up ? 0b00010000 : 0;
                states[player] |= controlerState.getButtons().down ? 0b00100000 : 0;
                states[player] |= controlerState.getButtons().left ? 0b01000000 : 0;
                states[player] |= controlerState.getButtons().right ? 0b10000000 : 0;
            }
        }
    }
}
