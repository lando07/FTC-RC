package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.subsystems.*;
import org.opencv.core.Mat;

@Config
@Autonomous(name = "PrimaryAuto", group = "autonomous")
public class PrimaryAutonomous extends LinearOpMode {
    public static int humanDelayTime = 100;
    public static int clawReleaseDelay = 300;
    public static double testYvalue = 12;

    public static double testXvalue = -69;

    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d startingPose = new Pose2d(-17.2, 62, Math.toRadians(-90));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startingPose);
        RaiseArmSlider slider = new RaiseArmSlider(this, "raiseArmSlider");
        Claw primaryClaw = new Claw(this, "primaryClaw");
        DcMotor secondarySlider = hardwareMap.get(DcMotor.class, "armSlider");
        Servo backStop = hardwareMap.get(Servo.class, "backStop");
        secondarySlider.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        primaryClaw.closeClaw();
        while (!opModeIsActive() && !isStopRequested()) {
        }
        waitForStart();
        if (opModeIsActive()) {
            backStop.setPosition(0);
            //Even though secondarySlider is not extended, the claw is closed so clearance is ok
            slider.doHighSpecimenLowBasket();
            //Clip first specimen
            Actions.runBlocking(drive.actionBuilder(startingPose)
                    .strafeTo(new Vector2d(0, 32))
                    .build());
            slider.clipSpecimen();
            secondarySlider.setPower(1);
            sleep(500);
            secondarySlider.setPower(0);
            //Release specimen
            primaryClaw.openClaw();
            Actions.runBlocking(drive.actionBuilder(new Pose2d(0, 32, Math.toRadians(-90)))
                    .strafeTo(new Vector2d(0, 36))
                    .build());
            //Lower slider and take first field sample to observation zone
            slider.resetHeight();
            Actions.runBlocking(drive.actionBuilder(new Pose2d(0, 36, Math.toRadians(-90)))
                    .strafeTo(new Vector2d(-42, 36))
                    .build());
//            //Turn around and push sample to observation zone
            Actions.runBlocking(drive.actionBuilder(new Pose2d(-42, 36, Math.toRadians(-90)))
                    .strafeTo(new Vector2d(-42, 5))
                    .turnTo(Math.toRadians(90))
                    .strafeTo(new Vector2d(-50, 5))
                    .strafeTo(new Vector2d(-50, 52))
                    .strafeTo(new Vector2d(-50, 12))
                    .strafeTo(new Vector2d(-60, 12))
                    .strafeTo(new Vector2d( -60, 52))
                    .strafeTo(new Vector2d(-60, 12))
                    .strafeTo(new Vector2d(-69, 12))
                    .strafeTo(new Vector2d(-69, 52))
                    .build());

//            //Take specimen to submersible
//            primaryClaw.closeClaw();
//            sleep(humanDelayTime);
//            slider.doHighSpecimenLowBasket();
//            Actions.runBlocking(drive.actionBuilder(new Pose2d(-52, 56.5, Math.toRadians(90)))
//                    .strafeTo(new Vector2d(-52, 40))
//                    .strafeToLinearHeading(new Vector2d(5, 30), Math.toRadians(270))
//                    .build());
//            slider.clipSpecimen();
//            sleep(clawReleaseDelay);
//            primaryClaw.openClaw();
//            Actions.runBlocking(drive.actionBuilder(new Pose2d(5, 30, Math.toRadians(270)))
//                    .strafeTo(new Vector2d(5, 35))
//                    .build());
//            slider.resetHeight();
//            Actions.runBlocking(drive.actionBuilder(new Pose2d(5, 35, Math.toRadians(270)))
//                    .strafeToLinearHeading(new Vector2d(-39, 59.5), Math.toRadians(180))
//                    .build());
//            primaryClaw.closeClaw();
//            sleep(clawReleaseDelay);
//            slider.doHighSpecimenLowBasket();
//            Actions.runBlocking(drive.actionBuilder(new Pose2d(-39, 59.5, Math.toRadians(180)))
//                    .strafeToLinearHeading(new Vector2d(-3, 31), Math.toRadians(270))
//                    .build());
//            slider.clipSpecimen();
//            sleep(clawReleaseDelay);
//            primaryClaw.openClaw();
//            secondarySlider.setPower(-1);
//            sleep(300);
//            secondarySlider.setPower(0);
//            Actions.runBlocking(drive.actionBuilder(new Pose2d(-3, 31, Math.toRadians(270)))
//                    .strafeTo(new Vector2d(-46, 60))
//                    .build());
        }
    }
}
