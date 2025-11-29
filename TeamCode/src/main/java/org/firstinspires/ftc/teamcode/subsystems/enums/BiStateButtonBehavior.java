package org.firstinspires.ftc.teamcode.subsystems.enums;

import androidx.annotation.NonNull;

/**
 * Represents the behaviors that can be assigned to a button.
 * @author Landon Smith
 */
public enum BiStateButtonBehavior {
    TOGGLE("Toggle"),
    HOLD("Hold");

    private final String displayName;

    BiStateButtonBehavior(String displayName) {
        this.displayName = displayName;
    }

    @NonNull
    @Override
    public String toString() {
        return displayName;
    }
}
