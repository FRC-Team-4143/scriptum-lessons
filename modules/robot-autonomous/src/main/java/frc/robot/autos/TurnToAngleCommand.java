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
  private final DrivetrainSubsystem drive = DrivetrainSubsystem.getInstance();
  private final DrivetrainConstants constants = drive.getConstants();
  private final Rotation2d goal;

  /**
   * @param degrees the heading to face. 0 is the starting direction, positive is left
   *     (counterclockwise).
   */
  public TurnToAngleCommand(double degrees) {
    goal = Rotation2d.fromDegrees(degrees);
  }

  /** How far we still have to turn, in radians, between -pi and pi. */
  private double errorRadians() {
    return MathUtil.angleModulus(goal.minus(drive.getPose().getRotation()).getRadians());
  }

  @Override
  public void execute() {
    if (Math.abs(Math.toDegrees(errorRadians())) < constants.TURN_TOLERANCE_DEGREES) {
      // Close enough: stop pushing and let the robot settle.
      drive.setCommandedSpeeds(0.0, 0.0);
      return;
    }
    // Turn faster the farther we are from the goal (this is "proportional" control). A positive
    // error means we need to turn left, but a positive turn command turns RIGHT, so flip the sign.
    double turn = -constants.TURN_KP * errorRadians();
    turn = MathUtil.clamp(turn, -constants.MAX_TURN_SPEED, constants.MAX_TURN_SPEED);
    // Never go slower than the minimum, or the robot would stall just short of the goal.
    if (Math.abs(turn) < constants.MIN_TURN_SPEED) {
      turn = Math.copySign(constants.MIN_TURN_SPEED, turn);
    }
    drive.setCommandedSpeeds(0.0, turn);
  }

  @Override
  public boolean isFinished() {
    // On target AND no longer spinning. Without the second check the robot would coast past it.
    boolean onTarget = Math.abs(Math.toDegrees(errorRadians())) < constants.TURN_TOLERANCE_DEGREES;
    return onTarget && Math.abs(drive.getAngularSpeed()) < 0.15;
  }

  @Override
  public void end(boolean interrupted) {
    drive.setCommandedSpeeds(0.0, 0.0);
  }
}
