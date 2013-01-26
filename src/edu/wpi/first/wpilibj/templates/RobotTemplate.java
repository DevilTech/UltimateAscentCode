package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Victor;


public class RobotTemplate extends SimpleRobot
{
    DriveThread dthread;
    Victor leftMotor;
    Victor rightMotor;
    RobotDrive rd;
    Joystick stick;
    Joystick wheel;
    
    public void robotInit() {
        leftMotor = new Victor(2);
        rightMotor = new Victor(4);
        wheel = new Joystick(2);
        stick = new Joystick(1);
        rd = new RobotDrive(leftMotor, rightMotor);
        dthread = new DriveThread(this, rd, wheel, stick);
    }
    
    public void autonomous()
    {
        while(isAutonomous())
        {
            
        }
    }

    public void operatorControl()
    {
        (new Thread(dthread)).start();
        while(isOperatorControl())
        {
            
        }
    }
    
    public void test()
    {
    
    }
}
