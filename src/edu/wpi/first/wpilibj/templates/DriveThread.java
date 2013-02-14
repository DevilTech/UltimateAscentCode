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
        this.drive.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
        this.drive.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
    }

    public void run()
    {
        JoystickButton leftTick = new JoystickButton(stick, 4);
        JoystickButton rightTick = new JoystickButton(stick, 5);
        
        while(main.isOperatorControl())
        {
            //rotating small distances left or right for accurate aiming
            if(leftTick.debouncedValue())
            {
                drive.arcadeDrive(0.0, -0.75);
                Timer.delay(Wiring.TURN_DELAY);
                drive.arcadeDrive(0.0, 0.0);
            }
            else if(rightTick.debouncedValue())
            {
                drive.arcadeDrive(0.0, 0.75);
                Timer.delay(Wiring.TURN_DELAY);
                drive.arcadeDrive(0.0, 0.0);
            }
            else
            {
                drive.arcadeDrive(stick);  
            }
            
        }
    }
    
}
