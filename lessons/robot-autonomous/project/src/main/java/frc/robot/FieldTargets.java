package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

/**
 * The places on the field the autonomous routine cares about. Provided for you, no need to edit.
 *
 * <p>These are the same three named poses that are saved in the Choreo project
 * (deploy/choreo/robot.chor), in meters and with 0 degrees pointing down the field (+x). The paths
 * you draw in Choreo start and end on them. The robot code only needs them to measure how close the
 * robot got, for the lesson's checks.
 */
public final class FieldTargets {
  private FieldTargets() {}

  /** Where the robot starts the match. */
  public static final Pose2d START = new Pose2d(1.5, 1.5, Rotation2d.fromDegrees(0.0));

  /** Where the game piece is. */
  public static final Pose2d PICKUP = new Pose2d(5.5, 2.5, Rotation2d.fromDegrees(0.0));

  /** Where the robot shoots from. */
  public static final Pose2d SCORE_SPOT = new Pose2d(6.5, 5.0, Rotation2d.fromDegrees(90.0));
}
