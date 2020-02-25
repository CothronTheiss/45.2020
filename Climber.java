//   ###   #       ###   #   #  ####   #####  #### 
//  #   #  #        #    ## ##  #   #  #      #   #
//  #      #        #    # # #  ####   ###    #### 
//  #   #  #        #    #   #  #   #  #      #  # 
//   ###   #####   ###   #   #  ####   #####  #   #

package frc.robot.climber;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber {
    // climber actuators
    // climber sensors
    private DigitalInput d_cabledirection;
    // climber globals
    Joystick control;

    // Creates a new Climber 
    public Climber(Joystick userControl) {
        // initialize
        control = userControl;
        d_cabledirection = new DigitalInput(1);
    }

    private void vibrate(boolean v) {
        if (v) {
            control.setRumble(RumbleType.kLeftRumble,1);
        } else {
            control.setRumble(RumbleType.kLeftRumble,0);
        }
    }
    private boolean unspooling() {
        return d_cabledirection.get();
    }
    private boolean respooling() {
        return !d_cabledirection.get();
    }
    private boolean q_ExtendClimber() {
        int pov = control.getPOV();
        boolean b = false;
        switch (pov) {
          case 315:
          case 0:
          case 45:
            if (respooling()) {
                vibrate(true);
            } else {
                b = true;
            }
            break;
          default:
            vibrate(false);
            break;
        }
        return b;
    }
    
    private boolean q_RetractClimber() {
        int pov = control.getPOV();
        boolean b = false;
        switch (pov) {
            case 135:
            case 180:
            case 225:
              if (unspooling()) {
                  vibrate(true);
              } else {
                  b = true;
              }
              break;
            default:
              vibrate(false);
              break;
        }
        return b;
    }

    public void run() {
        // control
    }
    public void sense() {
        // telemetry
        SmartDashboard.putBoolean("extend", q_ExtendClimber());
        SmartDashboard.putBoolean("retract", q_RetractClimber());
        SmartDashboard.putBoolean("cabledir", d_cabledirection.get());
    }
}