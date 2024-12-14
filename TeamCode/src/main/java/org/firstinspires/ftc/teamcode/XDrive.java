package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.TouchSensor;

/**
 * Ok, so this is our omega be-all-end-all class.
 * Here's the button mappings:
 * Gamepad 1:
 * left stick: translational movement
 * right stick: rotational movement, only by tilting it left or right
 * A: Toggle speed setting
 * Gamepad 2:
 * Left stick: raise and lower slide assembly
 * Right Stick: rotate arm
 * A: toggle open/close claw
 * Left+right D-Pad: CW/CCW claw yaw
 * Up+down D-Pad: CW/CCW claw pitch
 */
@TeleOp(name = "XDrive", group = "Robot")
public class XDrive extends OpMode {
    /**
     * The front-left motor for mecanum drive
     */
    private DcMotor frontleft;
    /**
     * The front-right motor for mecanum drive
     */
    private DcMotor frontright;
    /**
     * The back-left motor for mecanum drive
     */
    private DcMotor backleft;
    //test commit
    /**
     * The back-right motor for mecanum drive
     */
    private DcMotor backright;
    /**
     * The arm servo motor for arm control and pivot
     */
    private DcMotor arm;
    /**
     * The motor for raising the arm
     */
    private DcMotor raiseArmSlider;
    /**
     * The claw motor for claw control
     */
    private Servo claw;
    /**
     * The claw motor for claw pitch control
     */
    private Servo clawPitch;
    /**
     * The claw motor for claw yaw control
     */
    private Servo clawYaw;
    /**
     * The touch sensor to stop the slider motor from retracting after it has been fully retracted
     */
    private TouchSensor touchSensor;
    /**
     * Stores the computed value of the joystick used to control the arm rotation/pitch
     */
    private volatile double arm_direction;
    /**
     * Stores the slider button state
     */
    private volatile boolean sliderButton;
    /**
     * Stores the claw toggle button state
     */
    private volatile boolean clawToggleButton;
    /**
     * True if the touch sensor is pressed, false if it is not
     */
    private boolean touchSensorState;
    /**
     * True starts it in half speed mode, false starts it in full speed mode
     */
    private boolean halfSpeed = false;
    /**
     * Stores the button state for toggling speed
     */
    private volatile boolean speedToggleButton;


    /**
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        frontleft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontright = hardwareMap.get(DcMotor.class, "frontRight");
        backleft = hardwareMap.get(DcMotor.class, "backLeft");
        backright = hardwareMap.get(DcMotor.class, "backRight");
        raiseArmSlider = hardwareMap.get(DcMotor.class, "raiseArmSlider");
        clawPitch = hardwareMap.get(Servo.class, "clawPitch");
        clawYaw = hardwareMap.get(Servo.class, "clawYaw");
        claw = hardwareMap.get(Servo.class, "claw");
        touchSensor = hardwareMap.get(TouchSensor.class, "touchSensor");
        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void init_loop() {//not used as of right now

    }

    @Override
    public void start() {//not used as of right now
    }

    @Override
    public void loop() {
        getControllerData();
        if(speedToggleButton) {
            halfSpeed = !halfSpeed;
        }
        doXDrive();
        doSlider();
        doClaw();


    }

    void doXDrive() {//oh boy this is gonna get fun
        double max;
        // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
        double axial = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
        double lateral = gamepad1.left_stick_x;
        double yaw = gamepad1.right_stick_x;
        //these are the magic 4 statements right here
        // Combine the joystick requests for each axis-motion to determine each wheel's power.
        // Set up a variable for each drive wheel to save the power level for telemetry.
        double leftFrontPower = axial + lateral + yaw;
        double rightFrontPower = axial - lateral - yaw;
        double leftBackPower = axial - lateral + yaw;
        double rightBackPower = axial + lateral - yaw;

        // Normalize the values so no wheel power exceeds 100%
        // This ensures that the robot maintains the desired motion.
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
        if (halfSpeed) {
            leftFrontPower *= 0.5;
            rightFrontPower *= 0.5;
            leftBackPower *= 0.5;
            rightBackPower *= 0.5;
        }

        frontleft.setPower(leftFrontPower);
        frontright.setPower(rightFrontPower);
        backleft.setPower(leftBackPower);
        backright.setPower(rightBackPower);

    }
    void doSlider(){

    }
    void doClaw() {//this will handle pitch, yaw, and claw position

    }
    void getControllerData() {//drivetrain and gamepad1's joysticks are handled in doXDrive, every thing else is handled here
        speedToggleButton = gamepad1.a;

    }
}
