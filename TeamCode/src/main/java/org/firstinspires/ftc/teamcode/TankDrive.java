package org.firstinspires.ftc.teamcode;

/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
//MISSION CRITICAL IMPORTS, these import the controller variables, and allow the
// phone to recognize our class as executable, will probably never need to be updated

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/*
 * This OpMode executes a Tank Drive control TeleOp a direct drive robot
 * The code is structured as an Iterative OpMode
 *
 * In this mode, the left and right joysticks control the left and right motors respectively.
 * Pushing a joystick forward will make the attached motor drive forward.
 * It raises and lowers the claw using the left and right triggers respectively.
 * It also opens and closes the claws slowly using the left and right dpad buttons.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
 */

//REMEMBER, CHANGES WILL ***NOT*** TAKE EFFECT UNTIL THE CODE IS PUSHED TO THE PHONE!
@Disabled
@TeleOp(name = "Tank Drive", group = "Robot")
public class TankDrive extends OpMode {
    /* Declare OpMode members. */
    /**
     * The left motor
     */
    public DcMotor leftDrive = null;
    /**
     * The right motor
     */
    public DcMotor rightDrive = null;
    /**
     * The Hang Arm Motor
     */
    public DcMotor HangArm = null;
    /**
     * The claw
     */
    public Servo Claw = null;
    /**
     * True starts it in half speed mode, false starts it in full speed mode
     */
    private volatile boolean halfSpeed = false;
    /**
     * True starts the robot in reverse mode, false starts it in forward mode
     */
    private boolean reverse = false;
    /**
     * Stores the value of the button used to open the claw
     */
    private volatile boolean open_claw;
    /**
     * Stores the value of the button used to close the claw
     */
    private volatile boolean close_claw;
    /**
     * Stores the value of the button used to boost the hangarm motor power to help suspend during
     * a hang
     */
    private volatile boolean hangBoost;
    /**
     * Stores the value of the joystick that controls left motor.
     * Works on a range of 0 - 1
     */
    private volatile double left_drive_joystick;
    /**
     * Stores the computed value of the joystick or formula to determine the power of the right motor.
     * Works on a range of 0 - 1
     */
    private volatile double right_drive_joystick;
    /**
     * Stores the computed value of the formula to determine whether the claw is turning clockwise
     * or counterclockwise
     */
    private volatile double arm_direction;

    /**
     * Stores the value of the button to enable half speed
     */
    private volatile boolean halfSpeedButton;

    /**
     * Stores the value of the button to enable full speed
     */
    private volatile boolean fullSpeedButton;

    /**
     * Stores the value of the button to reset the motor
     */
    private volatile boolean motorPosResetButton;

    /**
     * Stores the value of the button to put the robot in forward mode
     */
    private volatile boolean forwardButton;

    /**
     * Stores the value of the button to put the robot in reverse mode
     */
    private volatile boolean reverseButton;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        // Define and Initialize Motors
        // this initializes all motors in the program, should never need to be edited
        leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");


        //the hang arm is initialized to use the encoder to hold it's position, hence the extra init lines
        HangArm = hardwareMap.get(DcMotor.class, "HangArm");//adds the hangArm motor to the hangArm object
        HangArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);//Resets the encoder
        HangArm.setTargetPosition(0);//initializes the hangarm position, THIS IS REQUIRED OR THE PROGRAM WILL CRASH
        HangArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);//starts the motor
        HangArm.setPower(1);//max strength so the motor holds it position with all available torque


        Claw = hardwareMap.get(Servo.class, "Claw");//initializes and stores the claw servo in Claw
        Claw.setPosition(.45);//start the claw as closed


        // To drive forward, our robot needs the motor on one side to be reversed, because the axles point in opposite directions.
        //The HangArm is reversed as it's pointed the same direction as the left drive
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        rightDrive.setDirection(DcMotor.Direction.FORWARD);
        HangArm.setDirection(DcMotor.Direction.REVERSE);


        // Send telemetry message to signify robot waiting;
        telemetry.addData(">", "Robot Ready.  Press Play.");    //
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {//this lifts the claw up so it's not dragging on the ground
        HangArm.setTargetPosition(HangArm.getCurrentPosition() - 50);
        HangArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {

        getControllerData();
        if (halfSpeedButton) {//half-speed button, not for analog input, remap it to any button not in use
            halfSpeed = true;
        } else if (fullSpeedButton) {//full-speed button, not for analog input, remap it to any button not in use
            halfSpeed = false;
        }


        if (forwardButton) {//The drive wheels are facing forward, remap this button to any other button not in use
            leftDrive.setDirection(DcMotor.Direction.REVERSE);
            rightDrive.setDirection(DcMotor.Direction.FORWARD);
            HangArm.setDirection(DcMotor.Direction.REVERSE);
            reverse = false;
        } else if (reverseButton) {//the idle wheels are facing forward, remap this button to any other button not in use
            leftDrive.setDirection(DcMotor.Direction.FORWARD);
            rightDrive.setDirection(DcMotor.Direction.REVERSE);
            reverse = true;//I can't set the claw motor to reverse or the algorithm controlling it's position freaks out
        }


        //ctrl + click on any of these 3 methods-anything with () behind it - to go to their code
        double[] telem = doDriveTrain();//the array is for telemetry only, NO TOUCH!
        doHangArm();//NO TOUCH, runs the hang arm code and the hangarm will not work if removed
        doClaw();//NO TOUCH, runs the claw code and the claw will not work if removed!


        if (motorPosResetButton) {// motor pos reset --> only do this when the robot arm is fully folded up
            //NO TOUCHY TOUCHY
            HangArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            HangArm.setTargetPosition(0);
            HangArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }


        // Send telemetry messages so we can see what tf the robot is up to
        telemetry.addData("left", "%.2f", telem[0]);
        telemetry.addData("right", "%.2f", telem[1]);
        telemetry.addData("HangArm: ", HangArm.getCurrentPosition());
        telemetry.addData("Servo Pos: ", "%.7f", Claw.getPosition());
        telemetry.addData("Current HangArm Speed", arm_direction);
    }

    double[] doDriveTrain() {
        double left = 0;//used to store the calculated power of the left motor
        double right = 0;//same, but for the right
        //the joysticks go negative as you push them forward, so I used double-negatives to get it to add power, rather than subtract
        //the reverse keeps the value intact, which makes it go negative as the joystick goes negative
        if (reverse) {
            right += right_drive_joystick;
            left += left_drive_joystick;
        } else {
            left -= left_drive_joystick;
            right -= right_drive_joystick;
        }


        if (halfSpeed) {
            left = left / 2; //halves the computed speed
            right = right / 2; //
        }


        leftDrive.setPower(left);
        rightDrive.setPower(right);


        return new double[]
                {left, right};
    }

    void doHangArm() {
        //hang arm code
        //left_trigger and right_trigger are analog triggers, DO NOT MODIFY ANYTHING BELOW UNLESS NOTED OTHERWISE
        int targetPos = 0;//this is the variable used to store the new calculated position of the motor, in reference to the old point
        //the formula is as follows
        //current position + (arm_direction * increment var (for sensitivity))
        if (arm_direction > 0.01 || arm_direction < -0.01) {//compensates for trigger drift
            if (reverse) {
                if (halfSpeed) {
                    //this is half speed +
                    // reversed polarity
                    targetPos = HangArm.getCurrentPosition() + (int) (arm_direction * 50);
                } else {
                    //this is full speed +
                    // reversed polarity
                    targetPos = HangArm.getCurrentPosition() + (int) (arm_direction * 100);
                }
            } else {
                if (halfSpeed) {
                    //this is half speed +
                    // normal polarity
                    targetPos = HangArm.getCurrentPosition() - (int) (arm_direction * 50);
                } else {
                    //this if full speed +
                    //normal polarity
                    targetPos = HangArm.getCurrentPosition() - (int) (arm_direction * 100);
                }
            }
            HangArm.setTargetPosition(Math.max(Math.min(targetPos, -50), -2355));//upper and lower limit for claw position so it doesn't drag on the ground
            HangArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);//Do not remove, this must go after every
            //instance of HangArm.setTargetPosition()
        }

        if (motorPosResetButton) {
            HangArm.setTargetPosition(HangArm.getCurrentPosition() - 300);//on hang, if the triggers can't get the motor to move, this will help
            HangArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);//Do not remove, this must go after every
            //instance of HangArm.setTargetPosition()
        }
    }

    void doClaw() {
        //claw goes from .12 (open) to .45 (closed)
        if (close_claw) {//close servo

            Claw.setPosition(.45);//do not change value unless claw design is changed
            //Claw.setPosition(1);
        } else if (open_claw) {// open servo
            Claw.setPosition(0.12);//do not change value unless claw design is changed
            //Claw.setPosition(0);
        }
    }

    void getControllerData() {
        //Here are the button mappings, change them as you please
        open_claw = gamepad1.dpad_left;//this is meant to be any digital button
        close_claw = gamepad1.dpad_right;//this is meant to be any digital button
        hangBoost = gamepad1.b;//this is meant to be any digital button
        left_drive_joystick = gamepad1.left_stick_y;//this is for the joysticks only, both this and right_drive shouldn't need to be changed, unless for arcade mode
        right_drive_joystick = gamepad1.right_stick_y;//this is for the joysticks only
        arm_direction = gamepad1.left_trigger - gamepad1.right_trigger;//this is meant for the joysticks or the triggers
        //the bumpers are currently not used, but are digital buttons
        halfSpeedButton = gamepad1.y;//this is meant to be any digital button
        fullSpeedButton = gamepad1.a;//this is meant to be any digital button
        forwardButton = gamepad1.dpad_up;//this is meant to be any digital button
        reverseButton = gamepad1.dpad_down;//this is meant to be any digital button

    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }
}
