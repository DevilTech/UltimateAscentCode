package edu.wpi.first.wpilibj.templates;


import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Hopper 
{
    Victor hopper;
    Timer time;
    DigitalInput mag;
    
    
    public Hopper(int port, DigitalInput magnet)
    {
        hopper = new Victor(port);
        time = new Timer();
        mag = magnet;
       
    }
    
    public void load()
    {
        time.reset();
        time.start();
        while (!mag.get() && time.get() < 5.0) {
            hopper.set(.25);
            System.out.println(mag.get());
        }
        while (mag.get() && time.get() < 5.0) {
            hopper.set(.25);
            System.out.println(mag.get());
        }
        hopper.set(0);
        if (time.get() > 5.0) {
            System.out.println("Kicker timeout");
            System.out.println(mag.get());
        }
    }
}
