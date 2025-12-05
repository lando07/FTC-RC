package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.subsystems.FeedServoLauncher;

/**
 * Autonomous Program for when the robot starts on the red team,
 * at the start zone.
 *
 * @author Thu
 * @author Mentor Landon Smith
 */
@Config
@Autonomous(name = "Red Start Zone", group = "autonomous")
public class RedStartZoneAuto extends LinearOpMode {
    @Override
    public void runOpMode() {
        Pose2d startingPose = new Pose2d(57.8, 12.7, Math.toRadians(180));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startingPose);

        // --- Initialize Launcher and Servos ---
        DcMotorEx launcher = hardwareMap.get(DcMotorEx.class, "shooterMotor");
        FeedServoLauncher feedServos = new FeedServoLauncher(this);
        feedServos.stop();
        launcher.setDirection(DcMotorEx.Direction.REVERSE);

        // --- End of Initialization ---

        MecanumDrive.DriveLocalizer dl = (MecanumDrive.DriveLocalizer) drive.localizer;

        Action autonomous = drive.actionBuilder(startingPose)

                // Current Path
                .strafeToConstantHeading(new Vector2d(36.3,12.7))
                .turn(Math.toRadians(-90))

                .strafeToConstantHeading(new Vector2d(34.8,56.2))
                .strafeToConstantHeading(new Vector2d(35.9,24.4))

                .strafeToLinearHeading(new Vector2d(-32.2,23.2), Math.toRadians(90))

                .turn(Math.toRadians(40))
                .strafeToConstantHeading(new Vector2d(-51.2,48.9))

                .strafeToLinearHeading(new Vector2d(-32.2,23.2), Math.toRadians(90))


                .strafeToConstantHeading(new Vector2d(12.7,24.2))

                .strafeToConstantHeading(new Vector2d(11.7,54.6))
                .strafeToConstantHeading(new Vector2d(12.7,24.2))
                .strafeToLinearHeading(new Vector2d(-32.2,23.2), Math.toRadians(90))


                .turn(Math.toRadians(40))
                .strafeToConstantHeading(new Vector2d(-51.2,48.9))

                .strafeToLinearHeading(new Vector2d(-32.2,23.2), Math.toRadians(90))

                .strafeToConstantHeading(new Vector2d(6.9,24.0))

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
