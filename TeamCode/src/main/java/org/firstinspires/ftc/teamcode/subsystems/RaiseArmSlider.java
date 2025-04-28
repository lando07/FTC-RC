package org.firstinspires.ftc.teamcode.subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;

@Config
public class RaiseArmSlider {

    private final DcMotor raiseArmSlider;
    public static int lowSpecimen = -100;
    public static int lowBasket = -2100;
    public static int highSpecimenLowBasket = -1920;
    public static int highBasket = -4150;
    public static int clipSpecimenOffsetAuto = 700;

    public static int clipSpecimenOffsetTeleOp = 425;
    public static int minHeightAuto = -125;
    public GamepadButton raiseArmButton = GamepadButton.Y;
    public GamepadButton lowerArmButton = GamepadButton.X;
    public GamepadButton highSpecimenLowBasketButton = GamepadButton.RIGHT_BUMPER;
    public GamepadButton clipSpecimenButton = GamepadButton.LEFT_BUMPER;
    public BiStateButtonBehavior clipSpecimenButtonBehavior = BiStateButtonBehavior.HOLD;
    private TouchSensor touchSensor;
    private GamepadController gamepad;

    public RaiseArmSlider(@NonNull OpMode opMode, String hwName) {
        raiseArmSlider = opMode.hardwareMap.get(DcMotor.class, hwName);
        raiseArmSlider.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        raiseArmSlider.setTargetPosition(0);
        raiseArmSlider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public RaiseArmSlider(@NonNull OpMode opMode, String hwName, GamepadController controller, TouchSensor tS) {
        raiseArmSlider = opMode.hardwareMap.get(DcMotor.class, hwName);
        raiseArmSlider.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        raiseArmSlider.setTargetPosition(0);
        raiseArmSlider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        gamepad = controller;
        gamepad.configureTristateButton(raiseArmButton, lowerArmButton);
        gamepad.configureBiStateButton(clipSpecimenButton, clipSpecimenButtonBehavior);
        gamepad.configureBiStateButton(raiseArmButton, BiStateButtonBehavior.HOLD);
        gamepad.configureBiStateButton(highSpecimenLowBasketButton, BiStateButtonBehavior.HOLD);
        touchSensor = tS;
    }

    public DcMotor getRaiseArmSlider() {
        return raiseArmSlider;
    }

    public void doHighSpecimenLowBasket() {
        raiseArmSlider.setPower(1);
        raiseArmSlider.setTargetPosition(highSpecimenLowBasket);
    }

    public void doHighSample() {
        raiseArmSlider.setPower(1);
        raiseArmSlider.setTargetPosition(highBasket);
    }

    public void clipSpecimenTeleOp() {
        raiseArmSlider.setTargetPosition(raiseArmSlider.getCurrentPosition() + clipSpecimenOffsetTeleOp);
    }

    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior zeroPowerBehavior) {
        raiseArmSlider.setZeroPowerBehavior(zeroPowerBehavior);
    }

    public int getCurrentPosition() {
        return raiseArmSlider.getCurrentPosition();
    }

    /**
     * Special method for autonomous, it has a more aggressive clip for reliability
     */
    public void clipSpecimenAuto() {
        raiseArmSlider.setTargetPosition(raiseArmSlider.getCurrentPosition() + clipSpecimenOffsetAuto);

    }

    public void resetHeightTeleOp() {
        raiseArmSlider.setPower(1);
        raiseArmSlider.setTargetPosition(0);
    }

    public void resetHeightAuto() {
        raiseArmSlider.setPower(1);
        raiseArmSlider.setTargetPosition(minHeightAuto);
    }

    public void setPower(double power) {
        raiseArmSlider.setPower(power);

    }

    public void setMode(DcMotor.RunMode mode) {
        raiseArmSlider.setMode(mode);
    }

    public void setTargetPosition(int position) {
        raiseArmSlider.setTargetPosition(position);

    }

    public void update() {
        if (touchSensor.isPressed() && !gamepad.getGamepadButtonValue(raiseArmButton)) {//this stops the motor from going any further down, but still allows it to go upward
            raiseArmSlider.setPower(0);//The zero power behavior set to float so the motor power is cut when the slider is fully retracted
            raiseArmSlider.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            raiseArmSlider.setTargetPosition(0);
            raiseArmSlider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        } else {
            int value = gamepad.getTristateButtonValue(raiseArmButton);
            if (value != 0) {
                int targetPos = raiseArmSlider.getCurrentPosition();
                if (value == -1) {
                    raiseArmSlider.setPower(1);
                    targetPos = raiseArmSlider.getCurrentPosition() + 200;
                } else if (value == 1) {
                    raiseArmSlider.setPower(1);
                    targetPos = raiseArmSlider.getCurrentPosition() - 200;
                }
                raiseArmSlider.setTargetPosition(Math.max(Math.min(2000, targetPos), -4050));
                raiseArmSlider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
            if (gamepad.getGamepadButtonValue(clipSpecimenButton)) {
                raiseArmSlider.setTargetPosition(getCurrentPosition() + clipSpecimenOffsetTeleOp);
            }
            if (gamepad.getGamepadButtonValue(highSpecimenLowBasketButton)) {
                raiseArmSlider.setTargetPosition(highSpecimenLowBasket);
            }
        }
        raiseArmSlider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
}
