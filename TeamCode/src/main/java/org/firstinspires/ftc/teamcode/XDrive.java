package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.subsystems.RaiseArmSlider.highSpecimenLowBasket;
import static org.firstinspires.ftc.teamcode.subsystems.RaiseArmSlider.clipSpecimenOffsetTeleOp;


import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.bosch.BHI260IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
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

@Config
@TeleOp(name = "XDrive", group = "Robot")
public class XDrive extends OpMode {

    @Override
    public void init() {

    }

    @Override
    public void loop() {

    }
}