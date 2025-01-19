package org.firstinspires.ftc.teamcode.subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
@Config
public class RaiseArmSlider {
    private DcMotor raiseArmSlider;
    public static int lowSpecimen = -1300;
    public static int highSpecimenLowBasket = -2700;
    public static int clipSpecimenOffSet = 220;

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
        raiseArmSlider.setTargetPosition(highSpecimenLowBasket + clipSpecimenOffSet);
    }

    public void resetHeight() {
        raiseArmSlider.setPower(1);
        raiseArmSlider.setTargetPosition(0);
    }
}
