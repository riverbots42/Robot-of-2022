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
  int lcal = 0;
  int rcal = 0;
  //private final Talon m_leftDrive = new Talon(0);
  //private final Talon m_rightDrive = new Talon(2);
  // private final DifferentialDrive m_robotDrive = new DifferentialDrive(lefty, righty);
  private final Joystick l_stick = new Joystick(0);
  private final Joystick r_stick = new Joystick(1);
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
    lcal = pct(l_stick.getY(), 0);
    rcal= pct(r_stick.getY(), 0);

  }
  public static int pct (double raw,int cal) {
    return (int)(raw*100.0) - cal;
  }
  /** This function is called periodically during teleoperated mode. */
  @Override
  public void teleopPeriodic() {
    //m_robotDrive.arcadeDrive(m_stick.getY(), m_stick.getX());
    
    int lpct = pct(l_stick.getY(), lcal);
    if(lpct <10 &&lpct > -10) {
      lpct = 0;
    }
    lefty.set(ControlMode.PercentOutput, lpct);
    lefty2.set(ControlMode.PercentOutput, lpct);

    int rpct = pct(r_stick.getY(), rcal);
    if(rpct <10 &&rpct > -10) {
      rpct = 0;
    }
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
