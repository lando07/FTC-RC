package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

@Config
@Autonomous(name = "BlueLargeAutoDecode", group="autonomous")
public class BlueLargeAutoDecode extends LinearOpMode {
    @Override
    public void runOpMode(){
        Pose2d startingPose = new Pose2d(-52.0,-46.1,Math.toRadians(232));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startingPose);

        // --- Initialize Launcher and Servos ---
        DcMotorEx launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        Servo servo1 = hardwareMap.get(Servo.class, "servo1");
        Servo servo2 = hardwareMap.get(Servo.class, "servo2");

            launcher.setDirection(DcMotorEx.Direction.REVERSE);
            // Using setPower to match TeleOp, so RUN_USING_ENCODER is not needed.

        double servoOffPosition = 0.5;
        servo1.setPosition(servoOffPosition);
        servo2.setPosition(servoOffPosition);
        // --- End of Initialization ---

        Action autonomous = drive.actionBuilder(startingPose)
                // Current Path



                .build();

        waitForStart();

        if (opModeIsActive()){
            Actions.runBlocking(autonomous);
        }
    }

}
