package edu.wpi.first.wpilibj.templates;

public class Wiring
{
    //Climb buttons
    static public final int CLIMB_UP_PART               = 4;
    static public final int CLIMB_UP_MAX                = 5;
    static public final int CLIMB_DOWN                  = 2;
    static public final int AUTO_CLIMB_FIRST            = 6;
    static public final int AUTO_CLIMB                  = 7;
    static public final int FORWARD                     = 11;
    static public final int BACKWARD                    = 10;
    //wheel buttons
    static public final int WHEEL_X_BUTTON              = 1;
    static public final int SQUARE_BUTTON               = 2;
    static public final int CIRCLE_BUTTON               = 3;
    static public final int TRIANGLE_BUTTON             = 4;
    static public final int R1_BUTTON                   = 5;
    static public final int L1_BUTTON                   = 6;
    static public final int R2_BUTTON                   = 7;
    static public final int L2_BUTTON                   = 8;
    static public final int SELECT_BUTTON               = 9;
    static public final int START_BUTTON                = 10;
    static public final int R3_BUTTON                   = 11;
    static public final int L3_BUTTON                   = 12;
    //Joystick
    static public final int TRIGGER                     = 1;
    //XBox
    static public final int XBOX_A_BUTTON               = 1;
    static public final int XBOX_B_BUTTON               = 2;
    static public final int XBOX_X_BUTTON               = 3;
    static public final int XBOX_Y_BUTTON               = 4;
    static public final int XBOX_RIGHT_BUMPER           = 6;
    //CANJaguars
    static public final int WINCH_MOTOR                 = 5;
    static public final int LEFT_WHEEL                  = 7;
    static public final int RIGHT_WHEEL                 = 6;
    static public final int SHOOTER_MOTOR               = 4;
    //Victors
    static public final int HOPPER_VICTOR               = 4;
    //Climbing

    //solenoid
    static public final int CLIMB_SOLENOID_UP           = 1;
    static public final int CLIMBING_SOLENOID_FORWARD   = 2;
    static public final int CLIMBING_SOLENOID_BACKWARD  = 3;
    //Analog
    static public final int GYRO_ANALOG                 = 2;
    //Digital I/O
    static public final int HOPPER_MAGNET               = 2;
    static public final int AUTONOMOUS_SWITCH_A         = 6;
    static public final int AUTONOMOUS_SWITCH_B         = 7;
    static public final int CYLINDER_HOME               = 3;
    static public final int CYLINDER_PART               = 4;
    static public final int CYLINDER_MAX                = 5;
    //Joysticks
    static public final int WHEEL                       = 2;
    static public final int THROTTLE                    = 1;
    static public final int COPILOT                     = 3;
    //MISC
            static public final double TURN_TOLERANCE   = .75;
    static public final double TURN_DELAY               = 1.0/16.0;
    static public final double P_SPEED                  = .76;
    static public final double I_SPEED                  = .046;
    static public final double D_SPEED                  = 0.0;
    static public final double P                        = .8;
    static public final double I                        = .02;
    static public final double D                        = .4;
    static public final int TICKSPERREV                 = 360;
    static public final int WHEELSPROCKET               = 42;
    static public final int DRIVESPROCKET               = 38;
    static public final int MAXJAGVOLTAGE               = 12;

}

