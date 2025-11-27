package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

@Config
@Autonomous(name = "Red Start Zone", group="autonomous")
public class RedStartZoneAuto extends LinearOpMode {
    @Override
    public void runOpMode(){
        Pose2d startingPose = new Pose2d(57.8,12.7,Math.toRadians(180));
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

        MecanumDrive.DriveLocalizer dl = (MecanumDrive.DriveLocalizer) drive.localizer;

        Action autonomous = drive.actionBuilder(startingPose)

                // Current Path
                .strafeToConstantHeading(new Vector2d(36.3,12.7))
                .turn(Math.toRadians(-90))

                .strafeToConstantHeading(new Vector2d(34.8,56.2))
                .strafeToConstantHeading(new Vector2d(35.9,24.4))
                .turn(Math.toRadians(90))
                .strafeToConstantHeading(new Vector2d(-31.0,24.2))
                .turn(Math.toRadians(-54))
                .strafeToConstantHeading(new Vector2d(-51.2,48.9))

                .strafeToConstantHeading(new Vector2d(-31.4,24.2))
                .turn(Math.toRadians(-127))
                .strafeToConstantHeading(new Vector2d(12.7,24.2))
                .turn(Math.toRadians(92))

                .strafeToConstantHeading(new Vector2d(11.7,54.6))
                .strafeToConstantHeading(new Vector2d(12.7,24.2))
                .turn(Math.toRadians(92))
                .strafeToConstantHeading(new Vector2d(-31.6,24.4))
                .turn(Math.toRadians(-54))
                .strafeToConstantHeading(new Vector2d(-51.2,48.9))

                .strafeToConstantHeading(new Vector2d(-31.4,24.2))
                .turn(Math.toRadians(-127))
                .strafeToConstantHeading(new Vector2d(-12.3,24.2))
                .turn(Math.toRadians(92))



                // --- End of Launch Sequence ---

                .build();

        while(!opModeIsActive() && !isStopRequested()){
            dl.imu.resetYaw();
            sleep(50);
        }

        waitForStart();

        if (opModeIsActive()){
            Actions.runBlocking(autonomous);
        }
    }

}
