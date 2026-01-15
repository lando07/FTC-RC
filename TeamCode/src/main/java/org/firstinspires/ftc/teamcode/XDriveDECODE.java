package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.subsystems.DriveTrain;
import org.firstinspires.ftc.teamcode.subsystems.FeedServoLauncher;
import org.firstinspires.ftc.teamcode.subsystems.GamepadController;
import org.firstinspires.ftc.teamcode.subsystems.enums.AxisBehavior;
import org.firstinspires.ftc.teamcode.subsystems.enums.BiStateButtonBehavior;
import org.firstinspires.ftc.teamcode.subsystems.enums.GamepadButton;

/**
 * This year's TeleOp for the robot
 * @author Thu
 * @author Mentor Landon Smith
 */
@Config
@TeleOp(name = "XDriveDECODE", group = "Robot")
public class XDriveDECODE extends OpMode {
    /**
     * Stores the drivetrain subsystem object which handles all movement and motor power
     */
    private DriveTrain driveTrain;
    /**
     * Stores the controller keybinds and configurations
     */
    private GamepadController controller1, controller2;
    private FeedServoLauncher feedServos;
    private DcMotorEx shooterMotor;
    private DcMotorEx intakeMotor;
    public static AxisBehavior launcherAxis = AxisBehavior.RIGHT_TRIGGER;
    public static AxisBehavior reverseLauncherAxis = AxisBehavior.LEFT_TRIGGER;
    public static GamepadButton feedForwardButton = GamepadButton.A;
    public static GamepadButton feedBackwardButton = GamepadButton.B;

    // --- Shooter Power and Voltage Compensation ---
    // 1. SET YOUR SHOOTER POWER HERE (e.g., 0.80 for 80%)
    public static double SHOOTER_POWER_SETTING = .60;

    private VoltageSensor batteryVoltageSensor;
    public static double NOMINAL_VOLTAGE = 12.5; // The baseline voltage for compensation

    public static double targetVelocity = 400;

    private double compensatedShooterPower;
    private double currentVoltage;
    private double intakePower;


    @Override
    public void init() {
        //Initialize subsystems and controllers here
        //Always create objects before configuring them, or you will get
        //a null pointer exception
        controller1 = new GamepadController(gamepad1);
        controller2 = new GamepadController(gamepad2);
        driveTrain = new DriveTrain(this, controller1);

        // Controller 2 Trigger Configuration
        controller2.configureAxis(launcherAxis);
        controller2.configureAxis(reverseLauncherAxis);
        controller2.configureBiStateButton(feedForwardButton, BiStateButtonBehavior.HOLD);
        controller2.configureBiStateButton(feedBackwardButton, BiStateButtonBehavior.HOLD);
        // --- Hardware Initialization ---
        shooterMotor = hardwareMap.get(DcMotorEx.class, "shooterMotor");
        shooterMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        feedServos = new FeedServoLauncher(this, controller2);
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");

        // Initialize the VoltageSensor from the hardware map
        batteryVoltageSensor = hardwareMap.voltageSensor.iterator().next();

        telemetry.addData("Status", "Initialized");
//        telemetry.addData("Shooter Power Set To", "%.0f%%", SHOOTER_POWER_SETTING * 100);
        telemetry.update();
    }

    @Override
    public void init_loop() {
        //Not needed
    }

    @Override
    public void start() {
        driveTrain.setBrakingMode(DcMotor.ZeroPowerBehavior.BRAKE);
        shooterMotor.setPower(1);
        shooterMotor.setVelocity(targetVelocity, AngleUnit.DEGREES);

    }
    //NOTE: Due to the way this codebase is designed, loop() should only be running subsystems or very primitive motor
    //controls, such as setting power.
    @Override
    public void loop() {
        // Update controllers
        controller1.update();
        controller2.update();

        // Update drivetrain
        driveTrain.updateDriveTrainBehavior();
        computeIntakeMotorDirection();
        computeShooterMotorVelocity();
//        doShooterMotorWithVoltageCompensation();
        // Set positions for the four servos based on bumpers
        feedServos.updateFeedServoLauncherBehavior();

        // --- Update Telemetry ---
        telemetry.addData("Shooter Power Setting", "%.0f%%", SHOOTER_POWER_SETTING * 100);
        telemetry.addData("Compensated Power", "%.2f (Active)", compensatedShooterPower);
        telemetry.addData("Battery Voltage", "%.2f V", currentVoltage);
        telemetry.addData("Intake Power", intakePower);
        telemetry.addData("Left Servo Pos: ", feedServos.getLeftServoPositions());
        telemetry.addData("Right Servo Pos", feedServos.getRightServoPositions());
        telemetry.addData("Launch Motor speed (deg/s): ", shooterMotor.getVelocity(AngleUnit.DEGREES));
    }
    private void computeIntakeMotorDirection(){
        // Set power for intake motor (A/B buttons)
        intakePower = 0;
        if (controller2.getGamepadButtonValue(feedForwardButton)) {
            intakePower = 1.0;
        } else if (controller2.getGamepadButtonValue(feedBackwardButton)) {
            intakePower = -1.0;
        }
        intakeMotor.setPower(intakePower);
    }

    private void computeShooterMotorVelocity() {
        shooterMotor.setVelocity(controller2.getAxisValue(launcherAxis)*targetVelocity, AngleUnit.DEGREES);
    }
    private void doShooterMotorWithVoltageCompensation(){
        // --- Shooter Motor Logic with Fixed Power and Voltage Compensation ---
        double shooterPower = 0.0;

        // 2. Triggers now act as on/off buttons for the fixed power setting
        if (controller2.getAxisValue(launcherAxis) > 0.1) { // Fire forward
            shooterPower = SHOOTER_POWER_SETTING;
        } else if (controller2.getAxisValue(reverseLauncherAxis) > 0.1) { // Fire reverse
            shooterPower = -SHOOTER_POWER_SETTING;
        }

        // 3. Apply voltage compensation
        currentVoltage = batteryVoltageSensor.getVoltage();
        if (currentVoltage < 8.0) { // Safety check
            currentVoltage = NOMINAL_VOLTAGE;
        }
        double voltageCompensationFactor = NOMINAL_VOLTAGE / currentVoltage;
        compensatedShooterPower = shooterPower * voltageCompensationFactor;

        // Clip the final power to the valid range [-1.0, 1.0]
        compensatedShooterPower = Math.max(-1.0, Math.min(1.0, compensatedShooterPower));

        shooterMotor.setPower(compensatedShooterPower);
    }

    @Override
    public void stop() {
        driveTrain.stop();
        shooterMotor.setPower(0);
        intakeMotor.setPower(0);

        // Ensure servos stop moving
        feedServos.stop();
    }
}