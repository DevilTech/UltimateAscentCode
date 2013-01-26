package edu.wpi.first.wpilibj.templates;

import com.sun.squawk.platform.windows.natives.Time;
import edu.wpi.first.wpilibj.*;




public class RobotTemplate extends SimpleRobot
{
    DriveThread dthread;
    Victor leftMotor;
    Victor rightMotor;
    RobotDrive rd;
    Joystick stick;
    Joystick wheel;
    JoystickButton shootOn;
    JoystickButton shootOff;
    JoystickButton h90;
    Victor shooter;
    boolean shooting;
    boolean automatic;
    Servo hopper;
    
    public void robotInit() {
        leftMotor = new Victor(2);
        rightMotor = new Victor(4);
        wheel = new Joystick(2);
        stick = new Joystick(1);
        rd = new RobotDrive(leftMotor, rightMotor);
        dthread = new DriveThread(this, rd, wheel, stick);
        shootOn = new JoystickButton(stick, 2);
        shootOff = new JoystickButton(stick, 3);
        shooter = new Victor(8);
        hopper = new Servo(7);
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
        shooting = false;
        automatic = true;
        while(isOperatorControl())
        {
            if(shootOn.debouncedValue())
            {
                System.out.println("Shooting set to true");
                shooting = true;
            }
            else if(shootOff.debouncedValue())
            {
                System.out.println("Shooting set to false");
                shooting = false;
            }
            
            if(shooting)
            {
                System.out.println("Shooting");
                shooter.set(1);
            }
            else
            {
                System.out.println("Not Shooting");
                shooter.set(0);
            }
            
            if(stick.getRawButton(1) && shooting && automatic)
            {
                System.out.println("Servo set to 90");
                hopper.setAngle(90);
                automatic = false;
                Timer.delay(.3);
                hopper.setAngle(0);
                Timer.delay(.5);
                automatic = true;
            }    
            
        }
    }
    
    public void test()
    {
    
    }
}
