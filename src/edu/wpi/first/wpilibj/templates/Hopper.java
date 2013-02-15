package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Hopper 
{
    Victor hopper;
    DigitalInput mag;
    Timer time;
    
    
    public Hopper(int port, DigitalInput magnet)
    {
        hopper = new Victor(port);
        mag = magnet;
       
    }
    
    public void load()
    {
        time.reset();
        time.start();
        while(!mag.get() && time.get() < 5.0){
            hopper.set(.25);
        }
        while(mag.get() && time.get() < 5.0){
            hopper.set(.25);
        }
        hopper.set(0);
        if(time.get() > 5.0){
            System.out.println("Kicker Timeout");
        }
    }
}
