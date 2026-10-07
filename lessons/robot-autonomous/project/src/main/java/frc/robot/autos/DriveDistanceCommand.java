package frc.robot.autos;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.drive.DrivetrainConstants;
import frc.robot.subsystems.drive.DrivetrainSubsystem;

/**
 * Drives straight ahead (in whatever direction the robot is facing) for a distance, then stops.
 * Compare it with TurnToAngleCommand, which is already finished.
 */
public class DriveDistanceCommand extends Command {
  private final DrivetrainSubsystem drive = DrivetrainSubsystem.getInstance();
  private final DrivetrainConstants constants = drive.getConstants();
  private final double goalMeters;
  private Pose2d startPose = new Pose2d();

  /**
   * @param meters how far to drive. Negative drives backward.
   */
  public DriveDistanceCommand(double meters) {
    goalMeters = meters;
  }

  /** How far the robot has driven since the command started, in meters. */
  private double distanceDriven() {
    return drive.getPose().getTranslation().getDistance(startPose.getTranslation());
  }

  @Override
  public void initialize() {
    // TODO: remember where the robot is right now: startPose = drive.getPose();
  }

  @Override
  public void execute() {
    // TODO: work out how far we still have to go: error = Math.abs(goalMeters) - distanceDriven()
    // Then drive faster the farther we are from the goal:
    //   double forward = constants.DRIVE_KP * error;
    //   forward = MathUtil.clamp(forward, ...);   // no faster than constants.MAX_AUTO_SPEED
    // Make it negative if goalMeters is negative, then call drive.setCommandedSpeeds(forward, 0.0).
    drive.setCommandedSpeeds(0.0, 0.0);
  }

  @Override
  public boolean isFinished() {
    // TODO: finished when we are within constants.DRIVE_TOLERANCE_METERS of the goal.
    return true;
  }

  @Override
  public void end(boolean interrupted) {
    drive.setCommandedSpeeds(0.0, 0.0);
  }
}
