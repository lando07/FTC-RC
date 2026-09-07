package org.firstinspires.ftc.teamcode.subsystems.Universal;

import static org.firstinspires.ftc.teamcode.RoadRunner.MecanumDrive.PARAMS;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.bosch.BNO055IMUNew;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.subsystems.enums.AxisBehavior;
import org.firstinspires.ftc.teamcode.subsystems.enums.BiStateButtonBehavior;
import org.firstinspires.ftc.teamcode.subsystems.enums.GamepadButton;

/**
 * This is the main class to control the 4 motors on the drive train. It has dual-functionality,
 * since it can both drive with a constant heading and a field-relative heading.
 * @author Landon Smith
 */
@Config
public class DriveTrain {
    // --- Public Configuration Variables ---
    /**
     * Axis to strafe left and right
     */
    public static AxisBehavior lateralAxis = AxisBehavior.LEFT_STICK_X;
    /**
     * Axis to move forwards and backwards
     */
    public static AxisBehavior axialAxis = AxisBehavior.LEFT_STICK_Y;
    /**
     * Axis to rotate clockwise or counterclockwise
     */
    public static AxisBehavior yawAxis = AxisBehavior.RIGHT_STICK_X;
    /**
     * Button to reset heading of robot(only applies to field-centric mode)
     */
    public static GamepadButton resetIMUButton = GamepadButton.X;
    /**
     * Button to limit top speed of robot for greater precision
     */
    public static GamepadButton lowSpeedButton = GamepadButton.RIGHT_BUMPER;
    /**
     * Button to switch between field and robot-centric mode
     */
    public static GamepadButton toggleDriveModeButton = GamepadButton.Y;
    /**
     * DO NOT CHANGE, Unless odometry pod location has changed
     */
    public static GoBildaPinpointDriver.EncoderDirection xEncoderDirection = GoBildaPinpointDriver.EncoderDirection.FORWARD;
    /**
     * DO NOT CHANGE, Unless odometry pod location has changed
     */
    public static GoBildaPinpointDriver.EncoderDirection yEncoderDirection = GoBildaPinpointDriver.EncoderDirection.FORWARD;
    //The accuracy of the goBilda Pinpoint module is far, far better than even the BNO55 IMU, and blows
    // the BHI260 IMU out of the water, so we use it now.
    /**
     * DO NOT CHANGE, ask Landon first before modifying
     */
    public static boolean usePinpointIMU = true;
    /**
     * Set to true to lock a specific drive mode, either field or robot centric
     */
    public static boolean toggleDriveModeButtonDisabled = false;
    /**
     * Set to true if resetIMUButton is being erroneously pressed(only applies to field-centric mode)
     */
    public static boolean resetIMUButtonDisabled = false;
    /**
     * Higher odd numbers make the stick respond little at first, then ramp to max increasingly fast.
     * In other words, changes response curve of left-right movement, NUMBER MUST BE ODD
     */
    public static double lateralGain = 1.0;
    /**
     * Higher odd numbers make the stick respond little at first, then ramp to max increasingly fast.
     * In other words, changes response curve of forward-backward movement, NUMBER MUST BE ODD
     */
    public static double axialGain = 1.0;
    /**
     * Higher odd numbers make the stick respond little at first, then ramp to max increasingly fast.
     * In other words, changes response curve of clockwise-counterclockwise movement, NUMBER MUST BE ODD
     */
    public static double yawGain = 3.0;
    /**
     * Maximum speed when lowSpeedButton is pressed for clockwise-counterclockwise rotation
     */
    public static double yawMultiplier = 0.5;
    /**
     * Maximum speed for non-rotational movement on field, when lowSpeedButton is not pressed
     */
    public static double speedMultiplier = 1;
    /**
     * Maximum speed when lowSpeedButton is pressed for non-rotational movement
     */
    public static double lowSpeedMultiplier = 0.5;
    /**
     * Used to calculate the minimum change on the joystick needed to compute the new stick inputs,
     * reduces input lag by bypassing the calculations if the change in input is not noticeable to the
     * driver.
     * DO NOT CHANGE, consult Landon first
     */
    public static double minUserInputDelta = 0.001;
    /**
     * Name for the left front motor, MUST BE LEFT FRONT MOTOR
     */
    private final String leftFront = "leftFront";
    /**
     * Name for the right front motor, MUST BE RIGHT FRONT MOTOR
     */
    private final String rightFront = "rightFront";
    /**
     * Name for the right front motor, MUST BE LEFT BACK MOTOR
     */
    private final String leftBack = "leftBack";
    /**
     * Name for the right front motor, MUST BE RIGHT BACK MOTOR
     */
    private final String rightBack = "rightBack";

    // --- Private Subsystem Components ---
    private GoBildaPinpointDriver pinpoint;
    private BNO055IMUNew imu;
    private final DcMotor frontLeft;
    private final DcMotor frontRight;
    private final DcMotor backLeft;
    private final DcMotor backRight;
    private final GamepadController gamepad;
    private volatile double prevAxialInput;
    private volatile double prevLateralInput;
    private volatile double prevYawInput;

    // --- State Variables ---
    /**
     * Change this for the default mode when starting an TeleOp OpMode.
     * false is default robot-oriented, true is default field-oriented
     */
    public static boolean isFieldOrientedMode = false;

    /**
     * Initializes the IMU and motors for use.
     *
     * @param opMode     the OpMode from any TeleOp Class
     * @param controller the controller to be used for user input
     */
    public DriveTrain(OpMode opMode, GamepadController controller) {
        gamepad = controller;
        //Branch condition depending on current IMU mode(prefer pinpoint due to drastically higher accuracy)
        if (usePinpointIMU) {
            pinpoint = opMode.hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
            pinpoint.setErrorDetectionType(GoBildaPinpointDriver.ErrorDetectionType.CRC);
            pinpoint.setOffsets(3.81, 15.765, DistanceUnit.CM);
            pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
            pinpoint.setEncoderDirections(xEncoderDirection, yEncoderDirection);
            pinpoint.resetPosAndIMU();
        }
        else {
            // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
            // on a Core Device Interface Module, configured to be a sensor of type "BNO55",
            // and named "imu 1".
            imu = opMode.hardwareMap.get(BNO055IMUNew.class, "imu 1");
            BNO055IMUNew.Parameters parameters = new BNO055IMUNew.Parameters(new RevHubOrientationOnRobot(PARAMS.logoFacingDirection, PARAMS.usbFacingDirection));
            parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // precalibrated data to increase accuracy
            imu.initialize(parameters); //actually starts the IMU
        }

        // Retrieve and initialize the motors
        frontLeft = opMode.hardwareMap.get(DcMotorEx.class, leftFront);
        frontRight = opMode.hardwareMap.get(DcMotorEx.class, rightFront);
        backLeft = opMode.hardwareMap.get(DcMotorEx.class, leftBack);
        backRight = opMode.hardwareMap.get(DcMotorEx.class, rightBack);

        //Set left motors to reverse, and all to brake mode when power is zero
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
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
     * By taking the inverse tangent of the lateral(x-axis) and axial(y-axis) movement, and adding the
     * IMU heading, we can calculate the compensation in radians needed to always keep the robot moving
     * forward regardless of heading. By then calculating the magnitude of the stick inputs, we can
     * calculate the velocity of the robot in said direction.
     * Then, we can convert the polar vector into 4 motor powers by taking the sine and cosine of our
     * speed(magnitude) and our direction for each axis(cos for x, sin for y) and add or subtract
     * our yaw input(yaw only needs left motors to move at a different velocity than the right motors)
     * to calculate the power to send to each motor so the stick inputs given match the requested movement.
     * </p>
     */
    private void doFieldOrientedDrive() {

        //Collect joystick inputs
        final double lateral = speedMultiplier * -getProcessedAxisValue(lateralAxis, lateralGain);
        final double axial = speedMultiplier * getProcessedAxisValue(axialAxis, axialGain);
        final double yaw = yawMultiplier * -getProcessedAxisValue(yawAxis, yawGain);
        //store as previous inputs to be compared on subsequent calls to updateDriveTrainBehavior()
        prevLateralInput = lateral;
        prevAxialInput = axial;
        prevYawInput = yaw;

        double heading;
        //Branch condition if using pinpoint imu
        if (usePinpointIMU) {
            heading = pinpoint.getHeading(AngleUnit.RADIANS);
        }
        else {
            heading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        }
        //convert rectangular stick inputs to polar coordinates
        final double direction = -(Math.atan2(lateral, axial) + heading);//find angle theta to compensate
        final double speed = Math.min(1.0, Math.sqrt(lateral * lateral + axial * axial));//find radius(magnitude)
        //Rotate movement vectors by 45 degrees to align with mecanum wheel rollers
        final double vCos = speed * Math.cos(direction + Math.PI / 4.0);//convert back to rectangular coordinates(x-axis)
        final double vSin = speed * Math.sin(direction + Math.PI / 4.0);//convert back to rectangular coordinates(y-axis)
        //These 4 lines calculate the motor powers
        //Add yaw to the left half, subtract from the right half
        double lf = vCos + yaw;
        double rf = vSin - yaw;
        double lr = vSin + yaw;
        double rr = vCos - yaw;
        //Now we check to cut speed by our configure multiplier
        if (gamepad.getGamepadButtonValue(lowSpeedButton)) {
            lf *= lowSpeedMultiplier;
            rf *= lowSpeedMultiplier;
            lr *= lowSpeedMultiplier;
            rr *= lowSpeedMultiplier;

        }
        //This is what actually sends the final motor powers to the motors
        setMotorPowers(lf, rf, lr, rr);
    }


    /**
     * This method keeps the robot heading aligned with the front of the drivetrain, rather than the field.
     * It's still holonomic motion(robot can freely strafe without steering),
     * but a forward stick input moves the front of the robot forward, not the robot
     * forward relative to the field, or last-set zero-position
     */
    private void doClassicMecanumDrive() {
        // Omni Mode uses right joystick to go forward & strafe, and left joystick to rotate.
        //Just like a drone
        final double lateral = speedMultiplier * -getProcessedAxisValue(lateralAxis, lateralGain);
        final double axial = speedMultiplier * getProcessedAxisValue(axialAxis, axialGain);
        final double yaw = yawMultiplier * -getProcessedAxisValue(yawAxis, yawGain);
        //store as previous inputs to be compared on subsequent calls to updateDriveTrainBehavior()
        prevLateralInput = lateral;
        prevAxialInput = axial;
        prevYawInput = yaw;

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

        setMotorPowers(leftFrontPower, rightFrontPower, leftBackPower, rightBackPower);
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
            if (usePinpointIMU) {
                pinpoint.setHeading(0,AngleUnit.DEGREES);
            }
            else {
                imu.resetYaw();
            }
        }

        // The TOGGLE button flips its state each press. We use this to switch our drive mode.
        if(!toggleDriveModeButtonDisabled){
            isFieldOrientedMode = gamepad.getGamepadButtonValue(toggleDriveModeButton);
        }
        //We check here for non-negligable stick input change, and only then do we run line 259 or 261 to change the dt state
        if(Math.abs(getProcessedAxisValue(lateralAxis, lateralGain) - prevLateralInput) > minUserInputDelta ||
           Math.abs(getProcessedAxisValue(axialAxis, axialGain) - prevAxialInput) > minUserInputDelta ||
           Math.abs(getProcessedAxisValue(yawAxis, yawGain) - prevYawInput) > minUserInputDelta)
        {
            if (isFieldOrientedMode) {
                doFieldOrientedDrive();
            } else {
                doClassicMecanumDrive();
            }
        }
    }

    /**
     * Processes raw joystick input by applying a gain curve and truncating to 4 decimal places.
     *
     * @param axis The controller axis to read from.
     * @param gain The exponent to apply to the input, for adding a response curve.
     * @return The processed joystick value.
     */
    private double getProcessedAxisValue(AxisBehavior axis, double gain) {
        double rawValue = gamepad.getAxisValue(axis);
        // Truncate to 4 decimal places to reduce joystick drift
        double truncatedValue = (int) (rawValue * 10000) / 10000.0;
        return Math.pow(truncatedValue, gain);
    }


    /**
     * Sets the power for all four drive motors, applying the half-speed multiplier if active.
     */
    private void setMotorPowers(double lf, double rf, double lb, double rb) {
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
