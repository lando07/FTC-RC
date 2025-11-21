package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.teamcode.subsystems.DriveTrain;
import org.firstinspires.ftc.teamcode.subsystems.GamepadController;
import org.firstinspires.ftc.teamcode.subsystems.axisBehavior;

/**
 * This year's TeleOp for the robot
 */
@Config
@TeleOp(name = "XDriveDECODE", group = "Robot")
public class XDriveDECODE extends OpMode {
    // Motors
    private DriveTrain driveTrain;
    private GamepadController controller1, controller2;
    private DcMotor shooterMotor;
    private DcMotor intakeMotor;

    // Servos
    private Servo servo1, servo2, servo3, servo4;

    // Servo Positions
    public static double SERVO_FORWARD_POS = 1.0;
    public static double SERVO_REVERSE_POS = 0.0;
    public static double SERVO_NEUTRAL_POS = 0.5;

    // --- Shooter Power and Voltage Compensation ---
    // 1. SET YOUR SHOOTER POWER HERE (e.g., 0.80 for 80%)
    public static double SHOOTER_POWER_SETTING = 0.80;

    private VoltageSensor batteryVoltageSensor;
    public static double NOMINAL_VOLTAGE = 12.5; // The baseline voltage for compensation

    @Override
    public void init() {
        // Gamepad and Drivetrain Initialization
        controller1 = new GamepadController(gamepad1);
        controller2 = new GamepadController(gamepad2);
        driveTrain = new DriveTrain(this, controller1);

        // Controller 2 Trigger Configuration
        controller2.configureAxis(axisBehavior.RIGHT_TRIGGER);
        controller2.configureAxis(axisBehavior.LEFT_TRIGGER);

        // --- Hardware Initialization ---
        shooterMotor = hardwareMap.get(DcMotor.class, "shooterMotor");
        shooterMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");

        servo1 = hardwareMap.get(Servo.class, "servo1");
        servo2 = hardwareMap.get(Servo.class, "servo2");
        servo3 = hardwareMap.get(Servo.class, "servo3");
        servo4 = hardwareMap.get(Servo.class, "servo4");

        // Initialize the VoltageSensor from the hardware map
        batteryVoltageSensor = hardwareMap.voltageSensor.iterator().next();

        telemetry.addData("Status", "Initialized");
        telemetry.addData("Shooter Power Set To", "%.0f%%", SHOOTER_POWER_SETTING * 100);
        telemetry.update();
    }

    @Override
    public void init_loop() {
        //Not needed
    }

    @Override
    public void start() {
        driveTrain.setBrakingMode(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @Override
    public void loop() {
        // Update controllers
        controller1.update();
        controller2.update();

        // Update drivetrain
        driveTrain.updateDriveTrainBehavior();

        // --- Shooter Motor Logic with Fixed Power and Voltage Compensation ---
        double shooterPower = 0.0;

        // 2. Triggers now act as on/off buttons for the fixed power setting
        if (gamepad2.right_trigger > 0.1) { // Fire forward
            shooterPower = SHOOTER_POWER_SETTING;
        } else if (gamepad2.left_trigger > 0.1) { // Fire reverse
            shooterPower = -SHOOTER_POWER_SETTING;
        }

        // 3. Apply voltage compensation
        double currentVoltage = batteryVoltageSensor.getVoltage();
        if (currentVoltage < 8.0) { // Safety check
            currentVoltage = NOMINAL_VOLTAGE;
        }
        double voltageCompensationFactor = NOMINAL_VOLTAGE / currentVoltage;
        double compensatedShooterPower = shooterPower * voltageCompensationFactor;

        // Clip the final power to the valid range [-1.0, 1.0]
        compensatedShooterPower = Math.max(-1.0, Math.min(1.0, compensatedShooterPower));

        shooterMotor.setPower(compensatedShooterPower);

        // Set power for intake motor (A/B buttons)
        double intakePower = 0;
        if (gamepad2.a) {
            intakePower = 1.0;
        } else if (gamepad2.b) {
            intakePower = -1.0;
        }
        intakeMotor.setPower(intakePower);

        // Set positions for the four servos based on bumpers
        if (gamepad2.left_bumper) {
            servo1.setPosition(SERVO_FORWARD_POS);
            servo4.setPosition(SERVO_REVERSE_POS);
            servo2.setPosition(SERVO_FORWARD_POS);
            servo3.setPosition(SERVO_REVERSE_POS);
        } else if (gamepad2.right_bumper) {
            servo1.setPosition(SERVO_REVERSE_POS);
            servo4.setPosition(SERVO_FORWARD_POS);
            servo2.setPosition(SERVO_REVERSE_POS);
            servo3.setPosition(SERVO_FORWARD_POS);
        } else {
            servo1.setPosition(SERVO_NEUTRAL_POS);
            servo2.setPosition(SERVO_NEUTRAL_POS);
            servo3.setPosition(SERVO_NEUTRAL_POS);
            servo4.setPosition(SERVO_NEUTRAL_POS);
        }

        // --- Update Telemetry ---
        telemetry.addData("Shooter Power Setting", "%.0f%%", SHOOTER_POWER_SETTING * 100);
        telemetry.addData("Compensated Power", "%.2f (Active)", compensatedShooterPower);
        telemetry.addData("Battery Voltage", "%.2f V", currentVoltage);
        telemetry.addData("Intake Power", intakePower);
        telemetry.addData("Servo 1/4 Pos", servo1.getPosition());
        telemetry.addData("Servo 2/3 Pos", servo2.getPosition());
    }

    @Override
    public void stop() {
        driveTrain.stop();
        shooterMotor.setPower(0);
        intakeMotor.setPower(0);

        // Ensure servos stop moving
        servo1.setPosition(SERVO_NEUTRAL_POS);
        servo2.setPosition(SERVO_NEUTRAL_POS);
        servo3.setPosition(SERVO_NEUTRAL_POS);
        servo4.setPosition(SERVO_NEUTRAL_POS);
    }
}
