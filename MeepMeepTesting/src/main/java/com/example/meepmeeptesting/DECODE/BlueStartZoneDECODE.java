package com.example.meepmeeptesting.DECODE;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class BlueStartZoneDECODE {
    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true");
        MeepMeep meepMeep = new MeepMeep(700);
        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                .setConstraints(90, 70, 55, 60, 14)
                .build();
        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(61.8, -12.5, Math.toRadians(180)))
                //Put actions here
                .strafeToConstantHeading(new Vector2d(36.3,-12.7))
                .turn(Math.toRadians(90))

                .strafeToConstantHeading(new Vector2d(35.0,-56.0))
                .strafeToConstantHeading(new Vector2d(36.3,-12.7))
                .turn(Math.toRadians(-90))
                .strafeToConstantHeading(new Vector2d(-30.8,-15.1))
                .turn(Math.toRadians(54))
                .strafeToConstantHeading(new Vector2d(-53.4,-46.1))

                .strafeToConstantHeading(new Vector2d(-30.8,-15.1))
                .turn(Math.toRadians(127))
                .strafeToConstantHeading(new Vector2d(10.5,-13.5))
                .turn(Math.toRadians(-92))

                .strafeToConstantHeading(new Vector2d(11.5,-51.4))
                .strafeToConstantHeading(new Vector2d(10.5,-13.5))
                .turn(Math.toRadians(-92))
                .strafeToConstantHeading(new Vector2d(-30.8,-15.1))
                .turn(Math.toRadians(54))
                .strafeToConstantHeading(new Vector2d(-53.4,-46.1))
                .strafeToConstantHeading(new Vector2d(-30.8,-15.1))
                .turn(Math.toRadians(127))
                .strafeToConstantHeading(new Vector2d(-12.5,-14.1))
                .turn(Math.toRadians(-90))
                .strafeToConstantHeading(new Vector2d(-12.5,-37.5))



                .build());
        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}
