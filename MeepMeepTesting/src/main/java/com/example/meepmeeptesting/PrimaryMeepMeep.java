package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.Constraints;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class PrimaryMeepMeep {
    public static double testYValue = 61.5;
    public static double testYValue2 = 33;
    public static double testYValue3 = 61.5;
    public static double testYValue4 = 33;
    public static double thirdSpecimenOffset = 3.5;
    public static double fourthSpecimenOffset = 3.5;
    public static double clipOffset = 3.5;
    public static double testXValue = -45;
    public static int clipDelay = 200;
    public static int extendLength = 515;
    public static double neutralPitch = 0.15;
    public static double neutralYaw = 1;
    public static int grabDelay = 100;
    public static int pickUpDelay = 200;
    public static int dropOffDelay = 200;
    public static double extendDelay = 1;

    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true");
        MeepMeep meepMeep = new MeepMeep(700);
        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(90, 70, 55, 60, 14)
                .build();

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(-17.2, 63, Math.toRadians(-90)))
                .strafeToConstantHeading(new Vector2d(-8, testYValue2))
                .waitSeconds(clipDelay / 1000.0)
                .strafeToLinearHeading(new Vector2d(-39, 36), Math.toRadians(0))
                .strafeTo(new Vector2d(-39, 21))
                .waitSeconds(pickUpDelay / 1000.0)
                .strafeToLinearHeading(new Vector2d(-41, 45), Math.toRadians(310))
                .strafeToLinearHeading(new Vector2d(-49.5, 21), Math.toRadians(0))
                .waitSeconds(pickUpDelay / 1000.0)
                .strafeToLinearHeading(new Vector2d(-49.5, 45), Math.toRadians(310))
                .strafeToLinearHeading(new Vector2d(-59, 21), Math.toRadians(0))
                .waitSeconds(pickUpDelay / 1000.0)
                .setTangent(Math.toRadians(90))
                .splineToLinearHeading(new Pose2d(new Vector2d(-50, testYValue3 - 4), Math.toRadians(310)), Math.toRadians(90))
                .waitSeconds(0.1)
                .turnTo(Math.toRadians(90))
                .strafeTo(new Vector2d(-50, testYValue3))
                .waitSeconds(grabDelay / 1000.0)
                .strafeToSplineHeading(new Vector2d(0, testYValue4), Math.toRadians(273))
                .waitSeconds(clipDelay / 1000.0)
                .strafeToLinearHeading(new Vector2d(-43, testYValue), Math.toRadians(90))
                .waitSeconds(grabDelay / 1000.0)
                .strafeToLinearHeading(new Vector2d(-2, testYValue4), Math.toRadians(273))
                .waitSeconds(clipDelay / 1000.0)
                .strafeToLinearHeading(new Vector2d(-43, testYValue), Math.toRadians(90))
                .waitSeconds(grabDelay / 1000.0)
                .strafeToLinearHeading(new Vector2d(-4, testYValue4), Math.toRadians(273))
                .waitSeconds(clipDelay / 1000.0)
                .strafeToLinearHeading(new Vector2d(-43, testYValue), Math.toRadians(90))
                .waitSeconds(grabDelay / 1000.0)
                .strafeToLinearHeading(new Vector2d(-6, testYValue4), Math.toRadians(273))
                .waitSeconds(clipDelay / 1000.0)
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_INTO_THE_DEEP_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}