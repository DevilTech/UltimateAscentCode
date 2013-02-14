package edu.wpi.first.wpilibj.templates;

import com.sun.squawk.platform.windows.natives.Time;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;




public class RobotTemplate extends SimpleRobot
{
    DriveThread dthread;
    CANJaguar leftMotor;
    CANJaguar rightMotor;
    RobotDrive drive;
    Joystick stick;
    Joystick wheel;
    Joystick copilot;
    JoystickButton shootOn;
    JoystickButton shootOff;
    JoystickButton move;
    JoystickButton h90;
    JoystickButton forward;
    JoystickButton backward;
    JoystickButton upPart;
    JoystickButton upMax;
    JoystickButton down;
    Shooter shooter;
    Hopper hopper;
    DigitalInput autonomousA;
    DigitalInput autonomousB;
    PIDController pid;
    Gyro gyro;
    int time;
    Output out;
    boolean frisbeesHaveBeenShot = false;
    Compressor comp; 
    ClimbingSystem climb;
    DigitalInput mag;
    
    public void robotInit()
    {
        try 
        {
            mag = new DigitalInput(Wiring.HOPPER_MAGNET);
            leftMotor = new CANJaguar(Wiring.LEFT_WHEEL);
            rightMotor = new CANJaguar(Wiring.RIGHT_WHEEL);// JAG CHANGE
            wheel = new Joystick(Wiring.WHEEL);
            stick = new Joystick(Wiring.THROTTLE);
            copilot = new Joystick(Wiring.COPILOT);
            drive = new RobotDrive(leftMotor, rightMotor);
            dthread = new DriveThread(this, drive, wheel, stick);// JAG CHANGE
            shootOn = new JoystickButton(stick, 3);
            shootOff = new JoystickButton(stick, 2);
            forward = new JoystickButton(copilot, Wiring.XBOX_A_BUTTON);
            backward = new JoystickButton(copilot, Wiring.XBOX_B_BUTTON);
            upPart = new JoystickButton(copilot, Wiring.XBOX_X_BUTTON);
            upMax = new JoystickButton(copilot, Wiring.XBOX_Y_BUTTON);
            down = new JoystickButton(copilot, Wiring.XBOX_RIGHT_BUMPER);
            move = new JoystickButton(stick, 6);
            shooter = new Shooter(Wiring.SHOOTER_MOTOR);
            hopper = new Hopper(Wiring.HOPPER_VICTOR, mag);
            autonomousA = new DigitalInput(Wiring.AUTONOMOUS_SWITCH_A);
            autonomousB = new DigitalInput(Wiring.AUTONOMOUS_SWITCH_B);
            gyro = new Gyro(Wiring.GYRO_ANALOG);
            out = new Output();
            pid = new PIDController(Wiring.P, Wiring.I, Wiring.D, gyro, out);
            pid.startLiveWindowMode();
            pid.setAbsoluteTolerance(1);
            SmartDashboard.putNumber("Lower Servo Angle", 0.0);
            SmartDashboard.putNumber("Higher Servo Angle", 0.0);
            SmartDashboard.putNumber("Shooter Motor Speed", 0.250);
            SmartDashboard.putNumber("P", 0.0);
            SmartDashboard.putNumber("I", 0.0);
            SmartDashboard.putNumber("D", 0.0);
            //comp = new Compressor(2,2);
           // climb = new ClimbingSystem(Wiring.CLIMBING_UP, Wiring.CLIMBING_DOWN, Wiring.CLIMBING_FORWARD, Wiring.CLIMBING_BACKWARD, Wiring.WINCH_MOTOR, Wiring.CYLINDER_HOME, Wiring.CYLINDER_PART, Wiring.CYLINDER_MAX);
            
        } 
        catch (CANTimeoutException ex) 
        {
            ex.printStackTrace();  //JAG CHANGE
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
            jag.setPID(1000, 0.01, 20);
            jag.configEncoderCodesPerRev(360/*((Wiring.TICKSPERREV * Wiring.WHEELSPROCKET) / Wiring.DRIVESPROCKET)/19*/);
            //jag.configMaxOutputVoltage(Wiring.MAXJAGVOLTAGE);
            //jag.setVoltageRampRate(50);
            jag.enableControl();
        }
        catch (Exception ex)
        {
            System.out.println(ex.toString());
        }
    }   //  cfgPosMode
    
    public void goForwardNormal (double inches)
    {
        try {
            double ticks = 0;
            cfgNormalMode(leftMotor);
            cfgNormalMode(rightMotor);
            ticks = leftMotor.getPosition() + 2.0;
            System.out.println("Starting Position: " + leftMotor.getPosition());
            while(leftMotor.getPosition() < ticks && isEnabled())
            {
                drive.arcadeDrive(0.5, 0.0);
                System.out.println(leftMotor.getPosition());
            }
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
        drive.arcadeDrive(0.0,0.0);
        
    }

    public void goForward(double inches)
    {
        try {

            cfgPosMode(leftMotor);
            cfgPosMode(rightMotor);
            leftMotor.setX(-10);
            rightMotor.setX(10);
            while(isEnabled())
            {
                System.out.println(leftMotor.getPosition());

            }
            leftMotor.setX(0);
            rightMotor.setX(0);
            cfgNormalMode(leftMotor);
            cfgNormalMode(rightMotor);
            
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }
    
    public void turn(int angle)
    {
        gyro.reset();
       // pid.setPID(SmartDashboard.getNumber("P"), SmartDashboard.getNumber("I"), SmartDashboard.getNumber("D"));
        pid.setSetpoint(angle);
        pid.setOutputRange(-25, 25);
        pid.enable();
        cfgSpeedMode(leftMotor);
        cfgSpeedMode(rightMotor);
        while(!pid.onTarget()&& (isEnabled() || isAutonomous()))
        {
            try
            {   System.out.println(out.getPidOut() + " , " + gyro.getAngle());
                if(angle > 0)
                {
                    leftMotor.setX(out.getPidOut());
                    rightMotor.setX(out.getPidOut());
                }
                else if(angle < 0)
                {
                    leftMotor.setX(out.getPidOut());
                    rightMotor.setX(out.getPidOut());
                    
                }
                System.out.println(isEnabled()+ " " + isAutonomous());
            }
            catch (CANTimeoutException ex)
            {
                ex.printStackTrace();
            }
        }
        pid.disable();
        drive.arcadeDrive(0.0 , 0.0);
        cfgNormalMode(leftMotor);
        cfgNormalMode(rightMotor);
        System.out.println("Stopped Turning");
    }
    
    // Configure a Jaguar for Speed mode
    public void cfgSpeedMode(CANJaguar jag)
    {
        try
        {
            jag.disableControl();
            jag.changeControlMode(CANJaguar.ControlMode.kSpeed);
            jag.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
            jag.setPID(Wiring.P_SPEED,Wiring.I_SPEED,Wiring.D_SPEED);
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
            
           /* if(!frisbeesHaveBeenShot)
            {
		shooter.shoot();
		Timer.delay(time);
		hopper.load();
                Timer.delay(1);
                hopper.load();
                Timer.delay(1);
                hopper.load();
                frisbeesHaveBeenShot = true;
            }
            else
            {
                try {
                    leftMotor.setX(1);
                    rightMotor.setX(1);
                } catch (CANTimeoutException ex) {
                    ex.printStackTrace();
                }
            }*/
	}
        
        cfgNormalMode(leftMotor);
        cfgNormalMode(rightMotor);
        
}
        

    public void operatorControl()
    {
        
        (new Thread(dthread)).start();
        boolean shooting = false;
        
        
        while(isEnabled())
        {  
            
            
            
            //logic for toggling
            System.out.println(mag.get());
            if(shootOn.debouncedValue())
            {
                shooting = true;
                System.out.println("Toggled on");
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
            
//            //climbing controls
//            if(forward.debouncedValue())
//            {
//                climb.goForward();
//            }
//            if(backward.debouncedValue())
//            {
//                climb.goBackward();
//            }
//            if(upPart.debouncedValue())
//            {
//                climb.goUpPart();
//            }
//            if(upMax.debouncedValue())
//            {
//                climb.goUpMax();
//            }
//            if(down.debouncedValue())
//            {
//                climb.goDown();
//            }
//            
//            //Climbing checks
//            if(!climb.isHome())
//            {
//                climb.stopDown();
//            }
//            if(!climb.isPart())
//            {
//                climb.stopUp();
//            }
//            if(!climb.isMax())
//            {
//                climb.stopUp();
//            }
            
            //semi automatic shooting system
            if(stick.getRawButton(1) && shooting)
            {
                hopper.load();
                System.out.println("Hoppa Moving");
            }    
            
        }
    }
    
    public void test()
    {
    
    }
    
    public void disabled()
    {
        try 
        {
            leftMotor.setX(0);
            rightMotor.setX(0);
            shooter.stop();   //  JAG CHANGE
        }
        catch (CANTimeoutException ex) 
        {
            ex.printStackTrace();
        }
    }
    public void turnRightRaw(Gyro gyro)
    {
        gyro.reset();
        while (gyro.getAngle() < 85)
        {
            drive.arcadeDrive(0, -.75);
        }
        drive.arcadeDrive(0,0);
    }
    public void turnLeftRaw(Gyro gyro)
    {
        gyro.reset();
        while(gyro.getAngle() > -85)
        {
            drive.arcadeDrive(0, .75);
        }
        drive.arcadeDrive(0,0);
    }
}