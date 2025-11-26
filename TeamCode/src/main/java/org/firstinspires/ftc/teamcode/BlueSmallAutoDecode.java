package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

@Config
@Autonomous(name = "BlueSmallAutoDecode", group="autonomous")
public class BlueSmallAutoDecode extends LinearOpMode {
    @Override
    public void runOpMode(){
        double originalMaxWheelVel = MecanumDrive.PARAMS.maxWheelVel;
        try {
            MecanumDrive.PARAMS.maxWheelVel = originalMaxWheelVel * 0.9;

            Pose2d startingPose = new Pose2d(61.8, -12.5, Math.toRadians(180));
            MecanumDrive drive = new MecanumDrive(hardwareMap, startingPose);

            // --- Initialize Launcher and Servos ---
            DcMotorEx launcher = hardwareMap.get(DcMotorEx.class, "launcher");
            Servo servo1 = hardwareMap.get(Servo.class, "servo1");
            Servo servo2 = hardwareMap.get(Servo.class, "servo2");
            Servo servo3 = hardwareMap.get(Servo.class, "servo3");
            Servo servo4 = hardwareMap.get(Servo.class, "servo4");


            launcher.setDirection(DcMotorEx.Direction.REVERSE);
            // Using setPower to match TeleOp, so RUN_USING_ENCODER is not needed.

            double servoOffPosition = 0.5;
            servo1.setPosition(servoOffPosition);
            servo2.setPosition(servoOffPosition);
            servo3.setPosition(servoOffPosition);
            servo4.setPosition(servoOffPosition);

            // --- End of Initialization ---

            Action autonomous = drive.actionBuilder(startingPose)
                    // Current Path
                    .strafeToConstantHeading(new Vector2d(36.3,-12.7))
                    .turn(Math.toRadians(90))

                    .strafeToConstantHeading(new Vector2d(35.0,-56.0))
                    .strafeToConstantHeading(new Vector2d(36.3,-12.7))
                    .turn(Math.toRadians(-90))
                    .strafeToConstantHeading(new Vector2d(-30.8,-15.1))
                    .turn(Math.toRadians(54))
                    .strafeToConstantHeading(new Vector2d(-53.4,-46.1))

                    .strafeToConstantHeading(new Vector2d(-30.8,-15.1))
                    .turn(Math.toRadians(127))
                    .strafeToConstantHeading(new Vector2d(10.5,-13.5))
                    .turn(Math.toRadians(-92))

                    .strafeToConstantHeading(new Vector2d(11.5,-51.4))
                    .strafeToConstantHeading(new Vector2d(10.5,-13.5))
                    .turn(Math.toRadians(-92))
                    .strafeToConstantHeading(new Vector2d(-30.8,-15.1))
                    .turn(Math.toRadians(54))
                    .strafeToConstantHeading(new Vector2d(-53.4,-46.1))
                    .strafeToConstantHeading(new Vector2d(-30.8,-15.1))
                    .turn(Math.toRadians(127))
                    .strafeToConstantHeading(new Vector2d(-12.5,-14.1))
                    .turn(Math.toRadians(-90))
                    .strafeToConstantHeading(new Vector2d(-12.5,-37.5))




                    // --- End of Launch Sequence ---

                    .build();

            waitForStart();

            if (opModeIsActive()) {
                Actions.runBlocking(autonomous);
            }
        } finally {
            MecanumDrive.PARAMS.maxWheelVel = originalMaxWheelVel;
        }
    }

}
