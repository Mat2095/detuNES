package de.mat2095.detunes;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

class InputProviderKeyboard implements InputProvider {

    private final Set<Integer> pressedKeys;

    InputProviderKeyboard() {
        pressedKeys = new HashSet<>();
    }

    void keyPressed(int keyCode) {
        pressedKeys.add(keyCode);
    }

    void keyReleased(int keyCode) {
        pressedKeys.remove(keyCode);
    }

    void focusLost() {
        pressedKeys.clear();
    }

    @Override
    public boolean isButtonPressed(int player, Button button) {

        switch (button) {
            case BUTTON_A:
                return pressedKeys.contains(KeyEvent.VK_K);
            case BUTTON_B:
                return pressedKeys.contains(KeyEvent.VK_J);
            case BUTTON_SELECT:
                return pressedKeys.contains(KeyEvent.VK_BACK_SPACE);
            case BUTTON_START:
                return pressedKeys.contains(KeyEvent.VK_ENTER);
            case BUTTON_UP:
                return pressedKeys.contains(KeyEvent.VK_UP) || pressedKeys.contains(KeyEvent.VK_W);
            case BUTTON_DOWN:
                return pressedKeys.contains(KeyEvent.VK_DOWN) || pressedKeys.contains(KeyEvent.VK_S);
            case BUTTON_LEFT:
                return pressedKeys.contains(KeyEvent.VK_LEFT) || pressedKeys.contains(KeyEvent.VK_A);
            case BUTTON_RIGHT:
                return pressedKeys.contains(KeyEvent.VK_RIGHT) || pressedKeys.contains(KeyEvent.VK_D);
            default:
                throw new Error("Invalid Button");
        }
    }
}
