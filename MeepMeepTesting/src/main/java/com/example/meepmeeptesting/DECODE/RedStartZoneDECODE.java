package com.example.meepmeeptesting.DECODE;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class RedStartZoneDECODE {

    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true");
        MeepMeep meepMeep = new MeepMeep(700);
        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                .setConstraints(90, 70, 55, 60, 14)
                .build();
        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(57.8,12.7, Math.toRadians(180)))
                //Put actions here
                // Current Path
                .strafeToConstantHeading(new Vector2d(36.3,12.7))
                .turn(Math.toRadians(-90))

                .strafeToConstantHeading(new Vector2d(34.8,56.2))
                .strafeToConstantHeading(new Vector2d(35.9,24.4))

                .strafeToLinearHeading(new Vector2d(-32.2,23.2), Math.toRadians(90))

                .turn(Math.toRadians(40))
                .strafeToConstantHeading(new Vector2d(-51.2,48.9))

                .strafeToLinearHeading(new Vector2d(-32.2,23.2), Math.toRadians(90))


                .strafeToConstantHeading(new Vector2d(12.7,24.2))

                .strafeToConstantHeading(new Vector2d(11.7,54.6))
                .strafeToConstantHeading(new Vector2d(12.7,24.2))
                .strafeToLinearHeading(new Vector2d(-32.2,23.2), Math.toRadians(90))


                .turn(Math.toRadians(40))
                .strafeToConstantHeading(new Vector2d(-51.2,48.9))

                .strafeToLinearHeading(new Vector2d(-32.2,23.2), Math.toRadians(90))

                .strafeToConstantHeading(new Vector2d(6.9,24.0))


                .build());
        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}
