package org.firstinspires.ftc.teamcode.subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Config
public class RaiseArmSlider {

    private final DcMotor raiseArmSlider;
    public static int lowSpecimen = -100;
    public static int lowBasket = -2100;
    public static int highSpecimenLowBasket = -1900;
    public static int highBasket = -4150;
    public static int clipSpecimenOffsetAuto = 700;

    public static int clipSpecimenOffsetTeleOp = 375;

    public RaiseArmSlider(@NonNull OpMode opMode, String hwName) {
        raiseArmSlider = opMode.hardwareMap.get(DcMotor.class, hwName);
        raiseArmSlider.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        raiseArmSlider.setTargetPosition(0);
        raiseArmSlider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
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
        raiseArmSlider.setTargetPosition(5);
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
}
