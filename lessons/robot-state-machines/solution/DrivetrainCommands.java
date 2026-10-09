package frc.robot.subsystems.drive;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.subsystems.drive.DrivetrainConstants.DriveStates;

/** Commands for the drivetrain. Buttons, and later the autonomous routines, start these. */
public final class DrivetrainCommands {
  private DrivetrainCommands() {}

  /** Aims at the goal for as long as the command runs, then goes back to driving. */
  public static Command aim() {
    DrivetrainSubsystem drive = DrivetrainSubsystem.getInstance();
    return Commands.startEnd(
        () -> drive.setWantedState(DriveStates.AIM),
        () -> drive.setWantedState(DriveStates.ARCADE));
  }
}
