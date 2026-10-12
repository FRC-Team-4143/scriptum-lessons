package frc.robot;

import com.marswars.subsystem.MwConstants;

/**
 * The robot's own constants. RobotContainer reads them as {@code CONSTANTS}, the same way a
 * subsystem reads its {@code *Constants} class. Values that belong to the whole robot rather than
 * one subsystem go here, starting with the robot's states (like {@code DriveStates} in
 * DrivetrainConstants).
 */
public class RobotConstants extends MwConstants {
  /** The robot's states. RobotContainer is in exactly one of them at a time. */
  public enum RobotStates {
    /** Disabled: nothing moves. */
    IDLE,
    /** Teleop driving from the sticks, shooter stopped. */
    DRIVE,
    /** Driving and shooting. */
    SHOOT,
    /** Turning to face the goal, shooter stopped. */
    AIM
  }
}
