package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.XDriveDECODE.NOMINAL_VOLTAGE;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.InstantFunction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.teamcode.subsystems.FeedServoLauncher;

@Config
@Autonomous(name = "Launch Auto", group = "Autonomous")
public class LaunchAuto extends LinearOpMode {
    double currentVoltage;
    double compensatedShooterPower;
    VoltageSensor vs;

    @Override
    public void runOpMode() {
        Pose2d startingPose = new Pose2d(-53.1, 46.1, Math.toRadians(-232));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startingPose);
        vs = hardwareMap.voltageSensor.iterator().next();

        DcMotorEx shooterMotor = hardwareMap.get(DcMotorEx.class, "shooterMotor");
        DcMotorEx intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");
        FeedServoLauncher feedServos = new FeedServoLauncher(this);
        feedServos.stop();
        shooterMotor.setDirection(DcMotorEx.Direction.REVERSE);
        PinpointLocalizer dl = (PinpointLocalizer) drive.localizer;
        Action autonomous = drive.actionBuilder(startingPose)
//                .stopAndAdd(new InstantAction(() -> intakeMotor.setPower(1)))
//                .waitSeconds(.5)
//                .stopAndAdd(new InstantAction(() -> intakeMotor.setPower(0)))
                .stopAndAdd(new InstantAction(() -> shooterMotor.setPower(0.55)))
                .waitSeconds(3)
                .stopAndAdd(feedServos.intakeBallAction())
                .stopAndAdd(new InstantAction(() -> intakeMotor.setPower(-1)))
                .waitSeconds(.2)
//                .strafeTo(new Vector2d(-53.1,46.1))
                .stopAndAdd(feedServos.stopIntakeAction())
                .waitSeconds(1)
                .stopAndAdd(new InstantAction(() -> intakeMotor.setPower(0)))
                .waitSeconds(2.5)
                .stopAndAdd(feedServos.intakeBallAction())
                .waitSeconds(.2)
                .stopAndAdd(feedServos.stopIntakeAction())
                .waitSeconds(2.8)
                .stopAndAdd(new InstantAction(() -> intakeMotor.setPower(-1)))
                .stopAndAdd(feedServos.intakeBallAction())
                .waitSeconds(3)
//                .strafeToConstantHeading(new Vector2d(-47, 22))
                .build();
        while (!opModeIsActive() && !isStopRequested()) {
            sleep(50);
        }

        waitForStart();

        if (opModeIsActive()) {
            Actions.runBlocking(autonomous);
        }
    }

    private void compensateShooterPower() {
        currentVoltage = vs.getVoltage();
        if (currentVoltage < 8.0) { // Safety check
            currentVoltage = NOMINAL_VOLTAGE;
        }
        double voltageCompensationFactor = NOMINAL_VOLTAGE / currentVoltage;
        compensatedShooterPower = 0.55 * voltageCompensationFactor;
    }
}
