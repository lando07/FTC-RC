package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.subsystems.FeedServoLauncher;

/**
 * Autonomous Program for when the robot starts on the red team,
 * at the start zone.
 *
 * @author Thu
 * @author Mentor Landon Smith
 */
@Config
@Autonomous(name = "Blue Start Zone", group = "autonomous")
public class BlueStartZoneAuto extends LinearOpMode {
    public static int minimumLauncherVelocity =400;//Degrees per second
    public static double testXValue= -15;
    public static double testYValue= 52;
    public static double launchTime= 3;
    private FeedServoLauncher feedServos;
    private DcMotorEx intakeMotor;
    private DcMotorEx shooterMotor;

    @Override
    public void runOpMode() {
        Pose2d startingPose = new Pose2d( 61.31118413970226, -20.0335849551704,Math.toRadians( 180));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startingPose);

        // --- Initialize Launcher and Servos ---

        shooterMotor = hardwareMap.get(DcMotorEx.class, "shooterMotor");

        intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");
        feedServos = new FeedServoLauncher(this);
        feedServos.stop();
        //This is how you create an action with specific behavior that is not defined anywhere else

        shooterMotor.setDirection(DcMotorEx.Direction.REVERSE);

        // --- End of Initialization ---


        Action autonomous = drive.actionBuilder(startingPose)
                // Current Path
                //.strafeToConstantHeading(new Vector2d(10.1,29))
                .strafeToConstantHeading(new Vector2d(-32.4,-23.2))
                .turn(Math.toRadians(49))
                .strafeToConstantHeading(new Vector2d(-48.7,-39.1))
                .stopAndAdd(launchBallsForSetTime())
                .strafeToLinearHeading(new Vector2d(-13,-22), Math.toRadians(-90))
                .waitSeconds(.2)
                .stopAndAdd(feedServos.rejectBallAction())
                .strafeToConstantHeading(new Vector2d(-13,-50))
                .strafeToLinearHeading(new Vector2d(-32.4,-24.2), Math.toRadians(-130))
                .strafeToConstantHeading(new Vector2d(-48.7,-43.1))
                .stopAndAdd(launchBallsForSetTime())
                .strafeToLinearHeading(new Vector2d(12.1,-23.6), Math.toRadians(-90))
                .strafeToLinearHeading(new Vector2d(12.1,-23.6), Math.toRadians(-90))
                .waitSeconds(.2)
                .stopAndAdd(feedServos.rejectBallAction())
                .strafeToLinearHeading(new Vector2d(12.1,-55.2), Math.toRadians(-90))
                .strafeToLinearHeading(new Vector2d(12.1,-23.6), Math.toRadians(-90))
                .strafeToLinearHeading(new Vector2d(-32.4,-24.2), Math.toRadians(-130))
                .strafeToConstantHeading(new Vector2d(-48.7,-43.1))
                .stopAndAdd(launchBallsForSetTime())
                .strafeToLinearHeading(new Vector2d(-67.8,-30), Math.toRadians(-90))


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
                    shooterMotor.setVelocity(XDriveDECODE.targetVelocity, AngleUnit.DEGREES);
                    initialized = true;
                }
                p.put("launcherVelocity: ", shooterMotor.getVelocity());
                return shooterMotor.getVelocity(AngleUnit.DEGREES) <= minimumLauncherVelocity;
            }
        };
        //This sequential action uses the above action, along with the rest of the launch sequence
        return new SequentialAction(
                waitForSufficientLauncherVelocity,
                new InstantAction(() -> intakeMotor.setPower(1)),
                feedServos.intakeBallAction(),
                new SleepAction(launchTime),
                feedServos.stopIntakeAction(),
                new InstantAction(() -> shooterMotor.setPower(0)));
    }
}

