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
        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(61.31118413970226,20.0335849551704, Math.toRadians(180)))
                //Put actions here
                // Current Path
                               // .strafeToConstantHeading(new Vector2d(10.1,29))

                .strafeToConstantHeading(new Vector2d(-32.4,23.2))
                        .turn(Math.toRadians(-49))
                        .strafeToConstantHeading(new Vector2d(-41.9,39.1))
                        .strafeToLinearHeading(new Vector2d(-15,23), Math.toRadians(90))
                .strafeToLinearHeading(new Vector2d(-15,23), Math.toRadians(90))
                .strafeToConstantHeading(new Vector2d(-15,50))
                .strafeToLinearHeading(new Vector2d(-32.4,24.2), Math.toRadians(130))
                .strafeToConstantHeading(new Vector2d(-48.7,43.1))
                .strafeToLinearHeading(new Vector2d(12.1,23.6), Math.toRadians(90))


                .strafeToLinearHeading(new Vector2d(12.1,55.2), Math.toRadians(90))
                .strafeToLinearHeading(new Vector2d(12.1,23.6), Math.toRadians(90))
//                .strafeToLinearHeading(new Vector2d(-32.4,24.2), Math.toRadians(130))
//                .strafeToConstantHeading(new Vector2d(-48.7,43.1))
//                .strafeToLinearHeading(new Vector2d(35.5,32.6), Math.toRadians(90))

                .build());
        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}
