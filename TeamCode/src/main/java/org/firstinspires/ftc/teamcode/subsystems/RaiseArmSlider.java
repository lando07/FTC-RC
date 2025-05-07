package org.firstinspires.ftc.teamcode.subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.InstantAction;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;

@Config
public class RaiseArmSlider {

    public static int highSpecimenLowBasket = -1920;
    public static int highBasket = -4150;
    public static int clipSpecimenOffsetAuto = 700;
    public static int clipSpecimenOffsetTeleOp = 425;
    public static int minHeightAuto = -125;
    public static int minHeightTeleOp = -175;
    private final DcMotor raiseArmSlider;
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

    public void resetHeightAuto() {
        raiseArmSlider.setPower(1);
        raiseArmSlider.setTargetPosition(minHeightAuto);
    }

    public void setPower(double power) {
        raiseArmSlider.setPower(power);

    }
    public void resetHeightTeleOp(){
        raiseArmSlider.setPower(1);
        raiseArmSlider.setTargetPosition(minHeightTeleOp);
    }
    public void setMode(DcMotor.RunMode mode) {
        raiseArmSlider.setMode(mode);
    }

    public void setTargetPosition(int position) {
        raiseArmSlider.setTargetPosition(position);

    }

    public void update() {
        if (touchSensor.isPressed() && (!gamepad.getGamepadButtonValue(raiseArmButton) && !gamepad.getGamepadButtonValue(highSpecimenLowBasketButton))) {//this stops the motor from going any further down, but still allows it to go upward
            raiseArmSlider.setPower(0);//The zero power behavior set to float so the motor power is cut when the slider is fully retracted
            raiseArmSlider.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            raiseArmSlider.setTargetPosition(0);
            raiseArmSlider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            resetHeightTeleOp();
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
                raiseArmSlider.setTargetPosition(Math.max(Math.min(2000, targetPos), highSpecimenLowBasket));
                raiseArmSlider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
            if (gamepad.getGamepadButtonValue(clipSpecimenButton)) {
                clipSpecimenTeleOp();
            }
            if (gamepad.getGamepadButtonValue(highSpecimenLowBasketButton)) {
                doHighSpecimenLowBasket();
            }
        }
        raiseArmSlider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public InstantAction clipSpecimenAutoAction(){
        return new InstantAction(this::clipSpecimenAuto);
    }
    public InstantAction resetHeightAutoAction(){
        return new InstantAction(this::resetHeightAuto);
    }
    public InstantAction doHighSpecimenLowBasketAction(){
        return new InstantAction(this::doHighSpecimenLowBasket);
    }
}
