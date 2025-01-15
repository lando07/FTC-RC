package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.subsystems.*;

@Config
@Autonomous(name = "PrimaryAuto", group = "autonomous")
public class PrimaryAutonomous extends LinearOpMode{
    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d startingPose = new Pose2d(-17.2,62,Math.toRadians(279));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startingPose);


        while(!opModeIsActive() && !isStopRequested()){

        }
        waitForStart();
        if(opModeIsActive()){
            Actions.runBlocking(
                    drive.actionBuilder(startingPose)
                            .splineToConstantHeading(new Vector2d(0, 32), Math.toRadians(270))
                            .strafeTo(new Vector2d(0, 41))
                            .strafeTo(new Vector2d(-48, 41))
                            .strafeTo(new Vector2d(-48, 36))
                            .strafeToSplineHeading(new Vector2d(-48, 52), Math.toRadians(90))
                            .strafeToSplineHeading(new Vector2d(-58, 45), Math.toRadians(270))
                            .strafeTo(new Vector2d(-58, 36))
                            .strafeToSplineHeading(new Vector2d(-58, 52), Math.toRadians(90))
                            .strafeToSplineHeading(new Vector2d(-3,41), Math.toRadians(270))
                            .strafeTo(new Vector2d(-3,32))
                            .strafeToSplineHeading(new Vector2d(-48, 52), Math.toRadians(90))
                            .strafeToSplineHeading(new Vector2d(-3,41), Math.toRadians(270))
                            .strafeTo(new Vector2d(-3,32))
                            .strafeTo(new Vector2d(-55,56))
//                .strafeToSplineHeading(new Vector2d(-48, 20), Math.toRadians(0))
//                .strafeTo(new Vector2d(-58, 20))
//                .strafeTo(new Vector2d(-58, 25))
//                .strafeToSplineHeading(new Vector2d(-54,52),Math.toRadians(270))
//                .strafeTo(new Vector2d(-54,48))
//                .strafeToSplineHeading(new Vector2d(-48,52),Math.toRadians(90))
                            .build());
        }
    }
}
