package org.firstinspires.ftc.teamcode.TeleOp.BIOBUZZ;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;


import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.subsystems.Universal.DriveTrain;
import org.firstinspires.ftc.teamcode.subsystems.Universal.GamepadController;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

/**
 * Runs just enough code to make a drivetrain move, and has the necessary spots to create a full
 * OpMode that runs everything
 * @author Mentor Landon Smith
 */

@Config
@TeleOp(name="XDriveBIOBUZZ", group="TeleOp")
public class XDriveBIOBUZZ extends LinearOpMode {
    private DcMotor frontLeft, frontRight, backLeft, backRight, intake, launcher;
    private Servo launcherAdjuster;
    private GoBildaPinpointDriver pinpoint;

    private final int gearCoefficient = -1;

    @Override
    public void runOpMode() throws InterruptedException {
        frontLeft = hardwareMap.get(DcMotor.class, "front_left");
        frontRight = hardwareMap.get(DcMotor.class, "front_right");
        backLeft = hardwareMap.get(DcMotor.class, "back_left");
        backRight = hardwareMap.get(DcMotor.class, "back_right");
        intake = hardwareMap.get(DcMotor.class, "intake");
        //launcher = hardwareMap.get(DcMotor.class, "launcher");
        //launcherAdjuster = hardwareMap.get(Servo.class, "launcher_adjuster");
        //pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
        intake.setDirection(DcMotorSimple.Direction.REVERSE);

        double x, y, yaw, speedCoefficient;

        //AprilTagProcessor aprilTagProcessor = AprilTagProcessor.easyCreateWithDefaults();
        //WebcamName webcamName = hardwareMap.get(WebcamName.class, "webcam");
        //VisionPortal visionPortal = VisionPortal.easyCreateWithDefaults(webcamName, aprilTagProcessor);

        waitForStart();

        while (opModeIsActive()) {
            x = gamepad1.left_stick_x;
            y = -gamepad1.left_stick_y;
            yaw = gamepad1.right_stick_x;
            if (gamepad1.right_bumper) {
                speedCoefficient = 0.3;
            } else {
                speedCoefficient = 1;
            }
            frontLeft.setPower(speedCoefficient * gearCoefficient * (y + x + yaw));
            backLeft.setPower(speedCoefficient * gearCoefficient * (y - x + yaw));
            frontRight.setPower(speedCoefficient * gearCoefficient * (y - x - yaw));
            backRight.setPower(speedCoefficient * gearCoefficient * (y + x - yaw));
            if (gamepad1.right_trigger > 0) {
                intake.setPower(gamepad1.right_trigger);
            } else if (gamepad1.left_trigger > 0) {
                intake.setPower(-gamepad1.left_trigger);
            } else {
                intake.setPower(0);
            }
            telemetry.addData("Left Stick x", gamepad1.left_stick_x);
            telemetry.addData("Left Stick y", gamepad1.left_stick_y);
            telemetry.addData("Right Stick x", gamepad1.right_stick_x);
            telemetry.addData("Right Stick y", gamepad1.right_stick_y);
            telemetry.addData("Left Trigger", gamepad1.left_trigger);
            telemetry.addData("Right Trigger", gamepad1.right_trigger);
            telemetry.addData("Status", "Running");
            telemetry.update();
        }
    }
}
