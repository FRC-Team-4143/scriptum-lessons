package frc.robot.autos;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.shooter.ShooterCommands;

/**
 * The robot's autonomous routines. Each one is a sequence of commands that run one after another:
 * the next starts when the one before it finishes.
 */
public final class Autos {
  private Autos() {}

  /**
   * Left auto: drive forward 5 meters, turn to 45 degrees, drive forward 2 more meters, then shoot.
   *
   * <p>TODO: return Commands.sequence(...) with, in order, these commands:
   *   new DriveDistanceCommand(5.0)
   *   new TurnToAngleCommand(45.0)
   *   new DriveDistanceCommand(2.0)
   *   ShooterCommands.shoot().withTimeout(2.0)   // shoots for 2 seconds, then stops
   */
  public static Command leftAuto() {
    return Commands.sequence(
        new DriveDistanceCommand(5.0),
        new TurnToAngleCommand(45.0),
        new DriveDistanceCommand(2.0),
        ShooterCommands.shoot().withTimeout(2.0));
  }

  /**
   * Right auto: the same, but turn to -45 degrees (to the right) instead.
   *
   * <p>TODO: build it like leftAuto().
   */
  public static Command rightAuto() {
    return Commands.sequence(
        new DriveDistanceCommand(5.0),
        new TurnToAngleCommand(-45.0),
        new DriveDistanceCommand(2.0),
        ShooterCommands.shoot().withTimeout(2.0));
  }

  public static Command squareAuto() {
    SequentialCommandGroup square = new SequentialCommandGroup();
    for (int i = 1; i <= 4; i++) {
      square.addCommands(new DriveDistanceCommand(1.0), new TurnToAngleCommand(90.0 * i));
    }
    return square;
  }
}
