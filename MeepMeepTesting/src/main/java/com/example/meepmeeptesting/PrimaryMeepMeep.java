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

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(-17.2, 62, Math.toRadians(-90)))
                //Clip first specimen
                .strafeToConstantHeading(new Vector2d(0, 34))
                .strafeToLinearHeading(new Vector2d(-36, 30), Math.toRadians(90))
                //get behind field samples
                .strafeToConstantHeading(new Vector2d(-36, 10))
                .setTangent(Math.toRadians(270))
                .splineToConstantHeading(new Vector2d(-50, 10), Math.toRadians(90))
                //Push first sample
                .strafeToLinearHeading(new Vector2d(-50, 54), Math.toRadians(90))
                .setTangent(Math.toRadians(270))
                //Get behind second sample
                .splineToConstantHeading(new Vector2d(-50, 12), Math.toRadians(270))
                .splineToConstantHeading(new Vector2d(-62, 10), Math.toRadians(90))
                //push second sample
                //and second specimen
                .strafeToConstantHeading(new Vector2d(-62, 61))
                .waitSeconds(0.3)
                .strafeToLinearHeading(new Vector2d(-3, 34), Math.toRadians(270 + 1e-9))
                //clip second specimen
                //grab third specimen
                .strafeToLinearHeading(new Vector2d(-40, 61.5), Math.toRadians(90))
                //clip third specimen
                .strafeToLinearHeading(new Vector2d(0, 34), Math.toRadians(270))
                //grab fourth specimen
                .strafeToLinearHeading(new Vector2d(-40, 60), Math.toRadians(90))
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_INTO_THE_DEEP_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}