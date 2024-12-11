package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;

@TeleOp(name = "XDrive", group = "Robot")
public class XDrive extends OpMode {
    /**
     * The front-left motor for mecanum drive
     */
    public DcMotor frontleft;
    /**
     * The front-right motor for mecanum drive
     */
    public DcMotor frontright;
    /**
     * The back-left motor for mecanum drive
     */
    public DcMotor backleft;
    /**
     * The back-right motor for mecanum drive
     */
    public DcMotor backright;
    /**
     * The arm motor for arm control and pivot
     */
    public DcMotor arm;
    /**
     * The motor for raising the arm
     */
    public DcMotor raiseArmSlider;
    /**
     * The claw motor for claw control
     */
    public Servo claw;
    /**
     * True starts it in half speed mode, false starts it in full speed mode
     */
    private boolean halfSpeed = false;
    /**
     * True starts the robot in reverse mode, false starts it in forward mode
     */
    private volatile boolean toggleClawPosition;
    private boolean clawPosition;
    /**
     * Stores the computed value of the formula to determine whether the claw is turning clockwise
     * or counterclockwise
     */
    private volatile double arm_direction;
    /**
     * Stores the computed values of the triggers to raise the arm to a set position
     */
    private volatile double raiseArmSpeed;
    private volatile boolean arm_CW;
    private volatile boolean arm_CCW;
    private volatile boolean speedToggleButton;
    private volatile boolean currentSpeed;

    /**
     * Stores the value of the button to reset the motor
     */
    private volatile boolean motorPosResetButton;

    /**
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        frontleft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontright = hardwareMap.get(DcMotor.class, "frontRight");
        backleft = hardwareMap.get(DcMotor.class, "backLeft");
        backright = hardwareMap.get(DcMotor.class, "backRight");
        //this is commented out because we currently don't have the right hubs to house more than 4 motors
//        arm = hardwareMap.get(DcMotor.class, "arm");
//        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        arm.setTargetPosition(0);
//        arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        arm.setPower(1);
        claw = hardwareMap.get(Servo.class, "claw");

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
        if (toggleClawPosition) {
            clawPosition = !clawPosition;
        }

        if (clawPosition) {
            claw.setPosition(0.45);
        } else {
            claw.setPosition(0.12);
        }

        if(speedToggleButton){
            halfSpeed = !halfSpeed;
        }
        doXDrive();



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
        backleft.setPower(rightBackPower);

    }

    void getControllerData() {
        //Here are the button mappings, change them as you please
        arm_CW = gamepad1.dpad_left;//this is meant to be any digital button, leftie loosy
        arm_CCW = gamepad1.dpad_right;//this is meant to be any digital button, rightie tighty
        arm_direction = gamepad1.left_trigger - gamepad1.right_trigger;//this is meant for the joysticks or the triggers
        //the bumpers are currently not used, but are digital buttons
        toggleClawPosition = gamepad1.x;//this is meant to be any digital button
        speedToggleButton = gamepad1.a;//this is meant to be any digital button
        raiseArmSpeed = gamepad1.right_trigger - gamepad1.left_trigger;//this dictates how fast the arm should be raised and lowered, using the analog triggers


    }
}
