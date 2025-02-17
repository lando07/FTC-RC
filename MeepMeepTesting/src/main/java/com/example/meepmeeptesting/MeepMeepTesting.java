package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.AccelConstraint;
import com.acmerobotics.roadrunner.Arclength;
import com.acmerobotics.roadrunner.MinMax;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Pose2dDual;
import com.acmerobotics.roadrunner.PosePath;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.SplineHeadingPath;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.VelConstraint;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

import org.jetbrains.annotations.NotNull;

public class MeepMeepTesting {
    public static void main(String[] args) throws InterruptedException {
        System.setProperty("sun.java2d.opengl", "true");
        MeepMeep meepMeep = new MeepMeep(700);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 13)
                .build();

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(-17.2, 62, Math.toRadians(-90)))
                //clip first specimen
                .strafeTo(new Vector2d(-6, 32))
                .waitSeconds(0.25)
                //spin around
                .strafeToLinearHeading(new Vector2d(-36, 36), Math.toRadians(90))
                //get behind field specimens
                .strafeTo(new Vector2d(-36, 10))
                .setTangent(Math.toRadians(180))
                //Push first sample
                .splineToConstantHeading(new Vector2d(-50, 52), Math.toRadians(90))
                .splineToConstantHeading(new Vector2d(-50, 15), Math.toRadians(270))
                //push second sample
                .splineToConstantHeading(new Vector2d(-60, 52), Math.toRadians(90))
                //grab second specimen
                .splineToConstantHeading(new Vector2d(-60, 60), Math.toRadians(270))
                .waitSeconds(0.5)
                .setTangent(Math.toRadians(0))
                .splineToLinearHeading(new Pose2d(-3, 40, Math.toRadians(270)), Math.toRadians(0))
                //clip second specimen
                .setTangent(Math.toRadians(270))
                .splineToConstantHeading(new Vector2d(-3, 32), Math.toRadians(270))
                //grab third specimen
                .setTangent(Math.toRadians(90))
                .splineToLinearHeading(new Pose2d(-40, 60, Math.toRadians(90)), Math.toRadians(90))
                //clip third specimen
                .setTangent(Math.toRadians(270))
                .splineToLinearHeading(new Pose2d(0, 32, Math.toRadians(270)), Math.toRadians(270))
                //grab fourth specimen
                .setTangent(Math.toRadians(90))
                .splineToLinearHeading(new Pose2d(-40, 60, Math.toRadians(90)), Math.toRadians(90))
                //clip fourth specimen
                .setTangent(Math.toRadians(270))
                .splineToLinearHeading(new Pose2d(3, 32, Math.toRadians(270)), Math.toRadians(270))
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_INTO_THE_DEEP_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}