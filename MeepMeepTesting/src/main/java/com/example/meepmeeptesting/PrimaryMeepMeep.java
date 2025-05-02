package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class PrimaryMeepMeep {
    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true");
        MeepMeep meepMeep = new MeepMeep(700);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(80, 60, 55, 60, 13)
                .build();

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(-17.2, 63, Math.toRadians(-90)))
                //Clip first specimen
                .strafeToConstantHeading(new Vector2d(6, 34 + 4))
                .strafeToConstantHeading(new Vector2d(6, 34))
                .strafeToLinearHeading(new Vector2d(-39, 34), Math.toRadians(0))
                .strafeTo(new Vector2d(-39, 23))
                .strafeToLinearHeading(new Vector2d(-41, 45), Math.toRadians(310))
                .strafeToLinearHeading(new Vector2d(-49.5, 23), Math.toRadians(0))
                .strafeToLinearHeading(new Vector2d(-49.5, 45), Math.toRadians(310))
                .strafeToLinearHeading(new Vector2d(-59, 23), Math.toRadians(0))
                .setTangent(Math.toRadians(90))
                .splineToLinearHeading(new Pose2d(new Vector2d(-50, 45), Math.toRadians(310)), Math.toRadians(90))
                .turnTo(Math.toRadians(90))
                .strafeTo(new Vector2d(-50, 57))
                .strafeToLinearHeading(new Vector2d(3, 34 + 4), Math.toRadians(273))
                .strafeTo(new Vector2d(3, 34))
                .strafeToLinearHeading(new Vector2d(-45, 57), Math.toRadians(90))
                .strafeToLinearHeading(new Vector2d(-3, 34 + 4), Math.toRadians(273))
                .strafeTo(new Vector2d(-3, 34))
                .strafeToLinearHeading(new Vector2d(-45, 57), Math.toRadians(90))
                .strafeToLinearHeading(new Vector2d(-6, 34 + 4), Math.toRadians(273))
                .strafeTo(new Vector2d(-6, 34))
                .strafeToLinearHeading(new Vector2d(-6,34 + 4), Math.toRadians(270))
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_INTO_THE_DEEP_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}