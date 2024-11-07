package org.firstinspires.ftc.teamcode;/* Copyright (c) 2017 FIRST. All rights reserved.
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
//MISSION CRITICAL IMPORTS, DON'T TOUCH EVER!
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

/*
 * This OpMode executes a Tank Drive control TeleOp a direct drive robot
 * The code is structured as an Iterative OpMode
 *
 * In this mode, the left and right joysticks control the left and right motors respectively.
 * Pushing a joystick forward will make the attached motor drive forward.
 * It raises and lowers the claw using the Gamepad Y and A buttons respectively.
 * It also opens and closes the claws slowly using the left and right Bumper buttons.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
 */


//UNLESS I SAY SO, DON'T MODIFY ANYTHING IF YOU DON'T KNOW ***EXACTLY*** WHAT YOU'RE DOING!
//REMEMBER, CHANGES WILL ***NOT*** TAKE EFFECT UNTIL THE CODE IS PUSHED TO THE PHONE!
@TeleOp(name = "Test Tank Drive", group = "Robot")
public class TankDrive extends OpMode {
    /* Declare OpMode members. */
    public DcMotor leftDrive = null;
    public DcMotor rightDrive = null;
    public DcMotor HangArm = null;
    public Servo Claw = null;

    private boolean halfSpeed = false;//change this to true if you want it to start in half-speed mode

    private boolean reverse = false;//change this to true if you want it to start in reverse mode

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        // Define and Initialize Motors
        // DON'T TOUCH ANY OF THIS CODE IN THIS METHOD, THE ROBOT WILL BREAK IF EVEN A SINGLE CHARACTER IS MISSING
        leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");

        //the hang arm is initialized to use the encoder to hold it's position, hence the extra init lines
        HangArm = hardwareMap.get(DcMotor.class, "HangArm");
        HangArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        HangArm.setTargetPosition(0);
        HangArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        HangArm.setPower(1);
        Claw = hardwareMap.get(Servo.class, "Claw");

        // To drive forward, our robot needs the motor on one side to be reversed, because the axles point in opposite directions.
        // NO TOUCHY TOUCHY
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        rightDrive.setDirection(DcMotor.Direction.FORWARD);
        HangArm.setDirection(DcMotor.Direction.REVERSE);

        //DO NOT UNCOMMENT THIS CODE AS OF RIGHT NOW
        // If there are encoders connected, switch to RUN_USING_ENCODER mode for greater accuracy
        // leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        // rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Send telemetry message to signify robot waiting;
        telemetry.addData(">", "Robot Ready.  Press Play.");    //
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
             // NOTHING, AS OF RIGHT NOW GOES HERE, LEAVE BLANK
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
        if (gamepad1.y) {//half-speed button, not for analog input, remap it to any button not in use
            halfSpeed = true;
        }
        if (gamepad1.a) {//full-speed button, not for analog input, remap it to any button not in use
            halfSpeed = false;
        }
        if (gamepad1.dpad_up) {//The drive wheels are facing forward, remap this button to any other button not in use
            leftDrive.setDirection(DcMotor.Direction.REVERSE);
            rightDrive.setDirection(DcMotor.Direction.FORWARD);
            HangArm.setDirection(DcMotor.Direction.REVERSE);
            reverse = false;
        }
        if (gamepad1.dpad_down) {//the idle wheels are facing forward, remap this button to any other button not in use
            leftDrive.setDirection(DcMotor.Direction.FORWARD);
            rightDrive.setDirection(DcMotor.Direction.REVERSE);
            reverse = true;//I can't set the claw motor to reverse or the algorithm controlling it's position freaks out
        }
        //ctrl + click on any of these 3 methods to go to their code
        double[] telem = doDriveTrain();//the array is for telemetry only, NO TOUCH!
        doHangArm();//NO TOUCH!
        doClaw();//NO TOUCH!
        if (gamepad1.x) {// motor pos reset --> only do this when the robot arm is fully folded up
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
        telemetry.addData("Current HangArm Speed", (int) (gamepad1.left_stick_y * 100));
    }

    double[] doDriveTrain() {
        double left = 0;//used to store the calculated power of the left motor
        double right = 0;//same, but for the right
        //the joysticks go negative as you push them foreward, so I used double-negatives to get it to add power, rather than subtract
        //the reverse keeps the value intact, which makes it go negative as the joystick goes negative
        if (reverse) {
            right += gamepad1.right_stick_y;
            left += gamepad1.left_stick_y;
        } else {
            left -= gamepad1.left_stick_y;
            right -= gamepad1.right_stick_y;
        }
        if (halfSpeed) {
            leftDrive.setPower(left / 2);
            rightDrive.setPower(right / 2);
        } else {
            leftDrive.setPower(left);
            rightDrive.setPower(right);
        }
        return new double[]{left, right};
    }

    void doHangArm() {
        //hang arm code
        //left_trigger and right_trigger are analog triggers, DO NOT MODIFY ANYTHING BELOW UNLESS NOTED OTHERWISE
        int targetPos = 0;//this is the variable used to store the new calculated position of the motor, in reference to the old point
        //the formula is as follows
        //current position + (left trigger(positive direction) - right trigger(negative direction) * increment var for sensitivity)
        if (gamepad1.left_trigger - gamepad1.right_trigger > 0.01 || gamepad1.left_trigger - gamepad1.right_trigger < -0.01) {//compensates for trigger drift
            if (reverse) {
                if (halfSpeed) {
                    targetPos = HangArm.getCurrentPosition() + (int) ((gamepad1.left_trigger - gamepad1.right_trigger) * 50);//this is half speed +
                    // reversed polarity
                } else {
                    targetPos = HangArm.getCurrentPosition() + (int) ((gamepad1.left_trigger - gamepad1.right_trigger) * 100);//this is full speed +
                    // reversed polarity
                }
            } else {
                if (halfSpeed) {
                    targetPos = HangArm.getCurrentPosition() - (int) ((gamepad1.left_trigger - gamepad1.right_trigger) * 50);//this is half speed +
                    // normal polarity
                } else {
                    targetPos = HangArm.getCurrentPosition() - (int) ((gamepad1.left_trigger - gamepad1.right_trigger) * 100);//this if full speed +
                    //normal polarity
                }
            }
            HangArm.setTargetPosition(Math.max(Math.min(targetPos, -50), -2620));//upper and lower limit for claw position so it doesn't drag on the ground
            HangArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);//NO TOUCH!
        }

        if (gamepad1.b) {
            HangArm.setTargetPosition(HangArm.getCurrentPosition() - 300);//on hang, if the triggers can't get the motor to move, this will help
            HangArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);//NO TOUCH!
        }
    }

    void doClaw() {
        //claw goes from .12 to .28
        if (gamepad1.dpad_right) {//close servo
            //NO TOUCHY TOUCHY
            Claw.setPosition(.45);
        } else if (gamepad1.dpad_left) {// open servo
            //NO TOUCHY TOUCHY
            Claw.setPosition(0.12);
        }
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }
}
