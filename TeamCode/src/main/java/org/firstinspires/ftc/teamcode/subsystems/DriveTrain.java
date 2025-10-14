package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.MecanumDrive.PARAMS;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.bosch.BNO055IMUNew;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * This is the main class to control the 4 motors on the drive train. It has dual-functionality,
 * since it can both drive with a constant heading and a field-relative heading.
 */
@Config
public class DriveTrain {
    // --- Public Configuration Variables ---
    public static axisBehavior lateralAxis = axisBehavior.LEFT_STICK_X;
    public static axisBehavior axialAxis = axisBehavior.LEFT_STICK_Y;
    public static axisBehavior yawAxis = axisBehavior.RIGHT_STICK_X;
    public static GamepadButton resetIMUButton = GamepadButton.X;
    public static GamepadButton lowSpeedButton = GamepadButton.RIGHT_BUMPER;
    public static GamepadButton toggleDriveModeButton = GamepadButton.Y;
    public static boolean toggleDriveModeButtonDisabled = false;
    public static boolean resetIMUButtonDisabled = false;
    public static double lateralGain = 1.0;
    public static double axialGain = 1.0;
    public static double yawGain = 3.0;
    public static double yawMultiplier = 0.5;
    public static double speedMultiplier = 0.9;
    public static double lowSpeedMultiplier = 0.5;

    // --- Private Subsystem Components ---
    private final BNO055IMUNew imu;
    private final DcMotor frontLeft;
    private final DcMotor frontRight;
    private final DcMotor backLeft;
    private final DcMotor backRight;
    private final GamepadController gamepad;

    // --- State Variables ---
    private boolean isFieldOrientedMode = true;

    /**
     * Initializes the IMU and motors for use.
     *
     * @param opmode     the OpMode from any TeleOp Class
     * @param controller the controller to be used for user input
     */
    public DriveTrain(OpMode opmode, GamepadController controller) {
        gamepad = controller;
        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "BNO55",
        // and named "imu 1".
        imu = opmode.hardwareMap.get(BNO055IMUNew.class, "imu 1");
        BNO055IMUNew.Parameters parameters = new BNO055IMUNew.Parameters(new RevHubOrientationOnRobot(PARAMS.logoFacingDirection, PARAMS.usbFacingDirection));
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // precalibrated data to increase accuracy
        imu.initialize(parameters); //actually starts the IMU

        // Retrieve and initialize the motors
        frontLeft = opmode.hardwareMap.get(DcMotorEx.class, "leftFront");
        frontRight = opmode.hardwareMap.get(DcMotorEx.class, "rightFront");
        backLeft = opmode.hardwareMap.get(DcMotorEx.class, "leftBack");
        backRight = opmode.hardwareMap.get(DcMotorEx.class, "rightBack");

        //Set left motors to reverse, and all to brake mode when power is zero
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //Lets motors run freely unlike in Autonomous where their position needs to be tracked
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //Configure axes and buttons from the gamepad
        controller.configureAxis(lateralAxis);
        controller.configureAxis(axialAxis);
        controller.configureAxis(yawAxis);
        controller.configureBiStateButton(lowSpeedButton, BiStateButtonBehavior.HOLD);
        controller.configureBiStateButton(toggleDriveModeButton, BiStateButtonBehavior.TOGGLE);
        controller.configureBiStateButton(resetIMUButton, BiStateButtonBehavior.HOLD);
    }

    /**
     * This method is how the robot knows where it is at all times, by knowing where it isn't.
     * <p>
     * By taking the magnitude of the lateral(x-axis) and axial(y-axis) movement, and adding the
     * IMU heading, we can calculate the compensation in radians needed to always keep the robot moving
     * forward regardless of heading. Then by calculating the magnitude of the stick inputs, we now
     * have the polar coordinates of how the robot should move.
     * Then, we can convert these polar coordinates back to rectangular coordinates using the
     * pythagorean theorem which allows us to calculate the power required to move the motors
     * in relation to our stick inputs.
     */
    private void doFieldOrientedDrive() {


        final double lateral = speedMultiplier * getProcessedAxisValue(lateralAxis, lateralGain);
        final double axial = speedMultiplier * -getProcessedAxisValue(axialAxis, axialGain);
        final double yaw = yawMultiplier * getProcessedAxisValue(yawAxis, yawGain);

        //convert rectangular stick inputs to polar coordinates
        final double heading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
        final double direction = -(Math.atan2(lateral, axial) + heading);//find angle theta to compensate
        final double speed = Math.min(1.0, Math.sqrt(lateral * lateral + axial * axial));//find radius(magnitude)
        //Rotate movement vectors by 45 degrees to align with mecanum wheel rollers
        final double vCos = speed * Math.cos(direction + Math.PI / 4.0);//convert back to rectangular coordinates(x-axis)
        final double vSin = speed * Math.sin(direction + Math.PI / 4.0);//convert back to rectangular coordinates(y-axis)
        //These 4 lines calculate the motor powers
        double lf = vCos + yaw;
        double rf = vSin - yaw;
        double lr = vSin + yaw;
        double rr = vCos - yaw;

        if (gamepad.getGamepadButtonValue(lowSpeedButton)) {
            lf *= lowSpeedMultiplier;
            rf *= lowSpeedMultiplier;
            lr *= lowSpeedMultiplier;
            rr *= lowSpeedMultiplier;

        }
        //Actually what makes the robot moves
        setMotorPowers(lf, rf, lr, rr);
    }


    /**
     * This method keeps the heading constant with the front of the drivetrain, rather than the field
     * Because of this, no trig is needed
     */
    private void doClassicMecanumDrive() {
        // Omni Mode uses right joystick to go forward & strafe, and left joystick to rotate.
        //Just like a drone
        //I decided to limit precision to 4 decimal places to counteract drift
        final double lateral = speedMultiplier * getProcessedAxisValue(lateralAxis, lateralGain);
        final double axial = speedMultiplier * -getProcessedAxisValue(axialAxis, axialGain);
        final double yaw = yawMultiplier * getProcessedAxisValue(yawAxis, yawGain);

        //these are the magic 4 statements right here
        // Combine the joystick requests for each axis-motion to determine each wheel's power.
        // Set up a variable for each drive wheel to save the power level for telemetry.
        double leftFrontPower = axial + lateral + yaw;
        double rightFrontPower = axial - lateral - yaw;
        double leftBackPower = axial - lateral + yaw;
        double rightBackPower = axial + lateral - yaw;

        // Normalize the values so no wheel power exceeds 100%
        // This ensures that the robot maintains the desired motion.
        double max;
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
        if (gamepad.getGamepadButtonValue(lowSpeedButton)) {
            leftFrontPower *= lowSpeedMultiplier;
            rightFrontPower *= lowSpeedMultiplier;
            leftBackPower *= lowSpeedMultiplier;
            rightBackPower *= lowSpeedMultiplier;
        }

        frontLeft.setPower(leftFrontPower);
        frontRight.setPower(rightFrontPower);
        backLeft.setPower(leftBackPower);
        backRight.setPower(rightBackPower);
    }

    /**
     * Sets the zero power behavior of the motors.
     *
     * @param zeroPowerBehavior The desired zero power behavior
     */
    public void setBrakingMode(DcMotor.ZeroPowerBehavior zeroPowerBehavior) {
        frontLeft.setZeroPowerBehavior(zeroPowerBehavior);
        backLeft.setZeroPowerBehavior(zeroPowerBehavior);
        frontRight.setZeroPowerBehavior(zeroPowerBehavior);
        backRight.setZeroPowerBehavior(zeroPowerBehavior);
    }

    /**
     * Actually executes the necessary logic to move the drivetrain, should be ran in the main loop
     * so that the robot can keep moving and updates as often as possible
     */
    public void updateDriveTrainBehavior() {
        if (!resetIMUButtonDisabled && gamepad.getGamepadButtonValue(resetIMUButton)) {
            imu.resetYaw();
        }

        // The TOGGLE button flips its state each press. We use this to switch our drive mode.
        isFieldOrientedMode = toggleDriveModeButtonDisabled || !gamepad.getGamepadButtonValue(toggleDriveModeButton);

        if (isFieldOrientedMode) {
            doFieldOrientedDrive();
        } else {
            doClassicMecanumDrive();
        }
    }

    /**
     * Processes raw joystick input by applying a gain curve and truncating to 4 decimal places.
     *
     * @param axis The controller axis to read from.
     * @param gain The exponent to apply to the input, for adding a response curve.
     * @return The processed joystick value.
     */
    private double getProcessedAxisValue(axisBehavior axis, double gain) {
        double rawValue = gamepad.getAxisValue(axis);
        // Truncate to 4 decimal places to reduce joystick drift
        double truncatedValue = (int) (rawValue * 10000) / 10000.0;
        return Math.pow(truncatedValue, gain);
    }


    /**
     * Sets the power for all four drive motors, applying the half-speed multiplier if active.
     */
    public void setMotorPowers(double lf, double rf, double lb, double rb) {
        boolean lowSpeed = gamepad.getGamepadButtonValue(lowSpeedButton);
        double currentSpeedMultiplier = lowSpeed ? lowSpeedMultiplier : 1.0;

        frontLeft.setPower(lf * currentSpeedMultiplier);
        frontRight.setPower(rf * currentSpeedMultiplier);
        backLeft.setPower(lb * currentSpeedMultiplier);
        backRight.setPower(rb * currentSpeedMultiplier);
    }

    /**
     * Ensures power is killed to all motors once the match ends
     */
    public void stop() {
        setMotorPowers(0, 0, 0, 0);
    }
}
