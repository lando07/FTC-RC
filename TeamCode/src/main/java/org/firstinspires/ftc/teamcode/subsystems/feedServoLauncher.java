package org.firstinspires.ftc.teamcode.subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Subsystem to control the feed servos to supply a whiffle ball to the launcher motor
 */
@Config
public class feedServoLauncher {
    /**
     * The controller to use for user input
     */
    private final GamepadController gamepad;
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

    /**
     * Creates a feedServoLauncher object and initializes the feed servos
     *
     * @param opMode     the TeleOp opMode
     * @param controller the controller to use for user input
     */
    public feedServoLauncher(OpMode opMode, GamepadController controller) {
        gamepad = controller;
        feedServoFrontLeft = opMode.hardwareMap.get(Servo.class, "feedServoFrontLeft");
        feedServoFrontRight = opMode.hardwareMap.get(Servo.class, "feedServoFrontRight");
        feedServoBackLeft = opMode.hardwareMap.get(Servo.class, "feedServoBackLeft");
        feedServoBackRight = opMode.hardwareMap.get(Servo.class, "feedServoBackRight");
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
        int feedState = gamepad.getTristateButtonValue(feedForwardButton);
        if(feedState == 1) {
            feedServoFrontRight.setPosition(SERVO_FORWARD_POS);
            feedServoBackRight.setPosition(SERVO_FORWARD_POS);
            feedServoFrontLeft.setPosition(SERVO_REVERSE_POS);
            feedServoBackLeft.setPosition(SERVO_REVERSE_POS);
        }
        else if(feedState == -1){
            feedServoFrontRight.setPosition(SERVO_REVERSE_POS);
            feedServoBackRight.setPosition(SERVO_REVERSE_POS);
            feedServoFrontLeft.setPosition(SERVO_FORWARD_POS);
            feedServoBackLeft.setPosition(SERVO_FORWARD_POS);
        }
        else{
            feedServoFrontLeft.setPosition(SERVO_NEUTRAL_POS);
            feedServoBackLeft.setPosition(SERVO_NEUTRAL_POS);
            feedServoBackRight.setPosition(SERVO_NEUTRAL_POS);
            feedServoFrontRight.setPosition(SERVO_NEUTRAL_POS);
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
    public void feedBall(@NonNull LinearOpMode autonomousOpMode) {

    }


}
