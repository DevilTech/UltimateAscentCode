package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;

public class Hopper 
{
    Victor hopper;
    DigitalInput mag;
    Timer time;
    ErrorHandler error;
    
    public Hopper(int port)
    {
        hopper = new Victor(port);
        time = new Timer();
        mag = new DigitalInput(Wiring.HOPPER_MAGNET);
        error = ErrorHandler.getErrorHandler();
    }
    
    public void load()
    {
        time.reset();
        time.start();

        while(!mag.get() && time.get() < 5.0)
        {
            hopper.set(.25);
        }

        while(mag.get() && time.get() < 5.0)
        {
            hopper.set(.25);
        }

        hopper.set(0);
	
        if(time.get() > 5.0)
        {
            error.error("KICKER TIME-OUT");
        }
    }
}
