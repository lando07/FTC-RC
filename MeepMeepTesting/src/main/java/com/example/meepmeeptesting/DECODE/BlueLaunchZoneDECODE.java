package com.example.meepmeeptesting.DECODE;

import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class BlueLaunchZoneDECODE {

    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true");
        MeepMeep meepMeep = new MeepMeep(700);
        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                .setConstraints(90, 70, 55, 60, 14)
                .build();
        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(-49.76891705370325,-55.758796751968504, Math.toRadians( -129.57617444247222)))
                //Put actions here
                //This strafe the robot to a coordinate with a set heading

                .strafeToConstantHeading(new Vector2d(-28.2,-29.0))
//                .stopAndAdd(launchBallsForSetTime())//launches
                .strafeToLinearHeading(new Vector2d(-9.5,-27.3), Math.toRadians(270))//moves to first set of balls
                // Start both motors
//                .stopAndAdd(new InstantAction(() -> {
//                    intakeMotor.setPower(-1);
//                }))
//                .waitSeconds(0.4)

                .strafeToConstantHeading(new Vector2d(-9.5,-61))
//                .stopAndAdd(new InstantAction(() -> intakeMotor.setPower(0)))
//                .strafeToLinearHeading(new Vector2d(-28.2,-29.0), Math.toRadians(-128.57617444247222))
//                .stopAndAdd(launchBallsForSetTime())
//                .strafeToLinearHeading(new Vector2d(15,-27.3), Math.toRadians(270))
//                .stopAndAdd(new InstantAction(() -> {
//                    intakeMotor.setPower(-1);
//                }))
//                .waitSeconds(0.4)
//                .strafeToConstantHeading(new Vector2d(15, -65.3))
//                .stopAndAdd(new InstantAction(() -> intakeMotor.setPower(0)))
//                .strafeToConstantHeading(new Vector2d(15, -33.5))
                .strafeToLinearHeading(new Vector2d(-28.2,-29.03 ), Math.toRadians(-128.57617444247222))
//                .stopAndAdd(launchBallsForSetTime())
                .strafeToLinearHeading(new Vector2d(-47.3,-28.3), Math.toRadians(-360))




                .build());
        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();


    }
}
