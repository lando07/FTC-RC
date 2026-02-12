package com.example.meepmeeptesting.DECODE;


import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;


public class RedLaunchZoneDECODE {
    public static Pose2d startingPose = new Pose2d( -55.37878321850394, 50.28092031403789,Math.toRadians(129.62027014375383));
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

//                .strafeToLinearHeading(new Vector2d(-52.8,47.7), Math.toRadians(129.62027014375383))
//                .strafeToLinearHeading(new Vector2d(-32.2,23.2), Math.toRadians(90))
//                .strafeToConstantHeading(new Vector2d(-15,23.0))
//                .strafeToConstantHeading(new Vector2d(-15,56))

                //.strafeToLinearHeading(new Vector2d(-32.2,23.2), Math.toRadians(-232))
              //  .strafeToLinearHeading(new Vector2d(-50,44), Math.toRadians(129.62027014375383))
               // .strafeToConstantHeading(new Vector2d(-32.2,23.2))
                .strafeToConstantHeading(new Vector2d(-32.2,23.2))
                  .strafeToLinearHeading(new Vector2d(12.7,23.8), Math.toRadians(90))
                .strafeToConstantHeading(new Vector2d(12.7,59))

                .strafeToLinearHeading(new Vector2d(11.5,23.8), Math.toRadians(90))
                .strafeToLinearHeading(new Vector2d(-32.2,23.2), Math.toRadians(-232))
                .strafeToLinearHeading(new Vector2d(-50,44), Math.toRadians(129.62027014375383))

                .strafeToConstantHeading(new Vector2d(-64.8,30.0))













































































                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();

    }
}
