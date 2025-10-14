package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
/**
 * Represents a gamepad, providing an interface for accessing button and analog states.
 */
public class GamepadController {
    /**
     * Mappings for button states and their behavior
     */
    private final Map<GamepadButton, ButtonState> buttonStates = new HashMap<>();

    /**
     * Mapping for Tristate buttons
     */
    private final Map<GamepadButton, TristateButtonState> tristateButtonStates = new HashMap<>();

    /**
     * Mapping for analog states and joysticks
     */
    private final Map<axisBehavior, AxisState> axisStates = new HashMap<>();
    /**
     * Stores the OpMode gamepad which either refers to gamepad1 or gamepad2
     */
    private final Gamepad gamepad;

    /**
     * Constructor to create a GamepadController object.
     *
     * @param gamepad the gamepad to be tracked
     */
    public GamepadController(Gamepad gamepad) {
        this.gamepad = gamepad;
    }

    /**
     * Configures a bi-state button with a specific behavior.
     * @throws IllegalArgumentException if the button is already configured.
     */
    public void configureBiStateButton(GamepadButton button, BiStateButtonBehavior behavior) {
        checkButtonNotAlreadyConfigured(button);
        buttonStates.put(button, new ButtonState(button, behavior));
    }

    /**
     * Configures a pair of buttons to act as a single tri-state input.
     * @throws IllegalArgumentException if the buttons are the same or are already configured.
     */
    public void configureTristateButton(GamepadButton buttonPositive, GamepadButton buttonNegative) {
        if (buttonPositive == buttonNegative) {
            throw new IllegalArgumentException("Positive and negative buttons for a tri-state configuration cannot be the same.");
        }
        checkButtonNotAlreadyConfigured(buttonPositive);
        checkButtonNotAlreadyConfigured(buttonNegative);
        tristateButtonStates.put(buttonPositive, new TristateButtonState(buttonPositive, buttonNegative));
    }

    /**
     * Configures an axis for reading data.
     * @throws IllegalArgumentException if the axis is already configured.
     */
    public void configureAxis(axisBehavior axis) {
        if (axisStates.containsKey(axis)){
            throw new IllegalArgumentException("Axis " + axis + " already configured.");
        }
        axisStates.put(axis, new AxisState());
    }
    /**
     * Checks if a button is already configured in any capacity (bistate or tristate).
     * @param button The button to check.
     * @throws IllegalArgumentException if the button is already configured.
     */
    private void checkButtonNotAlreadyConfigured(GamepadButton button) {
        if (buttonStates.containsKey(button)) {
            throw new IllegalArgumentException("Button " + button + " is already configured as a bi-state button.");
        }
        // Check if the button is part of any tristate configuration
        for (TristateButtonState state : tristateButtonStates.values()) {
            if (state.buttonPositive == button || state.buttonNegative == button) {
                throw new IllegalArgumentException("Button " + button + " is already part of a tri-state configuration.");
            }
        }
    }
    /**
     * Updates the state of all configured inputs by reading the raw gamepad values.
     * This method should be called once per loop cycle.
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

    /**
     * Gets the raw current state of a button from the gamepad hardware.
     * @param button The button to read
     * @return The current state of the button (true for pressed, false for not pressed)
     * @throws UnsupportedOperationException if the button enum is not mapped to a physical button.
     */
    private boolean getButtonState(GamepadButton button) {
        // This switch-case is enormous, but thankfully readable lol
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
                // This case should ideally not be reached if button enums are managed properly.
                throw new UnsupportedOperationException("Button " + button + " is not mapped to a physical gamepad button.");
        }
    }

    private void updateRawNormalButtonStates() {
        for (GamepadButton button : buttonStates.keySet()) {
            Objects.requireNonNull(buttonStates.get(button)).update(getButtonState(button));

        }
    }

    /**
     * Returns the processed value of the specified Gamepad Button based on its configured behavior.
     *
     * @param button The button to read
     * @return The processed boolean value of the button.
     * @throws NoSuchElementException if the button is not configured as a bi-state button.
     */
    public boolean getGamepadButtonValue(GamepadButton button) {
        ButtonState state = buttonStates.get(button);
        if (state == null) {
            throw new NoSuchElementException("Button " + button + " not configured. Ensure you call configureBiStateButton() first.");
        }
        return state.value;
    }

    /**
     * Returns the current value of the specified tristate button group.
     *
     * @param buttonPositive The positive button of the tristate pair used as the key.
     * @return 1 if the positive button is pressed, -1 if the negative is pressed, 0 otherwise.
     * @throws NoSuchElementException if no tri-state button is configured with this positive button.
     */
    public int getTristateButtonValue(GamepadButton buttonPositive) {
        TristateButtonState state = tristateButtonStates.get(buttonPositive);
        if (state == null) {
            throw new NoSuchElementException("Tristate button group starting with " + buttonPositive + " not configured. Ensure you call configureTristateButton() first.");
        }
        return state.value;
    }

    /**
     * Returns the current value of the specified axis.
     *
     * @param axis The axis to read.
     * @return The double value of the axis, ranges from [-1.0, 1.0].
     * @throws NoSuchElementException if the axis is not configured.
     */
    public double getAxisValue(axisBehavior axis) {
        AxisState state = axisStates.get(axis);
        if (state == null) {
            throw new NoSuchElementException("Axis " + axis + " not configured. Ensure you call configureAxis() first.");
        }
        return state.value;
    }
}
/**
 * Represents the state of a button on the gamepad.
 */
class ButtonState {
    /**
     * Stores the gamepad button enum constant
     */
    final GamepadButton button;
    volatile boolean isPressed = false;
    /**
     * Stores the behavior of the button
     */
    BiStateButtonBehavior behavior;
    boolean value = false;
    /**
     * Tracks the pressed state of the button from the previous update cycle.
     * This is used by the TOGGLE behavior to ensure the value only changes once per button press,
     * rather than flipping continuously while the button is held down.
     */
    private volatile boolean wasHeld = false;

    /**
     * Creates an object representing the state of a button on the gamepad
     *
     * @param button   the button to be tracked
     * @param behavior what is returned when the button is read
     */
    ButtonState(GamepadButton button, BiStateButtonBehavior behavior) {
        this.button = button;
        this.behavior = behavior;
    }

    /**
     * Updates the button state
     *
     * @param pressed whether the button is currently pressed
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
    /**
     * Stores the button, that if pressed, will return 1
     */
    final GamepadButton buttonPositive;
    /**
     * Stores the button, that if pressed, will return -1
     */
    final GamepadButton buttonNegative;
    private volatile boolean isPositivePressed = false;
    private volatile boolean isNegativePressed = false;
    volatile int value = 0;

    /**
     * Constructor to create a Tri-State button. This creates a behavior similar to a d-pad
     * where any 2 buttons can be configured as a single button, where one will return 1 and the
     * other will return -1, and neither pressed will return zero
     *
     * @param buttonPositive the button, if pressed, to return 1
     * @param buttonNegative the button, if pressed, to return -1
     */
    TristateButtonState(GamepadButton buttonPositive, GamepadButton buttonNegative) {
        this.buttonPositive = buttonPositive;
        this.buttonNegative = buttonNegative;
    }

    /**
     * Updates the TristateButtonState's value based on the two button inputs.
     * - Sets value to 1 if only the positive button is pressed.
     * - Sets value to -1 if only the negative button is pressed.
     * - Sets value to 0 if neither or both buttons are pressed.
     * @param positivePressed whether the positive button is currently pressed
     * @param negativePressed whether the negative button is currently pressed
     */
    void update(boolean positivePressed, boolean negativePressed) {
        isPositivePressed = positivePressed;
        isNegativePressed = negativePressed;
        if (isPositivePressed && !isNegativePressed) {
            value = 1;
        } else if (!isPositivePressed && isNegativePressed) {
            value = -1;
        } else {
            // This handles both the case where no buttons are pressed and
            // the case where both are pressed simultaneously.
            value = 0;
        }
    }
}

/**
 * Represents the state of an analog trigger or joystick axis on the gamepad.
 */
class AxisState {
    volatile double value;

    AxisState() {
        this.value = 0;
    }

    /**
     * Updates the axis state value
     *
     * @param value the new value of the axis
     */
    public void update(double value) {
        this.value = value;
    }
}