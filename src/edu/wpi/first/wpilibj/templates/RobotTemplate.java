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
    Shooter shooter;
    boolean shooting;
    Hopper hopper;
    DigitalInput autonomousA;
    DigitalInput autonomousB;
    int time;

    
    public void robotInit() {
        leftMotor = new Victor(2);
        rightMotor = new Victor(4);
        wheel = new Joystick(2);
        stick = new Joystick(1);
        rd = new RobotDrive(leftMotor, rightMotor);
        dthread = new DriveThread(this, rd, wheel, stick);
        shootOn = new JoystickButton(stick, 2);
        shootOff = new JoystickButton(stick, 3);
        shooter = new Shooter(8);
        hopper = new Hopper(7);
        autonomousA = new DigitalInput(4);
        autonomousB = new DigitalInput(5);
    }
    
    public void autonomous()
{
	/*if(autonomousSwitch.get() == 0)
	{
		time = 2;
	}
	if(autonomousSwitch.get() == 1)
	{
		time = 6;
	}
	if(autonomousSwitch.get())
	{
		time = 11;
	}*/

	while(isAutonomous())
	{
		shooter.shoot();
		Timer.delay(time);
		hopper.load();
                Timer.delay(1);
                hopper.load();
                Timer.delay(1);
                hopper.load();
	}
}


    public void operatorControl()
    {
        (new Thread(dthread)).start();
        shooting = false;
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
                shooter.shoot();
            }
            else
            {
                System.out.println("Not Shooting");
                shooter.stop();
            }
            
            if(stick.getRawButton(1) && shooting)
            {
                hopper.load();
            }    
            
        }
    }
    
    public void test()
    {
    
    }
    
    public void disabled()
    {
	leftMotor.set(0);
        rightMotor.set(0);
        shooter.stop();
    }

}
