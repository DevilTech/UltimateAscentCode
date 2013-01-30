package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;


public class Hopper 
{
    Servo hopper;
    
    public Hopper(int port)
    {
        hopper = new Servo(port);
    }
    
    public void load()
    {
        //turn the servo 90 degrees and then back (load a frisbee)
        hopper.setAngle(90);
        Timer.delay(.3);
        hopper.setAngle(0);
    }
}
