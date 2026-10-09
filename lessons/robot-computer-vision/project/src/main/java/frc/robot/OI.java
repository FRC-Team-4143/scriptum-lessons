package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.drive.DrivetrainCommands;
import frc.robot.subsystems.shooter.ShooterCommands;

/**
 * OI stands for "Operator Interface": everything the human drivers do. It now also connects
 * controller buttons to commands.
 */
public abstract class OI {
  // A CommandXboxController is an XboxController whose buttons can start commands.
  private static final CommandXboxController driver_controller_ = new CommandXboxController(0);

  /** How far forward the driver is pushing, -1.0 to 1.0. */
  public static double getForward() {
    return MathUtil.applyDeadband(-driver_controller_.getLeftY(), 0.1);
  }

  /** How much the driver wants to turn, -1.0 to 1.0. */
  public static double getTurn() {
    return MathUtil.applyDeadband(driver_controller_.getLeftX(), 0.1);
  }

  /** The aim button (left bumper). The drivetrain turns to face the goal while it is held. */
  public static Trigger getAimButton() {
    return driver_controller_.leftBumper();
  }

  /** Connects buttons to commands. Robot calls this once at startup. */
  public static void configureBindings() {
    DriverStation.silenceJoystickConnectionWarning(true);

    // whileTrue(command): the command runs while the button is held and stops when it is released.
    driver_controller_.rightBumper().whileTrue(ShooterCommands.shoot());

    // onTrue(command): the command runs once each time the button is pressed.
    driver_controller_.a().onTrue(ShooterCommands.simulateBallLaunch());

    // Aim at the goal while the left bumper is held. (Finish DrivetrainCommands.aim() first.)
    getAimButton().whileTrue(DrivetrainCommands.aim());
  }
}
