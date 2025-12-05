package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
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
 * at the launch zone.
 *
 * @author Thu
 * @author Mentor Landon Smith
 */
@Config
@Autonomous(name = "Red Launch Zone", group = "autonomous")
public class RedLaunchZoneAuto extends LinearOpMode {
    public static int minimumLauncherVelocity = 15;//Degrees per second
public static void main(String[] args) {


}
    @Override
    public void runOpMode() {
        Pose2d startingPose = new Pose2d(-53.1, 46.1, Math.toRadians(-232));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startingPose);

        // --- Initialize Launcher and Servos ---

        DcMotorEx shooterMotor = hardwareMap.get(DcMotorEx.class, "shooterMotor");

        DcMotorEx launcher = hardwareMap.get(DcMotorEx.class, "shooterMotor");
        FeedServoLauncher feedServos = new FeedServoLauncher(this);
        feedServos.stop();
        //This is how you create an action with specific behavior that is not defined anywhere else
        Action waitUntilSufficientLauncherVelocity = new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket p) {
                if (!initialized) {
                    shooterMotor.setPower(1);
                    initialized = true;
                }
                p.put("launcherVelocity: ", shooterMotor.getVelocity());
                return shooterMotor.getVelocity(AngleUnit.DEGREES) > minimumLauncherVelocity;
            }
        };

        SequentialAction launchBallsForSetTime = new SequentialAction(
                waitUntilSufficientLauncherVelocity,
                feedServos.intakeBallAction(),
                new SleepAction(1.000),
                feedServos.stopIntakeAction());

        shooterMotor.setDirection(DcMotorEx.Direction.REVERSE);

        // --- End of Initialization ---

        MecanumDrive.DriveLocalizer dl = (MecanumDrive.DriveLocalizer) drive.localizer;

        Action autonomous = drive.actionBuilder(startingPose)
                // Current Path
                .strafeToLinearHeading(new Vector2d(-32.2,23.2), Math.toRadians(90))
                .strafeToConstantHeading(new Vector2d(-12.3,23.0))
                .strafeToConstantHeading(new Vector2d(-11.9,52.0))

                .strafeToLinearHeading(new Vector2d(-32.2,23.2), Math.toRadians(-232))
                .strafeToConstantHeading(new Vector2d(-32.2,23.2))
                .strafeToConstantHeading(new Vector2d(-53.1,46.1))

                .strafeToConstantHeading(new Vector2d(-32.2,23.2))

                .strafeToLinearHeading(new Vector2d(11.3,23.4), Math.toRadians(90))

                .strafeToConstantHeading(new Vector2d(11.9,50.0))
                .strafeToConstantHeading(new Vector2d(11.3,23.4))
                .strafeToLinearHeading(new Vector2d(-32.2,23.2), Math.toRadians(90))
                .turn(Math.toRadians(38))
                .strafeToConstantHeading(new Vector2d(-32.2,23.2))
                .strafeToConstantHeading(new Vector2d(-53.1,46.1))
                .strafeToLinearHeading(new Vector2d(-32.2,23.2), Math.toRadians(90))

                .strafeToConstantHeading(new Vector2d(35.3,23.8))

                // --- End of Launch Sequence ---

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
