package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.MecanumDrive.PARAMS;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.bosch.BHI260IMU;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.BNO055IMUNew;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.MecanumDrive;

import java.util.Locale;

@Config
public class DriveTrain {

    public static axisBehavior lateralAxis = axisBehavior.LEFT_STICK_X;
    public static axisBehavior axialAxis = axisBehavior.LEFT_STICK_Y;
    public static axisBehavior yawAxis = axisBehavior.RIGHT_STICK_X;

    public static GamepadButton resetIMUButton = GamepadButton.X;

    public static GamepadButton halfSpeedButton = GamepadButton.RIGHT_BUMPER;
    public static GamepadButton toggleDriveModeButton = GamepadButton.Y;

    public static boolean toggleDriveModeButtonDisabled = true;
    public static boolean resetIMUButtonDisabled = false;
    public static double lateralGain = 1.0;
    public static double axialGain = 1.0;
    public static double yawGain = 3;
    public static double yawMultiplier = 0.5;
    public static double speedMultiplier = 0.7;
    private final BNO055IMUNew imu;
    private final DcMotor frontLeft;
    private final DcMotor frontRight;
    private final DcMotor backLeft;
    private final DcMotor backRight;
    private boolean halfSpeed = false;
    private final GamepadController gamepad;

    public DriveTrain(OpMode opmode, GamepadController controller) {
        gamepad = controller;
        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = opmode.hardwareMap.get(BNO055IMUNew.class, "imu 1");
        BNO055IMUNew.Parameters parameters = new BNO055IMUNew.Parameters(new RevHubOrientationOnRobot(PARAMS.logoFacingDirection, PARAMS.usbFacingDirection));
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        imu.initialize(parameters);

        frontLeft = opmode.hardwareMap.get(DcMotorEx.class, "leftFront");
        frontRight = opmode.hardwareMap.get(DcMotorEx.class, "rightFront");
        backLeft = opmode.hardwareMap.get(DcMotorEx.class, "leftBack");
        backRight = opmode.hardwareMap.get(DcMotorEx.class, "rightBack");
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        controller.configureAxis(lateralAxis);
        controller.configureAxis(axialAxis);
        controller.configureAxis(yawAxis);
        controller.configureBiStateButton(halfSpeedButton, BiStateButtonBehavior.HOLD);
        controller.configureBiStateButton(toggleDriveModeButton, BiStateButtonBehavior.TOGGLE);
        controller.configureBiStateButton(resetIMUButton, BiStateButtonBehavior.HOLD);
    }


    private void doFieldOrientedDrive() {


        final double lateral = speedMultiplier * Math.pow(((int) (gamepad.getAxisValue(lateralAxis) * 10000) / 10000.0), lateralGain);
        final double axial = speedMultiplier *Math.pow(((int) (-gamepad.getAxisValue(axialAxis) * 10000) / 10000.0), axialGain);
        final double yaw = yawMultiplier * Math.pow(((int) (gamepad.getAxisValue(yawAxis) * 10000) / 10000.0), yawGain);

        final double direction = -(Math.atan2(lateral, axial) + imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));// I have zero clue how the math works
        final double speed = Math.min(1.0, Math.sqrt(lateral * lateral + axial * axial));//vector normalization(I think) + pythagorean theorem
        //Somehow we throw more trig at these values and then it works
        final double vCos = speed * Math.cos(direction + Math.PI / 4.0);
        final double vSin = speed * Math.sin(direction + Math.PI / 4.0);
        double lf = vCos + yaw;//These 4 lines calculate the motor powers
        double rf = vSin - yaw;
        double lr = vSin + yaw;
        double rr = vCos - yaw;

        if (halfSpeed) {
            lf *= 0.5;
            rf *= 0.5;
            lr *= 0.5;
            rr *= 0.5;

        }
        //Actually what makes the robot moves
        frontLeft.setPower(lf);
        frontRight.setPower(rf);
        backLeft.setPower(lr);
        backRight.setPower(rr);
    }

    private void doClassicMecanumDrive() {
        double max;
        // Omni Mode uses right joystick to go forward & strafe, and left joystick to rotate.
        //Just like a drone
        //I decided to limit precision to 4 decimal places to counteract drift
        double axial = speedMultiplier * ((int) (gamepad.getAxisValue(axialAxis) * 10000) / 10000.0);  // Note: pushing stick forward gives negative value
        double lateral = speedMultiplier * ((int) (-gamepad.getAxisValue(lateralAxis) * 10000) / 10000.0);
        double yaw = yawMultiplier * ((int) (gamepad.getAxisValue(yawAxis) * 10000) / 10000.0 * yawMultiplier);
        //these are the magic 4 statements right here
        // Combine the joystick requests for each axis-motion to determine each wheel's power.
        // Set up a variable for each drive wheel to save the power level for telemetry.
        double leftFrontPower = axial + lateral + yaw;
        double rightFrontPower = axial - lateral - yaw;
        double leftBackPower = axial - lateral + yaw;
        double rightBackPower = axial + lateral - yaw;

        // Normalize the values so no wheel power exceeds 100%
        // This ensures that the robot maintains the desired motion.
        max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > 1.0) {
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }
        //cuts speed in half
        if (halfSpeed) {
            leftFrontPower *= 0.5;
            rightFrontPower *= 0.5;
            leftBackPower *= 0.5;
            rightBackPower *= 0.5;
        }

        frontLeft.setPower(leftFrontPower);
        frontRight.setPower(rightFrontPower);
        backLeft.setPower(leftBackPower);
        backRight.setPower(rightBackPower);
    }

    public void setBrakingMode(DcMotor.ZeroPowerBehavior zeroPowerBehavior) {
        frontLeft.setZeroPowerBehavior(zeroPowerBehavior);
        backLeft.setZeroPowerBehavior(zeroPowerBehavior);
        frontRight.setZeroPowerBehavior(zeroPowerBehavior);
        backRight.setZeroPowerBehavior(zeroPowerBehavior);
    }

    public void updateDriveTrainBehavior() {

        halfSpeed = gamepad.getGamepadButtonValue(halfSpeedButton);
        if(gamepad.getGamepadButtonValue(resetIMUButton)){
            imu.resetYaw();
        }
        if (gamepad.getGamepadButtonValue(toggleDriveModeButton) && !toggleDriveModeButtonDisabled) {
            doClassicMecanumDrive();
        } else {
            doFieldOrientedDrive();
        }

    }
    public BNO055IMUNew getIMU(){
        return imu;
    }

}
