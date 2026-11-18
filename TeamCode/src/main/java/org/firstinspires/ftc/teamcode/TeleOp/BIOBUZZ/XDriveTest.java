package org.firstinspires.ftc.teamcode.TeleOp.BIOBUZZ;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@Config
@TeleOp(name="XDriveTest", group="TeleOp")
public class XDriveTest extends LinearOpMode {
    private DcMotor leftFront;
    private DcMotor rightFront;
    private DcMotor leftBack;
    private DcMotor rightBack;
    private DcMotor collector;

    public void runOpMode() {
        leftFront = hardwareMap.get(DcMotor.class, "left_front");
        rightFront = hardwareMap.get(DcMotor.class, "right_front");
        leftBack = hardwareMap.get(DcMotor.class, "left_back");
        rightBack = hardwareMap.get(DcMotor.class, "right_back");
        collector = hardwareMap.get(DcMotor.class, "collector");

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        double x, y, yaw;

        waitForStart();
        while (opModeIsActive()) {
            x = gamepad1.left_stick_x;
            y = -gamepad1.left_stick_y;
            yaw = gamepad1.right_stick_x;

            //leftFront.setPower(y + x + yaw);
            //leftBack.setPower(y - x + yaw);
            //rightFront.setPower(y - x - yaw);
            //rightBack.setPower(y + x - yaw);
            collector.setPower(y);
            telemetry.addData("Left Stick x", gamepad1.left_stick_x);
            telemetry.addData("Left Stick y", gamepad1.left_stick_y);
            telemetry.addData("Right Stick x", gamepad1.right_stick_x);
            telemetry.addData("Right Stick y", gamepad1.right_stick_y);
            telemetry.addData("Status", "Running");
            telemetry.update();
        }
    }
}
