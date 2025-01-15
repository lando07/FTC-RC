package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.SplineHeadingPath;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true");
        MeepMeep meepMeep = new MeepMeep(700);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .build();

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(-17.2, 62, Math.toRadians(270)))
                .splineToConstantHeading(new Vector2d(0, 33), Math.toRadians(270))
                .strafeTo(new Vector2d(0, 36))
                .strafeTo(new Vector2d(-35, 34))
                .strafeTo(new Vector2d(-35, 13))
                .strafeTo(new Vector2d(-48, 13))
                //Push second into observation zone
                .strafeTo(new Vector2d(-48, 52))

                .turnTo(Math.toRadians(90))
                .strafeToLinearHeading(new Vector2d(3, 36), Math.toRadians(270))
                .strafeTo(new Vector2d(3, 33))
                .strafeTo(new Vector2d(3, 36))
                .strafeToLinearHeading(new Vector2d(-48, 52), Math.toRadians(90))
                .strafeToLinearHeading(new Vector2d(-3, 36), Math.toRadians(270))
                .strafeTo(new Vector2d(-3, 33))
                .strafeTo(new Vector2d(-3, 36))
                .strafeTo(new Vector2d(-48, 54))
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_INTO_THE_DEEP_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}