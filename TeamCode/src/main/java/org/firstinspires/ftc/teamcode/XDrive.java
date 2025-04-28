package org.firstinspires.ftc.teamcode;


import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.subsystems.*;


/**
 * Ok, so this is our omega be-all-end-all class.
 */

@Config
@TeleOp(name = "XDrive", group = "Robot")
public class XDrive extends OpMode {
    private final GamepadController GamePad1 = new GamepadController(gamepad1);
    private final GamepadController GamePad2 = new GamepadController(gamepad2);
    private axisBehavior armExtendAxis = axisBehavior.LEFT_STICK_Y;
    private GamepadButton resetServoOrientationButton = GamepadButton.A;
    private GamepadButton clawToggleButton = GamepadButton.B;
    private ButtonBehavior clawToggleBehavior = ButtonBehavior.TOGGLE;
    private GamepadButton yawRightButton = GamepadButton.D_PAD_RIGHT;
    private GamepadButton yawLeftButton = GamepadButton.D_PAD_LEFT;
    private ButtonBehavior yawBehavior = ButtonBehavior.TRI_STATE;
    private GamepadButton pitchUpButton = GamepadButton.D_PAD_UP;
    private GamepadButton pitchDownButton = GamepadButton.D_PAD_DOWN;
    private ButtonBehavior pitchBehavior = ButtonBehavior.TRI_STATE;
    private RaiseArmSlider raiseArmSlider;
    private DcMotor armExtender;
    private Claw primaryClaw;
    private Claw secondaryClaw;
    private Servo secondaryClawYaw;
    private Servo secondaryClawPitch;
    private Servo backStop;
    private TouchSensor touchSensor;
    private DriveTrain driveTrain;
    private GamepadController controller1, controller2;

    /**
     * Stores the speed to change claw pitch, do not do values greater than 5, the servo isn't fast enough
     */
    public static int pitchSpeed = 2;
    /**
     * Stores the speed to change claw yaw, do not do values greater than 5, the servo isn't fast enough
     */
    public static int yawSpeed = 2;
    /**
     * Stores the initial pitch offset when the game starts. The claw should be sticking outwards
     */
    public static double initialPitchOffset = 0.8;

    @Override
    public void init() {
        controller1 = new GamepadController(gamepad1);


        controller2 = new GamepadController(gamepad2);
        controller2.configureBiStateButton(clawToggleButton, clawToggleBehavior);
        controller2.configureAxis(armExtendAxis);
        controller2.configureBiStateButton(resetServoOrientationButton, ButtonBehavior.HOLD);
        controller2.configureTristateButton(yawLeftButton, yawRightButton);
        controller2.configureTristateButton(pitchUpButton, pitchDownButton);
        armExtender = hardwareMap.get(DcMotor.class, "armSlider");
        driveTrain = new DriveTrain(this, controller1);

        primaryClaw = new Claw(this, "primaryClaw", controller2);
        secondaryClaw = new Claw(this, "secondaryClaw", controller2);
        secondaryClawYaw = hardwareMap.get(Servo.class, "yaw");
        secondaryClawPitch = hardwareMap.get(Servo.class, "pitch");
        backStop = hardwareMap.get(Servo.class, "backStop");
        touchSensor = hardwareMap.get(TouchSensor.class, "touchSensor");
        raiseArmSlider = new RaiseArmSlider(this, "raiseArmSlider", controller2, touchSensor);

        telemetry.addData("Status:", "Initialized");
        telemetry.update();
    }

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

    @Override
    public void loop() {
        controller1.update();
        controller2.update();
        driveTrain.updateDriveTrainBehavior();
        raiseArmSlider.update();
        if (raiseArmSlider.getCurrentPosition() < -100) {
            driveTrain.setBrakingMode(DcMotor.ZeroPowerBehavior.FLOAT);
        } else {
            driveTrain.setBrakingMode(DcMotor.ZeroPowerBehavior.BRAKE);
        }
        doArmExtension();
        primaryClaw.update();
        secondaryClaw.update();
        doSecondaryClawPitch();
        doSecondaryClawYaw();


    }

    private void doSecondaryClawYaw() {
        int val = controller2.getTristateButtonValue(yawLeftButton);
        if (controller2.getGamepadButtonValue(resetServoOrientationButton)) {
            secondaryClawYaw.setPosition(0.5);
            secondaryClawPitch.setPosition(initialPitchOffset);
        } else if(val != 0){
            secondaryClawYaw.setPosition(secondaryClawYaw.getPosition() + (val * (yawSpeed / 100.0)));

        }
    }

    private void doSecondaryClawPitch() {
        int val = controller2.getTristateButtonValue(pitchUpButton);
        if(val != 0){
            secondaryClawPitch.setPosition((secondaryClawPitch.getPosition()) + (val * (pitchSpeed / 100.0)));

        }
    }

    private void doArmExtension() {
        armExtender.setPower(-controller2.getAxisValue(armExtendAxis));
    }


}