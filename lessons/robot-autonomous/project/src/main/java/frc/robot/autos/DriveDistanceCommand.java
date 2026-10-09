package frc.robot.autos;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.drive.DrivetrainConstants;
import frc.robot.subsystems.drive.DrivetrainSubsystem;

/**
 * Drives straight ahead (in whatever direction the robot is facing) for a distance, then stops.
 * This one is finished for you: read it along with TurnToAngleCommand to see how a command is
 * built.
 */
public class DriveDistanceCommand extends Command {
  private final DrivetrainSubsystem drive_ = DrivetrainSubsystem.getInstance();
  private final DrivetrainConstants CONSTANTS = drive_.getConstants();
  private final double goal_meters_;
  private Pose2d start_pose_ = new Pose2d();

  /**
   * @param meters how far to drive. Negative drives backward.
   */
  public DriveDistanceCommand(double meters) {
    goal_meters_ = meters;
  }

  /** How far the robot has driven since the command started, in meters. */
  private double distanceDriven() {
    return drive_.getPose().getTranslation().getDistance(start_pose_.getTranslation());
  }

  @Override
  public void initialize() {
    start_pose_ = drive_.getPose();
  }

  @Override
  public void execute() {
    double error = Math.abs(goal_meters_) - distanceDriven();
    double forward = CONSTANTS.DRIVE_KP * error;
    forward = MathUtil.clamp(forward, -CONSTANTS.MAX_AUTO_SPEED, CONSTANTS.MAX_AUTO_SPEED);
    if (Math.abs(forward) < CONSTANTS.MIN_AUTO_SPEED) {
      forward = Math.copySign(CONSTANTS.MIN_AUTO_SPEED, forward);
    }
    if (goal_meters_ < 0) {
      forward = -forward;
    }
    drive_.setCommandedSpeeds(forward, 0.0);
  }

  @Override
  public boolean isFinished() {
    return Math.abs(Math.abs(goal_meters_) - distanceDriven()) < CONSTANTS.DRIVE_TOLERANCE_METERS;
  }

  @Override
  public void end(boolean interrupted) {
    drive_.setCommandedSpeeds(0.0, 0.0);
  }
}
