package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStationEnhancedIO.EnhancedIOException;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

public class ClimbingSystem
{
    Solenoid up;
    Solenoid forward;
    Solenoid back;
    CANJaguar winch;
    DigitalInput home;
    DigitalInput part;
    DigitalInput max;
    RobotTemplate robo;
    
    Timer time = new Timer();
    
    double typicalCurrent = 0;
 
    final double thresh_hold = 11.0;
    final double downspeed   =  0.25;
    final double upspeed     = -0.5;
    
    public ClimbingSystem(RobotTemplate robo)
    {
        try 
        {
            up = new Solenoid(Wiring.CLIMB_SOLENOID_UP);
            forward = new Solenoid(Wiring.CLIMBING_SOLENOID_FORWARD);
            back = new Solenoid(Wiring.CLIMBING_SOLENOID_BACKWARD);
            home = new DigitalInput(Wiring.CYLINDER_HOME);
            part = new DigitalInput(Wiring.CYLINDER_PART);
            max = new DigitalInput(Wiring.CYLINDER_MAX);
            winch = new CANJaguar(Wiring.WINCH_MOTOR);
            winch.configMaxOutputVoltage(6.0);
            this.robo = robo;
            time.start();
            typicalCurrent = 0;
        } 
        catch (CANTimeoutException ex) 
        {
            ex.printStackTrace();
        }
    }
    
    public void autoClimbPartial(int button)
    {
       System.out.println("Auto CLimb Part");
       autoClimb(false, button);
    }
    
    public void autoClimbMax(int button)
    {
        System.out.println("Auto CLimb Max");
        autoClimb(true, button);
    }
    
    public void autoClimb(boolean max, int button)
    {
        int state = 0;
        
        while(!robo.shouldAbort() && state != 5)
        {
            System.out.println(state);
            switch(state)
            {
                case 0:
                    goDownManual(button);
                    state = 1;
                    break;
                case 1:
                    goForward();
                    state = 2;
                    break;
                case 2:
                    if(!max)
                    {
                        goUpPartial(button);
                    }
                    else
                    {
                        goUpMax(button);
                    }
                    state = 3;
                    break;
                case 3:
                    goBackward();
                    state = 4;
                    break;
                case 4:
                    goDownManual(button);
                    state = 5;
                    break;
                default:
                    stop();
                    break;
            }
                
        }
        System.out.println("****ABORTED*****");
    }
   
    public void goUpMax(int button)
    {         
        System.out.println("Go Up Max");
        double curTime = time.get();
        try
        {
            if(!max.get())
            {
                return;
            }

            up.set(true);
            winch.setX(upspeed);

                while(true)
                {
                    if(robo.shouldAbort())
                    {
                        stop();
                        break;
                    }

                    // abort if stuck at home for more than 2 seconds
                    if(!home.get() && time.get() > curTime + 2.0)
                    {
                        winch.setX(0.0);
                        break;
                    }

                    if(!max.get())
                    {
                        winch.setX(0.0);
                        break;
                    }  
                }
        }
        catch (CANTimeoutException ex) 
        {
            ex.printStackTrace();
        }
    }
    
    public void goUpPartial(int button) 
    {
        System.out.println("Go Up Part");
        double curTime = time.get();
        try
        {
            if(home.get())
            {                
                return;
            }

            up.set(true);
            winch.setX(upspeed);
            
            while(true)
            {
                if(robo.shouldAbort())
                {
                    stop();
                    break;
                }

                //abort if stuck at home for more than 2 seconds
                if(!home.get() && time.get() > curTime + 2.0)
                {
                    winch.setX(0.0);
                    break;
                }

                if(!part.get() || !max.get())
                {
                    winch.setX(0.0);
                    break;
                }
            }
        } 
        catch (CANTimeoutException ex) 
        {
            ex.printStackTrace();
        }
    }
    
    public void goDownManual(int button)
    {
        System.out.println("Going Down");
        try
        {
            if(!home.get())
            {
                //we are at home: Bail out
                return;
            }

            up.set(true);
            winch.setX(downspeed);

            while(true)
            {
                if(robo.shouldAbort())
                {
                    stop();
                    break;
                }
                if(!home.get())
                {
                    winch.setX(0.0);
                    up.set(false);
                    break;
                }
                if(isHitBar())
                {
                    //**ATTENTION** adjust motor speed if needed to a higher speed
                    winch.setX(.5);
                    up.set(false);
                }
            }
        }
        catch (CANTimeoutException ex)
        {
            ex.printStackTrace();
        }
    }
    
    public void goForward()
    {
        System.out.println("Forward");
        back.set(false);
        forward.set(true);
    }
    
    public void goBackward()
    {
        System.out.println("Backward");
        forward.set(false);
        back.set(true);
    }
    
    public boolean isHitBar()
    {
        boolean hitbar = false;
        try 
        {       
            double value = winch.getOutputCurrent();
            
            //Is 2.0 B/C we don't want to sample too fast so it will never fall out of the 
            if (value <= 2.0) 
            {
                typicalCurrent = value;
            }
            else if (value >= (typicalCurrent + thresh_hold))
            {
                hitbar = true;
            }
            else
            {
                hitbar = false;
            }
        } 
        catch (CANTimeoutException ex) 
        {
            ex.printStackTrace();
        }
        return hitbar;
    }
    
    public void stop()
    {
        try {
            up.set(false);
            forward.set(false);
            back.set(false);
            winch.setX(0.0);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }
}