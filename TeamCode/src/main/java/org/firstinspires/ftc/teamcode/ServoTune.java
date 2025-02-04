package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
@TeleOp(name="Servo Tune", group="TeleOp")
public class ServoTune extends  OpMode{
    private Servo testServo;

    @Override
    public void init() {
        testServo = hardwareMap.get(Servo.class, "Claw");
    }

    public void loop() {
        testServo.setPosition(gamepad1.left_stick_y);
        telemetry.addData("Position: ", testServo.getPosition());
    }

}
