package org.firstinspires.ftc.teamcode.subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;

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
     * The timestamp (in milliseconds) when the feed cycle started.
     */
    private volatile long feedCycleStartTime;
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
    private boolean isFeedCycleActive = false;

    private final CRServo feedServoLeft;
    private final CRServo feedServoRight;
    /**
     * How the feed button should behave, not recommended to change due to how the button is implemented
     */
    public static BiStateButtonBehavior feedButtonBehavior = BiStateButtonBehavior.HOLD;
    /**
     * The button to read from to control the feed servos
     */
    public static GamepadButton feedButton = GamepadButton.LEFT_BUMPER;

    /**
     * Creates a feedServoLauncher object and initializes the feed servos
     *
     * @param opMode     the TeleOp opMode
     * @param controller the controller to use for user input
     */
    public feedServoLauncher(OpMode opMode, GamepadController controller) {
        gamepad = controller;
        feedServoLeft = opMode.hardwareMap.get(CRServo.class, "feedServoLeft");
        feedServoRight = opMode.hardwareMap.get(CRServo.class, "feedServoRight");
        feedServoLeft.setDirection(CRServo.Direction.FORWARD);
        feedServoRight.setDirection(CRServo.Direction.REVERSE);
        gamepad.configureBiStateButton(feedButton, feedButtonBehavior);

    }

    public void stop() {
        feedServoLeft.setPower(0);
        feedServoRight.setPower(0);
    }

    /**
     * Updates the state of the feed servos based on user input and the feed cycle timer.
     * This method should be called once per loop in an OpMode
     */
    public void updateFeedServoLauncherBehavior() {
        // State 1: The feed cycle is currently active.
        if (isFeedCycleActive) {
            long elapsedTime = System.currentTimeMillis() - feedCycleStartTime;
            // Check if the timer has expired.
            if (elapsedTime >= duration) {
                // Stop the servos and reset the state to idle.
                feedServoLeft.setPower(0);
                feedServoRight.setPower(0);
                isFeedCycleActive = false;
            }
            // If the timer has not expired, do nothing and let the servos continue running.
        }
        // State 2: The system is idle and waiting for input.
        else {
            // Check for a new button press to start the cycle.
            if (gamepad.getGamepadButtonValue(feedButton)) {
                // Start the feed cycle.
                isFeedCycleActive = true;
                feedCycleStartTime = System.currentTimeMillis();
                feedServoLeft.setPower(1);
                feedServoRight.setPower(1);
            }
        }
    }

    /**
     * Autonomous method to feed a ball to the launcher, intentionally hangs the entire autonomous
     * to prevent any other actions from happening
     */
    public void feedBall(@NonNull LinearOpMode autonomousOpMode) {
        feedServoLeft.setPower(1);
        feedServoRight.setPower(1);
        long startTime = System.currentTimeMillis();
        while (autonomousOpMode.opModeIsActive() && (System.currentTimeMillis() - startTime < duration)) {
            // This empty loop hangs the thread for the specified duration
            // while remaining responsive to the OpMode stopping.
        }
        feedServoLeft.setPower(0);
        feedServoRight.setPower(0);
    }


}
