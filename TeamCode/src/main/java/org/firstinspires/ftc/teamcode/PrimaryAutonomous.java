package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.RaiseArmSlider;

@Config
@Autonomous(name = "PrimaryAuto", group = "autonomous")
public class PrimaryAutonomous extends LinearOpMode {
    public static double testYValue = 21;
    public static double testYValue2 = 35;
    public static double testYValue3 = 57;
    public static double testYValue4 = 45;
    public static double thirdSpecimenOffset = 3.5;
    public static double fourthSpecimenOffset = 3.5;
    public static double clipOffset = 3.5;
    public static double testXValue = -45;
    public static int clipDelay = 275;
    public static int extendLength = 515;
    public static double neutralPitch = 0.1;
    public static double neutralYaw = 1;
    public static int grabDelay = 200;


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
        armExtender.setPower(0);
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
                .strafeToConstantHeading(new Vector2d(6, testYValue2 + 4))
                .strafeToConstantHeading(new Vector2d(6, testYValue2))

                .stopAndAdd(new InstantAction(() -> armExtender.setPower(1)))
                .stopAndAdd(new InstantAction(() -> armExtender.setTargetPosition(extendLength)))
                .stopAndAdd(secondaryClaw.openClawAction())
                .stopAndAdd(slider.clipSpecimenAutoAction())
                .waitSeconds(clipDelay / 1000.0)
                .stopAndAdd(primaryClaw.openClawAction())
                .stopAndAdd(slider.resetHeightAutoAction())
                //go to pick up first field sample
                .strafeToLinearHeading(new Vector2d(-39, 36), Math.toRadians(0))
                .strafeTo(new Vector2d(-39, testYValue))
                .stopAndAdd(secondaryClaw.closeClawAction())
                //Drop Off first field sample
                .waitSeconds(.250)
                .stopAndAdd(new InstantAction(() -> pitch.setPosition(0.65)))
                .strafeToLinearHeading(new Vector2d(-41, 45), Math.toRadians(310))
                .stopAndAdd(secondaryClaw.openClawAction())
                //Reset Secondary Claw pitch and pick up second sample
                .stopAndAdd(new InstantAction(() -> pitch.setPosition(neutralPitch)))
                .strafeToLinearHeading(new Vector2d(-49.5, testYValue), Math.toRadians(0))
                .stopAndAdd(secondaryClaw.closeClawAction())
                //Drop Off second field sample
                .waitSeconds(.300)
                .stopAndAdd(new InstantAction(() -> pitch.setPosition(0.65)))
                .strafeToLinearHeading(new Vector2d(-49.5, 45), Math.toRadians(310))
                .stopAndAdd(secondaryClaw.openClawAction())
                //Reset Secondary Claw pitch and pick up third sample
                .stopAndAdd(new InstantAction(() -> pitch.setPosition(neutralPitch)))
                .strafeToLinearHeading(new Vector2d(-59, testYValue), Math.toRadians(0))
                .stopAndAdd(secondaryClaw.closeClawAction())
                //Drop Off third field sample
                .waitSeconds(0.3)
                .stopAndAdd(new InstantAction(() -> pitch.setPosition(0.65)))
//                .stopAndAdd(new InstantAction(() -> backStop.setPosition(0.5)))
                .setTangent(Math.toRadians(90))
                .splineToLinearHeading(new Pose2d(new Vector2d(-50, testYValue3 - 4), Math.toRadians(310)), Math.toRadians(90))
                .stopAndAdd(secondaryClaw.openClawAction())
                .waitSeconds(0.1)
                //Fold claw, and retract arm
                .stopAndAdd(new InstantAction(() -> yaw.setPosition(.5)))
                .stopAndAdd(new InstantAction(() -> armExtender.setTargetPosition(100)))
                //Pick up second specimen
                .turnTo(Math.toRadians(90))
                .strafeTo(new Vector2d(-50, testYValue3))
                .stopAndAdd(new InstantAction(() -> armExtender.setPower(0)))
                .stopAndAdd(primaryClaw.closeClawAction())
                .waitSeconds(grabDelay / 1000.0)
                //Clip second specimen
                .stopAndAdd(slider.doHighSpecimenLowBasketAction())
                .strafeToLinearHeading(new Vector2d(3, testYValue2 + 4), Math.toRadians(273))
                .strafeTo(new Vector2d(3, testYValue2))
                .stopAndAdd(slider.clipSpecimenAutoAction())
                .waitSeconds(clipDelay / 1000.0)
                .stopAndAdd(primaryClaw.openClawAction())
//                .stopAndAdd(new InstantAction(() -> backStop.setPosition(0.5)))
                //Pick up third specimen
                .stopAndAdd(slider.resetHeightAutoAction())
                .strafeToLinearHeading(new Vector2d(-43, testYValue3), Math.toRadians(90))
                .stopAndAdd(primaryClaw.closeClawAction())
                .waitSeconds(grabDelay / 1000.0)
                //Clip third specimen
                .stopAndAdd(slider.doHighSpecimenLowBasketAction())
                .strafeToLinearHeading(new Vector2d(-3, testYValue2 + 4), Math.toRadians(273))
                .strafeTo(new Vector2d(-3, testYValue2))
                .stopAndAdd(slider.clipSpecimenAutoAction())
                .waitSeconds(clipDelay / 1000.0)
                .stopAndAdd(primaryClaw.openClawAction())
                .stopAndAdd(slider.resetHeightAutoAction())
                //Pick up fourth specimen
                .strafeToLinearHeading(new Vector2d(-43, testYValue3 + 1), Math.toRadians(90))
                .stopAndAdd(primaryClaw.closeClawAction())
                .waitSeconds(grabDelay / 1000.0)
                //Clip fourth specimen
                .stopAndAdd(slider.doHighSpecimenLowBasketAction())
                .strafeToLinearHeading(new Vector2d(-6, testYValue2 + 4), Math.toRadians(273))
                .strafeTo(new Vector2d(-6, testYValue2))
                .stopAndAdd(slider.clipSpecimenAutoAction())
                .waitSeconds(clipDelay / 1000.0)
                .stopAndAdd(primaryClaw.openClawAction())
                .stopAndAdd(slider.resetHeightAutoAction())
                .strafeToLinearHeading(new Vector2d(-6,testYValue2 + 1), Math.toRadians(270))
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