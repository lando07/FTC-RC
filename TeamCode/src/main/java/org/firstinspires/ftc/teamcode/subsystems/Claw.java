package org.firstinspires.ftc.teamcode.subsystems;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

public class Claw {
    private Servo claw;
    public static double OPEN = 0.05;
    public static double CLOSED = 0.55;

    private boolean clawState;//false closed, true open

    public Claw(@NonNull OpMode opMode, String hwName) {
        claw = opMode.hardwareMap.get(Servo.class, hwName);
        claw.setPosition(CLOSED);
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
}
