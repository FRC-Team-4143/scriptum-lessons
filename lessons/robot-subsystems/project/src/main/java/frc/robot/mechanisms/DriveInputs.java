package frc.robot.mechanisms;

import org.littletonrobotics.junction.AutoLog;

/** Everything the drive's sensors report each loop. */
@AutoLog
public class DriveInputs {
  public double leftPositionRotations = 0.0;
  public double rightPositionRotations = 0.0;
  public double leftVelocityRps = 0.0;
  public double rightVelocityRps = 0.0;
}
