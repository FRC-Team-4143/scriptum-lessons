package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

/**
 * OI stands for "Operator Interface": everything the human drivers do. It now also connects
 * controller buttons to commands.
 */
public abstract class OI {
  // A CommandXboxController is an XboxController whose buttons can start commands.
  private static final CommandXboxController driverController = new CommandXboxController(0);

  /** How far forward the driver is pushing, -1.0 to 1.0. */
  public static double getForward() {
    return MathUtil.applyDeadband(-driverController.getLeftY(), 0.1);
  }

  /** How much the driver wants to turn, -1.0 to 1.0. */
  public static double getTurn() {
    return MathUtil.applyDeadband(driverController.getRightX(), 0.1);
  }

  /** Connects buttons to commands. Robot calls this once at startup. */
  public static void configureBindings() {
    DriverStation.silenceJoystickConnectionWarning(true);

    // whileTrue(command): the command runs while the button is held and stops when it is released.
    // TODO: bind the right bumper to ShooterCommands.shoot()
    // Hint: driverController.rightBumper().whileTrue(...)

    // onTrue(command): the command runs once each time the button is pressed.
    // TODO: bind the A button to ShooterCommands.simulateBallLaunch()
  }
}
