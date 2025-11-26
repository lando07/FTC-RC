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
@Autonomous(name = "RedLargeAutoDecode", group="autonomous")
public class RedLargeAutoDecode extends LinearOpMode {
    @Override
    public void runOpMode(){
        Pose2d startingPose = new Pose2d(-52.0,46.1,Math.toRadians(-232));
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
                .strafeToConstantHeading(new Vector2d(-32.2,23.2))
                .turn(Math.toRadians(-127))
                .strafeToConstantHeading(new Vector2d(-12.3,23.0))
                .turn(Math.toRadians(92))

                .strafeToConstantHeading(new Vector2d(-11.9,51.4))
                .strafeToConstantHeading(new Vector2d(-12.3,23.0))
                .turn(Math.toRadians(91))
                .strafeToConstantHeading(new Vector2d(-32.2,23.2))
                .turn(Math.toRadians(-56))
                .strafeToConstantHeading(new Vector2d(-53.1,46.1))

                .strafeToConstantHeading(new Vector2d(-32.2,23.2))
                .turn(Math.toRadians(-127))
                .strafeToConstantHeading(new Vector2d(11.3,23.4))
                .turn(Math.toRadians(92))

                .strafeToConstantHeading(new Vector2d(11.9,50.0))
                .strafeToConstantHeading(new Vector2d(11.3,23.4))
                .turn(Math.toRadians(92))
                .strafeToConstantHeading(new Vector2d(-32.2,23.2))
                .turn(Math.toRadians(-56))
                .strafeToConstantHeading(new Vector2d(-53.1,46.1))
                .strafeToConstantHeading(new Vector2d(-32.2,23.2))
                .turn(Math.toRadians(-127))
                .strafeToConstantHeading(new Vector2d(35.3,23.8))
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
