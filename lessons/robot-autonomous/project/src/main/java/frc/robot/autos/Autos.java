package frc.robot.autos;

import com.marswars.auto.Auto;

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
      // TODO 1: tell the routine which paths it uses, in the order it drives them. The names are
      // the names of the paths you drew in Choreo, spelled exactly the same:
      //   loadTrajectory("ToPickup");
      //   loadTrajectory("ToScore");

      // TODO 2 (optional): do something when the robot passes an event marker you put on a path in
      // Choreo. For a marker named "Shoot" on the ToScore path, start the shooter early:
      //   DrivetrainSubsystem.getInstance()
      //       .getChoreoEventTimeTrigger("Shoot")
      //       .onTrue(Commands.runOnce(() ->
      // ShooterSubsystem.getInstance().setWantedState(ShooterStates.SHOOT)));

      // TODO 3: add the commands, in order, with addCommands(...):
      //   DrivetrainCommands.followPath(getTrajectory("ToPickup"))   // drive the first path
      //   Commands.waitSeconds(0.5)                                  // pretend to pick the piece
      // up
      //   DrivetrainCommands.followPath(getTrajectory("ToScore"))    // drive the second path
      //   ShooterCommands.shoot().withTimeout(2.0)                   // shoot for 2 seconds
    }
  }
}
