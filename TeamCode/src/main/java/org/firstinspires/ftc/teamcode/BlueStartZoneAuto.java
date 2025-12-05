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
 * Autonomous Program for when the robot starts on the blue team,
 * at the start zone.
 * @author Thu
 * @author Mentor Landon Smith
 */
@Config
@Autonomous(name = "Blue Start Zone", group="autonomous")
public class BlueStartZoneAuto extends LinearOpMode {
    @Override
    public void runOpMode(){
        double originalMaxWheelVel = MecanumDrive.PARAMS.maxWheelVel;
        try {
            MecanumDrive.PARAMS.maxWheelVel = originalMaxWheelVel * 0.9;

            Pose2d startingPose = new Pose2d(61.8, -12.5, Math.toRadians(180));
            MecanumDrive drive = new MecanumDrive(hardwareMap, startingPose);

            // --- Initialize Launcher and Servos ---
            DcMotorEx launcher = hardwareMap.get(DcMotorEx.class, "launcher");
            launcher.setDirection(DcMotorEx.Direction.REVERSE);
            FeedServoLauncher feedServos = new FeedServoLauncher(this);
            feedServos.stop();


            // --- End of Initialization ---

            Action autonomous = drive.actionBuilder(startingPose)
                    // Current Path
                    .strafeToConstantHeading(new Vector2d(36.3,-12.7))
                    .turn(Math.toRadians(90))

                    .strafeToConstantHeading(new Vector2d(35.0,-56.0))
                    .strafeToConstantHeading(new Vector2d(36.3,-12.7))
                    .strafeToLinearHeading(new Vector2d(32.2,-23.2), Math.toRadians(-90))
                    .turn(Math.toRadians(-90))
                    .strafeToConstantHeading(new Vector2d(-30.8,-15.1))
                    .turn(Math.toRadians(54))
                    .strafeToConstantHeading(new Vector2d(-53.4,-46.1))

                    .strafeToConstantHeading(new Vector2d(-30.8,-15.1))
                    .turn(Math.toRadians(127))
                    .strafeToConstantHeading(new Vector2d(10.5,-13.5))
                    .turn(Math.toRadians(-92))

                    .strafeToConstantHeading(new Vector2d(11.5,-51.4))
                    .strafeToConstantHeading(new Vector2d(10.5,-13.5))
                    .turn(Math.toRadians(-92))
                    .strafeToConstantHeading(new Vector2d(-30.8,-15.1))
                    .turn(Math.toRadians(54))
                    .strafeToConstantHeading(new Vector2d(-53.4,-46.1))
                    .strafeToConstantHeading(new Vector2d(-30.8,-15.1))
                    .turn(Math.toRadians(127))
                    .strafeToConstantHeading(new Vector2d(-12.5,-14.1))
                    .turn(Math.toRadians(-90))
                    .strafeToConstantHeading(new Vector2d(-12.5,-37.5))




                    // --- End of Launch Sequence ---

                    .build();

            waitForStart();

            if (opModeIsActive()) {
                Actions.runBlocking(autonomous);
            }
        } finally {
            MecanumDrive.PARAMS.maxWheelVel = originalMaxWheelVel;
        }
    }

}
