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

      // TODO 2: add the commands, in order, with addCommands(...). The robot is
      // RobotContainer.getInstance(); ask it for states the same way the buttons do:
      //   robot.followPath(getTrajectory("ToPickup"))               // drive the first path
      //   Commands.waitSeconds(0.5)                                 // pretend to pick the piece up
      //   robot.followPath(getTrajectory("ToScore"))                // drive the second path
      //   Commands.startEnd(                                        // turn to face the goal:
      //           () -> robot.setWantedState(RobotStates.AIM),      //   ask for AIM,
      //           () -> robot.setWantedState(RobotStates.AUTO))     //   then back to AUTO,
      //       .until(DrivetrainSubsystem.getInstance()::isAimed)    // once the robot is aimed
      //       .withTimeout(3.0)                                     // (or after 3 seconds)
      //   Commands.startEnd(                                        // shoot:
      //           () -> robot.setWantedState(RobotStates.SHOOT),
      //           () -> robot.setWantedState(RobotStates.AUTO))
      //       .withTimeout(2.0)                                     // for 2 seconds
    }
  }
}
