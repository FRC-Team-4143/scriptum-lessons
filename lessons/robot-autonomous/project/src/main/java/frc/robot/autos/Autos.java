package frc.robot.autos;

import com.marswars.auto.Auto;

/**
 * The robot's autonomous routines. An {@link Auto} is a list of commands that run one after
 * another, and it can load the paths you drew in Choreo by name.
 */
public final class Autos {
  private Autos() {}

  /** Drive to the game piece, pick it up, drive to the scoring spot, aim at the goal, and shoot. */
  public static Auto pickupAndScore() {
    return new PickupAndScore();
  }

  /** The routine itself. */
  public static class PickupAndScore extends Auto {
    public PickupAndScore() {
      // TODO 1: tell the routine which paths it uses, in the order it drives them. The names are
      // the names of the paths you drew in Choreo, spelled exactly the same:
      //   loadTrajectory("ToPickup");
      //   loadTrajectory("ToScore");

      // TODO 2: add the commands, in order, with addCommands(...):
      //   DrivetrainCommands.followPath(getTrajectory("ToPickup"))   // drive the first path
      //   Commands.waitSeconds(0.5)                                  // pretend to pick the piece
      // up
      //   DrivetrainCommands.followPath(getTrajectory("ToScore"))    // drive the second path
      //   DrivetrainCommands.aim()                                   // turn to face the goal, but
      //       .until(DrivetrainSubsystem.getInstance()::isAimed)     // stop once the robot is
      // aimed
      //       .withTimeout(3.0)                                      // (and give up after 3
      // seconds)
      //   ShooterCommands.shoot().withTimeout(2.0)                   // shoot for 2 seconds
    }
  }
}
