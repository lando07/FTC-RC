package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

/**
 * This is an example class on how to create an autonomous
 * simulation using MeepMeep.
 */
public class ExampleMeepMeep {
    /*
     * Every MeepMeep will use a main method to run it's code,
     * as it is fully self-contained.
     */
    public static void main(String[] args) {
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
        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep).
                setConstraints(90, 70, 55, 60, 14).
                build();
        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(0,0,0))
                //Place your actions here
                //NOTE: Only strafes, splines, and lines work here,
                //since all you're simulating/creating are the waypoints
                //and paths the robot will take
                .build());
        //This final line should not be modified, except for changing the background
        //This line tells the meepMeep object to set some parameters and then
        //run our actions laid out above.
        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();

    }
}
