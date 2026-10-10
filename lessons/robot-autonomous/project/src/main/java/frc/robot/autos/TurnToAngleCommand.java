package frc.robot.autos;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.drive.DrivetrainConstants;
import frc.robot.subsystems.drive.DrivetrainSubsystem;

/**
 * Turns the robot until it faces a heading, then stops. This one is finished for you: read it, then
 * use it as an example for DriveDistanceCommand.
 *
 * <p>A command is a class with four parts: initialize() runs once at the start, execute() runs
 * every 20 ms, isFinished() says when it is done, and end() runs once when it stops.
 */
public class TurnToAngleCommand extends Command {
  private final DrivetrainSubsystem drive_ = DrivetrainSubsystem.getInstance();
  private final DrivetrainConstants CONSTANTS = drive_.getConstants();
  private final Rotation2d goal_;

  /**
   * @param degrees the heading to face. 0 is the starting direction, positive is left
   *     (counterclockwise).
   */
  public TurnToAngleCommand(double degrees) {
    goal_ = Rotation2d.fromDegrees(degrees);
  }

  /** How far we still have to turn, in radians, between -pi and pi. */
  private double errorRadians() {
    return MathUtil.angleModulus(goal_.minus(drive_.getPose().getRotation()).getRadians());
  }

  @Override
  public void execute() {
    if (Math.abs(Math.toDegrees(errorRadians())) < CONSTANTS.TURN_TOLERANCE_DEGREES) {
      // Close enough: stop pushing and let the robot settle.
      drive_.setCommandedSpeeds(0.0, 0.0);
      return;
    }
    // Turn faster the farther we are from the goal (this is "proportional" control). A positive
    // error means we need to turn left, but a positive turn command turns RIGHT, so flip the sign.
    double turn = -CONSTANTS.TURN_KP * errorRadians();
    turn = MathUtil.clamp(turn, -CONSTANTS.MAX_TURN_SPEED, CONSTANTS.MAX_TURN_SPEED);
    // Never go slower than the minimum, or the robot would stall just short of the goal.
    if (Math.abs(turn) < CONSTANTS.MIN_TURN_SPEED) {
      turn = Math.copySign(CONSTANTS.MIN_TURN_SPEED, turn);
    }
    drive_.setCommandedSpeeds(0.0, turn);
  }

  @Override
  public boolean isFinished() {
    // On target AND no longer spinning. Without the second check the robot would coast past it.
    boolean on_target = Math.abs(Math.toDegrees(errorRadians())) < CONSTANTS.TURN_TOLERANCE_DEGREES;
    return on_target && Math.abs(drive_.getAngularSpeed()) < 0.15;
  }

  @Override
  public void end(boolean interrupted) {
    drive_.setCommandedSpeeds(0.0, 0.0);
  }
}
