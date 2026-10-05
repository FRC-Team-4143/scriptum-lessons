package frc.robot.autos;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

/**
 * The robot's autonomous routines. Each one is a sequence of commands that run one after another:
 * the next starts when the one before it finishes.
 */
public final class Autos {
  private Autos() {}

  /**
   * Left auto: drive forward 5 meters, turn to 45 degrees, drive forward 2 more meters, then shoot.
   *
   * <p>TODO: return Commands.sequence(...) with, in order, these commands: new
   * DriveDistanceCommand(5.0) new TurnToAngleCommand(45.0) new DriveDistanceCommand(2.0)
   * ShooterCommands.shoot().withTimeout(2.0) // shoots for 2 seconds, then stops
   */
  public static Command leftAuto() {
    return Commands.none();
  }

  /**
   * Right auto: the same, but turn to -45 degrees (to the right) instead.
   *
   * <p>TODO: build it like leftAuto().
   */
  public static Command rightAuto() {
    return Commands.none();
  }

  /**
   * Square auto: drive a 1 meter square. That is the same two steps four times: drive forward 1
   * meter, then turn 90 degrees more than before (to 90, 180, 270 and finally 360 degrees).
   *
   * <p>TODO: do NOT write the four pairs out by hand. Create a SequentialCommandGroup, then use a
   * for loop to add a DriveDistanceCommand(1.0) and a TurnToAngleCommand(90.0 * i) each time, with
   * i going from 1 to 4. A command group has addCommands(...) for this. Return the group.
   */
  public static Command squareAuto() {
    return Commands.none();
  }
}
