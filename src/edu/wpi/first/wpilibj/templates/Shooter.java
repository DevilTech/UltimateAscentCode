package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Victor;


public class Shooter 
{
    Victor shooter;
    
    public Shooter(int port)
    {
        shooter = new Victor(port);
    }
    
    public void shoot()
    {
        shooter.set(1);
    }
    
    public void stop()
    {
        shooter.set(0);
    }
}
