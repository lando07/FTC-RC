package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.subsystems.RaiseArmSlider.highSpecimenLowBasket;
import static org.firstinspires.ftc.teamcode.subsystems.RaiseArmSlider.lowSpecimen;
import static org.firstinspires.ftc.teamcode.subsystems.RaiseArmSlider.clipSpecimenOffSet;
import static java.lang.Thread.sleep;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
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

@Config
@TeleOp(name = "XDrive", group = "Robot")
public class XDrive extends OpMode {
    /**
     * The front-left motor for mecanum drive
     */
    private DcMotor frontLeft;
    /**
     * The front-right motor for mecanum drive
     */
    private DcMotor frontRight;
    /**
     * The back-left motor for Mecanum drive
     */
    private DcMotor backLeft;
    /**
     * The back-right motor for Mecanum drive
     */
    private DcMotor backRight;
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
     * Tracks button state for preset clip instruction
     */
    private volatile boolean clipSpecimen;
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
    private boolean speedToggleButtonHeld = false;
    /**
     * True if open, false if closed, starts false
     */
    private boolean clawState = false;
    /**
     * Stores held button state for de-bouncing and anti-infinite loop stuff
     */
    private boolean clawToggleButtonHeld = false;
    /**
     * Stores button/bumper state to raise arm to preset height for the first specimen hook
     */
    private volatile boolean highSpecimenLowBasketButton;
    /**
     * Stores button/bumper state to raise arm to second specimen hook
     */
    private volatile boolean lowSpecimenButton;

    /**
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        //Mecanum drive init, these 4 motors go on the control hub
        frontLeft = hardwareMap.get(DcMotorEx.class, "leftFront");
        frontRight = hardwareMap.get(DcMotorEx.class, "rightFront");
        backLeft = hardwareMap.get(DcMotorEx.class, "leftBack");
        backRight = hardwareMap.get(DcMotorEx.class, "rightBack");
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
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

    /**
     * Code to run continuously before start but after init
     */
    @Override
    public void init_loop() {//not used as of right now

    }

    /**
     * Code to run once when the driver presses Start
     */
    @Override
    public void start() {
        primaryClaw.setPosition(1);
        secondaryClaw.setPosition(1);
        secondaryClawYaw.setPosition(0);//this will set the claw to hold a specimen parallel to the main claw

        raiseArmSlider.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        raiseArmSlider.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        raiseArmSlider.setTargetPosition(0);
        raiseArmSlider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        raiseArmSlider.setPower(1);

        armExtender.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    /**
     * Code to run continuously when the driver hits Start
     */
    @Override
    public void loop() {
        getControllerData();
        if (speedToggleButton && !speedToggleButtonHeld) {
            halfSpeed = !halfSpeed;
            clawToggleButtonHeld = true;
        } else {
            speedToggleButtonHeld = speedToggleButton;
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
        // Omni Mode uses right joystick to go forward & strafe, and left joystick to rotate.
        //Just like a drone
        double axial = -gamepad1.right_stick_y;  // Note: pushing stick forward gives negative value
        double lateral = gamepad1.right_stick_x;
        double yaw = gamepad1.left_stick_x;
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

        frontLeft.setPower(leftFrontPower);
        frontRight.setPower(rightFrontPower);
        backLeft.setPower(leftBackPower);
        backRight.setPower(rightBackPower);

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
        if (sliderState != 0) {
            int targetPos = raiseArmSlider.getCurrentPosition();
            if (sliderState == -1) {
                raiseArmSlider.setPower(1);
                targetPos = raiseArmSlider.getCurrentPosition() + 200;
            } else if (sliderState == 1) {
                raiseArmSlider.setPower(1);
                targetPos = raiseArmSlider.getCurrentPosition() - 200;
            }
            raiseArmSlider.setTargetPosition(Math.max(Math.min(15, targetPos), -3000));
            raiseArmSlider.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        }
        if (raiseArmSlider.getCurrentPosition() < -100) {//when the arm is raised, the robot's CG gets very high, which can cause the robot to easily roll over
            //The FLOAT value set below allows the motors to coast, reducing negative acceleration and reducing a roll over risk
            frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        } else {//When the robot's slider is fully retracted, the CG is very low, so braking will be enabled to increase responsiveness and accuracy
            frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }


    }

    /**
     * Controls both claws, pitch, and arm extension
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
        if (resetServoOrientationButton) {
            secondaryClawYaw.setPosition(0);
        }
        switch (clawYawState) {//does the claw yaw
            case -1:
                secondaryClawYaw.setPosition(secondaryClawYaw.getPosition() - .03);
                break;
            case 1:
                secondaryClawYaw.setPosition(secondaryClawYaw.getPosition() + .03);
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
        if (halfSpeed) {
            armExtender.setPower(0.5 * extenderState);
        } else {
            armExtender.setPower(extenderState);
        }
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
        } else {
            clawToggleButtonHeld = clawToggleButton;
        }
    }

    void goToPresetHeight() {
        if (lowSpecimenButton) {
            raiseArmSlider.setTargetPosition(lowSpecimen);
            raiseArmSlider.setPower(1);
        } else if (highSpecimenLowBasketButton) {
            raiseArmSlider.setTargetPosition(highSpecimenLowBasket);
            raiseArmSlider.setPower(1);
        } else if (clipSpecimen) {
            raiseArmSlider.setTargetPosition(raiseArmSlider.getCurrentPosition() + clipSpecimenOffSet);
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
        clawToggleButton = gamepad2.b;
        resetServoOrientationButton = gamepad2.dpad_up;
        touchSensorState = touchSensor.isPressed();
        lowSpecimenButton = gamepad2.left_bumper;
        highSpecimenLowBasketButton = gamepad2.right_bumper;
        clipSpecimen = gamepad2.dpad_down;
    }
}
