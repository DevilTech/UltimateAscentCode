package edu.wpi.first.wpilibj.templates;

import com.sun.squawk.platform.windows.natives.Time;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.can.CANTimeoutException;




public class RobotTemplate extends SimpleRobot
{
    DriveThread dthread;
    CANJaguar leftMotor;
    CANJaguar rightMotor;
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
        try 
        {
            leftMotor = new CANJaguar(Wiring.LEFT_WHEEL);
            rightMotor = new CANJaguar(Wiring.RIGHT_WHEEL);
            wheel = new Joystick(Wiring.WHEEL);
            stick = new Joystick(Wiring.THROTTLE);
            rd = new RobotDrive(leftMotor, rightMotor);
            dthread = new DriveThread(this, rd, wheel, stick);
            shootOn = new JoystickButton(stick, Wiring.SHOOTER_ON);
            shootOff = new JoystickButton(stick, Wiring.SHOOTER_OFF);
            shooter = new Shooter(Wiring.SHOOTER_MOTOR);
            hopper = new Hopper(Wiring.HOPPER_SERVO);
            autonomousA = new DigitalInput(Wiring.AUTONOMOUS_SWITCH_A);
            autonomousB = new DigitalInput(Wiring.AUTONOMOUS_SWITCH_B);
        } 
        catch (CANTimeoutException ex) 
        {
            ex.printStackTrace();
        }
    }
    
     // Configure a Jaguar for Position mode
    public void cfgPosMode(CANJaguar jag)
    {
        try
        {
            jag.disableControl();
            jag.changeControlMode(CANJaguar.ControlMode.kPosition);
            jag.setPositionReference(CANJaguar.PositionReference.kQuadEncoder);
            jag.setPID(0.0, 0.0, 0.0);
            jag.configEncoderCodesPerRev((Wiring.TICKSPERREV * Wiring.WHEELSPROCKET) / Wiring.DRIVESPROCKET);
            jag.configMaxOutputVoltage(Wiring.MAXJAGVOLTAGE);
            //jag.setVoltageRampRate(50);
            jag.enableControl();
        }
        catch (Exception ex)
        {
            System.out.println(ex.toString());
        }
    }   //  cfgPosMode

    // Configure a Jaguar for Speed mode
    public void cfgSpeedMode(CANJaguar jag)
    {
        try
        {
            jag.disableControl();
            jag.changeControlMode(CANJaguar.ControlMode.kSpeed);
            jag.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
            jag.setPID(Wiring.P,Wiring.I,Wiring.D);
	    //jag.setPID(0.76, 0.046, 0.0);
            jag.configEncoderCodesPerRev((Wiring.TICKSPERREV * Wiring.WHEELSPROCKET) / Wiring.DRIVESPROCKET);
	    //jag.configMaxOutputVoltage(MAXJAGVOLTAGE);
            //jag.setVoltageRampRate(50);
            jag.enableControl();
        }
        catch (Exception ex)
        {
            System.out.println(ex.toString());
        }
    }   //  cfgSpeedMode

    // Configure a Jaguar for normal (PWM like) mode
    public void cfgNormalMode(CANJaguar jag)
    {
        try
        {
            jag.disableControl();
            jag.changeControlMode(CANJaguar.ControlMode.kPercentVbus);
            jag.configMaxOutputVoltage(Wiring.MAXJAGVOLTAGE);
            jag.enableControl();      
        }
        catch (Exception ex)
        {
            System.out.println(ex.toString());
        }
    }   //  cfgNormalMode

    
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
        
        cfgPosMode(leftMotor);
        cfgPosMode(rightMotor);
        
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
        
        cfgNormalMode(leftMotor);
        cfgNormalMode(rightMotor);
        
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
            if(stick.getRawButton(Wiring.TRIGGER) && shooting)
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
