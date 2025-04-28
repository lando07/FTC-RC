package org.firstinspires.ftc.teamcode.subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;
@Config
public class Claw {
    private Servo claw;
    public static double OPEN = 0.55;
    public static double CLOSED = 0.05;

    private boolean clawState;//false closed, true open

    public static GamepadButton toggleButton = GamepadButton.B;
    public static BiStateButtonBehavior toggleBehavior = BiStateButtonBehavior.TOGGLE;
    private GamepadController controller;

    public Claw(@NonNull OpMode opMode, String hwName, GamepadController gamepad) {
        claw = opMode.hardwareMap.get(Servo.class, hwName);
//        claw.setPosition(CLOSED);
        clawState = false;
        controller = gamepad;
        controller.configureBiStateButton(toggleButton, toggleBehavior);

    }
    public Claw(@NonNull OpMode opMode, String hwName) {
        claw = opMode.hardwareMap.get(Servo.class, hwName);
//        claw.setPosition(CLOSED);
        clawState = false;

    }

    public void toggleState() {
        if (clawState) {
            closeClaw();
        } else {
            openClaw();
        }
    }

    public void closeClaw() {
        claw.setPosition(CLOSED);
        clawState = false;
    }

    public void openClaw() {
        claw.setPosition(OPEN);
        clawState = true;
    }

    public double getPosition() {
        return claw.getPosition();
    }

    public void update() {
        clawState = controller.getGamepadButtonValue(toggleButton);
        if (clawState) {
            claw.setPosition(OPEN);
        } else {
            claw.setPosition(CLOSED);
        }

    }
}
