package org.firstinspires.ftc.teamcode.TeleOp.DECODE;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.subsystems.DriveTrain;
import org.firstinspires.ftc.teamcode.subsystems.FeedServoLauncher;
import org.firstinspires.ftc.teamcode.subsystems.GamepadController;
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
    public static AxisBehavior launcherAxis = AxisBehavior.RIGHT_TRIGGER;
    public static AxisBehavior reverseLauncherAxis = AxisBehavior.LEFT_TRIGGER;
    public static AxisBehavior intereriorIntakeMotorAxis = AxisBehavior.LEFT_STICK_Y;
    public static AxisBehavior exteriorIntakeMotorAxis = AxisBehavior.RIGHT_STICK_Y;
    public static double slowSpeedIntakeModifier = 0.5; //50%, unused for now
    /**
     * Target velocity in degrees per second on the motor output shaft, not the shooter flywheel
     */
    public static double targetVelocity = 400;
    /**
     * Stores the current power of the exterior intake motors
     */
    private double intakePower = 0;
    /**
     * Stores the previous power of the interior intake motor, only updated when the {@code minimumInputDelta} has been reached
     */
    private double prevIntakePower = 0;
    /**
     * Stores the current power of the exterior intake motor
     */
    private double intakePower2 = 0;
    /**
     * Stores the previous power of the exterior intake motor, only updated when the {@code minimumInputDelta} has been reached
     */
    private double prevIntakePower2 = 0;
    /**
     * Stores the previous state of the shooter motor input given by the user, only updated when the {@code minimumInputDelta} has been reached
     */
    private double prevShooterMotorInput = 0;
    /**
     * Minimum change in user input on the triggers before motor speed on the launcher
     * motor is updated
     */
    public static double minimumInputDelta = 0.01;

    /**
     * Initialization method, called once when the opmode is started, before pressing play
     */
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
        telemetry.addData("Intake Power", intakePower);
        telemetry.addData("Intake Power 2", intakePower2);
        telemetry.addData("Left Servo Pos: ", feedServos.getLeftServoPositions());
        telemetry.addData("Right Servo Pos", feedServos.getRightServoPositions());
        telemetry.addData("Launch Motor speed (deg/s): ", shooterMotor.getVelocity(AngleUnit.DEGREES));
    }
    private void computeIntakeMotorDirection(){
        intakePower = controller2.getAxisValue(intereriorIntakeMotorAxis);
        if(Math.abs(intakePower - prevIntakePower) > minimumInputDelta){
            intakeMotor.setPower(intakePower);
            prevIntakePower = intakePower;
        }
    }


    private void computeIntake2MotorDirection(){
        intakePower2 = controller2.getAxisValue(exteriorIntakeMotorAxis);
        if(Math.abs(intakePower2 - prevIntakePower2) > minimumInputDelta) {
            intakeMotor2.setPower(intakePower2);
            prevIntakePower2 = intakePower2;
        }
    }

    private void computeShooterMotorVelocity() {
        double forwardTrigger = controller2.getAxisValue(launcherAxis);
        double reverseTrigger = controller2.getAxisValue(reverseLauncherAxis);
        double shooterVelocity;
            //Since motor writes like setPower and setVelocity similar
            //can be time-consuming, this extra check in both cases reduces
            //motor writes(called motor write caching) and thus decreases
            //input latency and lag
        if(forwardTrigger > 0.1){
            shooterVelocity = forwardTrigger * targetVelocity;

        }
        else if(reverseTrigger > 0.1){
            shooterVelocity = -reverseTrigger * targetVelocity;
        }
        else{
            shooterVelocity = 0;
        }

        if(Math.abs(shooterVelocity - prevShooterMotorInput) > minimumInputDelta){
            shooterMotor.setVelocity(shooterVelocity);
            prevShooterMotorInput = shooterVelocity;
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
