package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;

public class DriveThread implements Runnable
{
    RobotTemplate main;
    RobotDrive drive;
    Joystick wheel;
    Joystick stick;
    
    
    public DriveThread(RobotTemplate main, RobotDrive drive, Joystick wheel, Joystick stick)
    {
        this.main = main;
        this.drive = drive;
        this.stick = stick;
        this.wheel = wheel;
    }

    public void run()
    {
        JoystickButton leftTick = new JoystickButton(wheel, Wiring.L1_BUTTON);
        JoystickButton rightTick = new JoystickButton(wheel, Wiring.R1_BUTTON);
        
        while(main.isOperatorControl())
        {
            //rotating small distances left or right for accurate aiming
            if(leftTick.debouncedValue())
            {
                drive.arcadeDrive(-0.5, 0.0);
                Timer.delay(Wiring.TURN_DELAY);
                drive.arcadeDrive(0.0, 0.0);
            }
            else if(rightTick.debouncedValue())
            {
                drive.arcadeDrive(0.5, 0.0);
                Timer.delay(Wiring.TURN_DELAY);
                drive.arcadeDrive(0.0, 0.0);
            }
            else
            {
                drive.arcadeDrive(-1*stick.getY(), -1*wheel.getX());  
            }
            
        }
    }
    
}
