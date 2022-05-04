// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;



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
  VictorSPX basket = new VictorSPX(4);
  int rout = 0;
  int lout = 0;
  int throttleStep = 1;
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

  boolean reverse_triggered = false;
  boolean is_reverse = false;

  UsbCamera fronty;
  UsbCamera reary;
  NetworkTableEntry cam;

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
    righty2.setInverted(true);
    System.out.print("here");
    //l_stick.getXChannel(
    
    cam = NetworkTableInstance.getDefault().getTable("").getEntry("CameraSelection");
    fronty = CameraServer.startAutomaticCapture(0);
    reary = CameraServer.startAutomaticCapture(1);
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
    //this gives downish case
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

    basket.set(ControlMode.PercentOutput, (stick.getRawAxis(2)/2)-(stick.getRawAxis(3)/2));
  

    if (stick.getRawButtonPressed(1)){
      is_reverse = !is_reverse;
      System.out.println("here");
    }

    if(is_reverse == true) {
      cam.setString(reary.getName());
    } else {
      cam.setString(fronty.getName());
    } 
    System.out.printf("Left: %f, Right: %f\n", stick.getRawAxis(2), stick.getRawAxis(3));
    
    int currentPov = povToDirection();
    //dpad up increases throttle, dpad down decreases
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
    int ltarget = pct(stick.getX(), 0);
    /*if(lpct <1 &&lpct > -1) {
      lpct = 0;
    }
    */
    int rtarget = pct(stick.getY(), 0);
    /*if(rpct <1 &&rpct > -1) {
      rpct = 0;
    }*/
//steps motor power to input
if (lout < ltarget) {
  lout+=throttleStep;
  if(lout > ltarget) {
    lout = ltarget;
  }
} else if (lout > ltarget) {
  lout-=throttleStep;
  if (lout < ltarget){
    lout = ltarget;
  }
}
if (rout < rtarget) {
  rout+=throttleStep;
  if(rout > rtarget) {
    rout = rtarget;
  }
} else if (rout > rtarget) {
  rout-=throttleStep;
  if (rout < rtarget){
    rout = rtarget;
  }
}
    //converts output to a decimal percent to prevent the motors from having only o and 100 power
    lefty.set(ControlMode.PercentOutput, 1.0*lout/100.0);
    lefty2.set(ControlMode.PercentOutput, 1.0*lout/100.0);
    righty.set(ControlMode.PercentOutput, 1.0*rout/100.0);
    righty2.set(ControlMode.PercentOutput, 1.0*rout/100.0);

   // System.out.printf("hello %d %d\n", rpct, lpct);
  }

  /** This function is called once each time the robot enters test mode. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
