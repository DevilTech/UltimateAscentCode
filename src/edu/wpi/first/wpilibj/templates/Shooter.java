package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Shooter 
{
    CANJaguar shoot;
    boolean hasSeenMax = false;
    final double aCHigh = 25;
    final double aCLow  = 19;
    
    public Shooter(int port)
    {
        try 
        {
            shoot = new CANJaguar(port);
            shoot.setVoltageRampRate(Wiring.RAMP_VOLTS_PER_SECOND);
        }
        catch (CANTimeoutException ex) 
        {
        
        }
    }
    
    public void shoot()
    {
        try 
        {
            shoot.setX(SmartDashboard.getNumber("Shooter Motor Speed"));
            SmartDashboard.putNumber("Shooter Motor Current", shoot.getOutputCurrent());
        }
        catch (CANTimeoutException ex)
        {
            ex.printStackTrace();
        }
        try {
            if(hasSeenMax && shoot.getOutputCurrent() < aCLow)
            {
                SmartDashboard.putBoolean("Shooter Up To Speed", true);
            }
            else if (shoot.getOutputCurrent() > aCHigh)
            {
                hasSeenMax = true;
            }
        } catch (CANTimeoutException ex) {
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
        hasSeenMax = false;
        SmartDashboard.putBoolean("Shooter Up To Speed", false);
    }
}
