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


                // --- Autonomous Launch Sequence ---
                // 1. Spin up the launcher motor using setPower to match TeleOp
                .stopAndAdd(new InstantAction(() -> launcher.setPower(0.52)))// Using the power from XDriveDECODE
                .waitSeconds(3.0) // Wait 1.5s for the launcher to reach speed

                // 2. Push the note into the launcher
                .stopAndAdd(new InstantAction(() -> servo1.setPosition(0.0)))
                .stopAndAdd(new InstantAction(() -> servo2.setPosition(1.0)))
                .waitSeconds(0.7) // Wait for the servo to extend

                // 3. Retract the servo
                .stopAndAdd(new InstantAction(() -> servo1.setPosition(servoOffPosition)))
                .stopAndAdd(new InstantAction(() -> servo2.setPosition(servoOffPosition)))
                .waitSeconds(3.0) // Wait for the servo to retract

                .stopAndAdd(new InstantAction(() -> servo1.setPosition(0.0)))
                .stopAndAdd(new InstantAction(() -> servo2.setPosition(1.0)))
                .waitSeconds(0.7) // Wait for the servo to extend

                // 3. Retract the servo
                .stopAndAdd(new InstantAction(() -> servo1.setPosition(servoOffPosition)))
                .stopAndAdd(new InstantAction(() -> servo2.setPosition(servoOffPosition)))
                .waitSeconds(3.0) // Wait for the servo to retract

                .stopAndAdd(new InstantAction(() -> servo1.setPosition(0.0)))
                .stopAndAdd(new InstantAction(() -> servo2.setPosition(1.0)))
                .waitSeconds(0.7) // Wait for the servo to extend

                // 3. Retract the servo
                .stopAndAdd(new InstantAction(() -> servo1.setPosition(servoOffPosition)))
                .stopAndAdd(new InstantAction(() -> servo2.setPosition(servoOffPosition)))
                .waitSeconds(0.5) // Wait for the servo to retract

                .stopAndAdd(new InstantAction(() -> servo1.setPosition(servoOffPosition)))
                .stopAndAdd(new InstantAction(() -> servo2.setPosition(servoOffPosition)))
                .waitSeconds(3.0) // Wait for the servo to retract

                .stopAndAdd(new InstantAction(() -> servo1.setPosition(0.0)))
                .stopAndAdd(new InstantAction(() -> servo2.setPosition(1.0)))
                .waitSeconds(0.7) // Wait for the servo to extend
                // 4. Turn off the launcher
                .stopAndAdd(new InstantAction(() -> launcher.setPower(0)))

                .strafeToConstantHeading(new Vector2d(7.5, -5.6))
                .strafeToConstantHeading(new Vector2d(14.7, -15.7))
                // --- End of Launch Sequence ---

                .build();

        waitForStart();

        if (opModeIsActive()){
            Actions.runBlocking(autonomous);
        }
    }

}
