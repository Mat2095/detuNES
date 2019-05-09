package de.mat2095.detunes;

public interface InputProvider {

    boolean isButtonPressed(Button button);

    enum Button {

        BUTTON_A(0b00000001),
        BUTTON_B(0b00000010),
        BUTTON_SELECT(0b00000100),
        BUTTON_START(0b00001000),
        BUTTON_UP(0b00010000),
        BUTTON_DOWN(0b00100000),
        BUTTON_LEFT(0b01000000),
        BUTTON_RIGHT(0b10000000);

        int code;

        Button(int code) {
            this.code = code;
        }
    }
}
