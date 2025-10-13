package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Config
@Autonomous(name = "PrimaryAutonomousDECODE", group="autonomous")
public class PrimaryAutoDECODE extends LinearOpMode {
    @Override
    public void runOpMode(){
        Pose2d startingPose = new Pose2d(-50,-50,Math.toRadians(45));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startingPose);
        //Insert subsystem initialization here
        MecanumDrive.DriveLocalizer dl = (MecanumDrive.DriveLocalizer) drive.localizer;

        Action autonomous = drive.actionBuilder(startingPose)

                .build();

        while(!opModeIsActive() && !isStopRequested()){
            dl.imu.resetYaw();
            sleep(50);
        }
        waitForStart();
        if (opModeIsActive()){
            Actions.runBlocking(autonomous);
        }
    }

}
