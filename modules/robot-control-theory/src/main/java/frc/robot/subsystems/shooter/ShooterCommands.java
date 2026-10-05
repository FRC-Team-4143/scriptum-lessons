package frc.robot.subsystems.shooter;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.subsystems.shooter.ShooterConstants.ShooterStates;

/**
 * Commands for the shooter. A command is a small, reusable action. Buttons start commands, and
 * commands tell subsystems what to do. They are built from the helpers in {@link Commands}.
 */
public final class ShooterCommands {
  private ShooterCommands() {}

  /** Shoots for as long as the command runs, then goes back to idle. */
  public static Command shoot() {
    ShooterSubsystem shooter = ShooterSubsystem.getInstance();
    return Commands.startEnd(
        () -> shooter.setWantedState(ShooterStates.SHOOT),
        () -> shooter.setWantedState(ShooterStates.IDLE));
  }

  /** An instant command that pretends a game piece was just launched. */
  public static Command simulateBallLaunch() {
    return Commands.runOnce(() -> ShooterSubsystem.getInstance().simulateBallLaunch());
  }
}
