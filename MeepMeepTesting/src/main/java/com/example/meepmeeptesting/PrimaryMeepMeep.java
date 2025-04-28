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
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 13)
                .build();

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(-17.2, 60, Math.toRadians(-90)))
                //Clip first specimen
                .strafeToConstantHeading(new Vector2d(6, 36))
                .strafeToConstantHeading(new Vector2d(6, 32.5))
                .strafeToLinearHeading(new Vector2d(-39, 34), Math.toRadians(0))
                .strafeTo(new Vector2d(-39, 21))
                .strafeToLinearHeading(new Vector2d(-41, 50), Math.toRadians(310))
                .strafeToLinearHeading(new Vector2d(-50.5, 21), Math.toRadians(0))
                .strafeToLinearHeading(new Vector2d(-50.5, 50), Math.toRadians(310))
                .strafeToLinearHeading(new Vector2d(-59, 21), Math.toRadians(0))
                .strafeToLinearHeading(new Vector2d(-50, 50), Math.toRadians(310))
                .strafeToLinearHeading(new Vector2d(-50, 55), Math.toRadians(90))
                .strafeToLinearHeading(new Vector2d(3,35.2+2), Math.toRadians(270))
                .strafeToLinearHeading(new Vector2d(3,35.2), Math.toRadians(270))
                        .setTangent(Math.toRadians(180))
                .splineToLinearHeading(new Pose2d(new Vector2d(-45, 58), Math.toRadians(90)), Math.toRadians(90))
                .strafeToLinearHeading(new Vector2d(-3,35.2+2), Math.toRadians(270))
                .splineToLinearHeading(new Pose2d(new Vector2d(-45, 58), Math.toRadians(90)), Math.toRadians(90))
                .strafeToLinearHeading(new Vector2d(-3,36), Math.toRadians(270))


                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_INTO_THE_DEEP_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}