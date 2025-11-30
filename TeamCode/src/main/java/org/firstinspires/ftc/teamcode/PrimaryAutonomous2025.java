package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Deprecated
@Config
@Disabled
@Autonomous(name = "PrimaryAuto2025", group = "autonomous")
public class PrimaryAutonomous2025 extends LinearOpMode {
    public static double testYValue = 64;
    public static double testYValue2 = 31;
    public static double testYValue3 = 61.5;
    public static double testYValue4 = 19.5;
    public static double testXValue = -41;
    public static int clipDelay = 200;
    public static int extendLength = 515;
    public static double neutralPitch = 0.15;
    public static double neutralYaw = 0.9;
    public static int grabDelay = 75;
    public static int pickUpDelay = 200;
    public static int dropOffDelay = 100;
    public static double extendDelay = 1;

    @Override
    public void runOpMode() {
        Pose2d startingPose = new Pose2d(0, 0, Math.toRadians(-90));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startingPose);
        MecanumDrive.DriveLocalizer dl = (MecanumDrive.DriveLocalizer) drive.localizer;

        Action autonomous = drive.actionBuilder(startingPose)
                .strafeToConstantHeading(new Vector2d(0, 10))
                .build();


        while (!opModeIsActive() && !isStopRequested()) {
            dl.imu.resetYaw();
            sleep(50);
        }
        waitForStart();
        if (opModeIsActive()) {
            Actions.runBlocking(autonomous);
        }
    }
}