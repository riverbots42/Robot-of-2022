// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends TimedRobot { 
  TalonSRX lefty = new TalonSRX(0);
  TalonSRX lefty2 = new TalonSRX(1);
  TalonSRX righty = new TalonSRX(2);
  TalonSRX righty2 = new TalonSRX(3);
  int throttle = 55;
  int previousPov = 0;
  int lcal = 0;
  int rcal = 0;
  //private final Talon m_leftDrive = new Talon(0);
  //private final Talon m_rightDrive = new Talon(2);
  // private final DifferentialDrive m_robotDrive = new DifferentialDrive(lefty, righty);
  //private final Joystick l_stick = new Joystick(0);
  //private final Joystick r_stick = new Joystick(1);
  private final Joystick stick = new Joystick(0);
  private final Timer m_timer = new Timer();

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    righty.setInverted(true);
    System.out.print("here");
    //l_stick.getXChannel(
  }

  /** This function is run once each time the robot enters autonomous mode. */
  @Override
  public void autonomousInit() {
    m_timer.reset();
    m_timer.start();
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    // Drive for 2 seconds
    if (m_timer.get() < 2.0) {
      //m_robotDrive.arcadeDrive(0.5, 0.0); // drive forwards half speed
    } else {
      //m_robotDrive.stopMotor(); // stop robot
    }
  }
  /** This function is called once each time the robot enters teleoperated mode. */
  @Override
  public void teleopInit() {
    //m_robotDrive.arcadeDrive(0.5, 0.0);
    //Channel 1 is Left stick
    stick.setXChannel(1);
    //Channel 3 is RT
    stick.setZChannel(3);
    //Channel 5 is Rightstick
    stick.setYChannel(5);
   
    lcal = pct(stick.getX(), 0);
    rcal= pct(stick.getY(), 0);
    

  }
  public  int pct (double raw,int cal) {
    return (int)(raw*throttle*1.0) - cal;
  }
  public int povToDirection()
  {
    int pov = stick.getPOV();
    //this gives upish case from d-pad throttle input
    if(pov > 270 || (pov > -1 && pov < 90) )
    {
      return +1;
    }

    if (pov > 90 && pov < 270)
    {
      return -1;
    }
    return 0;
  }
  /** This function is called periodically during teleoperated mode. */
  @Override
  public void teleopPeriodic() {
    //m_robotDrive.arcadeDrive(m_stick.getY(), m_stick.getX());
    //System.out.printf("%d:%f\n",stick.getPOV(),stick.getZ());
    int currentPov = povToDirection();
    if (currentPov != previousPov )
    {
    if (currentPov == 0 ){
      previousPov = currentPov;
    }
    else if (currentPov == +1){
      throttle = throttle+10;
      if (throttle >105){
        throttle = 105;
      }
      previousPov = currentPov;
    }
    else if ( currentPov==-1){
      throttle = throttle-10;
      if (throttle <5){
        throttle = 5;
      }
      previousPov = currentPov;
    }
    System.out.printf("throttle = %d\n",throttle);
  }
    int lpct = pct(stick.getX(), 0);
    /*if(lpct <1 &&lpct > -1) {
      lpct = 0;
    }
    */
    lefty.set(ControlMode.PercentOutput, lpct);
    lefty2.set(ControlMode.PercentOutput, lpct);

    

    int rpct = pct(stick.getY(), 0);
    /*if(rpct <1 &&rpct > -1) {
      rpct = 0;
    }*/
    righty.set(ControlMode.PercentOutput, rpct);
    righty2.set(ControlMode.PercentOutput, rpct);

   // System.out.printf("hello %d %d\n", rpct, lpct);
  }

  /** This function is called once each time the robot enters test mode. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
