package frc.robot.autos;

import com.marswars.auto.Auto;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.RobotContainer;
import frc.robot.RobotStates;
import frc.robot.subsystems.drive.DrivetrainSubsystem;

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
      RobotContainer robot = RobotContainer.getInstance();

      // The paths this routine drives, in order. The names are the paths' names in Choreo.
      loadTrajectory("ToPickup");
      loadTrajectory("ToScore");

      addCommands(
          // Drive the first path.
          robot.followPath(getTrajectory("ToPickup")),
          // Pretend to pick the game piece up.
          Commands.waitSeconds(0.5),
          // Drive the second path.
          robot.followPath(getTrajectory("ToScore")),
          // Turn to face the goal. Stop as soon as the robot is aimed (and give up after 3 seconds
          // so a robot that cannot aim does not hold up the routine forever).
          Commands.startEnd(
                  () -> robot.setWantedState(RobotStates.AIM),
                  () -> robot.setWantedState(RobotStates.AUTO))
              .until(DrivetrainSubsystem.getInstance()::isAimed)
              .withTimeout(3.0),
          // Shoot for 2 seconds, then stop.
          Commands.startEnd(
                  () -> robot.setWantedState(RobotStates.SHOOT),
                  () -> robot.setWantedState(RobotStates.AUTO))
              .withTimeout(2.0));
    }
  }
}
