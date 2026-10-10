package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.shooter.ShooterSubsystem;

/**
 * OI stands for "Operator Interface": everything the human drivers do. It now also connects
 * controller buttons to robot states.
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

  /** Connects buttons to robot states. Robot calls this once at startup. */
  public static void configureBindings() {
    DriverStation.silenceJoystickConnectionWarning(true);
    RobotContainer robot = RobotContainer.getInstance();

    // whileTrue(command): the command runs while the button is held and stops when it is released.
    // Commands.startEnd(start, end) runs the start lambda when it starts and the end lambda when it
    // ends.
    // TODO: bind the right bumper so the robot wants SHOOT while it is held, and DRIVE when it is
    // let go.
    // Hint: driver_controller_.rightBumper().whileTrue(Commands.startEnd(
    //     () -> robot.setWantedState(RobotStates.SHOOT), () -> robot.setWantedState(...)));

    // onTrue(command): the command runs once each time the button is pressed.
    // TODO: bind the A button to Commands.runOnce(...) that calls
    // ShooterSubsystem.getInstance().simulateBallLaunch() (a pretend launch, for the simulator)
  }
}
