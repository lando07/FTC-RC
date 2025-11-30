package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.subsystems.DriveTrain;
import org.firstinspires.ftc.teamcode.subsystems.GamepadController;

/**
 * This file contains a minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in
 * the teleop period of an FTC match. The names of OpModes appear on the menu of the FTC Driver Station. This includes
 * full drivetrain functionality, so if used, you can at lease move the robot around using mecanum drive
 * according to it's specs.
 * @author Mentor Landon Smith
 */
@Disabled
@Config
@TeleOp(name="TeleOpTemplate_WithDriveTrain", group="TeleOp")
public class TeleOpTemplate_WithDriveTrain extends OpMode {
    /**
     * Stores the drivetrain subsystem object which handles all movement and motor power.
     * All drivetrain configurations are stored in the DriveTrain class, since the keybinds
     * are universal across mecanum drivetrains, ftc, and also the fact it requires 2
     * joysticks.
     */
    private DriveTrain driveTrain;
    /**
     * These will be the 2 controllers to use and pass through to
     * other subsystems for user input
     */
    private GamepadController controller1, controller2;

    @Override
    public void init(){
        //Initialize subsystems and controllers here
        //Always create objects before configuring them, or you will get
        //a null pointer exception
        controller1 = new GamepadController(gamepad1);
        controller2 = new GamepadController(gamepad2);
        //pass through controller 2 if you want driver 2/gamepad2 to control movement
        driveTrain = new DriveTrain(this, controller1);
    }
    @Override
    public void init_loop(){
        //Not needed, but serves as a loop for code that needs to run
        //continuously between initialization and pressing play on the driver hub
    }
    //This is one-time code to run at the beginning of a match.
    //Here can be code to pre-start motors, servos, etc.
    @Override
    public void start(){
        driveTrain.setBrakingMode(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    /**
     * This is the main control loop that runs during an FTC TeleOperated period.
     * Because of our architecture, the 3 things allowed to go here are as follows:
     * <br />
     * 1. Subsystem updates
     * <br />
     * 2. Controller updates
     * <br />
     * 3. Very primitive motor/servo movement(no complex functions, features, or math)
     */
    @Override
    public void loop(){
        // Update controllers
        controller1.update();
        controller2.update();

        // Update drivetrain
        driveTrain.updateDriveTrainBehavior();

        //Update subsystems here


        //Run any primitive motor/servo commands here


        //Update telemetry here

        telemetry.update();

    }


}
