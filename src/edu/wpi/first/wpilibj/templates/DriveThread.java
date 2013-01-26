package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;

public class DriveThread implements Runnable
{
    RobotTemplate main;
    RobotDrive drive;
    Joystick wheel;
    Joystick stick;
    
    
    public DriveThread(RobotTemplate main, RobotDrive drive, Joystick wheel, Joystick stick){
        this.main = main;
        this.drive = drive;
        this.stick = stick;
        this.wheel = wheel;
    }

    public void run() {
        while(main.isOperatorControl()){
            drive.arcadeDrive(wheel.getX(), stick.getY());
        }
    }
    
}
