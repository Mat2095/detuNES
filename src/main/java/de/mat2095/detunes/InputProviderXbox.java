package de.mat2095.detunes;

import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;


class InputProviderXbox implements InputProvider {

    private static final float ANALOG_DEADZONE = 0.5f;

    private final XInputDevice controller;

    InputProviderXbox(int player) throws XInputNotLoadedException {
        try {
            controller = XInputDevice.getDeviceFor(player);
        } catch (UnsatisfiedLinkError | NoClassDefFoundError e) {
            throw new XInputNotLoadedException(e.getMessage(), e);
        }
    }

    @Override
    public boolean isButtonPressed(Button button) {

        controller.poll();

        XInputComponents controllerState = controller.getComponents();

        switch (button) {
            case BUTTON_A:
                return controllerState.getButtons().b;
            case BUTTON_B:
                return controllerState.getButtons().a;
            case BUTTON_SELECT:
                return controllerState.getButtons().back;
            case BUTTON_START:
                return controllerState.getButtons().start;
            case BUTTON_UP:
                return controllerState.getButtons().up || controllerState.getAxes().ly > ANALOG_DEADZONE;
            case BUTTON_DOWN:
                return controllerState.getButtons().down || controllerState.getAxes().ly < -ANALOG_DEADZONE;
            case BUTTON_LEFT:
                return controllerState.getButtons().left || controllerState.getAxes().lx < -ANALOG_DEADZONE;
            case BUTTON_RIGHT:
                return controllerState.getButtons().right || controllerState.getAxes().lx > ANALOG_DEADZONE;
            default:
                throw new Error("Invalid Button");
        }
    }
}
