package org.firstinspires.ftc.teamcode.subsystems.enums;

import androidx.annotation.NonNull;

/**
 * Represents the buttons of an Xbox-style controller.
 * @author Landon Smith
 */
public enum GamepadButton {
    A("A"),
    B("B"),
    X("X"),
    Y("Y"),
    LEFT_BUMPER("Left Bumper"),
    RIGHT_BUMPER("Right Bumper"),
    LEFT_JOYSTICK_BUTTON("Left Joystick Button"),
    RIGHT_JOYSTICK_BUTTON("Right Joystick Button"),
    D_PAD_UP("D-Pad Up"),
    D_PAD_DOWN("D-Pad Down"),
    D_PAD_LEFT("D-Pad Left"),
    D_PAD_RIGHT("D-Pad Right");

    private final String displayName;

    GamepadButton(String displayName) {
        this.displayName = displayName;
    }

    @NonNull
    @Override
    public String toString() {
        return displayName;
    }
}
