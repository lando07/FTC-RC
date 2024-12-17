package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

/**
 * Ok, so this is our omega be-all-end-all class.
 * Here's the button mappings:
 * Gamepad 1:
 * left stick: translational movement
 * right stick: rotational movement, only by tilting it left or right
 * A: Toggle speed setting
 * Gamepad 2:
 * Right Bumper: Reset secondary claw orientation
 * Y: Raise Slider
 * X: Lower Slider
 * Right Stick: rotate arm
 * B: toggle open/close claw
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
    /**
     * The back-right motor for mecanum drive
     */
    private DcMotor backright;
    /**
     * The arm motor for secondary claw to grab pieces out of the submersible
     */
    private DcMotor arm;
    /**
     * Provides arm extension for the claw
     */
    private DcMotor armExtender;
    /**
     * The motor for raising the slider with static claw
     */
    private DcMotor raiseArmSlider;
    /**
     * The claw servo for the static claw
     */
    private Servo primaryClaw;
    /**
     * The claw servo for the secondary claw
     */
    private Servo secondaryClaw;
    /**
     * The claw motor for secondary claw pitch control
     */
    private Servo secondaryClawPitch;
    /**
     * The claw motor for secondary claw yaw control
     */
    private Servo secondaryClawYaw;
    /**
     * The touch sensor to stop the slider motor from retracting after it has been fully retracted
     */
    private TouchSensor touchSensor;
    /**
     * Stores the computed value of the joystick used to control the arm rotation/pitch
     */
    private volatile double arm_direction;
    /**
     * Stores the claw pitch state
     */
    private volatile int clawPitchState;
    /**
     * Stores the claw yaw state
     */
    private volatile int clawYawState;
    /**
     * Stores the slider button state
     */
    private volatile int sliderState;
    /**
     * Stores the extender's state on whether to extend or retract
     */
    private volatile double extenderState;
    /**
     * Stores the claw toggle button state
     */
    private volatile boolean clawToggleButton;
    /**
     * Resets the orientation of the secondary claw
     */
    private volatile boolean resetServoOrientation;
    /**
     * True if the touch sensor is pressed, false if it is not
     */
    private volatile boolean touchSensorState;
    /**
     * True starts it in half speed mode, false starts it in full speed mode
     */
    private boolean halfSpeed = false;
    /**
     * Stores the button state for toggling speed
     */
    private volatile boolean speedToggleButton;
    /**
     * True if open, false if closed, starts false
     */
    private boolean clawState = false;


    /**
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        //Mecanum drive init, these 4 motors go on the control hub
        frontleft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontright = hardwareMap.get(DcMotor.class, "frontRight");
        backleft = hardwareMap.get(DcMotor.class, "backLeft");
        backright = hardwareMap.get(DcMotor.class, "backRight");
        frontleft.setDirection(DcMotorSimple.Direction.REVERSE);
        backleft.setDirection(DcMotorSimple.Direction.REVERSE);
        //Arm and slider init, the slider motor and secondary claw servos go on the expansion hub, touch sensor on control hub
        raiseArmSlider = hardwareMap.get(DcMotor.class, "raiseArmSlider");
        arm = hardwareMap.get(DcMotor.class, "arm");
        armExtender = hardwareMap.get(DcMotor.class, "armSlider");
        armExtender.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        primaryClaw = hardwareMap.get(Servo.class, "primaryClaw");
        secondaryClaw = hardwareMap.get(Servo.class, "secondaryClaw");
        secondaryClawPitch = hardwareMap.get(Servo.class, "pitch");
        secondaryClawYaw = hardwareMap.get(Servo.class, "yaw");
        touchSensor = hardwareMap.get(TouchSensor.class, "touchSensor");


        telemetry.addData("Status:", "Initialized");
        telemetry.update();
    }

    @Override
    public void init_loop() {//not used as of right now

    }

    @Override
    public void start() {
        //Initialize arm to zero position
        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm.setTargetPosition(0);
        arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        arm.setPower(1);

        primaryClaw.setPosition(0);//TODO: Actually get the true servo measurement to close the claw
        secondaryClaw.setPosition(0);//TODO: Actually get the true servo measurement to close the claw
        secondaryClawPitch.setPosition(0);//this will reset the pitch to pointing straight backward
        secondaryClawYaw.setPosition(0);//this will set the claw to hold a specimen vertically

        raiseArmSlider.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        raiseArmSlider.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        raiseArmSlider.setTargetPosition(0);
        raiseArmSlider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        raiseArmSlider.setPower(1);
    }

    @Override
    public void loop() {
        getControllerData();
        if (speedToggleButton) {
            halfSpeed = !halfSpeed;
        }
        doXDrive();
        doSlider();
        doClaw();
        telemetry.addData("SliderCurrentPos: ", raiseArmSlider.getCurrentPosition());
    }

    /**
     * Runs Mecanum drive
     */
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

    /**
     * Does slider motor logic, such as protecting the slide from over-retraction
     */
    void doSlider() {

        if (touchSensorState && !gamepad2.y) {//this stops the motor from going any further down, but still allows it to go upward
            raiseArmSlider.setPower(0);
            if(sliderState == -1) {
                sliderState = 0;
            }
        }
        switch (sliderState) {
            case -1:
                raiseArmSlider.setPower(1);
                raiseArmSlider.setTargetPosition(raiseArmSlider.getCurrentPosition() + 100);
                raiseArmSlider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                break;
            case 1:
                raiseArmSlider.setPower(1);
                raiseArmSlider.setTargetPosition(raiseArmSlider.getCurrentPosition() - 100);//TODO: Get max slider height for high clip
                raiseArmSlider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                break;
            default://do nothing, hold position with 100% power
                break;
        }
    }

    /**
     * Controls both claws, pitch, and yaw, and arm extension
     */
    void doClaw() {//this will handle pitch, yaw, and claw position, and claw arm
        doArmExtension();
        doArm();
        doSecondaryClawPitch();
        doSecondaryClawYaw();
        doClawStateToggle();
    }

    /**
     * Calculates new pitch of secondary claw
     */
    void doSecondaryClawPitch(){
        switch (clawPitchState) {
            case -1:
                secondaryClawPitch.setPosition(secondaryClawPitch.getPosition() - .1);//TODO: Get actual pitch measurements
                break;
            case 1:
                secondaryClawPitch.setPosition(secondaryClawPitch.getPosition() + 1);//TODO: Get actual pitch measurements
                break;
            default:
                break;
        }
    }

    /**
     * Calculates new yaw of secondary claw
     */
    void doSecondaryClawYaw(){
        switch (clawYawState) {//does the claw yaw
            case -1:
                secondaryClawYaw.setPosition(secondaryClawYaw.getPosition() - .1);//TODO: Get actual rotate measurements
                break;
            case 1:
                secondaryClawYaw.setPosition(secondaryClawYaw.getPosition() + .1);//TODO: Get actual rotate measurements
                break;
            default:
                break;
        }
    }

    /**
     * Calculates the speed at which the arm extends
     */
    void doArmExtension(){
//        if (extenderState > 0.01 || extenderState < -0.01) {//extends or retracts the arm for the secondary claw
//            int targetpos = 0;
//            if (halfSpeed) {
//                targetpos = armExtender.getCurrentPosition() + (int) (extenderState * 50);
//            } else {
//                targetpos = armExtender.getCurrentPosition() + (int) (extenderState * 100);
//            }
//            armExtender.setTargetPosition(Math.min(Math.max(targetpos, 0),2000));//TODO: Get measurements for extender min and max
//
//        }
        if (halfSpeed) {
            armExtender.setPower(extenderState * 0.5);
        }else {
            armExtender.setPower(extenderState);
        }
    }

    /**
     * Handles the state of both claws
     */
    void doClawStateToggle(){
        if (clawToggleButton) {//Toggles claw state, then does servo toggling
            clawState = !clawState;

            if (clawState) {//opens both claws
                primaryClaw.setPosition(1);//TODO: Get actual close/open measurements
                secondaryClaw.setPosition(1);//TODO: Get actual close/open measurements
            } else {
                primaryClaw.setPosition(0);//TODO: Get actual close/open measurements
                secondaryClaw.setPosition(0);//TODO: Get actual close/open measurements
            }
        }
    }

    /**
     * Handles the entire arm which the secondary claw resides on
     */
    void doArm() {
        int targetPos = 0;
        if (arm_direction > 0.01 || arm_direction < -0.01) {
            if (halfSpeed) {
                targetPos = arm.getCurrentPosition() + (int) (arm_direction * 50);
            } else {
                targetPos = arm.getCurrentPosition() + (int) (arm_direction * 100);
            }

            arm.setTargetPosition(Math.max(Math.min(targetPos, 0), -4025));
            arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
    }

    /**
     * Grabs all controller data, then stores that shit for methods or whatever
     */
    void getControllerData() {//drivetrain and gamepad1's joysticks are handled in doXDrive, every thing else is handled here
        if (gamepad2.y) {//slider goes up
            sliderState = 1;
        } else if (gamepad2.x) {//slider goes down
            sliderState = -1;
        } else {
            sliderState = 0;
        }

        if (gamepad2.dpad_left) {//yaw left, or ccw
            clawYawState = -1;
        } else if (gamepad2.dpad_right) {//yaw right, or cw
            clawYawState = 1;
        } else {
            clawYawState = 0;
        }

        if (gamepad2.dpad_up) {
            clawPitchState = 1;
        } else if (gamepad2.dpad_down) {
            clawPitchState = -1;
        } else {
            clawPitchState = 0;
        }
        speedToggleButton = gamepad1.b;
        extenderState = -gamepad2.left_stick_y;
        arm_direction = -gamepad2.right_stick_y;
        clawToggleButton = gamepad2.b;
        resetServoOrientation = gamepad2.right_bumper;
        touchSensorState = touchSensor.isPressed();
    }
}
