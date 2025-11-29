package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.subsystems.RaiseArmSlider.clipSpecimenOffsetTeleOp;
import static org.firstinspires.ftc.teamcode.subsystems.RaiseArmSlider.highSpecimenLowBasket;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.bosch.BHI260IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.RaiseArmSlider;

/**
 * Ok, so this is our omega be-all-end-all class.
 * <p>
 * Here's the button mappings:
 * </p>
 * <ul>
 *   <li><b>Gamepad 1:</b>
 *     <ul>
 *       <li>Left Stick: translational movement</li>
 *       <li>Right Stick: rotational movement, only by tilting it left or right</li>
 *       <li>Right bumper: hold for half-speed</li>
 *       <li>X: Reset IMU Heading</li>
 *       <li>Y: Toggle between headless and headed mode(DISABLED)</li>
 *     </ul>
 *   </li>
 *   <li><b>Gamepad 2:</b>
 *     <ul>
 *       <li>Y: Raise Slider</li>
 *       <li>X: Lower Slider</li>
 *       <li>Left Stick Y: Extend/retract secondary arm</li>
 *       <li>A: Reset servo pitch and yaw on secondary claw (great for once a sample has been obtained)</li>
 *       <li>B: toggle open/close all claws (for simplicity)</li>
 *       <li>D-Pad Left/Right: Claw yaw</li>
 *       <li>D-Pad Up/Down: Claw Pitch</li>
 *       <li>Left Bumper: Clip Specimen</li>
 *       <li>Right Bumper: Raise slider to high specimen</li>
 *       <li>Right Trigger: Reset IMU Heading</li>
 *     </ul>
 *   </li>
 * </ul>
 */
@Disabled
@Config
@TeleOp(name = "LegacyXDrive", group = "Robot")
public class LegacyXDrive extends OpMode {
    /**
     * Stores the initial pitch offset when the game starts. The claw should be sticking outwards
     */
    public static double initialPitchOffset = 0.65;
    /**
     * Stores the speed to change claw pitch, do not do values greater than 5, the servo isn't fast enough
     */
    public static int pitchSpeed = 3;
    /**
     * Stores the speed to change claw yaw, do not do values greater than 5, the servo isn't fast enough
     */
    public static int yawSpeed = 5;
    public static double axialGain = 3;
    public static double lateralGain = 3;
    public static double yawGain = 3;
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
    private RaiseArmSlider raiseArmSlider;
    /**
     * The claw servo for the static claw
     */
    private Claw primaryClaw;
    /**
     * The claw servo for the secondary claw
     */
    private Claw secondaryClaw;
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
    private Servo backStop;
    /**
     * The touch sensor to stop the slider motor from retracting after it has been fully retracted
     */
    private TouchSensor touchSensor;
    /**
     * Stores the imu object for heading calculations in headless mode
     */
    private BHI260IMU imu;
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
    private volatile boolean halfSpeed = false;
    /**
     * True if open, false if closed, starts false
     */
    private boolean clawState = true;
    /**
     * Stores held button state for de-bouncing and anti-infinite loop stuff
     */
    private boolean clawToggleButtonHeld = false;
    /**
     * Stores button/bumper state to raise arm to preset height for the first specimen hook
     */
    private volatile boolean highSpecimenLowBasketButton;
    /**
     * Stores the state of the button used to reset the imu heading in case of drift
     */
    private volatile boolean resetIMUHeadingButton;
    /**
     * Supplemental boolean for de-bouncing and infinite toggling
     */
    private volatile boolean isResetIMUHeadingButtonHeld;
    /**
     * Stores the button state used to toggle between headed and headless mode
     */
    private volatile boolean toggleDriveModeButton;
    /**
     * Supplemental boolean for de-bouncing and infinite toggling
     */
    private boolean toggleDriveModeButtonHeld;
    /**
     * True to start headless, false to start headed
     */
    private boolean fieldOrientedMode = true;

    /**
     * Code to run ONCE when the driver hits INIT. Mainly just initializes all the robot's features
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
        raiseArmSlider = new RaiseArmSlider(this, "raiseArmSlider");
        armExtender = hardwareMap.get(DcMotor.class, "armSlider");


        primaryClaw = new Claw(this, "primaryClaw");
        secondaryClaw = new Claw(this, "secondaryClaw");
        secondaryClawYaw = hardwareMap.get(Servo.class, "yaw");
        secondaryClawPitch = hardwareMap.get(Servo.class, "pitch");
        backStop = hardwareMap.get(Servo.class, "backStop");
        touchSensor = hardwareMap.get(TouchSensor.class, "touchSensor");

        imu = hardwareMap.get(BHI260IMU.class, "imu");
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(MecanumDrive.PARAMS.logoFacingDirection, MecanumDrive.PARAMS.usbFacingDirection)));
        imu.resetYaw();
        telemetry.addData("Status:", "Initialized");
        telemetry.update();
    }

    /**
     * Code to run continuously before start but after init. Not used as of right now
     */
    @Override
    public void init_loop() {//not used as of right now

    }

    /**
     * Code to run once when the driver presses Start. Mainly just starts the robot in a preset config
     * according to our strategy.
     */
    @Override
    public void start() {
        primaryClaw.openClaw();
        secondaryClaw.openClaw();
        secondaryClawYaw.setPosition(initialPitchOffset);//This starts the servo in the middle of both extrema
        secondaryClawPitch.setPosition(0.5);

        backStop.setPosition(0);

        raiseArmSlider.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        raiseArmSlider.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        raiseArmSlider.setTargetPosition(0);
        raiseArmSlider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        raiseArmSlider.setPower(1);

        armExtender.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    /**
     * Code to run continuously when the driver hits Start, after start(). This does everything we need
     */
    @Override
    public void loop() {
        getControllerData();
        doMecanumDrive();
        doSlider();
        doClaw();
        goToPresetHeight();

        //You can add more telemetry by mimicking the method call structure here, very useful since line-by-line debugging is broken
        telemetry.addData("Current Drive Mode", fieldOrientedMode ? "headless" : "headed");
        telemetry.addData("Axial Movement:", Math.pow(((int) (-gamepad1.left_stick_y * 10000) / 10000.0), axialGain));
        telemetry.addData("Lateral Movement:", Math.pow(((int) (gamepad1.left_stick_x * 10000) / 10000.0), lateralGain));
        telemetry.addData("Yaw Movement:", Math.pow(((int) (gamepad1.right_stick_x * 10000) / 10000.0), yawGain));
        telemetry.addData("SliderCurrentPos: ", raiseArmSlider.getCurrentPosition());
        telemetry.addData("ExtCurrPos:", armExtender.getCurrentPosition());
        telemetry.addData("PrimaryClaw Pos:", primaryClaw.getPosition());
        telemetry.addData("SecondaryClaw pos", secondaryClaw.getPosition());
        telemetry.addData("Pitch:", secondaryClawPitch.getPosition());
        telemetry.addData("Yaw:", secondaryClawYaw.getPosition());
        telemetry.addData("IMU Heading (deg)", imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES));

        telemetry.update();
    }

    /**
     * Helper method to handle which mode the robot is in for mecanum drive. The driver can
     * hot-swap between both headless and headed mode without losing data or heading position
     */
    void doMecanumDrive() {
        if (fieldOrientedMode) {
            doHeadlessXDrive();
        } else {
            doXDrive();
        }
        if (toggleDriveModeButton && !toggleDriveModeButtonHeld) {//Toggles claw state, then does servo toggling
            fieldOrientedMode = !fieldOrientedMode;
            toggleDriveModeButtonHeld = true;

        } else {
            toggleDriveModeButtonHeld = toggleDriveModeButton;
        }
    }

    /**
     * Runs Mecanum drive, the direction of the robot is the forward
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
     * <br />
     * Does some funny math so the robot always goes in the direction you input, regardless of where it's pointed.
     */
    void doHeadlessXDrive() {
        if (resetIMUHeadingButton && !isResetIMUHeadingButtonHeld) {//resets headless state heading
            isResetIMUHeadingButtonHeld = true;
            imu.resetYaw();
        } else {
            isResetIMUHeadingButtonHeld = false;
        }


        final double lateral = Math.pow(((int) (gamepad1.left_stick_x * 10000) / 10000.0), lateralGain);
        final double axial = Math.pow(((int) (-gamepad1.left_stick_y * 10000) / 10000.0), axialGain);
        final double yaw = Math.pow(((int) (gamepad1.right_stick_x * 10000) / 10000.0), yawGain);

        final double direction = -(Math.atan2(lateral, axial) + imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));// I have zero clue how this works
        final double speed = Math.min(1.0, Math.sqrt(lateral * lateral + axial * axial));//vector normalization(I think) + pythagorean theorem

        final double vCos = speed * Math.cos(direction + Math.PI / 4.0);//I hate trig
        final double vSin = speed * Math.sin(direction + Math.PI / 4.0);//I have no clue how this even works
        double lf = vCos + yaw;//These 4 lines calculate the motor powers
        double rf = vSin - yaw;
        double lr = vSin + yaw;
        double rr = vCos - yaw;

        if (halfSpeed) {
            lf *= 0.5;
            rf *= 0.5;
            lr *= 0.5;
            rr *= 0.5;

        }
        //Actually what makes the robot moves
        frontLeft.setPower(lf);
        frontRight.setPower(rf);
        backLeft.setPower(lr);
        backRight.setPower(rr);

    }

    /**
     * Does slider motor logic, including protecting the slide from over-retraction and parasitic power draw,
     * resetting the motor encoder to account for drift, and changing motor behavior based on the
     * slider's height
     */
    void doSlider() {
        if (touchSensorState && !gamepad2.y) {//this stops the motor from going any further down, but still allows it to go upward
            raiseArmSlider.setPower(0);//The zero power behavior set to float so the motor power is cut when the slider is fully retracted
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
            //do not modify the 100.0, since pitch
            //speed is a whole number, it needs to be
            //divided to work with the servos correctly
            secondaryClawPitch.setPosition((secondaryClawPitch.getPosition()) + (pitchState * (pitchSpeed / 100.0)));
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
            //do not modify the 100.0, since yaw
            //speed is a whole number, it needs to be
            //divided to work with the servos correctly
            secondaryClawYaw.setPosition(secondaryClawYaw.getPosition() + (clawYawState * (yawSpeed / 100.0)));
        }
    }

    /**
     * Calculates the speed at which the arm extends
     */
    void doArmExtension() {
        //due to the unpredictable and high friction on the rack + pinion,
        // I just set the motor power without encoder position for high responsiveness
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
        if (clawState) {//opens both claws
            primaryClaw.openClaw();
            secondaryClaw.openClaw();
        } else {//closes claws
            primaryClaw.closeClaw();
            secondaryClaw.closeClaw();
        }
        if (clawToggleButton && !clawToggleButtonHeld) {//Toggles claw state, then does servo toggling
            clawState = !clawState;
            clawToggleButtonHeld = true;

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
            raiseArmSlider.setTargetPosition(raiseArmSlider.getCurrentPosition() + clipSpecimenOffsetTeleOp);
        }
        raiseArmSlider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    /**
     * Grabs all controller data, then stores that stuff for methods or whatever
     */
    void getControllerData() {//drivetrain and gamepad1's joysticks are handled in mecanumDrive() and it's method calls, every thing else is handled here

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

        if (gamepad2.y) {
            sliderState = 1;
        } else if (gamepad2.x) {
            sliderState = -1;
        } else {
            sliderState = 0;
        }
        halfSpeed = gamepad1.right_bumper;
        extenderState = -gamepad2.left_stick_y;
        clawToggleButton = gamepad2.b || gamepad2.right_stick_button;
        resetServoOrientationButton = gamepad2.a;
        touchSensorState = touchSensor.isPressed();
        highSpecimenLowBasketButton = gamepad2.right_bumper;
        clipSpecimen = gamepad2.left_bumper;
        resetIMUHeadingButton = gamepad2.right_trigger > 0.8 || gamepad1.x;
        //This is disabled, should only be used for debugging
//        toggleDriveModeButton = gamepad1.y;
    }
}
