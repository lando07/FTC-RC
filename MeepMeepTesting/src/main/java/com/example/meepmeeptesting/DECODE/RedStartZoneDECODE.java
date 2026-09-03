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
        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(59.5701202632874,26.73845186008243, Math.toRadians(180)))
                //Put actions here
                // Current Path
                .strafeToConstantHeading(new Vector2d(59.2,48))
                .strafeToConstantHeading(new Vector2d(-31.0,26.6))
                .turn(Math.toRadians(-51))
                .strafeToConstantHeading(new Vector2d(-50,50))
                .strafeToLinearHeading(new Vector2d(-11.6,23.6), Math.toRadians(90))
                .waitSeconds(.2)
                .strafeToConstantHeading(new Vector2d(-11.6,49))
                .strafeToLinearHeading(new Vector2d(-54,48.5), Math.toRadians(129.62027014375383))



                .strafeToConstantHeading(new Vector2d(11.9,23.8))
                .strafeToConstantHeading(new Vector2d(11.9,60))
                .waitSeconds(.2)
                .strafeToConstantHeading(new Vector2d(11.9,33.2))
                .strafeToLinearHeading(new Vector2d(-54,48.5), Math.toRadians(129.62027014375383))
                .strafeToLinearHeading(new Vector2d(-32.4,24.2), Math.toRadians(130))
                .strafeToConstantHeading(new Vector2d(-50,50))

                .strafeToLinearHeading(new Vector2d(-47.8,30.0), Math.toRadians(90))
                .strafeToConstantHeading(new Vector2d(-64.0,30.0))




                .build());
        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}
