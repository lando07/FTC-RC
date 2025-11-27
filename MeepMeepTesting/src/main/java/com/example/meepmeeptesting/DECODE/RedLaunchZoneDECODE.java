package com.example.meepmeeptesting.DECODE;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class RedLaunchZoneDECODE {
    public static Pose2d startingPose = new Pose2d(-53.1,46.1,Math.toRadians(-52));

    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true");
        MeepMeep meepMeep = new MeepMeep(700);
        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(90, 70, 55, 60, 14)
                .build();
        myBot.runAction(myBot.getDrive().actionBuilder(startingPose)
               //Current Path

                .strafeToConstantHeading(new Vector2d(-32.2,23.2))
                .turn(Math.toRadians(-127))
                .strafeToConstantHeading(new Vector2d(-12.3,23.0))
                .turn(Math.toRadians(92))

                .strafeToConstantHeading(new Vector2d(-11.9,51.4))
                .strafeToConstantHeading(new Vector2d(-12.3,23.0))
                .turn(Math.toRadians(91))
                .strafeToConstantHeading(new Vector2d(-32.2,23.2))
                .turn(Math.toRadians(-56))
                .strafeToConstantHeading(new Vector2d(-53.1,46.1))

                .strafeToConstantHeading(new Vector2d(-32.2,23.2))
                .turn(Math.toRadians(-127))
                .strafeToConstantHeading(new Vector2d(11.3,23.4))
                .turn(Math.toRadians(92))

                .strafeToConstantHeading(new Vector2d(11.9,50.0))
                .strafeToConstantHeading(new Vector2d(11.3,23.4))
                .turn(Math.toRadians(92))
                .strafeToConstantHeading(new Vector2d(-32.2,23.2))
                .turn(Math.toRadians(-56))
                .strafeToConstantHeading(new Vector2d(-53.1,46.1))
                .strafeToConstantHeading(new Vector2d(-32.2,23.2))
                .turn(Math.toRadians(-127))
                .strafeToConstantHeading(new Vector2d(35.3,23.8))
                .turn(Math.toRadians(92))












































































                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();

    }
}
