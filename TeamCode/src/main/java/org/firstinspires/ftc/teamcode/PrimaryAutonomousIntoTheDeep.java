package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.RaiseArmSlider;

@Disabled
@Config
@Autonomous(name = "PrimaryAutoIntoTheDeep", group = "autonomous")
public class PrimaryAutonomousIntoTheDeep extends LinearOpMode {
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
        Pose2d startingPose = new Pose2d(-17.2, 63, Math.toRadians(-90));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startingPose);
        RaiseArmSlider slider = new RaiseArmSlider(this, "raiseArmSlider");
        DcMotor armExtender = hardwareMap.get(DcMotor.class, "armSlider");
        armExtender.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armExtender.setTargetPosition(0);
        armExtender.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        armExtender.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        armExtender.setPower(1);
        Claw primaryClaw = new Claw(this, "primaryClaw");
        Claw secondaryClaw = new Claw(this, "secondaryClaw");
        Servo backStop = hardwareMap.get(Servo.class, "backStop");
        Servo pitch = hardwareMap.get(Servo.class, "pitch");
        Servo yaw = hardwareMap.get(Servo.class, "yaw");
        primaryClaw.closeClaw();
        MecanumDrive.DriveLocalizer dl = (MecanumDrive.DriveLocalizer) drive.localizer;

        Action autonomous = drive.actionBuilder(startingPose)
                .stopAndAdd(new InstantAction(() -> pitch.setPosition(neutralPitch)))
                .stopAndAdd(new InstantAction(() -> yaw.setPosition(neutralYaw)))
                .stopAndAdd(new InstantAction(() -> backStop.setPosition(0)))
                //Extend out backstop for specimen grabbing
                .stopAndAdd(slider.doHighSpecimenLowBasketAction())
                //Clip first specimen
                .strafeToConstantHeading(new Vector2d(-8, testYValue2))
                .stopAndAdd(slider.clipSpecimenAutoAction())
                .waitSeconds(clipDelay / 1000.0)
                .stopAndAdd(new InstantAction(() -> armExtender.setPower(1)))
                .stopAndAdd(new InstantAction(() -> armExtender.setTargetPosition(extendLength)))
                .stopAndAdd(secondaryClaw.openClawAction())
                .stopAndAdd(primaryClaw.openClawAction())
                .stopAndAdd(slider.resetHeightAutoAction())
                //go to pick up first field sample
                .strafeToLinearHeading(new Vector2d(-39, 36), Math.toRadians(0))
                .strafeTo(new Vector2d(-39, testYValue4))
                .stopAndAdd(secondaryClaw.closeClawAction())
                .waitSeconds(pickUpDelay/1000.0)
                //Drop Off first field sample
                .stopAndAdd(new InstantAction(() -> pitch.setPosition(0.65)))
                .strafeToLinearHeading(new Vector2d(-41, 46), Math.toRadians(310))
                .stopAndAdd(secondaryClaw.openClawAction())
                //Reset Secondary Claw pitch and pick up second sample
                .stopAndAdd(new InstantAction(() -> pitch.setPosition(neutralPitch)))
                .strafeToLinearHeading(new Vector2d(-49.5, testYValue4), Math.toRadians(0))
                .stopAndAdd(secondaryClaw.closeClawAction())
                .waitSeconds(pickUpDelay/1000.0)
                //Drop Off second field sample
                .stopAndAdd(new InstantAction(() -> pitch.setPosition(0.65)))
                .strafeToLinearHeading(new Vector2d(-49.5, 46), Math.toRadians(310))
                .stopAndAdd(secondaryClaw.openClawAction())
                //Reset Secondary Claw pitch and pick up third sample
                .stopAndAdd(new InstantAction(() -> pitch.setPosition(neutralPitch)))
                .strafeToLinearHeading(new Vector2d(-59, testYValue4), Math.toRadians(0))
                .stopAndAdd(secondaryClaw.closeClawAction())
                .waitSeconds(pickUpDelay/1000.0)
                //Drop Off third field sample
                .stopAndAdd(new InstantAction(() -> pitch.setPosition(0.65)))
//                .stopAndAdd(new InstantAction(() -> backStop.setPosition(0.5)))
                .setTangent(Math.toRadians(90))
                .splineToLinearHeading(new Pose2d(new Vector2d(-50, testYValue3 - 5), Math.toRadians(310)), Math.toRadians(90))
                .stopAndAdd(secondaryClaw.openClawAction())
                .waitSeconds(dropOffDelay/1000.0)
                //Fold claw, and retract arm
                .stopAndAdd(new InstantAction(() -> yaw.setPosition(.5)))
                .stopAndAdd(new InstantAction(() -> armExtender.setTargetPosition(100)))
                //Pick up second specimen
                .turnTo(Math.toRadians(88))
                .strafeTo(new Vector2d(-50, testYValue3))
                .stopAndAdd(new InstantAction(() -> armExtender.setPower(0)))
                .stopAndAdd(primaryClaw.closeClawAction())
                .waitSeconds(grabDelay / 1000.0)
                //Clip second specimen
                .stopAndAdd(slider.doHighSpecimenLowBasketAction())
                .strafeToSplineHeading(new Vector2d(0, testYValue2), Math.toRadians(273))
                .stopAndAdd(slider.clipSpecimenAutoAction())
                .waitSeconds(clipDelay / 1000.0)
                .stopAndAdd(primaryClaw.openClawAction())
                .stopAndAdd(slider.resetHeightAutoAction())
                //Pick up third specimen
                .strafeToLinearHeading(new Vector2d(testXValue, testYValue), Math.toRadians(90))
                .stopAndAdd(primaryClaw.closeClawAction())
                .waitSeconds(grabDelay / 1000.0)
                //Clip third specimen
                .stopAndAdd(slider.doHighSpecimenLowBasketAction())
                .strafeToLinearHeading(new Vector2d(-2, testYValue2), Math.toRadians(273))
                .stopAndAdd(slider.clipSpecimenAutoAction())
                .waitSeconds(clipDelay / 1000.0)
                .stopAndAdd(primaryClaw.openClawAction())
                .stopAndAdd(slider.resetHeightAutoAction())
                //Pick up fourth specimen
                .strafeToLinearHeading(new Vector2d(testXValue, testYValue), Math.toRadians(90))
                .stopAndAdd(primaryClaw.closeClawAction())
                .waitSeconds(grabDelay / 1000.0)
                //Clip fourth specimen
                .stopAndAdd(slider.doHighSpecimenLowBasketAction())
                .strafeToLinearHeading(new Vector2d(-4, testYValue2), Math.toRadians(273))
                .stopAndAdd(slider.clipSpecimenAutoAction())
                .waitSeconds(clipDelay / 1000.0)
                .stopAndAdd(primaryClaw.openClawAction())
                .stopAndAdd(slider.resetHeightAutoAction())
                //Grab fifth specimen
                .strafeToLinearHeading(new Vector2d(testXValue, testYValue), Math.toRadians(90))
                .stopAndAdd(primaryClaw.closeClawAction())
                .waitSeconds(grabDelay / 1000.0)
                //Clip fifth specimen
                .stopAndAdd(slider.doHighSpecimenLowBasketAction())
                .strafeToLinearHeading(new Vector2d(-6, testYValue2), Math.toRadians(273))
                .stopAndAdd(slider.clipSpecimenAutoAction())
                .waitSeconds(clipDelay / 1000.0)
                .stopAndAdd(primaryClaw.openClawAction())
                .stopAndAdd(slider.resetHeightAutoAction())
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