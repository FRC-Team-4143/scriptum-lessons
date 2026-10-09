package frc.robot.subsystems.drive;

import com.marswars.auto.ChoreoTrajectory;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.subsystems.drive.DrivetrainConstants.DriveStates;
import java.util.function.Supplier;

/** Commands for the drivetrain. Provided for you, like {@code ShooterCommands}. */
public final class DrivetrainCommands {
  private DrivetrainCommands() {}

  /**
   * Drives a Choreo path from start to finish, then stops. It finishes when the robot has arrived
   * at the end of the path, or after 10 seconds (so a robot that gets stuck cannot hold up the rest
   * of the autonomous routine forever).
   *
   * @param trajectory the path, as given by {@code getTrajectory("Name")} inside an {@code Auto}
   */
  public static Command followPath(Supplier<ChoreoTrajectory> trajectory) {
    DrivetrainSubsystem drive = DrivetrainSubsystem.getInstance();
    return Commands.sequence(
        // 1. Pick the path.
        drive.setDesiredChoreoTrajectoryCommand(trajectory),
        // 2. Switch to following it, and wait there until the robot has arrived.
        Commands.startEnd(
                () -> drive.setWantedState(DriveStates.CHOREO_PATH),
                () -> {
                  drive.stopChoreoEvents();
                  drive.setCommandedSpeeds(0.0, 0.0);
                  drive.setWantedState(DriveStates.COMMANDED);
                })
            .until(drive::isAtChoreoSetpoint)
            .withTimeout(10.0));
  }
}
