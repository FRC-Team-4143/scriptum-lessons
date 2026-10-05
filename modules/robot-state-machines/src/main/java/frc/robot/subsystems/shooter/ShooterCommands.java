package frc.robot.subsystems.shooter;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

/**
 * Commands for the shooter. A command is a small, reusable action. Buttons start commands, and
 * commands tell subsystems what to do. They are built from the helpers in {@link Commands}.
 */
public final class ShooterCommands {
  private ShooterCommands() {}

  /**
   * Shoots for as long as the command runs, then goes back to idle.
   *
   * <p>TODO: return Commands.startEnd(start, end) where start sets the shooter's wanted state to
   * SHOOT and end sets it back to IDLE. A start/end command does the first thing when it starts and
   * the second when it ends (for example when you let go of the button). Use
   * ShooterSubsystem.getInstance().setWantedState(...).
   */
  public static Command shoot() {
    return Commands.none();
  }

  /**
   * An instant command that pretends a game piece was just launched.
   *
   * <p>TODO: return Commands.runOnce(...) that calls the subsystem's simulateBallLaunch().
   */
  public static Command simulateBallLaunch() {
    return Commands.none();
  }
}
