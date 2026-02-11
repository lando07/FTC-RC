package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

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


    private final Servo feedServoFrontLeft, feedServoFrontRight, feedServoBackLeft, feedServoBackRight;
    // Servos
    // Servo Positions
    public static double SERVO_FORWARD_POS = 1.0;
    public static double SERVO_REVERSE_POS = 0.0;
    public static double SERVO_NEUTRAL_POS = 0.5;
    public FeedServoLauncher(OpMode opMode){

        feedServoFrontLeft = opMode.hardwareMap.get(Servo.class, "servo1");
        feedServoFrontRight = opMode.hardwareMap.get(Servo.class, "servo2");
        feedServoBackLeft = opMode.hardwareMap.get(Servo.class, "servo3");
        feedServoBackRight = opMode.hardwareMap.get(Servo.class, "servo4");
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
        feedServoFrontLeft = opMode.hardwareMap.get(Servo.class, "servo1");
        feedServoFrontRight = opMode.hardwareMap.get(Servo.class, "servo2");
        feedServoBackLeft = opMode.hardwareMap.get(Servo.class, "servo3");
        feedServoBackRight = opMode.hardwareMap.get(Servo.class, "servo4");
        gamepad.configureTristateButton(feedForwardButton, feedReverseButton);

    }

    public void stop() {
        feedServoFrontLeft.setPosition(SERVO_NEUTRAL_POS);
        feedServoBackLeft.setPosition(SERVO_NEUTRAL_POS);
        feedServoBackRight.setPosition(SERVO_NEUTRAL_POS);
        feedServoFrontRight.setPosition(SERVO_NEUTRAL_POS);
    }

    /**
     * Updates the state of the feed servos based on user input and the feed cycle timer.
     * This method should be called once per loop in an OpMode
     */
    public void updateFeedServoLauncherBehavior() {
        //TODO: Implement touchSensor to stop servos once intake is complete
        int feedState = gamepad.getTristateButtonValue(feedForwardButton);
        //This extra check is present since the servo motor writes
        //are time-consuming and we only need to write to the servos
        //on a change in user input
        if(feedState != previousBallFeedState) {
            if (feedState == 1) {
                intakeBall();
            }
            else if (feedState == -1) {
                rejectBall();
            }
            else {
                stopIntake();
            }
            previousBallFeedState = feedState;
        }
    }

    /**
     * Gets feedServoPositions for left servos, both act as the same so only one needs to be read
     * @return the position of the left servos
     */
    public double getLeftServoPositions(){
        return feedServoFrontLeft.getPosition();
    }

    /**
     * Gets feedServoPositions for right servos, both act as the same so only one needs to be read
     * @return the position of the right servos
     */
    public double getRightServoPositions(){
        return feedServoFrontRight.getPosition();
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
        feedServoFrontRight.setPosition(SERVO_FORWARD_POS);
        feedServoBackRight.setPosition(SERVO_REVERSE_POS);
        feedServoFrontLeft.setPosition(SERVO_FORWARD_POS);
        feedServoBackLeft.setPosition(SERVO_REVERSE_POS);
    }
    public Action rejectBallAction(){
        return new InstantAction(this::rejectBall);
    }
    private void rejectBall(){
        //TODO: Fix Servo directions
        feedServoFrontRight.setPosition(SERVO_REVERSE_POS);
        feedServoBackRight.setPosition(SERVO_FORWARD_POS);
        feedServoFrontLeft.setPosition(SERVO_REVERSE_POS);
        feedServoBackLeft.setPosition(SERVO_FORWARD_POS);
    }
    public Action stopIntakeAction(){
        return new InstantAction(this::stopIntake);
    }
    private void stopIntake(){
        //TODO: Fix Servo directions
        feedServoFrontLeft.setPosition(SERVO_NEUTRAL_POS);
        feedServoBackLeft.setPosition(SERVO_NEUTRAL_POS);
        feedServoBackRight.setPosition(SERVO_NEUTRAL_POS);
        feedServoFrontRight.setPosition(SERVO_NEUTRAL_POS);
    }


}
