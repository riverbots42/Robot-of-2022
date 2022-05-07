// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// Sources for Team 6845 (Grommit) below.  Here be dragons.

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
 * Concrete implementation of the TimedRobot class for team 6845.
 */
public class Robot extends TimedRobot {
  private static final int LEFT_TRIGGER = 2;
  private static final int RIGHT_TRIGGER = 3;
  TalonSRX leftyA = new TalonSRX(0);
  TalonSRX leftyB = new TalonSRX(1);
  TalonSRX rightyA = new TalonSRX(2);
  TalonSRX rightyB = new TalonSRX(3);
  VictorSPX basket = new VictorSPX(4);

  int rout = 0;
  int lout = 0;
  int throttleStep = 1;
  int throttle = 55;
  int previousPov = 0;
  int lcal = 0;
  int rcal = 0;

  private final Joystick stick = new Joystick(0);
  private final Timer m_timer = new Timer();

  boolean reverse_triggered = false;
  boolean is_reverse = false;

  UsbCamera fronty;
  UsbCamera reary;
  NetworkTableEntry cam;

  /**
   * This function is run when the robot is first started up and should be used
   * for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    rightyA.setInverted(true);
    rightyB.setInverted(true);

    // Initializes the cameras (uses a special UI that we do not have)
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
    return;
  }

  /**
   * This function is called once each time the robot enters teleoperated mode.
   */
  @Override
  public void teleopInit() {
    // m_robotDrive.arcadeDrive(0.5, 0.0);
    // Channel 1 is Left stick
    stick.setXChannel(1);
    // Channel 3 is RT
    stick.setZChannel(3);
    // Channel 5 is Rightstick
    stick.setYChannel(5);

    // deprecated?
    lcal = pct(stick.getX(), 0);
    rcal = pct(stick.getY(), 0);
  }

  public int pct(double raw, int cal) {
    return (int) (raw * throttle * 1.0) - cal;
  }

  /*
   * Convert the dpad POV input to either +1/0/-1.
   */
  public int povToDirection() {
    int pov = stick.getPOV();
    // this gives upish case from d-pad throttle input
    if (pov > 270 || (pov > -1 && pov < 90)) {
      return +1;
    }
    // this gives downish case
    if (pov > 90 && pov < 270) {
      return -1;
    }
    return 0;
  }

  /**
   * This function is called periodically during teleoperated mode.
   */
  @Override
  public void teleopPeriodic() {
    basket.set(ControlMode.PercentOutput, (stick.getRawAxis(LEFT_TRIGGER) / 2) - (stick.getRawAxis(RIGHT_TRIGGER) / 2));

    if (stick.getRawButtonPressed(1)) {
      is_reverse = !is_reverse;
      System.out.println("here");
    }

    if (is_reverse == true) {
      cam.setString(reary.getName());
    } else {
      cam.setString(fronty.getName());
    }
    System.out.printf("Left: %f, Right: %f\n", stick.getRawAxis(2), stick.getRawAxis(3));

    int currentPov = povToDirection();
    // dpad up increases throttle, dpad down decreases
    if (currentPov != previousPov) {
      if (currentPov == 0) {
        previousPov = currentPov;
      } else if (currentPov == +1) {
        throttle = throttle + 10;
        if (throttle > 105) {
          throttle = 105;
        }
        previousPov = currentPov;
      } else if (currentPov == -1) {
        throttle = throttle - 10;
        if (throttle < 5) {
          throttle = 5;
        }
        previousPov = currentPov;
      }
      System.out.printf("throttle = %d\n", throttle);
    }
    int ltarget = pct(stick.getX(), 0);

    int rtarget = pct(stick.getY(), 0);

    // steps motor power to input
    if (lout < ltarget) {
      lout += throttleStep;
      if (lout > ltarget) {
        lout = ltarget;
      }
    } else if (lout > ltarget) {
      lout -= throttleStep;
      if (lout < ltarget) {
        lout = ltarget;
      }
    }
    if (rout < rtarget) {
      rout += throttleStep;
      if (rout > rtarget) {
        rout = rtarget;
      }
    } else if (rout > rtarget) {
      rout -= throttleStep;
      if (rout < rtarget) {
        rout = rtarget;
      }
    }
    // converts output to a decimal percent to prevent the motors from having only o
    // and 100 power
    leftyA.set(ControlMode.PercentOutput, 1.0 * lout / 100.0);
    leftyB.set(ControlMode.PercentOutput, 1.0 * lout / 100.0);
    rightyA.set(ControlMode.PercentOutput, 1.0 * rout / 100.0);
    rightyB.set(ControlMode.PercentOutput, 1.0 * rout / 100.0);
  }

  /** This function is called once each time the robot enters test mode. */
  @Override
  public void testInit() {
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
  }
}
