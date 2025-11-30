package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.RaiseArmSlider;
/**
 * The UIL State Semi-Finalist Autonomous,
 * it made use of many test values that needed to change
 * as different floor mats had different levels of hardness and
 * slip, which affected the how the motor encoders
 * tracked the robot positioning since we did not have odometry
 * wheels in FTC Into the DEEP
 * @author Landon Smith
 */
@Disabled
@Config
@Autonomous(name = "SecondaryAutoIntoTheDeep", group = "autonomous")
public class SecondaryAutonomousIntoTheDeep extends LinearOpMode {
    public static double testYValue = 60;

    public static double testXvalue = 60;

    /**
     * @noinspection StatementWithEmptyBody
     */
    @Override
    public void runOpMode() {
        Pose2d startingPose = new Pose2d(17.2, 62, Math.toRadians(-90));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startingPose);
        RaiseArmSlider slider = new RaiseArmSlider(this, "raiseArmSlider");
        Claw primaryClaw = new Claw(this, "primaryClaw");
        DcMotor secondarySlider = hardwareMap.get(DcMotor.class, "armSlider");
        secondarySlider.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        primaryClaw.closeClaw();
        MecanumDrive.PARAMS.minProfileAccel = -10;
        MecanumDrive.PARAMS.maxProfileAccel = 25;
        MecanumDrive.PARAMS.maxAngAccel = 10;
        MecanumDrive.PARAMS.maxAngVel = 25;
        MecanumDrive.PARAMS.maxWheelVel = 25;

        while (!opModeIsActive() && !isStopRequested()) {
        }
        waitForStart();
        if (opModeIsActive()) {
            slider.doHighSample();
            Actions.runBlocking(drive.actionBuilder(startingPose)
                    .strafeToConstantHeading(new Vector2d(18, 58))
                    .strafeToLinearHeading(new Vector2d(testXvalue, testYValue), Math.toRadians(45))
                    .build());
            sleep(1000);
            primaryClaw.openClaw();
            sleep(1000);
            Actions.runBlocking(drive.actionBuilder(new Pose2d(testXvalue, testYValue, Math.toRadians(45)))
                    .strafeToConstantHeading(new Vector2d(testXvalue - 5, testYValue - 5))
                    .build());
            slider.setTargetPosition(-1310);
            primaryClaw.closeClaw();
            sleep(2000);
            Actions.runBlocking(drive.actionBuilder(new Pose2d(testXvalue - 5, testYValue - 5, Math.toRadians(45)))
                    .strafeToLinearHeading(new Vector2d(38, 32), Math.toRadians(180))
                    .strafeToConstantHeading(new Vector2d(38, 12))
                    .strafeToConstantHeading(new Vector2d(23, 6))
                    .build());


        }
    }
}
