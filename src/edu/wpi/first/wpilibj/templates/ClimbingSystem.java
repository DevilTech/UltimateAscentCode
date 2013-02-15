package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
    double cur = 0;
    boolean goingUpPart = false;
    boolean goingUpMax = false;
    boolean goingDown = false;
    boolean goingDownSlow = false;
    boolean completed = false;
    int state = 0;
    final double thresh_hold = 11.0;
    final double downspeed = .25;
    final double upspeed = -.5;
    public ClimbingSystem(int u, int f, int b, int w, int h, int p, int m, RobotTemplate robo)
    {
        try 
        {
            up = new Solenoid(u);
            forward = new Solenoid(f);
            back = new Solenoid(b);
            home = new DigitalInput(h);
            part = new DigitalInput(p);
            max = new DigitalInput(m);
            winch = new CANJaguar(w);
            winch.configMaxOutputVoltage(6.0);
            this.robo = robo;
            time.start();
            cur = 0;
        } 
        catch (CANTimeoutException ex) 
        {
            ex.printStackTrace();
        }
    }
    
    public void autoClimbPartial(int button)
    {
        autoClimb(false, button);
    }
    public void autoClimbMax(int button)
    {
        autoClimb(true, button);
    }
    public void autoClimb(boolean max, int button)
    {

        while(robo.isEnabled() && state != 5 && !robo.isButtonPressed(button))
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
        state = 0;
        System.out.println("****ABORTED*****");
    }
   
    
    public void goUpMax(int button)
    {
        double curTime = time.get();
        try
        {
            if(!max.get())
                {
                    return;
                }
                up.set(true);
                winch.setX(upspeed);
                while(robo.isEnabled())
                {
                    if(robo.isButtonPressed(button))
                    {
                        stop();
                        break;
                    }
                    System.out.println("Time: " + time.get());
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
        double curTime = time.get();
        try 
        {
            if(home.get())
            {
                
                return;
            }
            up.set(true);
            winch.setX(upspeed);
            while(robo.isEnabled())
            {
                if(robo.isButtonPressed(button))
                {
                    stop();
                    break;
                }
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
        try 
        {
            if(!home.get())
            {
                //we are at home: Bail out
                return;
            }
            up.set(true);
            winch.setX(downspeed);
            while(robo.isEnabled())
            {
                if(robo.isButtonPressed(button))
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
        forward.set(true);
        back.set(false);
    }
    
    public void goBackward()
    {
        forward.set(false);
        back.set(true);
    }
    
    
    public boolean isHitBar()
    {
        boolean hitbar = false;
        try 
        {
            SmartDashboard.putNumber("Current", winch.getOutputCurrent()); //change to winch.getOutputCurrent()
            SmartDashboard.putNumber("Cur", cur);
            double value = winch.getOutputCurrent();
            System.out.println("Current value: " + value + " cur: " + cur );          
            if(value <= 2.0) //Is 2.0 B/C we don't want to sample too fast so it will never fall out of the 
            {
                cur = value;
                System.out.println("Stuck Here, Value:  " + value);
            }
            else if(value >= (cur + thresh_hold))
            {
                hitbar = true;
                System.out.println("*********HITBAR IS TRUE******");
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
        System.out.println("Hitbar: " + hitbar);
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