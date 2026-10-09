package frc.robot.subsystems.drive;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

/** Commands for the drivetrain. Buttons, and later the autonomous routines, start these. */
public final class DrivetrainCommands {
  private DrivetrainCommands() {}

  /**
   * Aims at the goal for as long as the command runs, then goes back to driving.
   *
   * <p>TODO: return Commands.startEnd(start, end) where start sets the drivetrain's wanted state to
   * AIM and end sets it back to ARCADE. It is the same shape as ShooterCommands.shoot(). Use
   * DrivetrainSubsystem.getInstance().setWantedState(...).
   */
  public static Command aim() {
    return Commands.none();
  }
}
