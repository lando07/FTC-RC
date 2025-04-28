package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
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
    public static double testYValue = 58;
    public static double testYValue2 = 35.2;
    public static double testYValue3 = 36;
    public static double testXValue = -45;
    public static double testXValue2 = -50.5;
    public static int testHeadingDeg = 310;
    public static int clipDelay = 350;
    public static int extendLength = 500;
    public static double neutralPitch = 0.1;
    public static double neutralYaw = 1;

    /**
     * @noinspection StatementWithEmptyBody
     */
    @Override
    public void runOpMode() {
        Pose2d startingPose = new Pose2d(-17.2, 60, Math.toRadians(-90));
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
        while (!opModeIsActive() && !isStopRequested()) {
        }
        waitForStart();
        if (opModeIsActive()) {
            backStop.setPosition(0);
            pitch.setPosition(neutralPitch);
            yaw.setPosition(neutralYaw);
            //Extend out backstop for specimen grabbing
            slider.doHighSpecimenLowBasket();
            //Clip first specimen
            telemetry.addLine("Clipping first specimen");
            Actions.runBlocking(drive.actionBuilder(startingPose)
                    .strafeToConstantHeading(new Vector2d(6, 36))
                    .strafeToConstantHeading(new Vector2d(6, 32.5))
                    .build());
            armExtender.setPower(1);
            armExtender.setTargetPosition(extendLength);
            secondaryClaw.openClaw();
            slider.clipSpecimenAuto();
            sleep(clipDelay);
            primaryClaw.openClaw();
            slider.resetHeightAuto();
            //go to pick up first field sample
            telemetry.addLine("Going to pick up first field sample");
            Actions.runBlocking(drive.actionBuilder(new Pose2d(6, 32.5, Math.toRadians(-90)))
                    .strafeToLinearHeading(new Vector2d(-39, 34), Math.toRadians(0))
                    .strafeTo(new Vector2d(-39, 21))
                    .build());
            secondaryClaw.closeClaw();
            //Drop Off first field sample
            telemetry.addLine("Dropping off first field sample");
            sleep(250);
            pitch.setPosition(0.65);
            Actions.runBlocking(drive.actionBuilder(new Pose2d(-39, 21, Math.toRadians(0)))
                    .strafeToLinearHeading(new Vector2d(-41, 50), Math.toRadians(310))
                    .build());
            secondaryClaw.openClaw();
            //Reset Secondary Claw pitch and pick up second sample
            telemetry.addLine("Picking up second sample");
            pitch.setPosition(neutralPitch);
            Actions.runBlocking(drive.actionBuilder(new Pose2d(new Vector2d(-41, 50), Math.toRadians(310)))
                    .strafeToLinearHeading(new Vector2d(-50.5, 21), Math.toRadians(0))
                    .build());
            secondaryClaw.closeClaw();
            //Drop Off second field sample
            telemetry.addLine("Dropping off second field sample");
            sleep(300);
            pitch.setPosition(0.65);
            Actions.runBlocking(drive.actionBuilder(new Pose2d(new Vector2d(-50.5, 21), Math.toRadians(0)))
                    .strafeToLinearHeading(new Vector2d(-50.5, 50), Math.toRadians(310))
                    .build());
            secondaryClaw.openClaw();
            //Reset Secondary Claw pitch and pick up third sample
            telemetry.addLine("Picking up third sample");
            pitch.setPosition(neutralPitch);
            Actions.runBlocking(drive.actionBuilder(new Pose2d(new Vector2d(-50.5, 50), Math.toRadians(310)))
                    .strafeToLinearHeading(new Vector2d(-59, 21), Math.toRadians(0))
                    .build());
            secondaryClaw.closeClaw();
            //Drop Off third field sample
            telemetry.addLine("Dropping off third field sample");
            sleep(300);
            pitch.setPosition(0.65);
            Actions.runBlocking(drive.actionBuilder(new Pose2d(new Vector2d(-59, 21), Math.toRadians(0)))
                    .setTangent(Math.toRadians(90))
                    .splineToLinearHeading(new Pose2d(new Vector2d(-50, 50), Math.toRadians(310)), Math.toRadians(90))
                    .build());
            secondaryClaw.openClaw();
            //Fold claw, and retract arm
            yaw.setPosition(.5);
            armExtender.setTargetPosition(100);
            //Pick up first specimen
            telemetry.addLine("Picking up first specimen");
            Actions.runBlocking(drive.actionBuilder(new Pose2d(new Vector2d(-50, 50), Math.toRadians(310)))
                    .turnTo(Math.toRadians(90))
                    .strafeTo(new Vector2d(-50, testYValue))
                    .build());
            armExtender.setPower(0);
            primaryClaw.closeClaw();
            sleep(clipDelay);
            //Clip first specimen
            telemetry.addLine("Clipping first specimen");
            slider.doHighSpecimenLowBasket();
            Actions.runBlocking(drive.actionBuilder(new Pose2d(new Vector2d(-50, testYValue), Math.toRadians(90)))
                    .setTangent(Math.toRadians(315))
                    .splineToLinearHeading(new Pose2d(new Vector2d(3, testYValue2 + 2), Math.toRadians(270)), Math.toRadians(315))
                    .strafeToLinearHeading(new Vector2d(3, testYValue2), Math.toRadians(270))
                    .build());
            slider.clipSpecimenAuto();
            sleep(clipDelay + 100);
            primaryClaw.openClaw();
            //Pick up second specimen
            telemetry.addLine("Picking up second specimen");
            slider.resetHeightAuto();
            Actions.runBlocking(drive.actionBuilder(new Pose2d(new Vector2d(3, testYValue2), Math.toRadians(270)))
                    .setTangent(Math.toRadians(135))
                    .splineToLinearHeading(new Pose2d(new Vector2d(testXValue, testYValue), Math.toRadians(90)), Math.toRadians(135))
                    .build());
            primaryClaw.closeClaw();
            sleep(clipDelay);
            //Clip second specimen
            telemetry.addLine("Clipping second specimen");
            slider.doHighSpecimenLowBasket();
            Actions.runBlocking(drive.actionBuilder(new Pose2d(new Vector2d(testXValue, testYValue), Math.toRadians(90)))
                    .setTangent(Math.toRadians(315))
                    .splineToLinearHeading(new Pose2d(new Vector2d(-3, testYValue2 + 2), Math.toRadians(270)), Math.toRadians(315))
                    .build());
            slider.clipSpecimenAuto();
            sleep(clipDelay + 100);
            primaryClaw.openClaw();
            slider.resetHeightAuto();
            //Pick up third specimen
            telemetry.addLine("Picking up third specimen");
            Actions.runBlocking(drive.actionBuilder(new Pose2d(new Vector2d(3, testYValue2), Math.toRadians(270)))
                    .setTangent(Math.toRadians(135))
                    .splineToLinearHeading(new Pose2d(new Vector2d(testXValue, testYValue), Math.toRadians(135)), Math.toRadians(90))
                    .build());
            primaryClaw.closeClaw();
            sleep(clipDelay);
            //Clip third specimen
            telemetry.addLine("Clipping third specimen");
            slider.doHighSpecimenLowBasket();
            Actions.runBlocking(drive.actionBuilder(new Pose2d(new Vector2d(testXValue, testYValue), Math.toRadians(90)))
                    .setTangent(Math.toRadians(315))
                    .splineToLinearHeading(new Pose2d(new Vector2d(-3, testYValue3), Math.toRadians(270)), Math.toRadians(315))
                    .build());
            slider.clipSpecimenAuto();
            sleep(clipDelay + 100);
            primaryClaw.openClaw();
            slider.resetHeightAuto();


        }
    }
}
