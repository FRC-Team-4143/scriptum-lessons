package frc.robot;

import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.subsystems.drive.DrivetrainConstants.DriveStates;
import frc.robot.subsystems.drive.DrivetrainSubsystem;
import lesson.Checks;
import org.littletonrobotics.junction.LoggedRobot;

/**
 * The main robot class. Most of the work now happens in subsystems, so this class only connects
 * things together. You should not need to change it.
 */
public class Robot extends LoggedRobot {
  private final RobotContainer container = new RobotContainer();

  @Override
  public void robotPeriodic() {
    // Runs every subsystem: read sensors, update logic, write motor outputs.
    container.doControlLoop();
    CommandScheduler.getInstance().run();
    Checks.update(); // lesson helper, used by the Verify button
  }

  @Override
  public void disabledInit() {
    DrivetrainSubsystem.getInstance().setWantedState(DriveStates.IDLE);
  }

  @Override
  public void teleopInit() {
    DrivetrainSubsystem.getInstance().setWantedState(DriveStates.ARCADE);
  }
}
