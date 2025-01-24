package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.subsystems.*;
import org.opencv.core.Mat;

@Config
@Autonomous(name = "PrimaryAuto", group = "autonomous")
public class PrimaryAutonomous extends LinearOpMode {
    public static int humanDelayTime = 400;
    public static int clawReleaseDelay = 500;
    public static int testYvalue = 56;

    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d startingPose = new Pose2d(-17.2, 62, Math.toRadians(270));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startingPose);
        RaiseArmSlider slider = new RaiseArmSlider(this, "raiseArmSlider");
        Claw primaryClaw = new Claw(this, "primaryClaw");
        DcMotor secondarySlider = hardwareMap.get(DcMotor.class, "armSlider");
        secondarySlider.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        primaryClaw.closeClaw();
        while (!opModeIsActive() && !isStopRequested()) {
        }
        waitForStart();
        if (opModeIsActive()) {
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
            sleep(clawReleaseDelay);
            //Release specimen
            primaryClaw.openClaw();
            sleep(clawReleaseDelay);
            Actions.runBlocking(drive.actionBuilder(new Pose2d(0, 32, Math.toRadians(270)))
                    .strafeTo(new Vector2d(0, 40))
                    .build());
            //Lower slider and take first field sample to observation zone
            slider.resetHeight();
            Actions.runBlocking(drive.actionBuilder(new Pose2d(0, 40, Math.toRadians(270)))
                    .strafeTo(new Vector2d(-52, 40))
                    .build());
            primaryClaw.openClaw();
            Actions.runBlocking(drive.actionBuilder(new Pose2d(-52, 40, Math.toRadians(270)))
                    .strafeTo(new Vector2d(-52, 33))
                    .build());
            sleep(clawReleaseDelay);
            primaryClaw.closeClaw();
            //Turn around and take sample to observation zone
            Actions.runBlocking(drive.actionBuilder(new Pose2d(-52, 33, Math.toRadians(270)))
                    .strafeToLinearHeading(new Vector2d(-52, 40), Math.toRadians(90))
                    .strafeTo(new Vector2d(-52, 52))
                    .build());
            //Release sample, back out of observation zone, wait for human, then grab new specimen
            primaryClaw.openClaw();
            Actions.runBlocking(drive.actionBuilder(new Pose2d(-52, 52, Math.toRadians(90)))
                    .strafeTo(new Vector2d(-52, 40))
                    .waitSeconds(humanDelayTime / 1000.0)
                    .strafeTo(new Vector2d(-52, 55))
                    .build());
            //Take specimen to submersible
            primaryClaw.closeClaw();
            slider.doHighSpecimenLowBasket();
            Actions.runBlocking(drive.actionBuilder(new Pose2d(-52, 55, Math.toRadians(90)))
                    .strafeToLinearHeading(new Vector2d(-3, 32), Math.toRadians(270))
                    .build());
            slider.clipSpecimen();
            sleep(clawReleaseDelay);
            primaryClaw.openClaw();
            sleep(clawReleaseDelay);
            Actions.runBlocking(drive.actionBuilder(new Pose2d(-3, 32, Math.toRadians(270)))
                    .strafeTo(new Vector2d(-3, 40))
                    .build());
            slider.resetHeight();
            
        }
    }
}
