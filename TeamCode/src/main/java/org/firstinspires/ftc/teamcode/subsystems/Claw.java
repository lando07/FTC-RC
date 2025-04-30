package org.firstinspires.ftc.teamcode.subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.InstantAction;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class Claw {
    public static double OPEN = 0.55;
    public static double CLOSED = 0.05;
    public static GamepadButton toggleButton = GamepadButton.B;
    public static BiStateButtonBehavior toggleBehavior = BiStateButtonBehavior.TOGGLE;
    private final Servo claw;
    private boolean clawState;//false closed, true open
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
        toggleState();

    }

    public InstantAction closeClawAction(){
        return new InstantAction(this::closeClaw);
    }
    public InstantAction openClawAction(){
        return new InstantAction(this::openClaw);
    }
}
