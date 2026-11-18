package com.example.meepmeeptesting.BIOBUZZ;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * This is an example class on how to create an autonomous
 * simulation using MeepMeep.
 */
public class BlueWestMeepMeepBIOBUZZ {
    /*
     * Every MeepMeep will use a main method to run its code,
     * as it is fully self-contained.
     */
    public static void main(String[] args) throws IOException {
        //This enables hardware acceleration so the simulation
        //runs without slow-downs
        //This can be the cause of a laggy window due to
        // other factors, however
        //So change the string from "true" to "false"
        //to see if the lag is fixed
        System.setProperty("sun.java2d.opengl", "true");
        //This is our meepmeep instance. The 700 is the dimension
        //of the window in pixels.
        MeepMeep meepMeep = new MeepMeep(700);
        //This creates a virtual robot where all of your actions
        //will be assigned to. The constraints are found in the
        //MecanumDrive class in the teamCode.
        //Remember to update these constraints as
        //you do in the MecanumDrive class, to keep your
        //simulations and real-world semi-accurate
        int waitTime = 1;
        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep).
                setConstraints(90, 70, 55, 60, 14).
                build();
        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(-62,23.4,0))
//                .turn(-(Math.PI)/8)
//                //launch
//                .waitSeconds(waitTime)
//                .strafeToLinearHeading(new Vector2d(-23.2,57),Math.PI/2)
//                //intake
//                .waitSeconds(waitTime)
//                .strafeToLinearHeading(new Vector2d(-62,23.4),-Math.PI/8)
//                //launch
//                .waitSeconds(waitTime)
//               .turnTo(Math.PI/6)
//                .splineToLinearHeading(new Pose2d(38,46.9,0),-0.3)
//                //Spline makes the robot do a curve movement
//
//                .strafeToConstantHeading(new Vector2d(38,57))
  //              .strafeToConstantHeading(new Vector2d(-58,23.4))
    //            .turnTo(Math.PI/6)
      //          .splineToLinearHeading(new Pose2d(38,46.9,0),-0.3)
        //        .strafeToConstantHeading(new Vector2d(38,57))




                //Place your actions here
                //NOTE: Only strafes, splines, and lines work here,
                //since all you're simulating/creating are the waypoints
                //and paths the robot will take
                .build());
        //This final line should not be modified, except for changing the background
        //This line tells the meepMeep object to set some parameters and then
        //run our actions laid out above.
        Image img = ImageIO.read(new File("C:/Users/Flour Bluff/Downloads/Biobuzzfield.png"));
        meepMeep.setBackground(img)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}
