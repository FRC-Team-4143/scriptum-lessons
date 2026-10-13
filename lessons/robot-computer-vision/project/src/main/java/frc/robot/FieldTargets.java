package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

/**
 * The places on the field the lesson cares about. Provided for you, no need to edit.
 *
 * <p>Meters, in field coordinates (the blue alliance corner is the origin), with a heading of 0
 * radians pointing down the field (+x).
 */
public final class FieldTargets {
  private FieldTargets() {}

  /** Where the robot starts. Robot.java puts the simulated robot here. */
  public static final Pose2d START = new Pose2d(1.5, 1.5, new Rotation2d(0.0));

  /**
   * The goal the robot will aim at. Only its position matters. It is in the middle of the blue hub,
   * so its AprilTags are on the field layout the cameras use.
   */
  public static final Pose2d GOAL = new Pose2d(4.0, 4.0, new Rotation2d(0.0));
}
