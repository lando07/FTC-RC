package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.SplineHeadingPath;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) throws InterruptedException {
        System.setProperty("sun.java2d.opengl", "true");
        MeepMeep meepMeep = new MeepMeep(700);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 13)
                .build();

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(-17.2, 62, Math.toRadians(270)))
                .strafeTo(new Vector2d(0, 32))
                .waitSeconds(1)
                .strafeTo(new Vector2d(0, 40))
                .strafeTo(new Vector2d(-52, 40))
                .strafeTo(new Vector2d(-52, 33))
                .strafeToLinearHeading(new Vector2d(-52, 40), Math.toRadians(90))
                .strafeTo(new Vector2d(-52, 52))
                .strafeTo(new Vector2d(-52, 65))
                .waitSeconds(.5)
                .strafeTo(new Vector2d(-52, 60))
                .strafeToLinearHeading(new Vector2d(0, 32), Math.toRadians(270))

                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_INTO_THE_DEEP_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}