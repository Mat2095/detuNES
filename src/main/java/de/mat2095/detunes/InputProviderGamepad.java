package de.mat2095.detunes;


import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;


class InputProviderGamepad implements InputProvider {

    private static final float ANALOG_DEADZONE = 0.5f;

    private final ControllerManager cm;

    InputProviderGamepad() {
        cm = new ControllerManager();
        cm.initSDLGamepad();
        Runtime.getRuntime().addShutdownHook(new Thread(cm::quitSDLGamepad));
    }

    @Override
    public boolean isButtonPressed(int player, Button button) {

        ControllerState controllerState = cm.getState(player);

        switch (button) {
            case BUTTON_A:
                return controllerState.b;
            case BUTTON_B:
                return controllerState.a;
            case BUTTON_SELECT:
                return controllerState.back;
            case BUTTON_START:
                return controllerState.start;
            case BUTTON_UP:
                return controllerState.dpadUp || controllerState.leftStickY > ANALOG_DEADZONE;
            case BUTTON_DOWN:
                return controllerState.dpadDown || controllerState.leftStickY < -ANALOG_DEADZONE;
            case BUTTON_LEFT:
                return controllerState.dpadLeft || controllerState.leftStickX < -ANALOG_DEADZONE;
            case BUTTON_RIGHT:
                return controllerState.dpadRight || controllerState.leftStickX > ANALOG_DEADZONE;
            default:
                throw new Error("Invalid Button");
        }
    }
}
