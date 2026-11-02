package frc.robot.mechanisms;

import org.littletonrobotics.junction.AutoLog;

/** Everything the drive's sensors report each loop. */
@AutoLog
public class DriveInputs {
  public double leftPositionRad = 0.0;
  public double rightPositionRad = 0.0;
  public double leftVelocityRadPerSec = 0.0;
  public double rightVelocityRadPerSec = 0.0;
  // From the NavX gyro: which way the robot faces (counterclockwise is positive), and whether the
  // gyro is plugged in and answering.
  public double gyroYawRad = 0.0;
  public boolean gyroConnected = false;
}
