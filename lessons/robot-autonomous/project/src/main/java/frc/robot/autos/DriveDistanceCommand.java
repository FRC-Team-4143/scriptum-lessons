package frc.robot.autos;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.drive.DrivetrainConstants;
import frc.robot.subsystems.drive.DrivetrainSubsystem;

/**
 * Drives straight ahead (in whatever direction the robot is facing) for a distance, then stops.
 * This one is finished for you: read it along with TurnToAngleCommand to see how a command is built.
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
    startPose = drive.getPose();
  }

  @Override
  public void execute() {
    double error = Math.abs(goalMeters) - distanceDriven();
    double forward = constants.DRIVE_KP * error;
    forward = MathUtil.clamp(forward, -constants.MAX_AUTO_SPEED, constants.MAX_AUTO_SPEED);
    if (Math.abs(forward) < constants.MIN_AUTO_SPEED) {
      forward = Math.copySign(constants.MIN_AUTO_SPEED, forward);
    }
    if (goalMeters < 0) {
      forward = -forward;
    }
    drive.setCommandedSpeeds(forward, 0.0);
  }

  @Override
  public boolean isFinished() {
    return Math.abs(Math.abs(goalMeters) - distanceDriven()) < constants.DRIVE_TOLERANCE_METERS;
  }

  @Override
  public void end(boolean interrupted) {
    drive.setCommandedSpeeds(0.0, 0.0);
  }
}
