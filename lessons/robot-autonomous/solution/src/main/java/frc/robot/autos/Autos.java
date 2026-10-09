package frc.robot.autos;

import com.marswars.auto.Auto;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.subsystems.drive.DrivetrainCommands;
import frc.robot.subsystems.drive.DrivetrainSubsystem;
import frc.robot.subsystems.shooter.ShooterCommands;
import frc.robot.subsystems.shooter.ShooterConstants.ShooterStates;
import frc.robot.subsystems.shooter.ShooterSubsystem;

/**
 * The robot's autonomous routines. An {@link Auto} is a list of commands that run one after
 * another, and it can load the paths you drew in Choreo by name.
 */
public final class Autos {
  private Autos() {}

  /** Drive to the game piece, pick it up, drive to the scoring spot, and shoot. */
  public static Auto pickupAndScore() {
    return new PickupAndScore();
  }

  /** The routine itself. */
  public static class PickupAndScore extends Auto {
    public PickupAndScore() {
      // The paths this routine drives, in order. The names are the paths' names in Choreo.
      loadTrajectory("ToPickup");
      loadTrajectory("ToScore");

      // The "Shoot" marker on the ToScore path: when the robot gets there, start spinning up.
      DrivetrainSubsystem.getInstance()
          .getChoreoEventTimeTrigger("Shoot")
          .onTrue(
              Commands.runOnce(
                  () -> ShooterSubsystem.getInstance().setWantedState(ShooterStates.SHOOT)));

      addCommands(
          // Drive the first path.
          DrivetrainCommands.followPath(getTrajectory("ToPickup")),
          // Pretend to pick the game piece up.
          Commands.waitSeconds(0.5),
          // Drive the second path.
          DrivetrainCommands.followPath(getTrajectory("ToScore")),
          // Shoot for 2 seconds, then stop.
          ShooterCommands.shoot().withTimeout(2.0));
    }
  }
}
