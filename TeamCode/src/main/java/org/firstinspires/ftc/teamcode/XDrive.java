package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.subsystems.RaiseArmSlider.highSpecimenLowBasket;
import static org.firstinspires.ftc.teamcode.subsystems.RaiseArmSlider.lowSpecimen;
import static org.firstinspires.ftc.teamcode.subsystems.RaiseArmSlider.clipSpecimenOffSet;
import static org.firstinspires.ftc.teamcode.subsystems.Claw.*;


import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.ftc.LazyImu;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.ImuOrientationOnRobot;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Quaternion;

/**
 * Ok, so this is our omega be-all-end-all class.
 * Here's the button mappings:
 * Gamepad 1:
 * left stick: translational movement
 * right stick: rotational movement, only by tilting it left or right
 * A: Toggle speed setting
 * Gamepad 2:
 * Y: Raise Slider
 * X: Lower Slider
 * Left Stick Vertical: Extend secondary arm
 * B: toggle open/close claw
 * Left Stick y: Secondary Claw extension
 * Right Stick x: CW/CCW claw yaw
 * Right Stick y: CW/CCW claw pitch
 * D-Pad down: Clip Specimen
 * D-Pad up: Reset yaw
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
     * The servo for controlling the pitch of the secondary claw
     */
    private Servo secondaryClawPitch;
    /**
     * The static claw mounted to the front - grabs field specimens
     */
    private Servo tertiaryClaw;
    /**
     * The touch sensor to stop the slider motor from retracting after it has been fully retracted
     */
    private TouchSensor touchSensor;

    private IMU imu;
    private LazyImu lazyImu;
    /**
     * Stores the joystick state to compute the pitch of the secondary claw
     */
    private volatile int pitchState;
    /**
     * Stores the extender's state on whether to extend or retract
     */
    private volatile double extenderState;
    /**
     * Stores the claw yaw state
     */
    private volatile int clawYawState;
    /**
     * Stores the initial pitch offset when the game starts. The claw should be sticking outwards
     */
    public static double initialPitchOffset = 0.65;
    /**
     * Stores the slider button state
     */
    private volatile double sliderState;
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
    private volatile boolean halfSpeed = false;
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

    public volatile boolean resetIMUHeadingButton;

    public volatile  boolean isResetIMUHeadingButtonHeld;
    public volatile  boolean toggleXDriveMode;
    public

    public volatile boolean isToggleXDriveModeHeld;

    public static boolean fieldOrientedMode = true;


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
        secondaryClawPitch = hardwareMap.get(Servo.class, "pitch");
        tertiaryClaw = hardwareMap.get(Servo.class, "fieldClaw");
        touchSensor = hardwareMap.get(TouchSensor.class, "touchSensor");
        lazyImu = new LazyImu(hardwareMap, "imu", new RevHubOrientationOnRobot(
                MecanumDrive.PARAMS.logoFacingDirection, MecanumDrive.PARAMS.usbFacingDirection));

        imu = lazyImu.get();


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
        primaryClaw.setPosition(OPEN);
        secondaryClaw.setPosition(OPEN);
        tertiaryClaw.setPosition(OPEN);
        secondaryClawYaw.setPosition(initialPitchOffset);//This starts the servo in the middle of both extrema
        secondaryClawPitch.setPosition(0);

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
        if (fieldOrientedMode) {
            doHeadlessXDrive();
        } else {
            doXDrive();
        }
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
        //I decided to limit precision to 4 decimal places to counteract drift
        double axial = ((int) (-gamepad1.left_stick_y * 10000) / 10000.0);  // Note: pushing stick forward gives negative value
        double lateral = ((int) (gamepad1.left_stick_x * 10000) / 10000.0);
        double yaw = ((int) (gamepad1.right_stick_x * 10000) / 10000.0);
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
     * Inspired by: <a href="https://github.com/cporter/ftc_app/blob/rr/pre-season/TeamCode/src/main/java/soupbox/Mecanum.java#L58">this code</a>
     */
    void doHeadlessXDrive() {
        if (resetIMUHeadingButton && !isResetIMUHeadingButtonHeld) {//Toggles claw state, then does servo toggling
            isResetIMUHeadingButtonHeld = true;
            imu.resetYaw();
        }
        else{
            isResetIMUHeadingButtonHeld = false;
        }
        if (toggleXDriveMode && !clawToggleButtonHeld) {//Toggles claw state, then does servo toggling
            clawState = !clawState;
            clawToggleButtonHeld = true;

            if (clawState) {//opens both claws
                primaryClaw.setPosition(OPEN);
                secondaryClaw.setPosition(OPEN);
                tertiaryClaw.setPosition(OPEN);
            } else {//closes claws
                primaryClaw.setPosition(CLOSED);
                secondaryClaw.setPosition(CLOSED);
                tertiaryClaw.setPosition(CLOSED);
            }
        } else {
            clawToggleButtonHeld = clawToggleButton;
        }




        final double lateral = Math.pow(((int) (gamepad1.left_stick_x * 10000) / 10000.0), 3.0);
        final double axial = Math.pow(((int) (-gamepad1.left_stick_y * 10000) / 10000.0), 3.0);

        final double yaw = Math.pow(((int) (gamepad1.right_stick_x * 10000) / 10000.0), 3.0);
        final double direction = -(Math.atan2(lateral, axial) + imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));
        final double speed = Math.min(1.0, Math.sqrt(lateral * lateral + axial * axial));

        final double lf = speed * Math.cos(direction + Math.PI / 4.0) + yaw;
        final double rf = speed * Math.sin(direction + Math.PI / 4.0) - yaw;
        final double lr = speed * Math.sin(direction + Math.PI / 4.0) + yaw;
        final double rr = speed * Math.cos(direction + Math.PI / 4.0) - yaw;

        frontLeft.setPower(lf);
        frontRight.setPower(rf);
        backLeft.setPower(lr);
        backRight.setPower(rr);

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
            raiseArmSlider.setTargetPosition(Math.max(Math.min(2000, targetPos), -4050));
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
        doSecondaryClawPitch();
        doSecondaryClawYaw();
        doClawStateToggle();
    }

    /**
     * Calculates secondary claw pitch position
     */
    void doSecondaryClawPitch() {
        if (pitchState != 0) {
            secondaryClawPitch.setPosition((secondaryClawPitch.getPosition()) + (pitchState * 0.03));
        }
    }

    /**
     * Calculates new yaw of secondary claw
     */
    void doSecondaryClawYaw() {
        if (resetServoOrientationButton) {
            secondaryClawYaw.setPosition(0.5);
            secondaryClawPitch.setPosition(initialPitchOffset);
        } else if (clawYawState != 0) {
            secondaryClawYaw.setPosition(secondaryClawYaw.getPosition() + (clawYawState * 0.05));
        }
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
                primaryClaw.setPosition(OPEN);
                secondaryClaw.setPosition(OPEN);
                tertiaryClaw.setPosition(OPEN);
            } else {//closes claws
                primaryClaw.setPosition(CLOSED);
                secondaryClaw.setPosition(CLOSED);
                tertiaryClaw.setPosition(CLOSED);
            }
        } else {
            clawToggleButtonHeld = clawToggleButton;
        }
    }

    /**
     * Just some QoL stuff
     */
    void goToPresetHeight() {
        if (highSpecimenLowBasketButton) {
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

        if (gamepad2.dpad_right) {
            clawYawState = -1;
        } else if (gamepad2.dpad_left) {
            clawYawState = 1;
        } else {
            clawYawState = 0;
        }

        if (gamepad2.dpad_up) {
            pitchState = 1;
        } else if (gamepad2.dpad_down) {
            pitchState = -1;
        } else {
            pitchState = 0;
        }

        sliderState = gamepad2.right_stick_y;
        halfSpeed = gamepad1.right_bumper;
        extenderState = -gamepad2.left_stick_y;
        clawToggleButton = gamepad2.b || gamepad2.right_stick_button;
        resetServoOrientationButton = gamepad2.a;
        touchSensorState = touchSensor.isPressed();
        highSpecimenLowBasketButton = gamepad2.right_bumper;
        clipSpecimen = gamepad2.left_bumper;
        resetIMUHeadingButton = gamepad1.x;
        toggleXDriveMode = gamepad1.y;
    }
}
