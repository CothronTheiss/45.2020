//  ####   ####    ###   #   #  #####  ####    ###    ####  #####
//  #   #  #   #    #    #   #  #      #   #  #   #  #      #    
//  #   #  ####     #    #   #  ###    ####   #####   ###   ###  
//  #   #  #  #     #     # #   #      #   #  #   #      #  #    
//  ####   #   #   ###     #    #####  ####   #   #  ####   #####

// 	motors				
// 		Left 1	CIM	40A	Talon SRX CAN ID 0
// 		Left 2	CIM	40A	Talon SRX or Victor SPX CAN ID 2
// 		Right 1	CIM	40A	Talon SRX CAN ID 1
// 		Right 2	CIM	40A	Talon SRX or Victor SPX CAN ID 3
// 	sensors				
// 		Left encoder	CTRE Mag		Talon SRX CAN ID 0
// 		Right encoder	CTRE Mag		Talon SRX CAN ID 1
//    camera	MS LifeCam		USB

package frc.robot.drivebase;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drivebase {

//  #   #  ####    ###   #   #
//   # #   #   #  #   #   # # 
//    #    ####   #   #    #  
//   # #   #   #  #   #   # # 
//  #   #  ####    ###   #   #

  // XboX controller axis definitions
  private static final int XBOX_LEFT_Y = 1;
  private static final int XBOX_RIGHT_X = 4;
  private static final int XBOX_RIGHT_Y = 5;
  // XboX controller button definitions
  private static final int XBOX_A = 1;
  private static final int XBOX_B = 2;
  private static final int XBOX_X = 3;
  private static final int XBOX_Y = 4;
  private static final int XBOX_LEFT_SHOULDER = 5;
  private static final int XBOX_RIGHT_SHOULDER = 6;
  private static final int XBOX_BACK = 7;
  private static final int XBOX_START = 8;
  private static final int XBOX_LEFT_PRESS = 9;
  private static final int XBOX_RIGHT_PRESS = 10;


  // drivebase actuators
  private TalonSRX m_left1;
//  private TalonSRX m_left2;
  private VictorSPX m_left2;
  private TalonSRX m_right1;
//  private TalonSRX m_right2;
  private VictorSPX m_right2;
  // drivebase sensors
  // left encoder is on m_left1 TalonSRX
  // right encoder is on m_right1 TalonSRX
  // drivebase globals
  Joystick control;
  private double odoorigin = 0;
  private double dirorigin = 0;

  private boolean autodrive = false;
  private double odotarget;
  private double dirtarget;

  private double deadband(double in, double band) {
    double value = 0;
    if (in > band) {
      value = in-band;
    }
    if (in <-band) {
      value = in+band;
    }
    return value / (1-band);
  }

  private boolean driverVernier() {
    return control.getRawButton(XBOX_RIGHT_PRESS) || control.getRawAxis(XBOX_RIGHT_Y) > 0.5;
  }

  private boolean driverResetOdometer() {
    return control.getRawButton(XBOX_LEFT_PRESS);
  }

  private boolean driverResetDirection() {
    return control.getRawButton(XBOX_LEFT_PRESS);
  }

  private double driverThrottle() {
    double value = -control.getRawAxis(XBOX_LEFT_Y);
    return deadband(value,0.1);
  }

  private double driverSteer() {
// speed-sensitive steering: 1.0 -> 0.2, 0.0 -> 0.7
// y = mx + b | 0.2 = m*1.0 + b | 0.7 = m*0.0 + b
// b = 0.7, m = -0.5
    double speed = Math.abs(driverThrottle());
    if (driverVernier()) {
      speed = 1.0;
    }
    double sss = -0.5 * speed + 0.7;

    double value = control.getRawAxis(XBOX_RIGHT_X);
    return sss * deadband(value,0.1);
  }

  private double odoleft() {
    return m_left1.getSelectedSensorPosition();
  }

  private double odoright() {
    return m_right1.getSelectedSensorPosition();
  }

  private double odometer(boolean reset) {
    final double inchesPerCount = (
      6.0 /* inch */ / 1.0 /* diameter */
    * 3.14159 /* diameter */ / 1.0 /* circumference */
    * 1.0 /* circumference */ / 4096.0 /* count */
    );
    double raw = (odoleft()+odoright())/2.0;
    if (reset) {
      odoorigin = raw;
    }
    return (raw - odoorigin) * inchesPerCount;
  }

  private double direction(boolean reset) {
    final double degreesPerCount = (
      6.0 /* inch */ / 1.0 /* diameter */
    * 3.14159 /* diameter */ / 1.0 /* circumference */
    * 1.0 /* circumference */ / 4096.0 /* count */
    * 1.0 /* wheelbase */ / 23.0 /* inch */
    * 1.0 /* circle */ / 6.28308 /* wheelbase */
    * 360.0 /* degree */ / 1.0 /* circle */
    );
    double raw = (odoleft()-odoright());
    if (reset) {
      dirorigin = raw;
    }
    return (raw - dirorigin) * degreesPerCount;
  }

  
    // Creates a new Drivebase 
    public Drivebase(Joystick userControl) {
        // initialize
        control = userControl;
        m_left1 = new TalonSRX(10);
        m_left2 = new VictorSPX(11);
        /* Factory default hardware to prevent unexpected behavior */
        m_left1.configFactoryDefault();
        m_left2.configFactoryDefault();
        m_left1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        m_left1.setSensorPhase(true);
        m_left2.follow(m_left1);
        
        m_right1 = new TalonSRX(12);
        m_right2 = new VictorSPX(13);
        /* Factory default hardware to prevent unexpected behavior */
        m_right1.configFactoryDefault();
        m_right2.configFactoryDefault();
        m_right1.setInverted(true);
//        m_right2.setInverted(true);
        m_right2.setInverted(InvertType.FollowMaster);
        m_right1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        m_right1.setSensorPhase(true);
        m_right2.follow(m_right1);
        odometer(true);
        direction(true);
    }
    public void move(double reach, double turn) {
      autodrive = true;
      odotarget = odometer(true) + reach;
      dirtarget = direction(true) + turn;
    }
    public void drive(double throttle, double steer) {
        // control
        double left = throttle + steer;
        double right = throttle - steer;
        double max;
        if (Math.abs(left) > Math.abs(right)) {
          max = Math.abs(left);
        } else {
          max = Math.abs(right);
        }
        if (max > 1) {
          left /= max;
          right /= max;
        }
        m_left1.set(ControlMode.PercentOutput, left);
        //m_left2.set(ControlMode.PercentOutput, left);
        m_right1.set(ControlMode.PercentOutput, right);
        //m_right2.set(ControlMode.PercentOutput, right);
        }
    public void run() {
      if (control.getRawButtonPressed(XBOX_Y)) {
        move(60,0);
      }
      if (control.getRawButtonPressed(XBOX_X)) {
        move(0,-90);
      }
      if (control.getRawButtonPressed(XBOX_B)) {
        move(0, 90);
      }
      if (control.getRawButtonPressed(XBOX_A)) {
        move(-60, 0);
      }
      if (driverThrottle() != 0 || driverSteer() != 0) {
        autodrive = false;
      }
      if (autodrive) {
        double reach = (odotarget - odometer(false))/120.0;
        double twist = (dirtarget - direction(false))/90.0;
        if (reach > 0.25) {
          reach = 0.25;
        }
        if (reach < -0.25) {
          reach = -0.25;
        }
        if (twist > 0.25) {
          twist = 0.25;
        }
        if (twist < -0.25) {
          twist = -0.25;
        }
        drive (reach, twist);
      } else {
        drive(driverThrottle(), driverSteer());
      }
    }
    public void sense() {
        // telemetry
        double odo = odometer(driverResetOdometer());
        SmartDashboard.putNumber("odometer", odo);
        double dir = direction(driverResetDirection());
        SmartDashboard.putNumber("direction", dir);
    }
}