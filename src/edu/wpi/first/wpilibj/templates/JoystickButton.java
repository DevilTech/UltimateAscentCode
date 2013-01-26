package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Joystick;


public class JoystickButton {
    Joystick joy;
    int button;
    boolean flag = true;
    
    public JoystickButton( int JoystickNum, int Button)
    {
        joy = new Joystick(JoystickNum);
        button = Button;
    }
    
    public JoystickButton(Joystick joy, int Button)
    {
        this.joy = joy;
        button = Button;
    }
    
    public boolean debouncedValue()
    {
        if(joy.getRawButton(button))
        {
            if(flag)
            {
                flag = false;
                return true;
            }
        }
        else
        {
            flag = true;
        }
        return false;
    }

}
