package de.mat2095.detunes;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

import java.awt.event.KeyEvent;
import java.util.*;

public class InputProviderImpl implements InputProvider {

    private static final float ANALOG_DEADZONE = 0.5f;

    private final List<Map<Button, InputTriggers>> inputMappings;

    private final Set<Integer> pressedKeys;
    private final ControllerManager cm;

    InputProviderImpl() {
        pressedKeys = new HashSet<>();
        inputMappings = new ArrayList<>(2);
        for (int player = 0; player < 2; player++) {
            Map<Button, InputTriggers> inputMapping = new EnumMap<>(Button.class);
            for (Button button : Button.values()) {
                inputMapping.put(button, new InputTriggers());
            }
            inputMappings.add(inputMapping);
        }
        applyDefaultMappings();
        cm = new ControllerManager();
        cm.initSDLGamepad();
        Runtime.getRuntime().addShutdownHook(new Thread(cm::quitSDLGamepad));
    }

    private void applyDefaultMappings() {
        for (int player = 0; player < 2; player++) {
            for (Button button : Button.values()) {
                inputMappings.get(player).get(button).clear();
            }
        }

        inputMappings.get(0).get(Button.BUTTON_A).keyboardCodes.add(KeyEvent.VK_K);
        inputMappings.get(0).get(Button.BUTTON_B).keyboardCodes.add(KeyEvent.VK_J);
        inputMappings.get(0).get(Button.BUTTON_SELECT).keyboardCodes.add(KeyEvent.VK_BACK_SPACE);
        inputMappings.get(0).get(Button.BUTTON_START).keyboardCodes.add(KeyEvent.VK_ENTER);
        inputMappings.get(0).get(Button.BUTTON_UP).keyboardCodes.add(KeyEvent.VK_UP);
        inputMappings.get(0).get(Button.BUTTON_UP).keyboardCodes.add(KeyEvent.VK_W);
        inputMappings.get(0).get(Button.BUTTON_DOWN).keyboardCodes.add(KeyEvent.VK_DOWN);
        inputMappings.get(0).get(Button.BUTTON_DOWN).keyboardCodes.add(KeyEvent.VK_S);
        inputMappings.get(0).get(Button.BUTTON_LEFT).keyboardCodes.add(KeyEvent.VK_LEFT);
        inputMappings.get(0).get(Button.BUTTON_LEFT).keyboardCodes.add(KeyEvent.VK_A);
        inputMappings.get(0).get(Button.BUTTON_RIGHT).keyboardCodes.add(KeyEvent.VK_RIGHT);
        inputMappings.get(0).get(Button.BUTTON_RIGHT).keyboardCodes.add(KeyEvent.VK_D);

        for (int player = 0; player < 2; player++) {
            inputMappings.get(player).get(Button.BUTTON_A)
                .controllerInputTriggers.add(new ControllerInputTrigger(player, ControllerInputType.BUTTON_B));
            inputMappings.get(player).get(Button.BUTTON_B)
                .controllerInputTriggers.add(new ControllerInputTrigger(player, ControllerInputType.BUTTON_A));
            inputMappings.get(player).get(Button.BUTTON_SELECT)
                .controllerInputTriggers.add(new ControllerInputTrigger(player, ControllerInputType.BUTTON_BACK));
            inputMappings.get(player).get(Button.BUTTON_START)
                .controllerInputTriggers.add(new ControllerInputTrigger(player, ControllerInputType.BUTTON_START));
            inputMappings.get(player).get(Button.BUTTON_UP)
                .controllerInputTriggers.add(new ControllerInputTrigger(player, ControllerInputType.DPAD_UP));
            inputMappings.get(player).get(Button.BUTTON_UP)
                .controllerInputTriggers.add(new ControllerInputTrigger(player, ControllerInputType.LEFT_STICK_UP));
            inputMappings.get(player).get(Button.BUTTON_DOWN)
                .controllerInputTriggers.add(new ControllerInputTrigger(player, ControllerInputType.DPAD_DOWN));
            inputMappings.get(player).get(Button.BUTTON_DOWN)
                .controllerInputTriggers.add(new ControllerInputTrigger(player, ControllerInputType.LEFT_STICK_DOWN));
            inputMappings.get(player).get(Button.BUTTON_LEFT)
                .controllerInputTriggers.add(new ControllerInputTrigger(player, ControllerInputType.DPAD_LEFT));
            inputMappings.get(player).get(Button.BUTTON_LEFT)
                .controllerInputTriggers.add(new ControllerInputTrigger(player, ControllerInputType.LEFT_STICK_LEFT));
            inputMappings.get(player).get(Button.BUTTON_RIGHT)
                .controllerInputTriggers.add(new ControllerInputTrigger(player, ControllerInputType.DPAD_RIGHT));
            inputMappings.get(player).get(Button.BUTTON_RIGHT)
                .controllerInputTriggers.add(new ControllerInputTrigger(player, ControllerInputType.LEFT_STICK_RIGHT));
        }
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
        InputTriggers inputTriggers = inputMappings.get(player).get(button);
        return !Collections.disjoint(inputTriggers.keyboardCodes, pressedKeys)
            || inputTriggers.controllerInputTriggers.stream().anyMatch(this::isActive);
    }

    private boolean isActive(ControllerInputTrigger controllerInputTrigger) { // TODO: naming (trigger/event/... and active/fired/...)
        ControllerState controllerState = cm.getState(controllerInputTrigger.player); // TODO: caching?

        switch (controllerInputTrigger.type) {
            case DPAD_UP:
                return controllerState.dpadUp;
            case DPAD_DOWN:
                return controllerState.dpadDown;
            case DPAD_LEFT:
                return controllerState.dpadLeft;
            case DPAD_RIGHT:
                return controllerState.dpadRight;
            case BUTTON_A:
                return controllerState.a;
            case BUTTON_B:
                return controllerState.b;
            case BUTTON_X:
                return controllerState.x;
            case BUTTON_Y:
                return controllerState.y;
            case BUTTON_BACK:
                return controllerState.back;
            case BUTTON_START:
                return controllerState.start;
            case BUTTON_GUIDE:
                return controllerState.guide;
            case BUMPER_LEFT:
                return controllerState.lb;
            case BUMPER_RIGHT:
                return controllerState.rb;
            case TRIGGER_LEFT:
                return controllerState.leftTrigger > ANALOG_DEADZONE;
            case TRIGGER_RIGHT:
                return controllerState.rightTrigger > ANALOG_DEADZONE;
            case LEFT_STICK_UP:
                return controllerState.leftStickY > ANALOG_DEADZONE;
            case LEFT_STICK_DOWN:
                return controllerState.leftStickY < -ANALOG_DEADZONE;
            case LEFT_STICK_LEFT:
                return controllerState.leftStickX < -ANALOG_DEADZONE;
            case LEFT_STICK_RIGHT:
                return controllerState.leftStickX > ANALOG_DEADZONE;
            case LEFT_STICK_CLICK:
                return controllerState.leftStickClick;
            case RIGHT_STICK_UP:
                return controllerState.rightStickY > ANALOG_DEADZONE;
            case RIGHT_STICK_DOWN:
                return controllerState.rightStickY < -ANALOG_DEADZONE;
            case RIGHT_STICK_LEFT:
                return controllerState.rightStickX < -ANALOG_DEADZONE;
            case RIGHT_STICK_RIGHT:
                return controllerState.rightStickX > ANALOG_DEADZONE;
            case RIGHT_STICK_CLICK:
                return controllerState.rightStickClick;
            default:
                throw new Error("Invalid ControllerInputType");
        }
    }

    class InputTriggers {
        final Set<Integer> keyboardCodes;
        final Set<ControllerInputTrigger> controllerInputTriggers;

        InputTriggers() {
            keyboardCodes = new HashSet<>();
            controllerInputTriggers = new HashSet<>();
        }

        void clear() {
            keyboardCodes.clear();
            controllerInputTriggers.clear();
        }
    }

    class ControllerInputTrigger {
        final int player;
        final ControllerInputType type;

        ControllerInputTrigger(int player, ControllerInputType type) {
            if (player < 0) {
                throw new IllegalArgumentException("player must be non-negative.");
            }
            if (type == null) {
                throw new IllegalArgumentException("type must be non-null.");
            }
            this.player = player;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ControllerInputTrigger that = (ControllerInputTrigger) o;
            return player == that.player &&
                type == that.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(player, type);
        }
    }

    enum ControllerInputType {
        DPAD_UP,
        DPAD_DOWN,
        DPAD_LEFT,
        DPAD_RIGHT,
        BUTTON_A,
        BUTTON_B,
        BUTTON_X,
        BUTTON_Y,
        BUTTON_BACK,
        BUTTON_START,
        BUTTON_GUIDE,
        BUMPER_LEFT,
        BUMPER_RIGHT,
        TRIGGER_LEFT,
        TRIGGER_RIGHT,
        LEFT_STICK_UP,
        LEFT_STICK_DOWN,
        LEFT_STICK_LEFT,
        LEFT_STICK_RIGHT,
        LEFT_STICK_CLICK,
        RIGHT_STICK_UP,
        RIGHT_STICK_DOWN,
        RIGHT_STICK_LEFT,
        RIGHT_STICK_RIGHT,
        RIGHT_STICK_CLICK
    }
}
