package frc.robot.subsystems.drive;

import com.marswars.subsystem.MwConstants;

/** Everything that describes the drivetrain subsystem, in one place. */
public class DrivetrainConstants extends MwConstants {

  /** The things the drivetrain can be doing. */
  public enum DriveStates {
    /** Not moving. */
    IDLE,
    /** Driving from the controller sticks. */
    ARCADE,
    /** Driving whatever speeds the autonomous commands ask for. */
    COMMANDED
  }

  // =============================================================================
  // AUTONOMOUS DRIVING
  // =============================================================================

  /** Fastest the robot drives on its own, as a duty cycle from 0.0 to 1.0. */
  public final double MAX_AUTO_SPEED = 0.5;
  /** Slowest it creeps along, so the last few centimeters do not take forever. */
  public final double MIN_AUTO_SPEED = 0.08;
  /** Driving: duty cycle per meter of distance left. */
  public final double DRIVE_KP = 0.6;
  /** Driving counts as finished when this close to the goal, in meters. */
  public final double DRIVE_TOLERANCE_METERS = 0.05;

  /** Fastest the robot turns on its own, as a duty cycle. Turning is much more sensitive. */
  public final double MAX_TURN_SPEED = 0.2;
  /** Slowest it creeps while turning. */
  public final double MIN_TURN_SPEED = 0.04;
  /** Turning: duty cycle per radian of heading error. */
  public final double TURN_KP = 0.3;
  /** Turning counts as finished when this close to the goal, in degrees. */
  public final double TURN_TOLERANCE_DEGREES = 2.0;
}
