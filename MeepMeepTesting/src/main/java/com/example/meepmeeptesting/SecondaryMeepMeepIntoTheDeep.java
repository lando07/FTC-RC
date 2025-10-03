package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class SecondaryMeepMeepIntoTheDeep {
    public static void main(String[] args) throws InterruptedException{
        System.setProperty("sun.java2d.opengl", "true");
        MeepMeep meepMeep = new MeepMeep(700);
        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 13)
                .build();

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(17.2, 62, Math.toRadians(270)))
                .strafeToConstantHeading(new Vector2d(18,58))
                .strafeToLinearHeading(new Vector2d(60, 60), Math.toRadians(45))
                .strafeToConstantHeading(new Vector2d(55, 55))
                        .strafeToLinearHeading(new Vector2d(38,32), Math.toRadians(180))
                        .strafeToConstantHeading(new Vector2d(38,12))
                        .strafeToConstantHeading(new Vector2d(23,10))
                .build());


        meepMeep.setBackground(MeepMeep.Background.FIELD_INTO_THE_DEEP_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}
