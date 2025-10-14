package org.firstinspires.ftc.teamcode;


import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

//import org.firstinspires.ftc.teamcode.subsystems.BiStateButtonBehavior;
import org.firstinspires.ftc.teamcode.subsystems.DriveTrain;
//import org.firstinspires.ftc.teamcode.subsystems.GamepadButton;
import org.firstinspires.ftc.teamcode.subsystems.GamepadController;
//import org.firstinspires.ftc.teamcode.subsystems.axisBehavior;

/**
 * DO NOT USE, this file has been superseded by XDriveDecode and is no longer needed
 */
@Deprecated
@Disabled
@Config
@TeleOp(name = "xdrive2025", group = "Robot")
public class xdrive2025 extends OpMode
{

    public static boolean dynamicBrakingEnabled = false;
   // public static double backStopPosition = 0.03;
  //  private final axisBehavior armExtendAxis = axisBehavior.LEFT_STICK_Y;
   // private final GamepadButton resetServoOrientationButton = GamepadButton.A;
   // private final GamepadButton clawToggleButton = GamepadButton.B;
  //  private final BiStateButtonBehavior clawToggleBehavior = BiStateButtonBehavior.TOGGLE;
  //  private final GamepadButton yawRightButton = GamepadButton.D_PAD_RIGHT;
  //  private final GamepadButton yawLeftButton = GamepadButton.D_PAD_LEFT;
  //  private final GamepadButton pitchUpButton = GamepadButton.D_PAD_UP;
  //  private final GamepadButton pitchDownButton = GamepadButton.D_PAD_DOWN;

    private DcMotor shooterMotor;
    private DriveTrain driveTrain;
    private GamepadController controller1;

    @Override
    public void init()
    {
        controller1 = new GamepadController(gamepad1);
        Gamepad currentGamepad1;
        Gamepad previousGamepad1;
        driveTrain = new DriveTrain(this, controller1);
        telemetry.addData("Status:", "Initialized");
        telemetry.update();
    }

    @Override
    public void start()

    {

        // while (opModeIsActive())

        telemetry.update();
    }

    private boolean opModeIsActive()
    {
        return false;
    }

    @Override
    public void loop()
    {
        {
            telemetry.addLine("Press A to reset Yaw");
            telemetry.addLine("Hold left bumper to drive in robot relative");
            telemetry.addLine("The left joystick sets the robot direction");
            telemetry.addLine("Moving the right joystick left and right turns the robot");

            // If you press the A button, then you reset the Yaw to be zero from the way
            // the robot is currently pointing
           // if (gamepad1.a) {
             //   imu.resetYaw();
            //}
            // If you press the left bumper, you get a drive from the point of view of the robot
            // (much like driving an RC vehicle)
            //if (gamepad1.left_bumper) {
              //  drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
            //} else {
             //   driveFieldRelative(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
            //}
        }
        controller1.update();

        driveTrain.updateDriveTrainBehavior();

    }

    @TeleOp(name="Shooter")
    public  class ShooterOpMode extends LinearOpMode {
        private boolean shooterOn = false;
        private boolean right_triggerWasPressed = false;

        @Override
        public void runOpMode() {
            // Map the hardware
            DcMotor shooterMotor = hardwareMap.get(DcMotor.class, "shooterMotor");
            telemetry.addData("Status", "Initialized");
            telemetry.update();

            waitForStart();

            while (opModeIsActive()) {
                // Check for 'right_trigger' button toggle
                // A threshold of 0.1 is often used to avoid accidental presses from resting fingers.
// You can adjust this value as needed.
                boolean right_triggerIsPressed = gamepad1.right_trigger > 0.1;


                // Rising edge detector: check if button is pressed NOW but wasn't LAST loop
                if (right_triggerIsPressed && !right_triggerWasPressed) {
                    shooterOn = !shooterOn; // Inverts the motor's state
                }

                // Control the motor based on the state variable
                if (shooterOn) {
                    shooterMotor.setPower(1.0);
                } else {
                    shooterMotor.setPower(0.0);
                }

                // Update the 'previous state' variable for the next loop
                right_triggerWasPressed = right_triggerIsPressed;

                telemetry.addData("Shooter", shooterOn ? "On" : "Off");
                telemetry.update();
            }
        }
    }





}