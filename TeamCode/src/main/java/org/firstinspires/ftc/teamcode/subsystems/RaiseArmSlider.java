package org.firstinspires.ftc.teamcode.subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Config
public class RaiseArmSlider {
    private final DcMotor raiseArmSlider;
    public static int lowSpecimen = -100;
    public static int lowBasket = -2100;
    public static int highSpecimenLowBasket = -1850;
    public static int highBasket = -4050;
    public static int clipSpecimenOffSet = 375;

    public RaiseArmSlider(@NonNull OpMode opMode, String hwName) {
        raiseArmSlider = opMode.hardwareMap.get(DcMotor.class, hwName);
        raiseArmSlider.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        raiseArmSlider.setTargetPosition(0);
        raiseArmSlider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void doHighSpecimenLowBasket() {
        raiseArmSlider.setPower(1);
        raiseArmSlider.setTargetPosition(highSpecimenLowBasket);
    }

    public void doHighSpecimenLowBasket(double power) {
        raiseArmSlider.setPower(power);
        raiseArmSlider.setTargetPosition(highSpecimenLowBasket);
    }

    public void clipSpecimen() {
        raiseArmSlider.setTargetPosition(highSpecimenLowBasket + clipSpecimenOffSet);
    }

    /**
     * Special method for autonomous
     *
     * @param offSet extra offset needed if clip doesn't work with default
     */
    public void clipSpecimen(int offSet) {
        raiseArmSlider.setTargetPosition(highSpecimenLowBasket + clipSpecimenOffSet + offSet);

    }

    public void resetHeight() {
        raiseArmSlider.setPower(1);
        raiseArmSlider.setTargetPosition(0);
    }
}
