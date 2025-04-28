package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Represents the state of a button on the gamepad.
 */
class ButtonState {
    public final GamepadButton button;
    public volatile boolean isPressed;
    public BiStateButtonBehavior behavior;
    public boolean value;
    private volatile boolean wasHeld;

    public ButtonState(GamepadButton button, BiStateButtonBehavior behavior) {
        this.button = button;
        this.isPressed = false;
        this.behavior = behavior;
        this.value = false;
        this.wasHeld = false;
    }

    /**
     * Updates the button state based on the given pressed state.
     */
    public void update(boolean pressed) {
        isPressed = pressed;

        switch (behavior) {
            case HOLD:
                value = isPressed;
                break;
            case TOGGLE:
                if (isPressed && !wasHeld) {
                    value = !value;
                }
                wasHeld = isPressed;
                break;
        }
    }
}

/**
 * Represents a ButtonState, but for buttons that work in a TRI_STATE pattern.
 * It has two input buttons for state: One sets to -1, the other sets to 1
 */
class TristateButtonState {
    public final GamepadButton buttonPositive;
    public final GamepadButton buttonNegative;
    public volatile boolean isPositivePressed;
    public volatile boolean isNegativePressed;
    public volatile int value;

    public TristateButtonState(GamepadButton buttonPositive, GamepadButton buttonNegative) {
        this.buttonPositive = buttonPositive;
        this.buttonNegative = buttonNegative;
        this.isPositivePressed = false;
        this.isNegativePressed = false;
        this.value = 0;
    }

    /**
     * Updates the TristateButtonState's value based on the two button inputs
     */
    public void update(boolean positivePressed, boolean negativePressed) {
        isPositivePressed = positivePressed;
        isNegativePressed = negativePressed;
        value = (isPositivePressed && !isNegativePressed) ? 1 : (!isPositivePressed && isNegativePressed) ? -1 : 0;
    }
}

/**
 * Represents the state of an analog trigger or joystick axis on the gamepad.
 */
class AxisState {
    public volatile double value;

    public AxisState() {
        this.value = 0;
    }

    /**
     * Updates the axis state value
     */
    public void update(double value) {
        this.value = value;
    }
}

/**
 * Represents a gamepad, providing an interface for accessing button and analog states.
 */
public class GamepadController {
    // Mappings for button states and their behavior
    private final Map<GamepadButton, ButtonState> buttonStates = new HashMap<>();

    // Mapping for Tristate buttons
    private final Map<GamepadButton, TristateButtonState> tristateButtonStates = new HashMap<>();

    // Mapping for analog states and joysticks
    private final Map<axisBehavior, AxisState> axisStates = new HashMap<>();
    private final Gamepad gamepad;

    public GamepadController(Gamepad gamepad) {
        this.gamepad = gamepad;
    }

    /**
     * Configures a  bi-state button with a specific behavior.
     */
    public void configureBiStateButton(GamepadButton button, BiStateButtonBehavior behavior) {
        buttonStates.put(button, new ButtonState(button, behavior));
    }

    /**
     * Configures a tri-state button with a specific behavior.
     */
    public void configureTristateButton(GamepadButton buttonPositive, GamepadButton buttonNegative) {
        tristateButtonStates.put(buttonPositive, new TristateButtonState(buttonPositive, buttonNegative));
    }

    /**
     * Configures an axis for reading data
     */
    public void configureAxis(axisBehavior axis) {
        axisStates.put(axis, new AxisState());
    }

    /**
     * Updates the state of all inputs based on the given input events.
     */
    public void update() {
        updateRawNormalButtonStates();
        updateRawTristateButtonStates();
        updateRawAxisStates();
    }

    private void updateRawAxisStates() {
        for (axisBehavior axis : axisStates.keySet()) {
            switch (axis) {
                case LEFT_STICK_X:
                    Objects.requireNonNull(axisStates.get(axis)).update(gamepad.left_stick_x);
                    break;
                case LEFT_STICK_Y:
                    Objects.requireNonNull(axisStates.get(axis)).update(gamepad.left_stick_y);
                    break;
                case RIGHT_STICK_X:
                    Objects.requireNonNull(axisStates.get(axis)).update(gamepad.right_stick_x);
                    break;
                case RIGHT_STICK_Y:
                    Objects.requireNonNull(axisStates.get(axis)).update(gamepad.right_stick_y);
                    break;
                case LEFT_TRIGGER:
                    Objects.requireNonNull(axisStates.get(axis)).update(gamepad.left_trigger);
                    break;
                case RIGHT_TRIGGER:
                    Objects.requireNonNull(axisStates.get(axis)).update(gamepad.right_trigger);
                    break;

            }
        }
    }

    private void updateRawTristateButtonStates() {
        for (TristateButtonState state : tristateButtonStates.values()) {
            state.update(getButtonState(state.buttonPositive), getButtonState(state.buttonNegative));
        }
    }

    private boolean getButtonState(GamepadButton button) {// This switch-case is enormous, but thankfully readable
        switch (button) {
            case A:
                return gamepad.a;
            case B:
                return gamepad.b;
            case X:
                return gamepad.x;
            case Y:
                return gamepad.y;
            case LEFT_BUMPER:
                return gamepad.left_bumper;
            case RIGHT_BUMPER:
                return gamepad.right_bumper;
            case LEFT_JOYSTICK_BUTTON:
                return gamepad.left_stick_button;
            case RIGHT_JOYSTICK_BUTTON:
                return gamepad.right_stick_button;
            case D_PAD_UP:
                return gamepad.dpad_up;
            case D_PAD_DOWN:
                return gamepad.dpad_down;
            case D_PAD_LEFT:
                return gamepad.dpad_left;
            case D_PAD_RIGHT:
                return gamepad.dpad_right;
            default:
                throw new UnsupportedOperationException("Button " + button + " not configured.");
        }
    }

    private void updateRawNormalButtonStates() {
        for (GamepadButton button : buttonStates.keySet()) {
            Objects.requireNonNull(buttonStates.get(button)).update(getButtonState(button));

        }
    }

    /**
     * Returns the current value of the specified Gamepad Button.
     * Will throw an error if the button doesn't exist
     */
    public boolean getGamepadButtonValue(GamepadButton button) {
        ButtonState state = buttonStates.get(button);
        if (state == null) {
            throw new NoSuchElementException("Button " + button + " not configured.");
        }
        return state.value;
    }

    /**
     * Returns the current value of the specified tristate button.
     * Will throw an error if the button doesn't exist
     */
    public int getTristateButtonValue(GamepadButton buttonPositive) {
        TristateButtonState state = tristateButtonStates.get(buttonPositive);
        if (state == null) {
            throw new NoSuchElementException("Tristate button for " + buttonPositive + " not configured.");
        }
        return state.value;
    }

    /**
     * Returns the current value of the specified axis.
     * Will throw an error if the axis doesn't exist
     */
    public double getAxisValue(axisBehavior axis) {
        AxisState state = axisStates.get(axis);
        if (state == null) {
            throw new NoSuchElementException("Axis " + axis + " not configured.");
        }
        return state.value;
    }
}