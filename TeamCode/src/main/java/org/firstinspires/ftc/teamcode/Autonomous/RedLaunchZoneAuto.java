<<<<<<<< HEAD:TeamCode/src/main/java/org/firstinspires/ftc/teamcode/Autonomous/DECODE/RedLaunchZoneAuto.java
package org.firstinspires.ftc.teamcode.Autonomous.DECODE;
========
package org.firstinspires.ftc.teamcode.Autonomous;
>>>>>>>> 5c9a1e5 (Reorganized and fixed logic):TeamCode/src/main/java/org/firstinspires/ftc/teamcode/Autonomous/RedLaunchZoneAuto.java

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
<<<<<<<< HEAD:TeamCode/src/main/java/org/firstinspires/ftc/teamcode/Autonomous/DECODE/RedLaunchZoneAuto.java
import org.firstinspires.ftc.teamcode.RoadRunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.subsystems.DECODEExclusive.FeedServoLauncher;
========
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.TeleOp.XDriveDECODE;
import org.firstinspires.ftc.teamcode.subsystems.FeedServoLauncher;
>>>>>>>> 5c9a1e5 (Reorganized and fixed logic):TeamCode/src/main/java/org/firstinspires/ftc/teamcode/Autonomous/RedLaunchZoneAuto.java

/**
 * Autonomous Program for when the robot starts on the red team,
 * at the launch zone.
 *
 * @author Thu
 * @author Mentor Landon Smith
 */
@Config
@Autonomous(name = "Red Launch Zone", group = "autonomous")
public class RedLaunchZoneAuto extends LinearOpMode {
    public static int minimumLauncherVelocity =-450;//Degrees per second
    public static double testXValue= -15;
    public static double testYValue= 52;
    public static double launchTime= 3;
    public static double intake2LaunchTime = 3; // Independent time for intakeMotor2 (less than launchTime)
    public static double intakeDelay = 0.3; // Added delay to wait before intaking
    private FeedServoLauncher feedServos;
    private DcMotorEx intakeMotor;

    private DcMotorEx intakeMotor2;
    private DcMotorEx shooterMotor;

    @Override
    public void runOpMode() {
        Pose2d startingPose = new Pose2d(  -49.76891705370325, 55.758796751968504,Math.toRadians(129.3629632573411));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startingPose);

        // --- Initialize Launcher and Servos ---

        shooterMotor = hardwareMap.get(DcMotorEx.class, "shooterMotor");

        intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");
        intakeMotor2 = hardwareMap.get(DcMotorEx.class, "intakeMotor2");
        feedServos = new FeedServoLauncher(this);
        feedServos.stop();
        //This is how you create an action with specific behavior that is not defined anywhere else

        shooterMotor.setDirection(DcMotorEx.Direction.REVERSE);

        // --- End of Initialization ---


        Action autonomous = drive.actionBuilder(startingPose)

                .strafeToConstantHeading(new Vector2d(-32.4,33.6))
                .stopAndAdd(launchBallsForSetTime())//launches
                .strafeToLinearHeading(new Vector2d(-9.5,27.3), Math.toRadians(90))//moves to first set of balls
                // Start both motors
                .stopAndAdd(new InstantAction(() -> {
                    intakeMotor.setPower(-1);
                }))
                .waitSeconds(0.4)

                .strafeToConstantHeading(new Vector2d(-9.5,61))
                .stopAndAdd(new InstantAction(() -> intakeMotor.setPower(0)))
                .strafeToLinearHeading(new Vector2d(-30.2,36.7), Math.toRadians(129.3629632573411))
                .stopAndAdd(launchBallsForSetTime())
                .strafeToLinearHeading(new Vector2d(14,27.3), Math.toRadians(90))
                .stopAndAdd(new InstantAction(() -> {
                    intakeMotor.setPower(-1);
                }))
                .waitSeconds(0.4)
                .strafeToConstantHeading(new Vector2d(14, 65.3))
                .stopAndAdd(new InstantAction(() -> intakeMotor.setPower(0)))
                .strafeToConstantHeading(new Vector2d(14, 33.5))
                .strafeToLinearHeading(new Vector2d(-30.2,36.7), Math.toRadians(129.3629632573411))
                .stopAndAdd(launchBallsForSetTime())
                .strafeToLinearHeading(new Vector2d(-47.3,28.3), Math.toRadians(360))
//                // Current Path










//                .strafeToConstantHeading(new Vector2d( -47.30055441067913,49.183412086306596)) //moves to position one
//                .stopAndAdd(launchBallsForSetTime())//launches
//                .strafeToLinearHeading(new Vector2d(-16.664379450279895,36.74443763072097), Math.toRadians(90))//moves to position two
//
//                // Start both motors
//                .stopAndAdd(new InstantAction(() -> {
//                    intakeMotor.setPower(-1);
//
//
//                }))
//                .waitSeconds(0.8)
//                .strafeToConstantHeading(new Vector2d( -16.664379450279895, 63.8651565191314))//moves to position three
//
//                .stopAndAdd(new InstantAction(() -> intakeMotor.setPower(0)))
//                .strafeToLinearHeading(new Vector2d(-47.30055441067913,49.183412086306596), Math.toRadians(130.3782025755718))//moves to position one
//
//                .stopAndAdd(launchBallsForSetTime())
//                .strafeToLinearHeading(new Vector2d(14.0328655092735, 39.49721689299336), Math.toRadians(90))//moves to position four
//                .stopAndAdd(new InstantAction(() -> {
//                    intakeMotor.setPower(-1);
//                }))
//                .waitSeconds(0.8)
//                .strafeToConstantHeading(new Vector2d(14.0328655092735,76.9694305179626))//moves to position five
//                .stopAndAdd(new InstantAction(() -> intakeMotor.setPower(0)))
//                .strafeToConstantHeading(new Vector2d(14.0328655092735,39.49721689299336))//moves back to four
////
//                .strafeToLinearHeading(new Vector2d(-47.30055441067913,49.183412086306596), Math.toRadians(130.3782025755718))//moves back to one
//                .waitSeconds(.225)
//                .stopAndAdd(launchBallsForSetTime())
//                .strafeToLinearHeading(new Vector2d(-51.82927169199065,18.91646858275406), Math.toRadians(360))//moves to position six

                // --- End of Launch Sequence ---

                .build();

        while (!opModeIsActive() && !isStopRequested()) {
            sleep(50);
        }

        waitForStart();

        if (opModeIsActive()) {
            Actions.runBlocking(autonomous);
        }
    }

    /**
     * @return SequentialAction which launches the balls for a set time
     * @see SequentialAction
     */
    private SequentialAction launchBallsForSetTime() {
        //This action implements a condition to wait until the velocity is above a certain threshold
        Action waitForSufficientLauncherVelocity = new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket p) {
                if (!initialized) {
                    shooterMotor.setPower(1);
                    shooterMotor.setVelocity(minimumLauncherVelocity, AngleUnit.DEGREES);
                    initialized = true;
                }
                p.put("launcherVelocity: ", shooterMotor.getVelocity());
                // Wait until velocity is negative enough (since minimumLauncherVelocity is -450)
                return shooterMotor.getVelocity(AngleUnit.DEGREES) > minimumLauncherVelocity;
            }
        };
        //This sequential action uses ParallelAction to control intake motors independently
        return new SequentialAction(
                waitForSufficientLauncherVelocity,
                new SleepAction(intakeDelay), // Wait before starting intake
                new ParallelAction(
                        new SequentialAction(
                                new InstantAction(() -> intakeMotor.setPower(-1)),
                                new SleepAction(launchTime),
                                new InstantAction(() -> intakeMotor.setPower(0))
                        ),
                        new SequentialAction(
                                new InstantAction(() -> intakeMotor2.setPower(-1)),
                                new SleepAction(intake2LaunchTime),
                                new InstantAction(() -> intakeMotor2.setPower(0))
                        )
                ),
                new InstantAction(() -> shooterMotor.setPower(0)));
    }
}
