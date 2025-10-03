package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.subsystems.BiStateButtonBehavior;
import org.firstinspires.ftc.teamcode.subsystems.DriveTrain;
import org.firstinspires.ftc.teamcode.subsystems.GamepadButton;
import org.firstinspires.ftc.teamcode.subsystems.GamepadController;

@Config
@TeleOp(name = "XDriveDECODE", group="Robot")
public class XDriveDECODE extends OpMode {

    private DriveTrain driveTrain;
    private GamepadController controller1, controller2;
    public static GamepadButton launcherButton = GamepadButton.B;
    private final BiStateButtonBehavior launcherButtonBehavior = BiStateButtonBehavior.TOGGLE;
    private DcMotorEx launcher;

    @Override
    public void init() {
        //Initialize subsystems and controllers here
        controller1 = new GamepadController(gamepad1);
        controller1.configureBiStateButton(launcherButton,launcherButtonBehavior);
//        controller2 = new GamepadController(gamepad2);
        driveTrain = new DriveTrain(this, controller1);
        launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void init_loop() {

    }
    @Override
    public void start(){
        driveTrain.setBrakingMode(DcMotor.ZeroPowerBehavior.BRAKE);
    }
    @Override
    public void loop() {
        controller1.update();
        launcher.setPower(controller1.getGamepadButtonValue(launcherButton) ? 1 : 0);
        driveTrain.updateDriveTrainBehavior();

    }
    @Override
    public void stop(){

    }
}
