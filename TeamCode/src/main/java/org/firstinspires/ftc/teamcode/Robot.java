package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * This is the main controller class for the Robot. Essentially, this provides an API or interface
 * where the robot can be interacted with without having to interact with any controller or Robot
 * code directly.
 */
public class Robot {
    /**
     * The left motor
     */
    private DcMotor leftDrive = null;
    /**
     * The right motor
     */
    private DcMotor rightDrive = null;
    /**
     * The Hang Arm Motor
     */
    private DcMotor HangArm = null;
    /**
     * The claw servo
     */
    private Servo Claw = null;
    /**
     * True starts it in half speed mode, false starts it in full speed mode
     */
    private boolean halfSpeed = false;
    /**
     * True starts it in reverse mode, false starts it in forward mode
     */
    private boolean reverse = false;

    /**
     * Main Constructor to initialize the Robot, but defaults to TeleOp(aka. manual) mode
     */
    public Robot() {
        this(false);
    }

    /**
     * Main Constructor to initialize the Robot
     *
     * @param autonomous if true, then the robot initializes the drive motors to use the encoder
     *                   if false, then the robot initializes in TeleOp mode
     */

    //TODO: Get april tags working and initialized in the robot class
    public Robot(boolean autonomous) {
        // Define and Initialize Motors
        // this initializes all motors in the program, should never need to be edited
        leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");


        //the hang arm is initialized to use the encoder to hold it's position, hence the extra init lines
        HangArm = hardwareMap.get(DcMotor.class, "HangArm");//adds the hangArm motor to the hangArm object
        HangArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);//Resets the encoder
        HangArm.setTargetPosition(0);//initializes the HangArm position, THIS IS REQUIRED OR THE PROGRAM WILL CRASH
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
        telemetry.addData(">", "Robot initialized");
    }

    /**
     * Sets the direction of the robot.
     *
     * @param forward if true, then the motor wheels are facing the front, if false, then idle wheels
     *                are facing the front
     */
    public void setDriveDirection(boolean forward) {
        leftDrive.setDirection(forward ? DcMotor.Direction.REVERSE : DcMotor.Direction.FORWARD);
        rightDrive.setDirection(forward ? DcMotor.Direction.FORWARD : DcMotor.Direction.REVERSE);
        reverse = !forward;
    }

    /**
     * Resets the current motor position of the HangArm to zero, should only be used if hangArm is
     * fully folded.
     */
    public void resetHangArmEncoder() {
        HangArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        HangArm.setTargetPosition(0);
        HangArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    /**
     * Sets the speed of the robot.
     *
     * @param halfSpeed if true, then the robot will run at half speed, if false, then the robot will
     *                  run at full speed
     */
    public void setHalfSpeed(boolean halfSpeed) {
        this.halfSpeed = halfSpeed;
    }

    /**
     * Sets the power of the motors. Depends on the state of halfSpeed. Check your code to see if you
     * forgot to set halfSpeed if the motors aren't running at the correct speed.
     *
     * @param leftPower  power to set the left motor
     * @param rightPower power to set the right motor
     */
    public void setMotorPower(double leftPower, double rightPower) {
        leftDrive.setPower(halfSpeed ? leftPower / 2 : leftPower);
        rightDrive.setPower(halfSpeed ? rightPower / 2 : rightPower);
    }

    /**
     * Sets the new Position of the HangArm, in reference to it's old position. Dependent on the state
     * of halfSpeed.
     *
     * @param newHangArmPosition the increment to add to the current position of the HangArm
     */
    public void setNewHangArmPosition(int newHangArmPosition) {
        if (reverse)//yes, this works, this is normal
            newHangArmPosition *= -1;
        int targetPos = halfSpeed ? HangArm.getCurrentPosition() + newHangArmPosition / 2 : (HangArm.getCurrentPosition() + newHangArmPosition);
        HangArm.setTargetPosition(Math.max(Math.min(targetPos, -50), -2355));//upper and lower limit for claw position so it doesn't drag on the ground
        HangArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    /**
     * Opens the claw. Claw goes from .12 (open) to .45 (closed)
     */
    public void openClaw() {
        Claw.setPosition(0.45);
    }

    /**
     * Closes the claw. Claw goes from .12 (open) to .45 (closed)
     */
    public void closeClaw() {
        Claw.setPosition(0.12);
    }

    /**
     * Sets the direction of the robot.
     * @param reverse if false, then the motor wheels are facing the front, if true, then idle wheels are the front.
     *                Essentially, if false, you have front-wheel-drive, if true, you have rear-wheel-drive
     */
    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    /*
        The methods below are if you need to do anything with the objects themselves outside of what the
        methods above do.
    */
    public DcMotor getLeftDrive() {
        return leftDrive;
    }

    public void setLeftDrive(DcMotor leftDrive) {
        this.leftDrive = leftDrive;
    }

    public DcMotor getRightDrive() {
        return rightDrive;
    }

    public void setRightDrive(DcMotor rightDrive) {
        this.rightDrive = rightDrive;
    }

    public DcMotor getHangArm() {
        return HangArm;
    }

    public void setHangArm(DcMotor hangArm) {
        HangArm = hangArm;
    }

    public Servo getClaw() {
        return Claw;
    }

    public void setClaw(Servo claw) {
        Claw = claw;
    }

    public boolean isHalfSpeed() {
        return halfSpeed;
    }
}
