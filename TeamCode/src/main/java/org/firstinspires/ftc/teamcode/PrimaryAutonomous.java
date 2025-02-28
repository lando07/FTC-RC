package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.AccelConstraint;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ProfileAccelConstraint;
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
    public static double testYValue = 55;

    public static double testXvalue = 31;

    @Override
    public void runOpMode() {
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
            //Extend out backstop for specimen grabbing
            //Even though secondarySlider is not extended, the claw is closed so clearance is ok
            slider.doHighSpecimenLowBasket();
            //Clip first specimen

            Actions.runBlocking(drive.actionBuilder(startingPose)
                    .strafeToConstantHeading(new Vector2d(0, 32)).build());
            slider.clipSpecimenAuto();
            secondarySlider.setPower(1);
            sleep(150);
            secondarySlider.setPower(0);
            sleep(200);
            //clip first specimen
            primaryClaw.openClaw();
            slider.resetHeightAuto();
            Actions.runBlocking(drive.actionBuilder(new Pose2d(0, 32, Math.toRadians(-90)))
                    .strafeToLinearHeading(new Vector2d(-36, 33), Math.toRadians(90))
                    //get behind field samples
                    .strafeToConstantHeading(new Vector2d(-36, 10))
                    .strafeToConstantHeading(new Vector2d(-50, 10))
                    //Push first sample
                    .strafeToLinearHeading(new Vector2d(-50, 54), Math.toRadians(90))
                    //Get behind second sample
                    .strafeToConstantHeading(new Vector2d(-50, 10))
                    .strafeToConstantHeading(new Vector2d(-62, 10))
                    //push second sample
                    //and grab second specimen
                    .strafeToConstantHeading(new Vector2d(-62, testYValue))
                    .waitSeconds(0.3)
                    .build());
            primaryClaw.closeClaw();
            sleep(200);
            slider.doHighSpecimenLowBasket();
            Actions.runBlocking(drive.actionBuilder(new Pose2d(-65, testYValue, Math.toRadians(90)))
                    .strafeToLinearHeading(new Vector2d(-3, testXvalue), Math.toRadians(270 + 1e-9))                    //clip second specimen
                    .build());
            slider.clipSpecimenAuto();
            sleep(350);
            primaryClaw.openClaw();
            slider.resetHeightAuto();
            Actions.runBlocking(drive.actionBuilder(new Pose2d(-3, testXvalue, Math.toRadians(270 + 1e-9)))
                    //grab third specimen
                    .strafeToLinearHeading(new Vector2d(-44, testYValue), Math.toRadians(90))
                    .build());
            primaryClaw.closeClaw();
            sleep(250);
            slider.doHighSpecimenLowBasket();
            Actions.runBlocking(drive.actionBuilder(new Pose2d(-44, testYValue, Math.toRadians(90)))
                    //clip third specimen
                    .strafeToLinearHeading(new Vector2d(0, 32), Math.toRadians(270))
                    .build());
            slider.clipSpecimenAuto();
            sleep(350);
            primaryClaw.openClaw();
            slider.resetHeightAuto();
            Actions.runBlocking(drive.actionBuilder(new Pose2d(0, 32, Math.toRadians(270)))
                    //grab fourth specimen
                    .strafeToConstantHeading(new Vector2d(-40, 60))
                    .build());
            primaryClaw.closeClaw();
        }
    }
}
