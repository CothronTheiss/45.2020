
//   ####   ###   #   #  #####  ####    ###   #####   ###   #####
//  #      #   #  ## ##  #      #   #    #    #      #   #  #    
//  # ###  #####  # # #  ###    ####     #    ###    #      ###  
//  #   #  #   #  #   #  #      #        #    #      #   #  #    
//   ###   #   #  #   #  #####  #       ###   #####   ###   #####

package frc.robot.gamepiece;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Gamepiece {
//  #   #  ####    ###   #   #
//   # #   #   #  #   #   # # 
//    #    ####   #   #    #  
//   # #   #   #  #   #   # # 
//  #   #  ####    ###   #   #

  // XboX controller axis definitions
  private static final int XBOX_LEFT_Y = 1;
  private static final int XBOX_RIGHT_X = 4;
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

  Joystick control;

  private boolean c_intake() {
      return control.getRawButton(XBOX_RIGHT_SHOULDER);
  }
  private boolean c_outtake() {
      return control.getRawButton(XBOX_LEFT_SHOULDER);
  }
  private boolean c_forward() {
      return control.getRawButton(XBOX_START);
  }
  private boolean c_backward() {
      return control.getRawButton(XBOX_BACK);
  }
  private boolean c_prefire() {
      return control.getRawButton(XBOX_X);
  }
  private boolean c_singleshot() {
      return control.getRawButton(XBOX_A);
  }
  private boolean c_volley() {
      return control.getRawButton(XBOX_Y);
  }
  private boolean c_override() {
    int pov = control.getPOV();
    boolean b = false;
    switch (pov) {
      case 315:
      case 0:
      case 45:
        b = true;
        break;
    }
    return b;
  }
  private boolean c_kick() {
      return control.getRawButton(XBOX_B);
  }

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

//   ###   #   #  #####   ###   #   #  #####
//    #    ##  #    #    #   #  #  #   #    
//    #    # # #    #    #####  ###    ###  
//    #    #  ##    #    #   #  #  #   #    
//   ###   #   #    #    #   #  #   #  #####

    // intake actuators
    private Talon m_intake;
    // intake sensors
    // intake globals
    // intake functions
    private void intake_init() {
        m_intake = new Talon(1);
    }
    private void intake_sense() {

    }
    // @@@ INTAKE SPEED CONTROL
    private void intake_run() {
        if (c_intake()) {
            m_intake.set(1.0);
        } else if (c_outtake()) {
            m_intake.set(-1.0);
        } else {
            m_intake.set(0);
        }
    }

//   ####  ####    ###   #   #  ####   #####  #   #
//  #      #   #    #    ##  #  #   #  #       # # 
//   ###   ####     #    # # #  #   #  ###      #  
//      #  #        #    #  ##  #   #  #       # # 
//  ####   #       ###   #   #  ####   #####  #   #

    // spindex actuators
    private Talon m_spindex;
    // spindex sensors
    private Encoder a_spindex_rot;
    private AnalogInput a_spindex_pos1;
    private AnalogInput a_spindex_pos5;
    private DigitalInput d_spindex_full;
    // spindex globals
    double spinorigin = 0;
    double spindex_target = 5;
    boolean spindex_slewing = false;
    // spindex functions
    boolean spindex_pos1() {
        return (a_spindex_pos1.getVoltage() < 2.5);
    }
    boolean spindex_pos5() {
        return (a_spindex_pos5.getVoltage() < 2.5);
    }
    boolean spindex_full() {
        return d_spindex_full.get();
    }
    double spindex_rot(boolean reset) {
        final double posPerCount = (
          6.0 /* position */ / 1.0 /* rotation */
        * 1.0 /* rotation */ / 7.7727 /* turn */
        * 1 /* turn */ / 2048 /* count */
        );
        double raw = a_spindex_rot.getDistance();
        if (reset) {
          spinorigin = raw;
          SmartDashboard.putNumber("spin origin", spinorigin);
        }
        return 5 - (raw - spinorigin) * posPerCount;
    }
    void spindex_rewind() {
        spindex_target = 0;
    }
    void spindex_home() {
        spindex_target = 1.0;
    }
    void spindex_advance() {
        if (!spindex_slewing) {
            spindex_slewing = true;
            spindex_target = spindex_target + 1;
        }
    }
    void spindex_retreat() {
        if (!spindex_slewing) {
            spindex_slewing = true;
            spindex_target = spindex_target - 1;
        }
    }
    double spindex_manual() {
        return deadband(control.getRawAxis(XBOX_RIGHT_X), 0.2);
    }
    private void spindex_init() {
        m_spindex = new Talon(2);
        a_spindex_pos1 = new AnalogInput(0);
        a_spindex_pos5 = new AnalogInput(1);
        a_spindex_rot  = new Encoder(6,7);
        d_spindex_full = new DigitalInput(0);
        SmartDashboard.putNumber("spindex speed", 0.2);
    }
    private void spindex_sense() {
        if (spindex_full()) {
            spindex_rot(true);
            spindex_target = 5;
        }
        SmartDashboard.putNumber("pos1 raw", a_spindex_pos1.getVoltage());
        SmartDashboard.putNumber("pos5 raw", a_spindex_pos5.getVoltage());
        SmartDashboard.putNumber("spin raw", a_spindex_rot.getDistance());
        SmartDashboard.putBoolean("spindex full", spindex_full());
        SmartDashboard.putBoolean("spindex pos1", spindex_pos1());
        SmartDashboard.putBoolean("spindex pos5", spindex_pos5());
        SmartDashboard.putNumber("spindex tgt", spindex_target);
        SmartDashboard.putNumber("spindex rot", spindex_rot(false));
    }
    private void spindex_run() {
        double spindex_speed;
        spindex_speed = SmartDashboard.getNumber("spindex speed", 0.2);
        if (!spindex_full() && spindex_pos1() && !spindex_pos5()) {
            spindex_advance();
        }
        if (c_forward()) {
            spindex_advance();
        }
        if (c_backward()) {
            spindex_retreat();
        }
        double delta = spindex_target - spindex_rot(false);
        if (!c_override()) {
            m_spindex.set(spindex_manual());
        } else {
            if (delta < -0.05) {
                m_spindex.set(-spindex_speed);
            } else if (delta > 0.05) {
                m_spindex.set(spindex_speed);
            } else {
                m_spindex.set(0);
                spindex_slewing = false;
            }
        }
    }


//   ####  #   #   ###    ###   #####  #####  #### 
//  #      #   #  #   #  #   #    #    #      #   #
//   ###   #####  #   #  #   #    #    ###    #### 
//      #  #   #  #   #  #   #    #    #      #  # 
//  ####   #   #   ###    ###     #    #####  #   #

    // shooter actuators
    private TalonSRX m_shoot1;
    private VictorSPX m_shoot2;
    private Talon m_kick;
    // shooter sensors
    /* none */
    // shooter globals
    double shooter_speed = 0;
    // shooter functions
    private void shoot(boolean v) {
        if (v) {
            shooter_speed = 3400;
        } else {
            shooter_speed = 0;
        }
    }
    private double shooter_feedback() {
        return m_shoot1.getSelectedSensorPosition();
    }
    private boolean shooter_at_speed() {
        return false;
    }
    private void shooter_init() {
        // initialize
        m_shoot1 = new TalonSRX(14);
        m_shoot2 = new VictorSPX(15);
        m_kick = new Talon(0);
        /* Factory default hardware to prevent unexpected behavior */
        m_shoot1.configFactoryDefault();
        m_shoot2.configFactoryDefault();
        m_shoot1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
        m_shoot1.setSensorPhase(true);
        m_shoot2.follow(m_shoot1);
		/* Config the peak and nominal outputs */
		m_shoot1.configNominalOutputForward(0, 30);
		m_shoot1.configNominalOutputReverse(0, 30);
		m_shoot1.configPeakOutputForward(1, 30);
		m_shoot1.configPeakOutputReverse(-1, 30);

		/* Config the Velocity closed loop gains in slot0 */
		m_shoot1.config_kF(0, 1023.0/6000.0, 30);
		m_shoot1.config_kP(0, 0.0, 30);
		m_shoot1.config_kI(0, 0.0, 30);
		m_shoot1.config_kD(0, 0.0, 30);
        
    }
    private void shooter_sense() {
        SmartDashboard.putNumber("shooter_raw", shooter_feedback());
        SmartDashboard.putBoolean("at speed", shooter_at_speed());
        SmartDashboard.putNumber("shooter RPM", m_shoot1.getSelectedSensorVelocity());
    }
    private void shooter_run() {
        if (c_singleshot() || c_volley()) {
            shoot(true);
        } else {
            shoot(false);
        }
		/* Velocity Closed Loop */
        m_shoot1.set(ControlMode.Velocity, shooter_speed);

        if (c_kick()) {
            m_kick.set(0.9);
        } else {
            m_kick.set(0);
        }
    }

    boolean targeted = false;

    // Creates a new Gamepiece 
    public Gamepiece(Joystick userControl) {
        // initialize
        control = userControl;
        intake_init();
        spindex_init();
        shooter_init();
        }

    public void run() {
        // control
        intake_run();
        spindex_run();
        shooter_run();
    }
    public void sense() {
        // telemetry
        intake_sense();
        spindex_sense();
        shooter_sense();
    }
    public void ontarget(boolean v) {
        targeted = v;
    }
}