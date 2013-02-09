package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Hopper 
{
    Servo hopper;
    
    
    public Hopper(int port)
    {
        hopper = new Servo(port);
       
    }
    
    public void load()
    {
        //turn the servo 60 degrees and then back (load a frisbee)
        System.out.println("Load");
        hopper.setAngle(SmartDashboard.getNumber("Higher Servo Angle"));
       
        Timer.delay(.3);
        hopper.setAngle(SmartDashboard.getNumber("Lower Servo Angle"));
    }
}
