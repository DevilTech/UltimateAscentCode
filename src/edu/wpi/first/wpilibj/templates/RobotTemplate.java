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

    
    public void robotInit()
    {
        leftMotor = new Victor(Wiring.LEFT_WHEEL);
        rightMotor = new Victor(Wiring.RIGHT_WHEEL);
        wheel = new Joystick(Wiring.WHEEL);
        stick = new Joystick(Wiring.THROTTLE);
        rd = new RobotDrive(leftMotor, rightMotor);
        dthread = new DriveThread(this, rd, wheel, stick);
        shootOn = new JoystickButton(stick, Wiring.L3_BUTTON);
        shootOff = new JoystickButton(stick, Wiring.R3_BUTTON);
        shooter = new Shooter(Wiring.SHOOTER_MOTOR);
        hopper = new Hopper(Wiring.HOPPER_SERVO);
        autonomousA = new DigitalInput(Wiring.AUTONOMOUS_SWITCH_A);
        autonomousB = new DigitalInput(Wiring.AUTONOMOUS_SWITCH_B);
    }
    
    public void autonomous()
    {
        //left for 2s, mid for 6s, right for 11s
	if(autonomousA.get() && !autonomousB.get())
	{
		time = 2;
	}
	if(autonomousA.get() && autonomousB.get())
	{
		time = 6;
	}
	if(!autonomousA.get() && autonomousB.get())
	{
		time = 11;
	}

        //shoot 3 with 1s delay in between
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
            //logic for toggling
            if(shootOn.debouncedValue())
            {
                shooting = true;
            }
            else if(shootOff.debouncedValue())
            {
                shooting = false;
            }
            
            //shoot if not already pressed down
            if(shooting)
            {
                shooter.shoot();
            }
            else
            {
                shooter.stop();
            }
            
            //semi automatic shooting system
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
