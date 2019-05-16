package de.mat2095.detunes;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class InputProviderImpl implements InputProvider {

    private static final float ANALOG_DEADZONE = 0.5f;

    private final Set<Integer> pressedKeys;
    private final ControllerManager cm;

    InputProviderImpl() {
        pressedKeys = new HashSet<>();
        cm = new ControllerManager();
        cm.initSDLGamepad();
        Runtime.getRuntime().addShutdownHook(new Thread(cm::quitSDLGamepad));
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

        ControllerState controllerState = cm.getState(player);

        switch (button) {
            case BUTTON_A:
                return pressedKeys.contains(KeyEvent.VK_K)
                    || controllerState.b;
            case BUTTON_B:
                return pressedKeys.contains(KeyEvent.VK_J)
                    || controllerState.a;
            case BUTTON_SELECT:
                return pressedKeys.contains(KeyEvent.VK_BACK_SPACE)
                    || controllerState.back;
            case BUTTON_START:
                return pressedKeys.contains(KeyEvent.VK_ENTER)
                    || controllerState.start;
            case BUTTON_UP:
                return pressedKeys.contains(KeyEvent.VK_UP)
                    || pressedKeys.contains(KeyEvent.VK_W)
                    || controllerState.dpadUp
                    || controllerState.leftStickY > ANALOG_DEADZONE;
            case BUTTON_DOWN:
                return pressedKeys.contains(KeyEvent.VK_DOWN)
                    || pressedKeys.contains(KeyEvent.VK_S)
                    || controllerState.dpadDown
                    || controllerState.leftStickY < -ANALOG_DEADZONE;
            case BUTTON_LEFT:
                return pressedKeys.contains(KeyEvent.VK_LEFT)
                    || pressedKeys.contains(KeyEvent.VK_A)
                    || controllerState.dpadLeft
                    || controllerState.leftStickX < -ANALOG_DEADZONE;
            case BUTTON_RIGHT:
                return pressedKeys.contains(KeyEvent.VK_RIGHT)
                    || pressedKeys.contains(KeyEvent.VK_D)
                    || controllerState.dpadRight
                    || controllerState.leftStickX > ANALOG_DEADZONE;
            default:
                throw new Error("Invalid Button");
        }
    }
}
