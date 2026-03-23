package org.firstinspires.ftc.teamcode.subsystems.DECODEExclusive;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;

import org.firstinspires.ftc.teamcode.subsystems.Universal.GamepadController;
import org.firstinspires.ftc.teamcode.subsystems.enums.GamepadButton;

/**
 * Subsystem to control the feed servos to supply a whiffle ball to the launcher motor
 * @author Mentor Landon Smith
 */
@Config
public class FeedServoLauncher {
    /**
     * The controller to use for user input
     */
    private GamepadController gamepad;
    private int previousBallFeedState = 0;
    /**
     * The duration (in milliseconds) for which the feed servos should run.
     * This can be configured via the FTC Dashboard.
     */
    public static long duration = 500;
    /**
     * Tracks the state of the feed cycle.
     * - true: The feed cycle is currently active and servos are (or should be) running.
     * - false: The system is idle and waiting for a button press.
     */
    public static GamepadButton feedForwardButton = GamepadButton.LEFT_BUMPER;
    public static GamepadButton feedReverseButton = GamepadButton.RIGHT_BUMPER;


    private final CRServo feedServoFrontLeft, feedServoFrontRight;
    // Servos
    // Servo Positions
    public static double SERVO_FORWARD_POW = 1.0;
    public static double SERVO_REVERSE_POW = -1.0;
    public static double SERVO_NEUTRAL_POW = 0;
    public FeedServoLauncher(OpMode opMode){

        feedServoFrontLeft = opMode.hardwareMap.get(CRServo.class, "servo1");
        feedServoFrontRight = opMode.hardwareMap.get(CRServo.class, "servo2");
        feedServoFrontRight.setDirection(CRServo.Direction.REVERSE);
    }
    /**
     * Creates a FeedServoLauncher object and initializes the feed servos
     *
     * @param opMode     the TeleOp opMode
     * @param controller the controller to use for user input
     */
    public FeedServoLauncher(OpMode opMode, GamepadController controller) {
        gamepad = controller;
        //TODO: wire servos according to their initialization below
        feedServoFrontLeft = opMode.hardwareMap.get(CRServo.class, "servo1");
        feedServoFrontRight = opMode.hardwareMap.get(CRServo.class, "servo2");
        feedServoFrontRight.setDirection(CRServo.Direction.REVERSE);
        gamepad.configureTristateButton(feedForwardButton, feedReverseButton);

    }

    public void stop() {
        feedServoFrontLeft.setPower(SERVO_NEUTRAL_POW);
        feedServoFrontRight.setPower(SERVO_NEUTRAL_POW);
    }

    /**
     * Updates the state of the feed servos based on user input and the feed cycle timer.
     * This method should be called once per loop in an OpMode
     */
    public void updateFeedServoLauncherBehavior() {
        //TODO: Implement touchSensor to stop servos once intake is complete
        int feedState = gamepad.getTristateButtonValue(feedForwardButton);
        if(feedState == 1) {
            intakeBall();
        }
        else if(feedState == -1){
            rejectBall();
        }
        else{
            stopIntake();
        }
    }

    /**
     * Gets feedServoPositions for left servos, both act as the same so only one needs to be read
     * @return the position of the left servos
     */
    public double getLeftServoPower(){
        return feedServoFrontLeft.getPower();
    }

    /**
     * Gets feedServoPositions for right servos, both act as the same so only one needs to be read
     * @return the position of the right servos
     */
    public double getRightServoPower(){
        return feedServoFrontRight.getPower();
    }

    /**
     * Autonomous method to feed a ball to the launcher, intentionally hangs the entire autonomous
     * to prevent any other actions from happening
     */
    public Action intakeBallAction() {
        return new InstantAction(this::intakeBall);
    }
    private void intakeBall() {
        //TODO: Fix Servo directions
        feedServoFrontRight.setPower(SERVO_FORWARD_POW);
        feedServoFrontLeft.setPower(SERVO_FORWARD_POW);
    }
    public Action rejectBallAction(){
        return new InstantAction(this::rejectBall);
    }
    private void rejectBall(){
        //TODO: Fix Servo directions
        feedServoFrontRight.setPower(SERVO_REVERSE_POW);
        feedServoFrontLeft.setPower(SERVO_REVERSE_POW);
    }
    public Action stopIntakeAction(){
        return new InstantAction(this::stopIntake);
    }
    private void stopIntake(){
        //TODO: Fix Servo directions
        feedServoFrontLeft.setPower(SERVO_NEUTRAL_POW);
        feedServoFrontRight.setPower(SERVO_NEUTRAL_POW);
    }


}
