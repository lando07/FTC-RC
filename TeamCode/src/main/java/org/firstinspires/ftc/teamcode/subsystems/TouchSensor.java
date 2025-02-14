package org.firstinspires.ftc.teamcode.subsystems;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class TouchSensor {
    private com.qualcomm.robotcore.hardware.TouchSensor touchSensor;

    public TouchSensor(@NonNull OpMode opMode, String hwName) {
        touchSensor = opMode.hardwareMap.get(com.qualcomm.robotcore.hardware.TouchSensor.class, hwName);

    }

    public boolean getTouchSensorState() {
        return touchSensor.isPressed();
    }
}
