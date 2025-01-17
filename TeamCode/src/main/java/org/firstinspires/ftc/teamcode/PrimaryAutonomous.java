package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.subsystems.*;
import org.opencv.core.Mat;

@Config
@Autonomous(name = "PrimaryAuto", group = "autonomous")
public class PrimaryAutonomous extends LinearOpMode {
    private final int humanDelayTime = 400;
    private final int clawReleaseDelay = 100;

    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d startingPose = new Pose2d(-17.2, 62, Math.toRadians(279));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startingPose);
        RaiseArmSlider slider = new RaiseArmSlider(this, "raiseArmSlider");
        Claw primaryClaw = new Claw(this, "primaryClaw");

        primaryClaw.closeClaw();
        while (!opModeIsActive() && !isStopRequested()) {
        }
        waitForStart();
        if (opModeIsActive()) {
            slider.doHighSpecimenLowBasket();
            //Clip first specimen
            Actions.runBlocking(drive.actionBuilder(startingPose)
                    .splineToConstantHeading(new Vector2d(0, 33), Math.toRadians(270))
                    .build());
            slider.clipSpecimen();
            sleep(clawReleaseDelay);
            //Release specimen
            primaryClaw.openClaw();
            sleep(2000);//TODO: Remove delay once tuned
            Actions.runBlocking(drive.actionBuilder(new Pose2d(0, 33, Math.toRadians(270)))
                    .strafeTo(new Vector2d(0, 40))
                    .build());
            //Lower slider and push first field sample to observation zone
            slider.resetHeight();
            Actions.runBlocking(drive.actionBuilder(new Pose2d(0, 40, Math.toRadians(270)))
                    .strafeTo(new Vector2d(-35, 34))
                    .strafeTo(new Vector2d(-35, 13))
                    .strafeTo(new Vector2d(-48, 13))
                    //Push second into observation zone
                    .strafeTo(new Vector2d(-48, 52))
                    .build());
            //Wait 300ms for human to grab sample, then rotate to show primary claw to human
            sleep(humanDelayTime);
            Actions.runBlocking(drive.actionBuilder(new Pose2d(-48, 52, Math.toRadians(270)))
                    .turnTo(Math.toRadians(90))
                    .build());
            primaryClaw.openClaw();
            sleep(humanDelayTime);
            primaryClaw.closeClaw();
            //Clip 2nd specimen
            slider.doHighSpecimenLowBasket();
            Actions.runBlocking(drive.actionBuilder(new Pose2d(-48,52,Math.toRadians(90)))
                    .strafeToLinearHeading(new Vector2d(-3, 33), Math.toRadians(270))
                    .build());
            slider.clipSpecimen();
            sleep(clawReleaseDelay);
            primaryClaw.openClaw();
            //Grab third specimen
            Actions.runBlocking(drive.actionBuilder(new Pose2d(-3,33,Math.toRadians(270)))
                    .strafeTo(new Vector2d(-3, 40))
                    .build());
            slider.resetHeight();
            Actions.runBlocking(drive.actionBuilder(new Pose2d(-3,40, Math.toRadians(270)))
                    .strafeToLinearHeading(new Vector2d(-48, 52), Math.toRadians(90))
                    .build());
            sleep(humanDelayTime);//Note: claw has already been opened on line 68
            primaryClaw.closeClaw();
            slider.doHighSpecimenLowBasket();
            //Clip third specimen
            Actions.runBlocking(drive.actionBuilder(new Pose2d(-48,52,Math.toRadians(90)))
                    .strafeToLinearHeading(new Vector2d(3, 33), Math.toRadians(270))
                    .build());
            slider.clipSpecimen();
            sleep(clawReleaseDelay);
            primaryClaw.openClaw();
            //Now RTB
            Actions.runBlocking(drive.actionBuilder(new Pose2d(3,33,Math.toRadians(270)))
                    .strafeTo(new Vector2d(3, 40))
                    .build());
            slider.resetHeight();
            Actions.runBlocking(drive.actionBuilder(new Pose2d(-48,58,Math.toRadians(270)))
                    .strafeTo(new Vector2d(-46,58))
                    .build());
        }
    }
}
