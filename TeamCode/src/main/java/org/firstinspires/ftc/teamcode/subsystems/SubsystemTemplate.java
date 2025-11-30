package org.firstinspires.ftc.teamcode.subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.subsystems.enums.BiStateButtonBehavior;
import org.firstinspires.ftc.teamcode.subsystems.enums.GamepadButton;

/**
 * This class is a template on how to create a subsystem,
 * the possibilities are endless on what you can do. The subsystem
 * architecture aims to allow for code reusability and readability,
 * while staying very simple
 * @author Mentor Landon Smith
 */
@Config
public class SubsystemTemplate {
    /*
     * Use public static variables and objects
     * to store constants that should be
     * tuned on the fly using FTCDashboard.
     */
    public static double modifiableConstant = 0.5;
    /*
     * States that must be accessed across multiple methods are typically private,
     * for example, this boolean can store the state of a servo and which position it is,
     * if it doesn't need absolute positioning control, but needs to jump between 2 positions.
     */
    private boolean state;
    /*
     * Use the GamepadButton Enum to store keybinds associated
     * with said subsystem - "A" could be used to open a claw.
     */
    public static GamepadButton keybind1 = GamepadButton.A;
    /*
     * Use the BiStateButtonBehavior to assign how a button will act.
     * There are 2 modes, TOGGLE, and HOLD. Toggle will invert states when the button is pressed,
     * while HOLD will only return 1 when the button is pressed.
     */
    public static BiStateButtonBehavior keybind1Behavior = BiStateButtonBehavior.TOGGLE;
    /*
     * You can also create Tri-States with 2 buttons.
     * A tri-state works as follows:
     * 1 : buttonPositive is pressed
     * 0 : no buttons are pressed
     * -1 : buttonPegative is pressed
     * Great for d-pads and bumpers.
     */
    public static GamepadButton buttonPositive = GamepadButton.D_PAD_UP;
    public static GamepadButton buttonNegative = GamepadButton.D_PAD_DOWN;
    /*
     * An example of a servo object. Should be private, and if always initialized,
     * will have the tag 'final' after 'private'.
     */
    private Servo servo1;
    /*
     * This will be present in every subsystem created,
     * this holds the reference, not a copy, of the gamepad controller
     * used to get input from the the users. This can be either gamepad,
     * but that information is not needed for any functionality. The gamepad
     * that is assigned is given in the constructor parameter by the teleop.
     */
    private GamepadController controller;
    /*
     * This is a constructor for TeleOp Use, it will get a reference to the opmode,
     * the assigned controller, and if needed, a string with the hardware name.
     * The string is optional, and should be defined in the constructor if there
     * is more than one motor/servo being initialized
     */
    public SubsystemTemplate(@NonNull OpMode opmode, @NonNull GamepadController controller, String hwName) {
        // This initializes the servo with it's name in the driver hub configuration
        servo1 = opmode.hardwareMap.servo.get(hwName);
        // This stores the reference to the assigned gamepad from the TeleOp
        this.controller = controller;
        // These 2 method calls assign buttons to the controller we were given.
        // By using gamepadController, only this class can use these buttons to prevent
        //undefined behavior.
        controller.configureBiStateButton(keybind1, keybind1Behavior);
        controller.configureTristateButton(buttonPositive, buttonNegative);
        //This initializes the initial state of whatever it is being used for
        //It does not have to be a boolean
        state = false;
    }
    /*
     * This is an overloaded constructor, primarily used for autonomous opmodes.
     * Since the autonomous does all of the required work, we do not/cannot use
     * any controller.
     */
    public SubsystemTemplate(@NonNull OpMode opmode,String hwName){
        //Same initialization as above
        servo1 = opmode.hardwareMap.servo.get(hwName);
        //This initializes the initial state of whatever it is being used for
        //It does not have to be a boolean
        state = false;
        //Additional code can go here that's exclusive to autonomous coding
    }

    //Below is where you put any methods, sometimes I call features, where all of your
    //reusable code goes.
    /*
     * This is a simple toggler that toggles the state
     */
    public void toggleState() {
        if (state) {
            state = false;
        } else {
            state = true;
        }
    }
    //See other subsystems to see what other features and methods are used in a subsystem



}
