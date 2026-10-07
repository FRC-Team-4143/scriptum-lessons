package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.subsystems.drive.DrivetrainConstants.DriveStates;
import frc.robot.subsystems.drive.DrivetrainSubsystem;
import frc.robot.subsystems.shooter.ShooterSubsystem;
import lesson.Checks;
import org.littletonrobotics.junction.LoggedRobot;

/**
 * The main robot class. It connects the subsystems, commands and the autonomous chooser. You should
 * not need to change it.
 */
public class Robot extends LoggedRobot {
  private final RobotContainer container = new RobotContainer();
  private Command autoCommand = null;

  public Robot() {
    OI.configureBindings();
  }

  @Override
  public void robotPeriodic() {
    container.doControlLoop();
    CommandScheduler.getInstance().run();
    Checks.update(); // lesson helper, used by the Verify button
  }

  @Override
  public void disabledInit() {
    DrivetrainSubsystem.getInstance().setWantedState(DriveStates.IDLE);
  }

  @Override
  public void autonomousInit() {
    // Start from the origin, let commands drive, then run the routine picked in the chooser.
    DrivetrainSubsystem.getInstance().resetPose();
    DrivetrainSubsystem.getInstance().setWantedState(DriveStates.COMMANDED);
    ShooterSubsystem.getInstance().resetShotCount();
    autoCommand = container.getSelectedAuto();
    CommandScheduler.getInstance().schedule(autoCommand);
  }

  @Override
  public void teleopInit() {
    // Stop the autonomous routine if it is still running, then give the driver control.
    if (autoCommand != null) {
      autoCommand.cancel();
    }
    DrivetrainSubsystem.getInstance().setWantedState(DriveStates.ARCADE);
  }
}
