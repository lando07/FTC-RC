package org.firstinspires.ftc.teamcode.Autonomous.BIOBUZZ;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.RoadRunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.subsystems.DECODEExclusive.FeedServoLauncher;

@Config
@Autonomous(name = "Blue West", group = "autonomous")
public class BlueWestAuto extends LinearOpMode {
    private DcMotor frontLeft, frontRight, backLeft, backRight;
    public void runOpMode() {
        Pose2d startingPose = new Pose2d(0,0,0);
       MecanumDrive drive = new MecanumDrive(hardwareMap, startingPose);
//        // --- End of Initialization ---


       Action autonomous = drive.actionBuilder(startingPose)
            .strafeToConstantHeading(new Vector2d(-58,23.4))
              .strafeToConstantHeading(new Vector2d(38,57))
                .strafeToLinearHeading(new Vector2d(0, 1), Math.PI / 2)
               .waitSeconds(1.0)
                .build();

       waitForStart();

       if (opModeIsActive()) {
            Actions.runBlocking(autonomous);
       }

//        frontLeft = hardwareMap.get(DcMotor.class, "front_left");
//        frontRight = hardwareMap.get(DcMotor.class, "front_right");
//        backLeft = hardwareMap.get(DcMotor.class, "back_left");
//        backRight = hardwareMap.get(DcMotor.class, "back_right");
//
//        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//
//        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
//        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
//
//        waitForStart();
//
//        frontLeft.setPower(-0.5);
//        frontRight.setPower(-0.5);
//        backLeft.setPower(-0.5);
//        backRight.setPower(-0.5);
//
//        sleep(500);
//
//        frontLeft.setPower(0);
//        frontRight.setPower(0);
//        backLeft.setPower(0);
//        backRight.setPower(0);
//
//        frontLeft.setPower(1);
//        frontRight.setPower(1);
//        backLeft.setPower(1);
//        backRight.setPower(1);
//
//        sleep(100);
//
//        frontLeft.setPower(0);
//        frontRight.setPower(0);
//        backLeft.setPower(0);
//        backRight.setPower(0);
    }
}
