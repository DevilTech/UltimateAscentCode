package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

public class ClimbingSystem
{
    
    Solenoid up;
    Solenoid down;
    Solenoid forward;
    Solenoid back;
    Jaguar winch;
    DigitalInput home;
    DigitalInput part;
    DigitalInput max;
    
    public ClimbingSystem(int u, int d, int f, int b, int w, int h, int p, int m)
    {
        up = new Solenoid(u);
        down = new Solenoid(d);
        forward = new Solenoid(f);
        back = new Solenoid(b);
        home = new DigitalInput(h);
        part = new DigitalInput(p);
        max = new DigitalInput(m);
        winch = new Jaguar(w);
    }
    
    public void goUpMax()
    {
        //try 
        //{
            up.set(true);
            down.set(false);
            winch.set(-0.5);

        //}
        //catch (CANTimeoutException ex) 
        //{
        //    ex.printStackTrace();
        //}    
                
    }
    
    public void goUpPart()
    {
        //try 
        //{
            up.set(true);
            down.set(false);
            winch.set(-0.5);
        //}
        //catch (CANTimeoutException ex) 
        //{
        //    ex.printStackTrace();
        //}
    }
    
    public void climb()
    {
       //**ATTENTION** FIRST GO DOWN SLOW, THEN ONCE THE CURRENT REACHES A CERTAIN THREASHHOLD, THEN GO DOWN QUICKLY AND CONTINUE UNTIL YOU HIT HOME.
    }
    
    public void goDownSlow()
    {
        up.set(false);
        down.set(true);
        winch.set(0.5);
    }
    public void goDown()
    {
        //try 
        //{
            System.out.println("Go Down");
            up.set(false);
            down.set(false);
            winch.set(0.5);
        //}
        //catch (CANTimeoutException ex) 
        //{
        //    ex.printStackTrace();
        //}
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
    
    public void stopDown()
    {
        up.set(false);
        down.set(false);
        winch.set(0.0);
    }
    
    public void stopUp()
    {
        up.set(false);
        down.set(true);
        winch.set(0.0);
    }
    
    public boolean isHome()
    {
        return home.get();
    }
    
    public boolean isPart()
    {
        return part.get();
    }
    
    public boolean isMax()
    {
        return max.get();
    }
}