package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.subsystems.*;

@Config
@Autonomous(name = "PrimaryAuto", group = "autonomous")
public class PrimaryAutonomous extends LinearOpMode {
    public static int humanDelayTime = 100;
    public static int clawReleaseDelay = 300;
    public static double testYValue = 12;

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

            //Extend out backstop for specimen grabbing
            backStop.setPosition(0);
            //Even though secondarySlider is not extended, the claw is closed so clearance is ok
            slider.doHighSpecimenLowBasket();
            //Clip first specimen

            Actions.runBlocking(drive.actionBuilder(startingPose)
                    .strafeTo(new Vector2d(-6, 32))
                    .build());
            slider.clipSpecimenAuto();
            secondarySlider.setPower(1);
            sleep(250);
            secondarySlider.setPower(0);
            //clip first specimen
            primaryClaw.openClaw();
            slider.resetHeightAuto();
            Actions.runBlocking(drive.actionBuilder(new Pose2d(-6, 32, Math.toRadians(-90)))
                    .strafeToLinearHeading(new Vector2d(-36, 36), Math.toRadians(90))
                    //get behind field samples
                    .strafeTo(new Vector2d(-36, 10))
                    .setTangent(Math.toRadians(180))
                    //Push first sample
                    .splineToConstantHeading(new Vector2d(-50, 52), Math.toRadians(90))
                    .splineToConstantHeading(new Vector2d(-50, 15), Math.toRadians(270))
                    //push second sample
                    .splineToConstantHeading(new Vector2d(-60, 52), Math.toRadians(90))
                    //grab second specimen
                    .splineToConstantHeading(new Vector2d(-60, 60), Math.toRadians(270))
                    .build());
            primaryClaw.closeClaw();
            slider.doHighSpecimenLowBasket();
            Actions.runBlocking(drive.actionBuilder(new Pose2d(-60, 60, Math.toRadians(270)))
                    .setTangent(Math.toRadians(0))
                    .splineToLinearHeading(new Pose2d(-3, 40, Math.toRadians(270)), Math.toRadians(0))
                    //clip second specimen
                    .setTangent(Math.toRadians(270))
                    .splineToConstantHeading(new Vector2d(-3, 32), Math.toRadians(270))
                    .build());
            slider.clipSpecimenAuto();
            sleep(250);
            primaryClaw.openClaw();
            slider.resetHeightAuto();
            Actions.runBlocking(drive.actionBuilder(new Pose2d(-3, 32, Math.toRadians(270)))
                    //grab third specimen
                    .setTangent(Math.toRadians(90))
                    .splineToLinearHeading(new Pose2d(-40, 60, Math.toRadians(90)), Math.toRadians(90))
                    .build());
            primaryClaw.closeClaw();
            slider.doHighSpecimenLowBasket();
            Actions.runBlocking(drive.actionBuilder(new Pose2d(-40, 60, Math.toRadians(90)))
                    //clip third specimen
                    .setTangent(Math.toRadians(270))
                    .splineToLinearHeading(new Pose2d(0, 32, Math.toRadians(270)), Math.toRadians(270))
                    .build());
            slider.clipSpecimenAuto();
            sleep(250);
            primaryClaw.openClaw();
            slider.resetHeightAuto();
            Actions.runBlocking(drive.actionBuilder(new Pose2d(0, 32, Math.toRadians(270)))
                    //grab fourth specimen
                    .setTangent(Math.toRadians(90))
                    .splineToLinearHeading(new Pose2d(-40, 60, Math.toRadians(90)), Math.toRadians(90))
                    .build());
            primaryClaw.closeClaw();
            slider.doHighSpecimenLowBasket();
            Actions.runBlocking(drive.actionBuilder(new Pose2d(-40, 60, Math.toRadians(90)))
                    .setTangent(Math.toRadians(270))
                    .splineToLinearHeading(new Pose2d(3, 32, Math.toRadians(270)), Math.toRadians(270))
                    .build());
        }
    }
}
