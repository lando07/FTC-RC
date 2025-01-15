package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.subsystems.*;

@Config
@Autonomous(name = "PrimaryAuto", group = "autonomous")
public class PrimaryAutonomous extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d startingPose = new Pose2d(-17.2, 62, Math.toRadians(279));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startingPose);
        RaiseArmSlider slider = new RaiseArmSlider(this, "raiseArmSlider");
        Claw primaryClaw = new Claw(this, "primaryClaw");
//        hardwareMap.get(DcMotor.class, "armSlider").setPower(0.5);
//        sleep(1200);
//        hardwareMap.get(DcMotor.class, "armSlider").setPower(0);

        Pose2d pose1 = new Pose2d(0, 33, Math.toRadians(270));
        Pose2d pose2 = new Pose2d(3, 33, Math.toRadians(270));
        Pose2d pose3 = new Pose2d(0, 35, Math.toRadians(270));
        Pose2d pose4 = new Pose2d(3,33,Math.toRadians(270));
        Pose2d pose5 = new Pose2d(3, 36, Math.toRadians(270));
        Pose2d pose6 = new Pose2d(-48, 52, Math.toRadians(90));
        Pose2d pose7 = new Pose2d(-3,33,Math.toRadians(270));
        Pose2d pose8 = new Pose2d(-3, 33, Math.toRadians(270));

        primaryClaw.closeClaw();
        while (!opModeIsActive() && !isStopRequested()) {
        }
        waitForStart();
        if (opModeIsActive()) {
            //Clip first specimen

            slider.doHighSpecimenLowBasket();
            Actions.runBlocking(drive.actionBuilder(startingPose)
                    .splineToConstantHeading(new Vector2d(0, 33), Math.toRadians(270))
                    .build());
            //release first specimen
            telemetry.addLine("Clipped First Specimen");
            primaryClaw.openClaw();
            sleep(50);
            Actions.runBlocking(drive.actionBuilder(pose1)
                    .strafeTo(new Vector2d(0, 36))
                    .build());
            telemetry.addLine("Going to 2nd specimen");
            //strafe to second specimen on the field
            slider.resetHeight();
            Actions.runBlocking(drive.actionBuilder(pose2)
                    .strafeTo(new Vector2d(-35, 34))
                    .strafeTo(new Vector2d(-35, 13))
                    .strafeTo(new Vector2d(-48, 13))
                    //Push second into observation zone
                    .strafeTo(new Vector2d(-48, 52))
                    .turnTo(Math.toRadians(90))
                    .build());
            //grab third specimen
            primaryClaw.openClaw();
            sleep(400);
            primaryClaw.closeClaw();
            telemetry.addLine("Grabbing third specimen");
            slider.doHighSpecimenLowBasket();
            Actions.runBlocking(drive.actionBuilder(pose3)
                    .strafeToLinearHeading(new Vector2d(3, 36), Math.toRadians(270))
                    .strafeTo(new Vector2d(3, 33))
                    .build());
            //release clipped third specimen, and RTB
            sleep(50);
            primaryClaw.openClaw();
            telemetry.addLine("Clipped third specimen");
            Actions.runBlocking(drive.actionBuilder(pose4)
                    .strafeTo(new Vector2d(3, 36))
                    .build());
            slider.resetHeight();
            Actions.runBlocking(drive.actionBuilder(pose5)
                    .strafeToLinearHeading(new Vector2d(-48, 52), Math.toRadians(90))
                    .build());
            telemetry.addLine("Grabbed 2nd specimen");
            //Grab second specimen and clip
            primaryClaw.openClaw();
            sleep(400);
            primaryClaw.closeClaw();
            slider.doHighSpecimenLowBasket();
            Actions.runBlocking(drive.actionBuilder(pose6)
                    .strafeToLinearHeading(new Vector2d(-3, 36), Math.toRadians(270))
                    .strafeTo(new Vector2d(-3, 33))
                    .build());
            primaryClaw.openClaw();
            sleep(50);
            telemetry.addLine("Clipped second specimen");
            Actions.runBlocking(drive.actionBuilder(pose7)
                    .strafeTo(new Vector2d(-3, 36))
                    .build());
            telemetry.addLine("RTB");
            //Park in observation zone
            slider.resetHeight();
            Actions.runBlocking(drive.actionBuilder(pose8)
                    .strafeTo(new Vector2d(-48, 54))
                    .build());
            telemetry.addLine("DONE");
        }
    }
}
