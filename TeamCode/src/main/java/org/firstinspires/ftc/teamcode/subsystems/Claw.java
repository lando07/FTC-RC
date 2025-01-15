package org.firstinspires.ftc.teamcode.subsystems;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

public class Claw {
    private Servo claw;
    private static final double OPEN = 0.1;
    private static final double CLOSED = 1;

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
