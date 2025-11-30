package org.firstinspires.ftc.teamcode;

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

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.subsystems.FeedServoLauncher;

/**
 * Autonomous Program for when the robot starts on the blue team,
 * at the launch zone.
 * @author Thu
 * @author Mentor Landon Smith
 */
@Config
@Autonomous(name = "Blue Launch Zone", group="autonomous")
public class BlueLaunchZoneAuto extends LinearOpMode {
    public static int minimumLauncherVelocity = 15;//Degrees per second
    @Override
    public void runOpMode(){
        Pose2d startingPose = new Pose2d(-52.0,-46.1, Math.toRadians(232));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startingPose);
        // --- Initialize Launcher and Servos ---
        DcMotorEx launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        FeedServoLauncher feedServos = new FeedServoLauncher(this);
        launcher.setDirection(DcMotorEx.Direction.REVERSE);
        //This is how you create an action with specific behavior that is not defined anywhere else
        InstantAction waitUntilSufficentLauncherVelocity = new InstantAction(new InstantFunction() {
            public void run() {
                while(launcher.getVelocity(AngleUnit.DEGREES) < minimumLauncherVelocity);
            }
        });

            // Using setPower to match TeleOp, so RUN_USING_ENCODER is not needed.
        // --- End of Initialization ---

        Action autonomous = drive.actionBuilder(startingPose)
                // Current Path
                //TODO: Finish actions for launching and intaking balls
                .stopAndAdd(new InstantAction(() -> launcher.setPower(1)))//TODO: Calculate compensated shooter power
                .stopAndAdd(waitUntilSufficentLauncherVelocity)
                .stopAndAdd(feedServos.intakeBallAction()) //launch balls
                .strafeToConstantHeading(new Vector2d(-32.2,-23.2))
                .turn(Math.toRadians(127))
                .strafeToConstantHeading(new Vector2d(-12.3,-23.0))
                .turn(Math.toRadians(-92))
                .strafeToConstantHeading(new Vector2d(-11.9,-51.4))

                .strafeToConstantHeading(new Vector2d(-12.3,-23.0))
                .turn(Math.toRadians(-92))
                .strafeToConstantHeading(new Vector2d(-32.2,-23.2))
                .turn(Math.toRadians(56))
                .strafeToConstantHeading(new Vector2d(-53.1,-46.1))

                .strafeToConstantHeading(new Vector2d(-32.2,-23.2))
                .turn(Math.toRadians(127))
                .strafeToConstantHeading(new Vector2d(11.3,-23.4))
                .turn(Math.toRadians(-92))

                .strafeToConstantHeading(new Vector2d(11.9,-50.0))
                .strafeToConstantHeading(new Vector2d(11.3,-23.4))
                .turn(Math.toRadians(-92))
                .strafeToConstantHeading(new Vector2d(-32.2,-23.2))
                .turn(Math.toRadians(56))
                .strafeToConstantHeading(new Vector2d(-53.1,-46.1))
                .strafeToConstantHeading(new Vector2d(-32.2,-23.2))
                .turn(Math.toRadians(127))
                .strafeToConstantHeading(new Vector2d(35.3,-23.8))
                .turn(Math.toRadians(-92))



                .build();

        waitForStart();

        if (opModeIsActive()){
            Actions.runBlocking(autonomous);
        }
    }

}
