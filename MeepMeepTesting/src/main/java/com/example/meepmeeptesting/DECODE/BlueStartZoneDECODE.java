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
        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d( 60.10764294721949,  -22.46029831293061, Math.toRadians( 180)))
                //Put actions here
                .strafeToConstantHeading(new Vector2d(59.2,-48))
//                .strafeToConstantHeading(new Vector2d(-31.0,-26.6))
//                .turn(Math.toRadians(51))
//                .strafeToConstantHeading(new Vector2d(-50,-48))
//                .strafeToConstantHeading(new Vector2d(-64.0,-30.0))
//                .strafeToLinearHeading(new Vector2d(-54,48.5), Math.toRadians(129.62027014375383))
////                .strafeToConstantHeading(new Vector2d(-32.4,23.2))
////                .turn(Math.toRadians(-49))
//                .strafeToConstantHeading(new Vector2d(-41.9,39.1))
//                .strafeToLinearHeading(new Vector2d(-15,23), Math.toRadians(90))
//
//                .strafeToConstantHeading(new Vector2d(-15,50))
//                .strafeToLinearHeading(new Vector2d(-32.4,24.2), Math.toRadians(130))
//                .strafeToConstantHeading(new Vector2d(-48.7,43.1))
//                .strafeToLinearHeading(new Vector2d(12.1,23.6), Math.toRadians(90))
//
//
//                .strafeToLinearHeading(new Vector2d(12.1,55.2), Math.toRadians(90))
//                .strafeToLinearHeading(new Vector2d(12.1,23.6), Math.toRadians(90))
//                .strafeToLinearHeading(new Vector2d(-32.4,24.2), Math.toRadians(130))
//                .strafeToConstantHeading(new Vector2d(-48.7,43.1))
//                .strafeToLinearHeading(new Vector2d(35.5,32.6), Math.toRadians(90))









//
//                .strafeToConstantHeading(new Vector2d(-32.4,-23.2))
//                .turn(Math.toRadians(49))
//                .strafeToConstantHeading(new Vector2d(-50,-50))
//                //.stopAndAdd(launchBallsForSetTime())
//                .strafeToLinearHeading(new Vector2d(-13,-22), Math.toRadians(-90))
//                .waitSeconds(.2)
//                //.stopAndAdd(feedServos.rejectBallAction())
//                .strafeToConstantHeading(new Vector2d(-13,-50))
//                .strafeToLinearHeading(new Vector2d(-32.4,-24.2), Math.toRadians(-130))
//                .strafeToConstantHeading(new Vector2d(-50,-50))
//                //.stopAndAdd(launchBallsForSetTime())
//                .strafeToLinearHeading(new Vector2d(14,-23.6), Math.toRadians(-90))
//
//                .waitSeconds(.2)
//               // .stopAndAdd(feedServos.rejectBallAction())
//                .strafeToLinearHeading(new Vector2d(12.1,-55.2), Math.toRadians(-90))
//                .strafeToLinearHeading(new Vector2d(12.1,-23.6), Math.toRadians(-90))
//                .strafeToLinearHeading(new Vector2d(-32.4,-24.2), Math.toRadians(-130))
//                .strafeToConstantHeading(new Vector2d(-50,-50))
//                //.stopAndAdd(launchBallsForSetTime())
//                .strafeToLinearHeading(new Vector2d(-47.8,-30), Math.toRadians(-90))
//
//               .strafeToLinearHeading(new Vector2d(35.5,-32.6), Math.toRadians(-90))

                .build());
        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}
