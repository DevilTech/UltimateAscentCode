package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RobotTemplate extends SimpleRobot
{
    //Drive System
    DriveThread dthread;
    CANJaguar leftMotor;
    CANJaguar rightMotor;
    RobotDrive drive;
    Joystick stick;
    Joystick wheel;
    Joystick copilot;
    
    //Shooter
    JoystickButton shootOn;
    JoystickButton shootOff;
    
    JoystickButton fire;
    Shooter shooter;
    Hopper hopper;
    boolean shooting = false;
    
    //Climbing
    JoystickButton upPart;
    JoystickButton upMax;
    JoystickButton down;
    JoystickButton autoClimb;
    DriverStationEnhancedIO driverStationButtons = DriverStation.getInstance().getEnhancedIO();
    Compressor comp; 
    ClimbingSystem climb;
    
    //Autonomous Crap
    DigitalInput autonomousA;
    DigitalInput autonomousB;
    Gyro gyro;
        
    PIDController pid;
    Output out;
    JoystickButton test;

    
    ErrorHandler errHandler = ErrorHandler.getErrorHandler();
    
    public void robotInit()
    {
        try 
        {
            //Joystick Constructors
            wheel       = new Joystick(Wiring.WHEEL);
            stick       = new Joystick(Wiring.THROTTLE);
            copilot     = new Joystick(Wiring.COPILOT);
            
            //Drive Constructors
            leftMotor   = new CANJaguar(Wiring.LEFT_WHEEL);
            rightMotor  = new CANJaguar(Wiring.RIGHT_WHEEL);// JAG CHANGE
            drive       = new RobotDrive(leftMotor, rightMotor);
            dthread     = new DriveThread(this, drive, stick, rightMotor, leftMotor);// JAG CHANGE
            
            //Climber Constructors
            upPart      = new JoystickButton(Wiring.CLIMB_UP_PART);
            upMax       = new JoystickButton(Wiring.CLIMB_UP_MAX);
            down        = new JoystickButton(Wiring.CLIMB_DOWN);
            autoClimb   = new JoystickButton(Wiring.AUTO_CLIMB);
            climb       = new ClimbingSystem(this);
            comp        = new Compressor(8,1);
            comp.start();
            climb.goDownManual(1); // **ATTENTION** take out at RIT in order to pass rules
            
            //Shooter Constructors
            shootOn     = new JoystickButton(stick, Wiring.XBOX_A_BUTTON);
            shootOff    = new JoystickButton(stick, Wiring.XBOX_B_BUTTON);
            fire        = new JoystickButton(stick, Wiring.XBOX_X_BUTTON);
            shooter     = new Shooter(Wiring.SHOOTER_MOTOR);
            hopper      = new Hopper(Wiring.HOPPER_MOTOR);
            
            //Autonomous Stuff
            autonomousA = new DigitalInput(Wiring.AUTONOMOUS_SWITCH_A);
            autonomousB = new DigitalInput(Wiring.AUTONOMOUS_SWITCH_B);
            gyro        = new Gyro(Wiring.GYRO_ANALOG);
            out         = new Output();
            pid         = new PIDController(Wiring.P, Wiring.I, Wiring.D, gyro, out);
            pid.setAbsoluteTolerance(1);
            test = new JoystickButton(Wiring.XBOX_Y_BUTTON);
            SmartDashboard.putBoolean("Shooter Up To Speed", false);

        } 
        catch (CANTimeoutException ex) 
        {
            ex.printStackTrace();  //JAG CHANGE
        }
    }

    public void operatorControl()
    {
        (new Thread(dthread)).start();
        cfgNormalMode(leftMotor);
        cfgNormalMode(rightMotor);
        climb.goDownManual(Wiring.CLIMB_DOWN);
        while(isOperatorControl()&& isEnabled())
        {           
            if(stick.getRawButton(Wiring.XBOX_Y_BUTTON))
            {
                try {
                    climb.winch.setX(.75);
                } catch (CANTimeoutException ex) {
                    ex.printStackTrace();
                }
            }
            else
            {
                try {
                    climb.winch.setX(0.0);
                } catch (CANTimeoutException ex) {
                    ex.printStackTrace();
                }
            }
            errHandler.refresh();
            climbingCheck();
            shooterCheck();
            Thread.yield();
        }
    }
    
    public void shooterCheck()
    {   
        // logic for shooter motor control
        if (shootOn.debouncedValue())
        {
            shooting = true;
            shooter.shoot();
        }
        else if (shootOff.debouncedValue())
        {
            shooter.stop();
            shooting = false;
        }

        // shoot if not already pressed down
        if(shooting)
        {
            shooter.shoot();
        }
        else
        {
            shooter.stop();
        }

        //semi automatic shooting system
        if(fire.debouncedValue() && shooting)
        {
            hopper.load();
        }    
    }
    
    
    public void climbingCheck()
    {
        try {
            SmartDashboard.putBoolean("AT HOME", !climb.home.get());
            //climbing
            //System.out.println("Climb On: " + driverStationButtons.getDigital(Wiring.CLIMB_ON));
            //System.out.println("CLIMBING SENSORS: HOME- " + climb.home.get() + " PART- " + climb.part.get() + " MAX- " + climb.max.get());
            //System.out.println("Partial Sensor: " + climb.part.get() + " Climbing Counter: " + climb.count.get());
            if (driverStationButtons.getDigital(Wiring.CLIMB_ON))
            {
               //System.out.println("Up Part Button: " + upPart.debouncedValueDigital());
               //System.out.println("Up Max Button: " + upMax.debouncedValueDigital());
               //System.out.println("Down Button: " + down.debouncedValueDigital());
                

               if (upPart.debouncedValueDigital())
               {
                   climb.goUpPartial(Wiring.CLIMB_UP_PART);
               }

               else if (upMax.debouncedValueDigital())
               {
                   climb.goUpMax(Wiring.CLIMB_UP_MAX);
               }

               else if (down.debouncedValueDigital())
               {
                   climb.goDownManual(Wiring.CLIMB_DOWN);
               }

               else if (autoClimb.debouncedValueDigital())
               {
                    if(!driverStationButtons.getDigital(Wiring.FORWARD_BACK))
                    {
                        climb.autoClimbPartial(Wiring.AUTO_CLIMB_FIRST);
                    }
                    else 
                    {
                        //System.out.println("AutoClimb Button: " + autoClimb.debouncedValueDigital());
                        climb.autoClimbMax(Wiring.AUTO_CLIMB);
                    }
               } 
               else 
               {
                    //System.out.println("Forward Backward: " + driverStationButtons.getDigital(Wiring.FORWARD_BACK));
                    if (driverStationButtons.getDigital(Wiring.FORWARD_BACK))
                    {
                        climb.goBackward();
                    }
                    else
                    {
                        climb.goForward();
                    }
               }
            }
            else
            {
                climb.stop();
            }

        } 
        catch (DriverStationEnhancedIO.EnhancedIOException ex) 
        {
           // ex.printStackTrace(); **ATTENTION** FIX LATER, COMMENTED IN ORDER TO STOP THE PRINTS
        }
    }
   
    public boolean shouldAbort() 
    {
        try 
        {
            if(!isEnabled() || !driverStationButtons.getDigital(Wiring.CLIMB_ON) )
            {
                errHandler.error("ABORTING ALL CLIMBING OPERATIONS");
                return true;
            }
            
        }
        catch (DriverStationEnhancedIO.EnhancedIOException ex) 
        {
            ex.printStackTrace();
        }
        return false;
    }
    
    public void disabled()
    {
        
        try 
        {
            leftMotor.setX(0);
            rightMotor.setX(0);
            shooter.stop();   //  JAG CHANGE
            /*while(isDisabled())
            {
                System.out.println("Partial Sensor: " + climb.part.get() + " Top Sensor: " + climb.max.get());
               
            }*/
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
        } 
        catch (CANTimeoutException ex)
        {
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
            
        } 
        catch (CANTimeoutException ex) 
        {
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

     public void autonomous()
    {
        boolean frisbeesHaveBeenShot = false;
        double preShotDelay = 0.0;
        
        //Select the delay when shooting with a 3 position switch
        //left for 2sec, mid for 6sec, right for 11sec
	/*if(autonomousA.get() && !autonomousB.get())
	{
		preShotDelay = 2;
	}
	if(autonomousA.get() && autonomousB.get())
	{
		preShotDelay = 6;
	}
	if(!autonomousA.get() && autonomousB.get())
	{
		preShotDelay = 11;
	}*/
        
        
        errHandler.error("Autonomous");
        boolean hasShotFirst = false;
        
        //shoot 3 with 1s delay in between
        while(isAutonomous() && isEnabled())
        {
            while(!shooter.atSpeed())
            {
                shooter.shoot();
            }
            
            if(shooter.atSpeed())
            {
                hopper.load();
                shooter.shoot();
            }
            Timer.delay(1.5);

            
            
          /*  if(isAutonomous()){
            shooter.shoot();
            }
            else{
                break;
            }
            Timer.delay(6);
            
            //Timer.delay(preShotDelay);
            if(isAutonomous()){
            hopper.load();
            }
            else{
                break;
            }
            //System.out.println("#######################SHOT FIRST FRISBEE########################");
            Timer.delay(1.5);
            if(isAutonomous()){
            hopper.load();
            }
            else{
                break;
            }
            //System.out.println("#######################SHOT SECOND FRISBEE########################");
            Timer.delay(1.5);
            if(isAutonomous()){
            hopper.load();
            }else{
                break;
            }
            //System.out.println("#######################SHOT FINAL FRISBEE########################");
             */
        }
    }    
     
     public void test()
     {
        climb.goDownManual(Wiring.CLIMB_DOWN);
     }
     
}