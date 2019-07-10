package de.mat2095.detunes;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;
import java.awt.event.KeyEvent;
import java.util.*;


public class InputProviderImpl implements InputProvider {

    private static final float ANALOG_DEADZONE = 0.5f;
    private static final long CONTROLLER_CACHING_TIME = 1; // milliseconds

    private final List<Map<Button, Set<InputCondition>>> inputMappings;

    private final Set<Integer> pressedKeys;
    private final ControllerManager cm;
    private final Map<Integer, ControllerCachedState> cachedStates;

    InputProviderImpl() {
        pressedKeys = new HashSet<>();
        inputMappings = new ArrayList<>(2);
        for (int player = 0; player < 2; player++) {
            Map<Button, Set<InputCondition>> inputMapping = new EnumMap<>(Button.class);
            for (Button button : Button.values()) {
                inputMapping.put(button, new HashSet<>());
            }
            inputMappings.add(inputMapping);
        }
        applyDefaultMappings();
        cm = new ControllerManager();
        cm.initSDLGamepad();
        Runtime.getRuntime().addShutdownHook(new Thread(cm::quitSDLGamepad));
        cachedStates = new HashMap<>();
    }

    private void applyDefaultMappings() {
        for (int player = 0; player < 2; player++) {
            for (Button button : Button.values()) {
                inputMappings.get(player).get(button).clear();
            }
        }

        inputMappings.get(0).get(Button.BUTTON_A).add(new KeyboardInputCondition(KeyEvent.VK_K));
        inputMappings.get(0).get(Button.BUTTON_B).add(new KeyboardInputCondition(KeyEvent.VK_J));
        inputMappings.get(0).get(Button.BUTTON_SELECT).add(new KeyboardInputCondition(KeyEvent.VK_BACK_SPACE));
        inputMappings.get(0).get(Button.BUTTON_START).add(new KeyboardInputCondition(KeyEvent.VK_ENTER));
        inputMappings.get(0).get(Button.BUTTON_UP).add(new KeyboardInputCondition(KeyEvent.VK_UP));
        inputMappings.get(0).get(Button.BUTTON_UP).add(new KeyboardInputCondition(KeyEvent.VK_W));
        inputMappings.get(0).get(Button.BUTTON_DOWN).add(new KeyboardInputCondition(KeyEvent.VK_DOWN));
        inputMappings.get(0).get(Button.BUTTON_DOWN).add(new KeyboardInputCondition(KeyEvent.VK_S));
        inputMappings.get(0).get(Button.BUTTON_LEFT).add(new KeyboardInputCondition(KeyEvent.VK_LEFT));
        inputMappings.get(0).get(Button.BUTTON_LEFT).add(new KeyboardInputCondition(KeyEvent.VK_A));
        inputMappings.get(0).get(Button.BUTTON_RIGHT).add(new KeyboardInputCondition(KeyEvent.VK_RIGHT));
        inputMappings.get(0).get(Button.BUTTON_RIGHT).add(new KeyboardInputCondition(KeyEvent.VK_D));

        for (int player = 0; player < 2; player++) {
            inputMappings.get(player).get(Button.BUTTON_A)
                .add(new ControllerInputCondition(player, ControllerInputConditionType.BUTTON_B));
            inputMappings.get(player).get(Button.BUTTON_B)
                .add(new ControllerInputCondition(player, ControllerInputConditionType.BUTTON_A));
            inputMappings.get(player).get(Button.BUTTON_SELECT)
                .add(new ControllerInputCondition(player, ControllerInputConditionType.BUTTON_BACK));
            inputMappings.get(player).get(Button.BUTTON_START)
                .add(new ControllerInputCondition(player, ControllerInputConditionType.BUTTON_START));
            inputMappings.get(player).get(Button.BUTTON_UP)
                .add(new ControllerInputCondition(player, ControllerInputConditionType.DPAD_UP));
            inputMappings.get(player).get(Button.BUTTON_UP)
                .add(new ControllerInputCondition(player, ControllerInputConditionType.LEFT_STICK_UP));
            inputMappings.get(player).get(Button.BUTTON_DOWN)
                .add(new ControllerInputCondition(player, ControllerInputConditionType.DPAD_DOWN));
            inputMappings.get(player).get(Button.BUTTON_DOWN)
                .add(new ControllerInputCondition(player, ControllerInputConditionType.LEFT_STICK_DOWN));
            inputMappings.get(player).get(Button.BUTTON_LEFT)
                .add(new ControllerInputCondition(player, ControllerInputConditionType.DPAD_LEFT));
            inputMappings.get(player).get(Button.BUTTON_LEFT)
                .add(new ControllerInputCondition(player, ControllerInputConditionType.LEFT_STICK_LEFT));
            inputMappings.get(player).get(Button.BUTTON_RIGHT)
                .add(new ControllerInputCondition(player, ControllerInputConditionType.DPAD_RIGHT));
            inputMappings.get(player).get(Button.BUTTON_RIGHT)
                .add(new ControllerInputCondition(player, ControllerInputConditionType.LEFT_STICK_RIGHT));
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
        return inputMappings.get(player).get(button)
            .stream().anyMatch(InputCondition::isFulfilled);
    }

    Set<InputCondition> getInputConditions(int player, Button button) {
        return inputMappings.get(player).get(button);
    }

    void setInputConditions(int player, Button button, Set<InputCondition> inputConditions) {
        inputMappings.get(player).put(button, inputConditions);
    }

    private ControllerState getControllerState(int player) {
        return cachedStates.computeIfAbsent(player, integer -> new ControllerCachedState(player)).getState();
    }

    Set<ControllerInputCondition> getCurrentlyFulfilledControllerInputConditions() {
        cm.update();
        Set<ControllerInputCondition> result = new HashSet<>();
        for (int player = 0; player < cm.getNumControllers(); player++) {
            ControllerState controllerState = getControllerState(player);
            if (controllerState.dpadUp) {
                result.add(new ControllerInputCondition(player, ControllerInputConditionType.DPAD_UP));
            }
            if (controllerState.dpadDown) {
                result.add(new ControllerInputCondition(player, ControllerInputConditionType.DPAD_DOWN));
            }
            if (controllerState.dpadLeft) {
                result.add(new ControllerInputCondition(player, ControllerInputConditionType.DPAD_LEFT));
            }
            if (controllerState.dpadRight) {
                result.add(new ControllerInputCondition(player, ControllerInputConditionType.DPAD_RIGHT));
            }
            if (controllerState.a) {
                result.add(new ControllerInputCondition(player, ControllerInputConditionType.BUTTON_A));
            }
            if (controllerState.b) {
                result.add(new ControllerInputCondition(player, ControllerInputConditionType.BUTTON_B));
            }
        }
        return result;
    }


    private class ControllerCachedState {
        private final int player;
        private long updateTime;
        private ControllerState state;

        private ControllerCachedState(int player) {
            this.player = player;
        }

        private ControllerState getState() {
            long currentTime = System.currentTimeMillis();
            if (currentTime - updateTime >= CONTROLLER_CACHING_TIME) {
                state = cm.getState(player);
                updateTime = currentTime;
            }
            return state;
        }
    }


    abstract class InputCondition implements Comparable<InputCondition> {
        abstract boolean isFulfilled();

        @Override
        public int compareTo(InputCondition o) {
            return Comparator.comparing(o1 -> o1.getClass().getSimpleName())
                .reversed()
                .compare(this, o);
        }
    }

    class KeyboardInputCondition extends InputCondition {
        final int keyCode;

        KeyboardInputCondition(int keyCode) {
            this.keyCode = keyCode;
        }

        @Override
        boolean isFulfilled() {
            return pressedKeys.contains(keyCode);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            KeyboardInputCondition that = (KeyboardInputCondition) o;
            return keyCode == that.keyCode;
        }

        @Override
        public int hashCode() {
            return Objects.hash(keyCode);
        }

        @Override
        public String toString() {
            return "Keyboard: " + KeyEvent.getKeyText(keyCode);
        }

        @Override
        public int compareTo(InputCondition o) {
            if (o instanceof KeyboardInputCondition) {
                return Comparator.comparingInt((KeyboardInputCondition kic) -> kic.keyCode)
                    .compare(this, (KeyboardInputCondition) o);
            } else {
                return super.compareTo(o);
            }
        }
    }


    class ControllerInputCondition extends InputCondition {
        final int player;
        final ControllerInputConditionType type;

        ControllerInputCondition(int player, ControllerInputConditionType type) {
            if (player < 0) {
                throw new IllegalArgumentException("player must be non-negative.");
            }
            if (type == null) {
                throw new IllegalArgumentException("type must be non-null.");
            }
            this.player = player;
            this.type = type;
        }

        boolean isFulfilled() {
            ControllerState controllerState = getControllerState(player);

            switch (type) {
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
                    throw new Error("Invalid ControllerInputConditionType");
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ControllerInputCondition that = (ControllerInputCondition) o;
            return player == that.player &&
                type == that.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(player, type);
        }

        @Override
        public String toString() {
            return "Controller " + player + ": " + type;
        }

        @Override
        public int compareTo(InputCondition o) {
            if (o instanceof ControllerInputCondition) {
                return Comparator.comparingInt((ControllerInputCondition cic) -> cic.player)
                    .thenComparing(cic -> cic.type)
                    .compare(this, (ControllerInputCondition) o);
            } else {
                return super.compareTo(o);
            }
        }
    }


    enum ControllerInputConditionType {
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
