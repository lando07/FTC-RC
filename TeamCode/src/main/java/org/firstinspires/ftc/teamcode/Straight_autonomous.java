package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/*
 * This autonomous program drives the robot forward a set distance using motor encoders.
 * It is configured to drive forward 18 inches and then stop.
 */

@Autonomous(name = "Straight Auto", group = "Robot")
public class Straight_autonomous extends LinearOpMode {

    // Declare motor variables
    private DcMotor leftFront = null;
    private DcMotor rightFront = null;
    private DcMotor leftBack = null;
    private DcMotor rightBack = null;

    private ElapsedTime runtime = new ElapsedTime();

    // Constants for robot dimensions and motor specifications.
    // You may need to tune these for your specific robot.
  //  static final double COUNTS_PER_MOTOR_REV = 1440;    // Example: TETRIX Motor Encoder
  //  static final double DRIVE_GEAR_REDUCTION = 1.0;     // No external gearing
  //  static final double WHEEL_DIAMETER_INCHES = 4.0;    // Diameter of the robot's wheels
    static final double COUNTS_PER_INCH = 28.6475; //(COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
           // (WHEEL_DIAMETER_INCHES * 3.1415);
//new_COUNTS_PER_INCH = old_COUNTS_PER_INCH * (commanded_distance / actual_distance);
  // commanded_distance = 18
  //  actual_distance = 72
    // Set desired driving speed
    static final double DRIVE_SPEED = 0.6;

    @Override
    public void runOpMode() {

        // --- 1. INITIALIZATION ---

        // Map motors from the hardware configuration
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
//Breaking
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Reverse the motors on one side so they spin in the same direction.
        // You may need to reverse the RIGHT side instead, depending on your robot's construction.
        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        // Reset the motor encoders to zero
        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Set motors to run using encoders
        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Signal that initialization is complete
        telemetry.addData("Status", "Ready to run");
        telemetry.update();

        // Wait for the driver to press the START button on the Driver Hub
        waitForStart();

        // --- 2. EXECUTION ---

        // This is the main command: Drive forward for 18 inches at DRIVE_SPEED with a 5-second timeout.
        encoderDrive(DRIVE_SPEED, 18, 18, 5.0);

        // *** ADD THIS BLOCK FOR THE TURN ***
        // Step 2: Turn left 90 degrees
        // To turn left, the left wheels move backward (-inches) and the right wheels move forward (+inches).
        // The distance "12" is an estimate for a 90-degree turn. You will need to TUNE this value.
        telemetry.addData("Step 2", "Turning left");
        telemetry.update();
        encoderDrive(DRIVE_SPEED, -15, 15, 2.0);

       encoderDrive(DRIVE_SPEED, 18, 18, 5.0);
        // --- 3. SHUTDOWN ---

        // Display completion message
        telemetry.addData("Path", "Complete");
        telemetry.update();
        sleep(1000); // Pause for a second to show the message
    }

    /**
     * Drives the robot a specified distance in inches.
     * @param speed The speed at which to drive (0.0 to 1.0).
     * @param leftInches The distance the left wheels should travel.
     * @param rightInches The distance the right wheels should travel.
     * @param timeoutS The maximum time allowed for the move, in seconds.
     */
    public void encoderDrive(double speed, double leftInches, double rightInches, double timeoutS) {
        if (opModeIsActive()) {
            // Calculate target positions for each motor
            int newLeftTarget = leftFront.getCurrentPosition() + (int) (leftInches * COUNTS_PER_INCH);
            int newRightTarget = rightFront.getCurrentPosition() + (int) (rightInches * COUNTS_PER_INCH);

            // Set the target positions
            leftFront.setTargetPosition(newLeftTarget);
            rightFront.setTargetPosition(newRightTarget);
            leftBack.setTargetPosition(newLeftTarget); // Assumes back motors follow front
            rightBack.setTargetPosition(newRightTarget);

            // Switch motors to RUN_TO_POSITION mode
            leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // Start the motors
            runtime.reset();
            leftFront.setPower(Math.abs(speed));
            rightFront.setPower(Math.abs(speed));
            leftBack.setPower(Math.abs(speed));
            rightBack.setPower(Math.abs(speed));

            // Loop until the motors reach their target or the timeout is reached
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (leftFront.isBusy() || rightFront.isBusy())) { // Use || to wait for both to finish

                // Optional: Display telemetry
                telemetry.addData("Target", "%7d : %7d", newLeftTarget, newRightTarget);
                telemetry.addData("Current", "%7d : %7d",
                        leftFront.getCurrentPosition(), rightFront.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motors
            leftFront.setPower(0);
            rightFront.setPower(0);
            leftBack.setPower(0);
            rightBack.setPower(0);

            // Reset motors to their default RUN_USING_ENCODER mode
            leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            sleep(250); // Optional pause
        }
    }
}
