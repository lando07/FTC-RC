package org.firstinspires.ftc.teamcode.TeleOp.DECODE;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.subsystems.Universal.DriveTrain;
import org.firstinspires.ftc.teamcode.subsystems.DECODEExclusive.FeedServoLauncher;
import org.firstinspires.ftc.teamcode.subsystems.Universal.GamepadController;
import org.firstinspires.ftc.teamcode.subsystems.enums.AxisBehavior;

/**
 * This year's TeleOp for the robot
 *
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
    private DcMotorEx intakeMotor2;
    public static AxisBehavior launcherAxis = AxisBehavior.LEFT_TRIGGER;
    public static AxisBehavior reverseLauncherAxis = AxisBehavior.RIGHT_TRIGGER;
    public static AxisBehavior intereriorIntakeMotorAxis = AxisBehavior.RIGHT_STICK_Y;
    public static AxisBehavior exteriorIntakeMotorAxis = AxisBehavior.LEFT_STICK_Y;



    // --- Shooter Power and Voltage Compensation ---
    // 1. SET YOUR SHOOTER POWER HERE (e.g., 0.80 for 80%)
    public static double SHOOTER_POWER_SETTING = 1;

    public static double targetVelocity = -450;

    private double intakePower;

    private double intakePower2;


    @Override
    public void init() {
        //Initialize subsystems and controllers here
        //Always create objects before configuring them, or you will get
        //a null pointer exception
        controller1 = new GamepadController(gamepad1);
        controller2 = new GamepadController(gamepad2);
        driveTrain = new DriveTrain(this, controller1);

        // Controller 2 Trigger Configuration
        controller2.configureAxis(intereriorIntakeMotorAxis);
        controller2.configureAxis(exteriorIntakeMotorAxis);
        controller2.configureAxis(launcherAxis);
//        controller2.configureBiStateButton(feedForwardButton, BiStateButtonBehavior.HOLD);
//        controller2.configureBiStateButton(feedBackwardButton, BiStateButtonBehavior.HOLD);
        // --- Hardware Initialization ---
        intakeMotor2 = hardwareMap.get(DcMotorEx.class, "intakeMotor2");
        shooterMotor = hardwareMap.get(DcMotorEx.class, "shooterMotor");
        shooterMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        feedServos = new FeedServoLauncher(this, controller2);
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");
        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    /**
     * Not needed, but used to continuously run code before the user presses play
     */
    @Override
    public void init_loop() {
        //Not needed
    }

    /**
     * Code to run once when the user presses play. Typically just sets certain subsystem states
     * that can't be set during initialization
     */
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
        computeIntake2MotorDirection();
        computeShooterMotorVelocity();
        feedServos.updateFeedServoLauncherBehavior();

        // --- Update Telemetry ---
        telemetry.addData("Shooter Power Setting", "%.0f%%", SHOOTER_POWER_SETTING * 100);
        telemetry.addData("Intake Power: ", intakePower);
        telemetry.addData("Intake Power 2: ", intakePower2);
        telemetry.addData("Left Servo Pos: ", feedServos.getLeftServoPower());
        telemetry.addData("Right Servo Pos", feedServos.getRightServoPower());
        telemetry.addData("Launch Motor speed (deg/s): ", shooterMotor.getVelocity(AngleUnit.DEGREES));
        telemetry.addData("ForwardLauncherAxis", controller2.getAxisValue(launcherAxis));
        telemetry.addData("ReverseLauncherAxis: ", controller2.getAxisValue(reverseLauncherAxis));
    }
    private void computeIntakeMotorDirection(){
        int feedState = controller2.getTristateButtonValue(FeedServoLauncher.feedForwardButton);
        intakePower = controller2.getAxisValue(intereriorIntakeMotorAxis);
        if (feedState != 0) {
            intakePower = feedState;
        }
        intakeMotor.setPower(intakePower*1.2);
    }


    private void computeIntake2MotorDirection(){
        int feedState = controller2.getTristateButtonValue(FeedServoLauncher.feedForwardButton);
        intakePower2 = controller2.getAxisValue(exteriorIntakeMotorAxis);
        if (feedState != 0) {
            intakePower2 = feedState;
        }
        intakeMotor2.setPower(intakePower2*1.2);
    }

    private void computeShooterMotorVelocity() {
        if(controller2.getAxisValue(launcherAxis) > 0.1) {
            shooterMotor.setVelocity(controller2.getAxisValue(launcherAxis) * targetVelocity, AngleUnit.DEGREES);
        }
        else if(controller2.getAxisValue(reverseLauncherAxis) > 0.1){
            shooterMotor.setVelocity(-controller2.getAxisValue(reverseLauncherAxis) * targetVelocity, AngleUnit.DEGREES);
        }
        else{
            shooterMotor.setVelocity(0);
        }
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
