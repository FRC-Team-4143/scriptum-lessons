package frc.robot;

import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.RobotConstants.RobotStates;
import frc.robot.subsystems.drive.DrivetrainSubsystem;
import lesson.Checks;
import org.littletonrobotics.junction.LoggedRobot;

/**
 * The main robot class. Most of the work happens in RobotContainer and the subsystems, so this
 * class only connects things together. You should not need to change it.
 */
public class Robot extends LoggedRobot {
  private final RobotContainer container_ = RobotContainer.getInstance();

  public Robot() {
    // Put the (simulated) robot at the start spot, and the pose estimate with it.
    DrivetrainSubsystem.getInstance().resetPose(FieldTargets.START);
    // Connect the controller buttons to robot states.
    OI.configureBindings();
  }

  @Override
  public void robotPeriodic() {
    // Runs the robot's state machine, then every subsystem: read sensors, update logic, write
    // motor outputs.
    container_.doControlLoop();
    // Runs the button bindings.
    CommandScheduler.getInstance().run();
    Checks.update(); // lesson helper, used by the Verify button
  }

  @Override
  public void disabledInit() {
    container_.setWantedState(RobotStates.IDLE);
  }

  @Override
  public void teleopInit() {
    container_.setWantedState(RobotStates.DRIVE);
  }
}
