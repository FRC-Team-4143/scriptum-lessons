package frc.robot.subsystems.drive;

import com.marswars.subsystem.MwConstants;

/** Everything that describes the drivetrain subsystem, in one place. */
public class DrivetrainConstants extends MwConstants {

  /** The things the drivetrain can be doing. */
  public enum DriveStates {
    /** Not moving. */
    IDLE,
    /** Driving from the controller sticks. */
    ARCADE
  }
}
