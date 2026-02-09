package org.firstinspires.ftc.teamcode.TeleOp;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@TeleOp(name = "Field-Centric Drive", group = "Linear OpMode")
public class FieldCentricTeleOp extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        // 1. INITIALIZE YOUR MECANUMDRIVE CLASS
        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));

        waitForStart();

        while (opModeIsActive() && !isStopRequested()) {
            // 2. UPDATE THE ROBOT'S POSITION
            drive.updatePoseEstimate();

            // 3. GET THE ROBOT'S CURRENT POSE AND HEADING
            Pose2d currentPose = drive.localizer.getPose();
            double heading = currentPose.heading.toDouble();

            // 4. GET JOYSTICK INPUTS
            double leftStickY = -gamepad1.left_stick_y;
            double leftStickX = gamepad1.left_stick_x;
            double rightStickX = gamepad1.right_stick_x;

            // 5. ROTATE THE JOYSTICK INPUTS BY THE ROBOT'S HEADING
            // *** THIS IS THE FIX ***
            // Manually rotate the vector to avoid library version conflicts with .rotated()
            double rotatedX = leftStickX * Math.cos(-heading) - leftStickY * Math.sin(-heading);
            double rotatedY = leftStickX * Math.sin(-heading) + leftStickY * Math.cos(-heading);

            // 6. CREATE A POSEVELOCITY2D OBJECT
            // Use the manually rotated values to create the drive powers.
            PoseVelocity2d drivePowers = new PoseVelocity2d(
                    new Vector2d(
                            rotatedY, // Corresponds to Forward/Backward
                            -rotatedX  // Corresponds to Strafe Left/Right (must be negated)
                    ),
                    -rightStickX // Corresponds to Turn Left/Right (must be negated)
            );

            // 7. SEND THE POWERS TO THE DRIVETRAIN
            drive.setDrivePowers(drivePowers);

            // Optional: Display the robot's position and heading on the Driver Hub
            telemetry.addData("X", currentPose.position.x);
            telemetry.addData("Y", currentPose.position.y);
            telemetry.addData("Heading (degrees)", Math.toDegrees(heading));
            telemetry.update();
        }
    }
}






