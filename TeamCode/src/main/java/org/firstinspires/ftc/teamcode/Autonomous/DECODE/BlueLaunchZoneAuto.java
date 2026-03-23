package org.firstinspires.ftc.teamcode.Autonomous.DECODE;

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
import org.firstinspires.ftc.teamcode.RoadRunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.subsystems.DECODEExclusive.FeedServoLauncher;

/**
 * Autonomous Program for when the robot starts on the blue team,
 * at the launch zone.
 *
 * @author Thu
 * @author Mentor Landon Smith
 */
@Config
@Autonomous(name = "Blue Launch Zone", group = "autonomous")
public class BlueLaunchZoneAuto extends LinearOpMode {
    public static int minimumLauncherVelocity =-450;//Degrees per second
    public static double testXValue= 14;
    public static double testYValue= -59;

    public static double launchTime= 3;
    public static double intake2LaunchTime = 3; // Independent time for intakeMotor2 (less than launchTime)
    public static double intakeDelay = 0.3; // Added delay to wait before intaking
    private FeedServoLauncher feedServos;
    private DcMotorEx intakeMotor;

    private DcMotorEx intakeMotor2;
    private DcMotorEx shooterMotor;

    @Override
    public void runOpMode() {
        Pose2d startingPose = new Pose2d( -65.44872854638287, -61.686670498585144,Math.toRadians(-127.62830011383296));
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
                // Current Path
                .strafeToConstantHeading(new Vector2d( -48.193258450725885,-38.08076873539001)) //moves to position one
                .stopAndAdd(launchBallsForSetTime())//launches
                .strafeToLinearHeading(new Vector2d(-13.550299697034943,-31.265789742981054), Math.toRadians(-90))//moves to position two

                // Start both motors
                .stopAndAdd(new InstantAction(() ->
                    intakeMotor.setPower(-1)))
                .waitSeconds(0.8)
                .strafeToConstantHeading(new Vector2d( -13.550299697034943, -66.708993986835621))//moves to position three

                .stopAndAdd(new InstantAction(() -> intakeMotor.setPower(0)))
                .strafeToLinearHeading(new Vector2d(-48.193258450725885,-38.08076873539001), Math.toRadians(-127.62830011383296))//moves to position one

                .stopAndAdd(launchBallsForSetTime())
                .strafeToLinearHeading(new Vector2d(19.879103532926305, -29.568652055394935), Math.toRadians(-90))//moves to position four
                .stopAndAdd(new InstantAction(() ->
                    intakeMotor.setPower(-1)))
                .waitSeconds(0.8)
                .strafeToConstantHeading(new Vector2d(19.879103532926305,-75.35826194943407))//moves to position five
                .stopAndAdd(new InstantAction(() -> intakeMotor.setPower(0)))
                .strafeToConstantHeading(new Vector2d(19.879103532926305,-29.568652055394935))//moves back to four
//
                .strafeToLinearHeading(new Vector2d(-48.193258450725885,-38.08076873539001), Math.toRadians(-127.62830011383296))//moves back to one
                .waitSeconds(.225)
                .stopAndAdd(launchBallsForSetTime())
                .strafeToLinearHeading(new Vector2d(-70.41374146489961,-128.44098892716537), Math.toRadians(-360))//moves to position six



//

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
     * The reason why this method was created, is because once a sequential action is complete(or any action),
     * passing it in as an argument in another part of the trajectory will not reset the action to repeat what it has
     * already completed, since passing in an object only passes its reference, so the state is universal among
     * all references to that object. By returning a new sequential action every time, we can "reset" the state of the action
     * by simply creating a new object reference with the exact same behavior
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
                return shooterMotor.getVelocity(AngleUnit.DEGREES) > minimumLauncherVelocity;
            }
        };
        //This sequential action uses the above action, along with the rest of the launch sequence
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


























