/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

//  #####  #####   ###   #   #  #   #   ###   #   #   ###   #####   ####
//    #    #      #   #  #   #  ##  #  #   #  #  #   #   #    #    #    
//    #    ###    #      #####  # # #  #   #  ###    #####    #     ### 
//    #    #      #   #  #   #  #  ##  #   #  #  #   #   #    #        #
//    #    #####   ###   #   #  #   #   ###   #   #  #   #    #    #### 
//
//  ####    ###   ####    ###   #####   ###    ###    ####
//  #   #  #   #  #   #  #   #    #      #    #   #  #    
//  ####   #   #  ####   #   #    #      #    #       ### 
//  #  #   #   #  #   #  #   #    #      #    #   #      #
//  #   #   ###   ####    ###     #     ###    ###   #### 
//
//  #####  #####   ###   #   #           # #   # #    ####
//    #    #      #   #  ## ##          #####  # #    #   
//    #    ###    #####  # # #           # #   ####   ### 
//    #    #      #   #  #   #          #####    #       #
//    #    #####  #   #  #   #           # #     #    ### 
//
/*----------------------------------------------------------------------------*/

package frc.robot;

//
//   ###   #   #  ####    ###   ####   #####
//    #    ## ##  #   #  #   #  #   #    #  
//    #    # # #  ####   #   #  ####     #  
//    #    #   #  #      #   #  #  #     #  
//   ###   #   #  #       ###   #   #    #  
//

import frc.robot.drivebase.*;
import frc.robot.gamepiece.*;
import frc.robot.climber.*;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;


//
//  ####    ###   ####    ###   #####
//  #   #  #   #  #   #  #   #    #  
//  ####   #   #  ####   #   #    #  
//  #  #   #   #  #   #  #   #    #  
//  #   #   ###   ####    ###     #  
//
public class Robot extends TimedRobot {

//  ####   ####    ###   #   #  #####  ####            ###   #####  #    
//  #   #  #   #    #    #   #  #      #   #          #   #    #    #    
//  #   #  ####     #    #   #  ###    ####           #        #    #    
//  #   #  #  #     #     # #   #      #  #           #   #    #    #    
//  ####   #   #   ###     #    #####  #   #           ###     #    #####

private Joystick Xbox0 = new Joystick(0);
private Joystick Xbox1 = new Joystick(1);

//   ###   ####   #####  ####    ###   #####   ###   ####            ###   #####  #    
//  #   #  #   #  #      #   #  #   #    #    #   #  #   #          #   #    #    #    
//  #   #  ####   ###    ####   #####    #    #   #  ####           #        #    #    
//  #   #  #      #      #  #   #   #    #    #   #  #  #           #   #    #    #    
//   ###   #      #####  #   #  #   #    #     ###   #   #           ###     #    #####

private Joystick Bbox2 = new Joystick(2);

Joystick m_drivebase_leftGamepad;
Joystick m_drivebase_rightGamepad;
Joystick m_buttonBox;

Drivebase drivebase = new Drivebase(Xbox0);
Gamepiece gamepiece = new Gamepiece(Xbox1);
Climber climber = new Climber(Xbox0);






    



//  #   #   ###    ###   #   #
//  ## ##  #   #    #    ##  #
//  # # #  #####    #    # # #
//  #   #  #   #    #    #  ##
//  #   #  #   #   ###   #   #

  @Override
  public void robotInit() {
    CameraServer.getInstance().startAutomaticCapture();
  }

  @Override
  public void teleopPeriodic() {
    drivebase.run();
    drivebase.sense();
    gamepiece.run();
    gamepiece.sense();
    climber.run();
    climber.sense();
  }

  @Override
  public void disabledPeriodic() {
    drivebase.sense();
    gamepiece.sense();
    climber.sense();
  }
}
