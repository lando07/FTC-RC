package org.firstinspires.ftc.teamcode.subsystems;

import androidx.annotation.NonNull;

/**
 * Represents the behaviors that can be assigned to a button.
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
