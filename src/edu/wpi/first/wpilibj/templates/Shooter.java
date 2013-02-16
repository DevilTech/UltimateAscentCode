package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Shooter 
{
    CANJaguar shoot;
    
    public Shooter(int port)
    {
        try 
        {
            shoot = new CANJaguar(port);
        }
        catch (CANTimeoutException ex) 
        {
            ex.printStackTrace();
        }
    }
    
    public void shoot()
    {
        try 
        {
            shoot.setX(SmartDashboard.getNumber("Shooter Motor Speed"));
        }
        catch (CANTimeoutException ex)
        {
            ex.printStackTrace();
          
        }
    }
    
    public void stop()
    {
        try 
        {
            shoot.setX(0);
        }
        catch (CANTimeoutException ex) 
        {
            ex.printStackTrace();
        }
    }
}
