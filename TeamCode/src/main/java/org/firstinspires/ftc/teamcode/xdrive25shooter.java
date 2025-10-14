package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.subsystems.DriveTrain;
import org.firstinspires.ftc.teamcode.subsystems.GamepadController;

/**
 * <ul>
 *      <li>DO NOT USE, this has been superseded by XDriveDECODE</li>
 *      <li>Main TeleOp class for X-Drive robot with a shooter.</li>
 * </ul>
 */
@Deprecated
@Disabled
@TeleOp(name = "xdrive25shooter", group = "Robot")
// @Disabled // I've commented this out so you can run the OpMode
public class xdrive25shooter extends LinearOpMode { // Changed to LinearOpMode

    private DriveTrain driveTrain;
    private GamepadController controller1;
    private DcMotor shooterMotor;

    // Shooter state variables


    @Override
    public void runOpMode() {
        // --- INITIALIZATION (init) ---
        controller1 = new GamepadController(gamepad1);
        driveTrain = new DriveTrain(this, controller1);

        // Map the shooter motor from the hardware configuration
        shooterMotor = hardwareMap.get(DcMotor.class, "shooterMotor");
// reverse motor
        shooterMotor.setDirection(DcMotor.Direction.REVERSE);
        telemetry.addData("Status", "Initialized and Ready to Run");
        telemetry.addLine("Press Right Trigger to toggle the shooter.");
        telemetry.update();

        // Wait for the start button to be pressed on the Driver Station
        waitForStart();

        // --- MAIN LOOP (replaces loop()) ---
        while (opModeIsActive()) {
            // Update the gamepad controller state
            controller1.update();

            // Run drivetrain logic
            driveTrain.updateDriveTrainBehavior();

            // --- Shooter Toggle Logic ---
            // Check if the right trigger is pressed more than a small amount
            boolean right_triggerIsPressed = gamepad1.right_trigger > 0.1;

            // Check if the trigger is pressed now but wasn't pressed in the previous loop
            if (gamepad1.right_trigger > 0.1) {
                shooterMotor.setPower(1.0);
            } else {
                shooterMotor.setPower(0.0);
            }


            // --- Telemetry ---
            telemetry.addLine("Press A to reset Yaw");
            telemetry.addLine("Hold left bumper to drive in robot relative");
            telemetry.addData("Shooter Power", shooterMotor.getPower());
            telemetry.update();
        }
    }
}

