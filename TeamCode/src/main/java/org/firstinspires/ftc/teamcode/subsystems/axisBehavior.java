package org.firstinspires.ftc.teamcode.subsystems;

import androidx.annotation.NonNull;

/**
 * All supported axes on the controller
 */
public enum axisBehavior {
    LEFT_STICK_X("Left Stick X-Axis"),
    LEFT_STICK_Y("Left Stick Y-Axis"),
    RIGHT_STICK_X("Right Stick X-Axis"),
    RIGHT_STICK_Y("Right Stick Y-Axis"),
    LEFT_TRIGGER("Left Trigger"),
    RIGHT_TRIGGER("Right Trigger");
    private final String displayName;

    axisBehavior(String displayName) {
        this.displayName = displayName;
    }

    @NonNull
    @Override
    public String toString() {
        return displayName;
    }
}
