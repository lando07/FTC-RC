package org.firstinspires.ftc.teamcode.subsystems;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class RaiseArmSlider {
    private DcMotor raiseArmSlider;
    private final int lowSpecimen = -1300;
    private final int highSpecimenLowBasket = -2700;

    public RaiseArmSlider(@NonNull OpMode opMode, String hwName) {
        raiseArmSlider = opMode.hardwareMap.get(DcMotor.class, hwName);
        raiseArmSlider.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        raiseArmSlider.setTargetPosition(0);
        raiseArmSlider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void doLowSpecimen() {
        raiseArmSlider.setPower(1);
        raiseArmSlider.setTargetPosition(lowSpecimen);
    }

    public void doHighSpecimenLowBasket() {
        raiseArmSlider.setPower(1);
        raiseArmSlider.setTargetPosition(highSpecimenLowBasket);
    }

    public void clipSpecimen() {
        raiseArmSlider.setTargetPosition(highSpecimenLowBasket + 220);
    }

    public void resetHeight() {
        raiseArmSlider.setPower(1);
        raiseArmSlider.setTargetPosition(0);
    }
}
