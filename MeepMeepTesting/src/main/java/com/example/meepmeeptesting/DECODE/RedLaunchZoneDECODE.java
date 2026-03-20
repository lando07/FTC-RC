package com.example.meepmeeptesting.DECODE;


import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;


public class RedLaunchZoneDECODE {
    public static Pose2d startingPose = new Pose2d( -49.76891705370325, 55.758796751968504,Math.toRadians(122.27541255940227));
//TODO : fix rotation

    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true");
        MeepMeep meepMeep = new MeepMeep(700);
        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(90, 70, 55, 60, 14)
                .build();
        myBot.runAction(myBot.getDrive().actionBuilder(startingPose)
               //Current Path
                // Launch Sequence

//                 // Current Path

                .strafeToConstantHeading(new Vector2d(-32.4,33.6))

                .strafeToLinearHeading(new Vector2d(-12.1,27.3), Math.toRadians(90))//moves to first set of balls

                .strafeToConstantHeading(new Vector2d(-12.1,58))

                .strafeToLinearHeading(new Vector2d(-32.6,33.6), Math.toRadians(129.3629632573411))

                .strafeToLinearHeading(new Vector2d(9.5,27.3), Math.toRadians(90))

                .strafeToConstantHeading(new Vector2d(9.5, 63))

                .strafeToConstantHeading(new Vector2d(9.5, 33.5))
                .strafeToLinearHeading(new Vector2d(-32.6,33.6), Math.toRadians(129.3629632573411))
                .strafeToLinearHeading(new Vector2d(-47.3,28.3), Math.toRadians(360))

















//                .strafeToConstantHeading(new Vector2d(-39.964469849593996,38.952098455954726))
//
//                .strafeToLinearHeading(new Vector2d(-12.81065260519193,22.70081767885704), Math.toRadians(90))//moves to first set of balls
//
//                .strafeToConstantHeading(new Vector2d(-12.81065260519193,55.66481702909695))
//                .waitSeconds(.5)
//
//                .strafeToLinearHeading(new Vector2d(-39.964469849593996,38.952098455954726), Math.toRadians(138.09018282781153))
//
//                .strafeToLinearHeading(new Vector2d(8.555011178564838,22.70081767885704), Math.toRadians(90))//moves to second set of balls
//                .waitSeconds(.5)
//
//                .strafeToConstantHeading(new Vector2d(8.555011178564838,60.74348605899361))
//                .strafeToConstantHeading(new Vector2d(8.555011178564838,22.70081767885704))
//
//                .strafeToLinearHeading(new Vector2d(-39.964469849593996,38.952098455954726), Math.toRadians(138.09018282781153))
//
//                .strafeToLinearHeading(new Vector2d(-51.82927169199065,18.91646858275406), Math.toRadians(180))





                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();

    }
}
