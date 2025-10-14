package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.subsystems.DriveTrain;
import org.firstinspires.ftc.teamcode.subsystems.GamepadController;
import org.firstinspires.ftc.teamcode.subsystems.axisBehavior;
import org.firstinspires.ftc.teamcode.subsystems.feedServoLauncher;

/**
 * This year's TeleOp for the robot
 */
@Config
@TeleOp(name = "XDriveDECODE", group = "Robot")
public class XDriveDECODE extends OpMode {
    /**
     * Stores the drivetrain object and necessary data to move the robot
     */
    private DriveTrain driveTrain;
    /**
     * Stores the controller and its keybinds and behaviors
     */
    private GamepadController controller1, controller2;
    /**
     * The axis to read for the launcher power
     */
    public static axisBehavior launcherAxis = axisBehavior.RIGHT_TRIGGER;
    private feedServoLauncher feedLauncher;
    private DcMotor shooterMotor;

    @Override
    public void init() {
        //Initialize subsystems and controllers here
        //Always create objects before configuring them, or you will get
        //a null pointer exception
        controller1 = new GamepadController(gamepad1);
        controller2 = new GamepadController(gamepad2);
        driveTrain = new DriveTrain(this, controller1);
        feedLauncher = new feedServoLauncher(this, controller2);
        // --- Configuration for controller 1 ---
        //No configuration needed, as of right now, since it will just be for
        //the drivetrain, which configures controller1 by itself
        // --- Configuration for controller 2 ---
        controller2.configureAxis(launcherAxis);

        // --- Hardware Initialization ---
        shooterMotor = hardwareMap.get(DcMotor.class, "shooterMotor");
        shooterMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void init_loop() {
        //Not needed, as of right now
    }

    @Override
    public void start() {
        driveTrain.setBrakingMode(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @Override
    public void loop() {
        // 1. Update controllers to read the latest hardware states
        //Mandatory for proper robot function, do not remove
        controller1.update();
        controller2.update();

        // 2. Update subsystems that use the controller data
        driveTrain.updateDriveTrainBehavior();
        feedLauncher.updateFeedServoLauncherBehavior();

        // 3. Set power for motors (including servos) controlled directly in the OpMode
        shooterMotor.setPower(controller2.getAxisValue(launcherAxis));

        //4. Update telemetry
        telemetry.addData("Trigger Axis: ", controller1.getAxisValue(launcherAxis));

    }

    @Override
    public void stop() {
        driveTrain.stop();
        shooterMotor.setPower(0);
        feedLauncher.stop();
        //Not needed, as of right now
    }
}
