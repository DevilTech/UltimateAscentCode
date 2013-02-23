package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

public class DriveThread implements Runnable
{
    RobotTemplate main;
    RobotDrive drive;
    Joystick stick;
    CANJaguar leftJag;
    CANJaguar rightJag;
    
    
    public DriveThread(RobotTemplate main, RobotDrive drive, Joystick stick, CANJaguar right, CANJaguar left)
    {
        this.main = main;
        this.drive = drive;
        this.stick = stick;
        leftJag = left;
        rightJag = right;
        this.drive.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
        this.drive.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
    }

    public void run()
    {
        JoystickButton leftTick = new JoystickButton(stick, Wiring.XBOX_LEFT_BUMPER);
        JoystickButton rightTick = new JoystickButton(stick, Wiring.XBOX_RIGHT_BUMPER);
        
        while(main.isOperatorControl())
        {
            //rotating small distances left or right for accurate aiming
           /* if(leftTick.debouncedValue())
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
            } */
            
            if(leftTick.debouncedValue())
            {
                try {
                    rightJag.setX(-.5);
                    leftJag.setX(-.5);
                } catch (CANTimeoutException ex) {
                    ex.printStackTrace();
                }
                Timer.delay(Wiring.TURN_DELAY);
                drive.arcadeDrive(0.0, 0.0);
            }
            else if(rightTick.debouncedValue())
            {
                try {
                    rightJag.setX(.5);
                    leftJag.setX(.5);
                } catch (CANTimeoutException ex) {
                    ex.printStackTrace();
                }
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
