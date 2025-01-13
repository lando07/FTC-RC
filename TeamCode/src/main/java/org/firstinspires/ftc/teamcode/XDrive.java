package org.firstinspires.ftc.teamcode;

import static java.lang.Thread.sleep;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
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
     * Stores the extender's state on whether to extend or retract
     */
    private volatile double extenderState;
    /**
     * Stores the claw yaw state
     */
    private volatile int clawYawState;
    /**
     * Stores the slider button state
     */
    private volatile int sliderState;
    /**
     * Stores the claw toggle button state
     */
    private volatile boolean clawToggleButton;
    /**
     * Resets the orientation of the secondary claw
     */
    private volatile boolean resetServoOrientationButton;
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
     *  Stores held button state for de-bouncing and anti-infinite loop stuff
     */
    private boolean clawToggleButtonHeld = false;
    /**
     * Stores button/bumper state to raise arm to preset height for the first specimen hook
     */
    private volatile boolean secondHeightSpecimenButton;
    /**
     * Stores button/bumper state to raise arm to second specimen hook
     */
    private volatile boolean firstHeightSpecimenButton;
    /**
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        //Mecanum drive init, these 4 motors go on the control hub
        frontleft   = hardwareMap.get(DcMotorEx.class, "leftFront");
        frontright  = hardwareMap.get(DcMotorEx.class, "rightFront");
        backleft    = hardwareMap.get(DcMotorEx.class, "leftBack");
        backright   = hardwareMap.get(DcMotorEx.class, "rightBack");
        frontleft   .setDirection(DcMotorSimple.Direction.REVERSE);
        backleft    .setDirection(DcMotorSimple.Direction.REVERSE);
        frontleft   .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backleft    .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontright  .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backright   .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //Arm and slider init, the slider motor and secondary claw servos go on the expansion hub, touch sensor on control hub
        raiseArmSlider = hardwareMap.get(DcMotor.class, "raiseArmSlider");
        armExtender = hardwareMap.get(DcMotor.class, "armSlider");


        primaryClaw = hardwareMap.get(Servo.class, "primaryClaw");
        secondaryClaw = hardwareMap.get(Servo.class, "secondaryClaw");
        secondaryClawYaw = hardwareMap.get(Servo.class, "yaw");
        touchSensor = hardwareMap.get(TouchSensor.class, "touchSensor");


        telemetry.addData("Status:", "Initialized");
        telemetry.update();
    }

    @Override
    public void init_loop() {//not used as of right now

    }

    @Override
    public void start(){
        primaryClaw.setPosition(1);
        secondaryClaw.setPosition(1);
        secondaryClawYaw.setPosition(0);//this will set the claw to hold a specimen parallel to the main claw

        raiseArmSlider.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        raiseArmSlider.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        raiseArmSlider.setTargetPosition(0);
        raiseArmSlider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        raiseArmSlider.setPower(1);

        armExtender.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        armExtender.setPower(0.5);
        try {
            sleep(500);
        }
        catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
        armExtender.setPower(0);
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
        goToPresetHeight();
        telemetry.addData("SliderCurrentPos: ", raiseArmSlider.getCurrentPosition());
        telemetry.addData("ExtCurrPos:", armExtender.getCurrentPosition());
        telemetry.addData("PrimaryClaw Pos:", primaryClaw.getPosition());
        telemetry.addData("SecondaryClaw pos", secondaryClaw.getPosition());
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
            raiseArmSlider.setPower(0);//The zero power behavior set to float is because the arm has so much resistance it can hold it's own weight
            raiseArmSlider.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            raiseArmSlider.setTargetPosition(0);
            raiseArmSlider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            if (sliderState == -1) {
                sliderState = 0;
            }
        }
        if(sliderState != 0) {
            int targetpos = raiseArmSlider.getCurrentPosition();
            if (sliderState == -1) {
                raiseArmSlider.setPower(1);
                targetpos = raiseArmSlider.getCurrentPosition() + 100;
            } else if (sliderState == 1) {
                raiseArmSlider.setPower(1);
                targetpos = raiseArmSlider.getCurrentPosition() - 100;
            }
            raiseArmSlider.setTargetPosition(Math.max(Math.min(15,targetpos), -3000));
            raiseArmSlider.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        }



    }

    /**
     * Controls both claws, pitch, and yaw, and arm extension
     */
    void doClaw() {//this will handle extension, yaw, and claw state
        doArmExtension();
        doSecondaryClawYaw();
        doClawStateToggle();
    }

    /**
     * Calculates new yaw of secondary claw
     */
    void doSecondaryClawYaw() {
        switch (clawYawState) {//does the claw yaw
            case -1:
                secondaryClawYaw.setPosition(secondaryClawYaw.getPosition() - .01);//TODO: Get actual rotate measurements
                break;
            case 1:
                secondaryClawYaw.setPosition(secondaryClawYaw.getPosition() + .01);//TODO: Get actual rotate measurements
                break;
            default:
                break;
        }
        clawYawState = 0;
    }

    /**
     * Calculates the speed at which the arm extends
     */
    void doArmExtension() {

    }

    /**
     * Handles the state of both claws
     */
    void doClawStateToggle() {
        if (clawToggleButton && !clawToggleButtonHeld) {//Toggles claw state, then does servo toggling
            clawState = !clawState;
            clawToggleButtonHeld = true;

            if (clawState) {//opens both claws
                primaryClaw.setPosition(0.1);
                secondaryClaw.setPosition(0);
            } else {//closes claws
                primaryClaw.setPosition(1);
                secondaryClaw.setPosition(1);
            }
        }
        else{
            clawToggleButtonHeld = clawToggleButton;
        }
    }

    void goToPresetHeight(){
        if(firstHeightSpecimenButton){
            raiseArmSlider.setTargetPosition(-1400);
        }
        else if(secondHeightSpecimenButton){
            raiseArmSlider.setTargetPosition(-3000);
        }
        raiseArmSlider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
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
            clawYawState = 1;
        } else if (gamepad2.dpad_right) {//yaw right, or cw
            clawYawState = -1;
        } else {
            clawYawState = 0;
        }

        speedToggleButton = gamepad1.b;
        extenderState = -gamepad2.left_stick_y;
        arm_direction = -gamepad2.right_stick_y;
        clawToggleButton = gamepad2.b;
        resetServoOrientationButton = gamepad2.right_bumper;
        touchSensorState = touchSensor.isPressed();
        firstHeightSpecimenButton = gamepad2.left_bumper;
        secondHeightSpecimenButton = gamepad2.right_bumper;
    }
}
