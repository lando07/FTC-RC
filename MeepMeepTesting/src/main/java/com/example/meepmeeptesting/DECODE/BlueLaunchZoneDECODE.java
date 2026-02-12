package com.example.meepmeeptesting.DECODE;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class BlueLaunchZoneDECODE {

    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true");
        MeepMeep meepMeep = new MeepMeep(700);
        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                .setConstraints(90, 70, 55, 60, 14)
                .build();
        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(-55.37878321850394,-50.28092031403789, Math.toRadians(-129.62027014375383)))
                //Put actions here
                //This strafe the robot to a coordinate with a set heading
                .strafeToLinearHeading(new Vector2d(-47.9,-40.3), Math.toRadians(-40))
              //  .strafeToLinearHeading(new Vector2d(-32.2,-23.2), Math.toRadians(-90))
//                .strafeToConstantHeading(new Vector2d(-11.9,-23.0))
//
//                .strafeToConstantHeading(new Vector2d(-11.9,-56))
//                .strafeToLinearHeading(new Vector2d(-32.2,-23.2), Math.toRadians(232))
//                .strafeToLinearHeading(new Vector2d(-50,-44), Math.toRadians(-129.62027014375383))
//
//                .strafeToConstantHeading(new Vector2d(-32.2,-23.2))
//                .strafeToLinearHeading(new Vector2d(11.5,-24.8), Math.toRadians(-90))
//                .strafeToConstantHeading(new Vector2d(11.5,-58.6))
//                .strafeToLinearHeading(new Vector2d(11.5,-24.8), Math.toRadians(-90))
//                .strafeToLinearHeading(new Vector2d(-32.2,-23.2), Math.toRadians(232))
//                .strafeToLinearHeading(new Vector2d(-50,-44), Math.toRadians(-129.62027014375383))
//                .strafeToLinearHeading(new Vector2d(34.8,-25), Math.toRadians(-90))
                .build());
        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();


    }
}
