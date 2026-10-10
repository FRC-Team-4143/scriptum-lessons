package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

/**
 * The places on the field the autonomous routine cares about. Provided for you, no need to edit.
 *
 * <p>These are the same four named poses that are saved in the Choreo project
 * (deploy/choreo/robot.chor), in meters and with 0 degrees pointing down the field (+x). The paths
 * you draw in Choreo start and end on them. The robot code only needs them to measure how close the
 * robot got, for the lesson's checks, and the aim state needs the goal.
 */
public final class FieldTargets {
  private FieldTargets() {}

  /** Where the robot starts the match. */
  public static final Pose2d START = new Pose2d(1.5, 1.5, Rotation2d.fromDegrees(0.0));

  /** Where the game piece is. */
  public static final Pose2d PICKUP = new Pose2d(5.5, 2.5, Rotation2d.fromDegrees(0.0));

  /** Where the robot shoots from. */
  public static final Pose2d SCORE_SPOT = new Pose2d(6.5, 5.0, Rotation2d.fromDegrees(90.0));

  /**
   * The goal the game piece has to go into. Only its position matters: the aim state turns the
   * robot to face it. From the scoring spot the robot is pointing along +y, so it has to turn about
   * 110 degrees (to the left, over the "back" of the field) to face this.
   */
  public static final Pose2d GOAL = new Pose2d(4.0, 4.0, Rotation2d.fromDegrees(0.0));
}
