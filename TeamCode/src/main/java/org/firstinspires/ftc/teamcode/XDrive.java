package org.firstinspires.ftc.teamcode;


import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.subsystems.BiStateButtonBehavior;
import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.DriveTrain;
import org.firstinspires.ftc.teamcode.subsystems.GamepadButton;
import org.firstinspires.ftc.teamcode.subsystems.GamepadController;
import org.firstinspires.ftc.teamcode.subsystems.RaiseArmSlider;
import org.firstinspires.ftc.teamcode.subsystems.axisBehavior;


/**
 * Ok, so this is our omega be-all-end-all class.
 */

@Config
@TeleOp(name = "XDrive", group = "Robot")
public class XDrive extends OpMode {
    /**
     * Stores the speed to change claw pitch, do not do values greater than 5, the servo isn't fast enough
     */
    public static int pitchSpeed = 3;
    /**
     * Stores the speed to change claw yaw, do not do values greater than 5, the servo isn't fast enough
     */
    public static int yawSpeed = 3;
    /**
     * Stores the initial pitch offset when the game starts. The claw should be sticking outwards
     */
    public static double initialPitchOffset = 0.65;
    public static boolean dynamicBrakingEnabled = false;
    private final axisBehavior armExtendAxis = axisBehavior.LEFT_STICK_Y;
    private final GamepadButton resetServoOrientationButton = GamepadButton.A;
    private final GamepadButton clawToggleButton = GamepadButton.B;
    private final BiStateButtonBehavior clawToggleBehavior = BiStateButtonBehavior.TOGGLE;
    private final GamepadButton yawRightButton = GamepadButton.D_PAD_RIGHT;
    private final GamepadButton yawLeftButton = GamepadButton.D_PAD_LEFT;
    private final GamepadButton pitchUpButton = GamepadButton.D_PAD_UP;
    private final GamepadButton pitchDownButton = GamepadButton.D_PAD_DOWN;
    private RaiseArmSlider raiseArmSlider;
    private DcMotor armExtender;
    private Claw primaryClaw;
    private Claw secondaryClaw;
    private Servo secondaryClawYaw;
    private Servo secondaryClawPitch;
    private Servo backStop;
    private DriveTrain driveTrain;
    private GamepadController controller1, controller2;

    @Override
    public void init() {
        controller1 = new GamepadController(gamepad1);


        controller2 = new GamepadController(gamepad2);
        controller2.configureBiStateButton(clawToggleButton, clawToggleBehavior);
        controller2.configureAxis(armExtendAxis);
        controller2.configureBiStateButton(resetServoOrientationButton, BiStateButtonBehavior.HOLD);
        controller2.configureTristateButton(yawLeftButton, yawRightButton);
        controller2.configureTristateButton(pitchUpButton, pitchDownButton);
        armExtender = hardwareMap.get(DcMotor.class, "armSlider");
        driveTrain = new DriveTrain(this, controller1);

        primaryClaw = new Claw(this, "primaryClaw", controller2);
        secondaryClaw = new Claw(this, "secondaryClaw", controller2);
        secondaryClawYaw = hardwareMap.get(Servo.class, "yaw");
        secondaryClawPitch = hardwareMap.get(Servo.class, "pitch");
        backStop = hardwareMap.get(Servo.class, "backStop");
        TouchSensor touchSensor = hardwareMap.get(TouchSensor.class, "touchSensor");
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
        if (raiseArmSlider.getCurrentPosition() < -100 && dynamicBrakingEnabled) {
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
        } else if (val != 0) {
            secondaryClawYaw.setPosition(secondaryClawYaw.getPosition() + (val * (yawSpeed / 100.0)));

        }
    }

    private void doSecondaryClawPitch() {
        int val = controller2.getTristateButtonValue(pitchUpButton);
        if (val != 0) {
            double pitch = Math.min(Math.max((secondaryClawPitch.getPosition()) + (val * (pitchSpeed / 100.0)),0.2), 0.7);
            secondaryClawPitch.setPosition(pitch);

        }
    }

    private void doArmExtension() {
            armExtender.setPower(-controller2.getAxisValue(armExtendAxis));
    }


}