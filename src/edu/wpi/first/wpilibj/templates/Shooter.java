package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Victor;


public class Shooter 
{
    Victor shoot;
    
    public Shooter(int port)
    {
        shoot = new Victor(port);
    }
    
    public void shoot()
    {
        shoot.set(1);
    }
    
    public void stop()
    {
        shoot.set(0);
    }
}
